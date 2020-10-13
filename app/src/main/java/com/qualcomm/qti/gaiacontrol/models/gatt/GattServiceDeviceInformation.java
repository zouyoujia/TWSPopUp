package com.qualcomm.qti.gaiacontrol.models.gatt;

import android.bluetooth.BluetoothGattService;
import com.qualcomm.qti.gaiacontrol.models.gatt.GATT;

class GattServiceDeviceInformation {
    private BluetoothGattService mGattService = null;

    GattServiceDeviceInformation() {
    }

    public boolean isSupported() {
        return isServiceAvailable();
    }

    /* access modifiers changed from: package-private */
    public boolean checkService(BluetoothGattService bluetoothGattService) {
        if (!bluetoothGattService.getUuid().equals(GATT.UUIDs.SERVICE_DEVICE_INFORMATION_UUID)) {
            return false;
        }
        this.mGattService = bluetoothGattService;
        return true;
    }

    public boolean isServiceAvailable() {
        return this.mGattService != null;
    }

    /* access modifiers changed from: package-private */
    public void reset() {
        this.mGattService = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DEVICE INFORMATION Service ");
        sb.append(isServiceAvailable() ? "available." : "not available.");
        return sb.toString();
    }
}
