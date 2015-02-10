package com.dgsd.android.common;

public class WearableConstants {

    public static final class Path {
        public static final String ALERT_START = "/start_alert";
        public static final String ALERT_STOP = "/stop_alert";
        public static final String ALERT_STATUS = "/alert_status";
        public static final String SEND_DEVICE_NAME = "/send_device_name";
        public static final String SEND_ALERT_STATUS = "/send_alert_status";
        public static final String DEVICE_NAME = "/device_name";
        public static final String WATCH_NAME = "/watch_name";
    }

    public static final class Data {
        public static final String DEVICE_NAME = "_device_name";
        public static final String STATUS = "_status";
    }

    public static final class AlertStatus {
        public static final String RUNNING = "_status_running";
        public static final String NOT_RUNNING = "_status_not_running";
        public static final String UNKNOWN = "_status_unknown";
    }

    public static final long API_CONNECTION_TIMEOUT = 30;

}
