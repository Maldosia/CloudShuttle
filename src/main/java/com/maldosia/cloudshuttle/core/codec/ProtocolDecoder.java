package com.maldosia.cloudshuttle.core.codec;

import com.maldosia.cloudshuttle.core.Bytes;
import com.maldosia.cloudshuttle.core.ProtocolException;
import com.maldosia.cloudshuttle.core.field.FieldDefinition;
import com.maldosia.cloudshuttle.core.field.FieldType;
import com.maldosia.cloudshuttle.core.message.FrameHeader;
import com.maldosia.cloudshuttle.core.message.Message;
import com.maldosia.cloudshuttle.core.message.MessageFactory;
import com.maldosia.cloudshuttle.core.protocol.Protocol;
import com.maldosia.cloudshuttle.core.protocol.ProtocolDefinition;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 协议解码器 - 将字节流解码为消息对象
 */
public class ProtocolDecoder extends ByteToMessageDecoder {
    private static final Logger log = LoggerFactory.getLogger(ProtocolDecoder.class);
    private final Protocol protocol; // 协议处理器
    private final int minFrameLength;      // 最小帧长度

    /**
     * 构造函数
     * @param protocol 协议处理器
     */
    public ProtocolDecoder(Protocol protocol) {
        this.protocol = protocol;
        this.minFrameLength = calculateMinFrameLength();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ProtocolDefinition definition = protocol.getDefinition();
        // 检查是否有足够数据读取
        if (in.readableBytes() < minFrameLength) {
            return;
        }

        log.info(ByteBufUtil.hexDump(in));

        // 标记当前读取位置
        in.markReaderIndex();

        FrameHeader header = new FrameHeader(); // 创建帧头容器
        int bodyLength = 0;                    // 报文体长度
        byte[] functionCode = null;             // 功能码
        boolean bodyFound = false;              // 是否找到报文体

        // 遍历所有字段进行解析
        for (FieldDefinition field : definition.getFields()) {
            // 检查是否有足够数据读取当前字段
            if (in.readableBytes() < field.getLength()) {
                in.resetReaderIndex();
                return;
            }

            // 根据字段类型进行不同处理
            switch (field.getType()) {
                case START_FLAG:
                    // 验证起始标志
                    byte[] startBytes = field.getFixedBytes();
                    for (byte b : startBytes) {
                        if (in.readByte() != b) {
                            handleInvalidStart(ctx, in);
                            return;
                        }
                    }
                    break;

                case END_FLAG:
                    // 结束标志在报文体后处理
                    break;

                case FUNCTION_CODE:
                    // 读取功能码
                    byte[] code = new byte[field.getLength()];
                    in.readBytes(code);
                    functionCode = code;
                    header.addField(field.getName(), code);
                    break;

                case LENGTH:
                    // 读取长度字段
                    byte[] lengthData = new byte[field.getLength()];
                    in.readBytes(lengthData);
                    bodyLength = Bytes.toInt(lengthData);
                    header.addField(field.getName(), lengthData);
                    break;

                case BODY:
                    // 标记找到报文体
                    bodyFound = true;
                    break;

                default:
                    // 读取普通字段
                    byte[] fieldData = new byte[field.getLength()];
                    in.readBytes(fieldData);
                    header.addField(field.getName(), fieldData);
                    break;
            }
        }

        // 处理报文体
        if (bodyFound) {
            // 检查报文体数据是否完整
            if (in.readableBytes() < bodyLength) {
                in.resetReaderIndex();
                return;
            }

            // 读取报文体
            ByteBuf bodyBuf = in.readRetainedSlice(bodyLength);

            // 处理结束标志
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

            // 检查功能码
            if (functionCode == null) {
                throw new ProtocolException("未找到功能码");
            }
            // 通过功能码查找消息工厂
            MessageFactory factory = protocol.getFactory(functionCode);
            if (factory == null) {
                throw new ProtocolException("未注册的功能码: " + java.util.Arrays.toString(functionCode));
            }
            Message message = factory.create();
            // 设置帧头并解析报文体
            message.setFrameHeader(header);
            message.deserialize(bodyBuf);
            bodyBuf.release();
            out.add(message);
        }
    }

    /**
     * 计算最小帧长度（不包括报文体）
     * @return 最小帧长度
     */
    private int calculateMinFrameLength() {
        int length = 0;
        for (FieldDefinition field : protocol.getDefinition().getFields()) {
            if (field.getType() != FieldType.BODY) {
                length += field.getLength();
            }
        }
        return length;
    }

    /**
     * 处理无效的起始标志
     * @param ctx 通道上下文
     * @param in 输入缓冲区
     */
    private void handleInvalidStart(ChannelHandlerContext ctx, ByteBuf in) {
        in.resetReaderIndex();
        // 尝试寻找下一个起始标志
        byte firstStartByte = protocol.getDefinition().getFields().get(0).getFixedBytes()[0];
        while (in.readableBytes() > 0) {
            byte b = in.readByte();
            if (b == firstStartByte) {
                in.readerIndex(in.readerIndex() - 1);
                return;
            }
        }
    }

    /**
     * 将字节数组转换为十六进制字符串
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
