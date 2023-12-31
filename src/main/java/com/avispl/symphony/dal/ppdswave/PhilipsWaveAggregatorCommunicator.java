/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.ppdswave;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.api.dal.monitor.aggregator.Aggregator;
import com.avispl.symphony.dal.aggregator.parser.AggregatedDeviceProcessor;
import com.avispl.symphony.dal.aggregator.parser.PropertiesMapping;
import com.avispl.symphony.dal.aggregator.parser.PropertiesMappingParser;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.ppdswave.dto.CustomerByHandle;
import com.avispl.symphony.dal.ppdswave.dto.Data;
import com.avispl.symphony.dal.ppdswave.dto.ReportedDataWrapper;
import com.avispl.symphony.dal.ppdswave.dto.ResponseWrapper;
import com.avispl.symphony.dal.ppdswave.dto.display.Alert;
import com.avispl.symphony.dal.ppdswave.dto.display.Bookmarks;
import com.avispl.symphony.dal.ppdswave.dto.display.Display;
import com.avispl.symphony.dal.ppdswave.dto.display.Group;
import com.avispl.symphony.dal.ppdswave.dto.display.power.LatestJob;
import com.avispl.symphony.dal.ppdswave.dto.display.power.PowerSchedule;
import com.avispl.symphony.dal.ppdswave.dto.display.power.Schedule;
import com.avispl.symphony.dal.ppdswave.dto.display.power.TimeBlock;
import com.avispl.symphony.dal.util.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Philips Wave Communicator to retrieve information about Philips Wave Devices.
 * Currently monitored property categories are:
 * <li>Device Metadata</li>
 * <li>Alerts</li>
 * <li>Audio</li>
 * <li>Bookmarks</li>
 * <li>Customer</li>
 * <li>Firmware</li>
 * <li>NetworkInformation</li>
 * <li>NetworkInterfaces</li>
 * <li>Platform</li>
 * <li>Power</li>
 * <li>PowerSchedule</li>
 * <li>Screenshot</li>
 * <li>Site</li>
 * <li>System</li>
 * <li>Video</li>
 *
 * Currently supported control operations are:
 * <li>Reboot</li>
 * <li>Audio Mute</li>
 * <li>Volume</li>
 * <li>Brightness</li>
 * <li>Power Mode</li>
 * <li>Orientation</li>
 * <li>Input Source</li>
 * <li>Screenshot</li>
 * <li>IR Control Mode</li>
 * <li>Keyboard Control Mode</li>
 * <li>Ports Control Mode</li>
 * <li>Display Alias</li>
 * <li>Led Strip Color</li>
 *
 * @author Maksym.Rossiytsev
 * @since 1.0.0
 */
public class PhilipsWaveAggregatorCommunicator extends RestCommunicator implements Aggregator, Monitorable, Controller {

    /**
     * Process that is running constantly and triggers collecting data from Philips Wave API endpoints,
     * based on the given timeouts and thresholds.
     *
     * @author Maksym.Rossiytsev
     * @since 1.0.0
     */
    class PPDSDeviceDataLoader implements Runnable {
        private volatile boolean inProgress;

        public PPDSDeviceDataLoader() {
            inProgress = true;
        }

        @Override
        public void run() {
            mainloop:
            while (inProgress) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Process was interrupted!", e);
                    }
                }

                if (!inProgress) {
                    break mainloop;
                }

