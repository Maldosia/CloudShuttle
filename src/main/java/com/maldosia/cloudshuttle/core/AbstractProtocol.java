package com.maldosia.cloudshuttle.core;

import io.netty.channel.ChannelHandler;

public abstract class AbstractProtocol implements Protocol {

    @Override
    public void registerFrame(Frame frame) {
        FrameFactory.registerFrame(frame);
    }

    @Override
    public ChannelHandler getEncoder() {
        return new DefaultEncoder();
    }

    @Override
    public ChannelHandler getDecoder() {
        return new DefaultDecoder();
    }
}
