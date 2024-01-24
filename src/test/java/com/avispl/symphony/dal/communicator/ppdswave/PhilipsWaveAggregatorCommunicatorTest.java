/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.ppdswave;

import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

@Tag("test")
public class PhilipsWaveAggregatorCommunicatorTest {
    static PhilipsWaveAggregatorCommunicator philipsWaveAggregatorCommunicator;

    @BeforeEach
    public void init() throws Exception {
        philipsWaveAggregatorCommunicator = new PhilipsWaveAggregatorCommunicator();
        philipsWaveAggregatorCommunicator.setPassword("");
        philipsWaveAggregatorCommunicator.setHost("api.wave.ppds.com");
        philipsWaveAggregatorCommunicator.setProtocol("https");
        philipsWaveAggregatorCommunicator.setPort(443);
        philipsWaveAggregatorCommunicator.init();
    }

    @Test
    public void retrieveDevicesListTest() throws Exception {
        philipsWaveAggregatorCommunicator.setDisplayPropertyGroups("screenshot");
        philipsWaveAggregatorCommunicator.retrieveMultipleStatistics();
        Thread.sleep(30000);
        philipsWaveAggregatorCommunicator.retrieveMultipleStatistics();
        Thread.sleep(30000);
        List<AggregatedDevice> deviceList = philipsWaveAggregatorCommunicator.retrieveMultipleStatistics();
        Thread.sleep(30000);
        Assertions.assertNotNull(deviceList);
        Assertions.assertFalse(deviceList.isEmpty());
    }

    @Test
    public void retrieveDevicesListWithTypeFilterCorrectTest() throws Exception {
        philipsWaveAggregatorCommunicator.setCustomerHandleFilter(null);
        philipsWaveAggregatorCommunicator.retrieveMultipleStatistics();
        Thread.sleep(30000);
        philipsWaveAggregatorCommunicator.retrieveMultipleStatistics();
        Thread.sleep(30000);
        List<AggregatedDevice> deviceList = philipsWaveAggregatorCommunicator.retrieveMultipleStatistics();
        Thread.sleep(30000);
        Assertions.assertFalse(deviceList.isEmpty());
    }

    @Test
    public void retrieveDevicesListWithTypeFilterIncorrectTest() throws Exception {
        philipsWaveAggregatorCommunicator.setCustomerHandleFilter(null);
        philipsWaveAggregatorCommunicator.retrieveMultipleStatistics();
        Thread.sleep(30000);
        philipsWaveAggregatorCommunicator.retrieveMultipleStatistics();
        Thread.sleep(30000);
        List<AggregatedDevice> deviceList = philipsWaveAggregatorCommunicator.retrieveMultipleStatistics();
        Thread.sleep(30000);
        Assertions.assertTrue(deviceList.isEmpty());
    }

    @Test
    public void retrieveDevicesListWithHandleFilterCorrectTest() throws Exception {
        philipsWaveAggregatorCommunicator.setCustomerHandleFilter("example-customer");
        List<AggregatedDevice> deviceList = philipsWaveAggregatorCommunicator.retrieveMultipleStatistics();
        Thread.sleep(30000);
        deviceList = philipsWaveAggregatorCommunicator.retrieveMultipleStatistics();
        Thread.sleep(30000);
        deviceList = philipsWaveAggregatorCommunicator.retrieveMultipleStatistics();
        Thread.sleep(30000);
        Assertions.assertFalse(deviceList.isEmpty());
    }

    @Test
    public void retrieveDevicesListWithHandleFilterIncorrectTest() throws Exception {
        philipsWaveAggregatorCommunicator.setCustomerHandleFilter("asdasdasd");
        philipsWaveAggregatorCommunicator.retrieveMultipleStatistics();
        Thread.sleep(30000);
        philipsWaveAggregatorCommunicator.retrieveMultipleStatistics();
        Thread.sleep(30000);
        List<AggregatedDevice> deviceList = philipsWaveAggregatorCommunicator.retrieveMultipleStatistics();
        Thread.sleep(30000);
        Assertions.assertTrue(deviceList.isEmpty());
    }

    @Test
    public void volumeChangeTest() throws Exception {
        philipsWaveAggregatorCommunicator.retrieveMultipleStatistics();
        Thread.sleep(30000);
        philipsWaveAggregatorCommunicator.retrieveMultipleStatistics();
        ControllableProperty controllableProperty = new ControllableProperty();
        controllableProperty.setValue("10.0");
        controllableProperty.setProperty("Audio#Volume");
        controllableProperty.setDeviceId("b2d87ebd-675a-44e8-b2d8-47cd4ed4e2a5");

        philipsWaveAggregatorCommunicator.controlProperty(controllableProperty);
    }
}
