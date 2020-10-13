package com.qualcomm.qti.libraries.gaia;

public final class GaiaUtils {
    private static final int BITS_IN_BYTE = 8;
    private static final int BYTES_IN_INT = 4;
    public static final boolean LOG_ENABLED = isAssertPanicLog();

    public static String getHexadecimalStringFromInt(int i) {
        return String.format("%04X", new Object[]{Integer.valueOf(i & 65535)});
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

    public static String getGAIACommandToString(int i) {
        String str = "UNKNOWN";
        switch (i) {
            case 256:
                str = "COMMAND_SET_RAW_CONFIGURATION" + "(deprecated)";
                break;
            case 257:
                str = "COMMAND_SET_LED_CONFIGURATION";
                break;
            case 258:
                str = "COMMAND_SET_TONE_CONFIGURATION";
                break;
            case 259:
                str = "COMMAND_SET_DEFAULT_VOLUME";
                break;
            case 260:
                str = "COMMAND_FACTORY_DEFAULT_RESET";
                break;
            case 261:
                str = "COMMAND_SET_VIBRATOR_CONFIGURATION";
                break;
            case 262:
                str = "COMMAND_SET_VOICE_PROMPT_CONFIGURATION";
                break;
            case 263:
                str = "COMMAND_SET_FEATURE_CONFIGURATION";
                break;
            case 264:
                str = "COMMAND_SET_USER_EVENT_CONFIGURATION";
                break;
            case 265:
                str = "COMMAND_SET_TIMER_CONFIGURATION";
                break;
            case GAIA.COMMAND_SET_AUDIO_GAIN_CONFIGURATION:
                str = "COMMAND_SET_AUDIO_GAIN_CONFIGURATION";
                break;
            case GAIA.COMMAND_SET_VOLUME_CONFIGURATION:
                str = "COMMAND_SET_VOLUME_CONFIGURATION";
                break;
            case GAIA.COMMAND_SET_POWER_CONFIGURATION:
                str = "COMMAND_SET_POWER_CONFIGURATION";
                break;
            default:
                switch (i) {
                    case GAIA.COMMAND_SET_USER_TONE_CONFIGURATION:
                        str = "COMMAND_SET_USER_TONE_CONFIGURATION";
                        break;
                    case 271:
                        str = "COMMAND_SET_DEVICE_NAME";
                        break;
                    case GAIA.COMMAND_SET_WLAN_CREDENTIALS:
                        str = "COMMAND_SET_WLAN_CREDENTIALS";
                        break;
                    case GAIA.COMMAND_SET_PEER_PERMITTED_ROUTING:
                        str = "COMMAND_SET_PEER_PERMITTED_ROUTING";
                        break;
                    case GAIA.COMMAND_SET_PERMITTED_NEXT_AUDIO_SOURCE:
                        str = "COMMAND_SET_PERMITTED_NEXT_AUDIO_SOURCE";
                        break;
                    default:
                        switch (i) {
                            case GAIA.COMMAND_GET_CONFIGURATION_VERSION:
                                str = "COMMAND_GET_CONFIGURATION_VERSION";
                                break;
                            case GAIA.COMMAND_GET_LED_CONFIGURATION:
                                str = "COMMAND_GET_LED_CONFIGURATION";
                                break;
                            case GAIA.COMMAND_GET_TONE_CONFIGURATION:
                                str = "COMMAND_GET_TONE_CONFIGURATION";
                                break;
                            case GAIA.COMMAND_GET_DEFAULT_VOLUME:
                                str = "COMMAND_GET_DEFAULT_VOLUME";
                                break;
                            case GAIA.COMMAND_GET_CONFIGURATION_ID:
                                str = "COMMAND_GET_CONFIGURATION_ID" + "(deprecated)";
                                break;
                            case GAIA.COMMAND_GET_VIBRATOR_CONFIGURATION:
                                str = "COMMAND_GET_VIBRATOR_CONFIGURATION";
                                break;
                            case GAIA.COMMAND_GET_VOICE_PROMPT_CONFIGURATION:
                                str = "COMMAND_GET_VOICE_PROMPT_CONFIGURATION";
                                break;
                            case GAIA.COMMAND_GET_FEATURE_CONFIGURATION:
                                str = "COMMAND_GET_FEATURE_CONFIGURATION";
                                break;
                            case GAIA.COMMAND_GET_USER_EVENT_CONFIGURATION:
                                str = "COMMAND_GET_USER_EVENT_CONFIGURATION";
                                break;
                            case GAIA.COMMAND_GET_TIMER_CONFIGURATION:
                                str = "COMMAND_GET_TIMER_CONFIGURATION";
                                break;
                            case GAIA.COMMAND_GET_AUDIO_GAIN_CONFIGURATION:
                                str = "COMMAND_GET_AUDIO_GAIN_CONFIGURATION";
                                break;
                            case GAIA.COMMAND_GET_VOLUME_CONFIGURATION:
                                str = "COMMAND_GET_VOLUME_CONFIGURATION";
                                break;
                            case GAIA.COMMAND_GET_POWER_CONFIGURATION:
                                str = "COMMAND_GET_POWER_CONFIGURATION";
                                break;
                            default:
                                switch (i) {
                                    case GAIA.COMMAND_GET_USER_TONE_CONFIGURATION:
                                        str = "COMMAND_GET_USER_TONE_CONFIGURATION";
                                        break;
                                    case GAIA.COMMAND_GET_DEVICE_NAME:
                                        str = "COMMAND_GET_DEVICE_NAME";
                                        break;
                                    case GAIA.COMMAND_GET_WLAN_CREDENTIALS:
                                        str = "COMMAND_GET_WLAN_CREDENTIALS";
                                        break;
                                    case GAIA.COMMAND_GET_PEER_PERMITTED_ROUTING:
                                        str = "COMMAND_GET_PEER_PERMITTED_ROUTING";
                                        break;
                                    case GAIA.COMMAND_GET_PERMITTED_NEXT_AUDIO_SOURCE:
                                        str = "COMMAND_GET_PERMITTED_NEXT_AUDIO_SOURCE";
                                        break;
                                    default:
                                        switch (i) {
                                            case GAIA.COMMAND_GET_MOUNTED_PARTITIONS:
                                                str = "COMMAND_GET_MOUNTED_PARTITIONS";
                                                break;
                                            case GAIA.COMMAND_GET_DFU_PARTITION:
                                                str = "COMMAND_GET_DFU_PARTITION";
                                                break;
                                            default:
                                                switch (i) {
                                                    case 513:
                                                        str = "COMMAND_CHANGE_VOLUME";
                                                        break;
                                                    case 514:
                                                        str = "COMMAND_DEVICE_RESET";
                                                        break;
                                                    case 515:
                                                        str = "COMMAND_SET_PIO_CONTROL";
                                                        break;
                                                    case 516:
                                                        str = "COMMAND_SET_POWER_STATE";
                                                        break;
                                                    case GAIA.COMMAND_SET_VOLUME_ORIENTATION:
                                                        str = "COMMAND_SET_VOLUME_ORIENTATION";
                                                        break;
                                                    case GAIA.COMMAND_SET_VIBRATOR_CONTROL:
                                                        str = "COMMAND_SET_VIBRATOR_CONTROL";
                                                        break;
                                                    case GAIA.COMMAND_SET_LED_CONTROL:
                                                        str = "COMMAND_SET_LED_CONTROL";
                                                        break;
                                                    case GAIA.COMMAND_FM_CONTROL:
                                                        str = "COMMAND_FM_CONTROL";
                                                        break;
                                                    case GAIA.COMMAND_PLAY_TONE:
                                                        str = "COMMAND_PLAY_TONE";
                                                        break;
                                                    case GAIA.COMMAND_SET_VOICE_PROMPT_CONTROL:
                                                        str = "COMMAND_SET_VOICE_PROMPT_CONTROL";
                                                        break;
                                                    case GAIA.COMMAND_CHANGE_AUDIO_PROMPT_LANGUAGE:
                                                        str = "COMMAND_CHANGE_AUDIO_PROMPT_LANGUAGE";
                                                        break;
                                                    case GAIA.COMMAND_SET_SPEECH_RECOGNITION_CONTROL:
                                                        str = "COMMAND_SET_SPEECH_RECOGNITION_CONTROL";
                                                        break;
                                                    case GAIA.COMMAND_ALERT_LEDS:
                                                        str = "COMMAND_ALERT_LEDS";
                                                        break;
                                                    case GAIA.COMMAND_ALERT_TONE:
                                                        str = "COMMAND_ALERT_TONE";
                                                        break;
                                                    default:
                                                        switch (i) {
                                                            case GAIA.COMMAND_ALERT_EVENT:
                                                                str = "COMMAND_ALERT_EVENT";
                                                                break;
                                                            case GAIA.COMMAND_ALERT_VOICE:
                                                                str = "COMMAND_ALERT_VOICE";
                                                                break;
                                                            case GAIA.COMMAND_SET_AUDIO_PROMPT_LANGUAGE:
                                                                str = "COMMAND_SET_AUDIO_PROMPT_LANGUAGE";
                                                                break;
                                                            case GAIA.COMMAND_START_SPEECH_RECOGNITION:
                                                                str = "COMMAND_START_SPEECH_RECOGNITION";
                                                                break;
                                                            case GAIA.COMMAND_SET_EQ_CONTROL:
                                                                str = "COMMAND_SET_EQ_CONTROL";
                                                                break;
                                                            case GAIA.COMMAND_SET_BASS_BOOST_CONTROL:
                                                                str = "COMMAND_SET_BASS_BOOST_CONTROL";
                                                                break;
                                                            case GAIA.COMMAND_SET_3D_ENHANCEMENT_CONTROL:
                                                                str = "COMMAND_SET_3D_ENHANCEMENT_CONTROL";
                                                                break;
                                                            case GAIA.COMMAND_SWITCH_EQ_CONTROL:
                                                                str = "COMMAND_SWITCH_EQ_CONTROL";
                                                                break;
                                                            case GAIA.COMMAND_TOGGLE_BASS_BOOST_CONTROL:
                                                                str = "COMMAND_TOGGLE_BASS_BOOST_CONTROL";
                                                                break;
                                                            case GAIA.COMMAND_TOGGLE_3D_ENHANCEMENT_CONTROL:
                                                                str = "COMMAND_TOGGLE_3D_ENHANCEMENT_CONTROL";
                                                                break;
                                                            case GAIA.COMMAND_SET_EQ_PARAMETER:
                                                                str = "COMMAND_SET_EQ_PARAMETER";
                                                                break;
                                                            case GAIA.COMMAND_SET_EQ_GROUP_PARAMETER:
                                                                str = "COMMAND_SET_EQ_GROUP_PARAMETER";
                                                                break;
                                                            case GAIA.COMMAND_DISPLAY_CONTROL:
                                                                str = "COMMAND_DISPLAY_CONTROL";
                                                                break;
                                                            case GAIA.COMMAND_ENTER_BLUETOOTH_PAIRING_MODE:
                                                                str = "COMMAND_ENTER_BLUETOOTH_PAIRING_MODE";
                                                                break;
                                                            case GAIA.COMMAND_SET_AUDIO_SOURCE:
                                                                str = "COMMAND_SET_AUDIO_SOURCE";
                                                                break;
                                                            case GAIA.COMMAND_AV_REMOTE_CONTROL:
                                                                str = "COMMAND_AV_REMOTE_CONTROL";
                                                                break;
                                                            case GAIA.COMMAND_SET_USER_EQ_CONTROL:
                                                                str = "COMMAND_SET_USER_EQ_CONTROL";
                                                                break;
                                                            case GAIA.COMMAND_TOGGLE_USER_EQ_CONTROL:
                                                                str = "COMMAND_TOGGLE_USER_EQ_CONTROL";
                                                                break;
                                                            case GAIA.COMMAND_SET_SPEAKER_EQ_CONTROL:
                                                                str = "COMMAND_SET_SPEAKER_EQ_CONTROL";
                                                                break;
                                                            case GAIA.COMMAND_TOGGLE_SPEAKER_EQ_CONTROL:
                                                                str = "COMMAND_TOGGLE_SPEAKER_EQ_CONTROL";
                                                                break;
                                                            case GAIA.COMMAND_SET_TWS_AUDIO_ROUTING:
                                                                str = "COMMAND_SET_TWS_AUDIO_ROUTING";
                                                                break;
                                                            case GAIA.COMMAND_SET_TWS_VOLUME:
                                                                str = "COMMAND_SET_TWS_VOLUME";
                                                                break;
                                                            case GAIA.COMMAND_TRIM_TWS_VOLUME:
                                                                str = "COMMAND_TRIM_TWS_VOLUME";
                                                                break;
                                                            case GAIA.COMMAND_SET_PEER_LINK_RESERVED:
                                                                str = "COMMAND_SET_PEER_LINK_RESERVED";
                                                                break;
                                                            default:
                                                                switch (i) {
                                                                    case GAIA.COMMAND_TWS_PEER_START_ADVERTISING:
                                                                        str = "COMMAND_TWS_PEER_START_ADVERTISING";
                                                                        break;
                                                                    case GAIA.COMMAND_FIND_MY_REMOTE:
                                                                        str = "COMMAND_FIND_MY_REMOTE";
                                                                        break;
                                                                    case GAIA.COMMAND_SET_SUPPORTED_FEATURES:
                                                                        str = "COMMAND_SET_SUPPORTED_FEATURES";
                                                                        break;
                                                                    case GAIA.COMMAND_DISCONNECT:
                                                                        str = "COMMAND_DISCONNECT";
                                                                        break;
                                                                    default:
                                                                        switch (i) {
                                                                            case GAIA.COMMAND_GET_BOOT_MODE:
                                                                                str = "COMMAND_GET_BOOT_MODE";
                                                                                break;
                                                                            case GAIA.COMMAND_GET_PIO_CONTROL:
                                                                                str = "COMMAND_GET_PIO_CONTROL";
                                                                                break;
                                                                            case GAIA.COMMAND_GET_POWER_STATE:
                                                                                str = "COMMAND_GET_POWER_STATE";
                                                                                break;
                                                                            case GAIA.COMMAND_GET_VOLUME_ORIENTATION:
                                                                                str = "COMMAND_GET_VOLUME_ORIENTATION";
                                                                                break;
                                                                            case GAIA.COMMAND_GET_VIBRATOR_CONTROL:
                                                                                str = "COMMAND_GET_VIBRATOR_CONTROL";
                                                                                break;
                                                                            case GAIA.COMMAND_GET_LED_CONTROL:
                                                                                str = "COMMAND_GET_LED_CONTROL";
                                                                                break;
                                                                            default:
                                                                                switch (i) {
                                                                                    case GAIA.COMMAND_GET_EQ_CONTROL:
                                                                                        str = "COMMAND_GET_EQ_CONTROL";
                                                                                        break;
                                                                                    case GAIA.COMMAND_GET_BASS_BOOST_CONTROL:
                                                                                        str = "COMMAND_GET_BASS_BOOST_CONTROL";
                                                                                        break;
                                                                                    case GAIA.COMMAND_GET_3D_ENHANCEMENT_CONTROL:
                                                                                        str = "COMMAND_GET_3D_ENHANCEMENT_CONTROL";
                                                                                        break;
                                                                                    default:
                                                                                        switch (i) {
                                                                                            case GAIA.COMMAND_GET_EQ_PARAMETER:
                                                                                                str = "COMMAND_GET_EQ_PARAMETER";
                                                                                                break;
                                                                                            case GAIA.COMMAND_GET_EQ_GROUP_PARAMETER:
                                                                                                str = "COMMAND_GET_EQ_GROUP_PARAMETER";
                                                                                                break;
                                                                                            default:
                                                                                                switch (i) {
                                                                                                    case GAIA.COMMAND_GET_TWS_AUDIO_ROUTING:
                                                                                                        str = "COMMAND_GET_TWS_AUDIO_ROUTING";
                                                                                                        break;
                                                                                                    case GAIA.COMMAND_GET_TWS_VOLUME:
                                                                                                        str = "COMMAND_GET_TWS_VOLUME";
                                                                                                        break;
                                                                                                    default:
                                                                                                        switch (i) {
                                                                                                            case 768:
                                                                                                                str = "COMMAND_GET_API_VERSION";
                                                                                                                break;
                                                                                                            case 769:
                                                                                                                str = "COMMAND_GET_CURRENT_RSSI";
                                                                                                                break;
                                                                                                            case 770:
                                                                                                                str = "COMMAND_GET_CURRENT_BATTERY_LEVEL";
                                                                                                                break;
                                                                                                            case 771:
                                                                                                                str = "COMMAND_GET_MODULE_ID";
                                                                                                                break;
                                                                                                            case 772:
                                                                                                                str = "COMMAND_GET_APPLICATION_VERSION";
                                                                                                                break;
                                                                                                            default:
                                                                                                                switch (i) {
                                                                                                                    case 774:
                                                                                                                        str = "COMMAND_GET_PIO_STATE";
                                                                                                                        break;
                                                                                                                    case 775:
                                                                                                                        str = "COMMAND_READ_ADC";
                                                                                                                        break;
                                                                                                                    default:
                                                                                                                        switch (i) {
                                                                                                                            case GAIA.COMMAND_AUTHENTICATE_REQUEST:
                                                                                                                                str = "COMMAND_AUTHENTICATE_REQUEST";
                                                                                                                                break;
                                                                                                                            case GAIA.COMMAND_AUTHENTICATE_RESPONSE:
                                                                                                                                str = "COMMAND_AUTHENTICATE_RESPONSE";
                                                                                                                                break;
                                                                                                                            case GAIA.COMMAND_SET_FEATURE:
                                                                                                                                str = "COMMAND_SET_FEATURE";
                                                                                                                                break;
                                                                                                                            case GAIA.COMMAND_SET_SESSION_ENABLE:
                                                                                                                                str = "COMMAND_SET_SESSION_ENABLE";
                                                                                                                                break;
                                                                                                                            default:
                                                                                                                                switch (i) {
                                                                                                                                    case GAIA.COMMAND_GET_FEATURE:
                                                                                                                                        str = "COMMAND_GET_FEATURE";
                                                                                                                                        break;
                                                                                                                                    case GAIA.COMMAND_GET_SESSION_ENABLE:
                                                                                                                                        str = "COMMAND_GET_SESSION_ENABLE";
                                                                                                                                        break;
                                                                                                                                    default:
                                                                                                                                        switch (i) {
                                                                                                                                            case GAIA.COMMAND_DATA_TRANSFER_SETUP:
                                                                                                                                                str = "COMMAND_DATA_TRANSFER_SETUP";
                                                                                                                                                break;
                                                                                                                                            case GAIA.COMMAND_DATA_TRANSFER_CLOSE:
                                                                                                                                                str = "COMMAND_DATA_TRANSFER_CLOSE";
                                                                                                                                                break;
                                                                                                                                            case GAIA.COMMAND_HOST_TO_DEVICE_DATA:
                                                                                                                                                str = "COMMAND_HOST_TO_DEVICE_DATA";
                                                                                                                                                break;
                                                                                                                                            case GAIA.COMMAND_DEVICE_TO_HOST_DATA:
                                                                                                                                                str = "COMMAND_DEVICE_TO_HOST_DATA";
                                                                                                                                                break;
                                                                                                                                            default:
                                                                                                                                                switch (i) {
                                                                                                                                                    case GAIA.COMMAND_GET_STORAGE_PARTITION_STATUS:
                                                                                                                                                        str = "COMMAND_GET_STORAGE_PARTITION_STATUS";
                                                                                                                                                        break;
                                                                                                                                                    case GAIA.COMMAND_OPEN_STORAGE_PARTITION:
                                                                                                                                                        str = "COMMAND_OPEN_STORAGE_PARTITION";
                                                                                                                                                        break;
                                                                                                                                                    case GAIA.COMMAND_OPEN_UART:
                                                                                                                                                        str = "COMMAND_OPEN_UART";
                                                                                                                                                        break;
                                                                                                                                                    default:
                                                                                                                                                        switch (i) {
                                                                                                                                                            case GAIA.COMMAND_WRITE_STREAM:
                                                                                                                                                                str = "COMMAND_WRITE_STREAM";
                                                                                                                                                                break;
                                                                                                                                                            case GAIA.COMMAND_CLOSE_STORAGE_PARTITION:
                                                                                                                                                                str = "COMMAND_CLOSE_STORAGE_PARTITION";
                                                                                                                                                                break;
                                                                                                                                                            default:
                                                                                                                                                                switch (i) {
                                                                                                                                                                    case GAIA.COMMAND_GET_FILE_STATUS:
                                                                                                                                                                        str = "COMMAND_GET_FILE_STATUS";
                                                                                                                                                                        break;
                                                                                                                                                                    case GAIA.COMMAND_OPEN_FILE:
                                                                                                                                                                        str = "COMMAND_OPEN_FILE";
                                                                                                                                                                        break;
                                                                                                                                                                    default:
                                                                                                                                                                        switch (i) {
                                                                                                                                                                            case GAIA.COMMAND_DFU_REQUEST:
                                                                                                                                                                                str = "COMMAND_DFU_REQUEST";
                                                                                                                                                                                break;
                                                                                                                                                                            case GAIA.COMMAND_DFU_BEGIN:
                                                                                                                                                                                str = "COMMAND_DFU_BEGIN";
                                                                                                                                                                                break;
                                                                                                                                                                            case GAIA.COMMAND_DFU_WRITE:
                                                                                                                                                                                str = "COMMAND_DFU_WRITE";
                                                                                                                                                                                break;
                                                                                                                                                                            case GAIA.COMMAND_DFU_COMMIT:
                                                                                                                                                                                str = "COMMAND_DFU_COMMIT";
                                                                                                                                                                                break;
                                                                                                                                                                            case GAIA.COMMAND_DFU_GET_RESULT:
                                                                                                                                                                                str = "COMMAND_DFU_GET_RESULT";
                                                                                                                                                                                break;
                                                                                                                                                                            default:
                                                                                                                                                                                switch (i) {
                                                                                                                                                                                    case GAIA.COMMAND_VM_UPGRADE_CONNECT:
                                                                                                                                                                                        str = "COMMAND_VM_UPGRADE_CONNECT";
                                                                                                                                                                                        break;
                                                                                                                                                                                    case GAIA.COMMAND_VM_UPGRADE_DISCONNECT:
                                                                                                                                                                                        str = "COMMAND_VM_UPGRADE_DISCONNECT";
                                                                                                                                                                                        break;
                                                                                                                                                                                    case GAIA.COMMAND_VM_UPGRADE_CONTROL:
                                                                                                                                                                                        str = "COMMAND_VM_UPGRADE_CONTROL";
                                                                                                                                                                                        break;
                                                                                                                                                                                    case GAIA.COMMAND_VM_UPGRADE_DATA:
                                                                                                                                                                                        str = "COMMAND_VM_UPGRADE_DATA";
                                                                                                                                                                                        break;
                                                                                                                                                                                    default:
                                                                                                                                                                                        switch (i) {
                                                                                                                                                                                            case 1792:
                                                                                                                                                                                                str = "COMMAND_NO_OPERATION";
                                                                                                                                                                                                break;
                                                                                                                                                                                            case GAIA.COMMAND_GET_DEBUG_FLAGS:
                                                                                                                                                                                                str = "COMMAND_GET_DEBUG_FLAGS";
                                                                                                                                                                                                break;
                                                                                                                                                                                            case GAIA.COMMAND_SET_DEBUG_FLAGS:
                                                                                                                                                                                                str = "COMMAND_SET_DEBUG_FLAGS";
                                                                                                                                                                                                break;
                                                                                                                                                                                            default:
                                                                                                                                                                                                switch (i) {
                                                                                                                                                                                                    case GAIA.COMMAND_RETRIEVE_PS_KEY:
                                                                                                                                                                                                        str = "COMMAND_RETRIEVE_PS_KEY";
                                                                                                                                                                                                        break;
                                                                                                                                                                                                    case GAIA.COMMAND_RETRIEVE_FULL_PS_KEY:
                                                                                                                                                                                                        str = "COMMAND_RETRIEVE_FULL_PS_KEY";
                                                                                                                                                                                                        break;
                                                                                                                                                                                                    case GAIA.COMMAND_STORE_PS_KEY:
                                                                                                                                                                                                        str = "COMMAND_STORE_PS_KEY";
                                                                                                                                                                                                        break;
                                                                                                                                                                                                    case GAIA.COMMAND_FLOOD_PS:
                                                                                                                                                                                                        str = "COMMAND_FLOOD_PS";
                                                                                                                                                                                                        break;
                                                                                                                                                                                                    case GAIA.COMMAND_STORE_FULL_PS_KEY:
                                                                                                                                                                                                        str = "COMMAND_STORE_FULL_PS_KEY";
                                                                                                                                                                                                        break;
                                                                                                                                                                                                    default:
                                                                                                                                                                                                        switch (i) {
                                                                                                                                                                                                            case GAIA.COMMAND_SEND_DEBUG_MESSAGE:
                                                                                                                                                                                                                str = "COMMAND_SEND_DEBUG_MESSAGE";
                                                                                                                                                                                                                break;
                                                                                                                                                                                                            case GAIA.COMMAND_SEND_APPLICATION_MESSAGE:
                                                                                                                                                                                                                str = "COMMAND_SEND_APPLICATION_MESSAGE";
                                                                                                                                                                                                                break;
                                                                                                                                                                                                            case GAIA.COMMAND_SEND_KALIMBA_MESSAGE:
                                                                                                                                                                                                                str = "COMMAND_SEND_KALIMBA_MESSAGE";
                                                                                                                                                                                                                break;
                                                                                                                                                                                                            default:
                                                                                                                                                                                                                switch (i) {
                                                                                                                                                                                                                    case GAIA.COMMAND_GET_DEBUG_VARIABLE:
                                                                                                                                                                                                                        str = "COMMAND_GET_DEBUG_VARIABLE";
                                                                                                                                                                                                                        break;
                                                                                                                                                                                                                    case GAIA.COMMAND_SET_DEBUG_VARIABLE:
                                                                                                                                                                                                                        str = "COMMAND_SET_DEBUG_VARIABLE";
                                                                                                                                                                                                                        break;
                                                                                                                                                                                                                    default:
                                                                                                                                                                                                                        switch (i) {
                                                                                                                                                                                                                            case 4096:
                                                                                                                                                                                                                                str = "COMMAND_IVOR_START";
                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                            case 4097:
                                                                                                                                                                                                                                str = "COMMAND_IVOR_VOICE_DATA_REQUEST";
                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                            case 4098:
                                                                                                                                                                                                                                str = "COMMAND_IVOR_VOICE_DATA";
                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                            case 4099:
                                                                                                                                                                                                                                str = "COMMAND_IVOR_VOICE_END";
                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                            case GAIA.COMMAND_IVOR_CANCEL:
                                                                                                                                                                                                                                str = "COMMAND_IVOR_CANCEL";
                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                            case GAIA.COMMAND_IVOR_CHECK_VERSION:
                                                                                                                                                                                                                                str = "COMMAND_IVOR_CHECK_VERSION";
                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                            case GAIA.COMMAND_IVOR_ANSWER_START:
                                                                                                                                                                                                                                str = "COMMAND_IVOR_ANSWER_START";
                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                            case GAIA.COMMAND_IVOR_ANSWER_END:
                                                                                                                                                                                                                                str = "COMMAND_IVOR_ANSWER_END";
                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                            default:
                                                                                                                                                                                                                                switch (i) {
                                                                                                                                                                                                                                    case GAIA.COMMAND_REGISTER_NOTIFICATION:
                                                                                                                                                                                                                                        str = "COMMAND_REGISTER_NOTIFICATION";
                                                                                                                                                                                                                                        break;
                                                                                                                                                                                                                                    case 16386:
                                                                                                                                                                                                                                        str = "COMMAND_CANCEL_NOTIFICATION";
                                                                                                                                                                                                                                        break;
                                                                                                                                                                                                                                    case GAIA.COMMAND_EVENT_NOTIFICATION:
                                                                                                                                                                                                                                        str = "COMMAND_EVENT_NOTIFICATION";
                                                                                                                                                                                                                                        break;
                                                                                                                                                                                                                                    default:
                                                                                                                                                                                                                                        switch (i) {
                                                                                                                                                                                                                                            case GAIA.COMMAND_SET_ONE_TOUCH_DIAL_STRING:
                                                                                                                                                                                                                                                str = "COMMAND_SET_ONE_TOUCH_DIAL_STRING";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_SET_DFU_PARTITION:
                                                                                                                                                                                                                                                str = "COMMAND_SET_DFU_PARTITION";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_GET_ONE_TOUCH_DIAL_STRING:
                                                                                                                                                                                                                                                str = "COMMAND_GET_ONE_TOUCH_DIAL_STRING";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_SET_CODEC:
                                                                                                                                                                                                                                                str = "COMMAND_SET_CODEC";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_GET_VOICE_PROMPT_CONTROL:
                                                                                                                                                                                                                                                str = "COMMAND_GET_VOICE_PROMPT_CONTROL";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_GET_SPEECH_RECOGNITION_CONTROL:
                                                                                                                                                                                                                                                str = "COMMAND_GET_SPEECH_RECOGNITION_CONTROL";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_GET_AUDIO_PROMPT_LANGUAGE:
                                                                                                                                                                                                                                                str = "COMMAND_GET_AUDIO_PROMPT_LANGUAGE";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_GET_AUDIO_SOURCE:
                                                                                                                                                                                                                                                str = "COMMAND_GET_AUDIO_SOURCE";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_GET_USER_EQ_CONTROL:
                                                                                                                                                                                                                                                str = "COMMAND_GET_USER_EQ_CONTROL";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_GET_SPEAKER_EQ_CONTROL:
                                                                                                                                                                                                                                                str = "COMMAND_GET_SPEAKER_EQ_CONTROL";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_GET_PEER_LINK_RESERVED:
                                                                                                                                                                                                                                                str = "COMMAND_GET_PEER_LINK_RESERVED";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_GET_CODEC:
                                                                                                                                                                                                                                                str = "COMMAND_GET_CODEC";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_GET_PEER_ADDRESS:
                                                                                                                                                                                                                                                str = "COMMAND_GET_PEER_ADDRESS";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_GET_DFU_STATUS:
                                                                                                                                                                                                                                                str = "COMMAND_GET_DFU_STATUS" + "(deprecated)";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_GET_HOST_FEATURE_INFORMATION:
                                                                                                                                                                                                                                                str = "COMMAND_GET_HOST_FEATURE_INFORMATION";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case 1408:
                                                                                                                                                                                                                                                str = "COMMAND_GET_AUTH_BITMAPS";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_I2C_TRANSFER:
                                                                                                                                                                                                                                                str = "COMMAND_I2C_TRANSFER";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_WRITE_STORAGE_PARTITION:
                                                                                                                                                                                                                                                str = "COMMAND_WRITE_STORAGE_PARTITION";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_MOUNT_STORAGE_PARTITION:
                                                                                                                                                                                                                                                str = "COMMAND_MOUNT_STORAGE_PARTITION";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_READ_FILE:
                                                                                                                                                                                                                                                str = "COMMAND_READ_FILE";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_CLOSE_FILE:
                                                                                                                                                                                                                                                str = "COMMAND_CLOSE_FILE";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_GET_MEMORY_SLOTS:
                                                                                                                                                                                                                                                str = "COMMAND_GET_MEMORY_SLOTS";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_DELETE_PDL:
                                                                                                                                                                                                                                                str = "COMMAND_DELETE_PDL";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_SET_BLE_CONNECTION_PARAMETERS:
                                                                                                                                                                                                                                                str = "COMMAND_SET_BLE_CONNECTION_PARAMETERS";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_IVOR_PING:
                                                                                                                                                                                                                                                str = "COMMAND_IVOR_PING";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                            case GAIA.COMMAND_GET_NOTIFICATION:
                                                                                                                                                                                                                                                str = "COMMAND_GET_NOTIFICATION";
                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                }
                                                                                                                                                                                                                        }
                                                                                                                                                                                                                }
                                                                                                                                                                                                        }
                                                                                                                                                                                                }
                                                                                                                                                                                        }
                                                                                                                                                                                }
                                                                                                                                                                        }
                                                                                                                                                                }
                                                                                                                                                        }
                                                                                                                                                }
                                                                                                                                        }
                                                                                                                                }
                                                                                                                        }
                                                                                                                }
                                                                                                        }
                                                                                                }
                                                                                        }
                                                                                }
                                                                        }
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
        return getHexadecimalStringFromInt(i) + " " + str;
    }

    private static boolean isAssertPanicLog() {
        try {
            Object invoke = Class.forName("android.os.SystemProperties").getDeclaredMethod("getBoolean", new Class[]{String.class, Boolean.TYPE}).invoke((Object) null, new Object[]{"persist.sys.assert.panic", false});
            if (invoke != null) {
                return ((Boolean) invoke).booleanValue();
            }
        } catch (Exception unused) {
        }
        return false;
    }
}
