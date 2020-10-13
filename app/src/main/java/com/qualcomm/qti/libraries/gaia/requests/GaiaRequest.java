package com.qualcomm.qti.libraries.gaia.requests;

import android.annotation.SuppressLint;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacket;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class GaiaRequest {
    public GaiaPacket packet;
    public final int type;

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        public static final int ACKNOWLEDGEMENT = 2;
        public static final int SINGLE_REQUEST = 1;
        public static final int UNACKNOWLEDGED_REQUEST = 3;
    }

    public GaiaRequest(int i) {
        this.type = i;
    }
}
