package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.codec.ProtocolDecoder;
import com.maldosia.cloudshuttle.core.codec.ProtocolEncoder;
import com.maldosia.cloudshuttle.core.protocol.Protocol;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * TCP端点抽象类 - TCP服务端和客户端的基类
 */
public abstract class AbstractTcpEndpoint {
    protected final String host;     // 主机地址
    protected final int port;        // 端口号
    protected final Protocol protocol; // 协议处理器
    public Channel channel;       // Netty通道
    protected EventLoopGroup group;  // Netty事件循环组
    protected final List<ChannelHandler> customHandlers = new ArrayList<>(); // 自定义处理器

    /**
     * 构造函数
     * @param host 主机地址
     * @param port 端口号
     * @param protocol 协议处理器
     */
    public AbstractTcpEndpoint(String host, int port, Protocol protocol) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
    }

    /**
     * 添加自定义处理器
     * @param handler 通道处理器
     */
    public void addHandler(ChannelHandler handler) {
        customHandlers.add(handler);
    }

    /**
     * 启动端点
     */
    public void startup() {
        group = new NioEventLoopGroup();
        try {
            AbstractBootstrap<?, ?> bootstrap = createBootstrap();
            bootstrap.group(group);

            //配置公共选项
            configureCommonOptions(bootstrap);

            //配置特定类型的处理器
            configureHandler(bootstrap);

            channel = doStart(bootstrap);
            System.out.println(getClass().getSimpleName() + " started successfully");
        } catch (Exception e) {
            shutdown();
            throw new RuntimeException("启动失败", e);
        }
    }

    /**
     * 配置处理器 - 由子类实现
     *
     * @param bootstrap 启动器
     */
    protected abstract void configureHandler(AbstractBootstrap<?, ?> bootstrap);

    /**
     * 关闭端点
     */
    public void shutdown() {
        if (channel != null) {
            channel.close();
            channel = null;
        }
        if (group != null) {
            group.shutdownGracefully();
            group = null;
        }
        System.out.println(getClass().getSimpleName() + " stopped");
    }

    /**
     * 创建通道初始化器
     * @return 通道初始化器
     */
    protected ChannelInitializer<Channel> createChannelInitializer() {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                configurePipeline(channel.pipeline());
            }
        };
    }

    /**
     * 配置通道管道
     * @param pipeline 通道管道
     */
    protected void configurePipeline(ChannelPipeline pipeline) {
        // 添加协议解码器
        pipeline.addLast("decoder", new ProtocolDecoder(protocol));
        // 添加协议编码器
        pipeline.addLast("encoder", new ProtocolEncoder(protocol));

        // 添加自定义处理器
        for (ChannelHandler handler : customHandlers) {
            pipeline.addLast(handler);
        }
    }

    // 抽象方法 - 由子类实现
    protected abstract AbstractBootstrap<?, ?> createBootstrap();
    protected abstract void configureCommonOptions(AbstractBootstrap<?, ?> bootstrap);
    protected abstract Channel doStart(AbstractBootstrap<?, ?> bootstrap) throws Exception;
}
