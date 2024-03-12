package com.harmonic.eis.messages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ScgTestMessage extends EisMessage {

    private short eisChannelId;
    private short scgId;
    private int msgLength = 0;

    public ScgTestMessage() {
       
    }

    public ScgTestMessage(String json) throws JsonMappingException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);

        this.eisChannelId = (short) rootNode.path("channelId").asInt();
        msgLength += 6;
        this.scgId = (short) rootNode.path("scgId").asInt();
        msgLength += 6;
    }

    @Override
    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.order(ByteOrder.BIG_ENDIAN); 

        // Header
        buffer.put(VERSION);
        buffer.putShort((short) EISMessageType.SCG_TEST.getType());
        buffer.putShort((short) msgLength);
        buffer.putShort((short) 0x0001).putShort((short) 2).putShort(eisChannelId);
        buffer.putShort((short) 0x0006).putShort((short) 2).putShort(scgId);

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