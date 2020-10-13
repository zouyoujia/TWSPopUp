package com.qualcomm.qti.gaiacontrol.models.gatt;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import com.qualcomm.qti.gaiacontrol.models.gatt.GATT;

public class GattServiceLinkLoss {
    private BluetoothGattCharacteristic mAlertLevelCharacteristic = null;
    private BluetoothGattService mGattService = null;

    public boolean isSupported() {
        return isServiceAvailable() && isAlertLevelCharacteristicAvailable();
    }

    /* access modifiers changed from: package-private */
    public boolean checkService(BluetoothGattService bluetoothGattService) {
        if (!bluetoothGattService.getUuid().equals(GATT.UUIDs.SERVICE_LINK_LOSS_UUID)) {
            return false;
        }
        this.mGattService = bluetoothGattService;
        for (BluetoothGattCharacteristic next : bluetoothGattService.getCharacteristics()) {
            if (next.getUuid().equals(GATT.UUIDs.CHARACTERISTIC_ALERT_LEVEL_UUID) && (next.getProperties() & 2) > 0 && (next.getProperties() & 8) > 0) {
                this.mAlertLevelCharacteristic = next;
            }
        }
        return true;
    }

    public boolean isServiceAvailable() {
        return this.mGattService != null;
    }

    public boolean isAlertLevelCharacteristicAvailable() {
        return this.mAlertLevelCharacteristic != null;
    }

    public BluetoothGattCharacteristic getAlertLevelCharacteristic() {
        return this.mAlertLevelCharacteristic;
    }

    /* access modifiers changed from: package-private */
    public void reset() {
        this.mGattService = null;
        this.mAlertLevelCharacteristic = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LINK LOSS Service ");
        if (isServiceAvailable()) {
            sb.append("available with the following characteristics:");
            sb.append("\n\t- ALERT LEVEL");
            sb.append(isAlertLevelCharacteristicAvailable() ? " available" : " not available or with wrong properties");
        } else {
            sb.append("not available.");
        }
        return sb.toString();
    }
}
