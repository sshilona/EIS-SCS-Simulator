package com.harmonic.eis.messages;

import java.nio.ByteBuffer;

public class ScgErrorMessage extends EisMessage {

    public ScgErrorMessage() {
    }

    @Override
    public void parseMessage(byte[] messageContent, int offset, StringBuilder sb) {
        ByteBuffer buffer = ByteBuffer.wrap(messageContent);
        buffer.position(offset);
        printTLVContent(buffer, messageContent.length - offset,sb);
    }

    @Override
    public byte[] serialize() {
        throw new UnsupportedOperationException("Unimplemented method 'serialize'");
    }
}