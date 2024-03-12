package com.harmonic.eis.messages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ChannelResetMessage extends EisMessage {
    private short eisChannelId;
    private static final byte EIS_CHANNEL_ID_TYPE = 0x01;
   
    public ChannelResetMessage(short eisChannelId) {
        this.eisChannelId = eisChannelId;
    }

    public ChannelResetMessage() {
    }

    @Override
    public byte[] serialize() {
        int tlvLength = 2 + 2 + 2; 
        int totalLength = 5 + tlvLength;
        ByteBuffer buffer = ByteBuffer.allocate(totalLength);
        buffer.order(ByteOrder.BIG_ENDIAN);

        // Header
        buffer.put(VERSION);
        buffer.putShort((short) EISMessageType.CHANNEL_RESET.getType());
        buffer.putShort((short) tlvLength);
        // Message containing TLV parameters
        buffer.putShort(EIS_CHANNEL_ID_TYPE); 
        buffer.putShort((short) 2); 
        buffer.putShort(eisChannelId);
        return buffer.array();
    }

    @Override
    public void parseMessage(byte[] messageContent, int offset, StringBuilder sb) {
        if (messageContent.length - offset < 6) {
            throw new IllegalArgumentException("Invalid message length for Channel Setup.");
        }
        ByteBuffer buffer = ByteBuffer.wrap(messageContent);
        buffer.position(offset);
        printTLVContent(buffer, messageContent.length - offset, sb);
    }
}