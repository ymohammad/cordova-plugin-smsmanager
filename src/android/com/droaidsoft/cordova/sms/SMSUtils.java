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
package com.droaidsoft.cordova.sms;

import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.droaidsoft.cordova.sms.constants.AppConstants;
import com.droaidsoft.cordova.sms.constants.Filter;
import com.droaidsoft.cordova.sms.exceptions.CursorJsonConverstionException;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.Telephony;
import android.provider.Telephony.Sms;
import android.provider.Telephony.Threads;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

/**
 * Business Logic to fetch SMS data.
 * @author ymohammad
 *
 */
public class SMSUtils {
    public static final String TAG = SMSUtils.class.getSimpleName();
    public static final Uri sAllThreadsUri =
            Threads.CONTENT_URI.buildUpon().appendQueryParameter("simple", "true").build();

    static final String[] SMS_PROJECTION = new String[] {
            BaseColumns._ID,
            // For SMS
            Sms.THREAD_ID,
            Sms.ADDRESS,
            Sms.BODY,
            Sms.DATE,
            Sms.READ,
            Sms.TYPE,
            Sms.STATUS,
            Sms.LOCKED,
            Sms.ERROR_CODE,
    };
    public static final String[] ALL_THREADS_PROJECTION = {
            Threads._ID, Threads.DATE, Threads.MESSAGE_COUNT, Threads.RECIPIENT_IDS,
            Threads.SNIPPET, Threads.SNIPPET_CHARSET, Threads.READ, Threads.ERROR,
            Threads.HAS_ATTACHMENT
    };

    public static final String[] CONVERSATION_PROJECTION = {
            Sms.Conversations.THREAD_ID,
            Sms.Conversations.SNIPPET, Sms.Conversations.MESSAGE_COUNT
    };
    // The indexes of the default columns which must be consistent
    // with above PROJECTION.
    static final int COLUMN_ID                  = 0;
    static final int COLUMN_THREAD_ID           = 1;
    static final int COLUMN_SMS_ADDRESS         = 2;
    static final int COLUMN_SMS_BODY            = 3;
    static final int COLUMN_SMS_DATE            = 4;
    static final int COLUMN_SMS_READ            = 5;
    static final int COLUMN_SMS_TYPE            = 6;
    static final int COLUMN_SMS_STATUS          = 7;
    static final int COLUMN_SMS_LOCKED          = 8;
    static final int COLUMN_SMS_ERROR_CODE      = 9;

    private static final int ID             = 0;
    private static final int DATE           = 1;
    private static final int MESSAGE_COUNT  = 2;
    private static final int RECIPIENT_IDS  = 3;
    private static final int SNIPPET        = 4;
    private static final int SNIPPET_CS     = 5;
    private static final int READ           = 6;
    private static final int ERROR          = 7;
    private static final int HAS_ATTACHMENT = 8;

    /**
     * Fetch Conversations and return to the user.
     * @param context
     * @param filter
     * @return
     * @throws CursorJsonConverstionException
     */
    public static JSONArray fetchConversationInfo(Context context, Filter filter) throws CursorJsonConverstionException {
    	if (filter.conversationId == -1) {
            throw new CursorJsonConverstionException("Conversation ID is required to get Conversation information.");
        }
    	
    	JSONArray returnArr = new JSONArray();
    	Uri uri = Telephony.Sms.Conversations.CONTENT_URI;
    	String selection = getConversationSelection(filter);
    	String sortOrder = " date DESC ";
    	if (filter.maxConversationCount != -1) {
            sortOrder += " LIMIT " + filter.maxConversationCount;
        }
        Cursor cur = context.getContentResolver().query(uri, CONVERSATION_PROJECTION, selection, (String[])null, sortOrder);
        int currIndex = 0;
        try {
        	while (cur.moveToNext()) {
                JSONObject json = PluginUtils.getJSONObjectFromCursorRecord(cur);
            	if (json == null) {
            		throw new CursorJsonConverstionException("Unable to get JSON object using the cursor.");
            	}
            	returnArr.put(json);
            	currIndex++;
            	//Backward compatibility if LIMIT is not implemented.
            	if (filter.maxConversationCount != -1 && currIndex >= filter.maxConversationCount) {
            	    break;
                }
            }
        } finally {
        	if (cur != null) {
        		cur.close();
        	}
        }
        return returnArr;
    }
    
