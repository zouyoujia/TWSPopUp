package com.qualcomm.qti.libraries.vmupgrade.codes;

import android.annotation.SuppressLint;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ResumePoints {
    private static final int RESUME_POINTS_COUNT = 5;

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Enum {
        public static final byte COMMIT = 4;
        public static final byte DATA_TRANSFER = 0;
        public static final byte IN_PROGRESS = 3;
        public static final byte TRANSFER_COMPLETE = 2;
        public static final byte VALIDATION = 1;
    }

    public static String getLabel(int i) {
        switch (i) {
            case 0:
                return "Data transfer";
            case 1:
                return "Data validation";
            case 2:
                return "Data transfer complete";
            case 3:
                return "Upgrade in progress";
            case 4:
                return "Upgrade commit";
            default:
                return "Initialisation";
        }
    }

    public static int getLength() {
        return 5;
    }

    public static int getResumePoint(byte b) {
        switch (b) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            default:
                return 0;
        }
    }
}
