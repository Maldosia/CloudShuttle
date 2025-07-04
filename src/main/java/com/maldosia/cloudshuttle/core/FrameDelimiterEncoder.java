package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.protocol.TcpProtocolDefinition;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.HashMap;
import java.util.Map;

public class FrameDelimiterEncoder extends MessageToByteEncoder<ByteBuf> {

    private final TcpProtocolDefinition tcpProtocolDefinition;
    private final Map<String, byte[]> delimiterMap = new HashMap<>();

    public FrameDelimiterEncoder(TcpProtocolDefinition tcpProtocolDefinition) {
        this.tcpProtocolDefinition = tcpProtocolDefinition;
        // 预定义分隔符
        delimiterMap.put("START_DELIMITER", new byte[]{(byte)0xAA, (byte)0x55, (byte)0x99, (byte)0x66});
        delimiterMap.put("END_DELIMITER", new byte[]{(byte)0x66, (byte)0x99, (byte)0x55, (byte)0xAA});
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {

    }
}
