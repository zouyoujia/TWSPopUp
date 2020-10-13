package com.qualcomm.qti.gaiacontrol;

import android.content.Context;
import android.graphics.drawable.Drawable;
import java.text.DecimalFormat;

public class Utils {
    private static final int BITS_IN_BYTE = 8;
    public static final int BITS_IN_HEXADECIMAL = 4;
    public static final int BYTES_IN_INT = 4;
    private static final int BYTES_IN_SHORT = 2;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();

    public static String getStringFromBytes(byte[] bArr) {
        if (bArr == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder(bArr.length * 2);
        for (int i = 0; i < bArr.length; i++) {
            sb.append(String.format("0x%02x ", new Object[]{Byte.valueOf(bArr[i])}));
        }
        return sb.toString();
    }

    public static String getIntToHexadecimal(int i) {
        return String.format("0x%04X", new Object[]{Integer.valueOf(i & 65535)});
    }

    public static void copyIntIntoByteArray(int i, byte[] bArr, int i2, int i3, boolean z) {
        if (i3 <= 0 || i3 > 4) {
            throw new IndexOutOfBoundsException("Length must be between 0 and 4");
        }
        int i4 = 0;
        if (z) {
            int i5 = 0;
            for (int i6 = i3 - 1; i6 >= 0; i6--) {
                bArr[i5 + i2] = (byte) (((255 << i4) & i) >> i4);
                i4 += 8;
                i5++;
            }
            return;
        }
        int i7 = (i3 - 1) * 8;
        while (i4 < i3) {
            bArr[i4 + i2] = (byte) (((255 << i7) & i) >> i7);
            i7 -= 8;
            i4++;
        }
    }

    public static short extractShortFromByteArray(byte[] bArr, int i, int i2, boolean z) {
        if (i2 <= 0 || i2 > 2) {
            throw new IndexOutOfBoundsException("Length must be between 0 and 2");
        }
        short s = 0;
        int i3 = (i2 - 1) * 8;
        if (z) {
            for (int i4 = (i2 + i) - 1; i4 >= i; i4--) {
                s = (short) (((bArr[i4] & 255) << i3) | s);
                i3 -= 8;
            }
        } else {
            for (int i5 = i; i5 < i + i2; i5++) {
                s = (short) (s | ((bArr[i5] & 255) << i3));
                i3 -= 8;
            }
        }
        return s;
    }

    public static int extractIntFromByteArray(byte[] bArr, int i, int i2, boolean z) {
        if (i2 <= 0 || i2 > 4) {
            throw new IndexOutOfBoundsException("Length must be between 0 and 4");
        }
        int i3 = 0;
        int i4 = (i2 - 1) * 8;
        if (z) {
            for (int i5 = (i2 + i) - 1; i5 >= i; i5--) {
                i3 |= (bArr[i5] & 255) << i4;
                i4 -= 8;
            }
        } else {
            for (int i6 = i; i6 < i + i2; i6++) {
                i3 |= (bArr[i6] & 255) << i4;
                i4 -= 8;
            }
        }
        return i3;
    }

    public static String getStringForPercentage(double d) {
        if (d <= 1.0d) {
            DECIMAL_FORMAT.setMaximumFractionDigits(2);
        } else {
            DECIMAL_FORMAT.setMaximumFractionDigits(1);
        }
        return DECIMAL_FORMAT.format(d) + " " + "%";
    }
}
