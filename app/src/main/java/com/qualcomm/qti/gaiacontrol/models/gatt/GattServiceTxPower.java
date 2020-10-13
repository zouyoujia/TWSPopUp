package com.qualcomm.qti.gaiacontrol.models.gatt;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import com.qualcomm.qti.gaiacontrol.models.gatt.GATT;

public class GattServiceTxPower {
    private BluetoothGattService mGattService = null;
    private BluetoothGattCharacteristic mTxPowerLevelCharacteristic = null;

    public boolean isSupported() {
        return isServiceAvailable() && isTxPowerLevelCharacteristicAvailable();
    }

    /* access modifiers changed from: package-private */
    public boolean checkService(BluetoothGattService bluetoothGattService) {
        if (!bluetoothGattService.getUuid().equals(GATT.UUIDs.SERVICE_TX_POWER_UUID)) {
            return false;
        }
        this.mGattService = bluetoothGattService;
        for (BluetoothGattCharacteristic next : bluetoothGattService.getCharacteristics()) {
            if (next.getUuid().equals(GATT.UUIDs.CHARACTERISTIC_TX_POWER_LEVEL_UUID) && (next.getProperties() & 2) > 0) {
                this.mTxPowerLevelCharacteristic = next;
            }
        }
        return true;
    }

    public boolean isServiceAvailable() {
        return this.mGattService != null;
    }

    public boolean isTxPowerLevelCharacteristicAvailable() {
        return this.mTxPowerLevelCharacteristic != null;
    }

    public BluetoothGattCharacteristic getTxPowerLevelCharacteristic() {
        return this.mTxPowerLevelCharacteristic;
    }

    /* access modifiers changed from: package-private */
    public void reset() {
        this.mGattService = null;
        this.mTxPowerLevelCharacteristic = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TX POWER Service ");
        if (isServiceAvailable()) {
            sb.append("available with the following characteristics:");
            sb.append("\n\t- TX POWER LEVEL");
            sb.append(isTxPowerLevelCharacteristicAvailable() ? " available" : " not available or with wrong properties");
        } else {
            sb.append("not available.");
        }
        return sb.toString();
    }
}
