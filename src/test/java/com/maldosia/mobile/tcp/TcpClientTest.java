package com.maldosia.mobile.tcp;

import com.maldosia.cloudshuttle.core.DefaultCodec;
import com.maldosia.cloudshuttle.core.TcpClient;
import com.maldosia.cloudshuttle.core.Url;
import com.maldosia.cloudshuttle.core.options.NetworkOptions;
import com.maldosia.mobile.protocol.DetectRequest;

/**
 * @author Maldosia
 * @since 2025/6/25
 */
public class TcpClientTest {

    public static void main(String[] args) {
        TcpClient tcpClient = new TcpClient(new Url("127.0.0.1", 18000));
        tcpClient.registerProtocol(new DetectRequest());
        tcpClient.startup();
        tcpClient.option(NetworkOptions.RECONNECT_SWITCH, Boolean.TRUE);
        tcpClient.connect();
    }
    

}