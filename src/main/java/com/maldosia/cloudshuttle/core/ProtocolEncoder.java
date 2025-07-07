package com.maldosia.cloudshuttle.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ProtocolException;
import java.util.HashMap;
import java.util.Map;

/**
 * 协议编码器 - 将消息对象编码为字节流
 * 修复了长度字段计算问题
 */
public class ProtocolEncoder extends MessageToByteEncoder<Message> {
    private static final Logger log = LoggerFactory.getLogger(ProtocolEncoder.class);
    private final Protocol protocol; // 协议处理器

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

        // 关键修复：计算报文体长度
        int bodyLength = calculateBodyLength(ctx, msg);

        // 记录长度字段位置（如果需要回填）
        Map<FieldDefinition, Integer> lengthFieldPositions = new HashMap<>();

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

                case LENGTH:
                    // 处理长度字段：记录位置并写入占位符
                    lengthFieldPositions.put(field, out.writerIndex());
                    out.writeBytes(new byte[field.getLength()]); // 占位符
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

        // 关键修复：回填长度字段
        for (Map.Entry<FieldDefinition, Integer> entry : lengthFieldPositions.entrySet()) {
            FieldDefinition lengthField = entry.getKey();
            int position = entry.getValue();

            // 保存当前位置
            int currentWriterIndex = out.writerIndex();

            // 移动到长度字段位置
            out.writerIndex(position);

            // 获取并写入实际长度
            byte[] lengthBytes = Bytes.fromInt(bodyLength, lengthField.getLength());
            if (lengthBytes.length != lengthField.getLength()) {
                throw new ProtocolException("长度字段转换错误: 预期长度 " +
                        lengthField.getLength() + ", 实际长度 " + lengthBytes.length);
            }
            out.writeBytes(lengthBytes);

            // 恢复写指针位置
            out.writerIndex(currentWriterIndex);
        }

        // 写入结束标志
        for (FieldDefinition field : definition.getFields()) {
            if (field.getType() == FieldType.END_FLAG) {
                out.writeBytes(((FixedField) field).getFixedBytes());
                break;
            }
        }

        log.info(ByteBufUtil.hexDump(out));
    }

    /**
     * 计算报文体长度
     * 注意：这里不能直接序列化到输出缓冲区，因为需要先知道长度
     */
    private int calculateBodyLength(ChannelHandlerContext ctx, Message msg) {
        // 创建临时缓冲区计算长度
        ByteBuf buffer = ctx.alloc().buffer();
        try {
            msg.serialize(buffer);
            return buffer.readableBytes();
        } finally {
            buffer.release();
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
