package com.harmonic.eis.messages;
public enum EISMessageType {
        CHANNEL_SETUP(0x0401, "Channel Setup"),
        CHANNEL_TEST(0x0402, "Channel Test"),
        CHANNEL_STATUS(0x0403, "Channel Status"),
        CHANNEL_CLOSE(0x0404, "Channel Close"),
        CHANNEL_ERROR(0x0405, "Channel Error"),
        CHANNEL_RESET(0x0406, "Channel Reset"),
        SCG_PROVISION(0x0408, "SCG Provision"),
        SCG_TEST(0x0409, "SCG Test"),
        SCG_STATUS(0x040A, "SCG Status"),
        SCG_ERROR(0x040B, "SCG Error"),
        SCG_LIST_REQUEST(0x040C, "SCG List Request"),
        SCG_LIST_RESPONSE(0x040D, "SCG List Response"),
        UNKNOWN(-1, "Unknown");
    
        private final short type;
        private final String label;
    
        EISMessageType(int type, String label) {
            this.type = (short)type;
            this.label = label;
        }
    
        public short getType() {
            return (short)type;
        }
    
        public String getLabel() {
            return label;
        }
    
        public static EISMessageType fromType(short type) {
            for (EISMessageType messageType : values()) {
                if (messageType.getType() == type) {
                    return messageType;
                }
            }
            return UNKNOWN;
        }
    }
    

