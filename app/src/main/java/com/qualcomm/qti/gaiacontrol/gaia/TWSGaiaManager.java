package com.qualcomm.qti.gaiacontrol.gaia;

import android.annotation.SuppressLint;
import com.qualcomm.qti.gaiacontrol.Consts;
import com.qualcomm.qti.libraries.gaia.GAIA;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacket;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TWSGaiaManager extends AGaiaManager {
    private static final boolean DEBUG = Consts.DEBUG;
    private static final int MAX_VOLUME = 127;
    private final String TAG = "TWSGaiaManager";
    private final GaiaManagerListener mListener;

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Channel {
        public static final int LEFT = 1;
        public static final int MONO = 3;
        public static final int RIGHT = 2;
        public static final int STEREO = 0;
    }

    public interface GaiaManagerListener {
        void onGetChannel(int i, int i2);

        void onGetVolume(int i, int i2);

        boolean sendGAIAPacket(byte[] bArr);
    }

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Speaker {
        public static final int MASTER_SPEAKER = 0;
        public static final int SLAVE_SPEAKER = 1;
    }

    private int getChannelType(int i) {
        switch (i) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            default:
                return -1;
        }
    }

    private int getSpeakerType(int i) {
        switch (i) {
            case 0:
                return 0;
            case 1:
                return 1;
            default:
                return -1;
        }
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

    /* access modifiers changed from: protected */
    public void receiveUnsuccessfulAcknowledgement(GaiaPacket gaiaPacket) {
    }

    public TWSGaiaManager(GaiaManagerListener gaiaManagerListener, int i) {
        super(i);
        this.mListener = gaiaManagerListener;
    }

    public void getChannel(int i) {
        createRequest(createPacket(GAIA.COMMAND_GET_TWS_AUDIO_ROUTING, new byte[]{(byte) i}));
    }

    public void getVolume(int i) {
        createRequest(createPacket(GAIA.COMMAND_GET_TWS_VOLUME, new byte[]{(byte) i}));
    }

    public void setChannel(int i, int i2) {
        createRequest(createPacket(GAIA.COMMAND_SET_TWS_AUDIO_ROUTING, new byte[]{(byte) i, (byte) i2}));
    }

    public void setVolume(int i, int i2) {
        if (i2 < 0) {
            i2 = 0;
        } else if (i2 > 127) {
            i2 = 127;
        }
        createRequest(createPacket(GAIA.COMMAND_SET_TWS_VOLUME, new byte[]{(byte) i, (byte) i2}));
    }

    /* access modifiers changed from: protected */
    public void receiveSuccessfulAcknowledgement(GaiaPacket gaiaPacket) {
        switch (gaiaPacket.getCommand()) {
            case GAIA.COMMAND_GET_TWS_AUDIO_ROUTING /*676*/:
                receiveGetChannelACK(gaiaPacket);
                return;
            case GAIA.COMMAND_GET_TWS_VOLUME /*677*/:
                receiveGetVolumeACK(gaiaPacket);
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: protected */
    public boolean sendGAIAPacket(byte[] bArr) {
        return this.mListener.sendGAIAPacket(bArr);
    }

    private void receiveGetChannelACK(GaiaPacket gaiaPacket) {
        byte[] payload = gaiaPacket.getPayload();
        if (payload.length >= 3) {
            this.mListener.onGetChannel(getSpeakerType(payload[1]), getChannelType(payload[2]));
        }
    }

    private void receiveGetVolumeACK(GaiaPacket gaiaPacket) {
        byte[] payload = gaiaPacket.getPayload();
        if (payload.length >= 3) {
            byte b = payload[1];
            byte b2 = payload[2];
            if (b2 > Byte.MAX_VALUE) {
                b2 = Byte.MAX_VALUE;
            } else if (b2 < 0) {
                b2 = 0;
            }
            this.mListener.onGetVolume(getSpeakerType(b), b2);
        }
    }
}
