package com.maldosia.cloudshuttle.core.protocol;

public class ProtocolTemplates {
    public static ProtocolDefinition simpleTcp() {
        return ProtocolDslBuilder.create()
            .startFlag(0x68)
            .functionCode(1)
            .length(2)
            .body()
            .endFlag(0x16)
            .description("通用TCP协议模板")
            .protocolType("TCP")
            .build();
    }
} 