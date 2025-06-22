package com.maldosia.cloudshuttle.core.exception;

/**
 * @author Maldosia
 * @since 2025/6/23
 */
public class ConnectionException extends RuntimeException{
    public ConnectionException(String message) {
        super(message);
    }
    
    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}