package com.maldosia.cloudshuttle.core.message;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 消息类型注解，供自动注册和功能码绑定使用
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageType {
    byte[] code();
} 