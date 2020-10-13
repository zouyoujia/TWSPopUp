package com.qualcomm.qti.gaiacontrol.receivers;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.os.Parcelable;

public class UUIDReceiver extends BroadcastReceiver {
    private final BluetoothDevice mDevice;
    private final UUIDListener mListener;

    public interface UUIDListener {
        void onUUIDFound(BluetoothDevice bluetoothDevice, ParcelUuid[] parcelUuidArr);
    }

    public UUIDReceiver(UUIDListener uUIDListener, BluetoothDevice bluetoothDevice) {
        this.mListener = uUIDListener;
        this.mDevice = bluetoothDevice;
    }

    public void onReceive(Context context, Intent intent) {
        BluetoothDevice bluetoothDevice;
        String action = intent.getAction();
        if (action != null && action.equals("android.bluetooth.device.action.UUID")) {
            BluetoothDevice bluetoothDevice2 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            Parcelable[] parcelableArrayExtra = intent.getParcelableArrayExtra("android.bluetooth.device.extra.UUID");
            if (bluetoothDevice2 != null && (bluetoothDevice = this.mDevice) != null && bluetoothDevice.equals(bluetoothDevice2) && parcelableArrayExtra != null) {
                ParcelUuid[] parcelUuidArr = new ParcelUuid[parcelableArrayExtra.length];
                for (int i = 0; i < parcelableArrayExtra.length; i++) {
                    parcelUuidArr[i] = (ParcelUuid) parcelableArrayExtra[i];
                }
                this.mListener.onUUIDFound(bluetoothDevice2, parcelUuidArr);
            }
        }
    }
}
