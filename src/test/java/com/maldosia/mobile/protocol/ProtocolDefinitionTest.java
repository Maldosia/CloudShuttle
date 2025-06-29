package com.maldosia.mobile.protocol;

import com.maldosia.cloudshuttle.core.protocol.DefaultProtocolDefinition;

/**
 * @author Maldosia
 * @since 2025/6/30
 */
public class ProtocolDefinitionTest {

    public static void main(String[] args) {
        // 1. 创建协议定义
        DefaultProtocolDefinition protocol = DefaultProtocolDefinition.builder()
                .addDelimiter("START_DELIMITER", new byte[4]) // 起始位
                .addField("CHECKSUM", 4)       // 校验位
                .addField("VERSION", 4)        // 版本
                .addField("FUNCTION_CODE", 4)  // 功能码
                .setLengthField("LENGTH")      // 长度字段名
                .addField("LENGTH", 4)         // 长度字段
                .addField("RESERVED", 12)      // 预留
                .setBodyField("BODY")          // 报文字段名
                .addDelimiter("END_DELIMITER", new byte[4]) // 结束位
                .build();
    }

}