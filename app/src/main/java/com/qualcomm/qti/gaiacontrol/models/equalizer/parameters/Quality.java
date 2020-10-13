package com.qualcomm.qti.gaiacontrol.models.equalizer.parameters;

import java.text.DecimalFormat;

public class Quality extends Parameter {
    private static final int FACTOR = 4096;
    private final DecimalFormat mDecimalFormat = new DecimalFormat();

    public int getFactor() {
        return 4096;
    }

    public Quality() {
        super(ParameterType.QUALITY);
    }

    /* access modifiers changed from: package-private */
    public String getLabel(double d) {
        if (!this.isConfigurable) {
            return "-";
        }
        this.mDecimalFormat.setMaximumFractionDigits(2);
        return this.mDecimalFormat.format(d);
    }
}
