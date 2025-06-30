package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.protocol.Field;
import com.maldosia.cloudshuttle.core.protocol.CommonProtocolDefinition;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.net.ProtocolException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Maldosia
 * @since 2025/6/26
 */
public class FrameDelimiterDecoder extends ByteToMessageDecoder {

    private final CommonProtocolDefinition commonProtocolDefinition;
    private final Map<String, byte[]> delimiterMap = new HashMap<>();

    public FrameDelimiterDecoder(CommonProtocolDefinition commonProtocolDefinition) {
        this.commonProtocolDefinition = commonProtocolDefinition;
        // 预定义分隔符
        delimiterMap.put("START_DELIMITER", new byte[]{(byte)0xAA, (byte)0x55, (byte)0x99, (byte)0x66});
        delimiterMap.put("END_DELIMITER", new byte[]{(byte)0x66, (byte)0x99, (byte)0x55, (byte)0xAA});
    }
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() > 0) {
            int startIdx = in.readerIndex();

            try {
                Object message = decodeFrame(in);
                if (message != null) {
                    out.add(message);
                } else {
                    break; // 数据不足，等待下次读取
                }
            } catch (ProtocolException e) {
                // 协议错误处理
                in.readerIndex(startIdx + 1); // 跳过当前字节继续解析
                ctx.fireExceptionCaught(e);
            }
        }
        
    }

    private Object decodeFrame(ByteBuf in) throws ProtocolException {
        int startIdx = in.readerIndex();
        Map<String, ByteBuf> fields = new HashMap<>();
        Map<String, byte[]> delimiters = new HashMap<>();
        ByteBuf body = null;

        // 1. 按顺序解码字段
        for (Field field : commonProtocolDefinition.getFields()) {
            // 检查足够数据
            if (in.readableBytes() < field.getLength()) {
                in.readerIndex(startIdx); // 重置读指针
                return null; // 数据不足
            }

            // 处理分隔符字段
            if (field.isDelimiter()) {
                byte[] expected = delimiterMap.get(field.getName());
                if (expected == null) {
                    throw new ProtocolException("Undefined delimiter: " + field.getName());
                }

                // 验证分隔符
                for (int i = 0; i < field.getLength(); i++) {
                    if (in.getByte(in.readerIndex() + i) != expected[i]) {
                        throw new ProtocolException("Invalid " + field.getName() + " delimiter");
                    }
                }

                // 存储分隔符值
                byte[] value = new byte[field.getLength()];
                in.readBytes(value);
                delimiters.put(field.getName(), value);
            }
            // 处理长度字段
            else if (field.getName().equals(commonProtocolDefinition.getLengthFieldName())) {
                ByteBuf fieldBuf = in.readSlice(field.getLength()).retain();
                fields.put(field.getName(), fieldBuf);

                // 读取总长度值
                int totalLength = fieldBuf.getInt(fieldBuf.readerIndex());

                // 验证长度有效性
                int minLength = commonProtocolDefinition.getFixedHeaderLength();
                if (totalLength < minLength) {
                    throw new ProtocolException("Invalid length: " + totalLength);
                }

                // 检查完整数据包
                if (in.readableBytes() < (totalLength - (in.readerIndex() - startIdx))) {
                    in.readerIndex(startIdx); // 重置等待
                    return null;
                }
            }
            // 处理报文体
            else if (field.getName().equals(commonProtocolDefinition.getBodyFieldName())) {
                // 获取长度字段值
                ByteBuf lengthField = fields.get(commonProtocolDefinition.getLengthFieldName());
                if (lengthField == null) {
                    throw new ProtocolException("Length field not found");
                }

                int totalLength = lengthField.getInt(lengthField.readerIndex());
                int bodyLength = totalLength - (in.readerIndex() - startIdx) - getDelimiterLength("END_DELIMITER");

                if (bodyLength < 0) {
                    throw new ProtocolException("Negative body length");
                }

                body = in.readRetainedSlice(bodyLength);
                fields.put(field.getName(), body);
            }
            // 处理普通字段
            else {
                fields.put(field.getName(), in.readSlice(field.getLength()).retain());
            }
        }

        return FrameFactory.createFrame(fields, delimiters, body);
    }

    private int getDelimiterLength(String name) {
        byte[] delimiter = delimiterMap.get(name);
        return delimiter != null ? delimiter.length : 0;
    }

}