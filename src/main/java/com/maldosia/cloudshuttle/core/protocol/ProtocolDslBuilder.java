package com.maldosia.cloudshuttle.core.protocol;

import com.maldosia.cloudshuttle.core.field.FieldDefinition;
import com.maldosia.cloudshuttle.core.field.FieldType;
import java.util.ArrayList;
import java.util.List;

public class ProtocolDslBuilder {
    private final List<FieldDefinition> fields = new ArrayList<>();
    private String description = "";
    private String protocolType = "TCP";
    private int order = 1;

    public static ProtocolDslBuilder create() {
        return new ProtocolDslBuilder();
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
    public ProtocolDefinition build() {
        return new ProtocolDefinition(fields, description, protocolType);
    }
} 