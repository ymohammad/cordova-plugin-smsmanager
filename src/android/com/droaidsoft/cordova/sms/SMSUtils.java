package com.droaidsoft.cordova.sms;

import org.json.JSONArray;
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
//import com.google.android.mms.pdu.CharacterSets;
import android.telephony.PhoneNumberUtils;

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

    public static JSONArray fetchSmsMessages(Context context, Filter filter) throws CursorJsonConverstionException {
    	JSONArray returnArr = new JSONArray();
    	Uri uri = getUri(filter.folderLocation);
        Cursor cur = context.getContentResolver().query(uri, (String[])null, "", (String[])null, null);
        int currIndex = 0;
        try {
        	while (cur.moveToNext()) {
                JSONObject json;
                
                if (isFilteredMessage(cur, filter, currIndex)) {
                	json = PluginUtils.getMessageJsonObject(cur);
                	if (json == null) {
                		throw new CursorJsonConverstionException("Unable to get JSON object using the cursor.");
                	}
                	returnArr.put(json);
                	currIndex++;
                }
            }
        } finally {
        	if (cur != null) {
        		cur.close();
        	}
        }
        return returnArr;
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




	/*
	public static List<Conversation> getSmsConversationList(Context context) {
        List<Conversation> returnList = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(Telephony.Sms.Conversations.CONTENT_URI, null, null, null, Telephony.Sms.Conversations.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            String[] allColumns = cursor.getColumnNames();
            int totalSMS = cursor.getCount();
            Log.d(TAG, "Total SMS Count in Inbox :" + totalSMS);
            if (cursor.moveToFirst()) {
                for (int j = 0; j < totalSMS; j++) {
                    Conversation eachConversationObj = new Conversation();
                    //String sender = cursor.getString(cursor.getColumnIndexOrThrow(Sms.Conversations.ADDRESS));
                    //Log.d(TAG, "From sender :" + sender);
                    eachConversationObj.setMessageCount(cursor.getInt(cursor.getColumnIndexOrThrow(Sms.Conversations.MESSAGE_COUNT)));
                    eachConversationObj.setSnippet(cursor.getString(cursor.getColumnIndexOrThrow(Sms.Conversations.SNIPPET)));
                    eachConversationObj.setThreadId(cursor.getLong(cursor.getColumnIndexOrThrow(Sms.Conversations.THREAD_ID)));
                    returnList.add(eachConversationObj);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } else {
            Log.d(TAG,"No message to show!");
        }
        return returnList;
    }
    public static void printMessageDetails(Context context, String threadId) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = null;
        String msgData = "";
        List<MessageModel> returnList = new ArrayList<>();
        try {
            String selection = Sms.Inbox.THREAD_ID + " = " + threadId;



            cursor = cr.query(Telephony.Sms.Inbox.CONTENT_URI, null, selection,
                    null, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
            int totalSMS = 0;
            long currentTime = System.currentTimeMillis();
            int count = 0;
            boolean countHoursAgo = true;
            if (cursor != null) {
                totalSMS = cursor.getCount();

                Log.d(TAG, "Total SMS Count in Thread id" + threadId + " is " + totalSMS);
                if (cursor.moveToFirst()) {
                    for (int j = 0; j < totalSMS; j++) {
                        MessageModel eachModel = new MessageModel();
                        eachModel.setBelongsToCurrentUser(false);
                        eachModel.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(Sms.Inbox.BODY)));
                        eachModel.setReceivedDate(cursor.getString(cursor.getColumnIndexOrThrow(Sms.Inbox.DATE)));
                        eachModel.setSender(cursor.getString(cursor.getColumnIndexOrThrow(Sms.Inbox.ADDRESS)));


                        returnList.add(eachModel);

                        for(int idx=0;idx<cursor.getColumnCount();idx++)
                        {
                            msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                        }
                        cursor.moveToNext();
                    }

                }
            } else {
                Log.d(TAG,  "No messages available for this thread id " + threadId);
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error raised ", ex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
       Log.d(TAG, "Got complete Messages list :" + returnList);
        Log.d(TAG, "Msg in all columns :" + msgData);
    }
    public static List<MessageModel> getMessagesFromInboxForThread(Context context, long threadId) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = null;
        List<MessageModel> returnList = new ArrayList<>();
        try {
            String selection = Sms.Inbox.THREAD_ID + " = " + threadId;

            cursor = cr.query(Telephony.Sms.Inbox.CONTENT_URI, null, selection,
                    null, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
            int totalSMS = 0;
            long currentTime = System.currentTimeMillis();
            int count = 0;
            boolean countHoursAgo = true;
            if (cursor != null) {
                totalSMS = cursor.getCount();

                Log.d(TAG, "Total SMS Count in Thread id" + threadId + " is " + totalSMS);
                if (cursor.moveToFirst()) {
                    for (int j = 0; j < totalSMS; j++) {
                        MessageModel eachModel = new MessageModel();
                        eachModel.setBelongsToCurrentUser(false);
                        eachModel.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(Sms.Inbox.BODY)));
                        eachModel.setReceivedDate(cursor.getString(cursor.getColumnIndexOrThrow(Sms.Inbox.DATE)));
                        eachModel.setSender(cursor.getString(cursor.getColumnIndexOrThrow(Sms.Inbox.ADDRESS)));

                        cursor.moveToNext();
                        returnList.add(eachModel);
                    }

                }
            } else {
                Log.d(TAG,  "No messages available for this thread id " + threadId);
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error raised ", ex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return returnList;
    }
    public static void printConversations(Context context) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(Telephony.Sms.Conversations.CONTENT_URI, null, null, null, Telephony.Sms.Conversations.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            String[] allColumns = cursor.getColumnNames();
            int totalSMS = cursor.getCount();
            Log.d(TAG, "Total SMS Count in Inbox :" + totalSMS);
            if (cursor.moveToFirst()) {
                for (int j = 0; j < totalSMS; j++) {
                    for (String eachColumn : allColumns) {
                        Log.d(TAG, eachColumn + " : " + cursor.getString(cursor.getColumnIndexOrThrow(eachColumn)));
                    }
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } else {
            Toast.makeText(context, "No message to show!", Toast.LENGTH_SHORT).show();
        }
    }

    public static void printInboxMessages(Context context) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = cr.query(Telephony.Sms.Inbox.CONTENT_URI, null, null,
                    null, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
            int totalSMS = 0;
            long currentTime = System.currentTimeMillis();
            int count = 0;
            boolean countHoursAgo = true;
            if (cursor != null) {
                totalSMS = cursor.getCount();

                Log.d(TAG, "Total SMS Count in Inbox :" + totalSMS);
                if (cursor.moveToFirst()) {
                    for (int j = 0; j < totalSMS; j++) {

                        //Log.d(TAG, "Received Date :" + cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.DATE)));
                       // Log.d(TAG, "Address :" + cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.ADDRESS)));
                        //Log.d(TAG, "Message body :" + cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.BODY));
                       // Log.d(TAG, "Is Read :" + cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.READ)));
                        String[] allColumns = cursor.getColumnNames();
                        for (String eachColumn : allColumns) {
                            Log.d(TAG, eachColumn + " : " + cursor.getString(cursor.getColumnIndexOrThrow(eachColumn)));
                        }
                        cursor.moveToNext();
                        //Log.d(TAG, "*******************************************************\n\n");
                    }

                }
            } else {
                Toast.makeText(context, "No message to show!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error raised ", ex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void dumpSmsTable(Context context) {
        Log.d(TAG, "**** Dump of sms table ****");
        Cursor c = context.getContentResolver().query(Sms.CONTENT_URI,
                SMS_PROJECTION, null, null, "_id DESC");
        try {
            // Only dump the latest 20 messages
            c.moveToPosition(-1);
            while (c.moveToNext() && c.getPosition() < 20) {
                String body = c.getString(COLUMN_SMS_BODY);
                Log.d(TAG, "dumpSmsTable " + BaseColumns._ID + ": " + c.getLong(COLUMN_ID) +
                        " " + Sms.THREAD_ID + " : " + c.getLong(DATE) +
                        " " + Sms.ADDRESS + " : " + c.getString(COLUMN_SMS_ADDRESS) +
                        " " + Sms.BODY + " : " + body.substring(0, Math.min(body.length(), 8)) +
                        " " + Sms.DATE + " : " + c.getLong(COLUMN_SMS_DATE) +
                        " " + Sms.TYPE + " : " + c.getInt(COLUMN_SMS_TYPE));
            }
        } finally {
            c.close();
        }
    }

    public static void dumpThreadsTable(Context context) {
        Log.d(TAG,"**** Dump of threads table ****");
        Cursor c = context.getContentResolver().query(sAllThreadsUri,
                ALL_THREADS_PROJECTION, null, null, "date ASC");
        try {
            c.moveToPosition(-1);
            while (c.moveToNext()) {
               // String snippet = extractEncStrFromCursor(c, SNIPPET, SNIPPET_CS);
                Log.d(TAG, "dumpThreadsTable threadId: " + c.getLong(ID) +
                        " " + ThreadsColumns.DATE + " : " + c.getLong(DATE) +
                        " " + ThreadsColumns.MESSAGE_COUNT + " : " + c.getInt(MESSAGE_COUNT) +
                        //" " + ThreadsColumns.SNIPPET + " : " + c.get(SNIPPET) +
                        //" " + ThreadsColumns.SNIPPET_CHARSET + " : " + c.getString(SNIPPET_CS) +
                        " " + ThreadsColumns.READ + " : " + c.getInt(READ) +
                        " " + ThreadsColumns.ERROR + " : " + c.getInt(ERROR) +
                        " " + ThreadsColumns.HAS_ATTACHMENT + " : " + c.getInt(HAS_ATTACHMENT) +
                        " " + ThreadsColumns.RECIPIENT_IDS + " : " + c.getString(RECIPIENT_IDS));

                //ContactList recipients = ContactList.getByIds(c.getString(RECIPIENT_IDS), false);
                //Log.d(TAG, "----recipients: " + recipients.serialize());
            }
        } finally {
            c.close();
        }
    }
    */
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
    /*public static ContactList getByIds(String spaceSepIds, boolean canBlock) {
        ContactList list = new ContactList();
        for (RecipientIdCache.Entry entry : RecipientIdCache.getAddresses(spaceSepIds)) {
            if (entry != null && !TextUtils.isEmpty(entry.number)) {
                Contact contact = Contact.get(entry.number, canBlock);
                contact.setRecipientId(entry.id);
                list.add(contact);
            }
        }
        return list;
    }
    public static String extractEncStrFromCursor(Cursor cursor,
                                                 int columnRawBytes, int columnCharset) {
        String rawBytes = cursor.getString(columnRawBytes);
        int charset = cursor.getInt(columnCharset);

        if (TextUtils.isEmpty(rawBytes)) {
            return "";
        } else if (charset == CharacterSets.ANY_CHARSET) {
            return rawBytes;
        } else {
            return new EncodedStringValue(charset, PduPersister.getBytes(rawBytes)).getString();
        }
    }*/
}
