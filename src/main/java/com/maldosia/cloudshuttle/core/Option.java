package com.maldosia.cloudshuttle.core;

import java.util.Objects;

/**
 * 配置项
 * 
 * @param <T> 配置值类型
 * @author Maldosia
 * @since 2025/6/22
 */
public class Option<T> {
    private final String name;
    private final Class<T> type;
    private final T defaultValue;
    private final Validator<T> validator;

    public Option(String name, Class<T> type, T defaultValue, Validator<T> validator) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.validator = validator;
    }

    public String name() {
        return name;
    }

    public Class<T> type() {
        return type;
    }

    public T defaultValue() {
        return defaultValue;
    }
    
    public void validate(T value) {
        if (validator != null) {
            validator.validate(value);
        }
    }


    /**
     * 构造器模式创建Option
     * 
     * @param <T> 配置值类型
     */
    public static class Builder<T> {
        private final String name;
        private final Class<T> type;
        private T defaultValue;
        private Validator<T> validator;

        public Builder(String name, Class<T> type) {
            this.name = Objects.requireNonNull(name, "name");
            this.type = Objects.requireNonNull(type, "type");
        }

        public Builder<T> defaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder<T> validator(Validator<T> validator) {
            this.validator = validator;
            return this;
        }

        public Option<T> build() {
            return new Option<>(name, type, defaultValue, validator);
        }
    }


    /**
     * 配置值验证器
     * 
     * @param <T> 配置值类型
     */
    public interface Validator<T> {
        void validate(T value);
    }
}
