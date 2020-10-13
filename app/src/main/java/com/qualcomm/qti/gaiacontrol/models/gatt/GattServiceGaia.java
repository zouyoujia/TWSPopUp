package com.qualcomm.qti.gaiacontrol.models.gatt;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;
import com.qualcomm.qti.gaiacontrol.models.gatt.GATT;
import java.util.Iterator;
import java.util.UUID;

public class GattServiceGaia {
    private final String TAG = "GattServiceGaia";
    private BluetoothGattCharacteristic mGaiaCommandCharacteristic = null;
    private BluetoothGattCharacteristic mGaiaDataCharacteristic = null;
    private BluetoothGattCharacteristic mGaiaResponseCharacteristic = null;
    private BluetoothGattService mGattService = null;
    private boolean mIsRWCPTransportSupported = false;

    public boolean isSupported() {
        return isServiceAvailable() && isCharacteristicGaiaCommandAvailable() && isCharacteristicGaiaDataAvailable() && isCharacteristicGaiaResponseAvailable();
    }

    /* access modifiers changed from: package-private */
    public boolean checkService(BluetoothGattService bluetoothGattService) {
        if (!bluetoothGattService.getUuid().equals(GATT.UUIDs.SERVICE_GAIA_UUID)) {
            return false;
        }
        this.mGattService = bluetoothGattService;
        Iterator<BluetoothGattCharacteristic> it = bluetoothGattService.getCharacteristics().iterator();
        while (true) {
            boolean z = true;
            if (!it.hasNext()) {
                return true;
            }
            BluetoothGattCharacteristic next = it.next();
            UUID uuid = next.getUuid();
            if (uuid.equals(GATT.UUIDs.CHARACTERISTIC_GAIA_RESPONSE_UUID)) {
                this.mGaiaResponseCharacteristic = next;
            } else if (uuid.equals(GATT.UUIDs.CHARACTERISTIC_GAIA_COMMAND_UUID) && (next.getProperties() & 8) > 0) {
                this.mGaiaCommandCharacteristic = next;
            } else if (uuid.equals(GATT.UUIDs.CHARACTERISTIC_GAIA_DATA_ENDPOINT_UUID) && (next.getProperties() & 2) > 0) {
                this.mGaiaDataCharacteristic = next;
                int properties = next.getProperties();
                if ((properties & 4) <= 0 || (properties & 16) <= 0) {
                    z = false;
                }
                this.mIsRWCPTransportSupported = z;
                if (!this.mIsRWCPTransportSupported) {
                    Log.i("GattServiceGaia", "GAIA Data Endpoint characteristic does not provide the required properties for RWCP - WRITE_NO_RESPONSE or NOTIFY.");
                }
            }
        }
    }

    public boolean isRWCPTransportSupported() {
        return this.mIsRWCPTransportSupported;
    }

    public boolean isServiceAvailable() {
        return this.mGattService != null;
    }

    public boolean isCharacteristicGaiaCommandAvailable() {
        return this.mGaiaCommandCharacteristic != null;
    }

    public boolean isCharacteristicGaiaDataAvailable() {
        return this.mGaiaDataCharacteristic != null;
    }

    public boolean isCharacteristicGaiaResponseAvailable() {
        return this.mGaiaResponseCharacteristic != null;
    }

    public BluetoothGattCharacteristic getGaiaCommandCharacteristic() {
        return this.mGaiaCommandCharacteristic;
    }

    public BluetoothGattCharacteristic getGaiaDataCharacteristic() {
        return this.mGaiaDataCharacteristic;
    }

    public BluetoothGattCharacteristic getGaiaResponseCharacteristic() {
        return this.mGaiaResponseCharacteristic;
    }

    /* access modifiers changed from: package-private */
    public void reset() {
        this.mGattService = null;
        this.mGaiaDataCharacteristic = null;
        this.mGaiaResponseCharacteristic = null;
        this.mGaiaCommandCharacteristic = null;
        this.mIsRWCPTransportSupported = false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GAIA Service ");
        if (isServiceAvailable()) {
            sb.append("available with the following characteristics:");
            sb.append("\n\t- GAIA COMMAND");
            sb.append(isCharacteristicGaiaCommandAvailable() ? " available" : " not available or with wrong properties");
            sb.append("\n\t- GAIA DATA");
            sb.append(isCharacteristicGaiaDataAvailable() ? " available" : " not available or with wrong properties");
            sb.append("\n\t- GAIA RESPONSE");
            sb.append(isCharacteristicGaiaResponseAvailable() ? " available" : " not available or with wrong properties");
        } else {
            sb.append("not available.");
        }
        return sb.toString();
    }
}
