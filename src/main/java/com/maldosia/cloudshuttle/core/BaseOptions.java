package com.maldosia.cloudshuttle.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基础配置容器实现
 *
 * @author Maldosia
 * @since 2025/6/22
 */
    public class BaseOptions {

    private final Map<Option<?>, Object> options = new ConcurrentHashMap<>();

    public <T> BaseOptions option(Option<T> key, T value) {
        // 类型检查
        if (!key.type().isInstance(value)) {
            throw new IllegalArgumentException(
                    "Invalid value type for option '" + key.name() +
                            "'. Expected: " + key.type().getSimpleName() +
                            ", Got: " + value.getClass().getSimpleName()
            );
        }

        // 值验证
        key.validate(value);

        // 存储配置
        options.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T option(Option<T> option) {
        Object value = options.get(option);
        if (value == null) {
            value = option.defaultValue();
        }

        return value == null ? null : (T) value;
    }

    /**
     * 获取所有设置的配置项
     */
    public Map<String, Object> getAllOptions() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<Option<?>, Object> entry : options.entrySet()) {
            result.put(entry.getKey().name(), entry.getValue());
        }
        return result;
    }
}