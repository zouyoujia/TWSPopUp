package com.qualcomm.qti.gaiacontrol.models.equalizer;

import com.qualcomm.qti.gaiacontrol.models.equalizer.parameters.Filter;
import com.qualcomm.qti.gaiacontrol.models.equalizer.parameters.Frequency;
import com.qualcomm.qti.gaiacontrol.models.equalizer.parameters.Gain;
import com.qualcomm.qti.gaiacontrol.models.equalizer.parameters.Parameter;
import com.qualcomm.qti.gaiacontrol.models.equalizer.parameters.ParameterType;
import com.qualcomm.qti.gaiacontrol.models.equalizer.parameters.Quality;

public class Band {
    private boolean isFilterUpToDate = false;
    private Filter mFilter = Filter.BYPASS;
    private final Parameter[] mParameters = new Parameter[ParameterType.getSize()];

    Band() {
        this.mParameters[ParameterType.FREQUENCY.ordinal()] = new Frequency();
        this.mParameters[ParameterType.GAIN.ordinal()] = new Gain();
        this.mParameters[ParameterType.QUALITY.ordinal()] = new Quality();
    }

    public void setFilter(Filter filter, boolean z) {
        this.mFilter = filter;
        Filter.defineParameters(filter, this.mParameters[ParameterType.FREQUENCY.ordinal()], this.mParameters[ParameterType.GAIN.ordinal()], this.mParameters[ParameterType.QUALITY.ordinal()]);
        if (!z) {
            this.isFilterUpToDate = true;
        }
    }

    public Filter getFilter() {
        return this.mFilter;
    }

    public Parameter getFrequency() {
        return this.mParameters[ParameterType.FREQUENCY.ordinal()];
    }

    public Parameter getGain() {
        return this.mParameters[ParameterType.GAIN.ordinal()];
    }

    public Parameter getQuality() {
        return this.mParameters[ParameterType.QUALITY.ordinal()];
    }

    public boolean isUpToDate() {
        int i = 1;
        while (true) {
            Parameter[] parameterArr = this.mParameters;
            if (i >= parameterArr.length) {
                return this.isFilterUpToDate;
            }
            if (parameterArr[i].isConfigurable() && !this.mParameters[i].isUpToDate()) {
                return false;
            }
            i++;
        }
    }

    public void hasToBeUpdated() {
        this.isFilterUpToDate = false;
        int i = 1;
        while (true) {
            Parameter[] parameterArr = this.mParameters;
            if (i < parameterArr.length) {
                parameterArr[i].hasTobeUpdated();
                i++;
            } else {
                return;
            }
        }
    }
}
