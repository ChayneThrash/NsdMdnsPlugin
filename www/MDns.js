var exec = require('cordova/exec');

var MDNS = {
	getHost: function(success, fail) {
		return exec(success, fail, "MDnsNsdPlugin", "Get Host Ip", []);
	}
}

module.exports = MDNS;