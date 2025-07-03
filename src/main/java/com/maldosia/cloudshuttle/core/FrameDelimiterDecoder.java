package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.protocol.ProtocolField;
import com.maldosia.cloudshuttle.core.protocol.CommonProtocolDefinition;
import com.maldosia.cloudshuttle.core.protocol.ProtocolFieldEnum;
import com.maldosia.cloudshuttle.core.protocol.ProtocolLengthField;
import com.maldosia.cloudshuttle.core.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.net.ProtocolException;
import java.util.List;

/**
 * @author Maldosia
 * @since 2025/6/26
 */
public class FrameDelimiterDecoder extends ByteToMessageDecoder {

    private final CommonProtocolDefinition protocolDefinition;

    public FrameDelimiterDecoder(CommonProtocolDefinition commonProtocolDefinition) {
        this.protocolDefinition = commonProtocolDefinition;
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
        // 确保有足够数据读取起始位
        if (in.readableBytes() < protocolDefinition.getStartFlagFieldLength()) {
            return null;
        }
        
        // 标记当前位置
        in.markReaderIndex();
        
        // 1.查找起始位
        if (!findStartFlag(in, protocolDefinition.getStartFlagField())) {
            return null;
        }

        // 2. 确保能够读取到完整的协议
        if (in.readableBytes() < protocolDefinition.getAllFieldsLength() - protocolDefinition.getStartFlagFieldLength()) { // 已读起始位长度
            in.resetReaderIndex();
            return null;
        }

        // 3. 读取协议头其他字段
        for (ProtocolField field : protocolDefinition.getAllFields()) {
            if (field.getType() == ProtocolFieldEnum.START_FLAG) continue;

            byte[] value = new byte[field.getLength()];
            in.readBytes(value);
            field.setValue(value);
            
            if (field.getType() == ProtocolFieldEnum.LENGTH) {
                // 4. 验证长度有效性
                if (isValidLength(in, field)) {
                    in.resetReaderIndex();
                    throw new IllegalArgumentException("Invalid total length");
                }
            }
        }

        // 5.校验码
        // 6.结束标志
        
        return FrameFactory.createFrame(fields, delimiters, body);
    }
    
    private boolean isValidLength(ByteBuf in, ProtocolField field) {
        ProtocolLengthField protocolLengthField = (ProtocolLengthField) field;
        int length = ByteUtil.toIntLittleEndian(protocolLengthField.getValue());
        if (protocolLengthField.getLengthFieldEnum() == ProtocolLengthField.ProtocolLengthFieldEnum.PROTOCOL_LENGTH) {
            //长度域代表整个帧长度
            int minLength = this.protocolDefinition.getAllFieldsLength();
            return length >= minLength;
        } else if (protocolLengthField.getLengthFieldEnum() == ProtocolLengthField.ProtocolLengthFieldEnum.BODY_LENGTH) {
            //长度域代表帧体长度
            int readableLength = in.readableBytes();
            return length >= readableLength;
        }
        return true;
    }

    // 查找起始位并移动读指针
    private boolean findStartFlag(ByteBuf in, ProtocolField startFlagField) {
        while (in.readableBytes() >= 4) {
            int startIndex = in.readerIndex();

            // 检查起始位
            boolean match = true;
            for (int i = 0; i < 4; i++) {
                if (in.getByte(startIndex + i) != startFlagField.getValue()[i]) {
                    match = false;
                    break;
                }
            }

            if (match) {
                in.readerIndex(startIndex + 4); // 移动指针到起始位后
                return true;
            }
            in.skipBytes(1); // 未匹配，跳过1字节继续查找
        }
        return false;
    }

}