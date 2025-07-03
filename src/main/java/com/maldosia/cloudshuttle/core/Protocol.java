package com.maldosia.cloudshuttle.core;

import io.netty.channel.ChannelHandler;

public interface Protocol {
    
    void registerFrame(Message message);

    ChannelHandler getEncoder();

    ChannelHandler getDecoder();
    
}
