package com.maldosia.cloudshuttle.core;

/**
 * 字段解析器接口 - 将字节数组转换为Java对象
 */
public interface FieldParser {

    /**
     * 将字节数组解析为Java对象
     * @param data 原始字节对象
     * @return 解析后的Java对象
     */
    Object parse(byte[] data);
}
