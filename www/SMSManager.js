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
module.exports = smsManagerExport;
