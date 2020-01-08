package com.droaidsoft.cordova.sms.services;

import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;

import com.droaidsoft.cordova.sms.PluginUtils;
import com.droaidsoft.cordova.sms.constants.AppConstants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

    public static final String TAG = SmsReceiver.class.getSimpleName();
    CordovaWebView webView;
    
    public SmsReceiver() {
    	
    }
    public SmsReceiver(CordovaWebView webView) {
    	this.webView = webView;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Receive service is called..");
        if (!intent.getAction().equals(AppConstants.SMS_RECEIVED_ACTION)) {
        	Log.e(TAG, "Invalid Action received from intent " + intent.getAction());
        	return;
        }
        
        //Check and get the message.
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String format = bundle.getString("format");
        Object[] pdus = (Object[]) bundle.get("pdus");
        boolean isVersionM =   (Build.VERSION.SDK_INT >= AppConstants.BUILD_VERSION);
        try {
        	if (isVersionM) {
            	msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        			
            } else {
            	if (pdus != null) {
                    msgs = new SmsMessage[pdus.length];
                    Log.d(TAG, "Number of messages received.." + pdus.length);
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);   
                    }
                }
            }
        	Log.d(TAG, "Got the messages..." + msgs);
        	// Get SMS contents as JSON
			if(msgs != null && msgs.length > 0) {
				JSONArray jsms = PluginUtils.getJsonFromSmsMessageArray(msgs);
				this.onSMSArrive(jsms);
				Log.d(AppConstants.LOG_TAG, jsms.toString());
			}else{
				Log.d(AppConstants.LOG_TAG, "msgs is null");
			}
		} 
        catch (Exception e){
			Log.d(AppConstants.LOG_TAG, e.getMessage());
		}
    }
    private void onSMSArrive(JSONArray json) {
		this.webView.loadUrl("javascript:try{cordova.fireDocumentEvent('onSMSArrive', {'data': "+json+"});}catch(e){console.log('exception firing onSMSArrive event from native');};");
	}
    
    private void notifyNewMessage(Context context, String address, String messsage, String time) {
        Log.d(TAG, "Message From :" + address);
        Log.d(TAG, "Message Body :" + messsage);
        Log.d(TAG, "Message Time :" + time);
    }
}
