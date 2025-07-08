package com.maldosia.cloudshuttle.core.message;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldDef {
    int order();
    int length(); // 0表示变长
} 