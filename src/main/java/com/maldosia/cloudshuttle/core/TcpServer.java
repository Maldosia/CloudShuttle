package com.maldosia.cloudshuttle.core;


import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * TCP服务端实现
 */
public class TcpServer extends AbstractTcpEndpoint {
    /**
     * 构造函数
     * @param port 监听端口
     * @param protocol 协议处理器
     */
    public TcpServer(int port, Protocol protocol) {
        super(null, port, protocol);
    }

    @Override
    protected AbstractBootstrap<?, ?> createBootstrap() {
        return new ServerBootstrap();
    }

    @Override
    protected void configureBootstrap(AbstractBootstrap<?, ?> bootstrap) {
        ServerBootstrap b = (ServerBootstrap) bootstrap;
        b.channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    protected Channel doStart(AbstractBootstrap<?, ?> bootstrap) throws InterruptedException {
        ServerBootstrap b = (ServerBootstrap) bootstrap;
        return b.bind(port).sync().channel();
    }
}
