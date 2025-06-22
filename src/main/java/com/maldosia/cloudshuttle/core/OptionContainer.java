package com.maldosia.cloudshuttle.core;

/**
 * 配置容器接口
 *
 * @author Maldosia
 * @since 2025/6/22
 */
public interface OptionContainer {

    /**
     * 设置配置项
     */
    <T> void setOption(Option<T> key, T value);

    /**
     * 获取配置项值
     */
    <T> T getOption(Option<T> key);

    /**
     * 获取配置项值，如果未设置则返回默认值
     */
    default <T> T getOptionOrDefault(Option<T> key) {
        T value = getOption(key);
        return value != null ? value : key.defaultValue();
    }
}
