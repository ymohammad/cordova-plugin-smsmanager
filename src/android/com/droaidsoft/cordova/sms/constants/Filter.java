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

import org.json.JSONObject;

public class Filter
{
	public String folderLocation;
	public int messageId;
	public String fromAddress;
	public int threadId;
	public int maxMessageCount;
	public int conversationId;
	public int maxConversationCount;
	public long recievedDateLong;
	public long greaterRecievedDateLong;
	public long lesserRecievedDateLong;
	
	public Filter(JSONObject filterOptions) {
		this.folderLocation = filterOptions.has(AppConstants.OPTION_FOLDER) ? filterOptions.optString(AppConstants.OPTION_FOLDER) : "inbox";
        this.messageId = filterOptions.has(AppConstants.OPTION_MSG_ID) ? filterOptions.optInt(AppConstants.OPTION_MSG_ID) : -1;
        this.fromAddress = filterOptions.has(AppConstants.OPTION_ADDRESS) ? filterOptions.optString(AppConstants.OPTION_ADDRESS) : "";
        this.threadId = filterOptions.has(AppConstants.OPTION_THREAD_ID) ? filterOptions.optInt(AppConstants.OPTION_THREAD_ID) : -1;
        this.maxMessageCount = filterOptions.has(AppConstants.OPTION_MAX_COUNT) ? filterOptions.optInt(AppConstants.OPTION_MAX_COUNT) : -1;
        
        //Date which is the received date.
        this.recievedDateLong = filterOptions.has(AppConstants.OPTION_RECEIVE_DATE) ? filterOptions.optLong(AppConstants.OPTION_RECEIVE_DATE) : -1;
        this.greaterRecievedDateLong = filterOptions.has(AppConstants.OPTION_RECEIVE_DATE_GREATER) ? filterOptions.optLong(AppConstants.OPTION_RECEIVE_DATE_GREATER) : -1;
        this.lesserRecievedDateLong = filterOptions.has(AppConstants.OPTION_RECEIVE_DATE_LESSER) ? filterOptions.optLong(AppConstants.OPTION_RECEIVE_DATE_LESSER) : -1;
        
        //Conversations
        this.conversationId = filterOptions.has(AppConstants.OPTION_CONVERSATION_ID) ? filterOptions.optInt(AppConstants.OPTION_CONVERSATION_ID) : -1;
        this.maxConversationCount = filterOptions.has(AppConstants.OPTION_MAX_CONVERSATION_COUNT) ? filterOptions.optInt(AppConstants.OPTION_MAX_CONVERSATION_COUNT) : -1;
	}

	@Override
	public String toString()
	{
		return "Filter [folderLocation=" + this.folderLocation + ", messageId=" + this.messageId + ", fromAddress=" + this.fromAddress + ", threadId=" + this.threadId
				+ ", maxMessageCount=" + this.maxMessageCount + ", conversationId=" + this.conversationId + ", maxConversationCount=" + this.maxConversationCount
				+ ", recievedDateLong=" + this.recievedDateLong + ", greaterRecievedDateLong=" + this.greaterRecievedDateLong + ", lesserRecievedDateLong="
				+ this.lesserRecievedDateLong + "]";
	}
}
