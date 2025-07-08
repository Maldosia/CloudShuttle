package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.codec.ProtocolDecoder;
import com.maldosia.cloudshuttle.core.codec.ProtocolEncoder;
import com.maldosia.cloudshuttle.core.message.MessageFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 协议运行时实例，负责协议注册、消息分发与编解码。
 * <p>
 * 支持标准协议（自动内置编解码器）和自定义协议（需自定义编解码器）。
 * 推荐通过 {@link ProtocolDslBuilder} 和 {@link ProtocolDefinition} 创建协议结构。
 * </p>
 */
public class Protocol {
    private final ProtocolDefinition definition;
    // functionCode(byte[]) -> 消息工厂
    private final Map<ByteArrayWrapper, MessageFactory> messageFactories = new HashMap<>();
    // functionCode(byte[]) -> 消息类型
    private final Map<ByteArrayWrapper, Class<? extends Message>> messageTypes = new HashMap<>();

    // 编解码器
    private final ProtocolEncoder encoder;
    private final ProtocolDecoder decoder;

    /**
     * 标准协议构造函数，自动使用内置Encoder/Decoder。
     * @param definition 协议结构定义
     */
    public Protocol(ProtocolDefinition definition) {
        this(definition,
            definition.isStandard() ? new com.maldosia.cloudshuttle.core.codec.ProtocolEncoder(null) : null,
            definition.isStandard() ? new com.maldosia.cloudshuttle.core.codec.ProtocolDecoder(null) : null
        );
    }

    /**
     * 自定义协议构造函数，必须传入自定义Encoder/Decoder。
     * @param definition 协议结构定义
     * @param encoder 协议编码器
     * @param decoder 协议解码器
     */
    public Protocol(ProtocolDefinition definition, ProtocolEncoder encoder, ProtocolDecoder decoder) {
        this.definition = definition;
        if (!definition.isStandard()) {
            if (encoder == null || decoder == null) {
                throw new IllegalArgumentException("自定义协议必须传入自定义Encoder和Decoder");
            }
        }
        this.encoder = encoder;
        this.decoder = decoder;
    }

    /**
     * 获取协议结构定义。
     * @return 协议定义对象
     */
    public ProtocolDefinition getDefinition() {
        return definition;
    }

    /**
     * 获取协议消息编码器（Netty集成用）。
     * @return 编码器
     */
    public ProtocolEncoder getEncoder() { return encoder; }
    /**
     * 获取协议消息解码器（Netty集成用）。
     * @return 解码器
     */
    public ProtocolDecoder getDecoder() { return decoder; }

    /**
     * 注册消息类型与工厂。
     * @param functionCode 功能码
     * @param clazz 消息类
     * @param factory 消息工厂
     */
    public void registerMessage(byte[] functionCode, Class<? extends Message> clazz, MessageFactory factory) {
        ByteArrayWrapper key = new ByteArrayWrapper(functionCode);
        messageFactories.put(key, factory);
        messageTypes.put(key, clazz);
    }

    /**
     * 根据功能码获取消息工厂。
     * @param functionCode 功能码
     * @return 消息工厂
     */
    public MessageFactory getFactory(byte[] functionCode) {
        return messageFactories.get(new ByteArrayWrapper(functionCode));
    }

    /**
     * 根据功能码获取消息类型。
     * @param functionCode 功能码
     * @return 消息类型Class
     */
    public Class<? extends Message> getMessageClass(byte[] functionCode) {
        return messageTypes.get(new ByteArrayWrapper(functionCode));
    }

    /**
     * 通过消息类型反查功能码。
     * @param clazz 消息类型Class
     * @return 功能码字节数组
     */
    public byte[] getFunctionCodeByMessageClass(Class<? extends Message> clazz) {
        for (Map.Entry<ByteArrayWrapper, Class<? extends Message>> entry : messageTypes.entrySet()) {
            if (entry.getValue().equals(clazz)) {
                return entry.getKey().data;
            }
        }
        return null;
    }

    /**
     * byte[] 作为Map key的包装类，重写equals和hashCode。
     */
    private static class ByteArrayWrapper {
        private final byte[] data;
        ByteArrayWrapper(byte[] data) { this.data = data; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ByteArrayWrapper that = (ByteArrayWrapper) o;
            return Arrays.equals(data, that.data);
        }
        @Override
        public int hashCode() {
            return Arrays.hashCode(data);
        }
    }
}
