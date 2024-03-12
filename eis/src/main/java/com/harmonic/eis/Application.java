package com.harmonic.eis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harmonic.eis.client.EISClient;
import com.harmonic.eis.messages.*;

@SpringBootApplication
public class Application {
    static EISClient client;
    static ObjectMapper objectMapper = new ObjectMapper();

    public static void createClient(String body) throws IOException {
        JsonNode rootNode = objectMapper.readTree(body);
        String ip = rootNode.path("ip").asText();
        int port = rootNode.path("port").asInt();
        client = new EISClient(ip, port);
    }

    public static void runScript() throws IOException {
        client = new EISClient("10.42.21.223", 11000);

        sendMessageAndReceiveResponse(new ChannelSetupMessage((short) 111));
        sendMessageAndReceiveResponse(new ChannelTestMessage((short) 111));

        String pathToJson = "src/main/java/com/harmonic/eis/debug/http/scgProvision.json";
        String jsonContent = new String(Files.readAllBytes(Paths.get(pathToJson)));
        sendMessageAndReceiveResponse(new ScgProvision(jsonContent));

        client.close();
    }

    private static void sendMessageAndReceiveResponse(EisMessage message) throws IOException {
        client.sendMessage(message);
        client.receiveMessage();
    }
    
    private static String processChannelMessage(String body, MessageType messageType) throws IOException {
        JsonNode rootNode = objectMapper.readTree(body);
        int channelId = rootNode.path("channelId").asInt();
        EisMessage message;
        switch (messageType) {
            case SETUP:
                message = new ChannelSetupMessage((short) channelId);
                break;
            case CLOSE:
                message = new ChannelCloseMessage((short) channelId);
                break;
            case TEST:
                message = new ChannelTestMessage((short) channelId);
                break;
            case RESET:
                message = new ChannelResetMessage((short) channelId);
                break;
            default:
                throw new IllegalArgumentException("Invalid message type");
        }
        return sendMessageAndGetResponse(message);
    }

    private static String sendMessageAndGetResponse(EisMessage message) throws IOException {
        String res = client.sendMessage(message);
        return res + client.receiveMessage();
    }

    public static String channelSetup(String body) throws IOException {
        return processChannelMessage(body, MessageType.SETUP);
    }

    public static String channelClose(String body) throws IOException {
        return processChannelMessage(body, MessageType.CLOSE);
    }

    public static String channelTest(String body) throws IOException {
        return processChannelMessage(body, MessageType.TEST);
    }

    public static String channelReset(String body) throws IOException {
        return processChannelMessage(body, MessageType.RESET);
    }

    public static String scgProvision(String body) throws IOException {
        EisMessage message = new ScgProvision(body);
        String res = client.sendMessage(message);
        return res + client.receiveMessage();
    }

    public static String scgList(String body) throws IOException {
        EisMessage message = new ScgListRequest(body);
        String res = client.sendMessage(message);
        return res + client.receiveMessage();
    }

    
    public static String scgTest(String body) throws IOException {
        EisMessage message = new ScgTestMessage(body);
        String res = client.sendMessage(message);
        return res + client.receiveMessage();
    }

    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class, args);
    }

    enum MessageType {
        SETUP, CLOSE, TEST, RESET
    }
}  
