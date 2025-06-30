package com.maldosia.mobile.tcp;

import com.maldosia.cloudshuttle.core.TcpClient;
import com.maldosia.cloudshuttle.core.Url;
import com.maldosia.cloudshuttle.core.options.NetworkOptions;
import com.maldosia.cloudshuttle.core.protocol.CommonProtocolDefinition;
import com.maldosia.mobile.protocol.MobileProtocol;
import com.maldosia.mobile.protocol.ScanRequest;

/**
 * @author Maldosia
 * @since 2025/6/25
 */
public class TcpClientTest {

    public static void main(String[] args) {
        // 1. 创建协议定义
        CommonProtocolDefinition protocol = CommonProtocolDefinition.builder()
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


        MobileProtocol mobileProtocol = new MobileProtocol(protocol);
        mobileProtocol.registerFrame(new ScanRequest());
        mobileProtocol.registerFrame(new ScanRequest());

        TcpClient tcpClient = new TcpClient(new Url("127.0.0.1", 18000), mobileProtocol);
        tcpClient.startup();
        tcpClient.option(NetworkOptions.RECONNECT_SWITCH, Boolean.TRUE);
        tcpClient.connect();
    }
    

}