package com.maldosia.cloudshuttle.core.protocol;

import com.maldosia.cloudshuttle.core.message.Message;
import com.maldosia.cloudshuttle.core.message.MessageFactory;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 协议运行时实例，负责消息注册、查找、编解码等
 */
public class Protocol {
    private final ProtocolDefinition definition;
    // functionCode(byte[]) -> 消息工厂
    private final Map<ByteArrayWrapper, MessageFactory> messageFactories = new HashMap<>();
    // functionCode(byte[]) -> 消息类型
    private final Map<ByteArrayWrapper, Class<? extends Message>> messageTypes = new HashMap<>();

    /**
     * 构造函数
     * @param definition 协议定义
     */
    public Protocol(ProtocolDefinition definition) {
        this.definition = definition;
    }

    /**
     * 获取协议定义
     * @return 协议定义
     */
    public ProtocolDefinition getDefinition() {
        return definition;
    }

    /**
     * 注册消息类型
     */
    public void registerMessage(byte[] functionCode, Class<? extends Message> clazz, MessageFactory factory) {
        ByteArrayWrapper key = new ByteArrayWrapper(functionCode);
        messageFactories.put(key, factory);
        messageTypes.put(key, clazz);
    }

    /**
     * 根据功能码获取消息工厂
     */
    public MessageFactory getFactory(byte[] functionCode) {
        return messageFactories.get(new ByteArrayWrapper(functionCode));
    }

    /**
     * 根据功能码获取消息类型
     */
    public Class<? extends Message> getMessageClass(byte[] functionCode) {
        return messageTypes.get(new ByteArrayWrapper(functionCode));
    }

    /**
     * 通过消息类型反查功能码
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
     * byte[] 作为Map key的包装类，重写equals和hashCode
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
