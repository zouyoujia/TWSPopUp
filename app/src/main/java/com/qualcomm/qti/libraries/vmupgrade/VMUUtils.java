package com.qualcomm.qti.libraries.vmupgrade;

import com.qualcomm.qti.libraries.vmupgrade.packet.VMUException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class VMUUtils {
    private static final int BITS_IN_BYTE = 8;
    private static final int BYTES_IN_INT = 4;
    private static final int BYTES_IN_LONG = 8;
    private static final int BYTES_IN_SHORT = 2;
    private static final String TAG = "VMUUtils";

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

    public static long extractLongFromByteArray(byte[] bArr, int i, int i2, boolean z) {
        if (i2 <= 0 || i2 > 8) {
            throw new IndexOutOfBoundsException("Length must be between 0 and 8");
        }
        long j = 0;
        int i3 = (i2 - 1) * 8;
        if (z) {
            for (int i4 = (i2 + i) - 1; i4 >= i; i4--) {
                j |= (long) ((bArr[i4] & 255) << i3);
                i3 -= 8;
            }
        } else {
            for (int i5 = i; i5 < i + i2; i5++) {
                j |= (long) ((bArr[i5] & 255) << i3);
                i3 -= 8;
            }
        }
        return j;
    }

    public static String getHexadecimalString(int i) {
        return String.format("0x%04X", new Object[]{Integer.valueOf(i & 65535)});
    }

    public static String getHexadecimalStringTwoBytes(int i) {
        return String.format("0x%02X", new Object[]{Integer.valueOf(i & 255)});
    }

    public static String getHexadecimalStringFromBytes(byte[] bArr) {
        if (bArr == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder(bArr.length * 2);
        for (int i = 0; i < bArr.length; i++) {
            sb.append(String.format("0x%02x ", new Object[]{Byte.valueOf(bArr[i])}));
        }
        return sb.toString();
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x009f A[SYNTHETIC, Splitter:B:26:0x009f] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00dc A[SYNTHETIC, Splitter:B:33:0x00dc] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static byte[] getMD5FromFile(File r7) {
        /*
            r0 = 0
            r1 = 0
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0063, all -> 0x005f }
            r2.<init>(r7)     // Catch:{ Exception -> 0x0063, all -> 0x005f }
            r1 = 1024(0x400, float:1.435E-42)
            byte[] r1 = new byte[r1]     // Catch:{ Exception -> 0x005d }
            java.lang.String r3 = "MD5"
            java.security.MessageDigest r3 = java.security.MessageDigest.getInstance(r3)     // Catch:{ Exception -> 0x005d }
            r4 = r0
        L_0x0012:
            r5 = -1
            if (r4 == r5) goto L_0x001f
            int r4 = r2.read(r1)     // Catch:{ Exception -> 0x005d }
            if (r4 <= 0) goto L_0x0012
            r3.update(r1, r0, r4)     // Catch:{ Exception -> 0x005d }
            goto L_0x0012
        L_0x001f:
            byte[] r0 = r3.digest()     // Catch:{ Exception -> 0x005d }
            r2.close()     // Catch:{ Exception -> 0x0027 }
            goto L_0x005c
        L_0x0027:
            r1 = move-exception
            java.lang.String r2 = "VMUUtils"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Exception occurs when tried to get MD5 check sum for file: "
            r3.append(r4)
            java.lang.String r7 = r7.getName()
            r3.append(r7)
            java.lang.String r7 = r3.toString()
            android.util.Log.w(r2, r7)
            java.lang.String r7 = "VMUUtils"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Exception: "
            r2.append(r3)
            java.lang.String r1 = r1.getMessage()
            r2.append(r1)
            java.lang.String r1 = r2.toString()
            android.util.Log.w(r7, r1)
        L_0x005c:
            return r0
        L_0x005d:
            r1 = move-exception
            goto L_0x0067
        L_0x005f:
            r0 = move-exception
            r2 = r1
            goto L_0x00da
        L_0x0063:
            r2 = move-exception
            r6 = r2
            r2 = r1
            r1 = r6
        L_0x0067:
            java.lang.String r3 = "VMUUtils"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d9 }
            r4.<init>()     // Catch:{ all -> 0x00d9 }
            java.lang.String r5 = "Exception occurs when tried to get MD5 check sum for file: "
            r4.append(r5)     // Catch:{ all -> 0x00d9 }
            java.lang.String r5 = r7.getName()     // Catch:{ all -> 0x00d9 }
            r4.append(r5)     // Catch:{ all -> 0x00d9 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00d9 }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x00d9 }
            java.lang.String r3 = "VMUUtils"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d9 }
            r4.<init>()     // Catch:{ all -> 0x00d9 }
            java.lang.String r5 = "Exception: "
            r4.append(r5)     // Catch:{ all -> 0x00d9 }
            java.lang.String r1 = r1.getMessage()     // Catch:{ all -> 0x00d9 }
            r4.append(r1)     // Catch:{ all -> 0x00d9 }
            java.lang.String r1 = r4.toString()     // Catch:{ all -> 0x00d9 }
            android.util.Log.e(r3, r1)     // Catch:{ all -> 0x00d9 }
            byte[] r0 = new byte[r0]     // Catch:{ all -> 0x00d9 }
            if (r2 == 0) goto L_0x00d8
            r2.close()     // Catch:{ Exception -> 0x00a3 }
            goto L_0x00d8
        L_0x00a3:
            r1 = move-exception
            java.lang.String r2 = "VMUUtils"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Exception occurs when tried to get MD5 check sum for file: "
            r3.append(r4)
            java.lang.String r7 = r7.getName()
            r3.append(r7)
            java.lang.String r7 = r3.toString()
            android.util.Log.w(r2, r7)
            java.lang.String r7 = "VMUUtils"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Exception: "
            r2.append(r3)
            java.lang.String r1 = r1.getMessage()
            r2.append(r1)
            java.lang.String r1 = r2.toString()
            android.util.Log.w(r7, r1)
        L_0x00d8:
            return r0
        L_0x00d9:
            r0 = move-exception
        L_0x00da:
            if (r2 == 0) goto L_0x0115
            r2.close()     // Catch:{ Exception -> 0x00e0 }
            goto L_0x0115
        L_0x00e0:
            r1 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Exception occurs when tried to get MD5 check sum for file: "
            r2.append(r3)
            java.lang.String r7 = r7.getName()
            r2.append(r7)
            java.lang.String r7 = r2.toString()
            java.lang.String r2 = "VMUUtils"
            android.util.Log.w(r2, r7)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r2 = "Exception: "
            r7.append(r2)
            java.lang.String r1 = r1.getMessage()
            r7.append(r1)
            java.lang.String r7 = r7.toString()
            java.lang.String r1 = "VMUUtils"
            android.util.Log.w(r1, r7)
        L_0x0115:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qualcomm.qti.libraries.vmupgrade.VMUUtils.getMD5FromFile(java.io.File):byte[]");
    }

    public static byte[] getBytesFromFile(File file) throws VMUException {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            long length = file.length();
            int i = (int) length;
            if (((long) i) == length) {
                byte[] bArr = new byte[i];
                int read = fileInputStream.read(bArr);
                fileInputStream.close();
                if (read != i) {
                    if (read != -1 || i != Integer.MAX_VALUE) {
                        throw new VMUException(3);
                    }
                }
                return bArr;
            }
            throw new VMUException(2);
        } catch (IOException e) {
            throw new VMUException(3, e.getMessage());
        }
    }

    public static void copyIntIntoByteArray(int i, byte[] bArr, int i2, int i3, boolean z) {
        if (i3 <= 0 || i3 > 4) {
            throw new IndexOutOfBoundsException("Length must be between 0 and 4");
        } else if (bArr.length >= i2 + i3) {
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
        } else {
            throw new IndexOutOfBoundsException("The targeted location must be contained in the target array.");
        }
    }
}
