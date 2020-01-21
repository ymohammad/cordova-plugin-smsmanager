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

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.provider.Telephony;
import android.util.Log;

import java.security.MessageDigest;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.droaidsoft.cordova.sms.constants.AppConstants;
import com.droaidsoft.cordova.sms.constants.Filter;
import com.droaidsoft.cordova.sms.exceptions.CursorJsonConverstionException;
import com.droaidsoft.cordova.sms.services.SmsReceiver;

/**
 * Main Plugin class.
 * @author ymohammad
 *
 */
public class SMSManager extends CordovaPlugin {
	
	private BroadcastReceiver mReceiver = null;

	/**
	 * Plugin Execution method.
	 */
	public boolean execute(String action, JSONArray inputs, CallbackContext callbackContext) throws JSONException {
		PluginResult result = null;
		
		if (action.equals(AppConstants.ACTION_START_WATCH)) {
			result = this.startWatch(callbackContext);
		} else if (action.equals(AppConstants.ACTION_STOP_WATCH)) {
			result = this.stopWatch(callbackContext);
		} else if (action.equals(AppConstants.ACTION_FETCH_SMS)) {
			JSONObject filters = inputs.optJSONObject(0);
            result = this.fetchSMS(filters, callbackContext);
		} else if (action.equals(AppConstants.ACTION_FETCH_CONVERSATION_INFO)) {
			JSONObject filters = inputs.optJSONObject(0);
            result = this.fetchConversationInfo(filters, callbackContext);
		} else if (action.equals(AppConstants.ACTION_FETCH_ALL_CONVERSATIONS_OF_A_NUMBER)) {
			JSONObject filters = inputs.optJSONObject(0);
            result = this.fetchAllConversationOfANumber(filters, callbackContext);
		} else if (action.equals(AppConstants.ACTION_FETCH_ALL_MESSAGES_OF_A_CONVERSATION)) {
			JSONObject filters = inputs.optJSONObject(0);
            result = this.fetchAllMessagesOfAConversation(filters, callbackContext);
		} else if (action.equals(AppConstants.ACTION_FETCH_ALL_USERS_OF_A_CONVERSATION)) {
			JSONObject filters = inputs.optJSONObject(0);
            result = this.fetchAllUsersOfAConversation(filters, callbackContext);
		} else {
			Log.d(AppConstants.LOG_TAG, String.format("Invalid action passed: %s", action));
			result = new PluginResult(PluginResult.Status.INVALID_ACTION);
		}
		if (result != null) {
			callbackContext.sendPluginResult(result);
		}
		return true;
	}

	/**
	 * Handler Method to perform Stop Watch.
	 */
	public void onDestroy() {
		this.stopWatch(null);
	}
	/**
	 * Plugin method to fetch Conversations.
	 * @param options
	 * @param callbackContext
	 * @return
	 */
	private PluginResult fetchConversationInfo(JSONObject options, CallbackContext callbackContext) {
		Log.i(AppConstants.LOG_TAG, "Starting the SMS Fetch operation." + options);
		if (options == null) {
			callbackContext.error("Options object is required to invoke this API Method.");
			return null;
		}
		Filter filter = new Filter(options);
        Activity ctx = this.cordova.getActivity();
        JSONArray jsonArray;
		try
		{
			jsonArray = SMSUtils.fetchConversationInfo(ctx, filter);
			callbackContext.success(jsonArray);
		} catch (Throwable e)
		{
			e.printStackTrace();
			callbackContext.error(e.getMessage());
		}
        return null;
	}
	/**
	 * Plugin Method to fetch All Messages of a conversation.
	 * @param options
	 * @param callbackContext
	 * @return
	 */
	private PluginResult fetchAllMessagesOfAConversation(JSONObject options, CallbackContext callbackContext)
	{
		if (options == null) {
			callbackContext.error("Options object is required to invoke this API Method.");
			return null;
		}
		Filter filter = new Filter(options);
        Activity ctx = this.cordova.getActivity();
        JSONArray jsonArray;
		try
		{
			jsonArray = SMSUtils.fetchAllMessagesOfConversation(ctx, filter);
			callbackContext.success(jsonArray);
		} catch (Throwable e)
		{
			e.printStackTrace();
			callbackContext.error(e.getMessage());
		}
        return null;
	}
	/**
	 * Plugin Method to fetch All Messages of a conversation.
	 * @param options
	 * @param callbackContext
	 * @return
	 */
	private PluginResult fetchAllUsersOfAConversation(JSONObject options, CallbackContext callbackContext)
	{
		if (options == null) {
			callbackContext.error("Options object is required to invoke this API Method.");
			return null;
		}
		Filter filter = new Filter(options);
        Activity ctx = this.cordova.getActivity();
        JSONArray jsonArray;
		try
		{
			jsonArray = SMSUtils.fetchAllUsersOfAConversation(ctx, filter);
			callbackContext.success(jsonArray);
		} catch (Throwable e)
		{
			e.printStackTrace();
			callbackContext.error(e.getMessage());
		}
        return null;
	}

