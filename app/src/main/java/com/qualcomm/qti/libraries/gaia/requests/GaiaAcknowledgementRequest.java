package com.qualcomm.qti.libraries.gaia.requests;

public class GaiaAcknowledgementRequest extends GaiaRequest {
    public final byte[] data;
    public final int status;

    public GaiaAcknowledgementRequest(int i, byte[] bArr) {
        super(2);
        this.status = i;
        this.data = bArr;
    }
}
