package com.qualcomm.qti.libraries.vmupgrade;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.qualcomm.qti.libraries.vmupgrade.codes.OpCodes;
import com.qualcomm.qti.libraries.vmupgrade.codes.ResumePoints;
import com.qualcomm.qti.libraries.vmupgrade.codes.ReturnCodes;
import com.qualcomm.qti.libraries.vmupgrade.packet.VMUException;
import com.qualcomm.qti.libraries.vmupgrade.packet.VMUPacket;

import org.uplus.twspopup.utils.SplitStringUtils;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class UpgradeManager {
    private final String TAG = "UpgradeManager";
    private boolean hasToAbort = false;
    private boolean hasToRestartUpgrade = false;
    private boolean isUpgrading = false;
    private byte[] mBytesFile;
    private int mBytesToSend = 0;
    private File mFile;
    private final Handler mHandler = new Handler();
    private final UpgradeManagerListener mListener;
    private int mMaxLengthForDataTransfer = 8;
    private int mResumePoint;
    private boolean mSendMultiplePackets = false;
    private boolean mShowDebugLogs = false;
    private int mStartAttempts = 0;
    private int mStartOffset = 0;
    private boolean wasLastPacket = false;

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ConfirmationType {
        public static final int BATTERY_LOW_ON_DEVICE = 5;
        public static final int COMMIT = 2;
        public static final int IN_PROGRESS = 3;
        public static final int TRANSFER_COMPLETE = 1;
        public static final int WARNING_FILE_IS_DIFFERENT = 4;
    }

    public interface UpgradeManagerListener {
        void askConfirmationFor(int i);

        void disconnectUpgrade();

        void onFileUploadProgress(double d);

        void onResumePointChanged(int i);

        void onUpgradeFinished();

        void onUpgradeProcessError(UpgradeError upgradeError);

        void sendUpgradePacket(byte[] bArr, boolean z);
    }

    public UpgradeManager(@NonNull UpgradeManagerListener upgradeManagerListener) {
        this.mListener = upgradeManagerListener;
    }

    public UpgradeManager(@NonNull UpgradeManagerListener upgradeManagerListener, int i) {
        this.mListener = upgradeManagerListener;
        this.mMaxLengthForDataTransfer = i - 3;
    }

    public void setFile(File file) {
        this.mFile = file;
    }

    public void showDebugLogs(boolean z) {
        this.mShowDebugLogs = z;
        StringBuilder sb = new StringBuilder();
        sb.append("Debug logs are now ");
        sb.append(z ? "activated" : "deactivated");
        sb.append(SplitStringUtils.SPLIT_FILE_SUFFIX);
        Log.i("UpgradeManager", sb.toString());
    }

    public void startUpgrade() {
        Log.w("UpgradeManager", "startUpgrade() is deprecated, please use startUpgrade(int maxLength, boolean sendMultiplePackets) instead.");
    }

    public void startUpgrade(int i, boolean z) {
        if (i < 8) {
            Log.w("UpgradeManager", "setPacketMaxLengthForDataTransfer: given length is too short, minimum value is setup: 8");
            this.mMaxLengthForDataTransfer = 8;
        } else {
            this.mMaxLengthForDataTransfer = i - 3;
        }
        this.mSendMultiplePackets = z;
        File file = this.mFile;
        if (file == null) {
            this.mListener.onUpgradeProcessError(new UpgradeError(6));
            return;
        }
        try {
            this.mBytesFile = VMUUtils.getBytesFromFile(file);
            startUpgradeProcess();
        } catch (VMUException e) {
            UpgradeError upgradeError = new UpgradeError(e);
            Log.e("UpgradeManager", "Error occurs when attempt to start the process: " + upgradeError.getString());
            this.mListener.onUpgradeProcessError(upgradeError);
        }
    }

    public boolean resumeUpgrade() {
        if (this.isUpgrading) {
            resetUpload();
            sendSyncReq();
        }
        return this.isUpgrading;
    }

    public boolean isUpgrading() {
        return this.isUpgrading;
    }

    public void receiveVMUPacket(byte[] bArr) {
        try {
            VMUPacket vMUPacket = new VMUPacket(bArr);
            if (!this.isUpgrading) {
                if (vMUPacket.getOpCode() != 8) {
                    Log.w("UpgradeManager", "Received VMU packet while application is not upgrading anymore, opcode received: " + OpCodes.getString(vMUPacket.getOpCode()));
                    return;
                }
            }
            if (this.mShowDebugLogs) {
                Log.d("UpgradeManager", "Received " + OpCodes.getString(vMUPacket.getOpCode()) + ": " + VMUUtils.getHexadecimalStringFromBytes(vMUPacket.getData()));
            }
            handleVMUPacket(vMUPacket);
        } catch (VMUException e) {
            startAbortion(new UpgradeError(e));
        }
    }

    public void receiveVMControlSucceed() {
        Log.w("UpgradeManager", "method receiveVMControlSucceed is deprecated, please use onSuccessfulTransmission() instead.");
    }

    public void onSuccessfulTransmission() {
        if (this.wasLastPacket) {
            if (this.mResumePoint == 0) {
                this.wasLastPacket = false;
                setResumePoint(1);
                sendValidationDoneReq();
            }
        } else if (this.hasToAbort) {
            this.hasToAbort = false;
            abortUpgrade();
        } else if (this.mBytesToSend > 0 && this.mResumePoint == 0 && !this.mSendMultiplePackets) {
            sendNextDataPacket();
        }
    }

    public void abortUpgrade() {
        if (this.isUpgrading) {
            sendAbortReq();
            this.isUpgrading = false;
        }
    }

    public void sendConfirmation(int i, boolean z) {
        switch (i) {
            case 1:
                sendTransferCompleteReq(z);
                if (!z) {
                    this.hasToAbort = true;
                    return;
                }
                return;
            case 2:
                sendCommitCFM(z);
                if (!z) {
                    this.hasToAbort = true;
                    return;
                }
                return;
            case 3:
                sendInProgressRes(z);
                if (!z) {
                    abortUpgrade();
                    return;
                }
                return;
            case 4:
                this.hasToRestartUpgrade = z;
                sendAbortReq();
                return;
            case 5:
                if (z) {
                    sendSyncReq();
                    return;
                } else {
                    abortUpgrade();
                    return;
                }
            default:
                return;
        }
    }

    public int getResumePoint() {
        return this.mResumePoint;
    }

    public void receiveVMDisconnectSucceed() {
        Log.w("UpgradeManager", "method receiveVMDisconnectSucceed() is deprecated, please use onUpgradeDisconnected() instead.");
    }

    public void onUpgradeDisconnected() {
        if (this.hasToRestartUpgrade) {
            this.hasToRestartUpgrade = false;
            startUpgradeProcess();
        }
    }

    private void startUpgradeProcess() {
        if (!this.isUpgrading && this.mBytesFile != null) {
            this.isUpgrading = true;
            resetUpload();
            sendSyncReq();
        } else if (this.isUpgrading) {
            this.mListener.onUpgradeProcessError(new UpgradeError(5));
        } else {
            this.mListener.onUpgradeProcessError(new UpgradeError(6));
        }
    }

    private void sendVMUPacket(VMUPacket vMUPacket, boolean z) {
        byte[] bytes = vMUPacket.getBytes();
        if (this.isUpgrading) {
            if (this.mShowDebugLogs) {
                Log.d("UpgradeManager", "send " + OpCodes.getString(vMUPacket.getOpCode()) + ": " + VMUUtils.getHexadecimalStringFromBytes(bytes));
            }
            this.mListener.sendUpgradePacket(bytes, z);
            return;
        }
        Log.w("UpgradeManager", "Sending failed as application is no longer upgrading for opcode: " + OpCodes.getString(vMUPacket.getOpCode()));
    }

    private void startAbortion(UpgradeError upgradeError) {
        Log.e("UpgradeManager", "Error occurs during upgrade process: " + upgradeError.getString() + "\nStart abortion...");
        this.mListener.onUpgradeProcessError(upgradeError);
        abortUpgrade();
    }

    private void setResumePoint(int i) {
        this.mResumePoint = i;
        this.mListener.onResumePointChanged(i);
    }

    private void askForConfirmation(int i) {
        this.mListener.askConfirmationFor(i);
    }

    private void stopUpgrade() {
        this.isUpgrading = false;
        this.mListener.disconnectUpgrade();
    }

    private void resetUpload() {
        this.mStartAttempts = 0;
        this.mBytesToSend = 0;
        this.mStartOffset = 0;
    }

    private void onFileUploadProgress() {
        double length = (((double) this.mStartOffset) * 100.0d) / ((double) this.mBytesFile.length);
        if (length < 0.0d) {
            length = 0.0d;
        } else if (length > 100.0d) {
            length = 100.0d;
        }
        this.mListener.onFileUploadProgress(length);
    }

    private void sendNextDataPacket() {
        onFileUploadProgress();
        int i = this.mBytesToSend;
        int i2 = this.mMaxLengthForDataTransfer;
        if (i >= i2 - 1) {
            i = i2 - 1;
        }
        boolean z = this.mBytesFile.length - this.mStartOffset <= i;
        byte[] bArr = new byte[i];
        System.arraycopy(this.mBytesFile, this.mStartOffset, bArr, 0, bArr.length);
        if (z) {
            this.wasLastPacket = true;
            this.mBytesToSend = 0;
        } else {
            this.mStartOffset += i;
            this.mBytesToSend -= i;
        }
        sendData(z, bArr);
    }

    private void sendSyncReq() {
        byte[] mD5FromFile = VMUUtils.getMD5FromFile(this.mFile);
        byte[] bArr = new byte[4];
        if (mD5FromFile.length >= 4) {
            System.arraycopy(mD5FromFile, mD5FromFile.length - 4, bArr, 0, 4);
        } else if (mD5FromFile.length > 0) {
            System.arraycopy(mD5FromFile, 0, bArr, 0, mD5FromFile.length);
        }
        sendVMUPacket(new VMUPacket(19, bArr), false);
    }

    /* access modifiers changed from: private */
    public void sendStartReq() {
        sendVMUPacket(new VMUPacket(1), false);
    }

    private void sendStartDataReq() {
        setResumePoint(0);
        sendVMUPacket(new VMUPacket(21), false);
    }

    private void sendData(boolean z, byte[] bArr) {
        byte[] bArr2 = new byte[(bArr.length + 1)];
        bArr2[0] = z ? (byte) 1 : 0;
        System.arraycopy(bArr, 0, bArr2, 1, bArr.length);
        sendVMUPacket(new VMUPacket(4, bArr2), true);
    }

    /* access modifiers changed from: private */
    public void sendValidationDoneReq() {
        sendVMUPacket(new VMUPacket(22), false);
    }

    private void sendTransferCompleteReq(boolean z) {
        sendVMUPacket(new VMUPacket(12, new byte[]{(byte) (z ? 0 : 1)}), false);
    }

    private void sendInProgressRes(boolean z) {
        sendVMUPacket(new VMUPacket(14, new byte[]{(byte) (z ? 0 : 1)}), false);
    }

    private void sendCommitCFM(boolean z) {
        sendVMUPacket(new VMUPacket(16, new byte[]{(byte) (z ? 0 : 1)}), false);
    }

    private void sendAbortReq() {
        sendVMUPacket(new VMUPacket(7), false);
    }

    private void sendErrorConfirmation(byte[] bArr) {
        sendVMUPacket(new VMUPacket(31, bArr), false);
    }

    @SuppressLint({"SwitchIntDef"})
    private void handleVMUPacket(VMUPacket vMUPacket) {
        switch (vMUPacket.getOpCode()) {
            case 2:
                receiveStartCFM(vMUPacket);
                return;
            case 3:
                receiveDataBytesREQ(vMUPacket);
                return;
            case 8:
                receiveAbortCFM();
                return;
            case 11:
                receiveTransferCompleteIND();
                return;
            case 15:
                receiveCommitREQ();
                return;
            case 17:
                receiveErrorWarnIND(vMUPacket);
                return;
            case 18:
                receiveCompleteIND();
                return;
            case 20:
                receiveSyncCFM(vMUPacket);
                return;
            case 23:
                receiveValidationDoneCFM(vMUPacket);
                return;
            default:
                return;
        }
    }

    private void receiveErrorWarnIND(VMUPacket vMUPacket) {
        byte[] data = vMUPacket.getData();
        sendErrorConfirmation(data);
        int returnCode = ReturnCodes.getReturnCode(VMUUtils.extractShortFromByteArray(data, 0, 2, false));
        if (returnCode == 129) {
            askForConfirmation(4);
        } else if (returnCode == 33) {
            askForConfirmation(5);
        } else {
            startAbortion(new UpgradeError(3, returnCode));
        }
    }

    private void receiveSyncCFM(VMUPacket vMUPacket) {
        byte[] data = vMUPacket.getData();
        if (data.length >= 6) {
            int resumePoint = ResumePoints.getResumePoint(data[0]);
            VMUUtils.extractIntFromByteArray(data, 1, 4, false);
            byte b = data[5];
            if (resumePoint == 3) {
                setResumePoint(resumePoint);
            } else {
                this.mResumePoint = resumePoint;
            }
        } else {
            this.mResumePoint = 0;
        }
        sendStartReq();
    }

    private void receiveStartCFM(VMUPacket vMUPacket) {
        byte[] data = vMUPacket.getData();
        if (data.length >= 3) {
            VMUUtils.extractShortFromByteArray(data, 1, 2, false);
            if (data[0] == 0) {
                this.mStartAttempts = 0;
                switch (this.mResumePoint) {
                    case 1:
                        sendValidationDoneReq();
                        return;
                    case 2:
                        askForConfirmation(1);
                        return;
                    case 3:
                        sendInProgressRes(true);
                        return;
                    case 4:
                        askForConfirmation(2);
                        return;
                    default:
                        sendStartDataReq();
                        return;
                }
            } else if (data[0] == 9) {
                int i = this.mStartAttempts;
                if (i < 5) {
                    this.mStartAttempts = i + 1;
                    this.mHandler.postDelayed(new Runnable() {
                        public void run() {
                            UpgradeManager.this.sendStartReq();
                        }
                    }, (long) 2000);
                    return;
                }
                this.mStartAttempts = 0;
                startAbortion(new UpgradeError(1));
            } else {
                startAbortion(new UpgradeError(2));
            }
        } else {
            startAbortion(new UpgradeError(2));
        }
    }

    private void receiveDataBytesREQ(VMUPacket vMUPacket) {
        byte[] data = vMUPacket.getData();
        if (data.length == 8) {
            this.mBytesToSend = VMUUtils.extractIntFromByteArray(data, 0, 4, false);
            int extractIntFromByteArray = VMUUtils.extractIntFromByteArray(data, 4, 4, false);
            int i = this.mStartOffset;
            if (extractIntFromByteArray <= 0 || extractIntFromByteArray + i >= this.mBytesFile.length) {
                extractIntFromByteArray = 0;
            }
            this.mStartOffset = i + extractIntFromByteArray;
            int i2 = this.mBytesToSend;
            if (i2 <= 0) {
                i2 = 0;
            }
            this.mBytesToSend = i2;
            int length = this.mBytesFile.length - this.mStartOffset;
            int i3 = this.mBytesToSend;
            if (i3 < length) {
                length = i3;
            }
            this.mBytesToSend = length;
            if (this.mSendMultiplePackets) {
                while (this.mBytesToSend > 0) {
                    sendNextDataPacket();
                }
                return;
            }
            sendNextDataPacket();
            return;
        }
        startAbortion(new UpgradeError(2));
    }

    private void receiveValidationDoneCFM(VMUPacket vMUPacket) {
        byte[] bytes = vMUPacket.getBytes();
        if (bytes.length == 2) {
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    UpgradeManager.this.sendValidationDoneReq();
                }
            }, VMUUtils.extractLongFromByteArray(bytes, 0, 2, false));
            return;
        }
        sendValidationDoneReq();
    }

    private void receiveTransferCompleteIND() {
        setResumePoint(2);
        sendTransferCompleteReq(true);
    }

    private void receiveCommitREQ() {
        setResumePoint(4);
        sendCommitCFM(true);
    }

    private void receiveCompleteIND() {
        this.isUpgrading = false;
        this.mListener.onUpgradeFinished();
    }

    private void receiveAbortCFM() {
        stopUpgrade();
    }
}
