package com.maldosia.cloudshuttle.core.protocol;

import com.maldosia.cloudshuttle.core.ProtocolDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用TCP协议定义
 *
 * @author Maldosia
 * @since 2025/6/30
 */
public class CommonProtocolDefinition implements ProtocolDefinition {

    private final byte[] startFlag;
    private final byte[] length;
    private final byte[] body;
    private final byte[] endFlag;
    private final List<byte[]> headerFieldList;
    private final List<byte[]> trailerFieldList;

    public CommonProtocolDefinition(Builder builder) {
        this.headerFieldList = builder.headerFields;
        this.trailerFieldList = builder.trailerFields;

        this.startFlag = builder.startFlagField;
        this.length = builder.lengthField;
        this.body = builder.bodyField;
        this.endFlag = builder.endFlagField;
    }

    public List<byte[]> getHeaderFields() {
        return headerFieldList;
    }

    public List<byte[]> getTrailerFields() {
        return trailerFieldList;
    }

    public byte[] getStartFlag() {
        return startFlag;
    }

    public byte[] getEndFlag() {
        return endFlag;
    }

    public byte[] getLength() {
        return length;
    }

    public byte[] getBody() {
        return body;
    }

    public int getFixedHeaderLength() {
        return headerFieldList.stream()
                .mapToInt(bytes -> bytes.length)
                .sum();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private byte[] startFlagField;
        private byte[] endFlagField;
        private byte[] lengthField;
        private byte[] bodyField;

        private final List<byte[]> headerFields = new ArrayList<>();
        private final List<byte[]> trailerFields = new ArrayList<>();

        public Builder addField(int length, ProtocolFieldEnum protocolField) {
            switch (protocolField) {
                case START_FLAG ->
                    startFlagField = new byte[length];
                case END_FLAG  ->
                    endFlagField = new byte[length];
                case BODY  ->
                    bodyField = new byte[length];
                case LENGTH  ->
                    lengthField = new byte[length];
                case HEADER  ->
                    headerFields.add(new byte[length]);
                case TRAILER  ->
                    trailerFields.add(new byte[length]);
            }
            return this;
        }

        public Builder addField(byte[] flagField, ProtocolFieldEnum protocolField) {
            switch (protocolField) {
                case START_FLAG ->
                        startFlagField = flagField;
                case END_FLAG  ->
                        endFlagField = flagField;
            }
            return this;
        }

        public CommonProtocolDefinition build() {
            if (lengthField == null) throw new IllegalStateException("Length field not set");
            if (bodyField == null) throw new IllegalStateException("Body field not set");
            return new CommonProtocolDefinition(this);
        }
    }

}