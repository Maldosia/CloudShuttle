package com.maldosia.cloudshuttle.core;

import io.netty.buffer.ByteBuf;

/**
 * 用户自定义消息接口
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

    /**
     * 设置帧头字段（自动创建header）
     * @param name 字段名
     * @param value 字节值
     */
    default void setHeaderField(String name, byte[] value) {
        if (getFrameHeader() == null) setFrameHeader(new FrameHeader());
        getFrameHeader().addField(name, value);
    }

    /**
     * 获取帧头字段
     * @param name 字段名
     * @return 字节值
     */
    default byte[] getHeaderField(String name) {
        return getFrameHeader() != null ? getFrameHeader().getField(name) : null;
    }

    /**
     * 获取帧头字段并转换为指定类型
     * @param name 字段名
     * @param type 类型
     * @return 转换后的值
     */
    default <T> T getHeaderFieldAs(String name, Class<T> type) {
        return getFrameHeader() != null ? getFrameHeader().getFieldAs(name, type) : null;
    }
}