                // next line will determine whether Philips Wave monitoring was paused
                updateAggregatorStatus();
                if (devicePaused) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Philips Wave Aggregator is paused");
                    }
                    continue mainloop;
                }

                while (nextDevicesCollectionIterationTimestamp > System.currentTimeMillis()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    } catch (InterruptedException e) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("Process was interrupted!", e);
                        }
                    }
                }

                boolean retrievedWithErrors = false;
                try {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Fetching SDVoE devices list");
                    }
                    fetchDevicesList();
                    if (logger.isDebugEnabled()) {
                        logger.debug("Fetched devices list: " + aggregatedDevices);
                    }
                } catch (Exception e) {
                    retrievedWithErrors = true;
                    latestErrors.put(e.toString(), e.getMessage());
                    logger.error("Error occurred during device list retrieval", e);
                }
                if (!retrievedWithErrors) {
                    latestErrors.clear();
                }

                try {
                    processDeviceDetails();
                } catch (Exception e) {
                    logger.error("Unable to process devices details", e);
                }
                if (!inProgress) {
                    break mainloop;
                }
                // We don't want to fetch devices statuses too often, so by default it's currentTime + 30s
                // otherwise - the variable is reset by the retrieveMultipleStatistics() call, which
                // launches devices detailed statistics collection
                nextDevicesCollectionIterationTimestamp = System.currentTimeMillis() + 30000;

                if (logger.isDebugEnabled()) {
                    logger.debug("Finished collecting devices statistics cycle at " + new Date());
                }
            }
            // Finished collecting
        }
        /**
         * Triggers main loop to stop
         */
        public void stop() {
            inProgress = false;
        }
    }

    /**
     * Adapter metadata, collected from the version.properties
     */
    private Properties adapterProperties;

    /**
     * Runner service responsible for collecting data
     */
    private PPDSDeviceDataLoader deviceDataLoader;

    /**
     * Executor that runs all the async operations
     */
    private static ExecutorService executorService;

    /**
     * Latest aggregator errors
     */
    Map<String, String> latestErrors = new HashMap<>();

    /**
     * Saved customer handles to use for devices retrieval
     * */
    Set<String> customerHandles = new HashSet<>();

    /**
     * If the {@link PhilipsWaveAggregatorCommunicator#deviceMetaDataRetrievalTimeout} is set to a value that is too small -
     * devices list will be fetched too frequently. In order to avoid this - the minimal value is based on this value.
     */
    private static final long defaultMetaDataTimeout = 60 * 1000 / 2;

    /**
     * Device metadata retrieval timeout. The general devices list is retrieved once during this time period.
     */
    private long deviceMetaDataRetrievalTimeout = 60 * 1000 * 10;

    /**
     * Device details retrieval timeout. The device details are updated once during this time period.
     * */
    private long deviceDetailsRetrievalTimeout = 60 * 1000 * 1;

    /**
     * Customer handle filter. The devices are only retrieved for the handle specified.
     * */
    private List<String> customerHandleFilter;

    /**
     * Device type filter. The devices are only retrieved/updated if type is matched.
     * */
    private List<String> deviceTypeFilter;

    /**
     * Aggregator inactivity timeout. If the {@link PhilipsWaveAggregatorCommunicator#retrieveMultipleStatistics()}  method is not
     * called during this period of time - device is considered to be paused, thus the Cloud API
     * is not supposed to be called
     */
    private static final long retrieveStatisticsTimeOut = 3 * 60 * 1000;

    /**
     * Time period within which the device metadata (basic devices information) cannot be refreshed.
     * Ignored if device list is not yet retrieved or the cached device list is empty {@link PhilipsWaveAggregatorCommunicator#aggregatedDevices}
     */
    private volatile long validDeviceMetaDataRetrievalPeriodTimestamp;

    /**
     * Time period within which the device details cannot be refreshed.
     * Ignored if device list is not yet retrieved or the cached device list is empty {@link PhilipsWaveAggregatorCommunicator#aggregatedDevices}
     */
    private volatile long validDeviceDetailsRetrievalPeriodTimestamp;

    /**
     * Devices this aggregator is responsible for
     * Data is cached and retrieved every {@link #defaultMetaDataTimeout}
     */
    private final ConcurrentHashMap<String, AggregatedDevice> aggregatedDevices = new ConcurrentHashMap<>();

    /**
     * We don't want the statistics to be collected constantly, because if there's not a big list of devices -
     * new devices statistics loop will be launched before the next monitoring iteration. To avoid that -
     * this variable stores a timestamp which validates it, so when the devices statistics is done collecting, variable
     * is set to currentTime + 30s, at the same time, calling {@link #retrieveMultipleStatistics()} and updating the
     * {@link #aggregatedDevices} resets it to the currentTime timestamp, which will re-activate data collection.
     */
    private static long nextDevicesCollectionIterationTimestamp;

    /**
     * This parameter holds timestamp of when we need to stop performing API calls
     * It used when device stop retrieving statistic. Updated each time of called #retrieveMultipleStatistics
     */
    private volatile long validRetrieveStatisticsTimestamp;

    /**
     * Indicates whether a device is considered as paused.
     * True by default so if the system is rebooted and the actual value is lost -> the device won't start stats
     * collection unless the {@link PhilipsWaveAggregatorCommunicator#retrieveMultipleStatistics()} method is called which will change it
     * to a correct value
     */
    private volatile boolean devicePaused = true;

    /**
     * List of all models, present in yml mapping
     */
    Map<String, PropertiesMapping> models;

    /**
     * Device adapter instantiation timestamp.
     */
    private long adapterInitializationTimestamp;

    /**
     * Aggregated device processor, for automatic response json processing, based on the model-mapping.yml configuration
     * */
    AggregatedDeviceProcessor aggregatedDeviceProcessor;

    public PhilipsWaveAggregatorCommunicator() {
    }

    @Override
    protected void internalInit() throws Exception {
        models = new PropertiesMappingParser()
                .loadYML("mapping/model-mapping.yml", getClass());
        aggregatedDeviceProcessor = new AggregatedDeviceProcessor(models);

        adapterProperties = new Properties();
        adapterProperties.load(getClass().getResourceAsStream("/version.properties"));

        executorService = Executors.newCachedThreadPool();
        adapterInitializationTimestamp = System.currentTimeMillis();
        executorService.submit(deviceDataLoader = new PPDSDeviceDataLoader());
        validDeviceMetaDataRetrievalPeriodTimestamp = System.currentTimeMillis();
        validDeviceDetailsRetrievalPeriodTimestamp = System.currentTimeMillis();
        //serviceRunning = true;
        super.internalInit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void internalDestroy() {
        if (logger.isDebugEnabled()) {
            logger.debug("Internal destroy is called.");
        }

        if (deviceDataLoader != null) {
            deviceDataLoader.stop();
            deviceDataLoader = null;
        }
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
        aggregatedDevices.clear();
        latestErrors.clear();
        super.internalDestroy();
    }

    @Override
    public void controlProperty(ControllableProperty controllableProperty) throws Exception {
        String deviceId = controllableProperty.getDeviceId();
        String command = controllableProperty.getProperty();
        String value = String.valueOf(controllableProperty.getValue());

        boolean controlPropagated = true;
        switch (command) {
            case Constants.ControlProperties.CONTROL_AUDIO_MUTE:
                commandChangeMuteStatus(deviceId, String.valueOf("1".equals(value)));
                break;
            case Constants.ControlProperties.CONTROL_AUDIO_VOLUME:
                commandChangeVolume(deviceId, value);
                break;
            case Constants.ControlProperties.CONTROL_VIDEO_BRIGHTNESS:
                commandChangeBrightness(deviceId, value);
                break;
            case Constants.ControlProperties.CONTROL_VIDEO_ORIENTATION:
                commandChangeOrientation(deviceId, value);
                break;
            case Constants.ControlProperties.CONTROL_VIDEO_INPUT_SOURCE:
                commandChangeInput(deviceId, value);
                break;
            case Constants.ControlProperties.CONTROL_POWER_MODE:
                commandChangePowerState(deviceId, value);
                break;
            case Constants.ControlProperties.CONTROL_SCREENSHOT_CREATE:
                commandTakeScreenshot(deviceId);
                break;
            case Constants.ControlProperties.CONTROL_POWER_REBOOT:
                commandReboot(deviceId);
                break;
            case Constants.ControlProperties.CONTROL_IR_CONTROL:
                commandChangeIRMode(deviceId, value);
                break;
            case Constants.ControlProperties.CONTROL_KEYBOARD_CONTROL:
                commandChangeKeyboardMode(deviceId, value);
                break;
            case Constants.ControlProperties.CONTROL_LED_COLOR:
                commandChangeLedColor(deviceId, value);
                break;
            case Constants.ControlProperties.CONTROL_ALIAS:
                commandChangeAlias(deviceId, value);
                break;
            case Constants.ControlProperties.CONTROL_PORTS_CONTROL:
                commandChangePortsControlState(deviceId, value);
                break;
            default:
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("Unable to execute %s command on device %s: Not Supported", command, deviceId));
                }
                controlPropagated = false;
                break;
        }
        if (controlPropagated) {
            updateLocalControlValue(deviceId, command, value);
        }
    }

    @Override
    public void controlProperties(List<ControllableProperty> controlProperties) throws Exception {
        if (CollectionUtils.isEmpty(controlProperties)) {
            throw new IllegalArgumentException("Controllable properties cannot be null or empty");
        }
        for (ControllableProperty controllableProperty : controlProperties) {
            controlProperty(controllableProperty);
        }
    }

    @Override
    public List<Statistics> getMultipleStatistics() throws Exception {
        Map<String, String> apiProperties = new HashMap<>();
        ExtendedStatistics extendedStatistics = new ExtendedStatistics();

        apiProperties.put("AdapterVersion", adapterProperties.getProperty("aggregator.version"));
        apiProperties.put("AdapterBuildDate", adapterProperties.getProperty("aggregator.build.date"));
        apiProperties.put("AdapterUptime", normalizeUptime((System.currentTimeMillis() - adapterInitializationTimestamp) / 1000));

        extendedStatistics.setStatistics(apiProperties);
        return Collections.singletonList(extendedStatistics);
    }

    @Override
    public List<AggregatedDevice> retrieveMultipleStatistics() throws Exception {
        long currentTimestamp = System.currentTimeMillis();
        nextDevicesCollectionIterationTimestamp = currentTimestamp;
        updateValidRetrieveStatisticsTimestamp();

        aggregatedDevices.values().forEach(aggregatedDevice -> aggregatedDevice.setTimestamp(currentTimestamp));
        return new ArrayList<>(aggregatedDevices.values());
    }

    @Override
    protected HttpHeaders putExtraRequestHeaders(HttpMethod httpMethod, String uri, HttpHeaders headers) throws Exception {
        headers.add("Authorization", "Basic " + getPassword());
        return super.putExtraRequestHeaders(httpMethod, uri, headers);
    }

    @Override
    public List<AggregatedDevice> retrieveMultipleStatistics(List<String> deviceIds) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Philips Wave retrieveMultipleStatistics deviceIds=" + String.join(" ", deviceIds));
        }
        return retrieveMultipleStatistics()
                .stream()
                .filter(aggregatedDevice -> deviceIds.contains(aggregatedDevice.getDeviceId()))
                .collect(toList());
    }

    @Override
    protected void authenticate() throws Exception {
    }

    /**
     * @return pingTimeout value if host is not reachable within
     * the pingTimeout, a ping time in milliseconds otherwise
     * if ping is 0ms it's rounded up to 1ms to avoid IU issues on Symphony portal
     * @throws IOException
     */
    @Override
    public int ping() throws IOException {
        long pingResultTotal = 0L;

        for (int i = 0; i < this.getPingAttempts(); i++) {
            long startTime = System.currentTimeMillis();

            try (Socket puSocketConnection = new Socket(this.getHost(), this.getPort())) {
                puSocketConnection.setSoTimeout(this.getPingTimeout());

                if (puSocketConnection.isConnected()) {
                    long endTime = System.currentTimeMillis();
                    long pingResult = endTime - startTime;
                    pingResultTotal += pingResult;
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace(String.format("PING OK: Attempt #%s to connect to %s on port %s succeeded in %s ms", i + 1, this.getHost(), this.getPort(), pingResult));
                    }
                } else {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug(String.format("PING DISCONNECTED: Connection to %s did not succeed within the timeout period of %sms", this.getHost(), this.getPingTimeout()));
                    }
                    return this.getPingTimeout();
                }
            } catch (SocketTimeoutException tex) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug(String.format("PING TIMEOUT: Connection to %s did not succeed within the timeout period of %sms", this.getHost(), this.getPingTimeout()));
                }
                return this.getPingTimeout();
            }
        }
        return Math.max(1, Math.toIntExact(pingResultTotal / this.getPingAttempts()));
    }

    /**
     * Retrieves {@link #deviceMetaDataRetrievalTimeout}
     *
     * @return value of {@link #deviceMetaDataRetrievalTimeout}
     */
    public long getDeviceMetaDataRetrievalTimeout() {
        return deviceMetaDataRetrievalTimeout;
    }

    /**
     * Sets {@link #deviceMetaDataRetrievalTimeout} value
     *
     * @param deviceMetaDataRetrievalTimeout new value of {@link #deviceMetaDataRetrievalTimeout}
     */
    public void setDeviceMetaDataRetrievalTimeout(long deviceMetaDataRetrievalTimeout) {
        this.deviceMetaDataRetrievalTimeout = Math.max(defaultMetaDataTimeout, deviceMetaDataRetrievalTimeout);
    }

    /**
     * Retrieves {@link #deviceDetailsRetrievalTimeout}
     *
     * @return value of {@link #deviceDetailsRetrievalTimeout}
     */
    public long getDeviceDetailsRetrievalTimeout() {
        return deviceDetailsRetrievalTimeout;
    }

    /**
     * Sets {@link #deviceDetailsRetrievalTimeout} value
     *
     * @param deviceDetailsRetrievalTimeout new value of {@link #deviceDetailsRetrievalTimeout}
     */
    public void setDeviceDetailsRetrievalTimeout(long deviceDetailsRetrievalTimeout) {
        this.deviceDetailsRetrievalTimeout = Math.max(defaultMetaDataTimeout, deviceDetailsRetrievalTimeout);
    }

    /**
     * Retrieves {@link #customerHandleFilter}
     *
     * @return value of {@link #customerHandleFilter}
     */
    public String getCustomerHandleFilter() {
        return String.join(",", customerHandleFilter);
    }

    /**
     * Sets {@link #customerHandleFilter} value
     *
     * @param customerHandleFilter new value of {@link #customerHandleFilter}
     */
    public void setCustomerHandleFilter(String customerHandleFilter) {
        if (customerHandleFilter == null) {
            this.customerHandleFilter = null;
            return;
        }
        this.customerHandleFilter = Arrays.stream(customerHandleFilter.split(",")).map(String::trim).collect(toList());
    }

    /**
     * Retrieves {@link #deviceTypeFilter}
     *
     * @return value of {@link #deviceTypeFilter}
     */
    public String getDeviceTypeFilter() {
        return String.join(",", deviceTypeFilter);
    }

    /**
     * Sets {@link #deviceTypeFilter} value
     *
     * @param deviceTypeFilter new value of {@link #deviceTypeFilter}
     */
    public void setDeviceTypeFilter(String deviceTypeFilter) {
        if (deviceTypeFilter == null) {
            this.deviceTypeFilter = null;
            return;
        }
        this.deviceTypeFilter = Arrays.stream(deviceTypeFilter.split(",")).map(String::trim).collect(toList());
    }

    /**
     * Fetch devices metadata (deviceId, alias and serial, to back up the initial provisioning process)
     * The process is executed once per {@link #validDeviceMetaDataRetrievalPeriodTimestamp} or whenever
     * {@link #aggregatedDevices} is empty (once every 30 seconds)
     *
     * @throws Exception if any error occurs
     */
    private void fetchDevicesList() throws Exception {
        long currentTimestamp = System.currentTimeMillis();
        if (aggregatedDevices.size() > 0 && validDeviceMetaDataRetrievalPeriodTimestamp > currentTimestamp) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("General devices metadata retrieval is in cooldown. %s seconds left",
                        (validDeviceMetaDataRetrievalPeriodTimestamp - currentTimestamp) / 1000));
            }
            return;
        }
        validDeviceMetaDataRetrievalPeriodTimestamp = currentTimestamp + deviceMetaDataRetrievalTimeout;

        JsonNode httpResponse = doPost("/graphql", Constants.GraphQLRequests.MonitoringRequests.CUSTOMERS_REQUEST, JsonNode.class);
        ArrayNode customers = (ArrayNode) httpResponse.at(Constants.GraphQLProperties.GQL_PATH_CUSTOMERS);

        if (customers != null && !customers.isEmpty()) {
            List<String> customerHandlesTmp = new ArrayList<>();
            customers.forEach(jsonNode -> {
                customerHandlesTmp.add(jsonNode.at(Constants.GraphQLProperties.GQL_PATH_HANDLE).asText());
            });
            customerHandles.addAll(customerHandlesTmp);
            customerHandles.removeIf(handle -> !customerHandlesTmp.contains(handle));

            if (customerHandleFilter != null && !customerHandleFilter.isEmpty()) {
                customerHandles.removeIf(handle -> !customerHandleFilter.contains(handle));
            }
        } else {
            customerHandles.clear();
        }

        for (String handle : customerHandles) {
            JsonNode customerDisplaysMeta = doPost("/graphql", String.format(Constants.GraphQLRequests.MonitoringRequests.DISPLAYS_METADATA_REQUEST, handle), JsonNode.class);
            List<AggregatedDevice> deviceList = aggregatedDeviceProcessor.extractDevices(customerDisplaysMeta.at(Constants.GraphQLProperties.GQL_PATH_CUSTOMER_BY_HANDLE));

            if (deviceTypeFilter != null && !deviceTypeFilter.isEmpty()) {
                deviceList.removeIf(aggregatedDevice -> !deviceTypeFilter.contains(aggregatedDevice.getProperties().get("DisplayType")));
            }

            List<String> retrievedDeviceIds = new ArrayList<>();
            deviceList.forEach(device -> {
                String deviceId = device.getDeviceId();
                retrievedDeviceIds.add(deviceId);
                device.setTimestamp(currentTimestamp);

                if (!aggregatedDevices.containsKey(device.getDeviceId())) {
                    aggregatedDevices.put(device.getDeviceId(), device);
                }
            });
            aggregatedDevices.keySet().removeIf(deviceId -> !retrievedDeviceIds.contains(deviceId));
        }
    }

    /**
     * Process detailed displays information
     *
     * @throws Exception when any error occurs
     */
    private void processDeviceDetails() throws Exception {
        long currentTimestamp = System.currentTimeMillis();
        if (aggregatedDevices.size() > 0 && validDeviceDetailsRetrievalPeriodTimestamp > currentTimestamp) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("General devices metadata retrieval is in cooldown. %s seconds left",
                        (validDeviceDetailsRetrievalPeriodTimestamp - currentTimestamp) / 1000));
            }
            return;
        }
        validDeviceDetailsRetrievalPeriodTimestamp = currentTimestamp + deviceDetailsRetrievalTimeout;

        if (logger.isDebugEnabled()) {
            logger.debug("Process devices details, devices to update: " + aggregatedDevices.keySet());
        }
        for (String handle : customerHandles) {
            JsonNode customerDisplaysBasic = doPost("/graphql", String.format(Constants.GraphQLRequests.MonitoringRequests.DISPLAYS_DETAILS_REQUEST_BASIC, handle), JsonNode.class);
            ResponseWrapper customerDisplaysDetailed = doPost("/graphql", String.format(Constants.GraphQLRequests.MonitoringRequests.DISPLAYS_DETAILS_REQUEST_DETAILED, handle), ResponseWrapper.class);
            Map<String, Display> displayDetails = new HashMap<>();
            if (customerDisplaysDetailed != null) {
                Data displaysWrapper = customerDisplaysDetailed.getData();
                if (displaysWrapper != null) {
                    CustomerByHandle customerByHandle = displaysWrapper.getCustomerByHandle();
                    if (customerByHandle != null) {
                        List<Display> displaysData = customerByHandle.getDisplays();

                        if (deviceTypeFilter != null && !deviceTypeFilter.isEmpty()) {
                            displaysData.removeIf(display -> !deviceTypeFilter.contains(display.getDisplayType()));
                        }

                        displaysData.forEach(display -> displayDetails.put(display.getId(), display));
                    }
                }
            }

            ArrayNode devicesDetails = (ArrayNode) customerDisplaysBasic.at(Constants.GraphQLProperties.GQL_PATH_DISPLAYS);
            for (JsonNode deviceDetails : devicesDetails) {
                String displayId = deviceDetails.at(Constants.GraphQLProperties.GQL_PATH_ID).asText();
                AggregatedDevice aggregatedDevice = aggregatedDevices.get(displayId);
                aggregatedDeviceProcessor.applyProperties(aggregatedDevice, deviceDetails, "WaveDevice");

                Display display = displayDetails.get(displayId);
                if (display != null) {
                    processDeviceAlerts(aggregatedDevice, display);
                    processDeviceBookmarks(aggregatedDevice, display);
                    processDevicePowerSchedule(aggregatedDevice, display);

                    processDeviceAppSubscriptions(aggregatedDevice, display);
                    processDeviceGroups(aggregatedDevice, display);
                }
            }
        }
    }

    /**
     * Process device power schedule details. Includes details about scheduled time blocks, general
     * synchronization details, schedule name etc.
     *
     * @param aggregatedDevice to process scheduling data for
     * @param deviceNode object containing all the data necessary
     */
    private void processDevicePowerSchedule(AggregatedDevice aggregatedDevice, Display deviceNode) {
        if (logger.isDebugEnabled()) {
            logger.debug("Processing device power schedule: " + aggregatedDevice + " " + deviceNode);
        }
        PowerSchedule powerSchedule = deviceNode.getPowerSchedule();
        if (powerSchedule == null) {
            return;
        }
        Map<String, String> properties = aggregatedDevice.getProperties();

        properties.put(Constants.MonitoredProperties.POWER_SCHEDULE_SYNCHRONIZED, String.valueOf(powerSchedule.getIsSynced()));
        Schedule schedule = powerSchedule.getSchedule();
        LatestJob latestJob = powerSchedule.getLatestJob();

        if (schedule != null) {
            properties.put(Constants.MonitoredProperties.POWER_SCHEDULE_CREATED_DATE, String.valueOf(schedule.getCreatedAt()));
            properties.put(Constants.MonitoredProperties.POWER_SCHEDULE_DESCRIPTION, String.valueOf(schedule.getDescription()));
            properties.put(Constants.MonitoredProperties.POWER_SCHEDULE_TITLE, String.valueOf(schedule.getTitle()));
            List<TimeBlock> timeBlocks = schedule.getTimeBlocks();
            if (timeBlocks != null && !timeBlocks.isEmpty()) {
                timeBlocks.forEach(timeBlock -> {
                    String day = timeBlock.getDay();
                    properties.put(String.format(Constants.MonitoredProperties.POWER_SCHEDULE_POWER_ON_TIME, day), timeBlock.getStart());
                    properties.put(String.format(Constants.MonitoredProperties.POWER_SCHEDULE_STANDBY_TIME, day), timeBlock.getEnd());
                });
            }
        }
        if (latestJob != null) {
            properties.put(Constants.MonitoredProperties.POWER_SCHEDULE_LATEST_JOB, String.valueOf(latestJob.getCreatedAt()));
        }
    }

    /**
     * Process device alert details. Includes alert name, last occurrence, total occurrences for each alert type, etc.
     *
     * @param aggregatedDevice to process alerts data for
     * @param deviceNode object containing all the data necessary
     */
    private void processDeviceAlerts(AggregatedDevice aggregatedDevice, Display deviceNode) {
        if (logger.isDebugEnabled()) {
            logger.debug("Processing device alerts: " + aggregatedDevice + " " + deviceNode);
        }
        List<Alert> alerts = deviceNode.getAlerts();
        Map<String, String> properties = aggregatedDevice.getProperties();
        if (alerts == null || alerts.isEmpty()) {
            properties.put(Constants.MonitoredProperties.ALERTS_TOTAL_COUNT, "0");
            return;
        }
        Map<String, TreeMap<Date, Alert>> alertEntriesByType = new HashMap<>();

        Map<String, Integer> alertTypesOccurrences = new HashMap<>();
        for (Alert alert : alerts) {
            String alertType = alert.getMessage();
            TreeMap<Date, Alert> alertEntries = alertEntriesByType.computeIfAbsent(alertType, k -> new TreeMap<>());
            alertEntries.put(alert.getCreatedAt(), alert);
            alertTypesOccurrences.merge(alertType, 1, Integer::sum);
        }

        int index = 1;
        int totalAlertCount = alertTypesOccurrences.values().stream().reduce(0, Integer::sum);
        for (Map.Entry<String, TreeMap<Date, Alert>> entry : alertEntriesByType.entrySet()) {
            Alert alertEntry = entry.getValue().lastEntry().getValue();
            properties.put(String.format(Constants.MonitoredProperties.ALERTS_ALERT_MESSAGE, index), alertEntry.getMessage());
            properties.put(String.format(Constants.MonitoredProperties.ALERTS_ALERT_OCCURRENCE_COUNT, index), String.valueOf(alertTypesOccurrences.get(entry.getKey())));
            properties.put(String.format(Constants.MonitoredProperties.ALERTS_ALERT_LAST_OCCURRED, index), String.valueOf(alertEntry.getCreatedAt()));
            index++;
        }
        properties.put(Constants.MonitoredProperties.ALERTS_ALERT_TOTAL_COUNT, String.valueOf(totalAlertCount));
    }

    /**
     * Process device bookmark details.
     *
     * @param aggregatedDevice to process bookmarks data for
     * @param deviceNode object containing all the data necessary
     */
    private void processDeviceBookmarks(AggregatedDevice aggregatedDevice, Display deviceNode) {
        if (logger.isDebugEnabled()) {
            logger.debug("Processing device bookmarks: " + aggregatedDevice + " " + deviceNode);
        }
        Bookmarks bookmarks = deviceNode.getBookmarks();
        if (bookmarks == null) {
            return;
        }
        ReportedDataWrapper reportedData = bookmarks.getAll();
        if (reportedData == null) {
            return;
        }
        List<String> data = reportedData.getReported();
        if (data == null) {
            return;
        }
        Map<String, String> properties = aggregatedDevice.getProperties();
        int index = 1;
        for (String bookmark : data) {
            if (StringUtils.isNotNullOrEmpty(bookmark)) {
                properties.put(String.format(Constants.MonitoredProperties.BOOKMARKS_BOOKMARK_TITLE, index), bookmark);
            }
            index++;
        }
    }

    /**
     * Process device groups details.
     *
     * @param aggregatedDevice to process groups data for
     * @param deviceNode object containing all the data necessary
     */
    private void processDeviceGroups(AggregatedDevice aggregatedDevice, Display deviceNode) {
        if (logger.isDebugEnabled()) {
            logger.debug("Processing device groups: " + aggregatedDevice + " " + deviceNode);
        }
        List<Group> groups = deviceNode.getGroups();
        if (groups == null) {
            return;
        }
        Map<String, String> properties = aggregatedDevice.getProperties();
        properties.put(Constants.MonitoredProperties.SYSTEM_GROUPS, groups.stream().map(Group::getName).collect(Collectors.joining(";")));
    }

    /**
     * Process device app subscriptions details.
     *
     * @param aggregatedDevice to process app subscriptions data for
     * @param deviceNode object containing all the data necessary
     */
    private void processDeviceAppSubscriptions(AggregatedDevice aggregatedDevice, Display deviceNode) {
        if (logger.isDebugEnabled()) {
            logger.debug("Processing device app subscriptions: " + aggregatedDevice + " " + deviceNode);
        }
    }

    /**
     * Reboot command execution
     *
     * @param displayId to execute the command for
     */
    private void commandReboot(String displayId) throws Exception {
        doPost("/graphql", String.format(Constants.GraphQLRequests.ControlRequest.REBOOT, displayId), JsonNode.class);
    }

    /**
     * Audio mute/unmute status change command
     *
     * @param displayId to execute command for
     * @param muteStatus new status
     */
    private void commandChangeMuteStatus(String displayId, String muteStatus) throws Exception {
        doPost("/graphql", String.format(Constants.GraphQLRequests.ControlRequest.MUTE, displayId, muteStatus), JsonNode.class);
    }

    /**
     * Volume change command
     *
     * @param displayId to execute command for
     * @param volumeLevel new volume level
     */
    private void commandChangeVolume(String displayId, String volumeLevel) throws Exception {
        doPost("/graphql", String.format(Constants.GraphQLRequests.ControlRequest.VOLUME, displayId, Float.parseFloat(volumeLevel)), JsonNode.class);
    }

    /**
     * Screen brightness change
     *
     * @param displayId to execute command for
     * @param brightnessLevel new brightness level
     */
    private void commandChangeBrightness(String displayId, String brightnessLevel) throws Exception {
        doPost("/graphql", String.format(Constants.GraphQLRequests.ControlRequest.BRIGHTNESS, displayId, Float.parseFloat(brightnessLevel)), JsonNode.class);
    }

    /**
     * Power state change command, ON/STANDBY
     *
     * @param displayId to execute command for
     * @param powerState new power state
     */
    private void commandChangePowerState(String displayId, String powerState) throws Exception {
        String powerStateValue = "1".equals(powerState) ? "ON" : "STANDBY";
        doPost("/graphql", String.format(Constants.GraphQLRequests.ControlRequest.POWER, displayId, powerStateValue), JsonNode.class);
    }

    /**
     * Screen orientation change, LANDSCAPE/PORTRAIT
     *
     * @param displayId to execute command for
     * @param orientationState new orientation state
     */
    private void commandChangeOrientation(String displayId, String orientationState) throws Exception {
        doPost("/graphql", String.format(Constants.GraphQLRequests.ControlRequest.ORIENTATION, displayId, orientationState), JsonNode.class);
    }

    /**
     * Video input source change
     *
     * @param displayId to execute command for
     * @param inputState new input state
     */
    private void commandChangeInput(String displayId, String inputState) throws Exception {
        doPost("/graphql", String.format(Constants.GraphQLRequests.ControlRequest.INPUT, displayId, inputState), JsonNode.class);
    }

    /**
     * Take screenshot command
     *
     * @param displayId to execute command for
     */
    private void commandTakeScreenshot(String displayId) throws Exception {
        doPost("/graphql", String.format(Constants.GraphQLRequests.ControlRequest.SCREENSHOT, displayId), JsonNode.class);
    }

    /**
     * Change IR control mode command, LOCKED/POWER_ONLY/UNLOCKED/VOLUME_ONLY
     *
     * @param displayId to execute command for
     * @param irMode new IR control mode
     */
    private void commandChangeIRMode(String displayId, String irMode) throws Exception {
        doPost("/graphql", String.format(Constants.GraphQLRequests.ControlRequest.IR_MODE, displayId, irMode), JsonNode.class);
    }

    /**
     * Change keyboard control mode command, LOCKED/POWER_ONLY/UNLOCKED/VOLUME_ONLY
     *
     * @param displayId to execute command for
     * @param keyboardState new keyboard control mode
     */
    private void commandChangeKeyboardMode(String displayId, String keyboardState) throws Exception {
        doPost("/graphql", String.format(Constants.GraphQLRequests.ControlRequest.KEYBOARD_MODE, displayId, keyboardState), JsonNode.class);
    }

    /**
     * LED Strip color change command
     *
     * @param displayId to execute command for
     * @param ledColor new LED Strip color
     */
    private void commandChangeLedColor(String displayId, String ledColor) throws Exception {
        doPost("/graphql", String.format(Constants.GraphQLRequests.ControlRequest.LED_COLOR, displayId, ledColor), JsonNode.class);
    }

    /**
     * Ports control state change command, LOCKED/UNLOCKED
     *
     * @param displayId to execute command for
     * @param controlState new ports control state
     */
    private void commandChangePortsControlState(String displayId, String controlState) throws Exception {
        doPost("/graphql", String.format(Constants.GraphQLRequests.ControlRequest.PORTS_CONTROL, displayId, controlState), JsonNode.class);
    }

    /**
     * Change display alias command
     *
     * @param displayId to execute command for
     * @param aliasValue new display alias
     */
    private void commandChangeAlias(String displayId, String aliasValue) throws Exception {
        doPost("/graphql", String.format(Constants.GraphQLRequests.ControlRequest.ALIAS, displayId, aliasValue), JsonNode.class);
    }

    /**
     * Updates cached devices' control value, after the control command was executed with the specified value.
     * It is done in order for aggregator to populate latest control values, after the control command has been executed,
     * but before the next devices details polling cycle was addressed.
     *
     * @param deviceId to update control value for
     * @param name of the control property
     * @param value to set to the control property
     */
    private void updateLocalControlValue(String deviceId, String name, String value) {
        Optional<AggregatedDevice> device = aggregatedDevices.values().stream().filter(aggregatedDevice ->
                deviceId.equals(aggregatedDevice.getDeviceId())).findFirst();
        device.flatMap(aggregatedDevice ->
                aggregatedDevice.getControllableProperties().stream().filter(advancedControllableProperty ->
                        name.equals(advancedControllableProperty.getName())).findFirst()).ifPresent(advancedControllableProperty ->
                advancedControllableProperty.setValue(value));
        device.ifPresent(aggregatedDevice -> aggregatedDevice.getProperties().put(name, value));
    }

    /**
     * Uptime is received in seconds, need to normalize it and make it human readable, like
     * 1 day(s) 5 hour(s) 12 minute(s) 55 minute(s)
     * Incoming parameter is may have a decimal point, so in order to safely process this - it's rounded first.
     * We don't need to add a segment of time if it's 0.
     *
     * @param uptimeSeconds value in seconds
     * @return string value of format 'x day(s) x hour(s) x minute(s) x minute(s)'
     */
    private String normalizeUptime(long uptimeSeconds) {
        StringBuilder normalizedUptime = new StringBuilder();

        long seconds = uptimeSeconds % 60;
        long minutes = uptimeSeconds % 3600 / 60;
        long hours = uptimeSeconds % 86400 / 3600;
        long days = uptimeSeconds / 86400;

        if (days > 0) {
            normalizedUptime.append(days).append(" day(s) ");
        }
        if (hours > 0) {
            normalizedUptime.append(hours).append(" hour(s) ");
        }
        if (minutes > 0) {
            normalizedUptime.append(minutes).append(" minute(s) ");
        }
        if (seconds > 0) {
            normalizedUptime.append(seconds).append(" second(s)");
        }
        return normalizedUptime.toString().trim();
    }

    /**
     * Update the status of the device.
     * The device is considered as paused if did not receive any retrieveMultipleStatistics()
     * calls during {@link PhilipsWaveAggregatorCommunicator#validRetrieveStatisticsTimestamp}
     */
    private synchronized void updateAggregatorStatus() {
        devicePaused = validRetrieveStatisticsTimestamp < System.currentTimeMillis();
    }

    /**
     * Update general aggregator status (paused or active) and update the value, based on which
     * it the device is considered paused (2 minutes inactivity -> {@link #retrieveStatisticsTimeOut})
     */
    private synchronized void updateValidRetrieveStatisticsTimestamp() {
        validRetrieveStatisticsTimestamp = System.currentTimeMillis() + retrieveStatisticsTimeOut;
        updateAggregatorStatus();
    }
}
