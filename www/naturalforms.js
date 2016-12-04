var exec = require('cordova/exec');

module.exports = {
    sendToNaturalForms: function (res, err, params) {
        cordova.exec(res, err, "naturalForms", "launchNaturalForms", params);
    }
};