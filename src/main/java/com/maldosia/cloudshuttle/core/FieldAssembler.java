package com.maldosia.cloudshuttle.core;

/**
 * 字段组装器接口 - 将Java对象转换为字节数组
 *
 * @param <T> 对象类型
 */
public interface FieldAssembler<T> {

    /**
     * 将Java对象转换为字节数组
     * @param value Java对象
     * @return 字节数组
     */
    byte[] assemble(T value);
}
