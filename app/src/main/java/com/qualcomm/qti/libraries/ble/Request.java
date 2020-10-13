package com.qualcomm.qti.libraries.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import androidx.annotation.NonNull;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

class Request {
    private int mAttempts = 0;
    private final boolean mBooleanData;
    private final BluetoothGattCharacteristic mCharacteristic;
    private final byte[] mData;
    private final BluetoothGattDescriptor mDescriptor;
    private final int mInteger;
    private final int mType;

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RequestType {
        public static final int CHARACTERISTIC_NOTIFICATION = 0;
        public static final int READ_CHARACTERISTIC = 1;
        public static final int READ_CHARACTERISTIC_TO_INDUCE_PAIRING = 6;
        public static final int READ_DESCRIPTOR = 4;
        public static final int READ_RSSI = 7;
        public static final int REQUEST_MTU = 8;
        public static final int WRITE_CHARACTERISTIC = 2;
        public static final int WRITE_DESCRIPTOR = 5;
        public static final int WRITE_NO_RESPONSE_CHARACTERISTIC = 3;
    }

    @NonNull
    public static Request createCharacteristicNotificationRequest(@NonNull BluetoothGattCharacteristic bluetoothGattCharacteristic, boolean z) {
        return new Request(0, bluetoothGattCharacteristic, (BluetoothGattDescriptor) null, (byte[]) null, z, 0);
    }

    @NonNull
    public static Request createReadCharacteristicRequest(@NonNull BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        return new Request(1, bluetoothGattCharacteristic, (BluetoothGattDescriptor) null, (byte[]) null, false, 0);
    }

    @NonNull
    public static Request createReadCharacteristicRequestToInducePairing(@NonNull BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        return new Request(6, bluetoothGattCharacteristic, (BluetoothGattDescriptor) null, (byte[]) null, false, 0);
    }

    @NonNull
    public static Request createReadDescriptorRequest(@NonNull BluetoothGattDescriptor bluetoothGattDescriptor) {
        return new Request(4, (BluetoothGattCharacteristic) null, bluetoothGattDescriptor, (byte[]) null, false, 0);
    }

    @NonNull
    public static Request createWriteCharacteristicRequest(@NonNull BluetoothGattCharacteristic bluetoothGattCharacteristic, @NonNull byte[] bArr) {
        return new Request(2, bluetoothGattCharacteristic, (BluetoothGattDescriptor) null, bArr, false, 0);
    }

    @NonNull
    public static Request createWriteNoResponseCharacteristicRequest(@NonNull BluetoothGattCharacteristic bluetoothGattCharacteristic, @NonNull byte[] bArr) {
        return new Request(3, bluetoothGattCharacteristic, (BluetoothGattDescriptor) null, bArr, false, 0);
    }

    @NonNull
    public static Request createWriteDescriptorRequest(@NonNull BluetoothGattDescriptor bluetoothGattDescriptor, @NonNull byte[] bArr) {
        return new Request(5, (BluetoothGattCharacteristic) null, bluetoothGattDescriptor, bArr, false, 0);
    }

    @NonNull
    public static Request createReadRssiRequest() {
        return new Request(7, (BluetoothGattCharacteristic) null, (BluetoothGattDescriptor) null, (byte[]) null, false, 0);
    }

    @NonNull
    public static Request createMTURequest(int i) {
        return new Request(8, (BluetoothGattCharacteristic) null, (BluetoothGattDescriptor) null, (byte[]) null, false, i);
    }

    public static String getRequestTypeLabel(int i) {
        switch (i) {
            case 0:
                return "CHARACTERISTIC_NOTIFICATION";
            case 1:
                return "READ_CHARACTERISTIC";
            case 2:
                return "WRITE_CHARACTERISTIC";
            case 3:
                return "WRITE_NO_RESPONSE_CHARACTERISTIC";
            case 4:
                return "READ_DESCRIPTOR";
            case 5:
                return "WRITE_DESCRIPTOR";
            case 6:
                return "READ_CHARACTERISTIC_TO_INDUCE_PAIRING";
            case 7:
                return "READ_RSSI";
            case 8:
                return "REQUEST_MTU";
            default:
                return "UNKNOWN " + i;
        }
    }

    private Request(int i, BluetoothGattCharacteristic bluetoothGattCharacteristic, BluetoothGattDescriptor bluetoothGattDescriptor, byte[] bArr, boolean z, int i2) {
        this.mType = i;
        this.mCharacteristic = bluetoothGattCharacteristic;
        this.mDescriptor = bluetoothGattDescriptor;
        this.mData = bArr;
        this.mBooleanData = z;
        this.mInteger = i2;
    }

    public BluetoothGattCharacteristic buildWriteCharacteristic() {
        BluetoothGattCharacteristic bluetoothGattCharacteristic;
        if (this.mType != 2 || (bluetoothGattCharacteristic = this.mCharacteristic) == null || (bluetoothGattCharacteristic.getProperties() & 8) <= 0) {
            return null;
        }
        byte[] bArr = this.mData;
        if (bArr != null) {
            this.mCharacteristic.setValue(bArr);
        }
        this.mCharacteristic.setWriteType(2);
        return this.mCharacteristic;
    }

    public BluetoothGattCharacteristic buildWriteNoResponseCharacteristic() {
        BluetoothGattCharacteristic bluetoothGattCharacteristic;
        if (this.mType != 3 || (bluetoothGattCharacteristic = this.mCharacteristic) == null || (bluetoothGattCharacteristic.getProperties() & 4) <= 0) {
            return null;
        }
        byte[] bArr = this.mData;
        if (bArr != null) {
            this.mCharacteristic.setValue(bArr);
        }
        this.mCharacteristic.setWriteType(1);
        return this.mCharacteristic;
    }

    public BluetoothGattCharacteristic buildReadCharacteristic() {
        int i = this.mType;
        if (i == 1 || i == 6) {
            return this.mCharacteristic;
        }
        return null;
    }

    public BluetoothGattCharacteristic buildNotifyCharacteristic() {
        if (this.mType == 0) {
            return this.mCharacteristic;
        }
        return null;
    }

    public BluetoothGattDescriptor buildWriteDescriptor() {
        if (this.mType != 5) {
            return null;
        }
        byte[] bArr = this.mData;
        if (bArr != null) {
            this.mDescriptor.setValue(bArr);
        }
        return this.mDescriptor;
    }

    public BluetoothGattDescriptor buildReadDescriptor() {
        if (this.mType == 4) {
            return this.mDescriptor;
        }
        return null;
    }

    public int getAttempts() {
        return this.mAttempts;
    }

    public int getType() {
        return this.mType;
    }

    public BluetoothGattCharacteristic getCharacteristic() {
        return this.mCharacteristic;
    }

    public BluetoothGattDescriptor getDescriptor() {
        return this.mDescriptor;
    }

    public boolean getBooleanData() {
        return this.mBooleanData;
    }

    public int getInteger() {
        return this.mInteger;
    }

    public void setAttempts(int i) {
        this.mAttempts = i;
    }

    public void increaseAttempts() {
        this.mAttempts++;
    }
}
