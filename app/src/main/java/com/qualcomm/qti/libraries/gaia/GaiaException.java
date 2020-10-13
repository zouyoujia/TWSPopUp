package com.qualcomm.qti.libraries.gaia;

import android.annotation.SuppressLint;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class GaiaException extends Exception {
    private final int mCommand;
    private final int mType;

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        public static final int PACKET_IS_ALREADY_AN_ACKNOWLEDGEMENT = 1;
        public static final int PACKET_NOT_AN_ACKNOWLEDGMENT = 4;
        public static final int PACKET_NOT_A_NOTIFICATION = 2;
        public static final int PACKET_PAYLOAD_INVALID_PARAMETER = 3;
        public static final int PAYLOAD_LENGTH_TOO_LONG = 0;
    }

    public GaiaException(int i) {
        this.mType = i;
        this.mCommand = -1;
    }

    public GaiaException(int i, int i2) {
        this.mType = i;
        this.mCommand = i2;
    }

    public int getType() {
        return this.mType;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch (this.mType) {
            case 0:
                sb.append("Build of a packet failed: the payload length is bigger than the authorized packet length.");
                break;
            case 1:
                sb.append("Build of a packet failed: the packet is already an acknowledgement packet: not possible to create an acknowledgement packet from it.");
                break;
            case 2:
                sb.append("Packet is not a COMMAND NOTIFICATION");
                if (this.mCommand >= 0) {
                    sb.append(", received command: ");
                    sb.append(GaiaUtils.getGAIACommandToString(this.mCommand));
                    break;
                }
                break;
            case 3:
                sb.append("Payload is missing argument");
                if (this.mCommand >= 0) {
                    sb.append(" for command: ");
                    sb.append(GaiaUtils.getGAIACommandToString(this.mCommand));
                    break;
                }
                break;
            case 4:
                sb.append("The packet is not an acknowledgement, ");
                if (this.mCommand >= 0) {
                    sb.append(" received command: ");
                    sb.append(GaiaUtils.getGAIACommandToString(this.mCommand));
                    break;
                }
                break;
            default:
                sb.append("Gaia Exception occurred.");
                break;
        }
        return sb.toString();
    }
}
