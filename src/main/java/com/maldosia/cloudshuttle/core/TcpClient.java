package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.exception.ConnectionException;
import com.maldosia.cloudshuttle.core.exception.LifeCycleException;
import com.maldosia.cloudshuttle.core.handler.ConnectionHandler;
import com.maldosia.cloudshuttle.core.options.NetworkOptions;
import com.maldosia.cloudshuttle.core.util.NettyUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * TCP 客户端
 * 
 * @author Maldosia
 * @since 2025/6/22
 */
public class TcpClient extends AbstractClient {

    private static final Logger log = LoggerFactory.getLogger(TcpClient.class);

    private static final EventLoopGroup workerGroup = NettyUtil.newEventLoopGroup(Runtime.getRuntime().availableProcessors() + 1);

    private final Bootstrap bootstrap;

    private Url url;

    private Channel channel;

    public TcpClient(Codec codec, ChannelHandler handler) {
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(workerGroup).channel(NettyUtil.getClientSocketChannelClass());
                // TODO set netty option
                //.option(ChannelOption.TCP_NODELAY, ConfigManager.tcp_nodelay())
                //.option(ChannelOption.SO_REUSEADDR, ConfigManager.tcp_so_reuseaddr())
                //.option(ChannelOption.SO_KEEPALIVE, ConfigManager.tcp_so_keepalive())
                //.option(ChannelOption.SO_SNDBUF, ConfigManager.tcp_so_sndbuf())
                //.option(ChannelOption.SO_RCVBUF, ConfigManager.tcp_so_rcvbuf());

        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast("decoder", codec.newDecoder());
                pipeline.addLast("encoder", codec.newEncoder());
                pipeline.addLast("connectionHandler", new ConnectionHandler());
                pipeline.addLast("handler", handler);
            }
        });
    }

    @Override
    public void startup(){
        super.startup();

        // TODO 可以自定义处理逻辑
        // user processor
        // select strategy
        // execute task
        // enable reconnect

    }

    @Override
    public void connect(Url url) {
        Integer connectionTimeout = option(NetworkOptions.CONNECTION_TIMEOUT);

        this.connect(url, connectionTimeout);
    }

    @Override
    public void connect(Url url, int connectTimeout) {
        this.url = url;
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(url.getRemoteIp(), url.getRemotePort()));

        future.awaitUninterruptibly();
        if (!future.isDone()) {
            String errMsg = "Create connection to " + url.getRemoteUrl() + " timeout!";
            throw new ConnectionException(errMsg);
        }
        if (future.isCancelled()) {
            String errMsg = "Create connection to " + url.getRemoteUrl() + " cancelled by user!";
            throw new ConnectionException(errMsg);
        }
        if (!future.isSuccess()) {
            String errMsg = "Create connection to " + url.getRemoteUrl() + " error!";
            throw new ConnectionException(errMsg, future.cause());
        }

        Channel channel = future.channel();
        if (channel.isActive()) {
            log.info("channel.isActive()");
            this.channel = channel;
        } else {
            throw new ConnectionException("create connection, but channel is inactive, url is " + url.getRemoteUrl());
        }
    }


    @Override
    public void disconnect() {
        try {
            if (this.channel != null) {
                this.channel.close().addListener((ChannelFutureListener) future -> log.info("Close the connection to remote address={}, result={}", url.getRemoteUrl(), future.isSuccess(), future.cause()));
            }
        } catch (Exception e) {
            log.error("Exception caught when closing connection {}", url.getRemoteUrl(), e);
        }
    }

    @Override
    public void reconnect(Url url) {
        this.disconnect();
        this.connect(url);
    }

    @Override
    public void sendSync(Object message) {

    }

    @Override
    public void sendAsync(Object message) {

    }
}