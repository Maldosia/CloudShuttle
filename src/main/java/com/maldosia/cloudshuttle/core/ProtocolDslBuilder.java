package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.field.FieldDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * 协议声明式构建器
 */
public class ProtocolDslBuilder {
    private final List<FieldDefinition> fields = new ArrayList<>();
    private String description = "";
    private String protocolType = "TCP";
    private int order = 1;
    private boolean standard = true; // 是否标准协议

    private ProtocolDslBuilder() {}

    /**
     * 创建标准协议构建器，自动添加标准字段（起始标志、功能码、长度、帧体）。
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
     */
    public static ProtocolDslBuilder custom() {
        ProtocolDslBuilder builder = new ProtocolDslBuilder();
        builder.standard = false;
        return builder;
    }

    public ProtocolDslBuilder startFlag(int... bytes) {
        byte[] b = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) b[i] = (byte) bytes[i];
        fields.add(new FieldDefinition("START", FieldType.START_FLAG, b.length, order++, b));
        return this;
    }
    public ProtocolDslBuilder functionCode(int length) {
        fields.add(new FieldDefinition("CODE", FieldType.FUNCTION_CODE, length, order++));
        return this;
    }
    public ProtocolDslBuilder length(int length) {
        fields.add(new FieldDefinition("LEN", FieldType.LENGTH, length, order++));
        return this;
    }
    public ProtocolDslBuilder body() {
        fields.add(new FieldDefinition("BODY", FieldType.BODY, 0, order++));
        return this;
    }
    public ProtocolDslBuilder endFlag(int... bytes) {
        byte[] b = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) b[i] = (byte) bytes[i];
        fields.add(new FieldDefinition("END", FieldType.END_FLAG, b.length, order++, b));
        return this;
    }
    public ProtocolDslBuilder addField(String name, FieldType type, int length) {
        fields.add(new FieldDefinition(name, type, length, order++));
        return this;
    }
    public ProtocolDslBuilder description(String desc) {
        this.description = desc;
        return this;
    }
    public ProtocolDslBuilder protocolType(String type) {
        this.protocolType = type;
        return this;
    }
    /**
     * 构建协议定义。标准协议会校验必须包含标准字段。
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