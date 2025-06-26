package com.maldosia.cloudshuttle.core;

import io.netty.channel.ChannelHandler;

public abstract class AbstractProtocol implements Protocol {

    @Override
    public void registerCommands(FunctionCode functionCode, Command command) {
        CommandFactory.registerCommands(functionCode, command);
    }

    @Override
    public ChannelHandler getEncoder() {
        return new DefaultEncoder();
    }
}
