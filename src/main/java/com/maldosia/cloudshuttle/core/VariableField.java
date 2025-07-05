package com.maldosia.cloudshuttle.core;


/**
 * 可变字段类 - 用于普通头字段、功能码、长度等
 */
public class VariableField<T> extends FieldDefinition<T> {
    private FieldParser parser;      // 字段解析器
    private FieldAssembler<T> assembler; // 字段组装器

    /**
     * 构造函数
     * @param length 字段长度
     * @param name 字段名称
     * @param type 字段类型
     */
    public VariableField(int length, String name, FieldType type) {
        super(length, name, type);
    }

    /**
     * 设置字段解析器
     * @param parser 解析器实例
     */
    public void setParser(FieldParser parser) {
        this.parser = parser;
    }

    /**
     * 设置字段组装器
     * @param assembler 组装器实例
     */
    public void setAssembler(FieldAssembler<T> assembler) {
        this.assembler = assembler;
    }

    @Override
    public Object parse(byte[] data) {
        if (parser != null) {
            // 使用自定义解析器
            return parser.parse(data);
        }

        // 默认解析为字节数组
        return data;
    }

    @Override
    public byte[] assemble(T value) {
        if (assembler != null) {
            // 使用自定义组装器
            return assembler.assemble(value);
        }

        if (value instanceof byte[]) {
            // 直接使用字节数组
            return (byte[]) value;
        }

        // 默认处理
        throw new UnsupportedOperationException("未定义字段组装器: " + name);
    }
}
