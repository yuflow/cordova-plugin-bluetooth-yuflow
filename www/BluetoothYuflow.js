var exec = require('cordova/exec');

module.exports = {
    createServer: function(success, failure) {
        cordova.exec(success, failure, "BluetoothYuflow", "createServer", []);
    },
    stopServer: function(success, failure) {
        cordova.exec(success, failure, "BluetoothYuflow", "stopServer", []);
    },
    connectToDevice: function(macAddress, success, failure){
        cordova.exec(success, failure, "BluetoothYuflow", "connectToDevice", [macAddress]);
    },
    subscribe: function(macAddress, success, failure){
        cordova.exec(success, failure, "BluetoothYuflow", "subscribe", [macAddress]);
    },
    write: function(macAddress, message, success, failure){
        cordova.exec(success, failure, "BluetoothYuflow", "write", [macAddress, message]);
    },
    list: function (success, failure) {
        cordova.exec(success, failure, "BluetoothYuflow", "list", []);
    },
    disconnect: function (macAddress, success, failure) {
        cordova.exec(success, failure, "BluetoothYuflow", "disconnect", [macAddress]);
    },
    disconnectAll: function(success, failure) {
        cordova.exec(success, failure, "BluetoothYuflow", "disconnectAll", []);
    },
    bluetooth: function (success, failure) {
        cordova.exec(success, failure, "BluetoothYuflow", "bluetooth", []);
    }
}
