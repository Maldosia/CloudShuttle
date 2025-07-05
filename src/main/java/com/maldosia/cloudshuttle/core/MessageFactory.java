package com.maldosia.cloudshuttle.core;

/**
 * 消息工厂接口 - 创建消息实例
 */
public interface MessageFactory {
    /**
     * 创建消息实例
     * @return 消息对象
     */
    Message create();
}
