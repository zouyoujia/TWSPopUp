package com.qualcomm.qti.gaiacontrol.rwcp;

import android.util.Log;
import com.qualcomm.qti.gaiacontrol.Utils;
import com.qualcomm.qti.libraries.vmupgrade.VMUUtils;

class Segment {
    private final String TAG;
    private byte[] mBytes;
    private final byte mHeader;
    private final int mOperationCode;
    private final byte[] mPayload;
    private final int mSequenceNumber;

    private static int getBits(byte b, int i, int i2) {
        return (b & (((1 << i2) - 1) << i)) >>> i;
    }

    Segment(int i, int i2, byte[] bArr) {
        this.TAG = "Segment";
        this.mOperationCode = i;
        this.mSequenceNumber = i2;
        this.mPayload = bArr;
        this.mHeader = (byte) ((i << 6) | i2);
    }

    Segment(int i, int i2) {
        this(i, i2, new byte[0]);
    }

    Segment(byte[] bArr) {
        this.TAG = "Segment";
        this.mBytes = bArr;
        if (bArr == null || bArr.length < 1) {
            StringBuilder sb = new StringBuilder();
            sb.append("Building of RWCP Segment failed: the byte array does not contain the minimum required information.\nbytes: ");
            sb.append(bArr != null ? VMUUtils.getHexadecimalStringFromBytes(bArr) : "null");
            Log.w("Segment", sb.toString());
            this.mOperationCode = -1;
            this.mSequenceNumber = -1;
            this.mHeader = -1;
            this.mPayload = bArr;
            return;
        }
        this.mHeader = bArr[0];
        this.mOperationCode = getBits(this.mHeader, 6, 2);
        this.mSequenceNumber = getBits(this.mHeader, 0, 6);
        this.mPayload = new byte[(bArr.length - 1)];
        byte[] bArr2 = this.mPayload;
        System.arraycopy(bArr, 1, bArr2, 0, bArr2.length);
    }

    /* access modifiers changed from: package-private */
    public int getOperationCode() {
        return this.mOperationCode;
    }

    /* access modifiers changed from: package-private */
    public int getSequenceNumber() {
        return this.mSequenceNumber;
    }

    /* access modifiers changed from: package-private */
    public byte[] getPayload() {
        return this.mPayload;
    }

    /* access modifiers changed from: package-private */
    public byte[] getBytes() {
        if (this.mBytes == null) {
            byte[] bArr = this.mPayload;
            int length = bArr == null ? 0 : bArr.length;
            this.mBytes = new byte[(length + 1)];
            byte[] bArr2 = this.mBytes;
            bArr2[0] = this.mHeader;
            if (length > 0) {
                byte[] bArr3 = this.mPayload;
                System.arraycopy(bArr3, 0, bArr2, 1, bArr3.length);
            }
        }
        return this.mBytes;
    }

    /* access modifiers changed from: package-private */
    public byte getHeader() {
        return this.mHeader;
    }

    public String toString() {
        return toString(false);
    }

    /* access modifiers changed from: package-private */
    public String toString(boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append("[code=");
        sb.append(this.mOperationCode);
        sb.append(", sequence=");
        sb.append(this.mSequenceNumber);
        if (z) {
            sb.append(", payload=");
            sb.append(Utils.getStringFromBytes(this.mPayload));
        }
        sb.append("]");
        return sb.toString();
    }
}
