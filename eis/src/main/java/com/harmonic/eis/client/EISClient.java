package com.harmonic.eis.client;

import java.nio.ByteBuffer;
import com.harmonic.eis.messages.EISMessageType;
import com.harmonic.eis.messages.EisMessage;
import com.harmonic.eis.util.MessageFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class EISClient {
    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public EISClient(String host, int port) throws IOException {
        // Establish a TCP connection to the SCS server
        this.socket = new Socket(host, port);
        // Create a DataOutputStream to send messages to the server
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        // Create a DataInputStream to read messages from the server
        this.inputStream = new DataInputStream(socket.getInputStream());
    }

    public String sendMessage(EisMessage message) throws IOException {
        byte[] serializedMessage = message.serialize();
        // Send the serialized message bytes
        outputStream.write(serializedMessage);
        // Flush the stream to ensure the message is sent
        String res = printMsg(serializedMessage);
        outputStream.flush();
        return res;
    }

    // Method to read the server's response after sending a message
    public String receiveMessage() throws IOException {
        // Read the first 5 bytes for the header
        byte[] header = new byte[5];
        inputStream.readFully(header);

        // Interpret bytes 4 and 5 as the message length
        ByteBuffer lengthBuffer = ByteBuffer.wrap(header, 3, 2);
        int messageLength = lengthBuffer.getShort() & 0xffff; // get the length as unsigned

        // Allocate buffer for full message (header + message)
        byte[] fullMessage = new byte[5 + messageLength];

        // Copy header into full message
        System.arraycopy(header, 0, fullMessage, 0, header.length);

        // Read the remaining message bytes based on the message length into the full
        // message buffer after the header
        inputStream.readFully(fullMessage, 5, messageLength);
        String res = printMsg(fullMessage);
        return res;
    }

    public String printMsg(byte[] msg) {
        StringBuilder sb = new StringBuilder();
        byte version = msg[0];
        short messageType = ByteBuffer.wrap(msg, 1, 2).getShort();
        short length = ByteBuffer.wrap(msg, 3, 2).getShort();
        sb.append("<b>Message Type: " + String.format("0x%04X", messageType) + " "
        + EISMessageType.fromType(messageType).getLabel() + "</b>\n\n");
        sb.append("Version: " + String.format("0x%02X", version) + "\n");
        sb.append("Message Length: " + (length & 0xFFFF) + "\n"); // Convert to unsigned
        sb.append("Message Content: " + "\n");
    
        EISMessageType typeEnum = EISMessageType.fromType(messageType);
        EisMessage message = MessageFactory.createMessage(typeEnum);
        if (message != null) {
            message.parseMessage(msg, 5,sb);
        } else {
            for (byte b : msg) {
                sb.append(String.format("0x%02X ", b));
            }
        }
        sb.append("\n---");
    
        // Print to the console
        System.out.print(sb.toString());
    
        // Return the string for the UI
        return sb.toString();
    }

    // Clean up resources, close streams and socket
    public void close() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
        if (outputStream != null) {
            outputStream.close();
        }
        if (socket != null) {
            socket.close();
        }
    }
}
