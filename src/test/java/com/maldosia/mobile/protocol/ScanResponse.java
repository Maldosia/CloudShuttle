package com.maldosia.mobile.protocol;

import com.maldosia.cloudshuttle.core.Frame;
import com.maldosia.cloudshuttle.core.ProtocolField;
import com.maldosia.cloudshuttle.core.protocol.ProtocolFunctionCode;

@ProtocolFunctionCode(value = {0x71, 0x01})
public class ScanResponse extends Frame {

    @ProtocolField(order = 6, length = 4)
    private int mcc;

    @ProtocolField(order = 7, length = 4)
    private int mnc;

    @Override
    public void serializeBody() {

    }

    @Override
    public void deserializeBody() {

    }
}
