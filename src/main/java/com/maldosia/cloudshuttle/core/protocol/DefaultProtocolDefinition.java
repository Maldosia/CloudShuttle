package com.maldosia.cloudshuttle.core.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Maldosia
 * @since 2025/6/30
 */
public class DefaultProtocolDefinition implements ProtocolDefinition{

    private final List<Field> fields;
    private final String lengthFieldName;
    private final String bodyFieldName;

    public DefaultProtocolDefinition(Builder builder) {
        this.fields = Collections.unmodifiableList(builder.fields);
        this.lengthFieldName = builder.lengthFieldName;
        this.bodyFieldName = builder.bodyFieldName;
    }

    public List<Field> getFields() {
        return fields;
    }

    public String getLengthFieldName() {
        return lengthFieldName;
    }

    public String getBodyFieldName() {
        return bodyFieldName;
    }

    public int getFixedHeaderLength() {
        return fields.stream()
                .filter(f -> !f.isDelimiter() && !f.getName().equals(bodyFieldName))
                .mapToInt(Field::getLength)
                .sum();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<Field> fields = new ArrayList<>();
        private String lengthFieldName;
        private String bodyFieldName;

        public Builder addField(String name, int length) {
            fields.add(new Field(name, length));
            return this;
        }

        public Builder addDelimiter(String name, byte[] delimiter) {
            fields.add(new Field(name, delimiter.length, true));
            return this;
        }

        public Builder setLengthField(String fieldName) {
            this.lengthFieldName = fieldName;
            return this;
        }

        public Builder setBodyField(String fieldName) {
            this.bodyFieldName = fieldName;
            return this;
        }

        public DefaultProtocolDefinition build() {
            if (lengthFieldName == null) throw new IllegalStateException("Length field not set");
            if (bodyFieldName == null) throw new IllegalStateException("Body field not set");
            return new DefaultProtocolDefinition(this);
        }
    }

}