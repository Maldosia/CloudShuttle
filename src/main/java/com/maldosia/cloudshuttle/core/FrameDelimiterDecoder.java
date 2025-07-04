package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.protocol.ProtocolField;
import com.maldosia.cloudshuttle.core.protocol.*;
import com.maldosia.cloudshuttle.core.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Maldosia
 * @since 2025/6/26
 */
public class FrameDelimiterDecoder extends ByteToMessageDecoder {

    private final TcpProtocolDefinition protocolDefinition;

    public FrameDelimiterDecoder(TcpProtocolDefinition tcpProtocolDefinition) {
        this.protocolDefinition = tcpProtocolDefinition;
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

    private Frame decodeFrame(ByteBuf in) throws ProtocolException {
        List<byte[]> fieldValues = new ArrayList<byte[]>();
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
        byte[] functionCode = new byte[0];
        for (ProtocolField field : protocolDefinition.getAllFields()) {
            if (field.getType() == ProtocolFieldEnum.START_FLAG) {
                ProtocolStartFlagField startFlagField = (ProtocolStartFlagField) field;
                fieldValues.add(startFlagField.getStartFlag());
                continue;
            }

            byte[] value = new byte[field.getLength()];
            in.readBytes(value);
            fieldValues.add(value);

            if (field.getType() == ProtocolFieldEnum.LENGTH) {
                // 4. 验证长度有效性
                if (isValidLength(field, value, in)) {
                    in.resetReaderIndex();
                    throw new IllegalArgumentException("Invalid total length");
                }
            }

            if (field.getType() == ProtocolFieldEnum.FUNCTION_CODE) {
                functionCode = value;
            }
        }

        // 5.校验码
        // 6.结束标志

        return FrameFactory.createFrame(functionCode, fieldValues);
    }

    private boolean isValidLength(ProtocolField field, byte[] lengthBytes, ByteBuf in) {
        ProtocolLengthField protocolLengthField = (ProtocolLengthField) field;
        int length = ByteUtil.toIntLittleEndian(lengthBytes);
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
    private boolean findStartFlag(ByteBuf in, ProtocolStartFlagField startFlagField) {
        while (in.readableBytes() >= 4) {
            int startIndex = in.readerIndex();

            // 检查起始位
            boolean match = true;
            for (int i = 0; i < 4; i++) {
                if (in.getByte(startIndex + i) != startFlagField.getStartFlag()[i]) {
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