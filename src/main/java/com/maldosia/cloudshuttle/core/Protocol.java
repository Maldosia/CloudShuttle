package com.maldosia.cloudshuttle.core;

import io.netty.channel.ChannelHandler;

public interface Protocol {

    void registerCommands(FunctionCode functionCode, Command command);

    ChannelHandler getEncoder();

    ChannelHandler getDecoder();
    
}
