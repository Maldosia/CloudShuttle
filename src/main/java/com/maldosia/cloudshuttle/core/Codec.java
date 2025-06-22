package com.maldosia.cloudshuttle.core;

import io.netty.channel.ChannelHandler;

public interface Codec {
    
    ChannelHandler newEncoder();

    ChannelHandler newDecoder();
}
