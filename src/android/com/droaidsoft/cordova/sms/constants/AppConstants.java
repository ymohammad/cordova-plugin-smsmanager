/*******************************************************************************************************************************
The MIT License (MIT)
Copyright © 2020, Mohammed Sufiyan Al Yousufi

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, 
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the 
following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED 
TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
IN THE SOFTWARE.
*******************************************************************************************************************************/

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
    public static final String LOG_TAG = "cordova-smsmanager";
    
    //Method Actions.
    public static final String ACTION_FETCH_SMS = "fetchSMS";
    public static final String ACTION_START_WATCH = "startWatch";
    public static final String ACTION_STOP_WATCH = "stopWatch";
    public static final String ACTION_FETCH_CONVERSATION_INFO = "fetchConversationInfo";
    public static final String ACTION_FETCH_ALL_CONVERSATIONS_OF_A_NUMBER = "fetchAllConversationsOfNumber";
    public static final String ACTION_FETCH_ALL_MESSAGES_OF_A_CONVERSATION = "fetchAllMessagesOfAConversation";
    public static final String ACTION_FETCH_ALL_USERS_OF_A_CONVERSATION = "fetchAllUsersOfAConversation";
    
    //Event Actions.
    public static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    public static final int BUILD_VERSION = 19;
    public static final int START_WATCH_REQ_CODE = 0;
    public static final int PERMISSION_DENIED_ERROR = 20;
    
    //Messages
    public static final String OPTION_FOLDER = "folder";
    public static final String OPTION_MSG_ID = "message_id";
    public static final String OPTION_THREAD_ID = "conversation_id";
    public static final String OPTION_ADDRESS = "from_mobile";
    public static final String OPTION_SERVICE_CENTER = "service_center";
    public static final String OPTION_DATE = "date";
    public static final String OPTION_DATE_SENT = "date_sent";
    public static final String OPTION_MAX_COUNT = "maxMsgCount";
    public static final String OPTION_RECEIVE_DATE = "receive_date";
    public static final String OPTION_RECEIVE_DATE_GREATER = "greater_receive_date";
    public static final String OPTION_RECEIVE_DATE_LESSER = "lesser_receive_date";
    
    //Conversations.
    public static final String OPTION_CONVERSATION_ID = "conversation_id";
    public static final String OPTION_MAX_CONVERSATION_COUNT = "maxConversationCount";
    
    public static final String SMS_URI = "content://sms/";
    public static final String SMS_INBOX = "inbox";
    public static final String SMS_SENT = "sent";
    
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
    
    //Constants for the Coumns.
    public static final String OWNER_NUMBER_CONST = "OWNER";
}
