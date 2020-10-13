package com.qualcomm.qti.libraries.ble;

public final class ErrorStatus {
    public static String getBluetoothGattStatusLabel(int i, boolean z) {
        String str;
        String str2 = "";
        String str3 = "";
        if (i == 0) {
            str2 = "GATT_SUCCESS";
            str3 = "A GATT operation completed successfully.";
        } else if (i == 13) {
            str2 = "GATT_INVALID_ATTRIBUTE_LENGTH";
            str3 = "A write operation exceeds the maximum length of the attribute.";
        } else if (i == 15) {
            str2 = "GATT_INSUFFICIENT_ENCRYPTION";
            str3 = "Insufficient encryption for a given operation.";
        } else if (i == 143) {
            str2 = "GATT_CONNECTION_CONGESTED";
            str3 = "A remote device connection is congested.";
        } else if (i != 257) {
            switch (i) {
                case 2:
                    str2 = "GATT_READ_NOT_PERMITTED";
                    str3 = "GATT read operation is not permitted.";
                    break;
                case 3:
                    str2 = "GATT_WRITE_NOT_PERMITTED";
                    str3 = "GATT write operation is not permitted.";
                    break;
                default:
                    switch (i) {
                        case 5:
                            str2 = "GATT_INSUFFICIENT_AUTHENTICATION";
                            str3 = "Insufficient authentication for a given operation.";
                            break;
                        case 6:
                            str2 = "GATT_REQUEST_NOT_SUPPORTED";
                            str3 = "The given request is not supported.";
                            break;
                        case 7:
                            str2 = "GATT_INVALID_OFFSET";
                            str3 = "A read or write operation was requested with an invalid offset.";
                            break;
                    }
            }
        } else {
            str2 = "GATT_FAILURE";
            str3 = "A GATT operation failed, different error of the BluetoothGatt ones.";
        }
        if (str2.length() <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str2);
        if (!z || str3.length() <= 0) {
            str = "";
        } else {
            str = ": " + str3;
        }
        sb.append(str);
        return sb.toString();
    }

    public static class GattApi {
        public static final int GATT_AUTH_FAIL = 137;
        public static final int GATT_BUSY = 132;
        public static final int GATT_CCC_CFG_ERR = 253;
        public static final int GATT_CMD_STARTED = 134;
        public static final int GATT_CONN_CANCEL = 256;
        public static final int GATT_DB_FULL = 131;
        public static final int GATT_ENCRYPED_NO_MITM = 141;
        public static final int GATT_ERROR = 133;
        public static final int GATT_ILLEGAL_PARAMETER = 135;
        public static final int GATT_INTERNAL_ERROR = 129;
        public static final int GATT_INVALID_CFG = 139;
        public static final int GATT_MORE = 138;
        public static final int GATT_NOT_ENCRYPTED = 142;
        public static final int GATT_NO_RESOURCES = 128;
        public static final int GATT_OUT_OF_RANGE = 255;
        public static final int GATT_PENDING = 136;
        public static final int GATT_PRC_IN_PROGRESS = 254;
        public static final int GATT_SERVICE_STARTED = 140;
        public static final int GATT_WRONG_STATE = 130;

