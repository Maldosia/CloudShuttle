package com.maldosia.cloudshuttle.core.message;

/**
 * 消息工厂接口，供Protocol等内部注册消息工厂使用
 */
public interface MessageFactory {
    Message create();
} 