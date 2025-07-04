package com.maldosia.cloudshuttle.core;

import io.netty.channel.ChannelHandler;

public interface Protocol {
    
    void registerFrame(Frame frame);

    ChannelHandler getEncoder();

    ChannelHandler getDecoder();
    
}
