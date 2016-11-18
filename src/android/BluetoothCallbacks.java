package com.yuflow.cordova;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import org.apache.cordova.CallbackContext;

public interface BluetoothCallbacks {
    void receiveMessage(String macAddress, String message);
    void connect(BluetoothSocket bluetoothSocket, BluetoothDevice bluetoothDevice, String socketType, CallbackContext callbackContext);
    void serverDisconnect(CallbackContext callbackContext, String message);
    void connectionLost(CallbackContext callbackContext, BluetoothDevice bluetoothDevice, String message);
    void onSuccess(CallbackContext callbackContext, String message);
    void onSuccess(CallbackContext callbackContext);
    void onFail(CallbackContext callbackContext, String message);
}
