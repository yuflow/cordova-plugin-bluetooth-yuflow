package com.yuflow.cordova;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.apache.cordova.CallbackContext;

import java.io.IOException;

/**
 * Created by julien on 18/04/16.
 */
public class AcceptConnectionThread extends Thread{

    private static final String TAG = "AcceptConnectionThread";

    // The local server socket
    private final BluetoothServerSocket mServerSocket;
    private BluetoothCallbacks mBluetoothCallbacks;
    private CallbackContext mCallbackContext;
    private String mSocketType;

    public AcceptConnectionThread(BluetoothAdapter bluetoothAdapter, String socketType, CallbackContext callbackContext, BluetoothCallbacks connectedCallback) {
        mBluetoothCallbacks = connectedCallback;
        mCallbackContext = callbackContext;
        mSocketType = socketType;

        BluetoothServerSocket tmp = null;

        // Create a new listening server socket
        try {
            if (mSocketType.equals(BluetoothConfig.SECURE_SOCKET)) {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(BluetoothConfig.NAME_SECURE,
                        BluetoothConfig.MY_UUID_SECURE);
            } else if (mSocketType.equals(BluetoothConfig.INSECURE_SOCKET)){
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(
                        BluetoothConfig.NAME_INSECURE, BluetoothConfig.MY_UUID_INSECURE);
            }
        } catch (IOException e) {
            Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
            mBluetoothCallbacks.serverDisconnect(mCallbackContext, "Failed to create server");
        }
        mServerSocket = tmp;
    }

    public void run() {
        Log.d(TAG, "Socket Type: " + mSocketType + "BEGIN mAcceptThread" + this);
        setName("AcceptThread" + mSocketType);

        BluetoothSocket socket = null;

        while (true) {
            try {
                // Call close from another thread to kill it
                socket = mServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
                mBluetoothCallbacks.serverDisconnect(mCallbackContext, "Error while creating socket");
                break;
            }

            if (socket != null) {
                mBluetoothCallbacks.connect(socket, socket.getRemoteDevice(), mSocketType, mCallbackContext);
            }
        }

        Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);
    }

    public void cancel() {
        Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this);
        try {
            mServerSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
        }
    }
}
