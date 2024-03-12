package com.harmonic.eis.util;

import com.harmonic.eis.messages.ChannelCloseMessage;
import com.harmonic.eis.messages.ChannelResetMessage;
import com.harmonic.eis.messages.ChannelSetupMessage;
import com.harmonic.eis.messages.ChannelStatusMessage;
import com.harmonic.eis.messages.ChannelTestMessage;
import com.harmonic.eis.messages.EISMessageType;
import com.harmonic.eis.messages.EisMessage;
import com.harmonic.eis.messages.ScgErrorMessage;
import com.harmonic.eis.messages.ScgListRequest;
import com.harmonic.eis.messages.ScgListResponse;
import com.harmonic.eis.messages.ScgProvision;
import com.harmonic.eis.messages.ScgStatusMessage;
import com.harmonic.eis.messages.ScgTestMessage;

public class MessageFactory {
    public static EisMessage createMessage(EISMessageType messageType) {
        switch (messageType) {
            case CHANNEL_SETUP:
                return new ChannelSetupMessage();
            case CHANNEL_RESET:
                return new ChannelResetMessage();
            case CHANNEL_CLOSE:
                return new ChannelCloseMessage();
            case CHANNEL_STATUS:
                return new ChannelStatusMessage();
            case CHANNEL_TEST:
                return new ChannelTestMessage();
            case SCG_LIST_REQUEST:
                return new ScgListRequest();
            case SCG_LIST_RESPONSE:
                return new ScgListResponse();
            case SCG_STATUS:
                return new ScgStatusMessage();
            case SCG_PROVISION:
                return new ScgProvision();
            case SCG_TEST:
                return new ScgTestMessage();
            case SCG_ERROR:
                return new ScgErrorMessage();
            default:
                System.out.print("Unknown message type: " + messageType);
                System.out.println();
        }
        return null;
    }
}