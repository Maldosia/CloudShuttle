package com.maldosia.cloudshuttle.core;



import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * TCP服务端实现
 */
public class TcpServer extends AbstractTcpEndpoint {

    private final EventLoopGroup bossGroup;

    /**
     * 构造函数
     *
     * @param port     监听端口
     * @param protocol 协议处理器
     */
    public TcpServer(int port, Protocol protocol) {
        super(null, port, protocol);
        this.bossGroup = new NioEventLoopGroup(1); // 单独boss组
    }

    @Override
    protected void configureHandler(AbstractBootstrap<?, ?> bootstrap) {
        ServerBootstrap b = (ServerBootstrap) bootstrap;
        // 服务端使用 childHandler()
        b.childHandler(createChannelInitializer());
    }

    @Override
    protected AbstractBootstrap<?, ?> createBootstrap() {
        return new ServerBootstrap().channel(NioServerSocketChannel.class);
    }

    @Override
    protected void configureCommonOptions(AbstractBootstrap<?, ?> bootstrap) {
        //super.configureCommonOptions(bootstrap);
        ServerBootstrap b = (ServerBootstrap) bootstrap;
        b.childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public void startup() {
        try {
            group = new NioEventLoopGroup();
            ServerBootstrap b = (ServerBootstrap) createBootstrap();

            // 配置组：bossGroup处理连接，workerGroup处理I/O
            b.group(bossGroup, group);

            configureCommonOptions(b);
            configureHandler(b);

            channel = doStart(b);
            System.out.println("TCP Server started on port " + port);
        } catch (Exception e) {
            shutdown();
            throw new RuntimeException("Server startup failed", e);
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        System.out.println("TCP Server stopped");
    }

    @Override
    protected Channel doStart(AbstractBootstrap<?, ?> bootstrap) throws InterruptedException {
        ServerBootstrap b = (ServerBootstrap) bootstrap;
        return b.bind(port).sync().channel();
    }
}
