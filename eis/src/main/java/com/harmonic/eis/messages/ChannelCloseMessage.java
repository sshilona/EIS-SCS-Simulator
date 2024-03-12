package com.harmonic.eis.messages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ChannelCloseMessage extends EisMessage {
    private static final byte EIS_CHANNEL_ID_TYPE = 0x01;
    private short eisChannelId;

    public ChannelCloseMessage(short eisChannelId) {
        this.eisChannelId = eisChannelId;
    }

    public ChannelCloseMessage() {
    }

    @Override
    public byte[] serialize() {
        int tlvLength = 2 + 2 + 2; // Type (1 byte) + Length (1 byte) + EIS channel ID (2 bytes)
        int totalLength = 5 + tlvLength;
        ByteBuffer buffer = ByteBuffer.allocate(totalLength);
        buffer.order(ByteOrder.BIG_ENDIAN); // ensure big-endian byte order

        // Header
        buffer.put(VERSION);
        buffer.putShort((short) EISMessageType.CHANNEL_CLOSE.getType());
        buffer.putShort((short) tlvLength);
        // Message containing TLV parameters
        buffer.putShort(EIS_CHANNEL_ID_TYPE); 
        buffer.putShort((short) 2); // Length of the EIS_CHANNEL_ID (2 bytes)
        buffer.putShort(eisChannelId); // Value of the EIS_CHANNEL_ID
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