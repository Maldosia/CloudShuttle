package com.maldosia.cloudshuttle.core;

/**
 * 协议配置异常类 - 协议配置错误时抛出
 */
public class ProtocolConfigurationException extends RuntimeException {
    public ProtocolConfigurationException(String message) {
        super(message);
    }
}
