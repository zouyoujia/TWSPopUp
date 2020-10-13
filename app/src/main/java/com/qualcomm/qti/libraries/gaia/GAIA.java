package com.qualcomm.qti.libraries.gaia;

import android.annotation.SuppressLint;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class GAIA {
    public static final int ACKNOWLEDGMENT_MASK = 32768;
    public static final int COMMANDS_CONFIGURATION_MASK = 256;
    public static final int COMMANDS_CONTROLS_MASK = 512;
    public static final int COMMANDS_DATA_TRANSFER_MASK = 1536;
    public static final int COMMANDS_DEBUGGING_MASK = 1792;
    public static final int COMMANDS_FEATURE_CONTROL_MASK = 1280;
    public static final int COMMANDS_IVOR_MASK = 4096;
    public static final int COMMANDS_NOTIFICATION_MASK = 16384;
    public static final int COMMANDS_POLLED_STATUS_MASK = 768;
    public static final int COMMAND_ALERT_EVENT = 528;
    public static final int COMMAND_ALERT_LEDS = 525;
    public static final int COMMAND_ALERT_TONE = 526;
    public static final int COMMAND_ALERT_VOICE = 529;
    public static final int COMMAND_AUTHENTICATE_REQUEST = 1281;
    public static final int COMMAND_AUTHENTICATE_RESPONSE = 1282;
    public static final int COMMAND_AV_REMOTE_CONTROL = 543;
    public static final int COMMAND_BATTERY_INFO_CHANGE = 805;
    public static final int COMMAND_CANCEL_NOTIFICATION = 16386;
    public static final int COMMAND_CHANGE_AUDIO_PROMPT_LANGUAGE = 523;
    public static final int COMMAND_CHANGE_VOLUME = 513;
    public static final int COMMAND_CHARGE_STATE_CHANGE = 807;
    public static final int COMMAND_CLOSE_FILE = 1576;
    public static final int COMMAND_CLOSE_STORAGE_PARTITION = 1560;
    public static final int COMMAND_DATA_TRANSFER_CLOSE = 1538;
    public static final int COMMAND_DATA_TRANSFER_SETUP = 1537;
    public static final int COMMAND_DELETE_PDL = 1872;
    public static final int COMMAND_DEVICE_RESET = 514;
    public static final int COMMAND_DEVICE_TO_HOST_DATA = 1540;
    public static final int COMMAND_DFU_BEGIN = 1585;
    public static final int COMMAND_DFU_COMMIT = 1587;
    public static final int COMMAND_DFU_GET_RESULT = 1588;
    public static final int COMMAND_DFU_REQUEST = 1584;
    public static final int COMMAND_DFU_WRITE = 1586;
    public static final int COMMAND_DISCONNECT = 557;
    public static final int COMMAND_DISPLAY_CONTROL = 540;
    public static final int COMMAND_ENTER_BLUETOOTH_PAIRING_MODE = 541;
    public static final int COMMAND_ENTER_FIND_MODE = 825;
    public static final int COMMAND_EVENT_NOTIFICATION = 16387;
    public static final int COMMAND_FACTORY_DEFAULT_RESET = 260;
    public static final int COMMAND_FIND_MY_REMOTE = 555;
    public static final int COMMAND_FLOOD_PS = 1811;
    public static final int COMMAND_FM_CONTROL = 520;
    public static final int COMMAND_GET_3D_ENHANCEMENT_CONTROL = 662;
    public static final int COMMAND_GET_API_VERSION = 768;
    public static final int COMMAND_GET_APPLICATION_VERSION = 772;
    public static final int COMMAND_GET_AUDIO_GAIN_CONFIGURATION = 394;
    public static final int COMMAND_GET_AUDIO_PROMPT_LANGUAGE = 658;
    public static final int COMMAND_GET_AUDIO_SOURCE = 670;
    public static final int COMMAND_GET_AUTH_BITMAPS = 1408;
    public static final int COMMAND_GET_BASS_BOOST_CONTROL = 661;
    public static final int COMMAND_GET_BATTERY_INFO = 804;
    public static final int COMMAND_GET_BOOT_MODE = 642;
    public static final int COMMAND_GET_CODEC = 704;
    public static final int COMMAND_GET_CONFIGURATION_ID = 388;
    public static final int COMMAND_GET_CONFIGURATION_VERSION = 384;
    public static final int COMMAND_GET_CURRENT_BATTERY_LEVEL = 770;
    public static final int COMMAND_GET_CURRENT_RSSI = 769;
    public static final int COMMAND_GET_DATA_ENDPOINT_MODE = 686;
    public static final int COMMAND_GET_DEBUG_FLAGS = 1793;
    public static final int COMMAND_GET_DEBUG_VARIABLE = 1856;
    public static final int COMMAND_GET_DEFAULT_VOLUME = 387;
    public static final int COMMAND_GET_DEVICE_NAME = 399;
    public static final int COMMAND_GET_DFU_PARTITION = 417;
    public static final int COMMAND_GET_DFU_STATUS = 784;
    public static final int COMMAND_GET_EQ_CONTROL = 660;
    public static final int COMMAND_GET_EQ_GROUP_PARAMETER = 667;
    public static final int COMMAND_GET_EQ_PARAMETER = 666;
    public static final int COMMAND_GET_FEATURE = 1411;
    public static final int COMMAND_GET_FEATURE_CONFIGURATION = 391;
    public static final int COMMAND_GET_FILE_STATUS = 1568;
    public static final int COMMAND_GET_HEADSET_STATE = 824;
    public static final int COMMAND_GET_HEADSET_VERSION = 818;
    public static final int COMMAND_GET_HOST_FEATURE_INFORMATION = 800;
    public static final int COMMAND_GET_LED_CONFIGURATION = 385;
    public static final int COMMAND_GET_LED_CONTROL = 647;
    public static final int COMMAND_GET_MEMORY_SLOTS = 1840;
    public static final int COMMAND_GET_MODULE_ID = 771;
    public static final int COMMAND_GET_MOUNTED_PARTITIONS = 416;
    public static final int COMMAND_GET_NOTIFICATION = 16513;
    public static final int COMMAND_GET_ONE_TOUCH_DIAL_STRING = 406;
    public static final int COMMAND_GET_PEER_ADDRESS = 778;
    public static final int COMMAND_GET_PEER_LINK_RESERVED = 679;
    public static final int COMMAND_GET_PEER_PERMITTED_ROUTING = 401;
    public static final int COMMAND_GET_PERMITTED_NEXT_AUDIO_SOURCE = 402;
    public static final int COMMAND_GET_PHONE_VERSION = 819;
    public static final int COMMAND_GET_PIO_CONTROL = 643;
    public static final int COMMAND_GET_PIO_STATE = 774;
    public static final int COMMAND_GET_POWER_CONFIGURATION = 396;
    public static final int COMMAND_GET_POWER_STATE = 644;
    public static final int COMMAND_GET_SESSION_ENABLE = 1412;
    public static final int COMMAND_GET_SPEAKER_EQ_CONTROL = 674;
    public static final int COMMAND_GET_SPEECH_RECOGNITION_CONTROL = 652;
    public static final int COMMAND_GET_STORAGE_PARTITION_STATUS = 1552;
    public static final int COMMAND_GET_TIMER_CONFIGURATION = 393;
    public static final int COMMAND_GET_TONE_CONFIGURATION = 386;
    public static final int COMMAND_GET_TWS_AUDIO_ROUTING = 676;
    public static final int COMMAND_GET_TWS_VOLUME = 677;
    public static final int COMMAND_GET_USER_EQ_CONTROL = 672;
    public static final int COMMAND_GET_USER_EVENT_CONFIGURATION = 392;
    public static final int COMMAND_GET_USER_TONE_CONFIGURATION = 398;
    public static final int COMMAND_GET_VIBRATOR_CONFIGURATION = 389;
    public static final int COMMAND_GET_VIBRATOR_CONTROL = 646;
    public static final int COMMAND_GET_VOICE_PROMPT_CONFIGURATION = 390;
    public static final int COMMAND_GET_VOICE_PROMPT_CONTROL = 650;
    public static final int COMMAND_GET_VOLUME_CONFIGURATION = 395;
    public static final int COMMAND_GET_VOLUME_ORIENTATION = 645;
    public static final int COMMAND_GET_WLAN_CREDENTIALS = 400;
    public static final int COMMAND_HOST_TO_DEVICE_DATA = 1539;
    public static final int COMMAND_I2C_TRANSFER = 1544;
    public static final int COMMAND_IVOR_ANSWER_END = 4103;
    public static final int COMMAND_IVOR_ANSWER_START = 4102;
    public static final int COMMAND_IVOR_CANCEL = 4100;
    public static final int COMMAND_IVOR_CHECK_VERSION = 4101;
    public static final int COMMAND_IVOR_PING = 4336;
    public static final int COMMAND_IVOR_START = 4096;
    public static final int COMMAND_IVOR_VOICE_DATA = 4098;
    public static final int COMMAND_IVOR_VOICE_DATA_REQUEST = 4097;
    public static final int COMMAND_IVOR_VOICE_END = 4099;
    public static final int COMMAND_LOW_BATTERY_STATE_CHANGE = 806;
    public static final int COMMAND_MASK = 32767;
    public static final int COMMAND_MOUNT_STORAGE_PARTITION = 1562;
    public static final int COMMAND_NO_OPERATION = 1792;
    public static final int COMMAND_OPEN_FILE = 1569;
    public static final int COMMAND_OPEN_STORAGE_PARTITION = 1553;
    public static final int COMMAND_OPEN_UART = 1554;
    public static final int COMMAND_PLAY_TONE = 521;
    public static final int COMMAND_READ_ADC = 775;
    public static final int COMMAND_READ_FILE = 1572;
    public static final int COMMAND_REGISTER_NOTIFICATION = 16385;
    public static final int COMMAND_RETRIEVE_FULL_PS_KEY = 1809;
    public static final int COMMAND_RETRIEVE_PS_KEY = 1808;
    public static final int COMMAND_SEND_APPLICATION_MESSAGE = 1825;
    public static final int COMMAND_SEND_DEBUG_MESSAGE = 1824;
    public static final int COMMAND_SEND_KALIMBA_MESSAGE = 1826;
    public static final int COMMAND_SET_3D_ENHANCEMENT_CONTROL = 534;
    public static final int COMMAND_SET_AUDIO_GAIN_CONFIGURATION = 266;
    public static final int COMMAND_SET_AUDIO_PROMPT_LANGUAGE = 530;
    public static final int COMMAND_SET_AUDIO_SOURCE = 542;
    public static final int COMMAND_SET_BASS_BOOST_CONTROL = 533;
    public static final int COMMAND_SET_BLE_CONNECTION_PARAMETERS = 1874;
    public static final int COMMAND_SET_CODEC = 576;
    public static final int COMMAND_SET_DATA_ENDPOINT_MODE = 558;
    public static final int COMMAND_SET_DEBUG_FLAGS = 1794;
    public static final int COMMAND_SET_DEBUG_VARIABLE = 1857;
    public static final int COMMAND_SET_DEFAULT_VOLUME = 259;
    public static final int COMMAND_SET_DEVICE_NAME = 271;
    public static final int COMMAND_SET_DFU_PARTITION = 289;
    public static final int COMMAND_SET_EQ_CONTROL = 532;
    public static final int COMMAND_SET_EQ_GROUP_PARAMETER = 539;
    public static final int COMMAND_SET_EQ_PARAMETER = 538;
    public static final int COMMAND_SET_FEATURE = 1283;
    public static final int COMMAND_SET_FEATURE_CONFIGURATION = 263;
    public static final int COMMAND_SET_LED_CONFIGURATION = 257;
    public static final int COMMAND_SET_LED_CONTROL = 519;
    public static final int COMMAND_SET_ONE_TOUCH_DIAL_STRING = 278;
    public static final int COMMAND_SET_PEER_LINK_RESERVED = 551;
    public static final int COMMAND_SET_PEER_PERMITTED_ROUTING = 273;
    public static final int COMMAND_SET_PERMITTED_NEXT_AUDIO_SOURCE = 274;
    public static final int COMMAND_SET_PIO_CONTROL = 515;
    public static final int COMMAND_SET_POWER_CONFIGURATION = 268;
    public static final int COMMAND_SET_POWER_STATE = 516;
    public static final int COMMAND_SET_RAW_CONFIGURATION = 256;
    public static final int COMMAND_SET_SESSION_ENABLE = 1284;
    public static final int COMMAND_SET_SPEAKER_EQ_CONTROL = 546;
    public static final int COMMAND_SET_SPEECH_RECOGNITION_CONTROL = 524;
    public static final int COMMAND_SET_SUPPORTED_FEATURES = 556;
    public static final int COMMAND_SET_TIMER_CONFIGURATION = 265;
    public static final int COMMAND_SET_TONE_CONFIGURATION = 258;
    public static final int COMMAND_SET_TWS_AUDIO_ROUTING = 548;
    public static final int COMMAND_SET_TWS_VOLUME = 549;
    public static final int COMMAND_SET_USER_EQ_CONTROL = 544;
    public static final int COMMAND_SET_USER_EVENT_CONFIGURATION = 264;
    public static final int COMMAND_SET_USER_TONE_CONFIGURATION = 270;
    public static final int COMMAND_SET_VIBRATOR_CONFIGURATION = 261;
    public static final int COMMAND_SET_VIBRATOR_CONTROL = 518;
    public static final int COMMAND_SET_VOICE_PROMPT_CONFIGURATION = 262;
    public static final int COMMAND_SET_VOICE_PROMPT_CONTROL = 522;
    public static final int COMMAND_SET_VOLUME_CONFIGURATION = 267;
    public static final int COMMAND_SET_VOLUME_ORIENTATION = 517;
    public static final int COMMAND_SET_WLAN_CREDENTIALS = 272;
    public static final int COMMAND_START_SPEECH_RECOGNITION = 531;
    public static final int COMMAND_STORE_FULL_PS_KEY = 1812;
    public static final int COMMAND_STORE_PS_KEY = 1810;
    public static final int COMMAND_SWITCH_EQ_CONTROL = 535;
    public static final int COMMAND_TOGGLE_3D_ENHANCEMENT_CONTROL = 537;
    public static final int COMMAND_TOGGLE_BASS_BOOST_CONTROL = 536;
    public static final int COMMAND_TOGGLE_SPEAKER_EQ_CONTROL = 547;
    public static final int COMMAND_TOGGLE_USER_EQ_CONTROL = 545;
    public static final int COMMAND_TRIM_TWS_VOLUME = 550;
    public static final int COMMAND_TWS_PEER_START_ADVERTISING = 554;
    public static final int COMMAND_VM_UPGRADE_CONNECT = 1600;
    public static final int COMMAND_VM_UPGRADE_CONTROL = 1602;
    public static final int COMMAND_VM_UPGRADE_DATA = 1603;
    public static final int COMMAND_VM_UPGRADE_DISCONNECT = 1601;
    public static final int COMMAND_WRITE_STORAGE_PARTITION = 1557;
    public static final int COMMAND_WRITE_STREAM = 1559;
    public static final int VENDOR_NONE = 32766;
    public static final int VENDOR_QUALCOMM = 38913;

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NotificationEvents {
        public static final int AV_COMMAND = 13;
        public static final int BATTERY_CHARGED = 8;
        public static final int BATTERY_HIGH_THRESHOLD = 4;
        public static final int BATTERY_LOW_THRESHOLD = 3;
        public static final int CAPSENSE_UPDATE = 10;
        public static final int CHARGER_CONNECTION = 9;
        public static final int DEBUG_MESSAGE = 7;
        public static final int DEVICE_STATE_CHANGED = 5;
        public static final int DFU_STATE = 16;
        public static final int HOST_NOTIFICATION = 19;
        public static final int KEY = 15;
        public static final int NOT_NOTIFICATION = 0;
        public static final int PIO_CHANGED = 6;
        public static final int REMOTE_BATTERY_LEVEL = 14;
        public static final int RSSI_HIGH_THRESHOLD = 2;
        public static final int RSSI_LOW_THRESHOLD = 1;
        public static final int SPEECH_RECOGNITION = 12;
        public static final int UART_RECEIVED_DATA = 17;
        public static final int USER_ACTION = 11;
        public static final int VMU_PACKET = 18;
    }

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
        public static final int AUTHENTICATING = 4;
        public static final int INCORRECT_STATE = 6;
        public static final int INSUFFICIENT_RESOURCES = 3;
        public static final int INVALID_PARAMETER = 5;
        public static final int IN_PROGRESS = 7;
        public static final int NOT_AUTHENTICATED = 2;
        public static final int NOT_STATUS = -1;
        public static final int NOT_SUPPORTED = 1;
        public static final int SUCCESS = 0;
    }

    @SuppressLint({"ShiftFlags"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Transport {
        public static final int BLE = 0;
        public static final int BR_EDR = 1;
    }

    public static int getNotificationEvent(byte b) {
        switch (b) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 7:
                return 7;
            case 8:
                return 8;
            case 9:
                return 9;
            case 10:
                return 10;
            case 11:
                return 11;
            case 12:
                return 12;
            case 13:
                return 13;
            case 14:
                return 14;
            case 15:
                return 15;
            case 16:
                return 16;
            case 17:
                return 17;
            case 18:
                return 18;
            case 19:
                return 19;
            default:
                return 0;
        }
    }

    public static int getStatus(byte b) {
        switch (b) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 7:
                return 7;
            default:
                return -1;
        }
    }

    public static String getStatusToString(int i) {
        switch (i) {
            case -1:
                return "NOT STATUS";
            case 0:
                return "SUCCESS";
            case 1:
                return "NOT SUPPORTED";
            case 2:
                return "NOT AUTHENTICATED";
            case 3:
                return "INSUFFICIENT RESOURCES";
            case 4:
                return "AUTHENTICATING";
            case 5:
                return "INVALID PARAMETER";
            case 6:
                return "INCORRECT STATE";
            case 7:
                return "IN PROGRESS";
            default:
                return "UNKNOWN STATUS";
        }
    }
}