	/**
	 * Plugin Method to get All the converstation of a number.
	 * @param options
	 * @param callbackContext
	 * @return
	 */
	private PluginResult fetchAllConversationOfANumber(JSONObject options, CallbackContext callbackContext)
	{
		if (options == null) {
			callbackContext.error("Options object is required to invoke this API Method.");
			return null;
		}
		Filter filter = new Filter(options);
        Activity ctx = this.cordova.getActivity();
        JSONArray jsonArray;
		try
		{
			jsonArray = SMSUtils.fetchUniqueConversations(ctx, filter);
			callbackContext.success(jsonArray);
		} catch (Throwable e)
		{
			e.printStackTrace();
			callbackContext.error(e.getMessage());
		}
        return null;
	}
	/**
	 * Plugin method to fetch SMS.
	 * @param options
	 * @param callbackContext
	 * @return
	 */
	private PluginResult fetchSMS(JSONObject options, CallbackContext callbackContext)
	{
		Log.i(AppConstants.LOG_TAG, "Starting the SMS Fetch operation." + options);
        
		Filter filter = new Filter(options);
        Activity ctx = this.cordova.getActivity();
        JSONArray jsonArray;
		try
		{
			jsonArray = SMSUtils.fetchSmsMessages(ctx, filter);
			callbackContext.success(jsonArray);
		} catch (CursorJsonConverstionException e)
		{
			e.printStackTrace();
			callbackContext.error(e.getMessage());
		}
        return null;
	}
	/**
	 * Plugin method to perform Start Watch.
	 * @param callbackContext
	 * @return
	 */
	private PluginResult startWatch(CallbackContext callbackContext) {
		Log.d(AppConstants.LOG_TAG, AppConstants.ACTION_START_WATCH);
		if (this.mReceiver == null) {
			this.createIncomingSMSReceiver();
		}
		if (callbackContext != null) {
			callbackContext.success();
		}
		return null;
	}

	/**
	 * Plugin method to perform Stop Watch.
	 * @param callbackContext
	 * @return
	 */
	private PluginResult stopWatch(CallbackContext callbackContext) {
		Log.d(AppConstants.LOG_TAG, AppConstants.ACTION_STOP_WATCH);
		if (this.mReceiver != null) {
			try {
				webView.getContext().unregisterReceiver(this.mReceiver);
			} catch (Exception e) {
				Log.d(AppConstants.LOG_TAG, "error unregistering network receiver: " + e.getMessage());
			} finally {
				this.mReceiver = null;
			}
		}
		if (callbackContext != null) {
			callbackContext.success();
		}
		return null;
	}

	/**
	 * To create Incoming SMS Receiver.
	 */
	protected void createIncomingSMSReceiver() {
		this.mReceiver = new SmsReceiver(webView);
		IntentFilter filter = new IntentFilter(AppConstants.ACTION_SMS_RECEIVED);
		try {
			webView.getContext().registerReceiver(this.mReceiver, filter);
		} catch (Exception e) {
			Log.d(AppConstants.LOG_TAG, "error registering broadcast receiver: " + e.getMessage());
		}
	}
}