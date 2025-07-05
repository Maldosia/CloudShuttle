package com.maldosia.cloudshuttle.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 协议处理器 - 管理协议定义和消息注册
 */
public class Protocol {
    private final ProtocolDefinition definition; // 协议定义
    private final Map<String, MessageFactory> messageFactories = new HashMap<>(); // 消息工厂映射
    private final Map<Class<?>, byte[]> functionCodeMap = new HashMap<>(); // 功能码映射

    /**
     * 构造函数
     * @param definition 协议定义
     */
    public Protocol(ProtocolDefinition definition) {
        this.definition = definition;
    }

    /**
     * 注册消息类型
     * @param messageClass 消息类
     * @param factory 消息工厂
     */
    public void registerMessage(Class<? extends Message> messageClass, MessageFactory factory) {
        if (!FunctionCode.class.isAssignableFrom(messageClass)) {
            throw new IllegalArgumentException("消息类必须实现FunctionCode接口: " + messageClass.getName());
        }

        try {
            // 通过反射获取功能码
            FunctionCode instance = (FunctionCode) messageClass.getDeclaredConstructor().newInstance();
            byte[] code = instance.getCode();
            String key = bytesToKey(code);
            messageFactories.put(key, factory);
            functionCodeMap.put(messageClass, code);
        } catch (Exception e) {
            throw new RuntimeException("注册消息失败: " + messageClass.getName(), e);
        }
    }

    /**
     * 创建消息实例
     * @param functionCode 功能码
     * @return 消息实例
     */
    public Message createMessage(byte[] functionCode) {
        String key = bytesToKey(functionCode);
        MessageFactory factory = messageFactories.get(key);
        if (factory == null) return null;
        return factory.create();
    }

    /**
     * 获取消息类的功能码
     * @param clazz 消息类
     * @return 功能码字节数组
     */
    public byte[] getFunctionCode(Class<?> clazz) {
        return functionCodeMap.get(clazz);
    }

    /**
     * 获取协议定义
     * @return 协议定义
     */
    public ProtocolDefinition getDefinition() {
        return definition;
    }

    /**
     * 将字节数组转换为字符串键
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private String bytesToKey(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
