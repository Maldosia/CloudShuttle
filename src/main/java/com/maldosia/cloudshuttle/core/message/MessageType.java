package com.maldosia.cloudshuttle.core.message;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 消息类型注解，用于将消息类与功能码绑定
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageType {
    byte[] code();
} 