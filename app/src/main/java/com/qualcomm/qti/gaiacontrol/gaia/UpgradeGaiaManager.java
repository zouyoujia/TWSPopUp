package com.qualcomm.qti.gaiacontrol.gaia;

import android.util.Log;
import com.qualcomm.qti.gaiacontrol.Consts;
import com.qualcomm.qti.libraries.gaia.GAIA;
import com.qualcomm.qti.libraries.gaia.GaiaException;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacket;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacketBLE;
import com.qualcomm.qti.libraries.vmupgrade.UpgradeError;
import com.qualcomm.qti.libraries.vmupgrade.UpgradeManager;
import java.io.File;

public class UpgradeGaiaManager extends AGaiaManager implements UpgradeManager.UpgradeManagerListener {
    private final String TAG = "UpgradeGaiaManager";
    private boolean mIsRWCPEnabled = false;
    private final GaiaManagerListener mListener;
    private int mPayloadSizeMax;
    private final UpgradeManager mUpgradeManager;

    public interface GaiaManagerListener {
        void askConfirmation(int i);

        void onRWCPEnabled(boolean z);

        void onRWCPNotSupported();

        void onResumePointChanged(int i);

        void onUpgradeError(UpgradeError upgradeError);

        void onUpgradeFinish();

        void onUploadProgress(double d);

        void onVMUpgradeDisconnected();

        boolean sendGAIAUpgradePacket(byte[] bArr, boolean z);
    }

    /* access modifiers changed from: protected */
    public void onSendingFailed(GaiaPacket gaiaPacket) {
    }

    public UpgradeGaiaManager(GaiaManagerListener gaiaManagerListener, int i) {
        super(i);
        this.mListener = gaiaManagerListener;
        this.mPayloadSizeMax = i == 1 ? 254 : 16;
        this.mUpgradeManager = new UpgradeManager(this);
        this.mUpgradeManager.showDebugLogs(Consts.DEBUG);
    }

    public void enableDebugLogs(boolean z) {
        showDebugLogs(z);
        this.mUpgradeManager.showDebugLogs(z);
    }

    public void startUpgrade(File file) {
        if (!this.mUpgradeManager.isUpgrading()) {
            registerNotification(18);
            this.mUpgradeManager.setFile(file);
            sendUpgradeConnect();
        }
    }

    public void abortUpgrade() {
        this.mUpgradeManager.abortUpgrade();
    }

    public int getResumePoint() {
        return this.mUpgradeManager.getResumePoint();
    }

    public void sendConfirmation(int i, boolean z) {
        if (this.mUpgradeManager.isUpgrading()) {
            this.mUpgradeManager.sendConfirmation(i, z);
        }
    }

    public void setRWCPMode(boolean z) {
        this.mIsRWCPEnabled = z;
        sendSetDataEndPointMode(new byte[]{z ? (byte) 1 : 0});
    }

    public void getRWCPStatus() {
        sendGetDataEndPointMode();
    }

    public boolean isRWCPEnabled() {
        return this.mIsRWCPEnabled;
    }

    public void onRWCPNotSupported() {
        this.mIsRWCPEnabled = false;
    }

    public void onTransferFinished() {
        this.mUpgradeManager.onSuccessfulTransmission();
    }

    public void sendUpgradePacket(byte[] bArr, boolean z) {
        sendUpgradeControl(bArr, z);
    }

    public void onUpgradeProcessError(UpgradeError upgradeError) {
        this.mListener.onUpgradeError(upgradeError);
        switch (upgradeError.getError()) {
            case 1:
            case 2:
            case 3:
            case 4:
                this.mUpgradeManager.abortUpgrade();
                return;
            default:
                return;
        }
    }

    public void onResumePointChanged(int i) {
        this.mListener.onResumePointChanged(i);
    }

    public void onUpgradeFinished() {
        this.mListener.onUpgradeFinish();
        disconnectUpgrade();
    }

    public void onFileUploadProgress(double d) {
        this.mListener.onUploadProgress(d);
    }

    public void askConfirmationFor(int i) {
        this.mListener.askConfirmation(i);
    }

    public void disconnectUpgrade() {
        cancelNotification(18);
        sendUpgradeDisconnect();
    }

    public void reset() {
        super.reset();
    }

    public boolean isUpgrading() {
        return this.mUpgradeManager.isUpgrading();
    }

    public void onGaiaReady() {
        if (this.mUpgradeManager.isUpgrading()) {
            if (this.mIsRWCPEnabled) {
                setRWCPMode(true);
            }
            registerNotification(18);
            sendUpgradeConnect();
        }
    }

    public void setPacketMaximumSize(int i) {
        this.mPayloadSizeMax = i - 4;
    }

    private void sendUpgradeConnect() {
        createRequest(createPacket(GAIA.COMMAND_VM_UPGRADE_CONNECT));
    }

    private void sendUpgradeDisconnect() {
        createRequest(createPacket(GAIA.COMMAND_VM_UPGRADE_DISCONNECT));
    }

    private void sendUpgradeControl(byte[] bArr, boolean z) {
        if (!z || !this.mIsRWCPEnabled) {
            createRequest(createPacket(GAIA.COMMAND_VM_UPGRADE_CONTROL, bArr));
            return;
        }
        GaiaPacket createPacket = createPacket(GAIA.COMMAND_VM_UPGRADE_CONTROL, bArr);
        try {
            if (!this.mListener.sendGAIAUpgradePacket(createPacket.getBytes(), true)) {
                Log.w("UpgradeGaiaManager", "Fail to send GAIA packet for GAIA command: " + createPacket.getCommandId());
                onSendingFailed(createPacket);
            }
        } catch (GaiaException e) {
            Log.w("UpgradeGaiaManager", "Exception when attempting to create GAIA packet: " + e.toString());
        }
    }

