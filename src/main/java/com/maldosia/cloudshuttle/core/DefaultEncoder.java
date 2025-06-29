package com.maldosia.cloudshuttle.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class DefaultEncoder extends MessageToByteEncoder<Frame> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Frame o, ByteBuf byteBuf) throws Exception {

    }
}
