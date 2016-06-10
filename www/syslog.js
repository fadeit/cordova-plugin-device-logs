var exec = require('cordova/exec');

exports.syslog = function(success, error) {
  exec(success, error, 'Syslog', 'syslog', []);
};
