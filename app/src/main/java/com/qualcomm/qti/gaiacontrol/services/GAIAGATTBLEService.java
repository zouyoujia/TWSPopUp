package com.qualcomm.qti.gaiacontrol.services;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.IntRange;
import org.uplus.twspopup.utils.SplitStringUtils;
import com.qualcomm.qti.gaiacontrol.Consts;
import com.qualcomm.qti.gaiacontrol.Utils;
import com.qualcomm.qti.gaiacontrol.gaia.UpgradeGaiaManager;
import com.qualcomm.qti.gaiacontrol.models.gatt.GATT;
import com.qualcomm.qti.gaiacontrol.models.gatt.GATTServices;
import com.qualcomm.qti.gaiacontrol.models.gatt.GattServiceBattery;
import com.qualcomm.qti.gaiacontrol.receivers.BondStateReceiver;
import com.qualcomm.qti.gaiacontrol.rwcp.RWCPClient;
import com.qualcomm.qti.libraries.ble.BLEService;
import com.qualcomm.qti.libraries.ble.BLEUtils;
import com.qualcomm.qti.libraries.vmupgrade.UpgradeError;
import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class GAIAGATTBLEService extends BLEService implements BondStateReceiver.BondStateListener, UpgradeGaiaManager.GaiaManagerListener, BluetoothService, RWCPClient.RWCPListener {
    private static final int RSSI_WAITING_TIME = 1000;
    private final String TAG = "GAIAGATTBLEService";
    private final List<Handler> mAppListeners = new ArrayList();
    /* access modifiers changed from: private */
    public int mAttemptsForPairingInduction = 0;
    private final IBinder mBinder = new LocalBinder();
    private final BondStateReceiver mBondStateReceiver = new BondStateReceiver(this);
    /* access modifiers changed from: private */
    public boolean mBondingInitiated = false;
    /* access modifiers changed from: private */
    public final GATTServices mGattServices = new GATTServices();
    private final Handler mHandler = new Handler();
    private boolean mIsGaiaReady = false;
    private boolean mIsGattReady = false;
    private boolean mIsRWCPSupported = true;
    private final ArrayList<UUID> mNotifiedCharacteristics = new ArrayList<>();
    private final Queue<Double> mProgressQueue = new LinkedList();
    private final RWCPClient mRWCPClient = new RWCPClient(this);
    private final Runnable mRssiRunnable = new Runnable() {
        public void run() {
            if (GAIAGATTBLEService.this.mUpdateRssi) {
                GAIAGATTBLEService gAIAGATTBLEService = GAIAGATTBLEService.this;
                boolean unused = gAIAGATTBLEService.mUpdateRssi = gAIAGATTBLEService.requestReadRssi();
            }
        }
    };
    private boolean mShowDebugLogs = Consts.DEBUG;
    private long mTransferStartTime = 0;
    /* access modifiers changed from: private */
    public boolean mUpdateRssi = false;
    private UpgradeGaiaManager mUpgradeGaiaManager;

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GattMessage {
        public static final int BATTERY_LEVEL_UPDATE = 3;
        public static final int BODY_SENSOR_LOCATION = 5;
        public static final int GATT_STATE = 11;
        public static final int HEART_RATE_MEASUREMENT = 4;
        public static final int LINK_LOSS_ALERT_LEVEL = 1;
        public static final int MTU_SUPPORTED = 9;
        public static final int MTU_UPDATED = 10;
        public static final int RSSI_LEVEL = 2;
        public static final int RWCP_ENABLED = 7;
        public static final int RWCP_SUPPORTED = 6;
        public static final int TRANSFER_FAILED = 8;
        public static final int TX_POWER_LEVEL = 0;
    }

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GattState {
        public static final int DISCOVERING_SERVICES = 0;
        public static final int IN_USE_BY_SYSTEM = 1;
    }

    public int getTransport() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public void onCharacteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
    }

    public boolean startRssiUpdates(boolean z) {
        return false;
    }

    static /* synthetic */ int access$408(GAIAGATTBLEService gAIAGATTBLEService) {
        int i = gAIAGATTBLEService.mAttemptsForPairingInduction;
        gAIAGATTBLEService.mAttemptsForPairingInduction = i + 1;
        return i;
    }

    public synchronized void addHandler(Handler handler) {
        if (!this.mAppListeners.contains(handler)) {
            this.mAppListeners.add(handler);
        }
    }

    public synchronized void removeHandler(Handler handler) {
        if (this.mAppListeners.contains(handler)) {
            this.mAppListeners.remove(handler);
        }
    }

    public void disconnectDevice() {
        if (isDisconnected()) {
            resetDeviceInformation();
            return;
        }
        unregisterNotifications();
        disconnectFromDevice();
    }

    public int getBondState() {
        BluetoothDevice device = getDevice();
        if (device != null) {
            return device.getBondState();
        }
        return 10;
    }

    public void enableDebugLogs(boolean z) {
        showDebugLogs(z);
        this.mShowDebugLogs = z;
        this.mRWCPClient.showDebugLogs(z);
        UpgradeGaiaManager upgradeGaiaManager = this.mUpgradeGaiaManager;
        if (upgradeGaiaManager != null) {
            upgradeGaiaManager.enableDebugLogs(z);
        }
    }

    public GATTServices getGattSupport() {
        return this.mGattServices;
    }

    public boolean sendGAIAPacket(byte[] bArr) {
        return sendGaiaCommandEndpoint(bArr);
    }

    public boolean isGaiaReady() {
        return this.mIsGaiaReady;
    }

    public boolean isGattReady() {
        return this.mIsGattReady;
    }

    public boolean enableMaximumMTU(boolean z) {
        return requestMTUSize(z ? 256 : 23);
    }

    public boolean enableRWCP(boolean z) {
        if (this.mIsRWCPSupported || !z) {
            this.mUpgradeGaiaManager.setRWCPMode(z);
            return true;
        }
        Log.w("GAIAGATTBLEService", "Request to enable or disable RWCP received but the feature is not supported.");
        return false;
    }

    public void getRWCPStatus() {
        if (this.mIsRWCPSupported) {
            this.mUpgradeGaiaManager.getRWCPStatus();
        } else {
            Log.w("GAIAGATTBLEService", "getRWCPStatus(): RWCP is not supported, cannot get its status.");
        }
    }

    public void startUpgrade(File file) {
        if (this.mUpgradeGaiaManager != null) {
            super.getBluetoothGatt().requestConnectionPriority(1);
            this.mUpgradeGaiaManager.startUpgrade(file);
            this.mProgressQueue.clear();
            this.mTransferStartTime = 0;
            return;
        }
        Log.e("GAIAGATTBLEService", "Upgrade has not been enabled.");
    }

    public int getResumePoint() {
        UpgradeGaiaManager upgradeGaiaManager = this.mUpgradeGaiaManager;
        if (upgradeGaiaManager != null) {
            return upgradeGaiaManager.getResumePoint();
        }
        return 0;
    }

    public void abortUpgrade() {
        if (this.mUpgradeGaiaManager != null) {
            if (this.mRWCPClient.isRunningASession()) {
                this.mRWCPClient.cancelTransfer();
            }
            this.mProgressQueue.clear();
            this.mUpgradeGaiaManager.abortUpgrade();
        }
    }

    public boolean isUpgrading() {
        UpgradeGaiaManager upgradeGaiaManager = this.mUpgradeGaiaManager;
        return upgradeGaiaManager != null && upgradeGaiaManager.isUpgrading();
    }

    public void sendConfirmation(int i, boolean z) {
        UpgradeGaiaManager upgradeGaiaManager = this.mUpgradeGaiaManager;
        if (upgradeGaiaManager != null) {
            upgradeGaiaManager.sendConfirmation(i, z);
        }
    }

    public boolean requestLinkLossAlertLevel() {
        return this.mGattServices.gattServiceLinkLoss.isSupported() && requestReadCharacteristic(this.mGattServices.gattServiceLinkLoss.getAlertLevelCharacteristic());
    }

    public boolean requestTxPowerLevel() {
        return this.mGattServices.gattServicetxPower.isSupported() && requestReadCharacteristic(this.mGattServices.gattServicetxPower.getTxPowerLevelCharacteristic());
    }

    public boolean requestBatteryLevels() {
        if (!this.mGattServices.isBatteryServiceSupported()) {
            return false;
        }
        boolean z = true;
        for (int i = 0; i < this.mGattServices.gattServiceBatteries.size(); i++) {
            if (!requestReadCharacteristic(this.mGattServices.gattServiceBatteries.get(this.mGattServices.gattServiceBatteries.keyAt(i)).getBatteryLevelCharacteristic())) {
                z = false;
            }
        }
        return z;
    }

    public boolean requestBodySensorLocation() {
        return this.mGattServices.gattServiceHeartRate.isBodySensorLocationCharacteristicAvailable() && requestReadCharacteristic(this.mGattServices.gattServiceHeartRate.getBodySensorLocationCharacteristic());
    }

    public boolean sendLinkLossAlertLevel(@IntRange(from = 0, to = 2) int i) {
        if (!this.mGattServices.gattServiceLinkLoss.isSupported()) {
            return false;
        }
        return requestWriteCharacteristic(this.mGattServices.gattServiceLinkLoss.getAlertLevelCharacteristic(), new byte[]{(byte) i});
    }

    public boolean sendImmediateAlertLevel(@IntRange(from = 0, to = 2) int i) {
        if (!this.mGattServices.gattServiceimmediateAlert.isSupported()) {
            return false;
        }
        return requestWriteNoResponseCharacteristic(this.mGattServices.gattServiceimmediateAlert.getAlertLevelCharacteristic(), new byte[]{(byte) i});
    }

    public boolean sendHeartRateControlPoint(byte b) {
        if (!this.mGattServices.gattServiceHeartRate.isHeartRateControlPointCharacteristicAvailable()) {
            return false;
        }
        return requestWriteCharacteristic(this.mGattServices.gattServiceHeartRate.getHeartRateControlPointCharacteristic(), new byte[]{b});
    }

    public boolean requestHeartMeasurementNotifications(boolean z) {
        if (!this.mGattServices.gattServiceHeartRate.isHeartRateMeasurementCharacteristicAvailable() || !this.mGattServices.gattServiceHeartRate.isClientCharacteristicConfigurationDescriptorAvailable()) {
            return false;
        }
        return requestCharacteristicNotification(this.mGattServices.gattServiceHeartRate.getHeartRateMeasurementCharacteristic(), z);
    }

    public void enableUpgrade(boolean z) {
        if (z && this.mUpgradeGaiaManager == null) {
            this.mUpgradeGaiaManager = new UpgradeGaiaManager(this, 0);
            this.mUpgradeGaiaManager.enableDebugLogs(this.mShowDebugLogs);
        } else if (!z) {
            this.mUpgradeGaiaManager = null;
        }
    }

    public IBinder onBind(Intent intent) {
        if (this.mShowDebugLogs) {
            Log.i("GAIAGATTBLEService", "Service bound");
        }
        return this.mBinder;
    }

    public boolean onUnbind(Intent intent) {
        if (this.mShowDebugLogs) {
            Log.i("GAIAGATTBLEService", "Service unbound");
        }
        if (this.mAppListeners.isEmpty()) {
            disconnectDevice();
        }
        return super.onUnbind(intent);
    }

    public void onCreate() {
        super.onCreate();
        showDebugLogs(Consts.DEBUG);
        this.mRWCPClient.showDebugLogs(Consts.DEBUG);
        initialize();
        setDelayForRequest(60000);
        registerBondReceiver();
    }

    public void onDestroy() {
        disconnectDevice();
        unregisterBondReceiver();
        if (this.mShowDebugLogs) {
            Log.i("GAIAGATTBLEService", "Service destroyed");
        }
        super.onDestroy();
    }

    public void onVMUpgradeDisconnected() {
        if (!isUpgrading()) {
            this.mUpgradeGaiaManager.reset();
            this.mUpgradeGaiaManager = null;
        }
    }

    public void onResumePointChanged(int i) {
        sendMessageToListener(7, 2, Integer.valueOf(i));
    }

    public void onUpgradeError(UpgradeError upgradeError) {
        Log.e("GAIAGATTBLEService", "ERROR during upgrade: " + upgradeError.getString());
        sendMessageToListener(7, 3, upgradeError);
        if (this.mRWCPClient.isRunningASession()) {
            this.mRWCPClient.cancelTransfer();
            this.mProgressQueue.clear();
        }
    }

    public void onUploadProgress(double d) {
        if (this.mUpgradeGaiaManager.isRWCPEnabled()) {
            this.mProgressQueue.add(Double.valueOf(d));
        } else {
            sendMessageToListener(7, 4, Double.valueOf(d));
        }
    }

    public boolean sendGAIAUpgradePacket(byte[] bArr, boolean z) {
        if (!this.mUpgradeGaiaManager.isRWCPEnabled() || !z) {
            return sendGaiaCommandEndpoint(bArr);
        }
        if (this.mTransferStartTime <= 0) {
            this.mTransferStartTime = System.currentTimeMillis();
        }
        return this.mRWCPClient.sendData(bArr);
    }

    public void onUpgradeFinish() {
        sendMessageToListener(7, 0, (Object) null);
    }

    public void askConfirmation(int i) {
        if (!sendMessageToListener(7, 1, Integer.valueOf(i))) {
            sendConfirmation(i, true);
        }
    }

    public void onRWCPEnabled(boolean z) {
        requestCharacteristicNotification(this.mGattServices.gattServiceGaia.getGaiaDataCharacteristic(), z);
    }

    public void onRWCPNotSupported() {
        this.mIsRWCPSupported = false;
        sendMessageToListener(6, 6, false);
    }

    public boolean sendRWCPSegment(byte[] bArr) {
        boolean requestWriteNoResponseCharacteristic = requestWriteNoResponseCharacteristic(this.mGattServices.gattServiceGaia.getGaiaDataCharacteristic(), bArr);
        if (requestWriteNoResponseCharacteristic && this.mShowDebugLogs) {
            Log.i("GAIAGATTBLEService", "Attempt to send RWCP segment on DATA ENDPOINT characteristic: " + Utils.getStringFromBytes(bArr));
        } else if (!requestWriteNoResponseCharacteristic) {
            Log.w("GAIAGATTBLEService", "Attempt to send RWCP segment on DATA ENDPOINT characteristic FAILED: " + Utils.getStringFromBytes(bArr));
        }
        return requestWriteNoResponseCharacteristic;
    }

    public void onTransferFailed() {
        abortUpgrade();
        sendMessageToListener(6, 8);
    }

    public void onTransferFinished() {
        this.mUpgradeGaiaManager.onTransferFinished();
        this.mProgressQueue.clear();
    }

    public void onTransferProgress(int i) {
        if (i > 0) {
            double d = 0.0d;
            while (i > 0 && !this.mProgressQueue.isEmpty()) {
                d = this.mProgressQueue.poll().doubleValue();
                i--;
            }
            sendMessageToListener(7, 4, Double.valueOf(d));
        }
    }

    public BluetoothDevice getDevice() {
        return super.getDevice();
    }

    @SuppressLint({"WrongConstant"})
    public int getConnectionState() {
        switch (super.getConnectionState()) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            default:
                return 0;
        }
    }

    public boolean connectToDevice(String str) {
        return super.connectToDevice(str);
    }

    public void onBondStateChange(BluetoothDevice bluetoothDevice, int i) {
        BluetoothDevice device = getDevice();
        if (bluetoothDevice != null && device != null && bluetoothDevice.getAddress().equals(device.getAddress())) {
            Log.i("GAIAGATTBLEService", "ACTION_BOND_STATE_CHANGED for " + bluetoothDevice.getAddress() + " with bond state " + BLEUtils.getBondStateName(i));
            sendMessageToListener(1, Integer.valueOf(i));
            if (i == 12) {
                sendMessageToListener(6, 11, 1);
                onGattReady();
            } else if (i == 11) {
                this.mBondingInitiated = true;
            }
        }
    }

    private boolean sendMessageToListener(int i) {
        if (!this.mAppListeners.isEmpty()) {
            for (int i2 = 0; i2 < this.mAppListeners.size(); i2++) {
                this.mAppListeners.get(i2).obtainMessage(i).sendToTarget();
            }
        }
        return !this.mAppListeners.isEmpty();
    }

    private boolean sendMessageToListener(int i, Object obj) {
        if (!this.mAppListeners.isEmpty()) {
            for (int i2 = 0; i2 < this.mAppListeners.size(); i2++) {
                this.mAppListeners.get(i2).obtainMessage(i, obj).sendToTarget();
            }
        }
        return !this.mAppListeners.isEmpty();
    }

    private boolean sendMessageToListener(int i, int i2, Object obj) {
        if (!this.mAppListeners.isEmpty()) {
            for (int i3 = 0; i3 < this.mAppListeners.size(); i3++) {
                this.mAppListeners.get(i3).obtainMessage(i, i2, 0, obj).sendToTarget();
            }
        }
        return !this.mAppListeners.isEmpty();
    }

    /* access modifiers changed from: protected */
    public synchronized void setState(int i) {
        super.setState(i);
        int i2 = 3;
        if (i == 2) {
            i2 = 2;
        } else if (i == 1) {
            i2 = 1;
        } else if (i != 3) {
            i2 = 0;
        }
        sendMessageToListener(0, Integer.valueOf(i2));
    }

    /* access modifiers changed from: protected */
    public void onConnectionStateChange(BluetoothGatt bluetoothGatt, int i, int i2) {
        Log.i("GAIAGATTBLEService", "onConnectionStateChange: " + BLEUtils.getGattStatusName(i, true));
        if (i == 0 && i2 == 2) {
            sendMessageToListener(6, 11, 0);
            Log.i("GAIAGATTBLEService", "Attempting to start service discovery: " + bluetoothGatt.discoverServices());
        } else if (i2 == 0) {
            resetDeviceInformation();
            if (isUpgrading()) {
                this.mHandler.postDelayed(new Runnable() {
                    public void run() {
                        GAIAGATTBLEService.this.reconnectToDevice();
                    }
                }, 1000);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
        if (i == 0) {
            this.mGattServices.setSupportedGattServices(bluetoothGatt.getServices());
            sendMessageToListener(2, this.mGattServices);
            if (this.mGattServices.gattServiceGaia.isSupported()) {
                requestReadCharacteristicForPairing(this.mGattServices.gattServiceGaia.getGaiaDataCharacteristic());
            } else {
                onGattReady();
            }
            if (this.mShowDebugLogs) {
                Log.i("GAIAGATTBLEService", this.mGattServices.toString());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
        if (bluetoothGattCharacteristic != null) {
            UUID uuid = bluetoothGattCharacteristic.getUuid();
            if (this.mIsGattReady || !uuid.equals(GATT.UUIDs.CHARACTERISTIC_GAIA_DATA_ENDPOINT_UUID)) {
                if (i == 0 && uuid.equals(GATT.UUIDs.CHARACTERISTIC_ALERT_LEVEL_UUID) && bluetoothGattCharacteristic.getService().getUuid().equals(GATT.UUIDs.SERVICE_LINK_LOSS_UUID)) {
                    sendMessageToListener(6, 1, Integer.valueOf(bluetoothGattCharacteristic.getIntValue(17, 0).intValue()));
                } else if (i == 0 && uuid.equals(GATT.UUIDs.CHARACTERISTIC_TX_POWER_LEVEL_UUID)) {
                    sendMessageToListener(6, 0, Integer.valueOf(bluetoothGattCharacteristic.getIntValue(33, 0).intValue()));
                } else if (i == 0 && uuid.equals(GATT.UUIDs.CHARACTERISTIC_BATTERY_LEVEL_UUID)) {
                    sendMessageToListener(6, 3, Integer.valueOf(bluetoothGattCharacteristic.getService().getInstanceId()));
                } else if (i == 0 && uuid.equals(GATT.UUIDs.CHARACTERISTIC_BODY_SENSOR_LOCATION_UUID)) {
                    sendMessageToListener(6, 5, Integer.valueOf(bluetoothGattCharacteristic.getValue()[0]));
                }
            } else if (i == 0) {
                if (this.mShowDebugLogs) {
                    Log.i("GAIAGATTBLEService", "Successful read characteristic to induce pairing: no need to bond device.");
                }
                onGattReady();
            } else if (i != 15 && i != 5 && i != 8 && i != 137 && i != 133 && i != 47) {
            } else {
                if (this.mAttemptsForPairingInduction < 3) {
                    this.mHandler.postDelayed(new Runnable() {
                        public void run() {
                            if (GAIAGATTBLEService.this.mBondingInitiated || !GAIAGATTBLEService.this.isConnected()) {
                                int unused = GAIAGATTBLEService.this.mAttemptsForPairingInduction = 0;
                                return;
                            }
                            GAIAGATTBLEService.access$408(GAIAGATTBLEService.this);
                            GAIAGATTBLEService gAIAGATTBLEService = GAIAGATTBLEService.this;
                            boolean unused2 = gAIAGATTBLEService.requestReadCharacteristicForPairing(gAIAGATTBLEService.mGattServices.gattServiceGaia.getGaiaDataCharacteristic());
                        }
                    }, 1000);
                    return;
                }
                this.mAttemptsForPairingInduction = 0;
                if (isUpgrading()) {
                    Log.w("GAIAGATTBLEService", "Unsuccessful READ characteristic to induce pairing after 3 attempts, aborting upgrade.");
                    abortUpgrade();
                    sendMessageToListener(7, 3, 4);
                } else {
                    Log.w("GAIAGATTBLEService", "Unsuccessful READ characteristic to induce pairing after 3 attempts, disconnecting device.");
                }
                disconnectDevice();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onReceivedCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        if (bluetoothGattCharacteristic != null) {
            UUID uuid = bluetoothGattCharacteristic.getUuid();
            if (uuid.equals(GATT.UUIDs.CHARACTERISTIC_GAIA_RESPONSE_UUID)) {
                byte[] value = bluetoothGattCharacteristic.getValue();
                if (value != null) {
                    UpgradeGaiaManager upgradeGaiaManager = this.mUpgradeGaiaManager;
                    if (upgradeGaiaManager != null) {
                        upgradeGaiaManager.onReceiveGAIAPacket(value);
                    } else {
                        sendMessageToListener(3, value);
                    }
                }
            } else if (uuid.equals(GATT.UUIDs.CHARACTERISTIC_HEART_RATE_MEASUREMENT_UUID)) {
                sendMessageToListener(6, 4, this.mGattServices.gattServiceHeartRate.getHeartRateMeasurementValues());
            } else if (uuid.equals(GATT.UUIDs.CHARACTERISTIC_GAIA_DATA_ENDPOINT_UUID)) {
                byte[] value2 = bluetoothGattCharacteristic.getValue();
                if (value2 != null) {
                    this.mRWCPClient.onReceiveRWCPSegment(value2);
                }
            } else if (this.mShowDebugLogs) {
                Log.i("GAIAGATTBLEService", "Received notification over characteristic: " + bluetoothGattCharacteristic.getUuid());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDescriptorWrite(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
        UUID uuid = bluetoothGattDescriptor.getUuid();
        UUID uuid2 = bluetoothGattDescriptor.getCharacteristic().getUuid();
        if (i == 0) {
            this.mNotifiedCharacteristics.add(uuid2);
            if (this.mShowDebugLogs) {
                Log.i("GAIAGATTBLEService", "Successful write descriptor " + uuid.toString() + " for characteristic " + uuid2.toString());
            }
        } else {
            Log.w("GAIAGATTBLEService", "Unsuccessful write descriptor " + uuid.toString() + " for characteristic " + uuid2.toString() + " with status " + BLEUtils.getGattStatusName(i, false));
        }
        if (i == 0 && this.mGattServices.gattServiceGaia.isSupported() && uuid.equals(GATT.UUIDs.DESCRIPTOR_CLIENT_CHARACTERISTIC_CONFIGURATION_UUID) && uuid2.equals(GATT.UUIDs.CHARACTERISTIC_GAIA_RESPONSE_UUID)) {
            this.mIsGaiaReady = true;
            sendMessageToListener(4);
            if (isUpgrading()) {
                this.mUpgradeGaiaManager.onGaiaReady();
            }
        } else if (i != 0 || !this.mGattServices.gattServiceHeartRate.isSupported() || !uuid2.equals(GATT.UUIDs.CHARACTERISTIC_HEART_RATE_MEASUREMENT_UUID)) {
            if (this.mGattServices.gattServiceGaia.isRWCPTransportSupported() && uuid.equals(GATT.UUIDs.DESCRIPTOR_CLIENT_CHARACTERISTIC_CONFIGURATION_UUID) && uuid2.equals(GATT.UUIDs.CHARACTERISTIC_GAIA_DATA_ENDPOINT_UUID)) {
                if (i == 0) {
                    sendMessageToListener(6, 7, Boolean.valueOf(Arrays.equals(bluetoothGattDescriptor.getValue(), BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)));
                    return;
                }
                this.mIsRWCPSupported = false;
                this.mUpgradeGaiaManager.onRWCPNotSupported();
                sendMessageToListener(6, 6, false);
            }
        } else if (this.mShowDebugLogs) {
            Log.d("GAIAGATTBLEService", "Received successful onDescriptorWrite for Heart Rate Measurement");
        }
    }

    /* access modifiers changed from: protected */
    public void onRemoteRssiRead(BluetoothGatt bluetoothGatt, int i, int i2) {
        if (i2 == 0 && this.mUpdateRssi) {
            sendMessageToListener(6, 2, Integer.valueOf(i));
            this.mHandler.postDelayed(this.mRssiRunnable, 1000);
        }
    }

    /* access modifiers changed from: protected */
    public void onMTUChanged(BluetoothGatt bluetoothGatt, int i, int i2) {
        if (i2 == 0) {
            Log.i("GAIAGATTBLEService", "MTU size had been updated to " + i);
            sendMessageToListener(6, 10, Integer.valueOf(i));
        } else {
            Log.w("GAIAGATTBLEService", "MTU request failed, mtu size is: " + i);
            sendMessageToListener(6, 9, false);
        }
        this.mUpgradeGaiaManager.setPacketMaximumSize(i - 3);
    }

    /* access modifiers changed from: protected */
    public void onDescriptorRead(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
        if (i == 0 && bluetoothGattDescriptor.getCharacteristic().getUuid().equals(GATT.UUIDs.CHARACTERISTIC_BATTERY_LEVEL_UUID)) {
            GattServiceBattery gattServiceBattery = this.mGattServices.gattServiceBatteries.get(Integer.valueOf(bluetoothGattDescriptor.getCharacteristic().getService().getInstanceId()));
            if (gattServiceBattery != null) {
                gattServiceBattery.updateDescription();
            }
        }
    }

    public boolean reconnectToDevice() {
        return super.reconnectToDevice();
    }

    /* access modifiers changed from: private */
    public boolean isConnected() {
        return super.getConnectionState() == 2;
    }

    private boolean isDisconnected() {
        return super.getConnectionState() == 0;
    }

    private void resetDeviceInformation() {
        this.mIsGattReady = false;
        this.mIsGaiaReady = false;
        this.mUpdateRssi = false;
        this.mBondingInitiated = false;
        this.mAttemptsForPairingInduction = 0;
        UpgradeGaiaManager upgradeGaiaManager = this.mUpgradeGaiaManager;
        if (upgradeGaiaManager != null) {
            upgradeGaiaManager.reset();
        }
        this.mRWCPClient.cancelTransfer();
        this.mProgressQueue.clear();
        this.mNotifiedCharacteristics.clear();
    }

    private void registerBondReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
        registerReceiver(this.mBondStateReceiver, intentFilter);
    }

    private void unregisterBondReceiver() {
        unregisterReceiver(this.mBondStateReceiver);
    }

    private void unregisterNotifications() {
        for (int i = 0; i < this.mNotifiedCharacteristics.size(); i++) {
            requestCharacteristicNotification(this.mNotifiedCharacteristics.get(i), false);
        }
    }

    private void onGattReady() {
        this.mIsGattReady = true;
        if (this.mShowDebugLogs) {
            Log.i("GAIAGATTBLEService", "GATT connection is ready to be used.");
        }
        sendMessageToListener(5);
        if (this.mGattServices.gattServiceGaia.isSupported()) {
            if (this.mShowDebugLogs) {
                Log.i("GAIAGATTBLEService", "GAIA is supported, start request for GAIA notifications.");
            }
            requestCharacteristicNotification(this.mGattServices.gattServiceGaia.getGaiaResponseCharacteristic(), true);
        }
        if (this.mGattServices.isBatteryServiceSupported()) {
            for (int i = 0; i < this.mGattServices.gattServiceBatteries.size(); i++) {
                if (this.mShowDebugLogs) {
                    Log.i("GAIAGATTBLEService", "Battery service is supported, request presentation format descriptors for service " + (i + 1) + SplitStringUtils.SPLIT_FILE_SUFFIX);
                }
                GattServiceBattery gattServiceBattery = this.mGattServices.gattServiceBatteries.get(this.mGattServices.gattServiceBatteries.keyAt(i));
                if (gattServiceBattery.isPresentationFormatDescriptorAvailable()) {
                    requestReadDescriptor(gattServiceBattery.getPresentationFormatDescriptor());
                }
            }
        }
    }

    private boolean sendGaiaCommandEndpoint(byte[] bArr) {
        if (this.mGattServices.gattServiceGaia.isCharacteristicGaiaCommandAvailable()) {
            return requestWriteCharacteristic(this.mGattServices.gattServiceGaia.getGaiaCommandCharacteristic(), bArr);
        }
        Log.w("GAIAGATTBLEService", "Attempt to send data over CHARACTERISTIC_CSR_GAIA_COMMAND_ENDPOINT failed: characteristic not available.");
        return false;
    }

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public GAIAGATTBLEService getService() {
            return GAIAGATTBLEService.this;
        }
    }
}
