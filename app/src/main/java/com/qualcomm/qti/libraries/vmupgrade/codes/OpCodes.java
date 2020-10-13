package com.qualcomm.qti.libraries.vmupgrade.codes;

import android.annotation.SuppressLint;
import com.qualcomm.qti.libraries.vmupgrade.VMUUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class OpCodes {

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Enum {
        public static final byte UPGRADE_ABORT_CFM = 8;
        public static final byte UPGRADE_ABORT_REQ = 7;
        public static final byte UPGRADE_COMMIT_CFM = 16;
        public static final byte UPGRADE_COMMIT_REQ = 15;
        public static final byte UPGRADE_COMPLETE_IND = 18;
        public static final byte UPGRADE_DATA = 4;
        public static final byte UPGRADE_DATA_BYTES_REQ = 3;
        public static final byte UPGRADE_ERASE_SQIF_CFM = 30;
        public static final byte UPGRADE_ERASE_SQIF_REQ = 29;
        public static final byte UPGRADE_ERROR_WARN_IND = 17;
        public static final byte UPGRADE_ERROR_WARN_RES = 31;
        public static final byte UPGRADE_IN_PROGRESS_IND = 13;
        public static final byte UPGRADE_IN_PROGRESS_RES = 14;
        public static final byte UPGRADE_IS_VALIDATION_DONE_CFM = 23;
        public static final byte UPGRADE_IS_VALIDATION_DONE_REQ = 22;
        public static final byte UPGRADE_PROGRESS_CFM = 10;
        public static final byte UPGRADE_PROGRESS_REQ = 9;
        public static final byte UPGRADE_RESUME_IND = 6;
        public static final byte UPGRADE_START_CFM = 2;
        public static final byte UPGRADE_START_DATA_REQ = 21;
        public static final byte UPGRADE_START_REQ = 1;
        public static final byte UPGRADE_SUSPEND_IND = 5;
        public static final byte UPGRADE_SYNC_AFTER_REBOOT_REQ = 24;
        public static final byte UPGRADE_SYNC_CFM = 20;
        public static final byte UPGRADE_SYNC_REQ = 19;
        public static final byte UPGRADE_TRANSFER_COMPLETE_IND = 11;
        public static final byte UPGRADE_TRANSFER_COMPLETE_RES = 12;
        public static final byte UPGRADE_VARIANT_CFM = 28;
        public static final byte UPGRADE_VARIANT_REQ = 27;
        public static final byte UPGRADE_VERSION_CFM = 26;
        public static final byte UPGRADE_VERSION_REQ = 25;
    }

    public static int getOpCode(byte b) {
        switch (b) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 7:
                return 7;
            case 8:
                return 8;
            case 9:
                return 9;
            case 10:
                return 10;
            case 11:
                return 11;
            case 12:
                return 12;
            case 13:
                return 13;
            case 14:
                return 14;
            case 15:
                return 15;
            case 16:
                return 16;
            case 17:
                return 17;
            case 18:
                return 18;
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
            default:
                return -1;
        }
    }

    public class UpgradeStartREQ {
        public static final byte DATA_LENGTH = 0;

        public UpgradeStartREQ() {
        }
    }

    public class UpgradeStartCFM {
        public static final int BATTERY_LEVEL_LENGTH = 2;
        public static final int BATTERY_LEVEL_OFFSET = 1;
        public static final byte DATA_LENGTH = 3;
        public static final int STATUS_LENGTH = 1;
        public static final int STATUS_OFFSET = 0;

        public UpgradeStartCFM() {
        }

        public class Status {
            public static final byte ERROR_APP_NOT_READY = 9;
            public static final byte SUCCESS = 0;

            public Status() {
            }
        }
    }

    public class UpgradeDataBytesREQ {
        public static final int DATA_LENGTH = 8;
        public static final int FILE_OFFSET_LENGTH = 4;
        public static final int FILE_OFFSET_OFFSET = 4;
        public static final int NB_BYTES_LENGTH = 4;
        public static final int NB_BYTES_OFFSET = 0;

        public UpgradeDataBytesREQ() {
        }
    }

    public class UpgradeData {
        public static final int FILE_BYTES_OFFSET = 1;
        public static final int LAST_PACKET_LENGTH = 1;
        public static final int LAST_PACKET_OFFSET = 0;
        public static final int MIN_DATA_LENGTH = 1;

        public UpgradeData() {
        }

        public class LastPacket {
            public static final byte IS_LAST_PACKET = 1;
            public static final byte IS_NOT_LAST_PACKET = 0;

            public LastPacket() {
            }
        }
    }

    public class UpgradeAbortREQ {
        public static final int DATA_LENGTH = 0;

        public UpgradeAbortREQ() {
        }
    }

    public class UpgradeAbortCFM {
        public static final int DATA_LENGTH = 0;

        public UpgradeAbortCFM() {
        }
    }

    public class UpgradeTransferCompleteIND {
        public static final int DATA_LENGTH = 0;

        public UpgradeTransferCompleteIND() {
        }
    }

    public class UpgradeTransferCompleteRES {
        public static final int ACTION_LENGTH = 1;
        public static final int ACTION_OFFSET = 0;
        public static final int DATA_LENGTH = 1;

        public UpgradeTransferCompleteRES() {
        }

        public class Action {
            public static final byte ABORT = 1;
            public static final byte CONTINUE = 0;

            public Action() {
            }
        }
    }

    public class UpgradeInProgressRES {
        public static final int ACTION_LENGTH = 1;
        public static final int ACTION_OFFSET = 0;
        public static final int DATA_LENGTH = 1;

        public UpgradeInProgressRES() {
        }

        public class Action {
            public static final byte ABORT = 1;
            public static final byte CONTINUE = 0;

            public Action() {
            }
        }
    }

    public class UpgradeCommitREQ {
        public static final int DATA_LENGTH = 0;

        public UpgradeCommitREQ() {
        }
    }

    public class UpgradeCommitCFM {
        public static final int ACTION_LENGTH = 1;
        public static final int ACTION_OFFSET = 0;
        public static final int DATA_LENGTH = 1;

        public UpgradeCommitCFM() {
        }

        public class Action {
            public static final byte ABORT = 1;
            public static final byte CONTINUE = 0;

            public Action() {
            }
        }
    }

    public class UpgradeErrorWarnIND {
        public static final int DATA_LENGTH = 2;
        public static final int RETURN_CODE_LENGTH = 2;
        public static final int RETURN_CODE_OFFSET = 0;

        public UpgradeErrorWarnIND() {
        }
    }

    public class UpgradeCompleteIND {
        public static final int DATA_LENGTH = 0;

        public UpgradeCompleteIND() {
        }
    }

    public class UpgradeSyncREQ {
        public static final int DATA_LENGTH = 4;
        public static final int IDENTIFIER_LENGTH = 4;
        public static final int IDENTIFIER_OFFSET = 0;

        public UpgradeSyncREQ() {
        }
    }

    public class UpgradeSyncCFM {
        public static final int DATA_LENGTH = 6;
        public static final int IDENTIFIER_LENGTH = 4;
        public static final int IDENTIFIER_OFFSET = 1;
        public static final int PROTOCOL_VERSION_LENGTH = 1;
        public static final int PROTOCOL_VERSION_OFFSET = 5;
        public static final int RESUME_POINT_LENGTH = 1;
        public static final int RESUME_POINT_OFFSET = 0;

        public UpgradeSyncCFM() {
        }
    }

    public class UpgradeStartDataREQ {
        public static final int DATA_LENGTH = 0;

        public UpgradeStartDataREQ() {
        }
    }

    public class UpgradeIsValidationDoneREQ {
        public static final int DATA_LENGTH = 0;

        public UpgradeIsValidationDoneREQ() {
        }
    }

    public class UpgradeIsValidationDoneCFM {
        public static final int DATA_LENGTH = 2;
        public static final int WAITING_TIME_LENGTH = 2;
        public static final int WAITING_TIME_OFFSET = 0;

        public UpgradeIsValidationDoneCFM() {
        }
    }

    public class UpgradeErrorWarnRES {
        public static final int DATA_LENGTH = 2;
        public static final int ERROR_LENGTH = 2;
        public static final int ERROR_OFFSET = 0;

        public UpgradeErrorWarnRES() {
        }
    }

    public static String getString(int i) {
        switch (i) {
            case 1:
                return "UPGRADE_START_REQ " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 2:
                return "UPGRADE_START_CFM " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 3:
                return "UPGRADE_DATA_BYTES_REQ " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 4:
                return "UPGRADE_DATA " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 5:
                return "UPGRADE_SUSPEND_IND " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 6:
                return "UPGRADE_RESUME_IND " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 7:
                return "UPGRADE_ABORT_REQ " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 8:
                return "UPGRADE_ABORT_CFM " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 9:
                return "UPGRADE_PROGRESS_REQ " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 10:
                return "UPGRADE_PROGRESS_CFM " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 11:
                return "UPGRADE_TRANSFER_COMPLETE_IND " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 12:
                return "UPGRADE_TRANSFER_COMPLETE_RES " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 13:
                return "UPGRADE_IN_PROGRESS_IND " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 14:
                return "UPGRADE_IN_PROGRESS_RES " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 15:
                return "UPGRADE_COMMIT_REQ " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 16:
                return "UPGRADE_COMMIT_CFM " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 17:
                return "UPGRADE_ERROR_WARN_IND " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 18:
                return "UPGRADE_COMPLETE_IND " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 19:
                return "UPGRADE_SYNC_REQ " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 20:
                return "UPGRADE_SYNC_CFM " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 21:
                return "UPGRADE_START_DATA_REQ " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 22:
                return "UPGRADE_IS_VALIDATION_DONE_REQ " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 23:
                return "UPGRADE_IS_VALIDATION_DONE_CFM " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 24:
                return "UPGRADE_SYNC_AFTER_REBOOT_REQ " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 25:
                return "UPGRADE_VERSION_REQ " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 26:
                return "UPGRADE_VERSION_CFM " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 27:
                return "UPGRADE_VARIANT_REQ " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 28:
                return "UPGRADE_VARIANT_CFM " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 29:
                return "UPGRADE_ERASE_SQIF_REQ " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 30:
                return "UPGRADE_ERASE_SQIF_CFM " + VMUUtils.getHexadecimalStringTwoBytes(i);
            case 31:
                return "UPGRADE_ERROR_WARN_RES " + VMUUtils.getHexadecimalStringTwoBytes(i);
            default:
                return "UNKNOWN OPCODE " + VMUUtils.getHexadecimalStringTwoBytes(i);
        }
    }
}
