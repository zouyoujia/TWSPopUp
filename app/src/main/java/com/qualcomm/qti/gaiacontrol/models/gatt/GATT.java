package com.qualcomm.qti.gaiacontrol.models.gatt;

import com.qualcomm.qti.libraries.ble.Characteristics;
import com.qualcomm.qti.libraries.ble.Services;
import java.util.UUID;

public final class GATT {
    public static final int UINT16_LENGTH_IN_BYTES = 2;
    public static final int UINT8_LENGTH_IN_BYTES = 1;

    public static class AlertLevel {
        public static final int DATA_LENGTH_IN_BYTES = 1;
        public static final int LEVEL_BYTE_OFFSET = 0;
        public static final int LEVEL_FORMAT = 17;

        public static class Levels {
            public static final int HIGH = 2;
            public static final int MILD = 1;
            public static final int NONE = 0;
            public static final int NUMBER_OF_LEVELS = 3;
        }
    }

    public static class BodySensorLocation {
        public static final int LOCATION_BYTE_OFFSET = 0;
        public static final int LOCATION_LENGTH_IN_BYTES = 1;

        public static class Locations {
            public static final int CHEST = 1;
            public static final int EAR_LOBE = 5;
            public static final int FINGER = 3;
            public static final int FOOT = 6;
            public static final int HAND = 4;
            public static final int OTHER = 0;
            public static final int WRIST = 2;
        }
    }

    public static class HeartRateControlPoint {
        public static final int CONTROL_BYTE_OFFSET = 0;
        public static final int CONTROL_LENGTH_IN_BYTES = 1;

        public static class Controls {
            public static final byte RESET_ENERGY_EXPENDED = 1;
        }
    }

    public static class HeartRateMeasurement {
        public static final int ENERGY_LENGTH_IN_BYTES = 2;
        public static final int FLAGS_BYTE_OFFSET = 0;
        public static final int FLAGS_LENGTH_IN_BYTES = 1;

        public static class Flags {
            public static final int ENERGY_EXPENDED_PRESENCE_BIT_OFFSET = 3;
            public static final int ENERGY_EXPENDED_PRESENCE_LENGTH_IN_BITS = 1;
            public static final int FORMAT_BIT_OFFSET = 0;
            public static final int FORMAT_LENGTH_IN_BITS = 1;
            public static final int RR_INTERVAL_BIT_OFFSET = 4;
            public static final int RR_INTERVAL_LENGTH_IN_BITS = 1;
            public static final int SENSOR_CONTACT_STATUS_BIT_OFFSET = 1;
            public static final int SENSOR_CONTACT_STATUS_LENGTH_IN_BITS = 2;

            public static class Format {
                public static final int UINT16 = 1;
                public static final int UINT8 = 0;
            }

            public static class Presence {
                public static final int NOT_PRESENT = 0;
                public static final int PRESENT = 1;
            }

            public static class SensorStatus {
                public static final int NOT_SUPPORTED = 0;
                public static final int NOT_SUPPORTED_2 = 1;
                public static final int SUPPORTED_WITH_CONTACT_DETECTED = 3;
                public static final int SUPPORTED_WITH_NO_CONTACT_DETECTED = 2;
            }

            public static int getFlag(byte b, int i, int i2) {
                return (b & (((1 << i2) - 1) << i)) >>> i;
            }
        }
    }

    public static class PresentationFormat {
        public static final int DATA_LENGTH_IN_BYTES = 7;
        public static final int DESCRIPTION_BYTE_OFFSET = 5;
        public static final int DESCRIPTION_LENGTH_IN_BYTES = 2;
        public static final int NAMESPACE_BYTE_OFFSET = 4;
        public static final int NAMESPACE_LENGTH_IN_BYTES = 1;

        public static class Description {
            public static final int INTERNAL = 271;
            public static final int SECOND = 2;
            public static final int THIRD = 3;
            public static final int UNKNOWN = 0;
        }

        public static class Namespace {
            public static final int BLUETOOTH_SIG_ASSIGNED_NUMBERS = 1;
        }
    }

    public static class TxPowerLevel {
        public static final int LEVEL_BYTE_OFFSET = 0;
        public static final int LEVEL_FORMAT = 33;
    }

    public static class UUIDs {
        public static final UUID CHARACTERISTIC_ALERT_LEVEL_UUID = Characteristics.getCharacteristicUUID(Characteristics.CHARACTERISTIC_ALERT_LEVEL);
        public static final UUID CHARACTERISTIC_BATTERY_LEVEL_UUID = Characteristics.getCharacteristicUUID(Characteristics.CHARACTERISTIC_BATTERY_LEVEL);
        public static final UUID CHARACTERISTIC_BODY_SENSOR_LOCATION_UUID = Characteristics.getCharacteristicUUID(Characteristics.CHARACTERISTIC_BODY_SENSOR_LOCATION);
        public static final UUID CHARACTERISTIC_GAIA_COMMAND_UUID = Characteristics.getCharacteristicUUID(Characteristics.CHARACTERISTIC_CSR_GAIA_COMMAND_ENDPOINT);
        public static final UUID CHARACTERISTIC_GAIA_DATA_ENDPOINT_UUID = Characteristics.getCharacteristicUUID(Characteristics.CHARACTERISTIC_CSR_GAIA_DATA_ENDPOINT);
        public static final UUID CHARACTERISTIC_GAIA_RESPONSE_UUID = Characteristics.getCharacteristicUUID(Characteristics.CHARACTERISTIC_CSR_GAIA_RESPONSE_ENDPOINT);
        public static final UUID CHARACTERISTIC_HEART_RATE_CONTROL_POINT_UUID = Characteristics.getCharacteristicUUID(Characteristics.CHARACTERISTIC_HEART_RATE_CONTROL_POINT);
        public static final UUID CHARACTERISTIC_HEART_RATE_MEASUREMENT_UUID = Characteristics.getCharacteristicUUID(Characteristics.CHARACTERISTIC_HEART_RATE_MEASUREMENT);
        public static final UUID CHARACTERISTIC_TX_POWER_LEVEL_UUID = Characteristics.getCharacteristicUUID(Characteristics.CHARACTERISTIC_TX_POWER_LEVEL);
        public static final UUID DESCRIPTOR_CHARACTERISTIC_PRESENTATION_FORMAT = Characteristics.CHARACTERISTIC_PRESENTATION_FORMAT;
        public static final UUID DESCRIPTOR_CLIENT_CHARACTERISTIC_CONFIGURATION_UUID = Characteristics.CLIENT_CHARACTERISTIC_CONFIG;
        public static final UUID SERVICE_BATTERY_UUID = Services.getStringServiceUUID(Services.SERVICE_BATTERY);
        public static final UUID SERVICE_DEVICE_INFORMATION_UUID = Services.getStringServiceUUID(Services.SERVICE_DEVICE_INFORMATION);
        public static final UUID SERVICE_GAIA_UUID = Services.getStringServiceUUID(Services.SERVICE_CSR_GAIA);
        public static final UUID SERVICE_HEART_RATE_UUID = Services.getStringServiceUUID(Services.SERVICE_HEART_RATE);
        public static final UUID SERVICE_IMMEDIATE_ALERT_UUID = Services.getStringServiceUUID(Services.SERVICE_IMMEDIATE_ALERT);
        public static final UUID SERVICE_LINK_LOSS_UUID = Services.getStringServiceUUID(Services.SERVICE_LINK_LOSS);
        public static final UUID SERVICE_TX_POWER_UUID = Services.getStringServiceUUID(Services.SERVICE_TX_POWER);
    }
}
