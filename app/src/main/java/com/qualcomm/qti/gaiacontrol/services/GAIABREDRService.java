package com.qualcomm.qti.gaiacontrol.services;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.qualcomm.qti.gaiacontrol.Consts;
import com.qualcomm.qti.gaiacontrol.models.gatt.GATTServices;
import com.qualcomm.qti.gaiacontrol.receivers.BondStateReceiver;
import com.qualcomm.qti.libraries.ble.BLEUtils;

import org.uplus.twspopup.BuildConfig;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class GAIABREDRService extends Service implements BluetoothService, BondStateReceiver.BondStateListener {
    private static final int RECONNECTION_TIMER_MS = 1000;
    private final String TAG = "GAIABREDRService";
    private final List<Handler> mAppListeners = new ArrayList();
    private final IBinder mBinder = new LocalBinder();
    private final BondStateReceiver mBondStateReceiver = new BondStateReceiver(this);
    private GAIABREDRProvider mGAIABREDRProvider;
    private final Handler mHandler = new Handler();
    private final ProviderHandler mProviderHandler = new ProviderHandler(this);
    private boolean mShowDebugLogs = Consts.DEBUG;

    public GATTServices getGattSupport() {
        return null;
    }

    public int getTransport() {
        return 1;
    }

    public boolean isGattReady() {
        return false;
    }

    public boolean requestBatteryLevels() {
        return false;
    }

    public boolean requestBodySensorLocation() {
        return false;
    }

    public boolean requestHeartMeasurementNotifications(boolean z) {
        return false;
    }

    public boolean requestLinkLossAlertLevel() {
        return false;
    }

    public boolean requestTxPowerLevel() {
        return false;
    }

    public boolean sendHeartRateControlPoint(byte b) {
        return false;
    }

    public boolean sendImmediateAlertLevel(int i) {
        return false;
    }

    public boolean sendLinkLossAlertLevel(int i) {
        return false;
    }

    public boolean startRssiUpdates(boolean z) {
        return false;
    }

    public IBinder onBind(Intent intent) {
        if (this.mShowDebugLogs) {
            Log.i("GAIABREDRService", "Service bound");
        }
        registerBondReceiver();
        return this.mBinder;
    }

    public boolean onUnbind(Intent intent) {
        if (this.mShowDebugLogs) {
            Log.i("GAIABREDRService", "Service unbound");
        }
        unregisterBondReceiver();
        if (this.mAppListeners.isEmpty() && !isUpgrading()) {
            disconnectDevice();
        }
        return super.onUnbind(intent);
    }

    public void onCreate() {
        super.onCreate();
        if (this.mGAIABREDRProvider == null) {
            this.mGAIABREDRProvider = new GAIABREDRProvider(this.mProviderHandler, (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE));
        }
        this.mGAIABREDRProvider.showDebugLogs(this.mShowDebugLogs);
    }

    public void onDestroy() {
        disconnectDevice();
        if (this.mShowDebugLogs) {
            Log.i("GAIABREDRService", "Service destroyed");
        }
        super.onDestroy();
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

    public boolean connectToDevice(String str) {
        if (this.mGAIABREDRProvider.getState() != 2) {
            return this.mGAIABREDRProvider.connect(str, (Context) this);
        }
        Log.w("GAIABREDRService", "connection failed: a device is already connected.");
        return false;
    }

    public boolean reconnectToDevice() {
        return this.mGAIABREDRProvider.reconnectToDevice(this);
    }

    public void disconnectDevice() {
        this.mGAIABREDRProvider.disconnect();
    }

    public void enableDebugLogs(boolean z) {
        this.mShowDebugLogs = z;
        this.mGAIABREDRProvider.enableDebugLogs(z);
    }

    public int getBondState() {
        BluetoothDevice device = getDevice();
        if (device != null) {
            return device.getBondState();
        }
        return 10;
    }

    public BluetoothDevice getDevice() {
        return this.mGAIABREDRProvider.getDevice();
    }

    public int getConnectionState() {
        switch (this.mGAIABREDRProvider.getState()) {
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

    public boolean sendGAIAPacket(byte[] bArr) {
        return this.mGAIABREDRProvider.sendData(bArr);
    }

    public boolean isGaiaReady() {
        return this.mGAIABREDRProvider.isGaiaReady();
    }

    public void startUpgrade(File file) {
        this.mGAIABREDRProvider.startUpgrade(file);
    }

    public int getResumePoint() {
        return this.mGAIABREDRProvider.getResumePoint();
    }

    public void abortUpgrade() {
        this.mGAIABREDRProvider.abortUpgrade();
    }

    public boolean isUpgrading() {
        return this.mGAIABREDRProvider.isUpgrading();
    }

    public void sendConfirmation(int i, boolean z) {
        this.mGAIABREDRProvider.sendConfirmation(i, z);
    }

    public void enableUpgrade(boolean z) {
        this.mGAIABREDRProvider.enableUpgrade(z);
    }

    public void onBondStateChange(BluetoothDevice bluetoothDevice, int i) {
        BluetoothDevice device = getDevice();
        if (bluetoothDevice != null && device != null && bluetoothDevice.getAddress().equals(device.getAddress())) {
            if (this.mShowDebugLogs) {
                Log.i("GAIABREDRService", "ACTION_BOND_STATE_CHANGED for " + bluetoothDevice.getAddress() + " with bond state " + BLEUtils.getBondStateName(i));
            }
            sendMessageToListener(1, Integer.valueOf(i));
            if (i == 12) {
                bluetoothDevice.fetchUuidsWithSdp();
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

    /* access modifiers changed from: private */
    public void handleMessageFromProvider(Message message) {
        String str;
        switch (message.what) {
            case 0:
                int intValue = ((Integer) message.obj).intValue();
                if (this.mShowDebugLogs) {
                    Log.i("GAIABREDRService", "Handle a message from BR/EDR Provider: " + "CONNECTION_STATE_HAS_CHANGED: " + (intValue == 2 ? "CONNECTED" : intValue == 1 ? "CONNECTING" : intValue == 3 ? "DISCONNECTING" : intValue == 0 ? "DISCONNECTED" : "UNKNOWN"));
                }
                onConnectionStateHasChanged(intValue);
                return;
            case 1:
                byte[] bArr = (byte[]) message.obj;
                if (this.mShowDebugLogs) {
                    Log.i("GAIABREDRService", "Handle a message from BR/EDR Provider: " + "GAIA_PACKET");
                }
                onGaiaDataReceived(bArr);
                return;
            case 2:
                int intValue2 = ((Integer) message.obj).intValue();
                if (intValue2 == 0) {
                    str = "CONNECTION_FAILED";
                } else if (intValue2 == 1) {
                    str = "CONNECTION_LOST";
                } else {
                    str = "UNKNOWN " + intValue2;
                }
                Log.w("GAIABREDRService", "Handle a message from BR/EDR Provider: " + "ERROR: " + str);
                onErrorReceived(intValue2);
                return;
            case 3:
                if (this.mShowDebugLogs) {
                    Log.i("GAIABREDRService", "Handle a message from BR/EDR Provider: " + "GAIA_READY");
                }
                sendMessageToListener(4);
                return;
            case 4:
                sendMessageToListener(7, message.arg1, message.obj);
                return;
            default:
                if (this.mShowDebugLogs) {
                    Log.d("GAIABREDRService", "Handle a message from BR/EDR Provider: " + "UNKNOWN MESSAGE: " + message.what + " obj: " + message.obj);
                    return;
                }
                return;
        }
    }

    private void onConnectionStateHasChanged(int i) {
        int i2 = 3;
        if (i == 2) {
            i2 = 2;
        } else if (i == 1) {
            i2 = 1;
        } else if (i != 3) {
            i2 = 0;
        }
        sendMessageToListener(0, Integer.valueOf(i2));
        if (i == 0) {
            isUpgrading();
        }
    }

    private void onErrorReceived(int i) {
        switch (i) {
        }
        Log.e("GAIABREDRService", "On error received : " + BuildConfig.APPLICATION_ID);
    }

    private void onGaiaDataReceived(byte[] bArr) {
        sendMessageToListener(3, bArr);
    }

    private void registerBondReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
        registerReceiver(this.mBondStateReceiver, intentFilter);
    }

    private void unregisterBondReceiver() {
        unregisterReceiver(this.mBondStateReceiver);
    }

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public GAIABREDRService getService() {
            return GAIABREDRService.this;
        }
    }

    private static class ProviderHandler extends Handler {
        final WeakReference<GAIABREDRService> mmReference;

        ProviderHandler(GAIABREDRService gAIABREDRService) {
            this.mmReference = new WeakReference<>(gAIABREDRService);
        }

        public void handleMessage(Message message) {
            GAIABREDRService gAIABREDRService = (GAIABREDRService) this.mmReference.get();
            if (gAIABREDRService == null) {
                Log.e("GAIABREDRService", "handleMessage() service is null");
            } else {
                gAIABREDRService.handleMessageFromProvider(message);
            }
        }
    }
}
