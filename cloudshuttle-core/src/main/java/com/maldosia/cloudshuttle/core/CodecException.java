package com.maldosia.cloudshuttle.core;

/**
 * 编解码相关异常。
 */
public class CodecException extends RuntimeException {
    public CodecException(String message) {
        super(message);
    }
    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }
} 