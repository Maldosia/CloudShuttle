package com.maldosia.cloudshuttle.core.protocol;

/**
 * @author Maldosia
 * @since 2025/6/30
 */
public class ProtocolField {

    private final int length;
    private final ProtocolFieldEnum type;

    public ProtocolField(int length, ProtocolFieldEnum type) {
        this.length = length;
        this.type = type;
    }

    public ProtocolField(byte[] value, ProtocolFieldEnum type) {
        this.length = value.length;
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public ProtocolFieldEnum getType() {
        return type;
    }
}