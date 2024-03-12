package com.harmonic.eis.messages;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public abstract class EisMessage {
    protected static final byte VERSION = 0x03;

    public abstract byte[] serialize();

    public abstract void parseMessage(byte[] messageContent, int offset, StringBuilder sb);

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

    public void printTLVContent(ByteBuffer buffer, int totalLength, StringBuilder sb) {
        int i = totalLength, type, length, val;
        while (i > 0) {
            type = buffer.getShort();
            length = buffer.getShort();
            i -= 4;
            if (length == 1) {
                val = buffer.get();
                System.out.println(getLabelByType((short) type) + ": " + val);
                sb.append(getLabelByType((short) type) + ": " + val + "\n");
            } else if (length == 2) {
                val = Short.toUnsignedInt(buffer.getShort());
                String description = "";
                if (type == 0x7000) // error status
                {
                    String hexString = String.format("0x%04X", val);
                    description += " (" + ErrorCodes.getLabelByCode(hexString) + ")";
                }
                System.out.println(getLabelByType((short) type) + ": " + val + description);
                sb.append(getLabelByType((short) type) + ": " + val + description + "\n");
            } else if (length == 4) {
                val = buffer.getInt();
                System.out.println(getLabelByType((short) type) + ": " + val);
                sb.append(getLabelByType((short) type) + ": " + val + "\n");
            } else if (type == 0x5) {
                // ECM Group case
                System.out.println();
                sb.append("\n");
                printTLVContent(buffer, length, sb);
            } else if (type == 0x0b) {
                printActivationTime(buffer, sb);
            } else {
                val = 0;
                System.out.print(getLabelByType((short) type) + ": ");
                sb.append(getLabelByType((short) type) + ": ");
                StringBuilder hexString = new StringBuilder();
                for (int j = 0; j < length; j++) {
                    val = buffer.get();
                    String hex = String.format("%02X", val & 0xFF); // Bitwise AND with 0xFF to prevent sign extension
                    hexString.append(hex); 
                }
                System.out.print("0x" + hexString.toString()); 
                sb.append("0x").append(hexString.toString());                                                            
                System.out.println();
                sb.append("\n");
            }
            i -= length;
        }
    }

    private static int bcdToDecimal(byte b) {
        return ((b >> 4) & 0x0F) * 10 + (b & 0x0F);
    }

    private void printActivationTime(ByteBuffer buffer, StringBuilder sb) {
        buffer.get();
        int year = (buffer.get());
        int month = bcdToDecimal(buffer.get());
        int day = bcdToDecimal(buffer.get());
        int hour = bcdToDecimal(buffer.get());
        int minute = bcdToDecimal(buffer.get());
        int second = bcdToDecimal(buffer.get());
        int hundredthSecond = bcdToDecimal(buffer.get());

        // Assuming the year needs to be offset by 2000
        year += 2000;

        System.out.println("Year: " + year);
        sb.append("Year: " + year + '\n');
        System.out.println("Month: " + month);
        sb.append("Month: " + month + '\n');
        System.out.println("Day: " + day);
        sb.append("Day: " + day + '\n');
        System.out.println("Hour: " + hour);
        sb.append("Hour: " + hour + '\n');
        System.out.println("Minute: " + minute);
        sb.append("Minute: " + minute + '\n');
        System.out.println("Second: " + second);
        sb.append("Second: " + second + '\n');
        System.out.println("Hundredth of a second: " + hundredthSecond);
        sb.append("Hundredth of a second: " + hundredthSecond + '\n');
    }
}