    /**
     * Gets the list of conversations ids this address is involved.
     * @param context
     * @param fromAddress
     * @return
     * @throws CursorJsonConverstionException 
     * @throws JSONException 
     */
    public static JSONArray fetchUniqueConversations(Context context, Filter filter) throws CursorJsonConverstionException, JSONException {
    	if (filter.fromAddress == null || filter.fromAddress.trim().length() == 0) {
    		throw new CursorJsonConverstionException("Mobile number is required to get unique conversation ids.");
    	}
    	
    	Uri inboxUri = getUri(AppConstants.SMS_INBOX);
    	String selectionStr = getMessagesSelection(filter);
    	String[] projectStrArr = new String[]{"DISTINCT thread_id"};    	
    	JSONArray inboxArray = executeNGetData(context, inboxUri, selectionStr, projectStrArr);
    	JSONArray sentBoxArray = executeNGetData(context, getUri(AppConstants.SMS_SENT), selectionStr, projectStrArr);
    	
    	JSONArray finalArr = new JSONArray();
    	HashSet hash = new HashSet();
    	if (inboxArray != null && inboxArray.length() > 0) {
    		for (int index = 0; index< inboxArray.length(); index++) {
    			Object obj = inboxArray.get(index);
    			if (obj != null && obj instanceof JSONObject) {
    				JSONObject jsonObj = (JSONObject)obj;
    				Object object = jsonObj.get("thread_id");
    				if (!hash.contains(object)){
    					finalArr.put(jsonObj);
    					hash.add(object);
    				}
    			}
    		}
    	}
    	if (sentBoxArray != null && sentBoxArray.length() > 0) {
    		for (int index = 0; index< sentBoxArray.length(); index++) {
    			Object obj = sentBoxArray.get(index);
    			if (obj != null && obj instanceof JSONObject) {
    				JSONObject jsonObj = (JSONObject)obj;
    				Object object = jsonObj.get("thread_id");
    				if (!hash.contains(object)){
    					finalArr.put(jsonObj);
    					hash.add(object);
    				}
    			}
    		}
    	}
       return finalArr; 
    }
    
    /**
     * API Method to get all the messages of a conversation. It fethes messages from Inbox and SentBox.
     * @param context
     * @param filter
     * @return
     * @throws CursorJsonConverstionException
     * @throws JSONException 
     */
    public static JSONArray fetchAllMessagesOfConversation(Context context, Filter filter) throws CursorJsonConverstionException, JSONException {
    	if (filter.threadId == -1) {
    		throw new CursorJsonConverstionException("Conversation id (thread_id) is required to get all the messages.");
    	}
    	filter.folderLocation = AppConstants.SMS_INBOX;
    	JSONArray inboxMsgs = fetchSmsMessages(context, filter);
    	
    	filter.folderLocation = AppConstants.SMS_SENT;
    	JSONArray sentBoxMsgs = fetchSmsMessages(context, filter);
    	//Update the addess to OWNER for the messages sent by the device Owner.
    	if (sentBoxMsgs != null && sentBoxMsgs.length() > 0) {
    		for(int ind = 0; ind < sentBoxMsgs.length(); ind++) {
    			JSONObject eachObj = (sentBoxMsgs.get(ind) instanceof JSONObject) ? (JSONObject)sentBoxMsgs.get(ind) : null;
    			if (eachObj != null) {
    				eachObj.put(Telephony.Sms.Sent.ADDRESS, AppConstants.OWNER_NUMBER_CONST);
    			}
    		}
    	}
    	JSONArray finalArr = PluginUtils.mergeByDateOrder(inboxMsgs, sentBoxMsgs);
    	return finalArr;
    }
    /**
     * API Method to get all the Users of a conversation other than the owner.
     * @param context
     * @param filter
     * @return
     * @throws CursorJsonConverstionException
     * @throws JSONException 
     */
    public static JSONArray fetchAllUsersOfAConversation(Context context, Filter filter) throws CursorJsonConverstionException, JSONException {
    	if (filter.threadId == -1) {
    		throw new CursorJsonConverstionException("Conversation id (thread_id) is required to get all the contacts.");
    	}
    	filter.folderLocation = AppConstants.SMS_INBOX;
    	Uri inboxUri = getUri(AppConstants.SMS_INBOX);
    	String addressKey = "address";
    	
    	String selectionStr = getMessagesSelection(filter);
    	String[] projectStrArr = new String[]{"DISTINCT " + addressKey , "person"};    	
    	JSONArray inboxArray = executeNGetData(context, inboxUri, selectionStr, projectStrArr);
    	
    	JSONArray returnArray = new JSONArray();
    	JSONArray sentBoxArray = executeNGetData(context, getUri(AppConstants.SMS_SENT), selectionStr, projectStrArr);
    	JSONArray mergedArr = PluginUtils.getAppendedArray(inboxArray, sentBoxArray, addressKey);

    	//Get unique phone numbers. In Sent Messages, address value can be "8977309663 +917981647273"
    	if (mergedArr != null && mergedArr.length() > 0) {
    		HashSet<String> hash = new HashSet<String>();
    		for (int index = 0; index < mergedArr.length(); index++) {
				Object obj = mergedArr.get(index);
				if (obj != null && obj instanceof JSONObject) {
					JSONObject jsonObj = (JSONObject) obj;
					String addressNumb = (String)jsonObj.get(addressKey);
					if (addressNumb != null && addressNumb.trim().length() > 0) {
						addressNumb = addressNumb.trim();
						
						if (addressNumb.indexOf(" ") != -1) {
							String[] numbsArr = addressNumb.split(" ");
							for (String eachNum: numbsArr) {
								if (!hash.contains(eachNum.trim())) {
									returnArray.put(PluginUtils.createJsonObj(addressKey, eachNum.trim()));
									hash.add(eachNum.trim());
								}
							}
						} else {
							if (!hash.contains(addressNumb)) {
								returnArray.put(PluginUtils.createJsonObj(addressKey, addressNumb));
								hash.add(addressNumb);
							}
						}
					}
				}
			}
    	}
    	return returnArray;
    }
    
