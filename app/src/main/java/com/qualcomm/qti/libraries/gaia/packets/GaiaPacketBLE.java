package com.qualcomm.qti.libraries.gaia.packets;

import com.qualcomm.qti.libraries.gaia.GaiaException;
import com.qualcomm.qti.libraries.gaia.GaiaUtils;

public class GaiaPacketBLE extends GaiaPacket {
    private static final int LENGTH_COMMAND_ID = 2;
    private static final int LENGTH_VENDOR_ID = 2;
    public static final int MAX_PAYLOAD = 16;
    public static final int MIN_PACKET_LENGTH = 4;
    private static final int OFFSET_COMMAND_ID = 2;
    private static final int OFFSET_PAYLOAD = 4;
    private static final int OFFSET_VENDOR_ID = 0;
    public static final int PACKET_INFORMATION_LENGTH = 4;

    /* access modifiers changed from: package-private */
    public int getPayloadMaxLength() {
        return 16;
    }

    public GaiaPacketBLE(byte[] bArr) throws GaiaException {
        int length = bArr.length - 4;
        if (length >= 0) {
            this.mVendorId = GaiaUtils.extractIntFromByteArray(bArr, 0, 2, false);
            this.mCommandId = GaiaUtils.extractIntFromByteArray(bArr, 2, 2, false);
            if (length > 0) {
                this.mPayload = new byte[length];
                System.arraycopy(bArr, 4, this.mPayload, 0, length);
            }
            this.mBytes = bArr;
            return;
        }
        throw new GaiaException(3);
    }

    public GaiaPacketBLE(int i, int i2) {
        this.mVendorId = i;
        this.mCommandId = i2;
        this.mPayload = new byte[0];
        this.mBytes = null;
    }

    public GaiaPacketBLE(int i, int i2, byte[] bArr) {
        this.mVendorId = i;
        this.mCommandId = i2;
        this.mPayload = bArr;
        this.mBytes = null;
    }

    /* access modifiers changed from: package-private */
    public byte[] buildBytes(int i, byte[] bArr) throws GaiaException {
        byte[] bArr2 = new byte[(bArr.length + 4)];
        GaiaUtils.copyIntIntoByteArray(this.mVendorId, bArr2, 0, 2, false);
        GaiaUtils.copyIntIntoByteArray(i, bArr2, 2, 2, false);
        System.arraycopy(bArr, 0, bArr2, 4, bArr.length);
        return bArr2;
    }
}
