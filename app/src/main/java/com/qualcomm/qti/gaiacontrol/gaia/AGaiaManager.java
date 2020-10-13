package com.qualcomm.qti.gaiacontrol.gaia;

import com.qualcomm.qti.libraries.gaia.GAIA;
import com.qualcomm.qti.libraries.gaia.GaiaManager;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacket;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacketBLE;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacketBREDR;

import org.uplus.twspopup.BuildConfig;

abstract class AGaiaManager extends GaiaManager {
    private final boolean hasChecksum = false;
    private final int mVendor = GAIA.VENDOR_QUALCOMM;

    AGaiaManager(int i) {
        super(i);
        showDebugLogs(BuildConfig.DEBUG);
    }

    /* access modifiers changed from: package-private */
    public GaiaPacket createPacket(int i) {
        return getTransportType() == 0 ? new GaiaPacketBLE(GAIA.VENDOR_QUALCOMM, i) : new GaiaPacketBREDR((int) GAIA.VENDOR_QUALCOMM, i, false);
    }

    /* access modifiers changed from: package-private */
    public GaiaPacket createPacket(int i, byte[] bArr) {
        return getTransportType() == 0 ? new GaiaPacketBLE(GAIA.VENDOR_QUALCOMM, i, bArr) : new GaiaPacketBREDR(GAIA.VENDOR_QUALCOMM, i, bArr, false);
    }
}
