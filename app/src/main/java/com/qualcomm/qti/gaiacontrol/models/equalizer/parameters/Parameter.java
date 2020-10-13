package com.qualcomm.qti.gaiacontrol.models.equalizer.parameters;

public abstract class Parameter {
    private static final int MAX_BOUND_OFFSET = 1;
    private static final int MIN_BOUND_OFFSET = 0;
    boolean isConfigurable = false;
    private boolean isUpToDate = false;
    private final int mFactor;
    private final String[] mLabelBounds = new String[2];
    private final int[] mRawBounds = new int[2];
    private int mRawValue;
    private final ParameterType mType;

    /* access modifiers changed from: package-private */
    public abstract int getFactor();

    /* access modifiers changed from: package-private */
    public abstract String getLabel(double d);

    Parameter(ParameterType parameterType) {
        this.mType = parameterType;
        this.mFactor = getFactor();
    }

    public ParameterType getParameterType() {
        return this.mType;
    }

    public int getValue() {
        return this.mRawValue;
    }

    public int getPositionValue() {
        return this.mRawValue - this.mRawBounds[0];
    }

    public int getBoundsLength() {
        int[] iArr = this.mRawBounds;
        return iArr[1] - iArr[0];
    }

    public boolean isConfigurable() {
        return this.isConfigurable;
    }

    public boolean isUpToDate() {
        return this.isUpToDate;
    }

    public String getLabelMinBound() {
        return this.isConfigurable ? this.mLabelBounds[0] : "";
    }

    public String getLabelMaxBound() {
        return this.isConfigurable ? this.mLabelBounds[1] : "";
    }

    public String getLabelValue() {
        return getLabel(((double) this.mRawValue) / ((double) this.mFactor));
    }

    /* access modifiers changed from: package-private */
    public int getMinBound() {
        return this.mRawBounds[0];
    }

    /* access modifiers changed from: package-private */
    public int getMaxBound() {
        return this.mRawBounds[1];
    }

    public void setValue(int i) {
        this.isUpToDate = true;
        this.mRawValue = i;
    }

    public void setValueFromProportion(int i) {
        this.mRawValue = i + this.mRawBounds[0];
    }

    public void setConfigurable(double d, double d2) {
        this.isConfigurable = true;
        setBound(0, d);
        setBound(1, d2);
    }

    /* access modifiers changed from: package-private */
    public void setNotConfigurable() {
        this.isConfigurable = false;
    }

    public void hasTobeUpdated() {
        this.isUpToDate = false;
    }

    private void setBound(int i, double d) {
        this.mLabelBounds[i] = getLabel(d);
        this.mRawBounds[i] = (int) (d * ((double) this.mFactor));
    }
}
