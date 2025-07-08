package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.field.FieldDefinition;

import java.util.Collections;
import java.util.List;

/**
 * 协议结构声明，描述协议的字段、类型、顺序等信息。
 * <p>
 * 推荐通过 {@link ProtocolDslBuilder} 构建。
 * </p>
 */
public class ProtocolDefinition {
    private final List<FieldDefinition> fields;
    private final String description;
    private final String protocolType;
    private final boolean standard; // 是否标准协议

    /**
     * 构造函数。
     * @param fields 字段定义列表
     * @param description 协议描述
     * @param protocolType 协议类型
     * @param standard 是否为标准协议
     */
    public ProtocolDefinition(List<FieldDefinition> fields, String description, String protocolType, boolean standard) {
        this.fields = Collections.unmodifiableList(fields);
        this.description = description;
        this.protocolType = protocolType;
        this.standard = standard;
    }

    /**
     * 获取所有字段定义（只读）。
     * @return 字段定义列表
     */
    public List<FieldDefinition> getFields() { return fields; }
    /**
     * 获取协议描述信息。
     * @return 描述字符串
     */
    public String getDescription() { return description; }
    /**
     * 获取协议类型（如TCP/UDP）。
     * @return 协议类型
     */
    public String getProtocolType() { return protocolType; }
    /**
     * 是否为标准协议（包含起始标志、功能码、长度、帧体）。
     * @return true=标准协议
     */
    public boolean isStandard() { return standard; }
}

