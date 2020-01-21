var smsManagerExport = {};

smsManagerExport.startWatch = function(successCallback, failureCallback) {
	cordova.exec( successCallback, failureCallback, 'SMSManager', 'startWatch', [] );
};

smsManagerExport.stopWatch = function(successCallback, failureCallback) {
	cordova.exec( successCallback, failureCallback, 'SMSManager', 'stopWatch', [] );
};

smsManagerExport.fetchSMS = function(options, successCallback, failureCallback) {
	cordova.exec( successCallback, failureCallback, 'SMSManager', 'fetchSMS', [ options ] );
};
smsManagerExport.fetchConversationInfo = function(options, successCallback, failureCallback) {
	cordova.exec( successCallback, failureCallback, 'SMSManager', 'fetchConversationInfo', [ options ] );
};
smsManagerExport.fetchAllConversationsOfNumber = function(options, successCallback, failureCallback) {
	cordova.exec( successCallback, failureCallback, 'SMSManager', 'fetchAllConversationsOfNumber', [ options ] );
};
smsManagerExport.fetchAllMessagesOfAConversation = function(options, successCallback, failureCallback) {
	cordova.exec( successCallback, failureCallback, 'SMSManager', 'fetchAllMessagesOfAConversation', [ options ] );
};
smsManagerExport.fetchAllUsersOfAConversation = function(options, successCallback, failureCallback) {
	cordova.exec( successCallback, failureCallback, 'SMSManager', 'fetchAllUsersOfAConversation', [ options ] );
};
module.exports = smsManagerExport;
