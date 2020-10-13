package com.qualcomm.qti.gaiacontrol.models.equalizer.parameters;

import java.text.DecimalFormat;

public class Frequency extends Parameter {
    private static final int FACTOR = 3;
    private final DecimalFormat mDecimalFormat = new DecimalFormat();
    private final LogValues mLogValues = new LogValues();

    public int getFactor() {
        return 3;
    }

    public Frequency() {
        super(ParameterType.FREQUENCY);
    }

    /* access modifiers changed from: package-private */
    public String getLabel(double d) {
        if (!this.isConfigurable) {
            return "- Hz";
        }
        if (d < 50.0d) {
            this.mDecimalFormat.setMaximumFractionDigits(1);
            return this.mDecimalFormat.format(d) + " Hz";
        } else if (d < 1000.0d) {
            this.mDecimalFormat.setMaximumFractionDigits(0);
            return this.mDecimalFormat.format(d) + " Hz";
        } else {
            this.mDecimalFormat.setMaximumFractionDigits(1);
            return this.mDecimalFormat.format(d / 1000.0d) + " kHz";
        }
    }

    public int getPositionValue() {
        return (int) Math.round((((double) this.mLogValues.rangeLength) * (Math.log((double) getValue()) - this.mLogValues.logMin)) / this.mLogValues.logRange);
    }

    public void setConfigurable(double d, double d2) {
        super.setConfigurable(d, d2);
        this.mLogValues.rangeLength = getMaxBound() - getMinBound();
        this.mLogValues.logMax = Math.log((double) getMaxBound());
        this.mLogValues.logMin = Math.log((double) getMinBound());
        LogValues logValues = this.mLogValues;
        logValues.logRange = logValues.logMax - this.mLogValues.logMin;
    }

    public void setValueFromProportion(int i) {
        setValue((int) Math.round(Math.exp(this.mLogValues.logMin + ((((double) i) * this.mLogValues.logRange) / ((double) this.mLogValues.rangeLength)))));
    }

    private class LogValues {
        double logMax;
        double logMin;
        double logRange;
        int rangeLength;

        private LogValues() {
        }
    }
}
