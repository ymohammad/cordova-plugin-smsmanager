/**
 * 
 */
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
	private JSONArray requestArgs;
	private CallbackContext callbackContext;

	public boolean execute(String action, JSONArray inputs, CallbackContext callbackContext) throws JSONException {
		PluginResult result = null;
		this.callbackContext = callbackContext;
		this.requestArgs = inputs;
		if (action.equals(AppConstants.ACTION_START_WATCH)) {
			if(!hasPermission()) {
				requestPermissions(AppConstants.START_WATCH_REQ_CODE);
			} else {
				result = this.startWatch(callbackContext);
			}
		} else if (action.equals(AppConstants.ACTION_STOP_WATCH)) {
			result = this.stopWatch(callbackContext);
		} else if (action.equals(AppConstants.ACTION_FETCH_SMS)) {
			JSONObject filters = inputs.optJSONObject(0);
            result = this.fetchSMS(filters, callbackContext);
		} else {
			Log.d(AppConstants.LOG_TAG, String.format("Invalid action passed: %s", action));
			result = new PluginResult(PluginResult.Status.INVALID_ACTION);
		}
		if (result != null) {
			callbackContext.sendPluginResult(result);
		}
		return true;
	}

	

	public void onDestroy() {
		this.stopWatch(null);
	}
	private PluginResult fetchSMS(JSONObject options, CallbackContext callbackContext2)
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

	protected void createIncomingSMSReceiver() {
		this.mReceiver = new SmsReceiver(webView);
		IntentFilter filter = new IntentFilter(AppConstants.SMS_RECEIVED_ACTION);
		try {
			webView.getContext().registerReceiver(this.mReceiver, filter);
		} catch (Exception e) {
			Log.d(AppConstants.LOG_TAG, "error registering broadcast receiver: " + e.getMessage());
		}
	}

	

	/**
	 * Check if we have been granted SMS receiving permission on Android 6+
	 */
	private boolean hasPermission() {

		if(Build.VERSION.SDK_INT < AppConstants.BUILD_VERSION) {
			return true;
		}
		
		if (cordova.getActivity().checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED) {
			return false;
		}

		return true;

	}

	/**
	 * We override this so that we can access the permissions variable, which no longer exists in
	 * the parent class, since we can't initialize it reliably in the constructor!
	 *
	 * @param requestCode The code to get request action
	 */
	public void requestPermissions(int requestCode) {

		cordova.requestPermission(this, requestCode, Manifest.permission.RECEIVE_SMS);

	}
	
	/**
	 * processes the result of permission request
	 *
	 * @param requestCode The code to get request action
	 * @param permissions The collection of permissions
	 * @param grantResults The result of grant
	 */
	public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
		PluginResult result;
		for (int r : grantResults) {
			if (r == PackageManager.PERMISSION_DENIED) {
				Log.d(AppConstants.LOG_TAG, "Permission Denied!");
				result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
				callbackContext.sendPluginResult(result);
				return;
			}
		}
		switch(requestCode) {
			case AppConstants.START_WATCH_REQ_CODE:
				this.startWatch(this.callbackContext);
			break;
		}
	}
}