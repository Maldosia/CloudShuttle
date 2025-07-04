package com.maldosia.mobile.protocol;

import com.maldosia.cloudshuttle.core.Frame;
import com.maldosia.cloudshuttle.core.ProtocolField;
import com.maldosia.cloudshuttle.core.protocol.ProtocolFunctionCode;

@ProtocolFunctionCode({0x70, 0x01})
public class ScanRequest extends Frame {

    @ProtocolField(order = 1, length = 4)
    private int direction;

    @ProtocolField(order = 2, length = 4)
    private int frequency;

    @Override
    public void serializeBody() {

    }

    @Override
    public void deserializeBody() {

    }
}
