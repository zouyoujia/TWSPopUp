package com.qualcomm.qti.gaiacontrol.models.gatt;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import com.qualcomm.qti.gaiacontrol.models.gatt.GATT;
import java.util.Iterator;
import java.util.UUID;

public class GattServiceHeartRate {
    private BluetoothGattCharacteristic mBodySensorLocationCharacteristic = null;
    private BluetoothGattService mGattService = null;
    private boolean mHasClientCharacteristicConfigurationDescriptor = false;
    private BluetoothGattCharacteristic mHeartRateControlPointCharacteristic = null;
    private BluetoothGattCharacteristic mHeartRateMeasurementCharacteristic = null;

    public boolean isSupported() {
        return isServiceAvailable() && isHeartRateMeasurementCharacteristicAvailable() && isClientCharacteristicConfigurationDescriptorAvailable();
    }

    public HeartRateMeasurementValues getHeartRateMeasurementValues() {
        int i;
        HeartRateMeasurementValues heartRateMeasurementValues = new HeartRateMeasurementValues();
        if (isHeartRateMeasurementCharacteristicAvailable()) {
            byte[] value = this.mHeartRateMeasurementCharacteristic.getValue();
            byte b = value[0];
            heartRateMeasurementValues.flags.heartRateFormat = GATT.HeartRateMeasurement.Flags.getFlag(b, 0, 1);
            heartRateMeasurementValues.flags.sensorContactStatus = GATT.HeartRateMeasurement.Flags.getFlag(b, 1, 2);
            heartRateMeasurementValues.flags.energyExpendedPresence = GATT.HeartRateMeasurement.Flags.getFlag(b, 3, 1);
            heartRateMeasurementValues.flags.rrIntervalPresence = GATT.HeartRateMeasurement.Flags.getFlag(b, 4, 1);
            if (heartRateMeasurementValues.flags.heartRateFormat == 0) {
                heartRateMeasurementValues.heartRateValue = this.mHeartRateMeasurementCharacteristic.getIntValue(17, 1).intValue();
                i = 1;
            } else if (heartRateMeasurementValues.flags.heartRateFormat != 1) {
                return heartRateMeasurementValues;
            } else {
                heartRateMeasurementValues.heartRateValue = this.mHeartRateMeasurementCharacteristic.getIntValue(18, 1).intValue();
                i = 2;
            }
            int i2 = i + 1;
            if (heartRateMeasurementValues.flags.energyExpendedPresence == 1) {
                heartRateMeasurementValues.energy = this.mHeartRateMeasurementCharacteristic.getIntValue(18, i2).intValue();
                i2 += 2;
            }
            if (heartRateMeasurementValues.flags.rrIntervalPresence == 1) {
                int length = value.length - i2;
                if (length % 2 == 0) {
                    int i3 = length / 2;
                    heartRateMeasurementValues.rrIntervals = new int[i3];
                    for (int i4 = 0; i4 < i3; i4++) {
                        heartRateMeasurementValues.rrIntervals[i4] = (int) ((((double) this.mHeartRateMeasurementCharacteristic.getIntValue(18, i2).intValue()) / 1024.0d) * 1000.0d);
                        i2 += 2;
                    }
                }
            }
        }
        return heartRateMeasurementValues;
    }

    /* access modifiers changed from: package-private */
    public boolean checkService(BluetoothGattService bluetoothGattService) {
        if (!bluetoothGattService.getUuid().equals(GATT.UUIDs.SERVICE_HEART_RATE_UUID)) {
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
            if (uuid.equals(GATT.UUIDs.CHARACTERISTIC_HEART_RATE_MEASUREMENT_UUID) && (next.getProperties() & 16) > 0) {
                this.mHeartRateMeasurementCharacteristic = next;
                if (next.getDescriptor(GATT.UUIDs.DESCRIPTOR_CLIENT_CHARACTERISTIC_CONFIGURATION_UUID) == null) {
                    z = false;
                }
                this.mHasClientCharacteristicConfigurationDescriptor = z;
            } else if (uuid.equals(GATT.UUIDs.CHARACTERISTIC_BODY_SENSOR_LOCATION_UUID) && (next.getProperties() & 2) > 0) {
                this.mBodySensorLocationCharacteristic = next;
            } else if (uuid.equals(GATT.UUIDs.CHARACTERISTIC_HEART_RATE_CONTROL_POINT_UUID) && (next.getProperties() & 8) > 0) {
                this.mHeartRateControlPointCharacteristic = next;
            }
        }
    }

    public boolean isServiceAvailable() {
        return this.mGattService != null;
    }

    public boolean isHeartRateMeasurementCharacteristicAvailable() {
        return this.mHeartRateMeasurementCharacteristic != null;
    }

    public boolean isClientCharacteristicConfigurationDescriptorAvailable() {
        return this.mHasClientCharacteristicConfigurationDescriptor;
    }

    public boolean isBodySensorLocationCharacteristicAvailable() {
        return this.mBodySensorLocationCharacteristic != null;
    }

    public boolean isHeartRateControlPointCharacteristicAvailable() {
        return this.mHeartRateControlPointCharacteristic != null;
    }

    public BluetoothGattCharacteristic getHeartRateMeasurementCharacteristic() {
        return this.mHeartRateMeasurementCharacteristic;
    }

    public BluetoothGattCharacteristic getBodySensorLocationCharacteristic() {
        return this.mBodySensorLocationCharacteristic;
    }

    public BluetoothGattCharacteristic getHeartRateControlPointCharacteristic() {
        return this.mHeartRateControlPointCharacteristic;
    }

    /* access modifiers changed from: package-private */
    public void reset() {
        this.mGattService = null;
        this.mHeartRateMeasurementCharacteristic = null;
        this.mHasClientCharacteristicConfigurationDescriptor = false;
        this.mBodySensorLocationCharacteristic = null;
        this.mHeartRateControlPointCharacteristic = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HEART RATE Service ");
        if (isServiceAvailable()) {
            sb.append("available with the following characteristics:");
            sb.append("\n\t- HEART RATE MEASUREMENT");
            if (isHeartRateMeasurementCharacteristicAvailable()) {
                sb.append(" available with the following descriptors:");
                sb.append("\n\t\t- CLIENT CHARACTERISTIC CONFIGURATION");
                sb.append(isClientCharacteristicConfigurationDescriptorAvailable() ? " available" : " not available or with wrong permissions");
            } else {
                sb.append(" not available or with wrong properties");
            }
            sb.append("\n\t- BODY SENSOR LOCATION");
            sb.append(isBodySensorLocationCharacteristicAvailable() ? " available" : " not available or with wrong properties");
            sb.append("\n\t- HEART RATE CONTROL POINT");
            sb.append(isHeartRateControlPointCharacteristicAvailable() ? " available" : " not available or with wrong properties");
        } else {
            sb.append("not available.");
        }
        return sb.toString();
    }

    public class HeartRateMeasurementValues {
        public static final int NO_VALUE = -1;
        public int energy = -1;
        public final Flags flags = new Flags();
        public int heartRateValue = -1;
        public int[] rrIntervals;

        public HeartRateMeasurementValues() {
        }

        public class Flags {
            public int energyExpendedPresence = -1;
            int heartRateFormat = -1;
            int rrIntervalPresence = -1;
            public int sensorContactStatus = -1;

            public Flags() {
            }
        }
    }
}
