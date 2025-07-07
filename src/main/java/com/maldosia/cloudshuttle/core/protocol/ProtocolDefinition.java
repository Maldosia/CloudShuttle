package com.maldosia.cloudshuttle.core.protocol;

import com.maldosia.cloudshuttle.core.field.FieldDefinition;
import com.maldosia.cloudshuttle.core.field.FieldType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 协议结构定义 - 精简版，采用链式API
 */
public class ProtocolDefinition {
    private final List<FieldDefinition> fields;
    private final String description;
    private final String protocolType;

    private ProtocolDefinition(List<FieldDefinition> fields, String description, String protocolType) {
        this.fields = Collections.unmodifiableList(fields);
        this.description = description;
        this.protocolType = protocolType;
    }

    public List<FieldDefinition> getFields() { return fields; }
    public String getDescription() { return description; }
    public String getProtocolType() { return protocolType; }

    /**
     * 链式构建器
     */
    public static class Builder {
        private final List<FieldDefinition> fields = new ArrayList<>();
        private String description = "";
        private String protocolType = "TCP";
        private int order = 1;

        public Builder description(String desc) {
            this.description = desc;
            return this;
        }
        public Builder protocolType(String type) {
            this.protocolType = type;
            return this;
        }
        public Builder addField(String name, FieldType type, int length) {
            fields.add(new FieldDefinition(name, type, length, order++));
            return this;
        }
        public Builder addFixedField(String name, FieldType type, byte[] fixedBytes) {
            fields.add(new FieldDefinition(name, type, fixedBytes.length, order++, fixedBytes));
            return this;
        }
        public ProtocolDefinition build() {
            return new ProtocolDefinition(fields, description, protocolType);
        }
    }

    public static Builder builder() { return new Builder(); }
}

