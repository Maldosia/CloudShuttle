package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.exception.ConnectionException;
import com.maldosia.cloudshuttle.core.handler.ConnectionHandler;
import com.maldosia.cloudshuttle.core.util.NettyUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author Maldosia
 * @since 2025/6/23
 */
public class DefaultConnectionFactory implements ConnectionFactory{

    private static final Logger log = LoggerFactory.getLogger(DefaultConnectionFactory.class);

    private final EventLoopGroup workerGroup = NettyUtil.newEventLoopGroup(Runtime.getRuntime().availableProcessors() + 1);

    private final Codec codec;
    private final ChannelHandler handler;
    
    private Bootstrap bootstrap;

    public DefaultConnectionFactory(Codec codec, ChannelHandler handler) {
        if (codec == null) {
            throw new IllegalArgumentException("Codec must not be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler must not be null");
        }
        
        this.codec = codec;
        this.handler = handler;
    }

    @Override
    public void init() {
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(workerGroup).channel(NettyUtil.getClientSocketChannelClass());
                //TODO set netty option
//                .option(ChannelOption.TCP_NODELAY, ConfigManager.tcp_nodelay())
//                .option(ChannelOption.SO_REUSEADDR, ConfigManager.tcp_so_reuseaddr())
//                .option(ChannelOption.SO_KEEPALIVE, ConfigManager.tcp_so_keepalive())
//                .option(ChannelOption.SO_SNDBUF, ConfigManager.tcp_so_sndbuf())
//                .option(ChannelOption.SO_RCVBUF, ConfigManager.tcp_so_rcvbuf());
        
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
    public Connection createConnection(Url url) throws Exception {
        return createConnection(url, 2);
    }

    @Override
    public Connection createConnection(Url url, int connectTimeout) throws Exception {
        Channel channel = doCreateConnection(url.getRemoteIp(), url.getRemotePort(), connectTimeout);
        Connection conn = new Connection(channel, url);
        if (channel.isActive()) {
            log.info("channel.isActive()");
        } else {
            throw new ConnectionException("create connection, but channel is inactive, url is " + url.getRemoteUrl());
        }
        return conn;
    }
    
    protected Channel doCreateConnection(String targetIP, int targetPort, int connectTimeout) throws Exception {
        String address = targetIP + ":" + targetPort;
        if (log.isDebugEnabled()) {
            log.debug("connectTimeout of address [{}] is [{}].", address, connectTimeout);
        }
        if (connectTimeout <= 0) {
            throw new IllegalArgumentException(String.format(
                    "illegal timeout for creating connection, address: %s, timeout: %d", address,
                    connectTimeout));
        }
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(targetIP, targetPort));

        future.awaitUninterruptibly();
        if (!future.isDone()) {
            String errMsg = "Create connection to " + address + " timeout!";
            log.warn(errMsg);
            throw new Exception(errMsg);
        }
        if (future.isCancelled()) {
            String errMsg = "Create connection to " + address + " cancelled by user!";
            log.warn(errMsg);
            throw new Exception(errMsg);
        }
        if (!future.isSuccess()) {
            String errMsg = "Create connection to " + address + " error!";
            log.warn(errMsg);
            throw new Exception(errMsg, future.cause());
        }
        return future.channel();
    }
}