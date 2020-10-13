package com.qualcomm.qti.gaiacontrol.models.equalizer;

import com.qualcomm.qti.gaiacontrol.models.equalizer.parameters.MasterGain;
import com.qualcomm.qti.gaiacontrol.models.equalizer.parameters.Parameter;

public class Bank {
    private static final double MASTER_GAIN_MAX = 12.0d;
    private static final double MASTER_GAIN_MIN = -36.0d;
    private final Band[] mBands;
    private int mCurrentBand;
    private final Parameter mMasterGain = new MasterGain();

    public Bank(int i) {
        this.mBands = new Band[i];
        for (int i2 = 0; i2 < i; i2++) {
            this.mBands[i2] = new Band();
        }
        this.mCurrentBand = 1;
        this.mMasterGain.setConfigurable(MASTER_GAIN_MIN, MASTER_GAIN_MAX);
    }

    public void setCurrentBand(int i) {
        if (i < 1) {
            i = 1;
        } else {
            Band[] bandArr = this.mBands;
            if (i >= bandArr.length) {
                i = bandArr.length;
            }
        }
        this.mCurrentBand = i;
    }

    public int getNumberCurrentBand() {
        return this.mCurrentBand;
    }

    public Band getCurrentBand() {
        return this.mBands[this.mCurrentBand - 1];
    }

    public Band getBand(int i) {
        if (i < 1) {
            i = 1;
        } else {
            Band[] bandArr = this.mBands;
            if (i > bandArr.length) {
                i = bandArr.length;
            }
        }
        return this.mBands[i - 1];
    }

    public Parameter getMasterGain() {
        return this.mMasterGain;
    }

    public void hasToBeUpdated() {
        for (Band hasToBeUpdated : this.mBands) {
            hasToBeUpdated.hasToBeUpdated();
        }
    }
}
