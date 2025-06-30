package com.maldosia.mobile.tcp;

import com.maldosia.cloudshuttle.core.TcpClient;
import com.maldosia.cloudshuttle.core.Url;
import com.maldosia.cloudshuttle.core.options.NetworkOptions;
import com.maldosia.cloudshuttle.core.protocol.CommonProtocolDefinition;
import com.maldosia.cloudshuttle.core.protocol.ProtocolFieldEnum;
import com.maldosia.mobile.protocol.MobileProtocol;
import com.maldosia.mobile.protocol.ScanRequest;

/**
 * @author Maldosia
 * @since 2025/6/25
 */
public class TcpClientTest {

    public static void main(String[] args) {
        // 1. 创建协议定义
        byte[] startFlag = new byte[]{(byte)0xAA, (byte)0x55, (byte)0x99, (byte)0x66};
        byte[] endFlag = new byte[]{(byte) 0x66, (byte) 0x99, (byte) 0x55, (byte) 0xAA};
        CommonProtocolDefinition protocol = CommonProtocolDefinition.builder()
                .addField(startFlag, ProtocolFieldEnum.START_FLAG) // 起始位
                .addField(4, ProtocolFieldEnum.HEADER)       // 校验位
                .addField(4, ProtocolFieldEnum.HEADER)        // 版本
                .addField(4, ProtocolFieldEnum.HEADER)  // 功能码
                .addField(4, ProtocolFieldEnum.LENGTH)      // 长度字段
                .addField(12, ProtocolFieldEnum.HEADER)      // 预留
                .addField(endFlag, ProtocolFieldEnum.HEADER) // 结束位
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