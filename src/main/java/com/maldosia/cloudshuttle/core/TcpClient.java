package com.maldosia.cloudshuttle.core;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * TCP客户端实现
 */
public class TcpClient extends AbstractTcpEndpoint {
    /**
     * 构造函数
     * @param host 主机地址
     * @param port 端口号
     * @param protocol 协议处理器
     */
    public TcpClient(String host, int port, Protocol protocol) {
        super(host, port, protocol);
    }

    @Override
    protected AbstractBootstrap<?, ?> createBootstrap() {
        return new Bootstrap();
    }

    @Override
    protected void configureBootstrap(AbstractBootstrap<?, ?> bootstrap) {
        Bootstrap b = (Bootstrap) bootstrap;
        b.channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
    }

    @Override
    protected Channel doStart(AbstractBootstrap<?, ?> bootstrap) throws InterruptedException {
        Bootstrap b = (Bootstrap) bootstrap;
        return b.connect(host, port).sync().channel();
    }
}
