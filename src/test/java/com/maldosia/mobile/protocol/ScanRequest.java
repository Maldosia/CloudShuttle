package com.maldosia.mobile.protocol;

import com.maldosia.cloudshuttle.core.FunctionCode;
import com.maldosia.cloudshuttle.core.annotation.ProtocolField;

public class ScanRequest extends MobileCommandHeader {

    @ProtocolField(order = 5, length = 4)
    private int direction;

    @ProtocolField(order = 6, length = 4)
    private int frequency;

    @Override
    public FunctionCode getFunctionCode() {
        return MobileFunctionCode.SCAN_REQUEST;
    }
}
