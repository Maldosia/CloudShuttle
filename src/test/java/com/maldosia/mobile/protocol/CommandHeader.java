package com.maldosia.mobile.protocol;

import com.maldosia.cloudshuttle.core.Command;
import com.maldosia.cloudshuttle.core.annotation.ProtocolField;

public abstract class CommandHeader implements Command {

    @ProtocolField(order = 1, length = 4)
    private byte[] startFlag;

    @ProtocolField(order = 2, length = 4, isFunctionCodeField = true)
    private byte[] functionCode;

    @ProtocolField(order = 3, length = 4)
    private byte[] length;

    @ProtocolField(order = 4, length = 4)
    private byte[] reserved;

//    @ProtocolField(order = 5)
    private byte[] body;

    @Override
    public void serializeContent() {

    }

    @Override
    public void deserializeContent() {

    }
}
