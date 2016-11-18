package com.yuflow.cordova;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class echoes a string called from JavaScript.
 */
public class BluetoothYuflow extends CordovaPlugin implements BluetoothCallbacks {

    private final static String TAG = "BluetoothYuflow";
    private final static boolean KEEP_CALLBACK = true;
    private final static boolean DONT_KEEP_CALLBACK = false;

    private AcceptConnectionThread mAcceptConnectionThread;
    private BluetoothAdapter mBluetoothAdapter;
    private ConnectionThread mConnectionThread;
    private HashMap<String,ConnectedThread> mConnectedThreads = new HashMap<String, ConnectedThread>();
    private HashMap<String,CallbackContext> mSubscribeHash = new HashMap<String, CallbackContext>();

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        Log.d(TAG, "action : " + action);

        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (action.equals("createServer")) {

            if (mAcceptConnectionThread== null) {
                createServer(callbackContext);
            }
            return true;

        } else if (action.equals("stopServer")){

            if (mAcceptConnectionThread.isAlive()) {
                mAcceptConnectionThread.cancel();
            }
            mAcceptConnectionThread = null;
            return true;
        } else if (action.equals("bluetooth")) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
                callbackContext.success();
            } else if (mBluetoothAdapter.isEnabled()) {
                callbackContext.success();
            } else {
                callbackContext.error("Bluetooth is disabled.");
            }

        } else if (action.equals("connectToDevice")) {

            connectToDevice(callbackContext, args.getString(0));
            return true;

        } else if (action.equals("disconnect")){

            ConnectedThread connectedThread = mConnectedThreads.remove(args.getString(0));
            if (connectedThread != null) {
                connectedThread.cancel();
            }

        } else if (action.equals("disconnectAll")){

            for(Map.Entry<String, ConnectedThread> currentEntry : mConnectedThreads.entrySet()) {
                currentEntry.getValue().cancel();
            }
            mConnectedThreads.clear();

        } else if (action.equals("subscribe")){

            if (mSubscribeHash.get(args.getString(0)) == null){
                mSubscribeHash.put(args.getString(0),callbackContext);
                return true;
            }

        } else if (action.equals("unsubscribe")) {

            if (mSubscribeHash.get(args.getString(0)) != null) {
                mSubscribeHash.remove(args.getString(0));
                return true;
            }

        } else if (action.equals("write")) {

            write(callbackContext, args.getString(0), (args.getString(1)).concat("\n"));
            return true;

        } else if (action.equals("list")) {

            listBondedDevices(callbackContext);
            return true;

        }

        return false;
    }

    private void write(CallbackContext callbackContext, String macAddress, String message) {
        ConnectedThread connectedThread = mConnectedThreads.get(macAddress);
        if (connectedThread != null) {
            connectedThread.write(message.getBytes(), callbackContext);
        }
    }

    private void createServer(CallbackContext callbackContext) {
        mAcceptConnectionThread = new AcceptConnectionThread(mBluetoothAdapter, BluetoothConfig.SECURE_SOCKET, callbackContext, this);
        mAcceptConnectionThread.start();
    }

    private void connectToDevice(CallbackContext callbackContext, String macAddress) {
        mConnectionThread = new ConnectionThread(mBluetoothAdapter.getRemoteDevice(macAddress),
                BluetoothConfig.SECURE_SOCKET,
                callbackContext,
                this);
        mConnectionThread.start();
    }

    @Override
    public void receiveMessage(String macAddress, String message) {
        Log.d(TAG, "Message : " + message);

        CallbackContext callbackContext = mSubscribeHash.get(macAddress);
        sendCallback(callbackContext, PluginResult.Status.OK, message, KEEP_CALLBACK);
    }


    @Override
    public synchronized void connect(BluetoothSocket bluetoothSocket, BluetoothDevice bluetoothDevice, String socketType, CallbackContext callbackContext) {
        sendCallback(callbackContext, PluginResult.Status.OK, bluetoothDevice.getAddress(), KEEP_CALLBACK);
        ConnectedThread tmpThread = new ConnectedThread(bluetoothSocket, this, socketType, callbackContext);
        mConnectedThreads.put(bluetoothDevice.getAddress(), tmpThread);
        tmpThread.start();
    }

    @Override
    public void serverDisconnect(CallbackContext callbackContext, String message) {
        mAcceptConnectionThread = null;
        sendCallback(callbackContext, PluginResult.Status.ERROR, message, DONT_KEEP_CALLBACK);
    }

    @Override
    public void connectionLost(CallbackContext callbackContext, BluetoothDevice bluetoothDevice, String message) {
        mConnectedThreads.remove(bluetoothDevice.getAddress());
        sendCallback(callbackContext, PluginResult.Status.ERROR, message, DONT_KEEP_CALLBACK);
    }

    @Override
    public void onSuccess(CallbackContext callbackContext, String message) {
        sendCallback(callbackContext, PluginResult.Status.OK, message, DONT_KEEP_CALLBACK);
    }

    @Override
    public void onSuccess(CallbackContext callbackContext) {
        sendCallback(callbackContext, PluginResult.Status.OK, DONT_KEEP_CALLBACK);
    }

    @Override
    public void onFail(CallbackContext callbackContext, String message) {
        sendCallback(callbackContext, PluginResult.Status.ERROR, message, DONT_KEEP_CALLBACK);
    }


    private void sendCallback(CallbackContext callbackContext, PluginResult.Status status, String message, boolean keep){
        if (callbackContext != null){
            PluginResult result = new PluginResult(status, message);
            result.setKeepCallback(keep);
            callbackContext.sendPluginResult(result);
        }
    }

    private void sendCallback(CallbackContext callbackContext, PluginResult.Status status, boolean keep){
        if (callbackContext != null){
            PluginResult result = new PluginResult(status);
            result.setKeepCallback(keep);
            callbackContext.sendPluginResult(result);
        }
    }

    private void listBondedDevices(CallbackContext callbackContext) throws JSONException {
        JSONArray deviceList = new JSONArray();
        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();

        for (BluetoothDevice device : bondedDevices) {
            deviceList.put(deviceToJSON(device));
        }
        callbackContext.success(deviceList);
    }
    

    private JSONObject deviceToJSON(BluetoothDevice device) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", device.getName());
        json.put("address", device.getAddress());
        json.put("id", device.getAddress());
        if (device.getBluetoothClass() != null) {
            json.put("class", device.getBluetoothClass().getDeviceClass());
        }
        return json;
    }
}
