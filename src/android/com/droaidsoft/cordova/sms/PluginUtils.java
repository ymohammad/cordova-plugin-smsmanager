package com.droaidsoft.cordova.sms;

import org.json.JSONArray;
import org.json.JSONObject;

import android.database.Cursor;
import android.telephony.SmsMessage;
import android.util.Log;

public class PluginUtils
{
	public static final String TAG = PluginUtils.class.getSimpleName();
	
	/**
	 * Gets json array object.
	 * @param smsArray
	 * @return
	 */
	public static JSONArray getJsonFromSmsMessageArray(SmsMessage[] smsArray) {
		JSONArray jsonArray = new JSONArray();
		try {
			if (smsArray != null) {
				for (SmsMessage eachMsg : smsArray) {
					JSONObject jsonFromSmsMessage = getJsonFromSmsMessage(eachMsg);
					jsonArray.put(jsonFromSmsMessage);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, e.getMessage());
		}
		return jsonArray;
	}
	/**
	 * Convert a SMS object to JSON object.
	 * @param sms
	 * @return
	 */
	public static JSONObject getJsonFromSmsMessage(SmsMessage sms) {
		JSONObject json = new JSONObject();
		try {
			json.put( "address", sms.getOriginatingAddress() );
			json.put( "body", sms.getMessageBody() ); // May need sms.getMessageBody.toString()
			json.put( "date_sent", sms.getTimestampMillis() );
			json.put( "date", System.currentTimeMillis() );
			json.put( "service_center", sms.getServiceCenterAddress());
		}
		catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
		return json;
	}
	/**
	 * From the cursor, prepare a JSON object.
	 * @param cur
	 * @return
	 */
	public static JSONObject getMessageJsonObject(Cursor cur) {
		JSONObject json = new JSONObject();
		
		int nCol = cur.getColumnCount();
		String keys[] = cur.getColumnNames();

		try {
			for(int j=0; j<nCol; j++) {
				switch(cur.getType(j)) {
				case Cursor.FIELD_TYPE_NULL:
					json.put(keys[j], null);
					break;
				case Cursor.FIELD_TYPE_INTEGER:
					json.put(keys[j], cur.getLong(j));
					break;
				case Cursor.FIELD_TYPE_FLOAT:
					json.put(keys[j], cur.getFloat(j));
					break;
				case Cursor.FIELD_TYPE_STRING:
					json.put(keys[j], cur.getString(j));
					break;
				case Cursor.FIELD_TYPE_BLOB:
					json.put(keys[j], cur.getBlob(j));
					break;
				}
			}
		} catch (Exception e) {
			return null;
		}

		return json;
    }
}
