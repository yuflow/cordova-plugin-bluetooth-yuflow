package com.yuflow.cordova;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.apache.cordova.CallbackContext;

import java.io.IOException;

/**
 * Created by julien on 18/04/16.
 */
public class ConnectionThread extends Thread {

    private static final String TAG = "ConnectionThread";

    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private BluetoothCallbacks mBluetoothCallbacks;
    private CallbackContext mCallbackContext;
    private String mSocketType;

    public ConnectionThread(BluetoothDevice device, String socketType, CallbackContext callbackContext, BluetoothCallbacks connectedCallback) {
        mBluetoothCallbacks = connectedCallback;
        mCallbackContext = callbackContext;
        mSocketType = socketType;
        mDevice = device;

        BluetoothSocket tmp = null;

        try {
            if (mSocketType.equals(BluetoothConfig.SECURE_SOCKET)) {
                tmp = device.createRfcommSocketToServiceRecord(BluetoothConfig.MY_UUID_SECURE);
            } else if (mSocketType.equals(BluetoothConfig.INSECURE_SOCKET)){
                tmp = device.createInsecureRfcommSocketToServiceRecord(BluetoothConfig.MY_UUID_INSECURE);
            }
        } catch (IOException e) {
            Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            mBluetoothCallbacks.onFail(callbackContext, "Socket Type: " + mSocketType + "create() failed");
        }
        mSocket = tmp;
    }

    public void run() {
        Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
        setName("ConnectThread" + mSocketType);

        // Always cancel discovery because it will slow down a connection
        //mAdapter.cancelDiscovery();

        // Make a connection to the BluetoothSocket
        try {
            // Call close from an other thread to shut it down
            mSocket.connect();
        } catch (IOException e) {
            // Close the socket
            try {
                mSocket.close();
            } catch (IOException e2) {
                Log.e(TAG, "unable to close() " + mSocketType +
                        " socket during connection failure", e2);
            }
            mBluetoothCallbacks.onFail(mCallbackContext, "Error while connecting socket");
            return;
        }

        // Start the connected thread
        mBluetoothCallbacks.connect(mSocket, mDevice, mSocketType, mCallbackContext);
    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
        }
    }
}
