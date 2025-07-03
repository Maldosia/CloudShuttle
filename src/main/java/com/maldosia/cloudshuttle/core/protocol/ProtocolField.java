package com.maldosia.cloudshuttle.core.protocol;

/**
 * @author Maldosia
 * @since 2025/6/30
 */
public class ProtocolField {

    private byte[] value;
    private final int length;
    private final ProtocolFieldEnum type;

    public ProtocolField(int length, ProtocolFieldEnum type) {
        this.value = new byte[length];
        this.length = length;
        this.type = type;
    }

    public ProtocolField(byte[] value, ProtocolFieldEnum type) {
        this.value = value;
        this.length = value.length;
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public ProtocolFieldEnum getType() {
        return type;
    }
    
    public byte[] getValue() {
        return value;
    }
    
    public void setValue(byte[] value) {
        this.value = value;
    }
}