        public static String getLabel(int i, boolean z) {
            String str;
            String str2 = "";
            String str3 = "";
            switch (i) {
                case 128:
                    str2 = "GATT_NO_RESOURCES";
                    break;
                case GATT_INTERNAL_ERROR /*129*/:
                    str2 = "GATT_INTERNAL_ERROR";
                    break;
                case GATT_WRONG_STATE /*130*/:
                    str2 = "GATT_WRONG_STATE";
                    break;
                case GATT_DB_FULL /*131*/:
                    str2 = "GATT_DB_FULL";
                    break;
                case GATT_BUSY /*132*/:
                    str2 = "GATT_BUSY";
                    break;
                case GATT_ERROR /*133*/:
                    str2 = "GATT_ERROR";
                    break;
                case GATT_CMD_STARTED /*134*/:
                    str2 = "GATT_CMD_STARTED";
                    break;
                case GATT_ILLEGAL_PARAMETER /*135*/:
                    str2 = "GATT_ILLEGAL_PARAMETER";
                    break;
                case GATT_PENDING /*136*/:
                    str2 = "GATT_PENDING";
                    break;
                case GATT_AUTH_FAIL /*137*/:
                    str2 = "GATT_AUTH_FAIL";
                    break;
                case GATT_MORE /*138*/:
                    str2 = "GATT_MORE";
                    break;
                case GATT_INVALID_CFG /*139*/:
                    str2 = "GATT_INVALID_CFG";
                    break;
                case GATT_SERVICE_STARTED /*140*/:
                    str2 = "GATT_SERVICE_STARTED";
                    break;
                case GATT_ENCRYPED_NO_MITM /*141*/:
                    str2 = "GATT_ENCRYPED_NO_MITM";
                    break;
                case GATT_NOT_ENCRYPTED /*142*/:
                    str2 = "GATT_NOT_ENCRYPTED";
                    break;
                default:
                    switch (i) {
                        case GATT_CCC_CFG_ERR /*253*/:
                            str2 = "GATT_CCC_CFG_ERR";
                            str3 = "Client Characteristic Configuration Descriptor improperly configured.";
                            break;
                        case 254:
                            str2 = "GATT_PRC_IN_PROGRESS or L2CAP_CONN_AMP_FAILED from l2cdefs.h";
                            str3 = "Procedure already in progress for GATT_PRC_IN_PROGRESS.";
                            break;
                        case 255:
                            str2 = "GATT_OUT_OF_RANGE or L2CAP_CONN_NO_LINK from l2cdefs.h";
                            str3 = "Attribute value out of range for GATT_OUT_OF_RANGE.";
                            break;
                        case 256:
                            str2 = "L2CAP_CONN_CANCEL";
                            str3 = "L2CAP connection cancelled";
                            break;
                    }
            }
            if (str2.length() <= 0) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            sb.append(str2);
            if (!z || str3.length() <= 0) {
                str = "";
            } else {
                str = ": " + str3;
            }
            sb.append(str);
            return sb.toString();
        }
    }

    public static class ATT {
        public static final int ATTRIBUTE_NOT_FOUND = 10;
        public static final int ATTRIBUTE_NOT_LONG = 11;
        public static final int INSUFFICIENT_AUTHENTICATION = 5;
        public static final int INSUFFICIENT_AUTHORIZATION = 8;
        public static final int INSUFFICIENT_ENCRYPTION = 15;
        public static final int INSUFFICIENT_ENCRYPTION_KEY_SIZE = 12;
        public static final int INSUFFICIENT_RESOURCES = 17;
        public static final int INVALID_ATTRIBUTE_VALUE_LENGTH = 13;
        public static final int INVALID_HANDLE = 1;
        public static final int INVALID_OFFSET = 7;
        public static final int INVALID_PDU = 4;
        public static final int PREPARE_QUEUE_FULL = 9;
        public static final int READ_NOT_PERMITTED = 2;
        public static final int REQUEST_NOT_SUPPORTED = 6;
        public static final int UNLIKELY_ERROR = 14;
        public static final int UNSUPPORTED_GROUP_TYPE = 16;
        public static final int WRITE_NOT_PERMITTED = 3;

