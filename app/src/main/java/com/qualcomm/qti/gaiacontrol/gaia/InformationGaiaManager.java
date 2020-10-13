package com.qualcomm.qti.gaiacontrol.gaia;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.ArrayMap;
import android.util.Log;
import com.qualcomm.qti.gaiacontrol.Consts;
import com.qualcomm.qti.libraries.gaia.GAIA;
import com.qualcomm.qti.libraries.gaia.GaiaException;
import com.qualcomm.qti.libraries.gaia.GaiaUtils;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacket;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class InformationGaiaManager extends AGaiaManager {
    private static final boolean DEBUG = Consts.DEBUG;
    private static final int DELAY_CUSTOM_NOTIFICATION = 5000;
    /* access modifiers changed from: private */
    public static final ArrayMap<Integer, Boolean> mPendingCustomNotifications = new ArrayMap<>();
    private final String TAG = "InformationGaiaManager";
    private final Handler mHandler = new Handler();
    private final GaiaManagerListener mListener;
    private final Runnable mRunnableBattery = new Runnable() {
        public void run() {
            synchronized (InformationGaiaManager.mPendingCustomNotifications) {
                if (InformationGaiaManager.mPendingCustomNotifications.containsKey(1)) {
                    InformationGaiaManager.mPendingCustomNotifications.put(1, true);
                    InformationGaiaManager.this.getInformation(1);
                }
            }
        }
    };
    private final Runnable mRunnableRSSI = new Runnable() {
        public void run() {
            synchronized (InformationGaiaManager.mPendingCustomNotifications) {
                if (InformationGaiaManager.mPendingCustomNotifications.containsKey(2)) {
                    InformationGaiaManager.mPendingCustomNotifications.put(2, true);
                    InformationGaiaManager.this.getInformation(2);
                }
            }
        }
    };

    public interface GaiaManagerListener {
        void onChargerConnected(boolean z);

        void onGetAPIVersion(int i, int i2, int i3);

        void onGetBatteryLevel(int i);

        void onGetRSSILevel(int i);

        void onInformationNotSupported(int i);

        boolean sendGAIAPacket(byte[] bArr);
    }

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Information {
        public static final int API_VERSION = 3;
        public static final int BATTERY = 1;
        public static final int RSSI = 2;
    }

    /* access modifiers changed from: protected */
    public void hasNotReceivedAcknowledgementPacket(GaiaPacket gaiaPacket) {
    }

    /* access modifiers changed from: protected */
    public void onSendingFailed(GaiaPacket gaiaPacket) {
    }

    public InformationGaiaManager(GaiaManagerListener gaiaManagerListener, int i) {
        super(i);
        this.mListener = gaiaManagerListener;
    }

    public void getInformation(int i) {
        switch (i) {
            case 1:
                createRequest(createPacket(770));
                return;
            case 2:
                createRequest(createPacket(769));
                return;
            case 3:
                createRequest(createPacket(768));
                return;
            default:
                return;
        }
    }

    @SuppressLint({"SwitchIntDef"})
    public void getNotifications(int i, boolean z) {
        switch (i) {
            case 1:
                getBatteryNotifications(z);
                return;
            case 2:
                getRSSINotifications(z);
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: protected */
    public void receiveSuccessfulAcknowledgement(GaiaPacket gaiaPacket) {
        switch (gaiaPacket.getCommand()) {
            case 768:
                receivePacketGetAPIVersionACK(gaiaPacket);
                return;
            case 769:
                receivePacketGetCurrentRSSIACK(gaiaPacket);
                return;
            case 770:
                receivePacketGetCurrentBatteryLevelACK(gaiaPacket);
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: protected */
    public void receiveUnsuccessfulAcknowledgement(GaiaPacket gaiaPacket) {
        if (gaiaPacket.getStatus() != 1) {
            onInformationNotSupported(gaiaPacket.getCommand());
        } else {
            onInformationNotSupported(gaiaPacket.getCommand());
        }
    }

    /* access modifiers changed from: protected */
    public boolean manageReceivedPacket(GaiaPacket gaiaPacket) {
        if (gaiaPacket.getCommand() == 16387) {
            return receiveEventNotification(gaiaPacket);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean sendGAIAPacket(byte[] bArr) {
        return this.mListener.sendGAIAPacket(bArr);
    }

    private void registerGAIANotification(int i) {
        try {
            createRequest(GaiaPacket.buildGaiaNotificationPacket(GAIA.VENDOR_QUALCOMM, GAIA.COMMAND_REGISTER_NOTIFICATION, i, (byte[]) null, getTransportType()));
        } catch (GaiaException e) {
            Log.e("InformationGaiaManager", e.getMessage());
        }
    }

    private void cancelGAIANotification(int i) {
        try {
            createRequest(GaiaPacket.buildGaiaNotificationPacket(GAIA.VENDOR_QUALCOMM, 16386, i, (byte[]) null, getTransportType()));
        } catch (GaiaException e) {
            Log.e("InformationGaiaManager", e.getMessage());
        }
    }

    @SuppressLint({"SwitchIntDef"})
    private boolean receiveEventNotification(GaiaPacket gaiaPacket) {
        if (gaiaPacket.getPayload().length < 1) {
            createAcknowledgmentRequest(gaiaPacket, 5, (byte[]) null);
            return true;
        } else if (gaiaPacket.getEvent() != 9) {
            return false;
        } else {
            return receiveEventChargerConnection(gaiaPacket);
        }
    }

    private boolean receiveEventChargerConnection(GaiaPacket gaiaPacket) {
        if (gaiaPacket.getPayload().length >= 2) {
            boolean z = false;
            createAcknowledgmentRequest(gaiaPacket, 0, (byte[]) null);
            if (gaiaPacket.getPayload()[1] == 1) {
                z = true;
            }
            this.mListener.onChargerConnected(z);
            return true;
        }
        createAcknowledgmentRequest(gaiaPacket, 5, (byte[]) null);
        return true;
    }

    private void receivePacketGetCurrentBatteryLevelACK(GaiaPacket gaiaPacket) {
        if (gaiaPacket.getPayload().length >= 3) {
            this.mListener.onGetBatteryLevel(GaiaUtils.extractIntFromByteArray(gaiaPacket.getPayload(), 1, 2, false));
            synchronized (mPendingCustomNotifications) {
                if (mPendingCustomNotifications.containsKey(1) && mPendingCustomNotifications.get(1).booleanValue()) {
                    mPendingCustomNotifications.put(1, false);
                    this.mHandler.postDelayed(this.mRunnableBattery, 5000);
                }
            }
        }
    }

    private void receivePacketGetCurrentRSSIACK(GaiaPacket gaiaPacket) {
        byte[] payload = gaiaPacket.getPayload();
        if (payload.length >= 2) {
            this.mListener.onGetRSSILevel(payload[1]);
            synchronized (mPendingCustomNotifications) {
                if (mPendingCustomNotifications.containsKey(2) && mPendingCustomNotifications.get(2).booleanValue()) {
                    mPendingCustomNotifications.put(2, false);
                    this.mHandler.postDelayed(this.mRunnableRSSI, 5000);
                }
            }
        }
    }

    private void receivePacketGetAPIVersionACK(GaiaPacket gaiaPacket) {
        byte[] payload = gaiaPacket.getPayload();
        if (payload.length >= 4) {
            this.mListener.onGetAPIVersion(payload[1], payload[2], payload[3]);
        }
    }

    private void onInformationNotSupported(int i) {
        switch (i) {
            case 768:
                this.mListener.onInformationNotSupported(3);
                return;
            case 769:
                this.mListener.onInformationNotSupported(2);
                synchronized (mPendingCustomNotifications) {
                    if (mPendingCustomNotifications.containsKey(2)) {
                        mPendingCustomNotifications.remove(2);
                    }
                }
                return;
            case 770:
                this.mListener.onInformationNotSupported(1);
                synchronized (mPendingCustomNotifications) {
                    if (mPendingCustomNotifications.containsKey(1)) {
                        mPendingCustomNotifications.remove(1);
                    }
                }
                return;
            default:
                return;
        }
    }

    private void getBatteryNotifications(boolean z) {
        synchronized (mPendingCustomNotifications) {
            if (z) {
                registerGAIANotification(9);
                mPendingCustomNotifications.put(1, true);
                getInformation(1);
            } else {
                mPendingCustomNotifications.remove(1);
                this.mHandler.removeCallbacks(this.mRunnableBattery);
                cancelGAIANotification(9);
            }
        }
    }

    private void getRSSINotifications(boolean z) {
        synchronized (mPendingCustomNotifications) {
            if (z) {
                mPendingCustomNotifications.put(2, true);
                getInformation(2);
            } else {
                mPendingCustomNotifications.remove(2);
                this.mHandler.removeCallbacks(this.mRunnableRSSI);
            }
        }
    }
}
