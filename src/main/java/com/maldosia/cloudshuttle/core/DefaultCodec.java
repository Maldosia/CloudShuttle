package com.maldosia.cloudshuttle.core;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author Maldosia
 * @since 2025/6/23
 */
public class DefaultCodec implements Codec{

    @Override
    public ChannelHandler newEncoder() {
        return new StringEncoder();
    }

    @Override
    public ChannelHandler newDecoder() {
        return new StringDecoder();
    }
}