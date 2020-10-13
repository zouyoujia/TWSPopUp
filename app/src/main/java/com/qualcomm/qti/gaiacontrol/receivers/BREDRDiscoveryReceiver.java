package com.qualcomm.qti.gaiacontrol.receivers;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BREDRDiscoveryReceiver extends BroadcastReceiver {
    private final BREDRDiscoveryListener mListener;

    public interface BREDRDiscoveryListener {
        void onDeviceFound(BluetoothDevice bluetoothDevice);
    }

    public BREDRDiscoveryReceiver(BREDRDiscoveryListener bREDRDiscoveryListener) {
        this.mListener = bREDRDiscoveryListener;
    }

    public void onReceive(Context context, Intent intent) {
        BluetoothDevice bluetoothDevice;
        if (intent.getAction().equals("android.bluetooth.device.action.FOUND") && (bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE")) != null) {
            this.mListener.onDeviceFound(bluetoothDevice);
        }
    }
}
