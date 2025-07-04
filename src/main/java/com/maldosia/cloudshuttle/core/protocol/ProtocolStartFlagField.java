package com.maldosia.cloudshuttle.core.protocol;

/**
 * @author Maldosia
 * @since 2025/7/4
 */
public class ProtocolStartFlagField extends ProtocolField {

    private final byte[] startFlag;

    public ProtocolStartFlagField(byte[] startFlag) {
        super(startFlag, ProtocolFieldEnum.START_FLAG);
        this.startFlag = startFlag;
    }

    public byte[] getStartFlag() {
        return this.startFlag;
    }
}