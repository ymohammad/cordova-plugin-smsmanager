package com.droaidsoft.cordova.sms.constants;

import org.json.JSONObject;

public class Filter
{
	public String folderLocation;
	public int messageId;
	public String fromAddress;
	public int threadId;
	public int maxMessageCount;
	
	public Filter(JSONObject filterOptions) {
		this.folderLocation = filterOptions.has(AppConstants.OPTION_FOLDER) ? filterOptions.optString(AppConstants.OPTION_FOLDER) : "inbox";
        this.messageId = filterOptions.has(AppConstants.OPTION_MSG_ID) ? filterOptions.optInt(AppConstants.OPTION_MSG_ID) : -1;
        this.fromAddress = filterOptions.has(AppConstants.OPTION_ADDRESS) ? filterOptions.optString(AppConstants.OPTION_ADDRESS) : "";
        this.threadId = filterOptions.has(AppConstants.OPTION_THREAD_ID) ? filterOptions.optInt(AppConstants.OPTION_THREAD_ID) : -1;
        this.maxMessageCount = filterOptions.has(AppConstants.OPTION_MAX_COUNT) ? filterOptions.optInt(AppConstants.OPTION_MAX_COUNT) : -1;
	}
}
