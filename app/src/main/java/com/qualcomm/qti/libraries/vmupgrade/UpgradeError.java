package com.qualcomm.qti.libraries.vmupgrade;

import android.annotation.SuppressLint;
import androidx.annotation.IntRange;
import com.qualcomm.qti.libraries.vmupgrade.codes.ReturnCodes;
import com.qualcomm.qti.libraries.vmupgrade.packet.VMUException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class UpgradeError {
    private final int mBoardError;
    private final int mError;
    private final VMUException mException;

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ErrorTypes {
        public static final int AN_UPGRADE_IS_ALREADY_PROCESSING = 5;
        public static final int ERROR_BOARD_NOT_READY = 1;
        public static final int EXCEPTION = 4;
        public static final int NO_FILE = 6;
        public static final int RECEIVED_ERROR_FROM_BOARD = 3;
        public static final int WRONG_DATA_PARAMETER = 2;
    }

    public UpgradeError(int i) {
        this.mError = i;
        this.mBoardError = 0;
        this.mException = null;
    }

    public UpgradeError(@IntRange(from = 3, to = 3) int i, int i2) {
        this.mError = i;
        this.mBoardError = i2;
        this.mException = null;
    }

    public UpgradeError(VMUException vMUException) {
        this.mError = 4;
        this.mBoardError = 0;
        this.mException = vMUException;
    }

    public int getError() {
        return this.mError;
    }

    public int getReturnCode() {
        return this.mBoardError;
    }

    public VMUException getException() {
        return this.mException;
    }

    public String getString() {
        switch (this.mError) {
            case 1:
                return "The board is not ready to process an upgrade.";
            case 2:
                return "The board does not send the expected parameter(s).";
            case 3:
                return "An error occurs on the board during the upgrade process.\n\t- Received error code: " + VMUUtils.getHexadecimalString(this.mBoardError) + "\n\t- Received error message: " + ReturnCodes.getReturnCodesMessage(this.mBoardError);
            case 4:
                StringBuilder sb = new StringBuilder();
                sb.append("An Exception has occurred");
                if (this.mException != null) {
                    sb.append(": ");
                    sb.append(this.mException.toString());
                }
                return sb.toString();
            case 5:
                return "Attempt to start an upgrade failed: an upgrade is already processing.";
            case 6:
                return "The provided file is empty or does not exist.";
            default:
                return "An error has occurred during the upgrade process.";
        }
    }
}
