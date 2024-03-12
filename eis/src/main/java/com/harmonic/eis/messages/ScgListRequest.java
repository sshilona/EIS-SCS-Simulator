package com.harmonic.eis.messages;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ScgListRequest extends EisMessage {
    private short channelId = 0;
    private int msgLength = 0;
   

    public ScgListRequest() {
    }

    public ScgListRequest(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);
        this.channelId = (short) rootNode.path("channelId").asInt();
        msgLength += 6;
    }

    @Override
    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.order(ByteOrder.BIG_ENDIAN);

        // Header
        buffer.put(VERSION);
        buffer.putShort((short) EISMessageType.SCG_LIST_REQUEST.getType());
        buffer.putShort((short) msgLength);
        buffer.putShort((short) 0x0001).putShort((short) 2).putShort(channelId);

        // Flip the buffer to prepare it for reading
        buffer.flip();
        byte[] message = new byte[buffer.remaining()];
        buffer.get(message);
        return message;
    }

    @Override
    public void parseMessage(byte[] messageContent, int offset, StringBuilder sb) {
        ByteBuffer buffer = ByteBuffer.wrap(messageContent);
        buffer.position(offset);
        printTLVContent(buffer, messageContent.length - offset, sb);
    }
}
