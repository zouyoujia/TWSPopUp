package com.qualcomm.qti.gaiacontrol.models.equalizer.parameters;

import java.text.DecimalFormat;

public class Gain extends Parameter {
    private static final int FACTOR = 60;
    private final DecimalFormat mDecimalFormat = new DecimalFormat();

    public int getFactor() {
        return 60;
    }

    public Gain() {
        super(ParameterType.GAIN);
    }

    /* access modifiers changed from: package-private */
    public String getLabel(double d) {
        if (!this.isConfigurable) {
            return "- dB";
        }
        this.mDecimalFormat.setMaximumFractionDigits(1);
        return this.mDecimalFormat.format(d) + " dB";
    }
}
