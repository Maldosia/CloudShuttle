package com.maldosia.cloudshuttle.core;

/**
 * 字段定义抽象类 - 所有字段的基类
 */
public class FieldDefinition {
    protected final int length;  // 字段长度（字节）
    protected final String name; // 字段名称
    protected final FieldType type; // 字段类型

    /**
     * 构造函数
     * @param length 字段长度
     * @param name 字段名称
     * @param type 字段类型
     */
    public FieldDefinition(int length, String name, FieldType type) {
        this.length = length;
        this.name = name;
        this.type = type;
    }

    // Getter方法
    public int getLength() { return length; }
    public String getName() { return name; }
    public FieldType getType() { return type; }
}