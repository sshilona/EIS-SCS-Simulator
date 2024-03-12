package com.harmonic.eis.messages;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ActivationTimeParser {

    public static byte[] parseActivationTime(JsonNode activationTimeNode) throws IOException {
        int year = activationTimeNode.get("yearLSB").asInt() + (activationTimeNode.get("yearMSB").asInt() << 8);
        int month = activationTimeNode.get("month").asInt();
        int day = activationTimeNode.get("day").asInt();
        int hour = activationTimeNode.get("hour").asInt();
        int minute = activationTimeNode.get("minute").asInt();
        int second = activationTimeNode.get("second").asInt();
        int hundredthOfSecond = activationTimeNode.get("hundredthOfSecond").asInt();

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putShort((short) year);
        buffer.put((byte) month);
        buffer.put((byte) day);
        buffer.put((byte) hour);
        buffer.put((byte) minute);
        buffer.put((byte) second);
        buffer.put((byte) hundredthOfSecond);

        return buffer.array();
    }
}
