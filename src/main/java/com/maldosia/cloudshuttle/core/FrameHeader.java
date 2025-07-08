package com.maldosia.cloudshuttle.core;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 帧头数据容器
 */
public class FrameHeader {
    private final Map<String, byte[]> fields = new HashMap<>();

    /**
     * 添加字段数据（链式API）
     * @param name 字段名称
     * @param data 字段值（字节数组）
     * @return this，便于链式调用
     */
    public FrameHeader addField(String name, byte[] data) {
        fields.put(name, data);
        return this;
    }

    /**
     * 获取字段原始字节数据
     * @param name 字段名称
     * @return 字节数组
     */
    public byte[] getField(String name) {
        return fields.get(name);
    }

    /**
     * 获取字段值并转换为指定类型
     * @param name 字段名称
     * @param type 目标类型
     * @return 转换后的值
     * @param <T> 目标类型
     */
    @SuppressWarnings("unchecked")
    public <T> T getFieldAs(String name, Class<T> type) {
        byte[] data = fields.get(name);
        if (data == null) return null;

        if (type == byte[].class) {
            return (T) data;
        }
        if (type == String.class) {
            return (T) new String(data, StandardCharsets.UTF_8);
        }
        if (type == Integer.class || type == int.class) {
            return (T) Integer.valueOf(Bytes.toInt(data));
        }
        if (type == Long.class || type == long.class) {
            return (T) Long.valueOf(Bytes.toLong(data));
        }
        if (type == Short.class || type == short.class) {
            return (T) Short.valueOf(Bytes.toShort(data));
        }
        throw new IllegalArgumentException("不支持的类型: " + type);
    }
}
