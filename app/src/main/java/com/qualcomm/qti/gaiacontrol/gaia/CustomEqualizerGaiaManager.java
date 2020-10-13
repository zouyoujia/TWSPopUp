package com.qualcomm.qti.gaiacontrol.gaia;

import android.util.Log;

import com.qualcomm.qti.gaiacontrol.Consts;
import com.qualcomm.qti.gaiacontrol.Utils;
import com.qualcomm.qti.gaiacontrol.models.equalizer.parameters.Filter;
import com.qualcomm.qti.gaiacontrol.models.equalizer.parameters.ParameterType;
import com.qualcomm.qti.libraries.gaia.GAIA;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacket;
import com.qualcomm.qti.libraries.vmupgrade.codes.OpCodes;

public class CustomEqualizerGaiaManager extends AGaiaManager {
    private static final boolean DEBUG = Consts.DEBUG;
    private static final int EQ_PARAMETER_FIRST_BYTE = 1;
    public static final int GENERAL_BAND = 0;
    private static final int GET_EQ_PARAMETER_PAYLOAD_LENGTH = 5;
    public static final int PARAMETER_MASTER_GAIN = 1;
    private final String TAG = "CustomEQGaiaManager";
    private boolean isBank1Selected = false;
    private final GaiaManagerListener mListener;

    public interface GaiaManagerListener {
        void onControlNotSupported();

        void onGetFilter(int i, Filter filter);

        void onGetFrequency(int i, int i2);

        void onGetGain(int i, int i2);

        void onGetMasterGain(int i);

        void onGetQuality(int i, int i2);

        void onIncorrectState();

        boolean sendGAIAPacket(byte[] bArr);
    }

    private int buildParameterIDLowByte(int i, int i2) {
        return (i << 4) | i2;
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

    public CustomEqualizerGaiaManager(GaiaManagerListener gaiaManagerListener, int i) {
        super(i);
        this.mListener = gaiaManagerListener;
    }

    public void getMasterGain() {
        getEQParameter(0, 1);
    }

    public void getEQParameter(int i, int i2) {
        createRequest(createPacket(GAIA.COMMAND_GET_EQ_PARAMETER, new byte[]{1, (byte) buildParameterIDLowByte(i, i2)}));
    }

    public void setEQParameter(int i, int i2, int i3) {
        byte[] bArr = new byte[5];
        bArr[0] = 1;
        bArr[1] = (byte) buildParameterIDLowByte(i, i2);
        Utils.copyIntIntoByteArray(i3, bArr, 2, 2, false);
        bArr[4] = (byte) (this.isBank1Selected ? 1 : 0);
        createRequest(createPacket(GAIA.COMMAND_SET_EQ_PARAMETER, bArr));
    }

    public void getPreset() {
        createRequest(createPacket(GAIA.COMMAND_GET_EQ_CONTROL));
    }

    /* access modifiers changed from: protected */
    public void receiveSuccessfulAcknowledgement(GaiaPacket gaiaPacket) {
        int command = gaiaPacket.getCommand();
        if (command == 660) {
            receiveGetEQControlACK(gaiaPacket);
        } else if (command == 666) {
            receiveGetEQParameterACK(gaiaPacket);
        }
    }

    /* access modifiers changed from: protected */
    public void receiveUnsuccessfulAcknowledgement(GaiaPacket gaiaPacket) {
        if (gaiaPacket.getStatus() == 6) {
            this.mListener.onIncorrectState();
        } else {
            this.mListener.onControlNotSupported();
        }
    }

    /* access modifiers changed from: protected */
    public boolean sendGAIAPacket(byte[] bArr) {
        return this.mListener.sendGAIAPacket(bArr);
    }

    private void receiveGetEQParameterACK(GaiaPacket gaiaPacket) {
        byte[] payload = gaiaPacket.getPayload();
        if (DEBUG) {
            Log.d("CustomEQGaiaManager", "EQ PARAM, payload: " + Utils.getStringFromBytes(payload));
        }
        if (payload.length < 5) {
            Log.w("CustomEQGaiaManager", "Received \"COMMAND_GET_EQ_PARAMETER\" packet with missing arguments.");
            return;
        }
        int i = (payload[2] & 240) >>> 4;
        byte b = (byte) (payload[2] & OpCodes.Enum.UPGRADE_COMMIT_REQ);
        if (i == 0 && b == 1) {
            short extractShortFromByteArray = Utils.extractShortFromByteArray(payload, 3, 2, false);
            this.mListener.onGetMasterGain(extractShortFromByteArray);
            if (DEBUG) {
                Log.d("CustomEQGaiaManager", "MASTER GAIN - value: " + extractShortFromByteArray);
                return;
            }
            return;
        }
        ParameterType valueOf = ParameterType.valueOf((int) b);
        if (valueOf == null) {
            Log.w("CustomEQGaiaManager", "Received \"COMMAND_GET_EQ_PARAMETER\" packet with an unknown parameter type: " + b);
            return;
        }
        switch (valueOf) {
            case FILTER:
                int extractIntFromByteArray = Utils.extractIntFromByteArray(payload, 3, 2, false);
                Filter valueOf2 = Filter.valueOf(extractIntFromByteArray);
                if (valueOf2 == null) {
                    Log.w("CustomEQGaiaManager", "Received \"COMMAND_GET_EQ_PARAMETER\" packet with an unknown filter type: " + extractIntFromByteArray);
                    return;
                }
                this.mListener.onGetFilter(i, valueOf2);
                if (DEBUG) {
                    Log.d("CustomEQGaiaManager", "BAND: " + i + " - PARAM: " + valueOf.toString() + " - FILTER: " + valueOf2.toString());
                    return;
                }
                return;
            case FREQUENCY:
                int extractIntFromByteArray2 = Utils.extractIntFromByteArray(payload, 3, 2, false);
                this.mListener.onGetFrequency(i, extractIntFromByteArray2);
                if (DEBUG) {
                    Log.d("CustomEQGaiaManager", "BAND: " + i + " - PARAM: " + valueOf.toString() + " - FREQUENCY: " + extractIntFromByteArray2);
                    return;
                }
                return;
            case GAIN:
                int extractIntFromByteArray3 = Utils.extractIntFromByteArray(payload, 3, 2, false);
                this.mListener.onGetGain(i, extractIntFromByteArray3);
                if (DEBUG) {
                    Log.d("CustomEQGaiaManager", "BAND: " + i + " - PARAM: " + valueOf.toString() + " - GAIN: " + extractIntFromByteArray3);
                    return;
                }
                return;
            case QUALITY:
                int extractIntFromByteArray4 = Utils.extractIntFromByteArray(payload, 3, 2, false);
                this.mListener.onGetQuality(i, extractIntFromByteArray4);
                if (DEBUG) {
                    Log.d("CustomEQGaiaManager", "BAND: " + i + " - PARAM: " + valueOf.toString() + " - QUALITY: " + extractIntFromByteArray4);
                    return;
                }
                return;
            default:
                return;
        }
    }

    private void receiveGetEQControlACK(GaiaPacket gaiaPacket) {
        byte[] payload = gaiaPacket.getPayload();
        if (payload.length >= 2) {
            boolean z = true;
            if (payload[1] != 1) {
                z = false;
            }
            this.isBank1Selected = z;
        }
    }
}
