package com.maldosia.cloudshuttle.core;

import io.netty.buffer.ByteBuf;

/**
 * 消息接口 - 所有自定义消息必须实现的接口
 */
public interface Message {
    /**
     * 设置帧头数据
     * @param header 帧头对象
     */
    void setFrameHeader(FrameHeader header);

    /**
     * 获取帧头数据
     * @return 帧头对象
     */
    FrameHeader getFrameHeader();

    /**
     * 反序列化方法 - 从ByteBuf中读取报文体数据
     * @param body 包含报文体数据的ByteBuf
     */
    void deserialize(ByteBuf body);

    /**
     * 序列化方法 - 将消息写入ByteBuf
     * @param buf 目标ByteBuf
     */
    void serialize(ByteBuf buf);
}
