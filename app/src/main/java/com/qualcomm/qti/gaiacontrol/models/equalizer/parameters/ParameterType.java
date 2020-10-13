package com.qualcomm.qti.gaiacontrol.models.equalizer.parameters;

public enum ParameterType {
    FILTER,
    FREQUENCY,
    GAIN,
    QUALITY;
    
    private static final ParameterType[] values;

    static {
        values = values();
    }

    public static ParameterType valueOf(int i) {
        if (i < 0) {
            return null;
        }
        ParameterType[] parameterTypeArr = values;
        if (i >= parameterTypeArr.length) {
            return null;
        }
        return parameterTypeArr[i];
    }

    public static int getSize() {
        return values.length;
    }
}
