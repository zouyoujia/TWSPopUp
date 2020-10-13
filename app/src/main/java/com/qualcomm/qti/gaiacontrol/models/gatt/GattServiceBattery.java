package com.qualcomm.qti.gaiacontrol.models.gatt;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import com.qualcomm.qti.gaiacontrol.Utils;
import com.qualcomm.qti.gaiacontrol.models.gatt.GATT;

public class GattServiceBattery {
    private BluetoothGattCharacteristic mBatteryLevelCharacteristic = null;
    private int mDescription = 0;
    private BluetoothGattService mGattService = null;
    private BluetoothGattDescriptor mPresentationFormatDescriptor = null;

    public boolean isSupported() {
        return isServiceAvailable() && isBatteryLevelCharacteristicAvailable();
    }

    public int getBatteryLevel() {
        BluetoothGattCharacteristic bluetoothGattCharacteristic = this.mBatteryLevelCharacteristic;
        if (bluetoothGattCharacteristic != null) {
            return bluetoothGattCharacteristic.getIntValue(17, 0).intValue();
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public boolean checkService(BluetoothGattService bluetoothGattService) {
        if (!bluetoothGattService.getUuid().equals(GATT.UUIDs.SERVICE_BATTERY_UUID)) {
            return false;
        }
        this.mGattService = bluetoothGattService;
        for (BluetoothGattCharacteristic next : bluetoothGattService.getCharacteristics()) {
            if (next.getUuid().equals(GATT.UUIDs.CHARACTERISTIC_BATTERY_LEVEL_UUID) && (next.getProperties() & 2) > 0) {
                this.mBatteryLevelCharacteristic = next;
                this.mPresentationFormatDescriptor = next.getDescriptor(GATT.UUIDs.DESCRIPTOR_CHARACTERISTIC_PRESENTATION_FORMAT);
            }
        }
        return true;
    }

    public boolean isServiceAvailable() {
        return this.mGattService != null;
    }

    public boolean isBatteryLevelCharacteristicAvailable() {
        return this.mBatteryLevelCharacteristic != null;
    }

    public boolean isPresentationFormatDescriptorAvailable() {
        return this.mPresentationFormatDescriptor != null;
    }

    public BluetoothGattCharacteristic getBatteryLevelCharacteristic() {
        return this.mBatteryLevelCharacteristic;
    }

    public BluetoothGattDescriptor getPresentationFormatDescriptor() {
        return this.mPresentationFormatDescriptor;
    }

    public int getDescription() {
        return this.mDescription;
    }

    public void updateDescription() {
        byte[] value;
        if (isPresentationFormatDescriptorAvailable() && (value = this.mPresentationFormatDescriptor.getValue()) != null && value.length >= 7 && value[4] == 1) {
            this.mDescription = Utils.extractIntFromByteArray(value, 5, 2, true);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BATTERY Service ");
        if (isServiceAvailable()) {
            sb.append("available with the following characteristics:");
            sb.append("\n\t- BATTERY LEVEL");
            if (isBatteryLevelCharacteristicAvailable()) {
                sb.append(" available with the following descriptors:");
                sb.append("\n\t\t- PRESENTATION FORMAT");
                sb.append(isPresentationFormatDescriptorAvailable() ? " available" : " not available or with wrong permissions");
            } else {
                sb.append(" not available or with wrong properties");
            }
        } else {
            sb.append("not available.");
        }
        return sb.toString();
    }
}
