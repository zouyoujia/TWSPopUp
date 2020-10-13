package com.qualcomm.qti.gaiacontrol.receivers;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.uplus.twspopup.utils.IntentUtils;

public class BondStateReceiver extends BroadcastReceiver {
    private final BondStateListener mListener;

    public interface BondStateListener {
        void onBondStateChange(BluetoothDevice bluetoothDevice, int i);
    }

    public BondStateReceiver(BondStateListener bondStateListener) {
        this.mListener = bondStateListener;
    }

    public void onReceive(Context context, Intent intent) {
        BluetoothDevice bluetoothDevice;
        if (intent.getAction().equals("android.bluetooth.device.action.BOND_STATE_CHANGED")) {
            BluetoothDevice bluetoothDevice2 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            int intExtra = IntentUtils.getIntExtra(intent, "android.bluetooth.device.extra.BOND_STATE", -1);
            if (bluetoothDevice2 != null && intExtra > -1) {
                this.mListener.onBondStateChange(bluetoothDevice2, intExtra);
            }
        } else if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST") && (bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE")) != null) {
            this.mListener.onBondStateChange(bluetoothDevice, 11);
        }
    }
}
