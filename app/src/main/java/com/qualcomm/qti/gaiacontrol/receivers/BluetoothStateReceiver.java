package com.qualcomm.qti.gaiacontrol.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.uplus.twspopup.utils.IntentUtils;

public class BluetoothStateReceiver extends BroadcastReceiver {
    private final BroadcastReceiverListener mListener;

    public interface BroadcastReceiverListener {
        void onBluetoothDisabled();

        void onBluetoothEnabled();
    }

    public BluetoothStateReceiver(BroadcastReceiverListener broadcastReceiverListener) {
        this.mListener = broadcastReceiverListener;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
            int intExtra = IntentUtils.getIntExtra(intent, "android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
            if (intExtra == 10) {
                this.mListener.onBluetoothDisabled();
            } else if (intExtra == 12) {
                this.mListener.onBluetoothEnabled();
            }
        }
    }
}
