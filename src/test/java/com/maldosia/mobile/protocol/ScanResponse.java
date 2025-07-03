package com.maldosia.mobile.protocol;

import com.maldosia.cloudshuttle.core.Message;
import com.maldosia.cloudshuttle.core.FunctionCode;
import com.maldosia.cloudshuttle.core.ProtocolField;

public class ScanResponse implements Message {

    @ProtocolField(order = 1, length = 4, isStartFlagField = true)
    private byte[] startFlag;

    @ProtocolField(order = 2, length = 4, isFunctionCodeField = true)
    private byte[] functionCode;

    @ProtocolField(order = 3, length = 4, isLengthField = true)
    private byte[] length;

    @ProtocolField(order = 4, length = 4)
    private byte[] reserved;

    @ProtocolField(isBody = true)
    private byte[] body;

    @Override
    public FunctionCode getFunctionCode() {
        return MobileFunctionCode.SCAN_RESPONSE;
    }

    @Override
    public void serializeBody() {

    }

    @Override
    public void deserializeBody() {

    }
}
