package com.harmonic.eis.messages;

import java.util.HashMap;
import java.util.Map;

public class ErrorCodes {
    private static final Map<String, String> errorStatusMap = new HashMap<>();
    static {
        // Individual error codes
        errorStatusMap.put("0x0000", "DVB Reserved");
        errorStatusMap.put("0x0001", "Invalid message");
        errorStatusMap.put("0x0002", "Unsupported protocol version");
        errorStatusMap.put("0x0003", "Unknown message_type value");
        errorStatusMap.put("0x0004", "Message too long");
        errorStatusMap.put("0x0005", "Inconsistent length for parameter");
        errorStatusMap.put("0x0006", "Missing mandatory parameter");
        errorStatusMap.put("0x0007", "Invalid value for parameter");
        errorStatusMap.put("0x0008", "Unknown EIS_channel_ID value");
        errorStatusMap.put("0x0009", "Unknown SCG_ID value");
        errorStatusMap.put("0x000A", "Max SCGs already defined");
        errorStatusMap.put("0x000B", "Service level SCG definitions not supported");
        errorStatusMap.put("0x000C", "Elementary Stream level SCG definitions not supported");
        errorStatusMap.put("0x000D", "Activation_time possibly too soon for SCs to be accurate");
        errorStatusMap.put("0x000E", "SCG definition cannot span transport boundaries");
        errorStatusMap.put("0x000F", "A resource does not exist on this SCS");
        errorStatusMap.put("0x0010", "A resource is already defined in an existing SCG");
        errorStatusMap.put("0x0011", "SCG may not contain one or more content entries and no ECM_Group entries");
        errorStatusMap.put("0x0012", "SCG may not contain one or more ECM_Group entries and no content entries");
        errorStatusMap.put("0x0013", "EIS_channel_ID value already in use");
        errorStatusMap.put("0x0014", "Unknown Super_CAS_Id.");
        errorStatusMap.put("0x7000", "Unknown error");
        errorStatusMap.put("0x7001", "Unrecoverable error");
    }

    public static String getLabelByCode(String code) {
        // Check individual error codes
        if (errorStatusMap.containsKey(code)) {
            return errorStatusMap.get(code);
        }
        // Check for ranges
        int errorCode = Integer.parseInt(code.replace("0x", ""), 16);
        if (errorCode >= 0x0015 && errorCode <= 0x6FFF) {
            return "Reserved";
        } else if (errorCode >= 0x7002 && errorCode <= 0x7FFF) {
            return "Reserved";
        } else if (errorCode >= 0x8000 && errorCode <= 0xFFFF) {
            return "EIS specific/CA system specific/User defined";
        }
        return "Code not found";
    }
}
