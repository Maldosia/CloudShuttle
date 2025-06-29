package com.maldosia.mobile.protocol;

import com.maldosia.cloudshuttle.core.FunctionCode;

public class ScanResponse extends MobileFrameHeader {
    @Override
    public FunctionCode getFunctionCode() {
        return MobileFunctionCode.SCAN_RESPONSE;
    }
}
