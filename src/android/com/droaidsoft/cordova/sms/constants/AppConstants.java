package com.droaidsoft.cordova.sms.constants;

public class AppConstants {
    public static final int READ_SMS_PERMISIONS = 101;
    public static final int RECEIVE_SMS_PERMISIONS = 102;
    public static final int SUCCESS = 200;
    public static final int ERROR = 401;
    public static final String ADDRESS = "ADDRESS";
    public static final String MESSAGE = "MESSAGE";
    public static final int NOTIFICATION_ID_SMALL = 100;
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String LOG_TAG = "cordova-plugin-smsmanager";
    public static final String ACTION_FETCH_SMS = "fetchSMS";
    public static final String ACTION_START_WATCH = "startWatch";
    public static final String ACTION_STOP_WATCH = "stopWatch";
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    public static final int BUILD_VERSION = 19;
    public static final int START_WATCH_REQ_CODE = 0;
    public static final int PERMISSION_DENIED_ERROR = 20;
    
    public static final String OPTION_FOLDER = "folder";
    public static final String OPTION_MSG_ID = "_id";
    public static final String OPTION_THREAD_ID = "thread_id";
    public static final String OPTION_ADDRESS = "address";
    public static final String OPTION_SERVICE_CENTER = "service_center";
    public static final String OPTION_DATE = "date";
    public static final String OPTION_DATE_SENT = "date_sent";
    public static final String OPTION_MAX_COUNT = "maxCount";
    
    public static final String SMS_URI = "content://sms/";
    
    /*
     * Response COnstants
     */
    public static final String RESPONSE_MSG_ID = "_id";
    public static final String RESPONSE_THREAD_ID = "thread_id";
    public static final String RESPONSE_ADDRESS = "address";
    public static final String RESPONSE_DATE = "date";
    public static final String RESPONSE_DATE_SENT = "date_sent";
    public static final String RESPONSE_PROTOCOL = "protocol";
    public static final String RESPONSE_READ = "read";
    public static final String RESPONSE_STATUS = "status";
    public static final String RESPONSE_TYPE = "type";
    public static final String RESPONSE_REPLY_PATH_PRESENT = "reply_path_present";
    public static final String RESPONSE_BODY = "body";
    public static final String RESPONSE_SERVICE_CENTER = "service_center";
    public static final String RESPONSE_LOCKED = "locked";
    public static final String RESPONSE_SUB_ID = "sub_id";
    public static final String RESPONSE_ERROR_CODE = "error_code";
    public static final String RESPONSE_CREATOR = "creator";
    public static final String RESPONSE_SEEN = "seen";
    public static final String RESPONSE_PRIORITY = "priority";
    
}
