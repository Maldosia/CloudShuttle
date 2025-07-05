package com.maldosia.cloudshuttle.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.net.ProtocolException;

/**
 * 协议编码器 - 将消息对象编码为字节流
 */
public class ProtocolEncoder extends MessageToByteEncoder<Message> {
    private final Protocol protocol; // 协议处理器

    /**
     * 构造函数
     * @param protocol 协议处理器
     */
    public ProtocolEncoder(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        ProtocolDefinition definition = protocol.getDefinition();
        // 获取消息的功能码
        byte[] functionCode = protocol.getFunctionCode(msg.getClass());
        if (functionCode == null) {
            throw new ProtocolException("未注册的消息类型: " + msg.getClass().getName());
        }

        // 获取或创建帧头
        FrameHeader header = msg.getFrameHeader();
        if (header == null) {
            header = new FrameHeader();
        }

        // 写入所有字段
        for (FieldDefinition field : definition.getFields()) {
            switch (field.getType()) {
                case START_FLAG:
                    // 写入起始标志
                    out.writeBytes(((FixedField) field).getFixedBytes());
                    break;

                case END_FLAG:
                    // 结束标志最后写入
                    break;

                case BODY:
                    // 报文体最后写入
                    break;

                case FUNCTION_CODE:
                    // 写入功能码
                    out.writeBytes(functionCode);
                    break;

                default:
                    String fieldName = field.getName();
                    byte[] fieldData;

                    // 尝试从帧头获取字段值
                    if (header.getField(fieldName) != null) {
                        fieldData = header.getField(fieldName);
                    } else {
                        // 使用默认值
                        fieldData = getDefaultFieldValue(field);
                    }

                    // 验证字段长度
                    if (fieldData.length != field.getLength()) {
                        throw new ProtocolException("字段长度不匹配: " + fieldName +
                                ", 预期: " + field.getLength() + ", 实际: " + fieldData.length);
                    }

                    // 写入字段数据
                    out.writeBytes(fieldData);
                    break;
            }
        }

        // 写入报文体
        ByteBuf bodyBuf = ctx.alloc().buffer();
        msg.serialize(bodyBuf);
        out.writeBytes(bodyBuf);
        bodyBuf.release();

        // 写入结束标志
        for (FieldDefinition field : definition.getFields()) {
            if (field.getType() == FieldType.END_FLAG) {
                out.writeBytes(((FixedField) field).getFixedBytes());
                break;
            }
        }
    }

    /**
     * 获取字段的默认值
     * @param field 字段定义
     * @return 默认值字节数组
     */
    private byte[] getDefaultFieldValue(FieldDefinition field) {
        if (field.getType() == FieldType.LENGTH) {
            // 长度字段默认值为0
            return Bytes.fromInt(0, field.getLength());
        }
        // 其他字段默认填充0
        return new byte[field.getLength()];
    }
}
