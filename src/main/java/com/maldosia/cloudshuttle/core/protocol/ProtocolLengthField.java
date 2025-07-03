package com.maldosia.cloudshuttle.core.protocol;

/**
 * @author Maldosia
 * @since 2025/7/4
 */
public class ProtocolLengthField extends ProtocolField {

    private final ProtocolLengthFieldEnum lengthFieldEnum;

    public ProtocolLengthField(int length, ProtocolFieldEnum type, ProtocolLengthFieldEnum lengthFieldEnum) {
        super(length, type);
        this.lengthFieldEnum = lengthFieldEnum;
    }

    public ProtocolLengthField(byte[] value, ProtocolFieldEnum type, ProtocolLengthFieldEnum lengthFieldEnum) {
        super(value, type);
        this.lengthFieldEnum = lengthFieldEnum;
    }

    public ProtocolLengthFieldEnum getLengthFieldEnum() {
        return lengthFieldEnum;
    }

    public enum ProtocolLengthFieldEnum {
        PROTOCOL_LENGTH, BODY_LENGTH;
    }
}