	/**
     * Fetch SMS from Provider based on the filter.
     * @param context
     * @param filter
     * @return
     * @throws CursorJsonConverstionException
	 * @throws JSONException 
     */
    public static JSONArray fetchSmsMessages(Context context, Filter filter) throws CursorJsonConverstionException {
    	JSONArray returnArr = new JSONArray();
    	Uri uri = getUri(filter.folderLocation);
    	String selectionStr = getMessagesSelection(filter);
    	String sortOrder = " date ASC ";
    	if (filter.maxMessageCount != -1) {
            sortOrder += " LIMIT " + filter.maxMessageCount;
        }
    	
        Cursor cur = context.getContentResolver().query(uri, (String[])null, selectionStr, (String[])null, sortOrder);
        int currIndex = 0;
        try {
        	while (cur.moveToNext()) {
                JSONObject json = PluginUtils.getJSONObjectFromCursorRecord(cur);
            	if (json == null) {
            		throw new CursorJsonConverstionException("Unable to get JSON object using the cursor.");
            	}
            	
            	returnArr.put(json);
            	currIndex++;
            	
            	//Backward compatibility if Limit is not considered/implemented.
            	if (filter.maxMessageCount != -1 && currIndex >= filter.maxMessageCount) {
            	    break;
                }
            }
        } finally {
        	if (cur != null) {
        		cur.close();
        	}
        }
        return returnArr;
    }
    /**
     * To fetch all threads from Threads Content Provider.
     * 
     * @param context
     * @return
     * @throws CursorJsonConverstionException
     * @throws JSONException
     */
    public static JSONArray fetchThreads(Context context) throws CursorJsonConverstionException, JSONException {
        JSONArray returnArr = new JSONArray();
        Log.d(TAG,"**** Dump of threads table ****");
        Cursor cur = context.getContentResolver().query(sAllThreadsUri,
                null, null, null, " date DESC  LIMIT 3 ");
        int currIndex = 0;
        JSONObject tempObj = new JSONObject();
        tempObj.put(AppConstants.OPTION_MAX_CONVERSATION_COUNT, 10);
        Filter filter = new Filter(tempObj);
        try {
            while (cur.moveToNext()) {
                JSONObject json;
                json = PluginUtils.getJSONObjectFromCursorRecord(cur);
                if (json == null) {
                    throw new CursorJsonConverstionException("Unable to get JSON object using the cursor.");
                }
                returnArr.put(json);
                currIndex++;
            }
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
        return returnArr;
    }
    
    // All the Private Methods..
    /**
     * Wrapper method to execute the URI based on the given selection and projection.
     * @param context
     * @param uri
     * @param selectionStr
     * @param projectionStrArr
     * @return
     * @throws CursorJsonConverstionException
     */
    private static JSONArray executeNGetData(Context context, Uri uri, String selectionStr, String[] projectionStrArr) throws CursorJsonConverstionException {
    	JSONArray returnArr = new JSONArray();
        Cursor cur = context.getContentResolver().query(uri, projectionStrArr, selectionStr, (String[])null, "");
        try {
        	while (cur.moveToNext()) {
                JSONObject json = PluginUtils.getJSONObjectFromCursorRecord(cur);
            	if (json == null) {
            		throw new CursorJsonConverstionException("Unable to get JSON object using the cursor.");
            	}
            	returnArr.put(json);
            }
        } finally {
        	if (cur != null) {
        		cur.close();
        	}
        }
        return returnArr;
    }
    private static boolean isFilteredConversation(Cursor cur, Filter filter, int currIndex)
	{
    	boolean matchFilter = false;
        if (filter.conversationId > -1) {
            matchFilter = (filter.messageId == cur.getInt(cur.getColumnIndex("_id")));
        } else if (filter.maxConversationCount != -1 && currIndex < filter.maxConversationCount){
            matchFilter = true;
        }
        
		return matchFilter;
	}
    private static boolean isFilteredMessage(Cursor cur, Filter filter, int currIndex)
	{
    	boolean matchFilter = false;
        if (filter.messageId > -1) {
            matchFilter = (filter.messageId == cur.getInt(cur.getColumnIndex("_id")));
        } else if (filter.fromAddress.length() > 0) {
            matchFilter = PhoneNumberUtils.compare(filter.fromAddress, cur.getString(cur.getColumnIndex(AppConstants.OPTION_ADDRESS)).trim());
        } else if (filter.maxMessageCount != -1 && currIndex < filter.maxMessageCount){
            matchFilter = true;
        }
        
		return matchFilter;
	}

    /**
     * Prepare and get Content URI of the SMS.
     * @param folderLocation
     * @return
     */
    private static Uri getUri(String folderLocation)
	{
    	Uri returnUri = null;
    	folderLocation = folderLocation.toLowerCase();
    	switch(folderLocation) {
	    	case "inbox":
	    		returnUri = Telephony.Sms.Inbox.CONTENT_URI;
	    		break;
	    	case "sent":
	    		returnUri = Telephony.Sms.Sent.CONTENT_URI;
	    		break;
	    	case "draft":
	    		returnUri = Telephony.Sms.Draft.CONTENT_URI;
	    		break;
	    	case "outbox":
	    		returnUri = Telephony.Sms.Outbox.CONTENT_URI;
	    		break;
	    	default:
	    		returnUri = Uri.parse(AppConstants.SMS_URI + folderLocation);
	    		break;
    	}
		return returnUri;
	}
    /**
     * Prepares Selection string based on the filter option.
     * @param filter
     * @return
     */
    private static String getMessagesSelection(Filter filter) {
    	StringBuffer selection = new StringBuffer();//Sms.Inbox.THREAD_ID + " = " + threadId
    	boolean isAndAppendRequired = false;
    	//From Address..
        if (filter.fromAddress != null && filter.fromAddress.trim().length() > 0) {
            selection.append(Sms.Inbox.ADDRESS + " LIKE '%" + filter.fromAddress.trim() + "'");
            isAndAppendRequired = true;
        }
        
        //Message ID..
        if (filter.messageId > -1) {
        	if (isAndAppendRequired) {
        		selection.append(" AND ");
        	}
            selection.append(Sms.Inbox._ID + " = " + filter.messageId);
            isAndAppendRequired = true;
        }
        
        //Thread ID...
        if (filter.threadId > -1) {
        	if (isAndAppendRequired) {
        		selection.append(" AND ");
        	}
            selection.append(Sms.Inbox.THREAD_ID + " = " + filter.threadId);
            isAndAppendRequired = true;
        }
        
        //Conversation id..
        if (filter.conversationId > -1) {
        	if (isAndAppendRequired) {
        		selection.append(" AND ");
        	}
            selection.append(Sms.Inbox.THREAD_ID + " = " + filter.conversationId);
            isAndAppendRequired = true;
        }
        
        //Dates..
        if (filter.recievedDateLong > -1) {
        	if (isAndAppendRequired) {
        		selection.append(" AND ");
        	}
            selection.append(Sms.Inbox.DATE + " = " + filter.recievedDateLong);
            isAndAppendRequired = true;
        } else if (filter.greaterRecievedDateLong > -1) {
        	if (isAndAppendRequired) {
        		selection.append(" AND ");
        	}
            selection.append(Sms.Inbox.DATE + " > " + filter.greaterRecievedDateLong);
            isAndAppendRequired = true;
        } else if (filter.lesserRecievedDateLong > -1) {
        	if (isAndAppendRequired) {
        		selection.append(" AND ");
        	}
            selection.append(Sms.Inbox.DATE + " < " + filter.lesserRecievedDateLong);
            isAndAppendRequired = true;
        }
        return selection.toString();
    }
    /**
     * Prepares selection string for the Conversation.
     * @param filter
     * @return
     */
    private static String getConversationSelection(Filter filter) {
        StringBuffer selection = new StringBuffer();//Sms.Inbox.THREAD_ID + " = " + threadId
        if (filter.conversationId > -1) {
            selection.append(Sms.Inbox.THREAD_ID + " = " + filter.conversationId);
        }
        return selection.toString();
    }
    
   
}
