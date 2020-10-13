package com.qualcomm.qti.gaiacontrol.services;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothManager;
import android.os.Handler;
import android.util.Log;
import androidx.annotation.NonNull;
import org.uplus.twspopup.utils.SplitStringUtils;
import com.qualcomm.qti.gaiacontrol.Consts;
import com.qualcomm.qti.gaiacontrol.Utils;
import com.qualcomm.qti.gaiacontrol.gaia.UpgradeGaiaManager;
import com.qualcomm.qti.libraries.vmupgrade.UpgradeError;
import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

class GAIABREDRProvider extends BREDRProvider implements UpgradeGaiaManager.GaiaManagerListener {
    private final String TAG = "GAIABREDRProvider";
    private final DataAnalyser mAnalyser = new DataAnalyser();
    private final Handler mHandler = new Handler();
    private final Handler mListener;
    private boolean mShowDebugLogs = Consts.DEBUG;
    /* access modifiers changed from: private */
    public UpgradeGaiaManager mUpgradeGaiaManager;

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    @interface Messages {
        public static final int CONNECTION_STATE_HAS_CHANGED = 0;
        public static final int ERROR = 2;
        public static final int GAIA_PACKET = 1;
        public static final int GAIA_READY = 3;
        public static final int UPGRADE_MESSAGE = 4;
    }

    public void onRWCPEnabled(boolean z) {
    }

    public void onRWCPNotSupported() {
    }

    GAIABREDRProvider(@NonNull Handler handler, BluetoothManager bluetoothManager) {
        super(bluetoothManager);
        this.mListener = handler;
    }

    /* access modifiers changed from: package-private */
    public void startUpgrade(File file) {
        UpgradeGaiaManager upgradeGaiaManager = this.mUpgradeGaiaManager;
        if (upgradeGaiaManager != null) {
            upgradeGaiaManager.startUpgrade(file);
        } else {
            Log.e("GAIABREDRProvider", "Upgrade has not been enabled.");
        }
    }

