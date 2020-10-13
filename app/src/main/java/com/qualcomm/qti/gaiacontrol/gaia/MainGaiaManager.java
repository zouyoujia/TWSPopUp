package com.qualcomm.qti.gaiacontrol.gaia;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Log;

import com.qualcomm.qti.gaiacontrol.Consts;
import com.qualcomm.qti.libraries.gaia.GAIA;
import com.qualcomm.qti.libraries.gaia.GaiaException;
import com.qualcomm.qti.libraries.gaia.GaiaUtils;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacket;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacketBLE;

import org.uplus.twspopup.utils.SplitStringUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MainGaiaManager extends AGaiaManager {
    private static final boolean DEBUG = Consts.DEBUG;
    private static final int DELAY_CUSTOM_NOTIFICATION = 5000;
    public static final int FEATURES_NUMBER = 5;
    private static final byte[] PAYLOAD_BOOLEAN_FALSE = {0};
    private static final byte[] PAYLOAD_BOOLEAN_TRUE = {1};
    /* access modifiers changed from: private */
    public static final ArrayMap<Integer, Boolean> mPendingCustomNotifications = new ArrayMap<>();
    private int COMMANDS_TO_CHECK = 0;
    private final String TAG = "MainGaiaManager";
    private int mCommandsChecked = 0;
    private final Handler mHandler = new Handler();
    private final MainGaiaManagerListener mListener;
    private final Runnable mRunnableBattery = new Runnable() {
        public void run() {
            synchronized (MainGaiaManager.mPendingCustomNotifications) {
                if (MainGaiaManager.mPendingCustomNotifications.containsKey(1)) {
                    MainGaiaManager.mPendingCustomNotifications.put(1, true);
                    MainGaiaManager.this.getInformation(1);
                }
            }
        }
    };
    private final Runnable mRunnableRSSI = new Runnable() {
        public void run() {
            synchronized (MainGaiaManager.mPendingCustomNotifications) {
                if (MainGaiaManager.mPendingCustomNotifications.containsKey(2)) {
                    MainGaiaManager.mPendingCustomNotifications.put(2, true);
                    MainGaiaManager.this.getInformation(2);
                }
            }
        }
    };

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Features {
        public static final int EQUALIZER = 1;
        public static final int LED = 0;
        public static final int REMOTE_CONTROL = 3;
        public static final int TWS = 2;
        public static final int UPGRADE = 4;
    }

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Information {
        public static final int API_VERSION = 3;
        public static final int BATTERY = 1;
        public static final int LED = 4;
        public static final int RSSI = 2;
    }

    public interface MainGaiaManagerListener {
        void onBatteryInfoChange(int i, int i2, int i3);

        void onChargeStateChange(int i, int i2, int i3);

        void onChargerConnected(boolean z);

        void onEnterFindMode(boolean z);

        void onFeatureSupported(int i);

        void onFeaturesDiscovered();

        void onGetAPIVersion(int i, int i2, int i3);

        void onGetBatteryInfo(int i, int i2, int i3);

        void onGetBatteryLevel(int i);

        void onGetHeadsetState(int i, int i2);

        void onGetHeadsetVersion(String str, String str2, String str3, String str4, String str5);

        void onGetLedControl(boolean z);

        void onGetRSSILevel(int i);

        void onInformationNotSupported(int i);

        void onLowBatteryStateChange(int i, int i2, int i3, int i4) throws RemoteException;

        boolean sendGAIAPacket(byte[] bArr);
    }

    /* access modifiers changed from: protected */
    public void onSendingFailed(GaiaPacket gaiaPacket) {
    }

    public MainGaiaManager(MainGaiaManagerListener mainGaiaManagerListener, int i) {
        super(i);
        this.mListener = mainGaiaManagerListener;
    }

    public void getSupportedFeatures() {
        this.mCommandsChecked = 0;
        this.COMMANDS_TO_CHECK = 0;
        checkCommands(647);
        checkCommands(GAIA.COMMAND_GET_3D_ENHANCEMENT_CONTROL, GAIA.COMMAND_GET_BASS_BOOST_CONTROL, GAIA.COMMAND_GET_USER_EQ_CONTROL, GAIA.COMMAND_GET_EQ_CONTROL);
        checkCommands(GAIA.COMMAND_GET_TWS_AUDIO_ROUTING, GAIA.COMMAND_GET_TWS_VOLUME);
        checkCommands(543);
        checkCommands(1600);
        this.COMMANDS_TO_CHECK += 2;
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
            case 4:
                createRequest(createPacket(GAIA.COMMAND_GET_LED_CONTROL));
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

    public void setLedState(boolean z) {
        createRequest(createPacket(GAIA.COMMAND_SET_LED_CONTROL, z ? PAYLOAD_BOOLEAN_TRUE : PAYLOAD_BOOLEAN_FALSE));
    }

    /* access modifiers changed from: protected */
    public void receiveSuccessfulAcknowledgement(GaiaPacket gaiaPacket) {
        onFeatureSupported(gaiaPacket.getCommand());
        int command = gaiaPacket.getCommand();
        if (command == 647) {
            receiveGetLedControlACK(gaiaPacket);
        } else if (command == 804) {
            receivePacketGetBatteryInfoACK(gaiaPacket);
        } else if (command == 818) {
            receivePacketGetHeadsetVersionACK(gaiaPacket);
        } else if (command == 1600) {
            createRequest(createPacket(GAIA.COMMAND_VM_UPGRADE_CONTROL));
        } else if (command != 1602) {
            switch (command) {
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
                    switch (command) {
                        case GAIA.COMMAND_GET_HEADSET_STATE /*824*/:
                            receivePacketGetHeadsetStateACK(gaiaPacket);
                            return;
                        case GAIA.COMMAND_ENTER_FIND_MODE /*825*/:
                            receivePacketEnterFindModeACK(gaiaPacket);
                            return;
                        default:
                            return;
                    }
            }
        } else {
            createRequest(createPacket(GAIA.COMMAND_VM_UPGRADE_DISCONNECT));
        }
    }

    /* access modifiers changed from: protected */
    public void receiveUnsuccessfulAcknowledgement(GaiaPacket gaiaPacket) {
        int command = gaiaPacket.getCommand();
        if (gaiaPacket.getStatus() != 1) {
            onFeatureSupported(command);
            onInformationNotSupported(command);
            if (command == 1600) {
                createRequest(createPacket(GAIA.COMMAND_VM_UPGRADE_CONTROL));
            } else if (command == 1602) {
                createRequest(createPacket(GAIA.COMMAND_VM_UPGRADE_DISCONNECT));
            }
        } else if (command == 647 || command == 662 || command == 661 || command == 672 || command == 660 || command == 676 || command == 677 || command == 543 || command == 1600 || command == 1602 || command == 1601) {
            onCommandChecked();
        } else {
            onInformationNotSupported(command);
        }
    }

    /* access modifiers changed from: protected */
    public void hasNotReceivedAcknowledgementPacket(GaiaPacket gaiaPacket) {
        int command = gaiaPacket.getCommand();
        if (command == 647 || command == 662 || command == 661 || command == 672 || command == 660 || command == 676 || command == 677 || command == 543 || command == 1600 || command == 1602 || command == 1601) {
            onCommandChecked();
        }
    }

    /* access modifiers changed from: protected */
    public boolean manageReceivedPacket(GaiaPacket gaiaPacket) {
        if (gaiaPacket.getCommand() == 16387) {
            return receiveEventNotification(gaiaPacket);
        }
        if (gaiaPacket.getCommand() == 805) {
            byte[] payload = gaiaPacket.getPayload();
            if (payload.length >= 6) {
                this.mListener.onBatteryInfoChange(payload[1], payload[3], payload[5]);
                createAcknowledgmentRequest(gaiaPacket, 0, (byte[]) null);
            } else {
                createAcknowledgmentRequest(gaiaPacket, -1, (byte[]) null);
            }
        } else if (gaiaPacket.getCommand() == 806) {
            byte[] payload2 = gaiaPacket.getPayload();
            if (payload2.length >= 6) {
                try {
                    this.mListener.onLowBatteryStateChange(payload2[1], payload2[2], payload2[4], payload2[5]);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                createAcknowledgmentRequest(gaiaPacket, 0, (byte[]) null);
            } else {
                createAcknowledgmentRequest(gaiaPacket, -1, (byte[]) null);
            }
        } else if (gaiaPacket.getCommand() == 807) {
            byte[] payload3 = gaiaPacket.getPayload();
            if (payload3.length >= 6) {
                this.mListener.onChargeStateChange(payload3[1], payload3[3], payload3[5]);
                createAcknowledgmentRequest(gaiaPacket, 0, (byte[]) null);
            } else {
                createAcknowledgmentRequest(gaiaPacket, -1, (byte[]) null);
            }
        } else if (gaiaPacket.getCommand() == 819) {
            createAcknowledgmentRequest(gaiaPacket, 0, new byte[]{1, 0, 0});
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean sendGAIAPacket(byte[] bArr) {
        return this.mListener.sendGAIAPacket(bArr);
    }

    private void registerGAIANotification(int i) {
        try {
            createRequest(GaiaPacketBLE.buildGaiaNotificationPacket(GAIA.VENDOR_QUALCOMM, GAIA.COMMAND_REGISTER_NOTIFICATION, i, (byte[]) null, getTransportType()));
        } catch (GaiaException e) {
            Log.e("MainGaiaManager", e.getMessage());
        }
    }

    private void cancelGAIANotification(int i) {
        try {
            createRequest(GaiaPacketBLE.buildGaiaNotificationPacket(GAIA.VENDOR_QUALCOMM, 16386, i, (byte[]) null, getTransportType()));
        } catch (GaiaException e) {
            Log.e("MainGaiaManager", e.getMessage());
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

    private void receiveGetLedControlACK(GaiaPacket gaiaPacket) {
        byte[] payload = gaiaPacket.getPayload();
        if (payload.length >= 2) {
            boolean z = true;
            if (payload[1] != 1) {
                z = false;
            }
            this.mListener.onGetLedControl(z);
        }
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

    private void onFeatureSupported(int i) {
        switch (i) {
            case GAIA.COMMAND_AV_REMOTE_CONTROL /*543*/:
                onCommandChecked();
                this.mListener.onFeatureSupported(3);
                return;
            case GAIA.COMMAND_GET_LED_CONTROL /*647*/:
                onCommandChecked();
                this.mListener.onFeatureSupported(0);
                return;
            case GAIA.COMMAND_GET_EQ_CONTROL /*660*/:
            case GAIA.COMMAND_GET_BASS_BOOST_CONTROL /*661*/:
            case GAIA.COMMAND_GET_3D_ENHANCEMENT_CONTROL /*662*/:
            case GAIA.COMMAND_GET_EQ_PARAMETER /*666*/:
            case GAIA.COMMAND_GET_USER_EQ_CONTROL /*672*/:
                onCommandChecked();
                this.mListener.onFeatureSupported(1);
                return;
            case GAIA.COMMAND_GET_TWS_AUDIO_ROUTING /*676*/:
            case GAIA.COMMAND_GET_TWS_VOLUME /*677*/:
                onCommandChecked();
                this.mListener.onFeatureSupported(2);
                return;
            case GAIA.COMMAND_VM_UPGRADE_CONNECT /*1600*/:
            case GAIA.COMMAND_VM_UPGRADE_CONTROL /*1602*/:
                onCommandChecked();
                return;
            case GAIA.COMMAND_VM_UPGRADE_DISCONNECT /*1601*/:
                onCommandChecked();
                this.mListener.onFeatureSupported(4);
                return;
            default:
                return;
        }
    }

    private void onInformationNotSupported(int i) {
        if (i != 647) {
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
        } else {
            this.mListener.onInformationNotSupported(4);
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

    private void checkCommands(int... iArr) {
        this.COMMANDS_TO_CHECK += iArr.length;
        for (int createPacket : iArr) {
            createRequest(createPacket(createPacket));
        }
    }

    private void onCommandChecked() {
        this.mCommandsChecked++;
        if (this.mCommandsChecked == this.COMMANDS_TO_CHECK) {
            this.mListener.onFeaturesDiscovered();
            this.COMMANDS_TO_CHECK = 0;
        }
    }

    public void getBatteryInfo() {
        createRequest(createPacket(GAIA.COMMAND_GET_BATTERY_INFO, PAYLOAD_BOOLEAN_TRUE));
    }

    public void getHeadsetState() {
        createRequest(createPacket(GAIA.COMMAND_GET_HEADSET_STATE, PAYLOAD_BOOLEAN_TRUE));
    }

    public void enterFindMode() {
        createRequest(createPacket(GAIA.COMMAND_ENTER_FIND_MODE, PAYLOAD_BOOLEAN_TRUE));
    }

    public void exitFindMode() {
        createRequest(createPacket(GAIA.COMMAND_ENTER_FIND_MODE, PAYLOAD_BOOLEAN_FALSE));
    }

    public void getHeadsetVersion() {
        createRequest(createPacket(GAIA.COMMAND_GET_HEADSET_VERSION, PAYLOAD_BOOLEAN_TRUE));
    }

    private void receivePacketGetBatteryInfoACK(GaiaPacket gaiaPacket) {
        byte[] payload = gaiaPacket.getPayload();
        if (payload.length >= 7) {
            this.mListener.onGetBatteryInfo(payload[2], payload[4], payload[6]);
        }
    }

    private void receivePacketGetHeadsetStateACK(GaiaPacket gaiaPacket) {
        byte[] payload = gaiaPacket.getPayload();
        if (payload.length >= 5) {
            this.mListener.onGetHeadsetState(payload[2], payload[4]);
        }
    }

    private void receivePacketEnterFindModeACK(GaiaPacket gaiaPacket) {
        byte[] payload = gaiaPacket.getPayload();
        if (payload.length >= 1) {
            boolean z = false;
            if (payload[0] == 0) {
                z = true;
            }
            this.mListener.onEnterFindMode(z);
        }
    }

    private void receivePacketGetHeadsetVersionACK(GaiaPacket gaiaPacket) {
        byte[] payload = gaiaPacket.getPayload();
        Log.d("MainGaiaManager", "payload's size is " + payload.length);
        Log.d("MainGaiaManager", "part1: " + payload[1] + ", " + payload[2] + ", " + payload[3] + ", " + payload[4]);
        Log.d("MainGaiaManager", "part2: " + payload[5] + ", " + payload[6] + ", " + payload[7] + ", " + payload[8] + ", " + payload[9] + ", " + payload[10]);
        Log.d("MainGaiaManager", "part3: " + payload[11] + ", " + payload[12] + ", " + payload[13] + ", " + payload[14] + ", " + payload[15] + ", " + payload[16]);
        Log.d("MainGaiaManager", "part4: " + payload[17] + ", " + payload[18] + ", " + payload[19]);
        Log.d("MainGaiaManager", "part5: " + payload[20] + ", " + payload[21] + ", " + payload[22] + ", " + payload[23] + ", " + payload[24] + ", " + payload[25] + ", " + payload[26] + ", " + payload[27] + ", " + payload[28] + ", " + payload[29] + ", " + payload[30] + ", " + payload[31] + ", " + payload[32] + ", " + payload[33]);
        String str = String.valueOf(payload[1]) + String.valueOf(payload[2]) + String.valueOf(payload[3]) + String.valueOf(payload[4]);
        String str2 = String.valueOf(payload[6]) + String.valueOf(payload[7]) + String.valueOf(payload[8]) + String.valueOf(payload[9]) + String.valueOf(payload[10]);
        String str3 = String.valueOf(payload[12]) + String.valueOf(payload[13]) + String.valueOf(payload[14]) + String.valueOf(payload[15]) + String.valueOf(payload[16]);
        String str4 = payload[17] + SplitStringUtils.SPLIT_FILE_SUFFIX + payload[18] + SplitStringUtils.SPLIT_FILE_SUFFIX + payload[19];
        String str5 = String.valueOf(payload[21]) + String.valueOf(payload[22]) + String.valueOf(payload[23]) + String.valueOf(payload[24]) + String.valueOf(payload[25]) + String.valueOf(payload[26]) + String.valueOf(payload[28]) + String.valueOf(payload[29]) + String.valueOf(payload[30]) + String.valueOf(payload[31]) + String.valueOf(payload[32]) + String.valueOf(payload[33]);
        if (payload.length >= 34) {
            this.mListener.onGetHeadsetVersion(str, str2, str3, str4, str5);
        }
    }
}
