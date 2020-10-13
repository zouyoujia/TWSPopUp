package com.qualcomm.qti.libraries.ble;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.ArrayMap;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public abstract class BLEService extends Service {
    private static final int DEFAULT_DELAY_FOR_NOTIFICATION_REQUEST = 1000;
    private static final int DEFAULT_DELAY_FOR_REQUEST = 60000;
    protected static final int MTU_SIZE_DEFAULT = 23;
    protected static final int MTU_SIZE_MAXIMUM = 512;
    private static final int REQUEST_MAX_ATTEMPTS = 2;
    private final String TAG = "BLEService";
    private boolean isQueueProcessing = false;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private final ArrayMap<UUID, BluetoothGattCharacteristic> mCharacteristics = new ArrayMap<>();
    private int mConnectionState = 0;
    private int mDelay = DEFAULT_DELAY_FOR_REQUEST;
    private BluetoothDevice mDevice;
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        public void onConnectionStateChange(BluetoothGatt bluetoothGatt, int i, int i2) {
            BLEService.this.receiveConnectionStateChange(bluetoothGatt, i, i2);
        }

        public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
            BLEService.this.receiveServicesDiscovered(bluetoothGatt, i);
        }

        public void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
            BLEService.this.receiveCharacteristicRead(bluetoothGatt, bluetoothGattCharacteristic, i);
        }

        public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
            BLEService.this.onReceivedCharacteristicChanged(bluetoothGatt, bluetoothGattCharacteristic);
        }

        public void onDescriptorWrite(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
            BLEService.this.receiveDescriptorWrite(bluetoothGatt, bluetoothGattDescriptor, i);
        }

        public void onCharacteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
            BLEService.this.receiveCharacteristicWrite(bluetoothGatt, bluetoothGattCharacteristic, i);
        }

        public void onDescriptorRead(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
            BLEService.this.receiveDescriptorRead(bluetoothGatt, bluetoothGattDescriptor, i);
        }

        public void onReadRemoteRssi(BluetoothGatt bluetoothGatt, int i, int i2) {
            BLEService.this.receiveRemoteRssiRead(bluetoothGatt, i, i2);
        }

        public void onMtuChanged(BluetoothGatt bluetoothGatt, int i, int i2) {
            BLEService.this.receiveMtuChanged(bluetoothGatt, i, i2);
        }
    };
    private final Handler mHandler = new Handler();
    private final Queue<Request> mRequestsQueue = new LinkedList();
    private boolean mShowDebugLogs = false;
    /* access modifiers changed from: private */
    public TimeOutRequestRunnable mTimeOutRequestRunnable = null;

    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
        public static final int CONNECTED = 2;
        public static final int CONNECTING = 1;
        public static final int DISCONNECTED = 0;
        public static final int DISCONNECTING = 3;
    }

    /* access modifiers changed from: protected */
    public abstract void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i);

    /* access modifiers changed from: protected */
    public abstract void onCharacteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i);

    /* access modifiers changed from: protected */
    public abstract void onConnectionStateChange(BluetoothGatt bluetoothGatt, int i, int i2);

    /* access modifiers changed from: protected */
    public abstract void onDescriptorRead(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i);

    /* access modifiers changed from: protected */
    public abstract void onDescriptorWrite(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i);

    /* access modifiers changed from: protected */
    public abstract void onMTUChanged(BluetoothGatt bluetoothGatt, int i, int i2);

    /* access modifiers changed from: protected */
    public abstract void onReceivedCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic);

    /* access modifiers changed from: protected */
    public abstract void onRemoteRssiRead(BluetoothGatt bluetoothGatt, int i, int i2);

    /* access modifiers changed from: protected */
    public abstract void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i);

    /* access modifiers changed from: protected */
    public void showDebugLogs(boolean z) {
        this.mShowDebugLogs = z;
        StringBuilder sb = new StringBuilder();
        sb.append("Debug logs are now ");
        sb.append(z ? "activated" : "deactivated");
        sb.append(".");
        Log.i("BLEService", sb.toString());
    }

    /* access modifiers changed from: protected */
    public synchronized void setDelayForRequest(int i) {
        this.mDelay = i;
    }

    /* access modifiers changed from: protected */
    public boolean requestCharacteristicNotification(UUID uuid, boolean z) {
        if (this.mShowDebugLogs) {
            StringBuilder sb = new StringBuilder();
            sb.append("Request received for notification on characteristic with UUID ");
            sb.append(uuid.toString());
            sb.append(" for ");
            sb.append(z ? "activation" : "deactivation");
            Log.d("BLEService", sb.toString());
        }
        if (this.mConnectionState != 2) {
            Log.w("BLEService", "request characteristic notification not initiated: device is disconnected.");
            return false;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristic = this.mCharacteristics.get(uuid);
        if (bluetoothGattCharacteristic != null) {
            return requestCharacteristicNotification(bluetoothGattCharacteristic, z);
        }
        Log.w("BLEService", "request characteristic notification not initiated: characteristic not found for UUID " + uuid);
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean requestCharacteristicNotification(BluetoothGattCharacteristic bluetoothGattCharacteristic, boolean z) {
        if (this.mShowDebugLogs) {
            StringBuilder sb = new StringBuilder();
            sb.append("Request received for notification on characteristic with UUID ");
            sb.append(bluetoothGattCharacteristic.getUuid().toString());
            sb.append(" for ");
            sb.append(z ? "activation" : "deactivation");
            Log.d("BLEService", sb.toString());
        }
        if (this.mConnectionState != 2) {
            Log.w("BLEService", "request characteristic notification not initiated: device is disconnected.");
            return false;
        } else if (bluetoothGattCharacteristic == null) {
            Log.w("BLEService", "request characteristic notification not initiated: characteristic is null.");
            return false;
        } else if (!this.mCharacteristics.containsKey(bluetoothGattCharacteristic.getUuid())) {
            Log.w("BLEService", "request characteristic notification not initiated: unknown characteristic UUID.");
            return false;
        } else {
            BluetoothGattDescriptor descriptor = bluetoothGattCharacteristic.getDescriptor(Characteristics.CLIENT_CHARACTERISTIC_CONFIG);
            if (descriptor == null) {
                Log.w("BLEService", "request characteristic notification not initiated: no CLIENT_CHARACTERISTIC_CONFIGURATION descriptor.");
                return false;
            }
            Request createCharacteristicNotificationRequest = Request.createCharacteristicNotificationRequest(bluetoothGattCharacteristic, z);
            byte[] bArr = z ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
            addToRequestsQueue(createCharacteristicNotificationRequest);
            addToRequestsQueue(Request.createWriteDescriptorRequest(descriptor, bArr));
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public boolean requestWriteCharacteristic(UUID uuid, byte[] bArr) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request received for write on characteristic with UUID " + uuid.toString());
        }
        if (this.mConnectionState != 2) {
            Log.w("BLEService", "request write characteristic not initiated: device is disconnected.");
            return false;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristic = this.mCharacteristics.get(uuid);
        if (bluetoothGattCharacteristic != null) {
            return requestWriteCharacteristic(bluetoothGattCharacteristic, bArr);
        }
        Log.w("BLEService", "request write characteristic not initiated: characteristic not found for UUID " + uuid);
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean requestWriteCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic, byte[] bArr) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request received for write on characteristic with UUID " + bluetoothGattCharacteristic.getUuid().toString());
        }
        if (this.mConnectionState != 2) {
            Log.w("BLEService", "request write characteristic not initiated: device is disconnected.");
            return false;
        } else if (bluetoothGattCharacteristic == null) {
            Log.w("BLEService", "request write characteristic not initiated: characteristic is null.");
            return false;
        } else if (!this.mCharacteristics.containsKey(bluetoothGattCharacteristic.getUuid())) {
            Log.w("BLEService", "request write characteristic not initiated: unknown characteristic UUID.");
            return false;
        } else if ((bluetoothGattCharacteristic.getProperties() & 8) <= 0) {
            Log.w("BLEService", "request write characteristic not initiated: characteristic does not have the WRITE property.");
            return false;
        } else {
            addToRequestsQueue(Request.createWriteCharacteristicRequest(bluetoothGattCharacteristic, bArr));
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public boolean requestWriteNoResponseCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic, byte[] bArr) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request received for write without response on characteristic with UUID " + bluetoothGattCharacteristic.getUuid().toString());
        }
        if (this.mConnectionState != 2) {
            Log.w("BLEService", "request write without response characteristic not initiated: device is disconnected.");
            return false;
        } else if (bluetoothGattCharacteristic == null) {
            Log.w("BLEService", "request write without response characteristic not initiated: characteristic is null.");
            return false;
        } else if (!this.mCharacteristics.containsKey(bluetoothGattCharacteristic.getUuid())) {
            Log.w("BLEService", "request write without response characteristic not initiated: unknown characteristic UUID.");
            return false;
        } else if ((bluetoothGattCharacteristic.getProperties() & 4) <= 0) {
            Log.w("BLEService", "request write without response characteristic not initiated: characteristic does not have the WRITE NO RESPONSE property.");
            return false;
        } else {
            addToRequestsQueue(Request.createWriteNoResponseCharacteristicRequest(bluetoothGattCharacteristic, bArr));
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public boolean requestReadCharacteristic(UUID uuid) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request received for read on characteristic with UUID " + uuid.toString());
        }
        if (this.mConnectionState != 2) {
            Log.w("BLEService", "request read characteristic not initiated: device is disconnected.");
            return false;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristic = this.mCharacteristics.get(uuid);
        if (bluetoothGattCharacteristic != null) {
            return requestReadCharacteristic(bluetoothGattCharacteristic);
        }
        Log.w("BLEService", "request read characteristic not initiated: characteristic not found for UUID " + uuid);
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean requestReadCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request received for read on characteristic with UUID " + bluetoothGattCharacteristic.getUuid().toString());
        }
        if (this.mConnectionState != 2) {
            Log.w("BLEService", "request read characteristic not initiated: device is disconnected.");
            return false;
        } else if (bluetoothGattCharacteristic == null) {
            Log.w("BLEService", "request read characteristic not initiated: characteristic is null.");
            return false;
        } else if (!this.mCharacteristics.containsKey(bluetoothGattCharacteristic.getUuid())) {
            Log.w("BLEService", "request read characteristic not initiated: unknown characteristic UUID.");
            return false;
        } else if ((bluetoothGattCharacteristic.getProperties() & 2) <= 0) {
            Log.w("BLEService", "request read characteristic not initiated: characteristic does not have the READ property.");
            return false;
        } else {
            addToRequestsQueue(Request.createReadCharacteristicRequest(bluetoothGattCharacteristic));
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public boolean requestReadCharacteristicForPairing(UUID uuid) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request received for read to induce pairing on characteristic with UUID " + uuid.toString());
        }
        if (this.mConnectionState != 2) {
            Log.w("BLEService", "request read to induce pairing characteristic not initiated: device is disconnected.");
            return false;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristic = this.mCharacteristics.get(uuid);
        if (bluetoothGattCharacteristic != null) {
            return requestReadCharacteristicForPairing(bluetoothGattCharacteristic);
        }
        Log.w("BLEService", "request read to induce pairing characteristic not initiated: characteristic not found for UUID " + uuid);
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean requestReadCharacteristicForPairing(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request received for read to induce pairing on characteristic with UUID " + bluetoothGattCharacteristic.getUuid().toString());
        }
        if (this.mConnectionState != 2) {
            Log.w("BLEService", "request read to induce pairing characteristic not initiated: device is disconnected.");
            return false;
        } else if (bluetoothGattCharacteristic == null) {
            Log.w("BLEService", "request read to induce pairing characteristic not initiated: characteristic is null.");
            return false;
        } else if (!this.mCharacteristics.containsKey(bluetoothGattCharacteristic.getUuid())) {
            Log.w("BLEService", "request read to induce pairing characteristic not initiated: unknown characteristic UUID.");
            return false;
        } else if ((bluetoothGattCharacteristic.getProperties() & 2) <= 0) {
            Log.w("BLEService", "request read to induce pairing characteristic not initiated: characteristic does not have the READ property.");
            return false;
        } else {
            Request createReadCharacteristicRequestToInducePairing = Request.createReadCharacteristicRequestToInducePairing(bluetoothGattCharacteristic);
            createReadCharacteristicRequestToInducePairing.setAttempts(1);
            addToRequestsQueue(createReadCharacteristicRequestToInducePairing);
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public boolean requestReadDescriptor(BluetoothGattDescriptor bluetoothGattDescriptor) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request received for read on descriptor with UUID " + bluetoothGattDescriptor.getUuid().toString());
        }
        if (this.mConnectionState != 2) {
            Log.w("BLEService", "request read on descriptor not initiated: device is disconnected.");
            return false;
        } else if (bluetoothGattDescriptor == null) {
            Log.w("BLEService", "request read on descriptor not initiated: descriptor is null.");
            return false;
        } else if (!this.mCharacteristics.containsKey(bluetoothGattDescriptor.getCharacteristic().getUuid())) {
            Log.w("BLEService", "request read on descriptor not initiated: unknown characteristic UUID.");
            return false;
        } else {
            addToRequestsQueue(Request.createReadDescriptorRequest(bluetoothGattDescriptor));
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public boolean requestReadRssi() {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request received for read RSSI level");
        }
        if (this.mConnectionState != 2) {
            Log.w("BLEService", "request read RSSI level not initiated: device is disconnected.");
            return false;
        }
        BluetoothDevice bluetoothDevice = this.mDevice;
        if (bluetoothDevice == null) {
            Log.w("BLEService", "request read RSSI level not initiated: device is null.");
            return false;
        } else if (bluetoothDevice.getType() != 2) {
            Log.w("BLEService", "request read RSSI level not initiated: device is not LE only.");
            return false;
        } else {
            addToRequestsQueue(Request.createReadRssiRequest());
            return true;
        }
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"ObsoleteSdkInt"})
    public boolean requestMTUSize(int i) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request received for request MTU.");
        }
        if (this.mConnectionState != 2) {
            Log.w("BLEService", "request MTU not initiated: device is disconnected.");
            return false;
        } else if (this.mDevice == null) {
            Log.w("BLEService", "request MTU not initiated: device is null.");
            return false;
        } else if (i < 23 || i > 512) {
            Log.w("BLEService", "request MTU not initiated: value (" + i + ") not in interval [" + 23 + ", " + 512 + "].");
            return false;
        } else if (Build.VERSION.SDK_INT < 21) {
            Log.w("BLEService", "request MTU not initiated: Android version is too low, minimum required is Lollipop.");
            return false;
        } else {
            addToRequestsQueue(Request.createMTURequest(i));
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public boolean initialize() {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request received for initialisation of the Bluetooth components");
        }
        if (this.mBluetoothAdapter == null || !this.mShowDebugLogs) {
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null) {
                Log.e("BLEService", "Initialisation of the Bluetooth Adapter failed: unable to initialize BluetoothManager.");
                return false;
            }
            this.mBluetoothAdapter = bluetoothManager.getAdapter();
            if (this.mBluetoothAdapter != null) {
                return true;
            }
            Log.e("BLEService", "Initialisation of the Bluetooth Adapter failed: unable to initialize BluetoothManager.");
            return false;
        }
        Log.d("BLEService", "Bluetooth adapter already initialized");
        return true;
    }

    /* access modifiers changed from: protected */
    public int getConnectionState() {
        return this.mConnectionState;
    }

    /* access modifiers changed from: protected */
    public BluetoothDevice getDevice() {
        return this.mDevice;
    }

    /* access modifiers changed from: protected */
    public BluetoothGatt getBluetoothGatt() {
        return this.mBluetoothGatt;
    }

    /* access modifiers changed from: protected */
    public boolean connectToDevice(String str) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request received to connect to a device with address " + str);
        }
        if (!BluetoothAdapter.checkBluetoothAddress(str)) {
            Log.w("BLEService", "request connect to device not initiated: bluetooth address is unknown.");
            return false;
        }
        BluetoothDevice remoteDevice = this.mBluetoothAdapter.getRemoteDevice(str);
        if (remoteDevice != null) {
            return connectToDevice(remoteDevice);
        }
        Log.w("BLEService", "request connect to device not initiated: unable to get a BluetoothDevice from address " + str);
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean connectToDevice(BluetoothDevice bluetoothDevice) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request received to connect to a BluetoothDevice");
        }
        if (bluetoothDevice == null) {
            Log.w("BLEService", "request connect to BluetoothDevice failed: device is null.");
            return false;
        } else if (this.mConnectionState == 2) {
            Log.w("BLEService", "request connect to BluetoothDevice failed: a device is already connected.");
            return false;
        } else if (this.mBluetoothAdapter == null) {
            Log.w("BLEService", "request connect to BluetoothDevice failed: no BluetoothAdapter initialized.");
            return false;
        } else {
            this.mDevice = bluetoothDevice;
            setState(1);
            Log.d("BLEService", "request connect to BluetoothDevice " + this.mDevice.getAddress() + " over GATT starts.");
            if (Build.VERSION.SDK_INT >= 23) {
                this.mBluetoothGatt = bluetoothDevice.connectGatt(this, false, this.mGattCallback, 2);
            } else {
                this.mBluetoothGatt = bluetoothDevice.connectGatt(this, false, this.mGattCallback);
            }
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public boolean reconnectToDevice() {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request received to reconnect to a BluetoothDevice");
        }
        if (this.mDevice == null) {
            Log.w("BLEService", "request reconnect to BluetoothDevice failed: device is null.");
            return false;
        } else if (this.mConnectionState == 2) {
            Log.w("BLEService", "request reconnect to BluetoothDevice failed: a device is already connected.");
            return false;
        } else if (this.mBluetoothAdapter == null) {
            Log.w("BLEService", "request reconnect to BluetoothDevice failed: no BluetoothAdapter initialized.");
            return false;
        } else {
            setState(1);
            Log.d("BLEService", "request reconnect to BluetoothDevice " + this.mDevice.getAddress() + " over GATT starts.");
            if (Build.VERSION.SDK_INT >= 23) {
                this.mBluetoothGatt = this.mDevice.connectGatt(this, true, this.mGattCallback, 2);
            } else {
                this.mBluetoothGatt = this.mDevice.connectGatt(this, true, this.mGattCallback);
            }
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void disconnectFromDevice() {
        resetQueue();
        this.mCharacteristics.clear();
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request received to disconnect from a BluetoothDevice");
        }
        if (this.mBluetoothAdapter == null) {
            Log.i("BLEService", "request disconnect from BluetoothDevice: BluetoothAdapter is null.");
            setState(0);
        } else if (this.mBluetoothGatt == null) {
            Log.i("BLEService", "request disconnect from BluetoothDevice: BluetoothGatt is null.");
            setState(0);
        } else {
            Log.i("BLEService", "Request disconnect from BluetoothDevice " + this.mBluetoothGatt.getDevice().getAddress() + " starts.");
            setState(3);
            this.mBluetoothGatt.disconnect();
        }
    }

    /* access modifiers changed from: protected */
    public synchronized void setState(int i) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Connection state changes from " + BLEUtils.getConnectionStateName(this.mConnectionState) + " to " + BLEUtils.getConnectionStateName(i));
        }
        this.mConnectionState = i;
    }

    /* access modifiers changed from: private */
    public void receiveConnectionStateChange(BluetoothGatt bluetoothGatt, int i, int i2) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "GattCallback - onConnectionStateChange, newState=" + i2 + ", status=" + i);
        }
        if (i == 0 && i2 == 2) {
            setState(2);
            Log.i("BLEService", "Successful connection to device: " + bluetoothGatt.getDevice().getAddress());
            if (this.mBluetoothGatt == null) {
                this.mBluetoothGatt = bluetoothGatt;
            }
        } else if (i2 == 0) {
            if (this.mConnectionState == 3) {
                Log.i("BLEService", "Successful disconnection from device: " + bluetoothGatt.getDevice().getAddress());
            } else {
                Log.i("BLEService", "Disconnected from device: " + bluetoothGatt.getDevice().getAddress());
            }
            setState(0);
            resetQueue();
            this.mCharacteristics.clear();
            if (this.mShowDebugLogs) {
                Log.d("BLEService", "Device disconnected, closing BluetoothGatt object.");
            }
            bluetoothGatt.close();
            this.mBluetoothGatt = null;
        }
        onConnectionStateChange(bluetoothGatt, i, i2);
    }

    /* access modifiers changed from: private */
    public void receiveServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "GattCallback - onServicesDiscovered, status=" + i);
        }
        if (i == 0) {
            for (BluetoothGattService characteristics : bluetoothGatt.getServices()) {
                for (BluetoothGattCharacteristic next : characteristics.getCharacteristics()) {
                    this.mCharacteristics.put(next.getUuid(), next);
                }
            }
        } else {
            Log.w("BLEService", "Unsuccessful status for GATT Services discovery on callback: " + BLEUtils.getGattStatusName(i, false));
        }
        processNextRequest();
        onServicesDiscovered(bluetoothGatt, i);
    }

    /* access modifiers changed from: private */
    public void receiveCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "GattCallback - onCharacteristicRead, characteristic=" + bluetoothGattCharacteristic.getUuid() + "status=" + i);
        }
        TimeOutRequestRunnable timeOutRequestRunnable = this.mTimeOutRequestRunnable;
        int i2 = 6;
        boolean z = true;
        if (timeOutRequestRunnable == null || timeOutRequestRunnable.request.getType() != 6) {
            i2 = 1;
        }
        Request onReceiveCallback = onReceiveCallback(i2, bluetoothGattCharacteristic);
        if (onReceiveCallback == null) {
            z = false;
        }
        if (i != 0) {
            Log.w("BLEService", "Unsuccessful read characteristic for characteristic " + bluetoothGattCharacteristic.getUuid().toString() + " - status: " + BLEUtils.getGattStatusName(i, false));
            if (z) {
                onRequestFailed(onReceiveCallback);
            }
        } else if (z) {
            processNextRequest();
        }
        onCharacteristicRead(bluetoothGatt, bluetoothGattCharacteristic, i);
    }

    /* access modifiers changed from: private */
    public void receiveRemoteRssiRead(BluetoothGatt bluetoothGatt, int i, int i2) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "GattCallback - onRemoteRssiRead, rssi=" + i + " status=" + i2);
        }
        Request onReceiveCallback = onReceiveCallback(7);
        boolean z = onReceiveCallback != null;
        if (i2 != 0) {
            Log.w("BLEService", "Unsuccessful remote rssi read - status: " + BLEUtils.getGattStatusName(i2, false));
            if (z) {
                onRequestFailed(onReceiveCallback);
            }
        } else if (z) {
            processNextRequest();
        }
        onRemoteRssiRead(bluetoothGatt, i, i2);
    }

    /* access modifiers changed from: private */
    public void receiveMtuChanged(BluetoothGatt bluetoothGatt, int i, int i2) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "GattCallback - onMTUChanged, mtu=" + i + " status=" + i2);
        }
        Request onReceiveCallback = onReceiveCallback(8);
        boolean z = onReceiveCallback != null;
        if (i2 != 0) {
            Log.w("BLEService", "Unsuccessful MTU request - status: " + BLEUtils.getGattStatusName(i2, false));
            if (z) {
                onRequestFailed(onReceiveCallback);
            }
        } else if (z) {
            processNextRequest();
        }
        onMTUChanged(bluetoothGatt, i, i2);
    }

    /* access modifiers changed from: private */
    public void receiveDescriptorWrite(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "GattCallback - onDescriptorWrite, descriptor=" + bluetoothGattDescriptor.getUuid() + "status=" + i);
        }
        Request onReceiveCallback = onReceiveCallback(5, bluetoothGattDescriptor);
        boolean z = onReceiveCallback != null;
        if (i != 0) {
            Log.w("BLEService", "Unsuccessful write descriptor for characteristic " + bluetoothGattDescriptor.getCharacteristic().getUuid().toString() + " - status: " + BLEUtils.getGattStatusName(i, false));
            if (z) {
                onRequestFailed(onReceiveCallback);
            }
        } else if (z) {
            processNextRequest();
        }
        onDescriptorWrite(bluetoothGatt, bluetoothGattDescriptor, i);
    }

    /* access modifiers changed from: private */
    public void receiveDescriptorRead(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "GattCallback - onDescriptorRead, descriptor=" + bluetoothGattDescriptor.getUuid() + "status=" + i);
        }
        Request onReceiveCallback = onReceiveCallback(4, bluetoothGattDescriptor);
        boolean z = onReceiveCallback != null;
        if (i != 0) {
            Log.w("BLEService", "Unsuccessful read descriptor for characteristic " + bluetoothGattDescriptor.getCharacteristic().getUuid().toString() + " - status: " + BLEUtils.getGattStatusName(i, false));
            if (z) {
                onRequestFailed(onReceiveCallback);
            }
        } else if (z) {
            processNextRequest();
        }
        onDescriptorRead(bluetoothGatt, bluetoothGattDescriptor, i);
    }

    /* access modifiers changed from: private */
    public void receiveCharacteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "GattCallback - onCharacteristicWrite, characteristic=" + bluetoothGattCharacteristic.getUuid() + "status=" + i);
        }
        TimeOutRequestRunnable timeOutRequestRunnable = this.mTimeOutRequestRunnable;
        int i2 = 3;
        if (timeOutRequestRunnable == null || timeOutRequestRunnable.request.getType() != 3) {
            i2 = 2;
        }
        Request onReceiveCallback = onReceiveCallback(i2, bluetoothGattCharacteristic);
        boolean z = onReceiveCallback != null;
        if (i != 0) {
            Log.w("BLEService", "Unsuccessful write characteristic for characteristic " + bluetoothGattCharacteristic.getUuid().toString() + " - status: " + BLEUtils.getGattStatusName(i, false));
            if (z) {
                onRequestFailed(onReceiveCallback);
            }
        } else if (z) {
            processNextRequest();
        }
        onCharacteristicWrite(bluetoothGatt, bluetoothGattCharacteristic, i);
    }

    private boolean readCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Process request read characteristic for characteristic " + bluetoothGattCharacteristic.getUuid());
        }
        if (this.mBluetoothAdapter == null) {
            Log.w("BLEService", "Read characteristic cannot be processed: BluetoothAdapter is null.");
            return false;
        }
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            Log.w("BLEService", "Read characteristic cannot be processed: BluetoothGatt is null.");
            return false;
        }
        boolean readCharacteristic = bluetoothGatt.readCharacteristic(bluetoothGattCharacteristic);
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request read characteristic dispatched to system: " + readCharacteristic);
        }
        return readCharacteristic;
    }

    private boolean setCharacteristicNotification(BluetoothGattCharacteristic bluetoothGattCharacteristic, boolean z) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Process request set characteristic notification for characteristic " + bluetoothGattCharacteristic.getUuid() + " with enabled=" + z);
        }
        if (this.mBluetoothAdapter == null) {
            Log.w("BLEService", "Set characteristic notification cannot be processed: BluetoothAdapter is null.");
            return false;
        }
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            Log.w("BLEService", "Set characteristic notification cannot be processed: BluetoothGatt is null.");
            return false;
        }
        boolean characteristicNotification = bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, z);
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request set characteristic notification dispatched to system: " + characteristicNotification);
        }
        return characteristicNotification;
    }

    private boolean readDescriptor(BluetoothGattDescriptor bluetoothGattDescriptor) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Process request read descriptor for descriptor " + bluetoothGattDescriptor.getUuid());
        }
        if (this.mBluetoothAdapter == null) {
            Log.w("BLEService", "Read descriptor cannot be processed: BluetoothAdapter is null.");
            return false;
        }
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            Log.w("BLEService", "Read descriptor cannot be processed: BluetoothGatt is null.");
            return false;
        }
        boolean readDescriptor = bluetoothGatt.readDescriptor(bluetoothGattDescriptor);
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request read descriptor dispatched to system: " + readDescriptor);
        }
        return readDescriptor;
    }

    private boolean writeDescriptor(BluetoothGattDescriptor bluetoothGattDescriptor) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Process request write descriptor for descriptor " + bluetoothGattDescriptor.getUuid());
        }
        if (this.mBluetoothAdapter == null) {
            Log.w("BLEService", "Write descriptor cannot be processed: BluetoothAdapter is null.");
            return false;
        }
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            Log.w("BLEService", "Write descriptor cannot be processed: BluetoothGatt is null.");
            return false;
        }
        boolean writeDescriptor = bluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request write descriptor dispatched to system: " + writeDescriptor);
        }
        return writeDescriptor;
    }

    private boolean writeCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Process request write characteristic for characteristic " + bluetoothGattCharacteristic.getUuid());
        }
        if (this.mBluetoothAdapter == null) {
            Log.w("BLEService", "Write characteristic cannot be processed: BluetoothAdapter is null.");
            return false;
        }
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            Log.w("BLEService", "Write characteristic cannot be processed: BluetoothGatt is null.");
            return false;
        }
        boolean writeCharacteristic = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request write characteristic dispatched to system: " + writeCharacteristic);
        }
        return writeCharacteristic;
    }

    private boolean readRemoteRssi() {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Process read remote RSSI");
        }
        if (this.mBluetoothAdapter == null) {
            Log.w("BLEService", "Read remote RSSI cannot be processed: BluetoothAdapter is null.");
            return false;
        }
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            Log.w("BLEService", "Read remote RSSI cannot be processed: BluetoothGatt is null.");
            return false;
        }
        boolean readRemoteRssi = bluetoothGatt.readRemoteRssi();
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Request read remote RSSI dispatched to system: " + readRemoteRssi);
        }
        return readRemoteRssi;
    }

    private boolean requestMTU(int i) {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Process request MTU");
        }
        if (this.mBluetoothAdapter == null) {
            Log.w("BLEService", "Request MTU cannot be processed: BluetoothAdapter is null.");
            return false;
        } else if (this.mBluetoothGatt == null) {
            Log.w("BLEService", "Request MTU cannot be processed: BluetoothGatt is null.");
            return false;
        } else if (Build.VERSION.SDK_INT >= 21) {
            boolean requestMtu = this.mBluetoothGatt.requestMtu(i);
            if (this.mShowDebugLogs) {
                Log.d("BLEService", "Request read remote RSSI dispatched to system: " + requestMtu);
            }
            return requestMtu;
        } else {
            Log.w("BLEService", "Request MTU cannot be processed: requires at least Android Lollipop API 21.");
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public List<BluetoothGattService> getSupportedGattServices() {
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt != null) {
            return bluetoothGatt.getServices();
        }
        Log.w("BLEService", "getSupportedGattServices() - BluetoothGatt is null.");
        return null;
    }

    /* access modifiers changed from: private */
    public void onRequestFailed(Request request) {
        if (request != null && request.getAttempts() < 2) {
            addToRequestsQueue(request);
        } else if (request != null) {
            Log.w("BLEService", "Request " + Request.getRequestTypeLabel(request.getType()) + " failed");
            if (request.getType() == 6 && this.mDevice.getBondState() == 10) {
                Log.i("BLEService", "Induce pairing by creating bond manually.");
                this.mDevice.createBond();
            }
        } else {
            Log.w("BLEService", "An unknown request failed (null request object).");
        }
        processNextRequest();
    }

    private Request onReceiveCallback(int i, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        TimeOutRequestRunnable timeOutRequestRunnable = this.mTimeOutRequestRunnable;
        if (timeOutRequestRunnable == null || timeOutRequestRunnable.request.getType() != i || bluetoothGattCharacteristic == null || this.mTimeOutRequestRunnable.request.getCharacteristic() == null || !this.mTimeOutRequestRunnable.request.getCharacteristic().getUuid().equals(bluetoothGattCharacteristic.getUuid())) {
            StringBuilder sb = new StringBuilder();
            sb.append("Received unexpected callback for characteristic ");
            sb.append(bluetoothGattCharacteristic != null ? bluetoothGattCharacteristic.getUuid() : "null");
            sb.append(" with request type = ");
            sb.append(Request.getRequestTypeLabel(i));
            Log.w("BLEService", sb.toString());
            return null;
        }
        Request access$800 = this.mTimeOutRequestRunnable.request;
        cancelTimeOutRequestRunnable();
        return access$800;
    }

    private Request onReceiveCallback(int i) {
        TimeOutRequestRunnable timeOutRequestRunnable = this.mTimeOutRequestRunnable;
        if (timeOutRequestRunnable == null || timeOutRequestRunnable.request.getType() != i) {
            Log.w("BLEService", "Received unexpected callback for request type = " + Request.getRequestTypeLabel(i));
            return null;
        }
        Request access$800 = this.mTimeOutRequestRunnable.request;
        cancelTimeOutRequestRunnable();
        return access$800;
    }

    private Request onReceiveCallback(int i, BluetoothGattDescriptor bluetoothGattDescriptor) {
        TimeOutRequestRunnable timeOutRequestRunnable = this.mTimeOutRequestRunnable;
        if (timeOutRequestRunnable == null || timeOutRequestRunnable.request.getType() != i || bluetoothGattDescriptor == null || this.mTimeOutRequestRunnable.request.getDescriptor() == null || !this.mTimeOutRequestRunnable.request.getDescriptor().getUuid().equals(bluetoothGattDescriptor.getUuid())) {
            StringBuilder sb = new StringBuilder();
            sb.append("Received unexpected callback for descriptor ");
            sb.append(bluetoothGattDescriptor != null ? bluetoothGattDescriptor.getUuid() : "null");
            sb.append(" with request type = ");
            sb.append(Request.getRequestTypeLabel(i));
            Log.w("BLEService", sb.toString());
            return null;
        }
        Request access$800 = this.mTimeOutRequestRunnable.request;
        cancelTimeOutRequestRunnable();
        return access$800;
    }

    private void addToRequestsQueue(Request request) {
        if (request.getAttempts() < 2) {
            if (this.mShowDebugLogs) {
                Log.d("BLEService", "Add request of type " + Request.getRequestTypeLabel(request.getType()) + "to the Queue of requests to process.");
            }
            this.mRequestsQueue.add(request);
        } else {
            Log.w("BLEService", "Request " + Request.getRequestTypeLabel(request.getType()) + " failed after " + request.getAttempts() + " attempts.");
        }
        if (!this.isQueueProcessing) {
            processNextRequest();
        }
    }

    private void resetQueue() {
        if (this.mShowDebugLogs) {
            Log.d("BLEService", "Reset the Queue of requests to process.");
        }
        this.mRequestsQueue.clear();
        this.isQueueProcessing = false;
        cancelTimeOutRequestRunnable();
    }

    private void cancelTimeOutRequestRunnable() {
        TimeOutRequestRunnable timeOutRequestRunnable = this.mTimeOutRequestRunnable;
        if (timeOutRequestRunnable != null) {
            this.mHandler.removeCallbacks(timeOutRequestRunnable);
            this.mTimeOutRequestRunnable = null;
        }
    }

    /* access modifiers changed from: private */
    public void processNextRequest() {
        this.isQueueProcessing = true;
        if (this.mTimeOutRequestRunnable == null) {
            boolean z = false;
            if (this.mRequestsQueue.size() <= 0) {
                this.isQueueProcessing = false;
            } else if (this.mConnectionState != 2) {
                resetQueue();
            } else {
                Request remove = this.mRequestsQueue.remove();
                remove.increaseAttempts();
                if (this.mShowDebugLogs) {
                    Log.d("BLEService", "Processing request of type " + Request.getRequestTypeLabel(remove.getType()));
                }
                switch (remove.getType()) {
                    case 0:
                        processNotificationCharacteristicRequest(remove);
                        return;
                    case 1:
                        this.mTimeOutRequestRunnable = new TimeOutRequestRunnable(remove);
                        this.mHandler.postDelayed(this.mTimeOutRequestRunnable, (long) this.mDelay);
                        BluetoothGattCharacteristic buildReadCharacteristic = remove.buildReadCharacteristic();
                        if (buildReadCharacteristic != null && readCharacteristic(buildReadCharacteristic)) {
                            z = true;
                            break;
                        }
                    case 2:
                        this.mTimeOutRequestRunnable = new TimeOutRequestRunnable(remove);
                        this.mHandler.postDelayed(this.mTimeOutRequestRunnable, (long) this.mDelay);
                        BluetoothGattCharacteristic buildWriteCharacteristic = remove.buildWriteCharacteristic();
                        if (buildWriteCharacteristic != null && writeCharacteristic(buildWriteCharacteristic)) {
                            z = true;
                            break;
                        }
                    case 3:
                        this.mTimeOutRequestRunnable = new TimeOutRequestRunnable(remove);
                        this.mHandler.postDelayed(this.mTimeOutRequestRunnable, (long) this.mDelay);
                        BluetoothGattCharacteristic buildWriteNoResponseCharacteristic = remove.buildWriteNoResponseCharacteristic();
                        if (buildWriteNoResponseCharacteristic != null && writeCharacteristic(buildWriteNoResponseCharacteristic)) {
                            z = true;
                            break;
                        }
                    case 4:
                        this.mTimeOutRequestRunnable = new TimeOutRequestRunnable(remove);
                        this.mHandler.postDelayed(this.mTimeOutRequestRunnable, (long) this.mDelay);
                        BluetoothGattDescriptor buildReadDescriptor = remove.buildReadDescriptor();
                        if (buildReadDescriptor != null && readDescriptor(buildReadDescriptor)) {
                            z = true;
                            break;
                        }
                    case 5:
                        this.mTimeOutRequestRunnable = new TimeOutRequestRunnable(remove);
                        this.mHandler.postDelayed(this.mTimeOutRequestRunnable, (long) this.mDelay);
                        BluetoothGattDescriptor buildWriteDescriptor = remove.buildWriteDescriptor();
                        if (buildWriteDescriptor != null && writeDescriptor(buildWriteDescriptor)) {
                            z = true;
                            break;
                        }
                    case 6:
                        this.mTimeOutRequestRunnable = new TimeOutRequestRunnable(remove);
                        this.mHandler.postDelayed(this.mTimeOutRequestRunnable, (long) this.mDelay);
                        BluetoothGattCharacteristic buildReadCharacteristic2 = remove.buildReadCharacteristic();
                        if (buildReadCharacteristic2 != null && readCharacteristic(buildReadCharacteristic2)) {
                            z = true;
                            break;
                        }
                    case 7:
                        this.mTimeOutRequestRunnable = new TimeOutRequestRunnable(remove);
                        this.mHandler.postDelayed(this.mTimeOutRequestRunnable, (long) this.mDelay);
                        z = readRemoteRssi();
                        break;
                    case 8:
                        this.mTimeOutRequestRunnable = new TimeOutRequestRunnable(remove);
                        this.mHandler.postDelayed(this.mTimeOutRequestRunnable, (long) this.mDelay);
                        z = requestMTU(remove.getInteger());
                        break;
                }
                if (!z) {
                    cancelTimeOutRequestRunnable();
                    Log.w("BLEService", "Request " + Request.getRequestTypeLabel(remove.getType()) + " fails to process.");
                    onRequestFailed(remove);
                }
            }
        }
    }

    private void processNotificationCharacteristicRequest(Request request) {
        BluetoothGattCharacteristic buildNotifyCharacteristic = request.buildNotifyCharacteristic();
        boolean z = buildNotifyCharacteristic != null && setCharacteristicNotification(buildNotifyCharacteristic, request.getBooleanData());
        if (!z && request.getAttempts() < 2) {
            addToRequestsQueue(request);
            processNextRequest();
        } else if (z) {
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    BLEService.this.processNextRequest();
                }
            }, 1000);
        } else {
            request.setAttempts(2);
            this.mTimeOutRequestRunnable = new TimeOutRequestRunnable(request);
            this.mHandler.postDelayed(this.mTimeOutRequestRunnable, 1000);
        }
    }

    private class TimeOutRequestRunnable implements Runnable {
        /* access modifiers changed from: private */
        public final Request request;

        TimeOutRequestRunnable(Request request2) {
            this.request = request2;
        }

        public void run() {
            TimeOutRequestRunnable unused = BLEService.this.mTimeOutRequestRunnable = null;
            Log.w("BLEService", "Request " + Request.getRequestTypeLabel(this.request.getType()) + ": TIME OUT");
            BLEService.this.onRequestFailed(this.request);
        }
    }
}
