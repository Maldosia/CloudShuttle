package com.maldosia.cloudshuttle.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ProtocolField {
    int order() default 0;
    int length() default 0;

    boolean isStartFlagField() default false;
    boolean isFunctionCodeField() default false;
    boolean isLengthField() default false;
    boolean isBody() default false;
    boolean isEndFlagField() default false;
}
