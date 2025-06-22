package com.maldosia.cloudshuttle.core;

import io.netty.channel.ChannelHandler;

/**
 * @author Maldosia
 * @since 2025/6/23
 */
public class CloudShuttleCodec implements Codec{

    @Override
    public ChannelHandler newEncoder() {
        return null;
    }

    @Override
    public ChannelHandler newDecoder() {
        return null;
    }
}