        public static String getLabel(int i, boolean z) {
            String str;
            String str2 = "";
            String str3 = "";
            switch (i) {
                case 1:
                    str2 = "INVALID_HANDLE";
                    str3 = "The attribute handle given was not valid on this server.";
                    break;
                case 2:
                    str2 = "READ_NOT_PERMITTED";
                    str3 = "The attribute cannot be read.";
                    break;
                case 3:
                    str2 = "WRITE_NOT_PERMITTED";
                    str3 = "The attribute cannot be written.";
                    break;
                case 4:
                    str2 = "INVALID_PDU";
                    str3 = "The attribute PDU was invalid.";
                    break;
                case 5:
                    str2 = "INSUFFICIENT_AUTHENTICATION";
                    str3 = "The attribute requires authentication before it can be read or written.";
                    break;
                case 6:
                    str2 = "REQUEST_NOT_SUPPORTED";
                    str3 = "Attribute server does not support the request received from the client.";
                    break;
                case 7:
                    str2 = "INVALID_OFFSET";
                    str3 = "Offset specified was past the end of the attribute.";
                    break;
                case 8:
                    str2 = "INSUFFICIENT_AUTHORIZATION";
                    str3 = "The attribute requires authorization before it can be read or written.";
                    break;
                case 9:
                    str2 = "PREPARE_QUEUE_FULL";
                    str3 = "Too many prepare writes have been queued.";
                    break;
                case 10:
                    str2 = "ATTRIBUTE_NOT_FOUND";
                    str3 = "No attribute found within the given attribute handle range.";
                    break;
                case 11:
                    str2 = "ATTRIBUTE_NOT_LONG";
                    str3 = "The attribute cannot be read using the Read Blob Request.";
                    break;
                case 12:
                    str2 = "INSUFFICIENT_ENCRYPTION_KEY_SIZE";
                    str3 = "The Encryption Key Size used for encrypting this link is insufficient.";
                    break;
                case 13:
                    str2 = "INVALID_ATTRIBUTE_VALUE_LENGTH";
                    str3 = "The attribute value length is invalid for the operation.";
                    break;
                case 14:
                    str2 = "UNLIKELY_ERROR";
                    str3 = "The attribute request that was requested has encountered an error that was unlikely, and therefore could not be completed as requested.";
                    break;
                case 15:
                    str2 = "INSUFFICIENT_ENCRYPTION";
                    str3 = "The attribute requires encryption before it can be read or written.";
                    break;
                case 16:
                    str2 = "UNSUPPORTED_GROUP_TYPE";
                    str3 = "The attribute type is not a supported grouping attribute as defined by a higher layer specification.";
                    break;
                case 17:
                    str2 = "INSUFFICIENT_RESOURCES";
                    str3 = "Insufficient Resources to complete the request.";
                    break;
            }
            if (str2.length() <= 0) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            sb.append(str2);
            if (!z || str3.length() <= 0) {
                str = "";
            } else {
                str = ": " + str3;
            }
            sb.append(str);
            return sb.toString();
        }
    }

