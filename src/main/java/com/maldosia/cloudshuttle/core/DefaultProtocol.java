package com.maldosia.cloudshuttle.core;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author Maldosia
 * @since 2025/6/26
 */
public abstract class DefaultProtocol implements Protocol {

    @Override
    public ChannelHandler getEncoder() {
        return new StringEncoder();
    }

    @Override
    public ChannelHandler getDecoder() {
        return new StringDecoder();
    }
}