# cordova-plugin-bluetooth-yuflow

Sorry for the horrible name
It's cordova Bluetooth plugin to connect to multiple devices
Only works on android

## How to use it

First you need to include it in your cordova project :

    "cordova plugin add cordova-plugin-bluetooth-yuflow".

You can reach the plugin in your scripts at "window.bluetoothyuflow".

    success and failure are always callbacks, they are triggered in case of... success and failure

Enable the bluetooth :

    bluetooth: function (success, failure)

--------------------------------------------------------------------------------

Create a bluetooth listening server waiting for incoming connections.
Success is trigger at each connection.

    createServer: function(success, failure)

--------------------------------------------------------------------------------

Stop the previously created server.

    stopServer: function(success, failure)

--------------------------------------------------------------------------------

List all the devices in range.
Return a JSON array to the success callback whith JS objects in :
"name"
"address"
"id"
"class"

    list: function (success, failure)

--------------------------------------------------------------------------------

Connect the device to another by giving it the macAddress.

    connectToDevice: function(macAddress, success, failure)

--------------------------------------------------------------------------------

If you have an incoming connexion or if you connect to another device, you won't
receive anything unless you subscribe it

    subscribe: function(macAddress, success, failure)

--------------------------------------------------------------------------------

Send a message to another device.

    write: function(macAddress, message, success, failure)

--------------------------------------------------------------------------------

Disconnect a device

    disconnect: function (macAddress, success, failure)

--------------------------------------------------------------------------------

Disconnect all devices

    disconnectAll: function(success, failure)

## Use it with caution !

I developed this plugin some months ago and we never used it in a production
environment.

## Todo

- A lot of things
