package com.qualcomm.qti.gaiacontrol.models.gatt;

import android.bluetooth.BluetoothGattService;
import androidx.collection.SimpleArrayMap;
import com.qualcomm.qti.gaiacontrol.models.gatt.GATT;
import java.util.List;

public class GATTServices {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public final SimpleArrayMap<Integer, GattServiceBattery> gattServiceBatteries = new SimpleArrayMap<>();
    public final GattServiceDeviceInformation gattServiceDeviceInformation = new GattServiceDeviceInformation();
    public final GattServiceGaia gattServiceGaia = new GattServiceGaia();
    public final GattServiceHeartRate gattServiceHeartRate = new GattServiceHeartRate();
    public final GattServiceLinkLoss gattServiceLinkLoss = new GattServiceLinkLoss();
    public final GattServiceImmediateAlert gattServiceimmediateAlert = new GattServiceImmediateAlert();
    public final GattServiceTxPower gattServicetxPower = new GattServiceTxPower();
    private boolean isSupported = false;

    public void setSupportedGattServices(List<BluetoothGattService> list) {
        this.isSupported = true;
        reset();
        for (BluetoothGattService next : list) {
            if (!this.gattServiceGaia.checkService(next) && !this.gattServiceLinkLoss.checkService(next) && !this.gattServiceimmediateAlert.checkService(next) && !this.gattServicetxPower.checkService(next)) {
                if (next.getUuid().equals(GATT.UUIDs.SERVICE_BATTERY_UUID)) {
                    GattServiceBattery gattServiceBattery = new GattServiceBattery();
                    gattServiceBattery.checkService(next);
                    this.gattServiceBatteries.put(Integer.valueOf(next.getInstanceId()), gattServiceBattery);
                } else if (!this.gattServiceHeartRate.checkService(next)) {
                    this.gattServiceDeviceInformation.checkService(next);
                }
            }
        }
    }

    public void reset() {
        this.isSupported = false;
        this.gattServiceLinkLoss.reset();
        this.gattServiceGaia.reset();
        this.gattServiceimmediateAlert.reset();
        this.gattServicetxPower.reset();
        this.gattServiceBatteries.clear();
        this.gattServiceHeartRate.reset();
        this.gattServiceDeviceInformation.reset();
    }

    public boolean isGattProfileProximitySupported() {
        return this.gattServiceLinkLoss.isSupported();
    }

    public boolean isGattProfileHeartRateSupported() {
        return this.gattServiceHeartRate.isSupported() && this.gattServiceDeviceInformation.isSupported();
    }

    public boolean isGattProfileFindMeSupported() {
        return this.gattServiceimmediateAlert.isSupported();
    }

    public boolean isBatteryServiceSupported() {
        return !this.gattServiceBatteries.isEmpty();
    }

    public boolean isSupported() {
        return this.isSupported;
    }

    public String toString() {
        String str = (this.gattServiceGaia.toString() + "\n\n" + this.gattServiceLinkLoss.toString() + "\n\n" + this.gattServiceimmediateAlert.toString() + "\n\n" + this.gattServicetxPower.toString() + "\n\n" + this.gattServiceHeartRate.toString() + "\n\n" + this.gattServiceDeviceInformation.toString()) + "\n\n" + this.gattServiceBatteries.size() + " BATTERY Service(s) available:";
        for (int i = 0; i < this.gattServiceBatteries.size(); i++) {
            int intValue = this.gattServiceBatteries.keyAt(i).intValue();
            str = str + "\ninstance " + intValue + ": " + this.gattServiceBatteries.get(Integer.valueOf(intValue)).toString();
        }
        return str;
    }
}
