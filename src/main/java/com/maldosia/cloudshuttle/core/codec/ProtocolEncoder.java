package com.maldosia.cloudshuttle.core.codec;

import com.maldosia.cloudshuttle.core.*;
import com.maldosia.cloudshuttle.core.field.FieldDefinition;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ProtocolException;

/**
 * 协议字节流编码器
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
        // 通过消息类型反查功能码（假设功能码为1字节，遍历 codeToClass）
        byte[] functionCode = protocol.getFunctionCodeByMessageClass(msg.getClass());
        if (functionCode == null) {
            throw new CodecException("未注册的消息类型: " + msg.getClass().getName());
        }

        // 获取或创建帧头
        FrameHeader header = msg.getFrameHeader();
        if (header == null) {
            header = new FrameHeader();
        }

        // 关键修复：计算报文体长度
        int bodyLength = calculateBodyLength(ctx, msg);

        FieldDefinition lengthField = null;
        int lengthFieldPosition = -1;
        // 写入所有字段（不包括 END_FLAG）
        for (FieldDefinition field : definition.getFields()) {
            if (field.getType() == FieldType.END_FLAG) continue;
            switch (field.getType()) {
                case START_FLAG:
                case END_FLAG:
                    out.writeBytes(field.getFixedBytes());
                    break;
                case LENGTH:
                    lengthField = field;
                    lengthFieldPosition = out.writerIndex();
                    out.writeBytes(new byte[field.getLength()]); // 占位
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
                        throw new CodecException("字段长度不匹配: " + fieldName +
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
        // 回填长度字段
        if (lengthField != null && lengthFieldPosition >= 0) {
            int currentWriterIndex = out.writerIndex();
            out.writerIndex(lengthFieldPosition);
            byte[] lengthBytes = Bytes.fromInt(bodyLength, lengthField.getLength());
            out.writeBytes(lengthBytes);
            out.writerIndex(currentWriterIndex);
        }
        // 最后写入 END_FLAG
        for (FieldDefinition field : definition.getFields()) {
            if (field.getType() == FieldType.END_FLAG) {
                out.writeBytes(field.getFixedBytes());
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