    public static class HCI {
        public static final int AUTHENTICATION_FAILURE = 5;
        public static final int CHANNEL_CLASSIFICATION_NOT_SUPPORTED = 46;
        public static final int COARSE_CLOCK_ADJUSTMENT_REJECTED = 64;
        public static final int COMMAND_DISALLOWED = 12;
        public static final int CONNECTION_ACCEPT_TIMEOUT_EXCEEDED = 16;
        public static final int CONNECTION_ALREADY_EXISTS = 11;
        public static final int CONNECTION_ESTABLISHMENT_FAILED = 62;
        public static final int CONNECTION_LIMIT_EXCEEDED = 9;
        public static final int CONNECTION_REJECTED_LIMITED_RESOURCES = 13;
        public static final int CONNECTION_REJECTED_NO_SUITABLE_CHANNEL_FOUND = 57;
        public static final int CONNECTION_REJECTED_SECURITY_REASONS = 14;
        public static final int CONNECTION_REJECTED_UNACCEPTABLE_BD_ADDR = 15;
        public static final int CONNECTION_TERMINATED_BY_LOCAL_HOST = 22;
        public static final int CONNECTION_TERMINATED_MIC_FAILURE = 61;
        public static final int CONNECTION_TIMEOUT = 8;
        public static final int CONTROLLER_BUSY = 58;
        public static final int DIFFERENT_TRANSACTION_COLLISION = 42;
        public static final int DIRECTED_ADVERTISING_TIMEOUT = 60;
        public static final int ENCRYPTION_MODE_NOT_ACCEPTABLE = 37;
        public static final int EXTENDED_INQUIRY_RESPONSE_TOO_LARGE = 54;
        public static final int HARDWARE_FAILURE = 3;
        public static final int HOST_BUSY_PAIRING = 56;
        public static final int INSTANT_PASSED = 40;
        public static final int INSUFFICIENT_SECURITY = 47;
        public static final int INVALID_HCI_COMMAND_PARAMETERS = 18;
        public static final int INVALID_LMP_OR_LL_PARAMETERS = 30;
        public static final int LIMIT_REACHED = 67;
        public static final int LINK_KEY_CANNOT_BE_CHANGED = 38;
        public static final int LMP_ERROR_TRANSACTION_COLLISION_OR_LL_PROCEDURE_COLLISION = 35;
        public static final int LMP_OR_LL_RESPONSE_TIMEOUT = 34;
        public static final int LMP_PDU_NOT_ALLOWED = 36;
        public static final int MAC_CONNECTION_FAILED = 63;
        public static final int MEMORY_CAPACITY_EXCEEDED = 7;
        public static final int OPERATION_CANCELLED_BY_HOST = 68;
        public static final int PAGE_TIMEOUT = 4;
        public static final int PAIRING_NOT_ALLOWED = 24;
        public static final int PAIRING_WITH_UNIT_KEY_NOT_SUPPORTED = 41;
        public static final int PARAMETER_OUT_OF_MANDATORY_RANGE = 48;
        public static final int PIN_OR_KEY_MISSING = 6;
        public static final int QOS_REJECTED = 45;
        public static final int REMOTE_DEVICE_TERMINATED_CONNECTION_LOW_RESOURCES = 20;
        public static final int REMOTE_DEVICE_TERMINATED_CONNECTION_POWER_OFF = 21;
        public static final int REMOTE_USER_TERMINATED_CONNECTION = 19;
        public static final int REPEATED_ATTEMPTS = 23;
        public static final int REQUESTED_QOS_NOT_SUPPORTED = 39;
        public static final int RESERVED_SLOT_VIOLATION = 52;
        public static final int ROLE_CHANGE_NOT_ALLOWED = 33;
        public static final int ROLE_SWITCH_FAILED = 53;
        public static final int ROLE_SWITCH_PENDING = 50;
        public static final int SCO_AIR_MODE_REJECTED = 29;
        public static final int SCO_INTERVAL_REJECTED = 28;
        public static final int SCO_OFFSET_REJECTED = 27;
        public static final int SECURE_SIMPLE_PAIRING_NOT_SUPPORTED_BY_HOST = 55;
        public static final int SUCCESS = 0;
        public static final int SYNCHRONOUS_CONNECTION_LIMIT_TO_A_DEVICE_EXCEEDED = 10;
        public static final int TYPE_0_SUBMAP_NOT_DEFINED = 65;
        public static final int UNACCEPTABLE_CONNECTION_PARAMETERS = 59;
        public static final int UNACCEPTABLE_PARAMETER = 44;
        public static final int UNKNOWN_ADVERTISING_IDENTIFIER = 66;
        public static final int UNKNOWN_CONNECTION_IDENTIFIER = 2;
        public static final int UNKNOWN_HCI_COMMAND = 1;
        public static final int UNKNOWN_LMP_PDU = 25;
        public static final int UNSPECIFIED_ERROR = 31;
        public static final int UNSUPPORTED_FEATURE_OR_PARAMETER_VALUE = 17;
        public static final int UNSUPPORTED_LMP_OR_LL_PARAMETER_VALUE = 32;
        public static final int UNSUPPORTED_REMOTE_FEATURE_OR_LMP_FEATURE = 26;

