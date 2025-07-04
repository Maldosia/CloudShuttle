package com.maldosia.mobile.tcp;

import com.maldosia.cloudshuttle.core.TcpClient;
import com.maldosia.cloudshuttle.core.Url;
import com.maldosia.cloudshuttle.core.options.NetworkOptions;
import com.maldosia.cloudshuttle.core.protocol.ProtocolFieldEnum;
import com.maldosia.cloudshuttle.core.protocol.ProtocolLengthField;
import com.maldosia.cloudshuttle.core.protocol.TcpProtocolDefinition;
import com.maldosia.mobile.protocol.MobileProtocol;
import com.maldosia.mobile.protocol.ScanRequest;
import com.maldosia.mobile.protocol.ScanResponse;

/**
 * @author Maldosia
 * @since 2025/6/25
 */
public class TcpClientTest {

    public static void main(String[] args) {
        // 1. 创建协议定义
        byte[] startFlag = new byte[]{(byte)0xAA, (byte)0x55, (byte)0x99, (byte)0x66};
        byte[] endFlag = new byte[]{(byte) 0x66, (byte) 0x99, (byte) 0x55, (byte) 0xAA};
        TcpProtocolDefinition protocolDefinition = TcpProtocolDefinition.builder()
                .addStartFlagField(startFlag) // 起始位
                .addField(4, ProtocolFieldEnum.HEADER)       // 校验位
                .addField(4, ProtocolFieldEnum.HEADER)       // 版本
                .addField(4, ProtocolFieldEnum.FUNCTION_CODE)      // 功能码
                .addLengthField(4, ProtocolLengthField.ProtocolLengthFieldEnum.PROTOCOL_LENGTH)      // 长度字段
                .addField(12, ProtocolFieldEnum.HEADER)      // 预留
                .addBodyField()
                .addEndFlagField(endFlag) // 结束位
                .build();


        MobileProtocol mobileProtocol = new MobileProtocol(protocolDefinition);
        mobileProtocol.registerFrame(new ScanRequest());
        mobileProtocol.registerFrame(new ScanResponse());



        TcpClient tcpClient = new TcpClient(new Url("127.0.0.1", 18000), mobileProtocol);
        tcpClient.startup();
        tcpClient.option(NetworkOptions.RECONNECT_SWITCH, Boolean.TRUE);
        tcpClient.connect();
    }
    

}