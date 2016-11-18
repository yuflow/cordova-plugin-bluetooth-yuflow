package com.yuflow.cordova;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.apache.cordova.CallbackContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by julien on 18/04/16.
 */
public class ConnectedThread extends Thread{

    private static final String TAG = "ConnectedThread";

    private final BluetoothSocket mSocket;
    private final InputStream mInStream;
    private final OutputStream mOutStream;
    private final BluetoothCallbacks mBluetoothCallbacks;
    private CallbackContext mCallbackContext;

    public ConnectedThread(BluetoothSocket socket, BluetoothCallbacks bluetoothCallbacks, String socketType, CallbackContext callbackContext) {
        Log.d(TAG, "create ConnectedThread: " + socketType);

        mBluetoothCallbacks = bluetoothCallbacks;
        mSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the BluetoothSocket input and output streams
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "temp sockets not created", e);
            mBluetoothCallbacks.onFail(callbackContext, "Error while creating sockets");
        }

        mInStream = tmpIn;
        mOutStream = tmpOut;
    }

    public void run() {
        Log.i(TAG, "BEGIN mConnectedThread");
        StringBuffer buffer = new StringBuffer();
        byte[] buff = new byte[1024];
        int bytes;

        // Keep listening to the InputStream while connected
        while (true) {
            try {
                // Read from the InputStream
                bytes = mInStream.read(buff);

                String readMessage = new String(buff);
                buffer.append(readMessage);

                mBluetoothCallbacks.receiveMessage(mSocket.getRemoteDevice().getAddress(), readUntil(buffer, "\n"));
            } catch (IOException e) {
                Log.e(TAG, "disconnected", e);
                mBluetoothCallbacks.connectionLost(mCallbackContext, mSocket.getRemoteDevice(),"disconnected");
                break;
            }
        }
    }

    /**
     * Write to the connected OutStream.
     *
     * @param buffer The bytes to write
     */
    private static String readUntil(StringBuffer buffer, String c) {
            String data = "";
            int index = buffer.indexOf(c, 0);
            if (index > -1) {
                data = buffer.substring(0, index + c.length());
                buffer.delete(0, buffer.length());
            }
            return data;
    }

    public void write(byte[] buffer, CallbackContext callbackContext) {
        try {
            mOutStream.write(buffer);
            mBluetoothCallbacks.onSuccess(callbackContext);
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
            mBluetoothCallbacks.onFail(callbackContext, "Exception during write");
        }
    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "close() of connect socket failed", e);
        }
    }
}
