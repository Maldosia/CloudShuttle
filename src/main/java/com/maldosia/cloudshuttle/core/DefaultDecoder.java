package com.maldosia.cloudshuttle.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author Maldosia
 * @since 2025/6/26
 */
public class DefaultDecoder extends ByteToMessageDecoder {

    // 协议固定起始位
    private static final byte[] START_SIGN = {
            (byte) 0xAA, (byte) 0x55, (byte) 0x99, (byte) 0x66
    };
    
    // 协议头固定长度（不包括变长报文体）
    private static final int FIXED_HEADER_LENGTH = 32; // 4*6 + 12 = 32字节
    
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        // 确保有足够数据读取起始位
        if (in.readableBytes() < 4) {
            return;
        }

        in.markReaderIndex(); // 标记当前读取位置

        // 1. 查找起始位
        if (!findStartFlag(in)) {
            return;
        }

        // 2. 检查完整协议头（32字节）
        if (in.readableBytes() < FIXED_HEADER_LENGTH - 4) { // 已读4字节起始位
            in.resetReaderIndex();
            return;
        }

        // 3. 读取协议头其他字段
        int checksum = in.readInt();      // 校验位（4字节）
        int version = in.readInt();       // 版本（4字节）
        int functionCode = in.readInt();  // 功能码（4字节）
        int totalLength = in.readInt();   // 总长度（4字节）
        in.skipBytes(12);                 // 跳过预留位（12字节）

        // 4. 验证长度有效性
        if (totalLength < FIXED_HEADER_LENGTH) {
            in.resetReaderIndex();
            throw new IllegalArgumentException("Invalid total length: " + totalLength);
        }

        // 5. 检查报文完整性
        int bodyLength = totalLength - FIXED_HEADER_LENGTH;
        if (in.readableBytes() < bodyLength) {
            in.resetReaderIndex(); // 等待更多数据
            return;
        }

        // 6. 提取报文体
        ByteBuf body = in.readRetainedSlice(bodyLength);

        // 7. 构造协议对象（自定义类需实现）
        CustomProtocolMessage message = new CustomProtocolMessage(
                checksum, version, functionCode, totalLength, body
        );

        out.add(message);
    }

    // 查找起始位并移动读指针
    private boolean findStartFlag(ByteBuf in) {
        while (in.readableBytes() >= 4) {
            int startIndex = in.readerIndex();

            // 检查起始位
            boolean match = true;
            for (int i = 0; i < 4; i++) {
                if (in.getByte(startIndex + i) != START_SIGN[i]) {
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