package com.maldosia.cloudshuttle.core;

import io.netty.channel.ChannelHandler;

/**
 * @author Maldosia
 * @since 2025/6/23
 */
public class DefaultConnectionFactory extends AbstractConnectionFactory{

    public DefaultConnectionFactory(Codec codec, ChannelHandler handler) {
        super(codec, handler);
    }
}