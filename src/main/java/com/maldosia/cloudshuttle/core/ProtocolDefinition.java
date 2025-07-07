package com.maldosia.cloudshuttle.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 协议定义类 - 定义TCP协议的结构
 */
public class ProtocolDefinition {
    private final List<FieldDefinition> fields = new ArrayList<>();

    /**
     * 获取协议字段列表
     * @return 字段列表
     */
    public List<FieldDefinition> getFields() {
        return fields;
    }

    /**
     * 创建Builder实例
     * @return Builder对象
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder内部类 - 用于构建ProtocolDefinition
     */
    public static class Builder {
        private final ProtocolDefinition definition = new ProtocolDefinition();

        /**
         * 添加起始标志字段
         * @param bytes 起始标志字节
         * @return Builder实例
         */
        public Builder addStartFlag(byte... bytes) {
            definition.fields.add(new FixedField(bytes, "START_FLAG", FieldType.START_FLAG));
            return this;
        }

        /**
         * 添加结束标志字段
         * @param bytes 结束标志字节
         * @return Builder实例
         */
        public Builder addEndFlag(byte... bytes) {
            definition.fields.add(new FixedField(bytes, "END_FLAG", FieldType.END_FLAG));
            return this;
        }

        /**
         * 添加固定长度头字段
         * @param length 字段长度
         * @param name 字段名称
         * @param type 字段类型
         * @return Builder实例
         */
        public Builder addHeaderField(int length, String name, FieldType type) {
            definition.fields.add(new FieldDefinition(length, name, type));
            return this;
        }

        /**
         * 添加功能码字段
         * @param length 功能码长度
         * @return Builder实例
         */
        public Builder addFunctionCodeField(int length) {
            definition.fields.add(new FieldDefinition(length, "FUNCTION_CODE", FieldType.FUNCTION_CODE));
            return this;
        }

        /**
         * 添加长度字段
         * @param length 长度字段的字节长度
         * @return Builder实例
         */
        public Builder addLengthField(int length) {
            definition.fields.add(new FieldDefinition(length, "LENGTH", FieldType.LENGTH));
            return this;
        }

        /**
         * 添加报文体字段
         * @return Builder实例
         */
        public Builder addBodyField() {
            definition.fields.add(new FieldDefinition(0, "BODY", FieldType.BODY));
            return this;
        }

        /**
         * 添加自定义字段
         * @param length 字段长度
         * @param name 字段名称
         * @param parser 字段解析器
         * @param assembler 字段组装器
         * @return Builder实例
         */
        public Builder addCustomField(int length, String name, FieldParser parser, FieldAssembler<?> assembler) {
            FieldDefinition field = new FieldDefinition(length, name, FieldType.CUSTOM);
            definition.fields.add(field);
            return this;
        }

        /**
         * 构建ProtocolDefinition实例
         * @return ProtocolDefinition对象
         */
        public ProtocolDefinition build() {
            // 验证协议定义
            if (definition.fields.stream().noneMatch(f -> f.getType() == FieldType.START_FLAG)) {
                throw new ProtocolConfigurationException("协议必须包含起始标志字段");
            }
            if (definition.fields.stream().noneMatch(f -> f.getType() == FieldType.FUNCTION_CODE)) {
                throw new ProtocolConfigurationException("协议必须包含功能码字段");
            }
            return definition;
        }
    }
}

