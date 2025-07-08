package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.field.FieldDefinition;

import java.util.Collections;
import java.util.List;

/**
 * 协议结构声明
 */
public class ProtocolDefinition {
    private final List<FieldDefinition> fields;
    private final String description;
    private final String protocolType;
    private final boolean standard; // 是否标准协议

    public ProtocolDefinition(List<FieldDefinition> fields, String description, String protocolType, boolean standard) {
        this.fields = Collections.unmodifiableList(fields);
        this.description = description;
        this.protocolType = protocolType;
        this.standard = standard;
    }

    public List<FieldDefinition> getFields() { return fields; }
    public String getDescription() { return description; }
    public String getProtocolType() { return protocolType; }
    /**
     * 是否为标准协议（包含起始标志、功能码、长度、帧体）
     */
    public boolean isStandard() { return standard; }
}

