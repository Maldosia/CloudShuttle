package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.field.FieldDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * 协议声明式构建器，支持链式API快速定义协议结构。
 * <p>
 * 推荐通过 {@link #standard()} 创建标准协议，或 {@link #custom()} 创建自定义协议。
 * </p>
 */
public class ProtocolDslBuilder {
    private final List<FieldDefinition> fields = new ArrayList<>();
    private String description = "";
    private String protocolType = "TCP";
    private int order = 1;
    private boolean standard = true; // 是否标准协议

    private ProtocolDslBuilder() {}

    /**
     * 创建标准协议构建器，自动添加起始标志、功能码、长度、帧体字段。
     * @return 构建器实例
     */
    public static ProtocolDslBuilder standard() {
        ProtocolDslBuilder builder = new ProtocolDslBuilder();
        builder.standard = true;
        // 自动添加标准字段
        builder.startFlag(0x68)
               .functionCode(1)
               .length(2)
               .body();
        return builder;
    }

    /**
     * 创建自定义协议构建器，所有字段需用户自行添加。
     * @return 构建器实例
     */
    public static ProtocolDslBuilder custom() {
        ProtocolDslBuilder builder = new ProtocolDslBuilder();
        builder.standard = false;
        return builder;
    }

    /**
     * 添加起始标志字段。
     * @param bytes 标志字节
     * @return 构建器
     */
    public ProtocolDslBuilder startFlag(int... bytes) {
        byte[] b = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) b[i] = (byte) bytes[i];
        fields.add(new FieldDefinition("START", FieldType.START_FLAG, b.length, order++, b));
        return this;
    }
    /**
     * 添加功能码字段。
     * @param length 字节长度
     * @return 构建器
     */
    public ProtocolDslBuilder functionCode(int length) {
        fields.add(new FieldDefinition("CODE", FieldType.FUNCTION_CODE, length, order++));
        return this;
    }
    /**
     * 添加长度字段。
     * @param length 字节长度
     * @return 构建器
     */
    public ProtocolDslBuilder length(int length) {
        fields.add(new FieldDefinition("LEN", FieldType.LENGTH, length, order++));
        return this;
    }
    /**
     * 添加帧体字段。
     * @return 构建器
     */
    public ProtocolDslBuilder body() {
        fields.add(new FieldDefinition("BODY", FieldType.BODY, 0, order++));
        return this;
    }
    /**
     * 添加结束标志字段。
     * @param bytes 标志字节
     * @return 构建器
     */
    public ProtocolDslBuilder endFlag(int... bytes) {
        byte[] b = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) b[i] = (byte) bytes[i];
        fields.add(new FieldDefinition("END", FieldType.END_FLAG, b.length, order++, b));
        return this;
    }
    /**
     * 添加自定义字段。
     * @param name 字段名
     * @param type 字段类型
     * @param length 字节长度
     * @return 构建器
     */
    public ProtocolDslBuilder addField(String name, FieldType type, int length) {
        fields.add(new FieldDefinition(name, type, length, order++));
        return this;
    }
    /**
     * 设置协议描述。
     * @param desc 描述
     * @return 构建器
     */
    public ProtocolDslBuilder description(String desc) {
        this.description = desc;
        return this;
    }
    /**
     * 设置协议类型。
     * @param type 协议类型
     * @return 构建器
     */
    public ProtocolDslBuilder protocolType(String type) {
        this.protocolType = type;
        return this;
    }
    /**
     * 构建协议定义。标准协议会校验必须包含标准字段。
     * @return 协议定义
     */
    public ProtocolDefinition build() {
        if (standard) {
            // 校验标准协议必须包含关键字段
            boolean hasStart = false, hasCode = false, hasLen = false, hasBody = false;
            for (FieldDefinition f : fields) {
                if (f.getType() == FieldType.START_FLAG) hasStart = true;
                if (f.getType() == FieldType.FUNCTION_CODE) hasCode = true;
                if (f.getType() == FieldType.LENGTH) hasLen = true;
                if (f.getType() == FieldType.BODY) hasBody = true;
            }
            if (!(hasStart && hasCode && hasLen && hasBody)) {
                throw new IllegalStateException("标准协议必须包含起始标志、功能码、长度、帧体字段");
            }
        }
        return new ProtocolDefinition(fields, description, protocolType, standard);
    }
} 