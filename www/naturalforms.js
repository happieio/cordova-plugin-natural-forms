var exec = require('cordova/exec');

module.exports = {
    sendToNaturalForms: function (res, err, params) {
        cordova.exec(res, err, "NaturalForms", "launchNaturalForms", params);
    }
};