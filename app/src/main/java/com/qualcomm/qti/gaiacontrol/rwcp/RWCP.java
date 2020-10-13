package com.qualcomm.qti.gaiacontrol.rwcp;

import android.annotation.SuppressLint;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class RWCP {
    static final int DATA_TIMEOUT_MS_DEFAULT = 100;
    static final int DATA_TIMEOUT_MS_MAX = 2000;
    static final int RST_TIMEOUT_MS = 1000;
    static final int SEQUENCE_NUMBER_MAX = 63;
    static final int SYN_TIMEOUT_MS = 1000;
    static final int WINDOW_DEFAULT = 15;
    public static final int WINDOW_MAX = 32;

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    @interface State {
        public static final int CLOSING = 3;
        public static final int ESTABLISHED = 2;
        public static final int LISTEN = 0;
        public static final int SYN_SENT = 1;
    }

    static class OpCode {
        static final byte NONE = -1;

        OpCode() {
        }

        class Client {
            static final byte DATA = 0;
            static final byte RESERVED = 3;
            static final byte RST = 2;
            static final byte SYN = 1;

            Client() {
            }
        }

        class Server {
            static final byte DATA_ACK = 0;
            static final byte GAP = 3;
            static final byte RST = 2;
            static final byte RST_ACK = 2;
            static final byte SYN_ACK = 1;

            Server() {
            }
        }
    }

    public static class Segment {
        static final int HEADER_LENGTH = 1;
        static final int HEADER_OFFSET = 0;
        static final int PAYLOAD_OFFSET = 1;
        public static final int REQUIRED_INFORMATION_LENGTH = 1;

        static class Header {
            static final int OPERATION_CODE_BITS_LENGTH = 2;
            static final int OPERATION_CODE_BIT_OFFSET = 6;
            static final int SEQUENCE_NUMBER_BITS_LENGTH = 6;
            static final int SEQUENCE_NUMBER_BIT_OFFSET = 0;

            Header() {
            }
        }
    }

    static String getStateLabel(int i) {
        switch (i) {
            case 0:
                return "LISTEN";
            case 1:
                return "SYN_SENT";
            case 2:
                return "ESTABLISHED";
            case 3:
                return "CLOSING";
            default:
                return "Unknown state (" + i + ")";
        }
    }
}