    /* access modifiers changed from: package-private */
    public int getResumePoint() {
        UpgradeGaiaManager upgradeGaiaManager = this.mUpgradeGaiaManager;
        if (upgradeGaiaManager != null) {
            return upgradeGaiaManager.getResumePoint();
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public void abortUpgrade() {
        UpgradeGaiaManager upgradeGaiaManager;
        if (getState() == 2 && (upgradeGaiaManager = this.mUpgradeGaiaManager) != null) {
            upgradeGaiaManager.abortUpgrade();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isUpgrading() {
        UpgradeGaiaManager upgradeGaiaManager = this.mUpgradeGaiaManager;
        return upgradeGaiaManager != null && upgradeGaiaManager.isUpgrading();
    }

    /* access modifiers changed from: package-private */
    public void sendConfirmation(int i, boolean z) {
        UpgradeGaiaManager upgradeGaiaManager = this.mUpgradeGaiaManager;
        if (upgradeGaiaManager != null) {
            upgradeGaiaManager.sendConfirmation(i, z);
        }
    }

    /* access modifiers changed from: package-private */
    public void enableUpgrade(boolean z) {
        if (z && this.mUpgradeGaiaManager == null) {
            this.mUpgradeGaiaManager = new UpgradeGaiaManager(this, 1);
        } else if (!z) {
            this.mUpgradeGaiaManager = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void enableDebugLogs(boolean z) {
        showDebugLogs(z);
        this.mUpgradeGaiaManager.enableDebugLogs(z);
    }

    public void onVMUpgradeDisconnected() {
        if (!isUpgrading()) {
            this.mUpgradeGaiaManager.reset();
        }
    }

    public void onResumePointChanged(int i) {
        sendMessageToListener(4, 2, Integer.valueOf(i));
    }

    public void onUpgradeError(UpgradeError upgradeError) {
        Log.e("GAIABREDRProvider", "ERROR during upgrade: " + upgradeError.getString());
        sendMessageToListener(4, 3, upgradeError);
    }

    public void onUploadProgress(double d) {
        sendMessageToListener(4, 4, Double.valueOf(d));
    }

    public boolean sendGAIAUpgradePacket(byte[] bArr, boolean z) {
        return sendData(bArr);
    }

    public void onUpgradeFinish() {
        sendMessageToListener(4, 0, (Object) null);
    }

    public void askConfirmation(int i) {
        sendConfirmation(i, true);
    }

    /* access modifiers changed from: package-private */
    public void showDebugLogs(boolean z) {
        this.mShowDebugLogs = z;
        StringBuilder sb = new StringBuilder();
        sb.append("Debug logs are now ");
        sb.append(z ? "activated" : "deactivated");
        sb.append(SplitStringUtils.SPLIT_FILE_SUFFIX);
        Log.i("GAIABREDRProvider", sb.toString());
        super.showDebugLogs(z);
    }

    /* access modifiers changed from: package-private */
    public void onConnectionStateChanged(int i) {
        sendMessageToListener(0, Integer.valueOf(i));
        if (i == 0 || i == 3) {
            this.mAnalyser.reset();
            UpgradeGaiaManager upgradeGaiaManager = this.mUpgradeGaiaManager;
            if (upgradeGaiaManager != null) {
                upgradeGaiaManager.reset();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onConnectionError(int i) {
        sendMessageToListener(2, Integer.valueOf(i));
    }

    /* access modifiers changed from: package-private */
    public void onCommunicationRunning() {
        sendMessageToListener(3);
        if (isUpgrading()) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    GAIABREDRProvider.this.mUpgradeGaiaManager.onGaiaReady();
                }
            });
        }
    }

    /* access modifiers changed from: package-private */
    public void onDataFound(byte[] bArr) {
        this.mAnalyser.analyse(bArr);
    }

    /* access modifiers changed from: private */
    public void onGAIAPacketFound(byte[] bArr) {
        if (this.mShowDebugLogs) {
            Log.d("GAIABREDRProvider", "Receive potential GAIA packet: " + Utils.getStringFromBytes(bArr));
        }
        UpgradeGaiaManager upgradeGaiaManager = this.mUpgradeGaiaManager;
        if (upgradeGaiaManager != null) {
            upgradeGaiaManager.onReceiveGAIAPacket(bArr);
        } else {
            sendMessageToListener(1, bArr);
        }
    }

    private void sendMessageToListener(int i) {
        Handler handler = this.mListener;
        if (handler != null) {
            handler.obtainMessage(i).sendToTarget();
        }
    }

    private void sendMessageToListener(int i, Object obj) {
        Handler handler = this.mListener;
        if (handler != null) {
            handler.obtainMessage(i, obj).sendToTarget();
        }
    }

    private void sendMessageToListener(int i, int i2, Object obj) {
        Handler handler = this.mListener;
        if (handler != null) {
            handler.obtainMessage(i, i2, 0, obj).sendToTarget();
        }
    }

    private class DataAnalyser {
        final byte[] mmData;
        int mmExpectedLength;
        int mmFlags;
        int mmReceivedLength;

        private DataAnalyser() {
            this.mmData = new byte[263];
            this.mmReceivedLength = 0;
            this.mmExpectedLength = 263;
        }

        /* access modifiers changed from: private */
        public void reset() {
            this.mmReceivedLength = 0;
            this.mmExpectedLength = 263;
        }

        /* access modifiers changed from: private */
        public void analyse(byte[] bArr) {
            int length = bArr.length;
            for (int i = 0; i < length; i++) {
                int i2 = this.mmReceivedLength;
                if (i2 > 0 && i2 < 263) {
                    this.mmData[i2] = bArr[i];
                    if (i2 == 2) {
                        this.mmFlags = bArr[i];
                    } else if (i2 == 3) {
                        this.mmExpectedLength = bArr[i] + 8 + ((this.mmFlags & 1) != 0 ? 1 : 0);
                    }
                    this.mmReceivedLength++;
                    int i3 = this.mmReceivedLength;
                    if (i3 == this.mmExpectedLength) {
                        byte[] bArr2 = new byte[i3];
                        System.arraycopy(this.mmData, 0, bArr2, 0, i3);
                        reset();
                        GAIABREDRProvider.this.onGAIAPacketFound(bArr2);
                    }
                } else if (bArr[i] == -1) {
                    this.mmReceivedLength = 1;
                } else if (this.mmReceivedLength >= 263) {
                    Log.w("GAIABREDRProvider", "Packet is too long: received length is bigger than the maximum length of a GAIA packet. Resetting analyser.");
                    reset();
                }
            }
        }
    }
}
