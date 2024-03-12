package com.harmonic.eis.messages;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class ScgProvision extends EisMessage {
    private short channelId = 0;
    private short scgId = 0;
    private short transportStreamId = 0;
    private short originalNetworkId = 0;
    private long scgReferenceId = 0;
    private short recommendedCpDuration = 0;
    private short serviceId = 0;
    private List<EcmGroup> ecmGroups = null;
    private int msgLength = 0;
    private int ecmGroupMsgLength = 0;
    private byte[] activationTime;

    public ScgProvision() {
    }

    public ScgProvision(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);

        this.channelId = (short) rootNode.path("channelId").asInt();
        msgLength += 6;
        this.scgId = (short) rootNode.path("scgId").asInt();
        msgLength += 6;
        this.transportStreamId = (short) rootNode.path("transportStreamId").asInt();
        msgLength += 6;
        if (VERSION > 2) {
            this.originalNetworkId = (short) rootNode.path("originalNetworkId").asInt();
            msgLength += 6;
        }
        this.scgReferenceId = rootNode.path("scgReferenceId").asLong();
        msgLength += 8;
        this.recommendedCpDuration = (short) rootNode.path("recommendedCpDuration").asInt();
        msgLength += 6;
        this.serviceId = (short) rootNode.path("serviceId").asInt();
        msgLength += 6;
        JsonNode activation = rootNode.path("activationTime");
        if (!activation.isEmpty()) {
            this.activationTime = ActivationTimeParser.parseActivationTime(activation);
            msgLength += 12;
        }
        this.ecmGroups = new ArrayList<>();

        JsonNode ecmGroupsNode = rootNode.path("ecmGroups");
        if (ecmGroupsNode.isArray()) {
            for (JsonNode groupNode : ecmGroupsNode) {
                msgLength += 4;
                short ecmId = (short) groupNode.path("ecmId").asInt();
                msgLength += 6;
                ecmGroupMsgLength = 6;
                String superCasIdHex = groupNode.path("superCasId").asText(); // Gets the hex value as a string
                int superCasID = Integer.parseInt(superCasIdHex, 16); // Converts hexadecimal string to int
                msgLength += 8;
                ecmGroupMsgLength += 8;
                byte[] accessCriteria = extractAccessCriteria(groupNode.path("accessCriteria"));
                int accessCriteriaLength = accessCriteria.length;
                msgLength += (4 + accessCriteriaLength);
                ecmGroupMsgLength += (4 + accessCriteriaLength);
                boolean acChangeFlag = groupNode.path("acChangeFlag").asBoolean();
                msgLength += 5;
                ecmGroupMsgLength += 5;
                EcmGroup ecmGroup = new EcmGroup(ecmId, superCasID, accessCriteria, acChangeFlag, ecmGroupMsgLength);
                this.ecmGroups.add(ecmGroup);
            }
        }
    }

    private byte[] extractAccessCriteria(JsonNode accessCriteriaNode) {
        String hexString = accessCriteriaNode.asText();
        return hexStringToByteArray(hexString);
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    @Override
    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.order(ByteOrder.BIG_ENDIAN); 

        // Header
        buffer.put(VERSION);
        buffer.putShort((short) EISMessageType.SCG_PROVISION.getType());
        buffer.putShort((short) msgLength);
        buffer.putShort((short) 0x0001).putShort((short) 2).putShort(channelId);
        buffer.putShort((short) 0x0006).putShort((short) 2).putShort(scgId);
        buffer.putShort((short) 0x000F).putShort((short) 2).putShort(transportStreamId);
        buffer.putShort((short) 0x0016).putShort((short) 2).putShort(originalNetworkId);
        buffer.putShort((short) 0x0007).putShort((short) 4).putInt((int) scgReferenceId);
        buffer.putShort((short) 0x0014).putShort((short) 2).putShort(recommendedCpDuration);
        buffer.putShort((short) 0x000E).putShort((short) 2).putShort(serviceId);

        // Serializing activationTime
        if (activationTime != null) {
            buffer.putShort((short) 0x000B).putShort((short) 8).put(activationTime);
        }
        // Serializing ECM_GROUPS
        for (EcmGroup ecmGroup : ecmGroups) {
            buffer.putShort((short) 0x0005); // ECM_GROUP type
            buffer.putShort((short) ecmGroup.length); // Length of ECM_GROUP value
            byte[] ecmSerialized = ecmGroup.serialize();
            buffer.put(ecmSerialized); // ECM_GROUP value
        }

        // Flip the buffer to prepare it for reading
        buffer.flip();
        byte[] message = new byte[buffer.remaining()];
        buffer.get(message);
        return message;
    }

    public class EcmGroup {
        private short ecmId;
        private int superCasID; 
        private byte[] accessCriteria; 
        private boolean acChangeFlag;
        private int length;

        // Constructor initializing fields
        public EcmGroup(short ecmId, int superCasID, byte[] accessCriteria, boolean acChangeFlag, int length) {
            this.ecmId = ecmId;
            this.superCasID = superCasID;
            this.accessCriteria = accessCriteria; 
            this.acChangeFlag = acChangeFlag;
            this.length = length;
        }

        public byte[] serialize() {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.putShort((short) 0x0009).putShort((short) 2).putShort(ecmId);
            buffer.putShort((short) 0x0008).putShort((short) 4).putInt(superCasID);
            buffer.putShort((short) 0x000a).putShort((short) accessCriteria.length).put(accessCriteria);
            buffer.putShort((short) 0x0010).putShort((short) 1).put((byte) (acChangeFlag ? 1 : 0));

            // Prepare the buffer to be read
            buffer.flip();
            byte[] serializedData = new byte[buffer.remaining()];
            buffer.get(serializedData);
            return serializedData;
        }
    }

    @Override
    public void parseMessage(byte[] messageContent, int offset, StringBuilder sb) {
        if (messageContent.length - offset < msgLength) {
            throw new IllegalArgumentException("Invalid message length for Channel Setup.");
        }
        ByteBuffer buffer = ByteBuffer.wrap(messageContent);
        buffer.position(offset);
        printTLVContent(buffer, messageContent.length - offset, sb);
    }
}
