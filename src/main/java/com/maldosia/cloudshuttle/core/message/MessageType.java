package com.maldosia.cloudshuttle.core.message;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MessageType {
    byte[] code();
} 