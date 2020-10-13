package com.qualcomm.qti.libraries.gaia.packets;

import com.qualcomm.qti.libraries.gaia.GaiaException;
import com.qualcomm.qti.libraries.gaia.GaiaUtils;

public class GaiaPacketBREDR extends GaiaPacket {
    public static final int CHECK_LENGTH = 1;
    public static final int FLAG_CHECK_MASK = 1;
    public static final int LENGTH_COMMAND_ID = 2;
    public static final int LENGTH_VENDOR_ID = 2;
    public static final int MAX_PACKET = 263;
    public static final int MAX_PAYLOAD = 254;
    public static final int OFFSET_COMMAND_ID = 6;
    public static final int OFFSET_FLAGS = 2;
    public static final int OFFSET_LENGTH = 3;
    public static final int OFFSET_PAYLOAD = 8;
    public static final int OFFSET_SOF = 0;
    public static final int OFFSET_VENDOR_ID = 4;
    public static final int OFFSET_VERSION = 1;
    public static final int PROTOCOL_VERSION = 1;
    public static final byte SOF = -1;
    private boolean mHasChecksum = false;

    /* access modifiers changed from: package-private */
    public int getPayloadMaxLength() {
        return 254;
    }

    public GaiaPacketBREDR(byte[] bArr) {
        byte b = bArr[2];
        int length = bArr.length - 8;
        length = (b & 1) != 0 ? length - 1 : length;
        this.mVendorId = GaiaUtils.extractIntFromByteArray(bArr, 4, 2, false);
        this.mCommandId = GaiaUtils.extractIntFromByteArray(bArr, 6, 2, false);
        if (length > 0) {
            this.mPayload = new byte[length];
            System.arraycopy(bArr, 8, this.mPayload, 0, length);
        }
        this.mBytes = bArr;
    }

    public GaiaPacketBREDR(int i, int i2, boolean z) {
        this.mVendorId = i;
        this.mCommandId = i2;
        this.mPayload = new byte[0];
        this.mHasChecksum = z;
        this.mBytes = null;
    }

    public GaiaPacketBREDR(int i, int i2) {
        this.mVendorId = i;
        this.mCommandId = i2;
        this.mPayload = new byte[0];
        this.mHasChecksum = false;
        this.mBytes = null;
    }

    public GaiaPacketBREDR(int i, int i2, byte[] bArr, boolean z) {
        this.mVendorId = i;
        this.mCommandId = i2;
        this.mPayload = bArr;
        this.mHasChecksum = z;
        this.mBytes = null;
    }

    public GaiaPacketBREDR(int i, int i2, byte[] bArr) {
        this.mVendorId = i;
        this.mCommandId = i2;
        this.mPayload = bArr;
        this.mHasChecksum = false;
        this.mBytes = null;
    }

    /* access modifiers changed from: package-private */
    public byte[] buildBytes(int i, byte[] bArr) throws GaiaException {
        int i2;
        int i3 = 0;
        if (bArr.length <= 254) {
            byte b = (byte) (this.mHasChecksum ? 1 : 0);
            int length = bArr.length + 8 + b;
            byte[] bArr2 = new byte[length];
            bArr2[0] = -1;
            bArr2[1] = 1;
            bArr2[2] = b;
            bArr2[3] = (byte) bArr.length;
            GaiaUtils.copyIntIntoByteArray(this.mVendorId, bArr2, 4, 2, false);
            GaiaUtils.copyIntIntoByteArray(i, bArr2, 6, 2, false);
            System.arraycopy(bArr, 0, bArr2, 8, bArr.length);
            if (this.mHasChecksum) {
                byte b2 = 0;
                while (true) {
                    i2 = length - 1;
                    if (i3 >= i2) {
                        break;
                    }
                    b2 = (byte) (b2 ^ bArr2[i3]);
                    i3++;
                }
                bArr2[i2] = b2;
            }
            return bArr2;
        }
        throw new GaiaException(0);
    }
}
