package com.example.ryan.androidfileopener;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import java.util.Set;
import java.util.UUID;

/**
 * Created by Ryan on 2014/11/02.
 */
public class BluetoothP2P extends Thread{
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket socket = null;
    Boolean server = true;
    final UUID TTTSERVICE_ID = UUID.fromString("00001101-0000-1000-8000-00805F9B34F7");
    Activity activity;
    ConnectionListener delegate;

    public BluetoothP2P(Activity activity, ConnectionListener delegate) {
        this.activity = activity;
        this.delegate = delegate;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void run() {
        Boolean connection = false;
        /*delegate.setMessage("Waiting as client");*/

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        // Get all devices paired with this one.
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        mBluetoothAdapter.cancelDiscovery(); 			// Cancel, discovery slows connection

        // Client discovers server MAC address
        if (pairedDevices.size() > 0)					// if one exists
            for (BluetoothDevice device : pairedDevices) 	// Loop through paired devices
                try {
                    socket = device.createRfcommSocketToServiceRecord(TTTSERVICE_ID);
                    socket.connect();
                    connection = true;
                    break;
                } catch (Exception e) {}

        if (connection) {
            delegate.setSocketServer(socket, false);
        }/* else {
            delegate.setMessage("Waiting as server");
            BluetoothServerSocket serverSocket;
            try {
                serverSocket = mBluetoothAdapter
                        .listenUsingRfcommWithServiceRecord("TTTService",
                                TTTSERVICE_ID);
                socket = serverSocket.accept();
                serverSocket.close();
                delegate.setSocketServer(socket, true);
            } catch (Exception e) {}
        }*/
    }
}