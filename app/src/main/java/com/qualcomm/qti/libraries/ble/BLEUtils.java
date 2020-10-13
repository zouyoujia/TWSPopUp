package com.qualcomm.qti.libraries.ble;

import com.qualcomm.qti.libraries.ble.ErrorStatus;

public class BLEUtils {
    public static String getBondStateName(int i) {
        return i == 10 ? "BOND_NONE" : i == 12 ? "BOND_BONDED" : i == 11 ? "BOND_BONDING" : "UNKNOWN";
    }

    public static String getConnectionStateName(int i) {
        return i == 2 ? "CONNECTED" : i == 1 ? "CONNECTING" : i == 3 ? "DISCONNECTING" : i == 0 ? "DISCONNECTED" : "UNKNOWN";
    }

    public static String getIntToHexadecimal(int i) {
        return String.format("0x%04X", new Object[]{Integer.valueOf(i & 65535)});
    }

    public static String getGattStatusName(int i, boolean z) {
        String intToHexadecimal = getIntToHexadecimal(i);
        StringBuilder sb = new StringBuilder();
        if (i == 0) {
            sb.append("Status ");
            sb.append(intToHexadecimal);
            sb.append(": SUCCESS");
        } else {
            boolean z2 = false;
            sb.append("Error status ");
            sb.append(intToHexadecimal);
            sb.append(": ");
            String bluetoothGattStatusLabel = ErrorStatus.getBluetoothGattStatusLabel(i, z);
            if (bluetoothGattStatusLabel.length() > 0) {
                sb.append("\n\t> BluetoothGatt - ");
                sb.append(bluetoothGattStatusLabel);
                z2 = true;
            }
            String label = ErrorStatus.ATT.getLabel(i, z);
            if (label.length() > 0) {
                sb.append("\n\t> ATT - ");
                sb.append(label);
                z2 = true;
            }
            String label2 = ErrorStatus.HCI.getLabel(i, z);
            if (label2.length() > 0) {
                sb.append("\n\t> HCI - ");
                sb.append(label2);
                z2 = true;
            }
            String label3 = ErrorStatus.GattApi.getLabel(i, z);
            if (label3.length() > 0) {
                sb.append("\n\t> gatt_api.h - ");
                sb.append(label3);
                z2 = true;
            }
            if (!z2) {
                sb.append("UNDEFINED");
            }
        }
        return sb.toString();
    }
}
