package com.maldosia.mobile.protocol;

import com.maldosia.cloudshuttle.core.Command;
import com.maldosia.cloudshuttle.core.annotation.ProtocolField;

public abstract class MobileCommandHeader implements Command {

    @ProtocolField(order = 1, length = 4, isStartFlagField = true)
    private byte[] startFlag;

    @ProtocolField(order = 2, length = 4, isFunctionCodeField = true)
    private byte[] functionCode;

    @ProtocolField(order = 3, length = 4, isLengthField = true)
    private byte[] length;

    @ProtocolField(order = 4, length = 4)
    private byte[] reserved;

    @ProtocolField(isContentField = true)
    private byte[] content;

    @Override
    public byte[] getStartFlag() {
        return new byte[]{(byte) 0xAA, (byte) 0x55, (byte) 0x99, (byte) 0x66};
    }

    @Override
    public byte[] getEndFlag() {
        return new byte[0];
    }

    @Override
    public void serializeContent() {

    }

    @Override
    public void deserializeContent() {

    }
}
