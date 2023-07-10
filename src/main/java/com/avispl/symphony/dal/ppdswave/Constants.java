/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.ppdswave;

/**
 * Aggregator constants, includes monitor/control Property names and GraphQL paths
 *
 * @author Maksym.Rossiytsev
 * @since 1.0.0
 * */
public interface Constants {
    /** Monitored properties */
    interface MonitoredProperties {
        String POWER_SCHEDULE_SYNCHRONIZED = "PowerSchedule#Synchronized";
        String POWER_SCHEDULE_CREATED_DATE = "PowerSchedule#CreatedDate";
        String POWER_SCHEDULE_DESCRIPTION = "PowerSchedule#Description";
        String POWER_SCHEDULE_TITLE = "PowerSchedule#Title";
        String POWER_SCHEDULE_POWER_ON_TIME = "PowerSchedule#%s_PowerOnTime";
        String POWER_SCHEDULE_STANDBY_TIME = "PowerSchedule#%s_StandbyTime";
        String POWER_SCHEDULE_LATEST_JOB = "PowerSchedule#LatestJob";
        String ALERTS_TOTAL_COUNT = "Alerts#TotalCount";
        String ALERTS_ALERT_MESSAGE = "Alerts#Alert%02d_Message";
        String ALERTS_ALERT_OCCURRENCE_COUNT = "Alerts#Alert%02d_OccurrenceCount";
        String ALERTS_ALERT_LAST_OCCURRED = "Alerts#Alert%02d_LastOccurred";
        String ALERTS_ALERT_TOTAL_COUNT = "Alerts#TotalCount";
        String BOOKMARKS_BOOKMARK_TITLE = "Bookmarks#Bookmark_%02d";
        String SYSTEM_GROUPS = "System#Groups";
    }
    /** Control properties */
    interface ControlProperties {
        String CONTROL_AUDIO_VOLUME = "Audio#Volume";
        String CONTROL_VIDEO_BRIGHTNESS = "Video#Brightness";
        String CONTROL_VIDEO_ORIENTATION = "Video#Orientation";
        String CONTROL_AUDIO_MUTE = "Audio#Mute";
        String CONTROL_VIDEO_INPUT_SOURCE = "Video#InputSource";
        String CONTROL_POWER_MODE = "Power#Mode";
        String CONTROL_POWER_REBOOT = "Power#Reboot";
        String CONTROL_SCREENSHOT_CREATE = "Screenshot#Create";
        String CONTROL_IR_CONTROL = "System#InfraRedControl";
        String CONTROL_KEYBOARD_CONTROL = "System#KeyboardControl";
        String CONTROL_LED_COLOR = "System#LEDStripColor";
        String CONTROL_ALIAS = "System#Alias";
        String CONTROL_PORTS_CONTROL = "System#PortsControl";
    }
    /** GraphQL path */
    interface GraphQLProperties {
        String GQL_PATH_CUSTOMERS = "/data/organization/customers";
        String GQL_PATH_HANDLE = "/handle";
        String GQL_PATH_CUSTOMER_BY_HANDLE = "/data/customerByHandle";
        String GQL_PATH_DISPLAYS = "/data/customerByHandle/displays";
        String GQL_PATH_ID = "/id";
    }
    /**
     * GraphQL requests
     *
     * GraphQL command templates for PPDS Wave communication
     * The values are built as string templates, to be used with String.format() in accordance to the
     * template variables set (%s, %d or %f)
     * */
    interface GraphQLRequests {
        /** Monitoring data requests */
        interface MonitoringRequests {
            String CUSTOMERS_REQUEST = "{\"operationName\": null,\"variables\":{},\"query\":\"{organization {customers {id\\nname\\nhandle}}}\"}";
            String DISPLAYS_METADATA_REQUEST = "{\"operationName\": null,\"variables\":{},\"query\":\"{customerByHandle(handle:\\\"%s\\\"){displays{id\\nalias\\ndisplayType\\nserialNumber\\npresence{\\nconnected}}}}\"}";
            String DISPLAYS_DETAILS_REQUEST_BASIC = "{\"operationName\": null,\"variables\":{},\"query\":\"{customerByHandle(handle:\\\"%s\\\"){displays{id\\nalias\\ndisplayType\\nserialNumber\\nagentVersion\\ncommercialTypeNumber\\nagentReleaseChannel{reported}agentVersion\\nbrightness{reported}commercialTypeNumber\\ninputSource{available\\ncurrent{reported{source}}}customer{avatarUrl\\nhandle\\nid\\nname\\n}site{address\\nname}displayType\\nfirmware{android{availableUpdates\\nlatestJob{createdAt\\nplannedAt\\ntargetVersion\\nid}version}scaler{version}}groups{id\\nname}hasEmptyShadow\\nhasSensitiveData\\ninfraRedControl{reported}keyboardControl{reported}ledStripColor{reported}networkInformation{ethernetMacAddress\\nlocalIPAddress\\nnetworkType\\nwifiMacAddress}networkInterfaces{active\\nip\\nmac\\nname\\nssid}orientation{reported}platform{name\\nversion}portsControl{reported}power{reported\\nreportedAt}presence{connected\\ntimestamp}reboot{latestJob{createdAt\\nid\\nplannedAt}}recommendedSettings{reported{recommended\\nwarnings{code\\ndescription\\nseverity\\n}}}screenshot{createdAt\\nurl}serialNumber\\nsignalDetection{reported}timeZone{reported}volume{isMuted{reported}level{reported}limits{max{reported}min{reported}}}power{reported\\nreportedAt}}}}\"}";
            String DISPLAYS_DETAILS_REQUEST_DETAILED = "{\"operationName\": null,\"variables\":{},\"query\":\"{customerByHandle(handle:\\\"%s\\\"){displays{id\\ndisplayType\\nalerts{createdAt\\nid\\nmessage}appSubscriptions{appInstallation{applicationId\\nid}createdAt\\niconUrl\\nid\\nname\\nupdatedAt\\nusage{current\\nmax}}bookmarks{all{reported}}groups{id\\nname}playlist{current{description\\nid\\nisOutOfSync\\nisTampered\\nsize\\ntitle}sync{description\\nid\\njobId\\nsize\\ntitle}}powerSchedule{isSynced\\nlatestJob{createdAt\\nid\\nscheduleId}schedule{createdAt\\ndescription\\nid\\ntimeBlocks{day\\nend\\nstart}title}}}}}\"}";
        }
        /** Control requests */
        interface ControlRequest {
            String REBOOT = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayBulkReboot(input:{displayIds:[\\\"%s\\\"]}){displays{id}}}\"}";
            String MUTE = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayUpdateVolumeMute(input:{id:\\\"%s\\\"\\nmute:%s}){display{id}}}\"}";
            String VOLUME = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayUpdateVolumeLevel (input:{id:\\\"%s\\\"\\nlevel:%.0f}){display{id\\nvolume{level{desired\\nreported}}}}}\"}";
            String BRIGHTNESS = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayBulkUpdateBrightness(input:{displayIds:[\\\"%s\\\"]\\nbrightness:%.0f}){displays{id\\nbrightness{reported}}}}\"}";
            String POWER = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayUpdatePower (input:{id:\\\"%s\\\"\\npower:%s}){display{id\\npower{reported}}}}\"}";
            String ORIENTATION = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayUpdateOrientation(input:{id:\\\"%s\\\"\\norientation:%s}){display{id\\norientation{reported}}}}\"}";
            String INPUT = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayUpdateInputSource(input:{id:\\\"%s\\\"\\nsource:\\\"%s\\\"}){display{id\\ninputSource{available\\ncurrent{reported{source}}}}}}\"}";
            String SCREENSHOT = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayCaptureScreenshot(input:{displayId:\\\"%s\\\"}){display{id\\nscreenshot{createdAt\\nurl}}}}\"}";
            String IR_MODE = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayBulkUpdateInfraRedControl(input:{displayIds:[\\\"%s\\\"]\\ncontrolState:%s}){displays{id\\ninfraRedControl{reported}}}}\"}";
            String KEYBOARD_MODE = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayBulkUpdateKeyboardControl(input:{displayIds:[\\\"%s\\\"]\\ncontrolState:%s}){displays{id\\nkeyboardControl{reported}}}}\"}";
            String LED_COLOR = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayBulkUpdateLedStripColor(input:{displayIds:[\\\"%s\\\"]\\nledStripColor:%s}){displays{id\\nledStripColor{reported}}}}\"}";
            String PORTS_CONTROL = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayBulkUpdatePortsControl(input:{displayIds:[\\\"%s\\\"]\\nportsControlState:%s}){displays{id}}}\"}";
            String ALIAS = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayUpdateAlias(input:{displayId:\\\"%s\\\"\\nalias:\\\"%s\\\"}){id\\nalias}}\"}";
        }
    }
}
