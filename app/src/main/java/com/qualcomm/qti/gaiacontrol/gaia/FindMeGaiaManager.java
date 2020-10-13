package com.qualcomm.qti.gaiacontrol.gaia;

import com.qualcomm.qti.gaiacontrol.Consts;
import com.qualcomm.qti.libraries.gaia.GAIA;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacket;

public class FindMeGaiaManager extends AGaiaManager {
    private static final boolean DEBUG = Consts.DEBUG;
    private final String TAG = "FindMeGaiaManager";
    private final FindMeGaiaManagerListener mListener;

    public interface FindMeGaiaManagerListener {
        void onFindMyRemoteNotSupported();

        boolean sendGAIAPacket(byte[] bArr);
    }

    public static class Levels {
        public static final byte HIGH_ALERT = 2;
        public static final byte MILD_ALERT = 1;
        public static final byte NO_ALERT = 0;
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
    public void receiveSuccessfulAcknowledgement(GaiaPacket gaiaPacket) {
    }

    public FindMeGaiaManager(FindMeGaiaManagerListener findMeGaiaManagerListener, int i) {
        super(i);
        this.mListener = findMeGaiaManagerListener;
    }

    public void setFindMyRemoteAlertLevel(byte b) {
        createRequest(createPacket(GAIA.COMMAND_FIND_MY_REMOTE, new byte[]{b}));
    }

    /* access modifiers changed from: protected */
    public void receiveUnsuccessfulAcknowledgement(GaiaPacket gaiaPacket) {
        if (gaiaPacket.getCommand() == 555) {
            this.mListener.onFindMyRemoteNotSupported();
        }
    }

    /* access modifiers changed from: protected */
    public boolean sendGAIAPacket(byte[] bArr) {
        return this.mListener.sendGAIAPacket(bArr);
    }
}
