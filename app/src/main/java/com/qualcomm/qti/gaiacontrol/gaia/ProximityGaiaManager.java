package com.qualcomm.qti.gaiacontrol.gaia;

import android.os.Handler;
import com.qualcomm.qti.gaiacontrol.Consts;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacket;

public class ProximityGaiaManager extends AGaiaManager {
    private static final boolean DEBUG = Consts.DEBUG;
    private static final int RSSI_DELAY_TIME = 1000;
    private final String TAG = "ProximityGaiaManager";
    private final Handler mHandler = new Handler();
    private final ProximityGaiaManagerListener mListener;
    private boolean mPendingRSSICustomNotification = false;
    private final Runnable mRunnableRSSI = new Runnable() {
        public void run() {
            if (ProximityGaiaManager.this.mUpdateRssi) {
                ProximityGaiaManager.this.getRSSIInformation();
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mUpdateRssi = false;

    public interface ProximityGaiaManagerListener {
        void onGetRSSILevel(int i);

        void onRSSINotSupported();

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

    public ProximityGaiaManager(ProximityGaiaManagerListener proximityGaiaManagerListener, int i) {
        super(i);
        this.mListener = proximityGaiaManagerListener;
    }

    public void getRSSINotifications(boolean z) {
        if (z && !this.mUpdateRssi) {
            this.mUpdateRssi = true;
            getRSSIInformation();
        } else if (!z && this.mUpdateRssi) {
            this.mUpdateRssi = false;
            this.mPendingRSSICustomNotification = false;
            this.mHandler.removeCallbacks(this.mRunnableRSSI);
        }
    }

    /* access modifiers changed from: protected */
    public void receiveSuccessfulAcknowledgement(GaiaPacket gaiaPacket) {
        if (gaiaPacket.getCommand() == 769) {
            receivePacketGetCurrentRSSIACK(gaiaPacket);
        }
    }

    /* access modifiers changed from: protected */
    public void receiveUnsuccessfulAcknowledgement(GaiaPacket gaiaPacket) {
        if (gaiaPacket.getCommand() == 769) {
            this.mListener.onRSSINotSupported();
            this.mUpdateRssi = false;
        }
    }

    /* access modifiers changed from: protected */
    public boolean sendGAIAPacket(byte[] bArr) {
        return this.mListener.sendGAIAPacket(bArr);
    }

    private void receivePacketGetCurrentRSSIACK(GaiaPacket gaiaPacket) {
        byte[] payload = gaiaPacket.getPayload();
        if (payload.length >= 2) {
            this.mListener.onGetRSSILevel(payload[1]);
            if (this.mUpdateRssi && this.mPendingRSSICustomNotification) {
                this.mPendingRSSICustomNotification = false;
                this.mHandler.postDelayed(this.mRunnableRSSI, 1000);
            }
        }
    }

    /* access modifiers changed from: private */
    public void getRSSIInformation() {
        if (!this.mPendingRSSICustomNotification) {
            this.mPendingRSSICustomNotification = true;
            createRequest(createPacket(769));
        }
    }
}
