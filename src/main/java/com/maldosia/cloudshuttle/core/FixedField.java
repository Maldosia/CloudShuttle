package com.maldosia.cloudshuttle.core;

/**
 * 固定字段类 - 用于起始标志和结束标志
 */
public class FixedField extends FieldDefinition {
    private final byte[] fixedBytes; // 固定的字节值

    /**
     * 构造函数
     * @param fixedBytes 固定的字节数组
     * @param name 字段名称
     * @param type 字段类型
     */
    public FixedField(byte[] fixedBytes, String name, FieldType type) {
        super(fixedBytes.length, name, type);
        this.fixedBytes = fixedBytes;
    }

    /**
     * 获取固定字节
     * @return 字节数组
     */
    public byte[] getFixedBytes() {
        return fixedBytes;
    }
}