    private void sendSetDataEndPointMode(byte[] bArr) {
        createRequest(new GaiaPacketBLE(GAIA.VENDOR_QUALCOMM, GAIA.COMMAND_SET_DATA_ENDPOINT_MODE, bArr));
    }

    private void sendGetDataEndPointMode() {
        createRequest(new GaiaPacketBLE(GAIA.VENDOR_QUALCOMM, GAIA.COMMAND_GET_DATA_ENDPOINT_MODE));
    }

    private void registerNotification(int i) {
        try {
            createRequest(GaiaPacketBLE.buildGaiaNotificationPacket(GAIA.VENDOR_QUALCOMM, GAIA.COMMAND_REGISTER_NOTIFICATION, i, (byte[]) null, getTransportType()));
        } catch (GaiaException e) {
            Log.e("UpgradeGaiaManager", e.getMessage());
        }
    }

    private void cancelNotification(int i) {
        try {
            createRequest(GaiaPacketBLE.buildGaiaNotificationPacket(GAIA.VENDOR_QUALCOMM, 16386, i, (byte[]) null, getTransportType()));
        } catch (GaiaException e) {
            Log.e("UpgradeGaiaManager", e.getMessage());
        }
    }

    private boolean receiveEventNotification(GaiaPacket gaiaPacket) {
        byte[] payload = gaiaPacket.getPayload();
        if (payload.length <= 0) {
            createAcknowledgmentRequest(gaiaPacket, 5, (byte[]) null);
            return true;
        } else if (gaiaPacket.getEvent() != 18 || this.mUpgradeManager == null) {
            return false;
        } else {
            createAcknowledgmentRequest(gaiaPacket, 0, (byte[]) null);
            byte[] bArr = new byte[(payload.length - 1)];
            System.arraycopy(payload, 1, bArr, 0, payload.length - 1);
            this.mUpgradeManager.receiveVMUPacket(bArr);
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void receiveSuccessfulAcknowledgement(GaiaPacket gaiaPacket) {
        int command = gaiaPacket.getCommand();
        if (command == 558) {
            this.mListener.onRWCPEnabled(this.mIsRWCPEnabled);
        } else if (command != 686) {
            switch (command) {
                case GAIA.COMMAND_VM_UPGRADE_CONNECT /*1600*/:
                    if (this.mUpgradeManager.isUpgrading()) {
                        this.mUpgradeManager.resumeUpgrade();
                        return;
                    }
                    int i = this.mPayloadSizeMax;
                    if (this.mIsRWCPEnabled) {
                        i--;
                        if (i % 2 != 0) {
                            i--;
                        }
                    }
                    this.mUpgradeManager.startUpgrade(i, isRWCPEnabled());
                    return;
                case GAIA.COMMAND_VM_UPGRADE_DISCONNECT /*1601*/:
                    this.mUpgradeManager.onUpgradeDisconnected();
                    this.mListener.onVMUpgradeDisconnected();
                    return;
                case GAIA.COMMAND_VM_UPGRADE_CONTROL /*1602*/:
                    this.mUpgradeManager.onSuccessfulTransmission();
                    return;
                default:
                    switch (command) {
                    }
                    return;
            }
        } else {
            boolean z = true;
            if (gaiaPacket.getPayload()[1] != 1) {
                z = false;
            }
            this.mIsRWCPEnabled = z;
            this.mListener.onRWCPEnabled(this.mIsRWCPEnabled);
        }
    }

    /* access modifiers changed from: protected */
    public void receiveUnsuccessfulAcknowledgement(GaiaPacket gaiaPacket) {
        if (gaiaPacket.getCommand() == 1600 || gaiaPacket.getCommand() == 1602) {
            sendUpgradeDisconnect();
        } else if (gaiaPacket.getCommand() == 1601) {
            this.mListener.onVMUpgradeDisconnected();
        } else if (gaiaPacket.getCommand() == 558 || gaiaPacket.getCommand() == 686) {
            this.mIsRWCPEnabled = false;
            this.mListener.onRWCPNotSupported();
        }
    }

    /* access modifiers changed from: protected */
    public void hasNotReceivedAcknowledgementPacket(GaiaPacket gaiaPacket) {
        if (gaiaPacket.getCommand() == 557) {
            this.mListener.onVMUpgradeDisconnected();
        } else if (gaiaPacket.getCommand() == 558 || gaiaPacket.getCommand() == 686) {
            this.mListener.onRWCPNotSupported();
        }
    }

    /* access modifiers changed from: protected */
    public boolean manageReceivedPacket(GaiaPacket gaiaPacket) {
        if (gaiaPacket.getCommand() != 16387) {
            return false;
        }
        return receiveEventNotification(gaiaPacket);
    }

    /* access modifiers changed from: protected */
    public boolean sendGAIAPacket(byte[] bArr) {
        return this.mListener.sendGAIAUpgradePacket(bArr, false);
    }

    private static final class TransferModes {
        private static final byte MODE_NONE = 0;
        private static final byte MODE_RWCP = 1;

        private TransferModes() {
        }
    }
}
