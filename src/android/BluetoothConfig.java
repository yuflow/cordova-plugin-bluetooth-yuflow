package com.yuflow.cordova;

import java.util.UUID;

/**
 * Created by julien on 18/04/16.
 */
public class BluetoothConfig {

    public static final String NAME_SECURE = "BluetoothYuflowSecure";
    public static final String NAME_INSECURE = "BluetoothYuflowInsecure";

    public static final String SECURE_SOCKET = "Secure";
    public static final String INSECURE_SOCKET = "Insecure";

    public static final String FROM_SERVER = "from_server";
    public static final String FROM_CONNECTION = "from_connection";

    // Unique UUID for this application
    public static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    public static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
}
