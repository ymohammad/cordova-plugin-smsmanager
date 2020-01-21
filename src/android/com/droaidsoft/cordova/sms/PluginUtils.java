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
	public static JSONObject getJSONObjectFromCursorRecord(Cursor cur) {
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
	public static boolean isEmpty(JSONArray jsonArray) {
		return jsonArray == null || jsonArray.length() == 0;
	}
	
	/**
	 * Merge two Message JSON objects array and return the final array arranged in Ascending order.
	 * @param firstArray
	 * @param secondArray
	 * @return
	 * @throws JSONException
	 */
	public static JSONArray mergeByDateOrder(JSONArray firstArray, JSONArray secondArray) throws JSONException
	{
		JSONArray mergeArr = new JSONArray();
		if (isEmpty(firstArray) && isEmpty(secondArray)) {
			return mergeArr;
		}
		
		if (isEmpty(firstArray)) {
			return secondArray;
		}
		if (isEmpty(secondArray)) {
			return firstArray;
		}
		
		int finalIndex = 0;
		int firstIndex = 0;
		int secondIndex = 0;
		
		while (firstIndex < firstArray.length()  && secondIndex < secondArray.length()) {
			JSONObject firstJsonObj = (JSONObject)firstArray.get(firstIndex);
			JSONObject secondJsonObj = (JSONObject)secondArray.get(secondIndex);
			
			if (firstJsonObj.getLong(AppConstants.RESPONSE_DATE) < secondJsonObj.getLong(AppConstants.RESPONSE_DATE)) {
				mergeArr.put(finalIndex, firstJsonObj);
				firstIndex++;
			} else if (secondJsonObj.getLong(AppConstants.RESPONSE_DATE) < firstJsonObj.getLong(AppConstants.RESPONSE_DATE)) {
				mergeArr.put(finalIndex, secondJsonObj);
				secondIndex++;
			} else {
				mergeArr.put(finalIndex, firstJsonObj);
				mergeArr.put(finalIndex, secondJsonObj);
				firstIndex++;
				secondIndex++;
			}
			finalIndex++;
    	}
		while (firstIndex < firstArray.length()) {
			JSONObject firstJsonObj = (JSONObject)firstArray.get(firstIndex);
			mergeArr.put(finalIndex, firstJsonObj);
			firstIndex++;
			finalIndex++;
		}
		while (secondIndex < secondArray.length()) {
			JSONObject secondJsonObj = (JSONObject)secondArray.get(secondIndex);
			mergeArr.put(finalIndex, secondJsonObj);
			secondIndex++;
			finalIndex++;
		}
		return mergeArr;
	}
	
	public static JSONObject createJsonObj(String key, String value) throws JSONException {
    	JSONObject numObj = new JSONObject();
    	numObj.put(key, value);
    	return numObj;
    }
	/**
	 * Create a merge List ans return.
	 * @param leftArray
	 * @param rightArray
	 * @param compareKey
	 * @return
	 * @throws JSONException
	 */
	public static JSONArray getAppendedArray(JSONArray leftArray, JSONArray rightArray, String compareKey) throws JSONException
	{
		JSONArray finalArr = new JSONArray();
		HashSet hash = new HashSet();
		if (leftArray != null && leftArray.length() > 0)
		{
			for (int index = 0; index < leftArray.length(); index++)
			{
				Object obj = leftArray.get(index);
				if (obj != null && obj instanceof JSONObject)
				{
					JSONObject jsonObj = (JSONObject) obj;
					Object object = jsonObj.get(compareKey);
					if (!hash.contains(object))
					{
						finalArr.put(jsonObj);
						hash.add(object);
					}
				}
			}
		}
		if (rightArray != null && rightArray.length() > 0)
		{
			for (int index = 0; index < rightArray.length(); index++)
			{
				Object obj = rightArray.get(index);
				if (obj != null && obj instanceof JSONObject)
				{
					JSONObject jsonObj = (JSONObject) obj;
					Object object = jsonObj.get(compareKey);
					if (!hash.contains(object))
					{
						finalArr.put(jsonObj);
						hash.add(object);
					}
				}
			}
		}
		return finalArr;
	}
}
