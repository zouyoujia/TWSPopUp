package com.qualcomm.qti.libraries.gaia.packets;

import com.qualcomm.qti.libraries.gaia.GAIA;
import com.qualcomm.qti.libraries.gaia.GaiaException;

public abstract class GaiaPacket {
    byte[] mBytes;
    int mCommandId;
    byte[] mPayload = new byte[0];
    int mVendorId = GAIA.VENDOR_QUALCOMM;

    /* access modifiers changed from: package-private */
    public abstract byte[] buildBytes(int i, byte[] bArr) throws GaiaException;

    /* access modifiers changed from: package-private */
    public abstract int getPayloadMaxLength();

    public byte[] getPayload() {
        return this.mPayload;
    }

    public int getVendorId() {
        return this.mVendorId;
    }

    public int getCommand() {
        return this.mCommandId & 32767;
    }

    public int getCommandId() {
        return this.mCommandId;
    }

    public int getStatus() {
        byte[] bArr;
        if (!isAcknowledgement() || (bArr = this.mPayload) == null || bArr.length < 1) {
            return -1;
        }
        return GAIA.getStatus(bArr[0]);
    }

    public boolean isAcknowledgement() {
        return (this.mCommandId & 32768) > 0;
    }

    public int getEvent() {
        byte[] bArr;
        if ((this.mCommandId & 16384) < 1 || (bArr = this.mPayload) == null || bArr.length < 1) {
            return 0;
        }
        return GAIA.getNotificationEvent(bArr[0]);
    }

    public byte[] getBytes() throws GaiaException {
        byte[] bArr = this.mBytes;
        if (bArr != null) {
            return bArr;
        }
        this.mBytes = buildBytes(this.mCommandId, this.mPayload);
        return this.mBytes;
    }

    public byte[] getAcknowledgementPacketBytes(int i, byte[] bArr) throws GaiaException {
        byte[] bArr2;
        if (!isAcknowledgement()) {
            int i2 = this.mCommandId | 32768;
            if (bArr != null) {
                int length = bArr.length + 1;
                bArr2 = new byte[length];
                System.arraycopy(bArr, 0, bArr2, 1, length - 1);
            } else {
                bArr2 = new byte[1];
            }
            bArr2[0] = (byte) i;
            return buildBytes(i2, bArr2);
        }
        throw new GaiaException(1);
    }

    public static GaiaPacket buildGaiaNotificationPacket(int i, int i2, int i3, byte[] bArr, int i4) throws GaiaException {
        byte[] bArr2;
        if ((i2 & 16384) == 16384) {
            if (bArr != null) {
                bArr2 = new byte[(bArr.length + 1)];
                bArr2[0] = (byte) i3;
                System.arraycopy(bArr, 0, bArr2, 1, bArr.length);
            } else {
                bArr2 = new byte[]{(byte) i3};
            }
            return i4 == 0 ? new GaiaPacketBLE(i, i2, bArr2) : new GaiaPacketBREDR(i, i2, bArr2);
        }
        throw new GaiaException(2);
    }
}
