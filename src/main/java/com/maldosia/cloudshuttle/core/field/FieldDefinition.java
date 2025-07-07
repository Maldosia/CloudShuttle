package com.maldosia.cloudshuttle.core.field;

/**
 * 字段定义 - 用于描述协议中的字段结构
 * 只保留一个类，所有类型用 FieldType 区分
 */
public class FieldDefinition {
    private final String name;      // 字段名称
    private final FieldType type;   // 字段类型
    private final int length;       // 字段长度
    private final int order;        // 字段顺序
    private final byte[] fixedBytes; // 固定字段内容，仅用于START_FLAG/END_FLAG

    /**
     * 构造函数
     * @param name 字段名称
     * @param type 字段类型
     * @param length 字段长度
     * @param order 字段顺序
     */
    public FieldDefinition(String name, FieldType type, int length, int order) {
        this(name, type, length, order, null);
    }

    /**
     * 构造函数
     * @param name 字段名称
     * @param type 字段类型
     * @param length 字段长度
     * @param order 字段顺序
     * @param fixedBytes 固定字段内容，仅用于START_FLAG/END_FLAG
     */
    public FieldDefinition(String name, FieldType type, int length, int order, byte[] fixedBytes) {
        this.name = name;
        this.type = type;
        this.length = length;
        this.order = order;
        this.fixedBytes = fixedBytes;
    }

    // Getter方法
    public String getName() { return name; }
    public FieldType getType() { return type; }
    public int getLength() { return length; }
    public int getOrder() { return order; }
    public byte[] getFixedBytes() { return fixedBytes; }
}