package com.maldosia.cloudshuttle.core.codec;

import com.maldosia.cloudshuttle.core.*;
import com.maldosia.cloudshuttle.core.field.FieldDefinition;
import com.maldosia.cloudshuttle.core.message.MessageFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 协议解码器 - 将字节流解码为消息对象
 * 支持标准协议和自定义协议（自定义时protocol可为null，需用户实现自定义Decoder）
 */
public class ProtocolDecoder extends ByteToMessageDecoder {
    private static final Logger log = LoggerFactory.getLogger(ProtocolDecoder.class);
    private final Protocol protocol; // 协议处理器
    private final int minFrameLength;

    /**
     * 构造函数
     * @param protocol 协议实例，标准协议下必填，自定义协议可为null
     */
    public ProtocolDecoder(Protocol protocol) {
        this.protocol = protocol;
        this.minFrameLength = protocol != null ? calculateMinFrameLength() : 0;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (protocol == null) {
            throw new IllegalStateException("标准协议必须传入Protocol实例，自定义协议请实现自定义Decoder");
        }
        ProtocolDefinition definition = protocol.getDefinition();
        if (in.readableBytes() < minFrameLength) {
            return;
        }
        log.info(ByteBufUtil.hexDump(in));
        in.markReaderIndex();
        FrameHeader header = new FrameHeader();
        int bodyLength = 0;
        byte[] functionCode = null;
        boolean bodyFound = false;
        for (FieldDefinition field : definition.getFields()) {
            if (in.readableBytes() < field.getLength()) {
                in.resetReaderIndex();
                return;
            }
            switch (field.getType()) {
                case START_FLAG:
                    byte[] startBytes = field.getFixedBytes();
                    for (byte b : startBytes) {
                        if (in.readByte() != b) {
                            handleInvalidStart(ctx, in);
                            return;
                        }
                    }
                    break;
                case END_FLAG:
                    break;
                case FUNCTION_CODE:
                    byte[] code = new byte[field.getLength()];
                    in.readBytes(code);
                    functionCode = code;
                    header.addField(field.getName(), code);
                    break;
                case LENGTH:
                    byte[] lengthData = new byte[field.getLength()];
                    in.readBytes(lengthData);
                    bodyLength = Bytes.toInt(lengthData);
                    header.addField(field.getName(), lengthData);
                    break;
                case BODY:
                    bodyFound = true;
                    break;
                default:
                    byte[] fieldData = new byte[field.getLength()];
                    in.readBytes(fieldData);
                    header.addField(field.getName(), fieldData);
                    break;
            }
        }
        if (bodyFound) {
            if (in.readableBytes() < bodyLength) {
                in.resetReaderIndex();
                return;
            }
            ByteBuf bodyBuf = in.readRetainedSlice(bodyLength);
            for (FieldDefinition field : definition.getFields()) {
                if (field.getType() == FieldType.END_FLAG) {
                    byte[] endBytes = field.getFixedBytes();
                    for (byte b : endBytes) {
                        if (in.readByte() != b) {
                            bodyBuf.release();
                            throw new ProtocolException("无效的结束标志");
                        }
                    }
                    break;
                }
            }
            if (functionCode == null) {
                throw new ProtocolException("未找到功能码");
            }
            MessageFactory factory = protocol.getFactory(functionCode);
            if (factory == null) {
                throw new ProtocolException("未注册的功能码: " + java.util.Arrays.toString(functionCode));
            }
            Message message = factory.create();
            message.setFrameHeader(header);
            message.deserialize(bodyBuf);
            bodyBuf.release();
            out.add(message);
        }
    }
    private int calculateMinFrameLength() {
        int length = 0;
        for (FieldDefinition field : protocol.getDefinition().getFields()) {
            if (field.getType() != FieldType.BODY) {
                length += field.getLength();
            }
        }
        return length;
    }
    private void handleInvalidStart(ChannelHandlerContext ctx, ByteBuf in) {
        in.resetReaderIndex();
        byte firstStartByte = protocol.getDefinition().getFields().get(0).getFixedBytes()[0];
        while (in.readableBytes() > 0) {
            byte b = in.readByte();
            if (b == firstStartByte) {
                in.readerIndex(in.readerIndex() - 1);
                return;
            }
        }
    }
}
