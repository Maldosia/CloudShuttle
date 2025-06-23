package com.maldosia.cloudshuttle.core;

import io.netty.channel.ChannelFuture;

public interface CloudShuttleClient extends OptionContainer{

    ChannelFuture connect(Url url);

    ChannelFuture connect(Url url, int connectTimeout);

    void disconnect();

    void reconnect();

    /**
     * 同步发送，需要等待响应
     */
    void sendSync(Object message);
    void sendSync(Object message, int timeout);

    /**
     * 异步发送，不需要等待响应
     */
    void sendAsync(Object message);
    
}
