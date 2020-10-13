package com.qualcomm.qti.gaiacontrol.gaia;

import android.annotation.SuppressLint;
import com.qualcomm.qti.gaiacontrol.Consts;
import com.qualcomm.qti.libraries.gaia.GAIA;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacket;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class RemoteGaiaManager extends AGaiaManager {
    private static final boolean DEBUG = Consts.DEBUG;
    private final String TAG = "RemoteGaiaManager";
    private final GaiaManagerListener mListener;

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Controls {
        public static final int FORWARD = 75;
        public static final int MUTE = 67;
        public static final int PAUSE = 70;
        public static final int PLAY = 68;
        public static final int REWIND = 76;
        public static final int STOP = 69;
        public static final int VOLUME_DOWN = 66;
        public static final int VOLUME_UP = 65;
    }

    public interface GaiaManagerListener {
        void onRemoteControlNotSupported();

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

    public RemoteGaiaManager(GaiaManagerListener gaiaManagerListener, int i) {
        super(i);
        this.mListener = gaiaManagerListener;
    }

    public void sendControlCommand(int i) {
        createRequest(createPacket(GAIA.COMMAND_AV_REMOTE_CONTROL, new byte[]{(byte) i}));
    }

    /* access modifiers changed from: protected */
    public void receiveSuccessfulAcknowledgement(GaiaPacket gaiaPacket) {
        gaiaPacket.getCommand();
    }

    /* access modifiers changed from: protected */
    public void receiveUnsuccessfulAcknowledgement(GaiaPacket gaiaPacket) {
        this.mListener.onRemoteControlNotSupported();
    }

    /* access modifiers changed from: protected */
    public boolean sendGAIAPacket(byte[] bArr) {
        return this.mListener.sendGAIAPacket(bArr);
    }
}
