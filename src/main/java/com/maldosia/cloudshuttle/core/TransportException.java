package com.maldosia.cloudshuttle.core;

/**
 * 网络传输相关异常。
 */
public class TransportException extends RuntimeException {
    public TransportException(String message) {
        super(message);
    }
    public TransportException(String message, Throwable cause) {
        super(message, cause);
    }
} 