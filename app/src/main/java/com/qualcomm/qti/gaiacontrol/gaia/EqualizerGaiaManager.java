package com.qualcomm.qti.gaiacontrol.gaia;

import android.annotation.SuppressLint;
import android.util.Log;
import com.qualcomm.qti.gaiacontrol.Consts;
import com.qualcomm.qti.libraries.gaia.GAIA;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacket;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class EqualizerGaiaManager extends AGaiaManager {
    public static final int CUSTOMIZABLE_PRESET = 1;
    private static final boolean DEBUG = Consts.DEBUG;
    public static final int NUMBER_OF_PRESETS = 7;
    private static final byte[] PAYLOAD_BOOLEAN_FALSE = {0};
    private static final byte[] PAYLOAD_BOOLEAN_TRUE = {1};
    private final String TAG = "EqualizerGaiaManager";
    private final GaiaManagerListener mListener;

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Controls {
        public static final int BASS_BOOST = 2;
        public static final int ENHANCEMENT_3D = 1;
        public static final int PRESETS = 3;
    }

    public interface GaiaManagerListener {
        void onControlNotSupported(int i);

        void onGetControlActivationState(int i, boolean z);

        void onGetPreset(int i);

        boolean sendGAIAPacket(byte[] bArr);
    }

    /* access modifiers changed from: protected */
    public void hasNotReceivedAcknowledgementPacket(GaiaPacket gaiaPacket) {
    }

    /* access modifiers changed from: protected */
    public boolean manageReceivedPacket(GaiaPacket gaiaPacket) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onSendingFailed(GaiaPacket gaiaPacket) {
    }

    public EqualizerGaiaManager(GaiaManagerListener gaiaManagerListener, int i) {
        super(i);
        this.mListener = gaiaManagerListener;
    }

    public void setPreset(int i) {
        if (i < 0 || i >= 7) {
            Log.w("EqualizerGaiaManager", "setPreset used with parameter not between 0 and 6, value: " + i);
            return;
        }
        createRequest(createPacket(GAIA.COMMAND_SET_EQ_CONTROL, new byte[]{(byte) i}));
    }

    public void getPreset() {
        createRequest(createPacket(GAIA.COMMAND_GET_EQ_CONTROL));
    }

    public void getActivationState(int i) {
        switch (i) {
            case 1:
                createRequest(createPacket(GAIA.COMMAND_GET_3D_ENHANCEMENT_CONTROL));
                return;
            case 2:
                createRequest(createPacket(GAIA.COMMAND_GET_BASS_BOOST_CONTROL));
                return;
            case 3:
                createRequest(createPacket(GAIA.COMMAND_GET_USER_EQ_CONTROL));
                return;
            default:
                return;
        }
    }

    public void setActivationState(int i, boolean z) {
        byte[] bArr;
        if (z) {
            bArr = PAYLOAD_BOOLEAN_TRUE;
        } else {
            bArr = PAYLOAD_BOOLEAN_FALSE;
        }
        switch (i) {
            case 1:
                createRequest(createPacket(GAIA.COMMAND_SET_3D_ENHANCEMENT_CONTROL, bArr));
                return;
            case 2:
                createRequest(createPacket(GAIA.COMMAND_SET_BASS_BOOST_CONTROL, bArr));
                return;
            case 3:
                createRequest(createPacket(GAIA.COMMAND_SET_USER_EQ_CONTROL, bArr));
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: protected */
    public void receiveSuccessfulAcknowledgement(GaiaPacket gaiaPacket) {
        int command = gaiaPacket.getCommand();
        if (command != 672) {
            switch (command) {
                case GAIA.COMMAND_GET_EQ_CONTROL /*660*/:
                    receiveGetEQControlACK(gaiaPacket);
                    return;
                case GAIA.COMMAND_GET_BASS_BOOST_CONTROL /*661*/:
                    receiveGetControlACK(2, gaiaPacket);
                    return;
                case GAIA.COMMAND_GET_3D_ENHANCEMENT_CONTROL /*662*/:
                    receiveGetControlACK(1, gaiaPacket);
                    return;
                default:
                    return;
            }
        } else {
            receiveGetControlACK(3, gaiaPacket);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0013, code lost:
        r1.mListener.onControlNotSupported(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x001a, code lost:
        r1.mListener.onControlNotSupported(2);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void receiveUnsuccessfulAcknowledgement(GaiaPacket r2) {
        /*
            r1 = this;
            int r2 = r2.getCommand()
            r0 = 544(0x220, float:7.62E-43)
            if (r2 == r0) goto L_0x0021
            r0 = 672(0x2a0, float:9.42E-43)
            if (r2 == r0) goto L_0x0021
            switch(r2) {
                case 532: goto L_0x0021;
                case 533: goto L_0x001a;
                case 534: goto L_0x0013;
                default: goto L_0x000f;
            }
        L_0x000f:
            switch(r2) {
                case 660: goto L_0x0021;
                case 661: goto L_0x001a;
                case 662: goto L_0x0013;
                default: goto L_0x0012;
            }
        L_0x0012:
            goto L_0x0027
        L_0x0013:
            com.qualcomm.qti.gaiacontrol.gaia.EqualizerGaiaManager$GaiaManagerListener r2 = r1.mListener
            r0 = 1
            r2.onControlNotSupported(r0)
            goto L_0x0027
        L_0x001a:
            com.qualcomm.qti.gaiacontrol.gaia.EqualizerGaiaManager$GaiaManagerListener r2 = r1.mListener
            r0 = 2
            r2.onControlNotSupported(r0)
            goto L_0x0027
        L_0x0021:
            com.qualcomm.qti.gaiacontrol.gaia.EqualizerGaiaManager$GaiaManagerListener r2 = r1.mListener
            r0 = 3
            r2.onControlNotSupported(r0)
        L_0x0027:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qualcomm.qti.gaiacontrol.gaia.EqualizerGaiaManager.receiveUnsuccessfulAcknowledgement(com.qualcomm.qti.libraries.gaia.packets.GaiaPacket):void");
    }

    /* access modifiers changed from: protected */
    public boolean sendGAIAPacket(byte[] bArr) {
        return this.mListener.sendGAIAPacket(bArr);
    }

    private void receiveGetControlACK(int i, GaiaPacket gaiaPacket) {
        byte[] payload = gaiaPacket.getPayload();
        if (payload.length >= 2) {
            boolean z = true;
            if (payload[1] != 1) {
                z = false;
            }
            this.mListener.onGetControlActivationState(i, z);
        }
    }

    private void receiveGetEQControlACK(GaiaPacket gaiaPacket) {
        byte[] payload = gaiaPacket.getPayload();
        if (payload.length >= 2) {
            this.mListener.onGetPreset(payload[1]);
        }
    }
}
