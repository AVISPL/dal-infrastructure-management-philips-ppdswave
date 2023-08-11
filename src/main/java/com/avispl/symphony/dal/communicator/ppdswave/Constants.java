/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.ppdswave;

/**
 * Aggregator constants, includes monitor/control Property names and GraphQL paths
 *
 * @author Maksym.Rossiytsev
 * Created on 10/07/2023
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
        String SYSTEM_GROUPS = "System#Groups";
        String CONTENT_SOURCE = "ContentSource#CurrentSource";
    }
    /** Utility constants */
    interface Utility {
        String EMPTY_STRING = "";
        String EMPTY = "Empty";
        String NONE_LABEL = "-NONE-";
        String NONE = "None";
        String COLON_CONTENT = "%s:%s";
    }
    /** Source types constants */
    interface SourceType {
        String APPLICATION = "Other:APPLICATION";
        String BOOKMARK = "Other:BOOKMARK";
        String INPUT = "Other:INPUT";
        String PLAYLIST = "Other:PLAYLIST";
        String APPLICATION_NAME = "APPLICATION";
        String BOOKMARK_NAME = "BOOKMARK";
        String INPUT_NAME = "INPUT";
        String PLAYLIST_NAME = "PLAYLIST";
        String PREFIX = "Other:";
    }
    /** Control properties */
    interface ControlProperties {
        String CONTROL_AUDIO_VOLUME = "Audio#Volume";
        String CONTROL_VIDEO_BRIGHTNESS = "Video#Brightness";
        String CONTROL_VIDEO_ORIENTATION = "Video#Orientation";
        String CONTROL_AUDIO_MUTE = "Audio#Mute";
        String CONTROL_VIDEO_INPUT_SOURCE = "ContentSource#SourceVideoInput";
        String CONTROL_APPLICATION_SOURCE = "ContentSource#SourceApplication";
        String CONTROL_BOOKMARK_SOURCE = "ContentSource#SourceBookmark";
        String CONTROL_PLAYLIST_SOURCE = "ContentSource#SourcePlaylist";
        String CONTROL_CONTENT_SOURCE = "ContentSource#ContentSourceType";
        String CONTROL_POWER_MODE = "Power#Mode";
        String CONTROL_POWER_REBOOT = "Power#Reboot";
        String CONTROL_SCREENSHOT_CREATE = "Screenshot#Create";
        String CONTROL_IR_CONTROL = "System#InfraRedControl";
        String CONTROL_KEYBOARD_CONTROL = "System#KeyboardControl";
        String CONTROL_LED_COLOR = "System#LEDStripColor";
        String CONTROL_ALIAS = "System#Alias";
        String CONTROL_PORTS_CONTROL = "System#PortsControl";
        String CONTENT_SOURCE_NAME = "ContentSource";
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
            String DISPLAYS_METADATA_REQUEST = "{\"operationName\": null,\"variables\":{},\"query\":\"{customerByHandle(handle:\\\"%s\\\"){displays{id\\nalias\\ndisplayType\\nserialNumber\\ncommercialTypeNumber\\npresence{\\nconnected}}}}\"}";
            String DISPLAYS_DETAILS_REQUEST_BASIC = "{\"operationName\": null,\"variables\":{},\"query\":\"{customerByHandle(handle:\\\"%s\\\"){displays{id\\nalias\\ndisplayType\\nserialNumber\\nagentVersion\\ncommercialTypeNumber\\nagentReleaseChannel{reported}agentVersion\\nbrightness{reported}commercialTypeNumber\\ncustomer{avatarUrl\\nhandle\\nid\\nname\\n}site{address\\nname}displayType\\nfirmware{android{availableUpdates\\nlatestJob{createdAt\\nplannedAt\\ntargetVersion\\nid}version}scaler{version}}groups{id\\nname}hasEmptyShadow\\nhasSensitiveData\\ninfraRedControl{reported}keyboardControl{reported}ledStripColor{reported}networkInformation{ethernetMacAddress\\nlocalIPAddress\\nnetworkType\\nwifiMacAddress}networkInterfaces{active\\nip\\nmac\\nname\\nssid}orientation{reported}platform{name\\nversion}portsControl{reported}power{reported\\nreportedAt}presence{connected\\ntimestamp}reboot{latestJob{createdAt\\nid\\nplannedAt}}recommendedSettings{reported{recommended\\nwarnings{code\\ndescription\\nseverity\\n}}}screenshot{createdAt\\nurl}serialNumber\\nsignalDetection{reported}timeZone{reported}volume{isMuted{reported}level{reported}limits{max{reported}min{reported}}}power{reported\\nreportedAt}}}}\"}";
            String DISPLAYS_DETAILS_REQUEST_DETAILED = "{\"operationName\": null,\"variables\":{},\"query\":\"{customerByHandle(handle:\\\"%s\\\"){playlists{id\\ntitle}displays{id\\ndisplayType\\nalerts{createdAt\\nid\\nmessage}\\ncontentSource{available{...on InputContentSource{source}...on AppContentSource{applicationId\\nlabel}}current{reported{...on InputContentSource{source}...on AppContentSource{applicationId\\nlabel}...on BookmarkContentSource{index}...on PlaylistContentSource{playlistId}}}}\\nappSubscriptions{appInstallation{applicationId\\nid}createdAt\\niconUrl\\nid\\nname\\nupdatedAt\\nusage{current\\nmax}}bookmarks{all{reported}}groups{id\\nname}playlist{current{description\\nid\\nisOutOfSync\\nisTampered\\nsize\\ntitle}sync{description\\nid\\njobId\\nsize\\ntitle}}powerSchedule{isSynced\\nlatestJob{createdAt\\nid\\nscheduleId}schedule{createdAt\\ndescription\\nid\\ntimeBlocks{day\\nend\\nstart}title}}}}}\"}";
        }
        /** Control requests */
        interface ControlRequest {
            String REBOOT = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayBulkReboot(input:{displayIds:[\\\"%s\\\"]}){displays{id}}}\"}";
            String MUTE = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayUpdateVolumeMute(input:{id:\\\"%s\\\"\\nmute:%s}){display{id}}}\"}";
            String VOLUME = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayUpdateVolumeLevel (input:{id:\\\"%s\\\"\\nlevel:%.0f}){display{id\\nvolume{level{desired\\nreported}}}}}\"}";
            String BRIGHTNESS = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayBulkUpdateBrightness(input:{displayIds:[\\\"%s\\\"]\\nbrightness:%.0f}){displays{id\\nbrightness{reported}}}}\"}";
            String POWER = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayUpdatePower (input:{id:\\\"%s\\\"\\npower:%s}){display{id\\npower{reported}}}}\"}";
            String ORIENTATION = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayUpdateOrientation(input:{id:\\\"%s\\\"\\norientation:%s}){display{id\\norientation{reported}}}}\"}";
            String INPUT = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayBulkUpdateInputContentSource(input:{displayIds:[\\\"%s\\\"]\\nsource:\\\"%s\\\"}){displays{id\\ninputSource{available\\ncurrent{reported{source}}}}}}\"}";
            String PLAYLIST = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayBulkUpdatePlaylistContentSource(input:{displayIds:[\\\"%s\\\"]\\nplaylistId:\\\"%s\\\"}){displays{id\\ninputSource{available\\ncurrent{reported{source}}}}}}\"}";
            String BOOKMARK = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayBulkUpdateBookmarkContentSource(input:{displayIds:[\\\"%s\\\"]\\nindex:%d}){displays{id\\ninputSource{available\\ncurrent{reported{source}}}}}}\"}";
            String APPLICATION = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayBulkUpdateAppContentSource(input:{displayIds:[\\\"%s\\\"]\\napplicationId:\\\"%s\\\"}){displays{id\\ninputSource{available\\ncurrent{reported{source}}}}}}\"}";
            String SCREENSHOT = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayCaptureScreenshot(input:{displayId:\\\"%s\\\"}){display{id\\nscreenshot{createdAt\\nurl}}}}\"}";
            String IR_MODE = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayBulkUpdateInfraRedControl(input:{displayIds:[\\\"%s\\\"]\\ncontrolState:%s}){displays{id\\ninfraRedControl{reported}}}}\"}";
            String KEYBOARD_MODE = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayBulkUpdateKeyboardControl(input:{displayIds:[\\\"%s\\\"]\\ncontrolState:%s}){displays{id\\nkeyboardControl{reported}}}}\"}";
            String LED_COLOR = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayBulkUpdateLedStripColor(input:{displayIds:[\\\"%s\\\"]\\nledStripColor:%s}){displays{id\\nledStripColor{reported}}}}\"}";
            String PORTS_CONTROL = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayBulkUpdatePortsControl(input:{displayIds:[\\\"%s\\\"]\\nportsControlState:%s}){displays{id}}}\"}";
            String ALIAS = "{\"operationName\": null,\"variables\":{},\"query\":\"mutation{displayUpdateAlias(input:{displayId:\\\"%s\\\"\\nalias:\\\"%s\\\"}){id\\nalias}}\"}";
        }
    }
}
