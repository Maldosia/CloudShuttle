package com.maldosia.cloudshuttle.core;

import io.netty.channel.ChannelHandler;

public interface Protocol {
    
    ChannelHandler getEncoder();
    
    ChannelHandler getDecoder();
    
}