        public static String getLabel(int i, boolean z) {
            String str;
            String str2 = "";
            String str3 = "";
            switch (i) {
                case 0:
                    str2 = "SUCCESS";
                    break;
                case 1:
                    str2 = "UNKNOWN_HCI_COMMAND";
                    str3 = "The Unknown HCI Command error code indicates that the Controller does not understand the HCI Command Packet OpCode that the Host sent. The OpCode given might not correspond to any of the OpCodes specified in this document, or any vendor-specific OpCodes, or the command may have not been implemented.";
                    break;
                case 2:
                    str2 = "UNKNOWN_CONNECTION_IDENTIFIER";
                    str3 = "The Unknown Connection Identifier error code indicates that a command was sent from the Host that should identify a connection, but that connection does not exist.";
                    break;
                case 3:
                    str2 = "HARDWARE_FAILURE";
                    str3 = "The Hardware Failure error code indicates to the Host that something in the Controller has failed in a manner that cannot be described with any other error code. The meaning implied with this error code is implementation dependent.";
                    break;
                case 4:
                    str2 = "PAGE_TIMEOUT";
                    str3 = "The Page Timeout error code indicates that a page timed out because of the Page Timeout configuration parameter. This error code may occur only with the Remote_Name_Request and Create_Connection commands.";
                    break;
                case 5:
                    str2 = "AUTHENTICATION_FAILURE";
                    str3 = "The Authentication Failure error code indicates that pairing or authentication failed due to incorrect results in the pairing or authentication procedure. This could be due to an incorrect PIN or Link Key.";
                    break;
                case 6:
                    str2 = "PIN_OR_KEY_MISSING";
                    str3 = "The PIN or Key Missing error code is used when pairing failed because of a missing PIN, or authentication failed because of a missing Key.";
                    break;
                case 7:
                    str2 = "MEMORY_CAPACITY_EXCEEDED";
                    str3 = "The Memory Capacity Exceeded error code indicates to the Host that the Controller has run out of memory to store new parameters.";
                    break;
                case 8:
                    str2 = "CONNECTION_TIMEOUT";
                    str3 = "The Connection Timeout error code indicates that the link supervision timeout has expired for a given connection.";
                    break;
                case 9:
                    str2 = "CONNECTION_LIMIT_EXCEEDED";
                    str3 = "The Connection Limit Exceeded error code indicates that an attempt to create another connection failed because the Controller is already at its limit of the number of connections it can support. The number of connections a device can support is implementation dependent.";
                    break;
                case 10:
                    str2 = "SYNCHRONOUS_CONNECTION_LIMIT_TO_A_DEVICE_EXCEEDED";
                    str3 = "The Synchronous Connection Limit to a Device Exceeded error code indicates that the Controller has reached the limit to the number of synchronous connections that can be achieved to a device. The number of synchronous connections a device can support is implementation dependent.";
                    break;
                case 11:
                    str2 = "CONNECTION_ALREADY_EXISTS";
                    str3 = "The Connection Already Exists error code indicates that an attempt was made to create a new Connection to a device when there is already a connection to this device and multiple connections to the same device are not permitted.";
                    break;
                case 12:
                    str2 = "COMMAND_DISALLOWED";
                    str3 = "The Command Disallowed error code indicates that the command requested cannot be executed because the Controller is in a state where it cannot process this command at this time. This error shall not be used for command OpCodes where the error code Unknown HCI Command is valid.";
                    break;
                case 13:
                    str2 = "CONNECTION_REJECTED_LIMITED_RESOURCES";
                    str3 = "The Connection Rejected Due To Limited Resources error code indicates that a connection was rejected due to limited resources.";
                    break;
                case 14:
                    str2 = "CONNECTION_REJECTED_SECURITY_REASONS";
                    str3 = "The Connection Rejected Due To Security Reasons error code indicates that a connection was rejected due to security requirements not being fulfilled, like authentication or pairing.";
                    break;
                case 15:
                    str2 = "CONNECTION_REJECTED_UNACCEPTABLE_BD_ADDR";
                    str3 = "The Connection Rejected due to Unacceptable BD_ADDR error code indicates that a connection was rejected because this device does not accept the BD_ADDR. This may be because the device will only accept connections from specific BD_ADDRs.";
                    break;
                case 16:
                    str2 = "CONNECTION_ACCEPT_TIMEOUT_EXCEEDED";
                    str3 = "The Connection Accept Timeout Exceeded error code indicates that the Connection Accept Timeout has been exceeded for this connection attempt.";
                    break;
                case 17:
                    str2 = "UNSUPPORTED_FEATURE_OR_PARAMETER_VALUE";
                    str3 = "The Unsupported Feature Or Parameter Value error code indicates that a feature or parameter value in the HCI command is not supported. This error code shall not be used in an LMP PDU.";
                    break;
                case 18:
                    str2 = "INVALID_HCI_COMMAND_PARAMETERS";
                    str3 = "The Invalid HCI Command Parameters error code indicates that at least one of the HCI command parameters is invalid. This shall be used when:\n\t\t\t\t- the parameter total length is invalid.\n\t\t\t\t- a command parameter is an invalid type.\n\t\t\t\t- a connection identifier does not match the corresponding event.\n\t\t\t\t- a parameter is odd when it is required to be even.\n\t\t\t\t- a parameter is outside of the specified range.\n\t\t\t\t- two or more parameter values have inconsistent values.\nNote: An invalid type can be, for example, when a SCO connection handle is used where an ACL connection handle is required. </p>";
                    break;
                case 19:
                    str2 = "REMOTE_USER_TERMINATED_CONNECTION";
                    str3 = "The Remote User Terminated Connection error code indicates that the user on the remote device terminated the connection.";
                    break;
                case 20:
                    str2 = "REMOTE_USER_TERMINATED_CONNECTION";
                    str3 = "The Remote Device Terminated Connection due to Low Resources error code indicates that the remote device terminated the connection because of low resources.";
                    break;
                case 21:
                    str2 = "REMOTE_DEVICE_TERMINATED_CONNECTION_POWER_OFF";
                    str3 = "The Remote Device Terminated Connection due to Power Off error code indicates that the remote device terminated the connection because the device is about to power off.";
                    break;
                case 22:
                    str2 = "CONNECTION_TERMINATED_BY_LOCAL_HOST";
                    str3 = "The Connection Terminated By Local Host error code indicates that the local device terminated the connection.";
                    break;
                case 23:
                    str2 = "REPEATED_ATTEMPTS";
                    str3 = "The Repeated Attempts error code indicates that the Controller is disallowing an authentication or pairing procedure because too little time has elapsed since the last authentication or pairing attempt failed.";
                    break;
                case 24:
                    str2 = "PAIRING_NOT_ALLOWED";
                    str3 = "The Pairing Not Allowed error code indicates that the device does not allow pairing. For example, when a device only allows pairing during a certain time window after some user input allows pairing.";
                    break;
                case 25:
                    str2 = "UNKNOWN_LMP_PDU";
                    str3 = "The Unknown LMP PDU error code indicates that the Controller has received an unknown LMP OpCode.";
                    break;
                case 26:
                    str2 = "UNSUPPORTED_REMOTE_FEATURE_OR_LMP_FEATURE";
                    str3 = "The Unsupported Remote Feature error code indicates that the remote device does not support the feature associated with the issued command or LMP PDU.";
                    break;
                case 27:
                    str2 = "SCO_OFFSET_REJECTED";
                    str3 = "The SCO Offset Rejected error code indicates that the offset requested in the LMP_SCO_link_req PDU has been rejected.";
                    break;
                case 28:
                    str2 = "SCO_INTERVAL_REJECTED";
                    str3 = "The SCO Interval Rejected error code indicates that the interval requested in the LMP_SCO_link_req PDU has been rejected.";
                    break;
                case 29:
                    str2 = "SCO_AIR_MODE_REJECTED";
                    str3 = "The SCO Air Mode Rejected error code indicates that the air mode requested in the LMP_SCO_link_req PDU has been rejected.";
                    break;
                case 30:
                    str2 = "INVALID_LMP_OR_LL_PARAMETERS";
                    str3 = "The Invalid LMP Parameters / Invalid LL Parameters error code indicates that some LMP PDU / LL Control PDU parameters were invalid. This shall be used when:\n\t\t\t\t- the PDU length is invalid.\n\t\t\t\t- a parameter is odd when it is required to be even.\n\t\t\t\t- a parameter is outside of the specified range.\n\t\t\t\t- two or more parameters have inconsistent values.";
                    break;
                case 31:
                    str2 = "UNSPECIFIED_ERROR";
                    str3 = "The Unspecified Error error code indicates that no other error code specified is appropriate to use.";
                    break;
                case 32:
                    str2 = "UNSUPPORTED_LMP_OR_LL_PARAMETER_VALUE";
                    str3 = "The Unsupported LMP Parameter Value / Unsupported LL Parameter Value error code indicates that an LMP PDU or an LL Control PDU contains at least one parameter value that is not supported by the Controller at this time. This is normally used after a long negotiation procedure, for example during an LMP_hold_req, LMP_sniff_req and LMP_encryption_key_size_req PDU exchanges. This may be used by the Link Layer, for example during the Connection Parameters Request Link Layer Control procedure.";
                    break;
                case 33:
                    str2 = "ROLE_CHANGE_NOT_ALLOWED";
                    str3 = "The Role Change Not Allowed error code indicates that a Controller will not allow a role change at this time.";
                    break;
                case 34:
                    str2 = "LMP_OR_LL_RESPONSE_TIMEOUT";
                    str3 = "The LMP Response Timeout / LL Response Timeout error code indicates that an LMP transaction failed to respond within the LMP response timeout or an LL transaction failed to respond within the LL response timeout.";
                    break;
                case 35:
                    str2 = "LMP_ERROR_TRANSACTION_COLLISION_OR_LL_PROCEDURE_COLLISION";
                    str3 = "The LMP Error Transaction Collision / LL Procedure Collision error code indicates that an LMP transaction or LL procedure has collided with the same transaction or procedure that is already in progress.";
                    break;
                case 36:
                    str2 = "LMP_PDU_NOT_ALLOWED";
                    str3 = "The LMP PDU Not Allowed error code indicates that a Controller sent an LMP PDU with an OpCode that was not allowed.";
                    break;
                case 37:
                    str2 = "ENCRYPTION_MODE_NOT_ACCEPTABLE";
                    str3 = "The Encryption Mode Not Acceptable error code indicates that the requested encryption mode is not acceptable at this time.";
                    break;
                case 38:
                    str2 = "LINK_KEY_CANNOT_BE_CHANGED";
                    str3 = "The Link Key cannot be Changed error code indicates that a link key cannot be changed because a fixed unit key is being used.";
                    break;
                case 39:
                    str2 = "REQUESTED_QOS_NOT_SUPPORTED";
                    str3 = "The Requested QoS Not Supported error code indicates that the requested Quality of Service is not supported.";
                    break;
                case 40:
                    str2 = "INSTANT_PASSED";
                    str3 = "The Instant Passed error code indicates that an LMP PDU or LL PDU that includes an instant cannot be performed because the instant when this would have occurred has passed.";
                    break;
                case 41:
                    str2 = "PAIRING_WITH_UNIT_KEY_NOT_SUPPORTED";
                    str3 = "The Pairing With Unit Key Not Supported error code indicates that it was not possible to pair as a unit key was requested and it is not supported.";
                    break;
                case 42:
                    str2 = "DIFFERENT_TRANSACTION_COLLISION";
                    str3 = "The Different Transaction Collision error code indicates that an LMP transaction or LL Procedure was started that collides with an ongoing transaction.";
                    break;
                case 44:
                    str2 = "UNACCEPTABLE_PARAMETER";
                    str3 = "The QoS Unacceptable Parameter error code indicates that the specified quality of service parameters could not be accepted at this time, but other parameters may be acceptable.";
                    break;
                case 45:
                    str2 = "QOS_REJECTED";
                    str3 = "The QoS Rejected error code indicates that the specified quality of service parameters cannot be accepted and QoS negotiation should be terminated";
                    break;
                case 46:
                    str2 = "CHANNEL_CLASSIFICATION_NOT_SUPPORTED";
                    str3 = "The Channel Assessment Not Supported error code indicates that the Controller cannot perform channel assessment because it is not supported.";
                    break;
                case 47:
                    str2 = "INSUFFICIENT_SECURITY";
                    str3 = "The Insufficient Security error code indicates that the HCI command or LMP PDU sent is only possible on an encrypted link.";
                    break;
                case 48:
                    str2 = "PARAMETER_OUT_OF_MANDATORY_RANGE";
                    str3 = "The Parameter Out Of Mandatory Range error code indicates that a parameter value requested is outside the mandatory range of parameters for the given HCI command or LMP PDU and the recipient does not accept that value.";
                    break;
                case 50:
                    str2 = "ROLE_SWITCH_PENDING";
                    str3 = "The Role Switch Pending error code indicates that a Role Switch is pending. This can be used when an HCI command or LMP PDU cannot be accepted because of a pending role switch. This can also be used to notify a peer device about a pending role switch.";
                    break;
                case 52:
                    str2 = "RESERVED_SLOT_VIOLATION";
                    str3 = "The Reserved Slot Violation error code indicates that the current Synchronous negotiation was terminated with the negotiation state set to Reserved Slot Violation.";
                    break;
                case 53:
                    str2 = "ROLE_SWITCH_FAILED";
                    str3 = "The Role Switch Failed error code indicates that a role switch was attempted but it failed and the original piconet structure is restored. The switch may have failed because the TDD switch or piconet switch failed.";
                    break;
                case 54:
                    str2 = "EXTENDED_INQUIRY_RESPONSE_TOO_LARGE";
                    str3 = "The Extended Inquiry Response Too Large error code indicates that the extended inquiry response, with the requested requirements for FEC, is too large to fit in any of the packet types supported by the Controller.";
                    break;
                case 55:
                    str2 = "SECURE_SIMPLE_PAIRING_NOT_SUPPORTED_BY_HOST";
                    str3 = "The Secure Simple Pairing Not Supported by Host error code indicates that the IO capabilities request or response was rejected because the sending Host does not support Secure Simple Pairing even though the receiving Link Manager does.";
                    break;
                case 56:
                    str2 = "HOST_BUSY_PAIRING";
                    str3 = "The Host Busy - Pairing error code indicates that the Host is busy with another pairing operation and unable to support the requested pairing. The receiving device should retry pairing again later.";
                    break;
                case 57:
                    str2 = "CONNECTION_REJECTED_NO_SUITABLE_CHANNEL_FOUND";
                    str3 = "The Connection Rejected due to No Suitable Channel Found error code indicates that the Controller could not calculate an appropriate value for the Channel selection operation.";
                    break;
                case 58:
                    str2 = "CONTROLLER_BUSY";
                    str3 = "The Controller Busy error code indicates that the operation was rejected because the Controller was busy and unable to process the request.";
                    break;
                case 59:
                    str2 = "UNACCEPTABLE_CONNECTION_PARAMETERS";
                    str3 = "The Unacceptable Connection Parameters error code indicates that the remote device either terminated the connection or rejected a request because of one or more unacceptable connection parameters.";
                    break;
                case 60:
                    str2 = "DIRECTED_ADVERTISING_TIMEOUT";
                    str3 = "The Advertising Timeout error code indicates that advertising for a fixed duration completed or, for directed advertising, that advertising completed without a connection being created. (Formerly called Directed Advertising Timeout)";
                    break;
                case 61:
                    str2 = "CONNECTION_TERMINATED_MIC_FAILURE";
                    str3 = "The Connection Terminated Due to MIC Failure error code indicates that the connection was terminated because the Message Integrity Check (MIC) failed on a received packet.";
                    break;
                case 62:
                    str2 = "CONNECTION_ESTABLISHMENT_FAILED";
                    str3 = "The Connection Failed to be Established error code indicates that the LL initiated a connection but the connection has failed to be established.";
                    break;
                case 63:
                    str2 = "MAC_CONNECTION_FAILED";
                    str3 = "The MAC of the 802.11 AMP was requested to connect to a peer, but the connection failed.";
                    break;
                case 64:
                    str2 = "COARSE_CLOCK_ADJUSTMENT_REJECTED";
                    str3 = "The Coarse Clock Adjustment Rejected but Will Try to Adjust Using Clock Dragging error code indicates that the master, at this time, is unable to make a coarse adjustment to the piconet clock, using the supplied parameters. Instead the master will attempt to move the clock using clock dragging.";
                    break;
                case 65:
                    str2 = "TYPE_0_SUBMAP_NOT_DEFINED";
                    str3 = "The Type0 Submap Not Defined error code indicates that the LMP PDU is rejected because the Type 0 submap is not currently defined.";
                    break;
                case 66:
                    str2 = "UNKNOWN_ADVERTISING_IDENTIFIER";
                    str3 = "The Unknown Advertising Identifier error code indicates that a command was sent from the Host that should identify an Advertising or Sync handle, but the Advertising or Sync handle does not exist.";
                    break;
                case 67:
                    str2 = "LIMIT_REACHED";
                    str3 = "The Limit Reached error code indicates that number of operations requested has been reached and has indicated the completion of the activity (e.g., advertising or scanning).";
                    break;
                case 68:
                    str2 = "OPERATION_CANCELLED_BY_HOST";
                    str3 = "The Operation Cancelled by Host error code indicates a request to the Controller issued by the Host and still pending was successfully canceled.";
                    break;
            }
            if (str2.length() <= 0) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            sb.append(str2);
            if (!z || str3.length() <= 0) {
                str = "";
            } else {
                str = ": " + str3;
            }
            sb.append(str);
            return sb.toString();
        }
    }
}
