package com.qualcomm.qti.libraries.vmupgrade.codes;

import android.annotation.SuppressLint;
import com.qualcomm.qti.libraries.ble.ErrorStatus;
import com.qualcomm.qti.libraries.vmupgrade.VMUUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ReturnCodes {

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Enum {
        public static final short ERROR_APP_NOT_READY = 29;
        public static final short ERROR_BAD_LENGTH_DATAHDR_RESUME = 53;
        public static final short ERROR_BAD_LENGTH_DEPRECATED = 18;
        public static final short ERROR_BAD_LENGTH_PARTITION_HEADER = 51;
        public static final short ERROR_BAD_LENGTH_PARTITION_PARSE = 48;
        public static final short ERROR_BAD_LENGTH_SIGNATURE = 52;
        public static final short ERROR_BAD_LENGTH_TOO_SHORT = 49;
        public static final short ERROR_BAD_LENGTH_UPGRADE_HEADER = 50;
        public static final short ERROR_BATTERY_LOW = 33;
        public static final short ERROR_FILE_TOO_BIG = 89;
        public static final short ERROR_FILE_TOO_SMALL = 88;
        public static final short ERROR_INTERNAL_ERROR_1 = 101;
        public static final short ERROR_INTERNAL_ERROR_2 = 102;
        public static final short ERROR_INTERNAL_ERROR_3 = 103;
        public static final short ERROR_INTERNAL_ERROR_4 = 104;
        public static final short ERROR_INTERNAL_ERROR_5 = 105;
        public static final short ERROR_INTERNAL_ERROR_6 = 106;
        public static final short ERROR_INTERNAL_ERROR_7 = 107;
        public static final short ERROR_INTERNAL_ERROR_DEPRECATED = 16;
        public static final short ERROR_INVALID_SYNC_ID = 34;
        public static final short ERROR_IN_ERROR_STATE = 35;
        public static final short ERROR_LOADER_ERROR = 30;
        public static final short ERROR_MISSING_LOADER_MSG = 32;
        public static final short ERROR_NO_MEMORY = 36;
        public static final short ERROR_OEM_VALIDATION_FAILED = 27;
        public static final short ERROR_OEM_VALIDATION_FAILED_FOOTER = 61;
        public static final short ERROR_OEM_VALIDATION_FAILED_HEADERS = 56;
        public static final short ERROR_OEM_VALIDATION_FAILED_MEMORY = 62;
        public static final short ERROR_OEM_VALIDATION_FAILED_PARTITION_DATA = 60;
        public static final short ERROR_OEM_VALIDATION_FAILED_PARTITION_HEADER1 = 58;
        public static final short ERROR_OEM_VALIDATION_FAILED_PARTITION_HEADER2 = 59;
        public static final short ERROR_OEM_VALIDATION_FAILED_UPGRADE_HEADER = 57;
        public static final short ERROR_PARTITION_CLOSE_FAILED_1 = 25;
        public static final short ERROR_PARTITION_CLOSE_FAILED_2 = 64;
        public static final short ERROR_PARTITION_CLOSE_FAILED_HEADER = 65;
        public static final short ERROR_PARTITION_CLOSE_FAILED_PS_SPACE = 66;
        public static final short ERROR_PARTITION_OPEN_FAILED = 23;
        public static final short ERROR_PARTITION_SIZE_MISMATCH = 21;
        public static final short ERROR_PARTITION_TYPE_NOT_FOUND = 22;
        public static final short ERROR_PARTITION_TYPE_NOT_MATCHING = 72;
        public static final short ERROR_PARTITION_TYPE_TWO_DFU = 73;
        public static final short ERROR_PARTITION_WRITE_FAILED = 24;
        public static final short ERROR_PARTITION_WRITE_FAILED_DATA = 81;
        public static final short ERROR_PARTITION_WRITE_FAILED_HEADER = 80;
        public static final short ERROR_SFS_VALIDATION_FAILED = 26;
        public static final short ERROR_UNEXPECTED_LOADER_MSG = 31;
        public static final short ERROR_UNKNOWN_ID = 17;
        public static final short ERROR_UPGRADE_FAILED = 28;
        public static final short ERROR_WRONG_PARTITION_NUMBER = 20;
        public static final short ERROR_WRONG_VARIANT = 19;
        public static final short UNKNOWN_ERROR = 0;
        public static final short WARN_APP_CONFIG_VERSION_INCOMPATIBLE = 128;
        public static final short WARN_SYNC_ID_IS_DIFFERENT = 129;
    }

    public static int getReturnCode(short s) {
        switch (s) {
            case 16:
                return 16;
            case 17:
                return 17;
            default:
                switch (s) {
                    case 19:
                        return 19;
                    case 20:
                        return 20;
                    case 21:
                        return 21;
                    case 22:
                        return 22;
                    case 23:
                        return 23;
                    case 24:
                        return 24;
                    case 25:
                        return 25;
                    case 26:
                        return 26;
                    case 27:
                        return 27;
                    case 28:
                        return 28;
                    case 29:
                        return 29;
                    case 30:
                        return 30;
                    case 31:
                        return 31;
                    case 32:
                        return 32;
                    case 33:
                        return 33;
                    case 34:
                        return 34;
                    case 35:
                        return 35;
                    case 36:
                        return 36;
                    default:
                        switch (s) {
                            case 48:
                                return 48;
                            case 49:
                                return 49;
                            case 50:
                                return 50;
                            case 51:
                                return 51;
                            case 52:
                                return 52;
                            case 53:
                                return 53;
                            default:
                                switch (s) {
                                    case 56:
                                        return 56;
                                    case 57:
                                        return 57;
                                    case 58:
                                        return 58;
                                    case 59:
                                        return 59;
                                    case 60:
                                        return 60;
                                    case 61:
                                        return 61;
                                    case 62:
                                        return 62;
                                    default:
                                        switch (s) {
                                            case 64:
                                                return 64;
                                            case 65:
                                                return 65;
                                            case 66:
                                                return 66;
                                            default:
                                                switch (s) {
                                                    case 72:
                                                        return 72;
                                                    case 73:
                                                        return 73;
                                                    default:
                                                        switch (s) {
                                                            case 80:
                                                                return 80;
                                                            case 81:
                                                                return 81;
                                                            default:
                                                                switch (s) {
                                                                    case 88:
                                                                        return 88;
                                                                    case 89:
                                                                        return 89;
                                                                    default:
                                                                        switch (s) {
                                                                            case 101:
                                                                                return 101;
                                                                            case 102:
                                                                                return 102;
                                                                            case 103:
                                                                                return 103;
                                                                            case 104:
                                                                                return 104;
                                                                            case 105:
                                                                                return 105;
                                                                            case 106:
                                                                                return 106;
                                                                            case 107:
                                                                                return 107;
                                                                            default:
                                                                                switch (s) {
                                                                                    case 128:
                                                                                        return 128;
                                                                                    case ErrorStatus.GattApi.GATT_INTERNAL_ERROR:
                                                                                        return ErrorStatus.GattApi.GATT_INTERNAL_ERROR;
                                                                                    default:
                                                                                        return 0;
                                                                                }
                                                                        }
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
    }

    public static String getReturnCodesMessage(int i) {
        switch (i) {
            case 16:
                return "Deprecated error: internal error (" + VMUUtils.getHexadecimalString(i) + ")";
            case 17:
                return "Error: unknown ID (" + VMUUtils.getHexadecimalString(i) + ")";
            case 18:
                return "Deprecated error: bad length (" + VMUUtils.getHexadecimalString(i) + ")";
            case 19:
                return "Error: wrong variant (" + VMUUtils.getHexadecimalString(i) + ")";
            case 20:
                return "Error: wrong partition number (" + VMUUtils.getHexadecimalString(i) + ")";
            case 21:
                return "Error: partition size mismatch (" + VMUUtils.getHexadecimalString(i) + ")";
            case 22:
                return "Error: partition type not found (" + VMUUtils.getHexadecimalString(i) + ")";
            case 23:
                return "Error: partition open failed (" + VMUUtils.getHexadecimalString(i) + ")";
            case 24:
                return "Error: partition write failed (" + VMUUtils.getHexadecimalString(i) + ")";
            case 25:
                return "Partition close failed type 1 (" + VMUUtils.getHexadecimalString(i) + ")";
            case 26:
                return "Error: SFS validation failed (" + VMUUtils.getHexadecimalString(i) + ")";
            case 27:
                return "Error: OEM validation failed (" + VMUUtils.getHexadecimalString(i) + ")";
            case 28:
                return "Error: upgrade failed (" + VMUUtils.getHexadecimalString(i) + ")";
            case 29:
                return "Error: application not ready (" + VMUUtils.getHexadecimalString(i) + ")";
            case 30:
                return "Error: loader error (" + VMUUtils.getHexadecimalString(i) + ")";
            case 31:
                return "Error: unexpected loader message (" + VMUUtils.getHexadecimalString(i) + ")";
            case 32:
                return "Error: missing loader message (" + VMUUtils.getHexadecimalString(i) + ")";
            case 33:
                return "Error: battery low (" + VMUUtils.getHexadecimalString(i) + ")";
            case 34:
                return "Error: invalid sync ID (" + VMUUtils.getHexadecimalString(i) + ")";
            case 35:
                return "Error: in error state (" + VMUUtils.getHexadecimalString(i) + ")";
            case 36:
                return "Error: no memory (" + VMUUtils.getHexadecimalString(i) + ")";
            default:
                switch (i) {
                    case 48:
                        return "Error: bad length partition parse (" + VMUUtils.getHexadecimalString(i) + ")";
                    case 49:
                        return "Error: bad length too short (" + VMUUtils.getHexadecimalString(i) + ")";
                    case 50:
                        return "Error: bad length upgrade header (" + VMUUtils.getHexadecimalString(i) + ")";
                    case 51:
                        return "Error: bad length partition header (" + VMUUtils.getHexadecimalString(i) + ")";
                    case 52:
                        return "Error: bad length signature (" + VMUUtils.getHexadecimalString(i) + ")";
                    case 53:
                        return "Error: bad length data handler resume (" + VMUUtils.getHexadecimalString(i) + ")";
                    default:
                        switch (i) {
                            case 56:
                                return "Error: OEM validation failed headers (" + VMUUtils.getHexadecimalString(i) + ")";
                            case 57:
                                return "Error: OEM validation failed upgrade header (" + VMUUtils.getHexadecimalString(i) + ")";
                            case 58:
                                return "Error: OEM validation failed partition header 1 (" + VMUUtils.getHexadecimalString(i) + ")";
                            case 59:
                                return "Error: OEM validation failed partition header 2 (" + VMUUtils.getHexadecimalString(i) + ")";
                            case 60:
                                return "Error: OEM validation failed partition data (" + VMUUtils.getHexadecimalString(i) + ")";
                            case 61:
                                return "Error: OEM validation failed footer (" + VMUUtils.getHexadecimalString(i) + ")";
                            case 62:
                                return "Error: OEM validation failed memory (" + VMUUtils.getHexadecimalString(i) + ")";
                            default:
                                switch (i) {
                                    case 64:
                                        return "Error: partition close failed type 2 (" + VMUUtils.getHexadecimalString(i) + ")";
                                    case 65:
                                        return "Error: partition close failed header (" + VMUUtils.getHexadecimalString(i) + ")";
                                    case 66:
                                        return "Error: partition close failed ps space (" + VMUUtils.getHexadecimalString(i) + ")";
                                    default:
                                        switch (i) {
                                            case 72:
                                                return "Error: partition type not matching (" + VMUUtils.getHexadecimalString(i) + ")";
                                            case 73:
                                                return "Error: partition type two DFU (" + VMUUtils.getHexadecimalString(i) + ")";
                                            default:
                                                switch (i) {
                                                    case 80:
                                                        return "Error: partition write failed header (" + VMUUtils.getHexadecimalString(i) + ")";
                                                    case 81:
                                                        return "Error: partition write failed data (" + VMUUtils.getHexadecimalString(i) + ")";
                                                    default:
                                                        switch (i) {
                                                            case 88:
                                                                return "Error: file too small (" + VMUUtils.getHexadecimalString(i) + ")";
                                                            case 89:
                                                                return "Error: file too big (" + VMUUtils.getHexadecimalString(i) + ")";
                                                            default:
                                                                switch (i) {
                                                                    case 101:
                                                                        return "Error: internal error 1 (" + VMUUtils.getHexadecimalString(i) + ")";
                                                                    case 102:
                                                                        return "Error: internal error 2 (" + VMUUtils.getHexadecimalString(i) + ")";
                                                                    case 103:
                                                                        return "Error: internal error 3 (" + VMUUtils.getHexadecimalString(i) + ")";
                                                                    case 104:
                                                                        return "Error: internal error 4 (" + VMUUtils.getHexadecimalString(i) + ")";
                                                                    case 105:
                                                                        return "Error: internal error 5 (" + VMUUtils.getHexadecimalString(i) + ")";
                                                                    case 106:
                                                                        return "Error: internal error 6 (" + VMUUtils.getHexadecimalString(i) + ")";
                                                                    case 107:
                                                                        return "Error: internal error 7 (" + VMUUtils.getHexadecimalString(i) + ")";
                                                                    default:
                                                                        switch (i) {
                                                                            case 128:
                                                                                return "Warning: application configuration version incompatible (" + VMUUtils.getHexadecimalString(i) + ")";
                                                                            case ErrorStatus.GattApi.GATT_INTERNAL_ERROR:
                                                                                return "Warning: Sync ID is different (" + VMUUtils.getHexadecimalString(i) + ")";
                                                                            default:
                                                                                return "Unknown return code (" + VMUUtils.getHexadecimalString(i) + ")";
                                                                        }
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
    }
}
