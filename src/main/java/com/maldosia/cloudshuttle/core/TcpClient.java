package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.options.NetworkOptions;
import com.maldosia.cloudshuttle.core.util.NettyUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

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

    private final Url url;

    private Channel channel;

    private final ConnectionHandler connectionHandler = new ConnectionHandler(this);

    public TcpClient(Url url, Protocol protocol) {
        this.url = url;
        
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(workerGroup).channel(NettyUtil.getClientSocketChannelClass());
                // TODO set netty option
                //.option(ChannelOption.TCP_NODELAY, ConfigManager.tcp_nodelay())
                //.option(ChannelOption.SO_REUSEADDR, ConfigManager.tcp_so_reuseaddr())
                //.option(ChannelOption.SO_KEEPALIVE, ConfigManager.tcp_so_keepalive())
                //.option(ChannelOption.SO_SNDBUF, ConfigManager.tcp_so_sndbuf())
                //.option(ChannelOption.SO_RCVBUF, ConfigManager.tcp_so_rcvbuf());

        this.bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, option(NetworkOptions.CONNECTION_TIMEOUT));

        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();

                pipeline.addLast("decoder", protocol.getDecoder());
                pipeline.addLast("encoder", protocol.getEncoder());
                pipeline.addLast("connectionHandler", connectionHandler);
//                pipeline.addLast("handler", handler);
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
    public ChannelFuture connect() {
//        log.info("Connecting to {}", url.getRemoteUrl());
        ChannelFuture future = this.bootstrap.connect(new InetSocketAddress(url.getRemoteIp(), url.getRemotePort()));

        future.awaitUninterruptibly();
//        if (!future.isDone()) {
//            String errMsg = "Create connection to " + url.getRemoteUrl() + " timeout!";
//            throw new ConnectionException(errMsg);
//        }
//        if (future.isCancelled()) {
//            String errMsg = "Create connection to " + url.getRemoteUrl() + " cancelled by user!";
//            throw new ConnectionException(errMsg);
//        }
        if (!future.isSuccess()) {
//            String errMsg = "Create connection to " + url.getRemoteUrl() + " error!";
//            throw new ConnectionException(errMsg, future.cause());
            log.info("Connection to {} failed", url.getRemoteUrl());
            if (this.option(NetworkOptions.RECONNECT_SWITCH)) {
                Integer reconnectIntervals = this.option(NetworkOptions.RECONNECT_INTERVALS);
                future.channel().eventLoop().schedule(this::connect, reconnectIntervals, TimeUnit.SECONDS);
            }
        } else {
            log.info("Connection to {} successful", url.getRemoteUrl());
        }

        this.channel = future.channel();
        return future;
    }


    @Override
    public void disconnect() {
        try {
            if (this.channel != null) {
                this.channel.close().addListener((ChannelFutureListener) future -> log.info("Disconnect from address={}, result={}", getUrl().getRemoteUrl(), future.isSuccess(), future.cause()));
            }
        } catch (Exception e) {
            log.error("Exception caught when closing connection {}", url.getRemoteUrl(), e);
        }
    }

    @Override
    public void sendSync(Object message) {

    }

    @Override
    public void sendSync(Object message, int timeout) {

    }

    @Override
    public void sendAsync(Object message) {

    }

    public Url getUrl() {
        return url;
    }
}