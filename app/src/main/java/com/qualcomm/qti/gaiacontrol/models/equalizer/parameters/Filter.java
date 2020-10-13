package com.qualcomm.qti.gaiacontrol.models.equalizer.parameters;

public enum Filter {
    BYPASS,
    LOW_PASS_1,
    HIGH_PASS_1,
    ALL_PASS_1,
    LOW_SHELF_1,
    HIGH_SHELF_1,
    TILT_1,
    LOW_PASS_2,
    HIGH_PASS_2,
    ALL_PASS_2,
    LOW_SHELF_2,
    HIGH_SHELF_2,
    TILT_2,
    PARAMETRIC_EQUALIZER;
    
    private static final Filter[] values;

    static {
        values = values();
    }

    public static Filter valueOf(int i) {
        if (i < 0) {
            return null;
        }
        Filter[] filterArr = values;
        if (i >= filterArr.length) {
            return null;
        }
        return filterArr[i];
    }

    public static int getSize() {
        return values.length;
    }

    public static void defineParameters(Filter filter, Parameter parameter, Parameter parameter2, Parameter parameter3) {
        Parameter parameter4 = parameter;
        Parameter parameter5 = parameter2;
        Parameter parameter6 = parameter3;
        switch (filter) {
            case HIGH_PASS_1:
            case ALL_PASS_1:
            case LOW_PASS_1:
                parameter4.setConfigurable(0.333d, 20000.0d);
                parameter2.setNotConfigurable();
                parameter3.setNotConfigurable();
                return;
            case HIGH_PASS_2:
            case ALL_PASS_2:
            case LOW_PASS_2:
                parameter4.setConfigurable(40.0d, 20000.0d);
                parameter2.setNotConfigurable();
                parameter6.setConfigurable(0.25d, 2.0d);
                return;
            case LOW_SHELF_1:
            case HIGH_SHELF_1:
            case TILT_1:
                parameter4.setConfigurable(20.0d, 20000.0d);
                parameter5.setConfigurable(-12.0d, 12.0d);
                parameter3.setNotConfigurable();
                return;
            case LOW_SHELF_2:
            case HIGH_SHELF_2:
            case TILT_2:
                parameter4.setConfigurable(40.0d, 20000.0d);
                parameter5.setConfigurable(-12.0d, 12.0d);
                parameter6.setConfigurable(0.25d, 2.0d);
                return;
            case BYPASS:
                parameter.setNotConfigurable();
                parameter2.setNotConfigurable();
                parameter3.setNotConfigurable();
                return;
            case PARAMETRIC_EQUALIZER:
                parameter4.setConfigurable(20.0d, 20000.0d);
                parameter5.setConfigurable(-36.0d, 12.0d);
                parameter6.setConfigurable(0.25d, 8.0d);
                return;
            default:
                return;
        }
    }
}
