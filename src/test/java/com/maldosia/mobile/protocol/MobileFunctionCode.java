package com.maldosia.mobile.protocol;

import com.maldosia.cloudshuttle.core.FunctionCode;

public enum MobileFunctionCode implements FunctionCode {
    DETECT_REQUEST(new byte[]{}, "开启侦码", "ACTIVE DETECT"),
    DETECT_RESPONSE(new byte[]{}, "侦码响应", "ACTIVE DETECT"),
    SCAN_REQUEST(new byte[]{}, "开启扫频", "ACTIVE SCAN"),
    SCAN_RESPONSE(new byte[]{}, "扫频响应", "ACTIVE SCAN");

    private final byte[] code;
    private final String name;
    private final String module;

    MobileFunctionCode(byte[] code, String name, String module) {
        this.code = code;
        this.name = name;
        this.module = module;
    }

    @Override
    public byte[] code() {
        return code;
    }
}
