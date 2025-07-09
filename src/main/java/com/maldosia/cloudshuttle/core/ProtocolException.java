package com.maldosia.cloudshuttle.core;

/**
 * 协议相关异常。
 */
public class ProtocolException extends RuntimeException {
    public ProtocolException(String message) {
        super(message);
    }
    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
}
