package com.maldosia.mobile.protocol;

import com.maldosia.cloudshuttle.core.Frame;
import com.maldosia.cloudshuttle.core.FunctionCode;
import com.maldosia.cloudshuttle.core.protocol.ProtocolField;

public class ScanRequest implements Frame {

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

    @ProtocolField(order = 5, length = 4)
    private int direction;

    @ProtocolField(order = 6, length = 4)
    private int frequency;

    @Override
    public FunctionCode getFunctionCode() {
        return MobileFunctionCode.SCAN_REQUEST;
    }

    @Override
    public void serializeBody() {

    }

    @Override
    public void deserializeBody() {

    }
}
