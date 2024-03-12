package com.harmonic.eis.messages;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public abstract class EisMessage {
    protected static final byte VERSION = 0x03;

    public abstract byte[] serialize();

    public abstract void parseMessage(byte[] messageContent, int offset, StringBuilder sb);

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Map<Short, String> parameterTypeLabels = new HashMap<>();

    static {
        parameterTypeLabels.put((short) 0x0000, "DVB Reserved");
        parameterTypeLabels.put((short) 0x0001, "EIS Channel ID");
        parameterTypeLabels.put((short) 0x0002, "Service Flag");
        parameterTypeLabels.put((short) 0x0003, "Component Flag");
        parameterTypeLabels.put((short) 0x0004, "Max SCG");
        parameterTypeLabels.put((short) 0x0005, "ECM Group");
        parameterTypeLabels.put((short) 0x0006, "SCG ID");
        parameterTypeLabels.put((short) 0x0007, "SCG Reference ID");
        parameterTypeLabels.put((short) 0x0008, "Super CAS ID");
        parameterTypeLabels.put((short) 0x0009, "ECM ID");
        parameterTypeLabels.put((short) 0x000A, "Access Criteria");
        parameterTypeLabels.put((short) 0x000B, "Activation Time");
        parameterTypeLabels.put((short) 0x000C, "Activation Pending Flag");
        parameterTypeLabels.put((short) 0x000D, "Component ID");
        parameterTypeLabels.put((short) 0x000E, "Service ID");
        parameterTypeLabels.put((short) 0x000F, "Transport Stream ID");
        parameterTypeLabels.put((short) 0x0010, "AC Changed Flag");
        parameterTypeLabels.put((short) 0x0011, "SCG Current Reference ID");
        parameterTypeLabels.put((short) 0x0012, "SCG Pending Reference ID");
        parameterTypeLabels.put((short) 0x0013, "CP Duration Flag");
        parameterTypeLabels.put((short) 0x0014, "Recommended CP Duration");
        parameterTypeLabels.put((short) 0x0015, "SCG Nominal CP Duration");
        parameterTypeLabels.put((short) 0x0016, "Network ID");
        parameterTypeLabels.put((short) 0x7000, "Error Status");
        parameterTypeLabels.put((short) 0x7001, "Error Information");
        parameterTypeLabels.put((short) 0x7002, "Error Description");
    }

    public String getLabelByType(short type) {
        return parameterTypeLabels.getOrDefault(type, "Unknown Type");
    }

    protected void printTLVContent(ByteBuffer buffer, int totalLength, StringBuilder sb) {
        while (totalLength > 0) {
            short type = buffer.getShort();
            int length = Short.toUnsignedInt(buffer.getShort());
            totalLength -= 4; // Account for the type and length fields themselves
            String label = getLabelByType(type);
            if (type == 0x0b) { // Activation Time
                handleActivationTime(buffer, sb, label);
            } else if (type == 0x05) { // ECM Group, requires special handling
                sb.append("\n");
                System.out.println();
                printTLVContent(buffer, length, sb); // Recursive call for nested TLV
            } else {
                handleGenericValue(buffer, type, length, sb, label);
            }
            totalLength -= length; // Deduct the length of the current TLV content
        }
    }

    private void handleGenericValue(ByteBuffer buffer, short type, int length, StringBuilder sb, String label) {
        String output;
        switch (length) {
            case 1: // Handling single-byte values
                int value1 = buffer.get() & 0xFF; // Ensure it's treated as unsigned
                output = label + ": " + value1;
                break;
            case 2: // Handling two-byte values
                int value2 = Short.toUnsignedInt(buffer.getShort());
                output = label + ": " + value2;
                if (type == 0x7000) { // Example: For error status, you might want additional description
                    output += " (" + ErrorCodes.getLabelByCode("0x" + Integer.toHexString(value2)) + ")";
                }
                break;
            case 4: // Handling four-byte values
                int value4 = buffer.getInt();
                output = label + ": " + value4;
                break;
            default: // For other lengths, assuming hexadecimal representation is preferred
                byte[] bytes = new byte[length];
                buffer.get(bytes);
                String valueDescription = formatBytesAsHex(bytes);
                output = label + ": " + valueDescription;
                break;
        }
        System.out.println(output);
        sb.append(output).append("\n");
    }
    

    private void handleActivationTime(ByteBuffer buffer, StringBuilder sb, String label) {
        buffer.get();
        LocalDateTime dateTime = LocalDateTime.of(
                buffer.get() + 2000, // Year
                buffer.get(), // Month
                buffer.get(), // Day
                buffer.get(), // Hour
                buffer.get(), // Minute
                buffer.get(), // Second
                buffer.get() * 10000000);
        String formattedDate = DATE_TIME_FORMATTER.format(dateTime);
        String output = label + ": " + formattedDate;
        System.out.println(output);
        sb.append(output).append("\n");
    }

    private static String formatBytesAsHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder("0x");
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b & 0xFF));
        }
        return hexString.toString();
    }
}
