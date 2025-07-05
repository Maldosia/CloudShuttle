package com.maldosia.cloudshuttle.core;

/**
 * 字段定义抽象类 - 所有字段的基类
 */
public abstract class FieldDefinition<T> {
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

    /**
     * 解析字节数据为Java对象
     * @param data 字节数据
     * @return Java对象
     */
    public abstract Object parse(byte[] data);

    /**
     * 将Java对象组装为字节数组
     * @param value Java对象
     * @return 字节数组
     */
    public abstract byte[] assemble(T value);

    // Getter方法
    public int getLength() { return length; }
    public String getName() { return name; }
    public FieldType getType() { return type; }
}