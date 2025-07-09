package com.maldosia.cloudshuttle.core;

/**
 * 消息相关异常。
 */
public class MessageException extends RuntimeException {
    public MessageException(String message) {
        super(message);
    }
    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }
} 