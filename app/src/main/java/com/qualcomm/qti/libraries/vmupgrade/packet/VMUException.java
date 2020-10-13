package com.qualcomm.qti.libraries.vmupgrade.packet;

import android.annotation.SuppressLint;
import com.qualcomm.qti.libraries.vmupgrade.VMUUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class VMUException extends Exception {
    private final byte[] mBytes;
    private final String mMessage;
    private final int mType;

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        public static final int DATA_LENGTH_ERROR = 1;
        public static final int DATA_TOO_SHORT = 0;
        public static final int FILE_TOO_BIG = 2;
        public static final int GET_BYTES_FILE_FAILED = 3;
    }

    public VMUException(int i) {
        this.mType = i;
        this.mMessage = "";
        this.mBytes = new byte[0];
    }

    public VMUException(int i, String str) {
        this.mType = i;
        this.mMessage = str;
        this.mBytes = new byte[0];
    }

    public VMUException(int i, byte[] bArr) {
        this.mType = i;
        this.mMessage = "";
        this.mBytes = bArr;
    }

    public int getType() {
        return this.mType;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = this.mType;
        if (i != 0) {
            switch (i) {
                case 2:
                    sb.append("Get file failed: The given file size is >= 2GB");
                    break;
                case 3:
                    sb.append("Get file failed");
                    if (this.mMessage.length() > 0) {
                        sb.append(": ");
                        sb.append(this.mMessage);
                        break;
                    }
                    break;
                default:
                    sb.append("VMU Exception occurs");
                    break;
            }
        } else {
            sb.append("Build of a VMUPacket failed: the byte array does not contain the minimum required information");
            sb.append("\nReceived bytes: ");
            sb.append(VMUUtils.getHexadecimalStringFromBytes(this.mBytes));
        }
        return sb.toString();
    }
}
