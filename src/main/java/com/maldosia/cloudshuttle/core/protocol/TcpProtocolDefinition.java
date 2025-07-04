package com.maldosia.cloudshuttle.core.protocol;

import com.maldosia.cloudshuttle.core.ProtocolDefinition;
import com.maldosia.cloudshuttle.core.exception.ProtocolException;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用TCP协议定义
 *
 * @author Maldosia
 * @since 2025/6/30
 */
public class TcpProtocolDefinition implements ProtocolDefinition {

    private ProtocolStartFlagField startFlag;
    private ProtocolField length;
    private ProtocolField body;
    private ProtocolField endFlag;
    private final List<ProtocolField> fieldList = new ArrayList<>();

    public TcpProtocolDefinition(Builder builder) {
        for (ProtocolField field : builder.fields) {
            fieldList.add(field);
            switch (field.getType()) {
                case START_FLAG -> startFlag = (ProtocolStartFlagField) field;
                case END_FLAG -> endFlag = field;
                case BODY -> body = field;
                case LENGTH -> length = field;
                default -> throw new IllegalStateException("Unexpected value: " + field.getType());
            }
        }

        if (startFlag == null) {
            throw new ProtocolException("Missing startFlag field");
        }
        if (length == null) {
            throw new ProtocolException("Missing length field");
        }
    }

    public ProtocolStartFlagField getStartFlagField() {
        return startFlag;
    }
    
    public int getStartFlagFieldLength(){
        return startFlag.getLength();
    }
    
    public int getAllFieldsLength() {
        return fieldList.stream().mapToInt(ProtocolField::getLength).sum();
    }
    

    public static Builder builder() {
        return new Builder();
    }

    public List<ProtocolField> getAllFields() {
        return fieldList;
    }


    public static class Builder {

        private final List<ProtocolField> fields = new ArrayList<>();

        public Builder addField(int length, ProtocolFieldEnum protocolField) {
            fields.add(new ProtocolField(length, protocolField));
            return this;
        }

        public Builder addStartFlagField(byte[] startFlag) {
            fields.add(new ProtocolStartFlagField(startFlag));
            return this;
        }

        public Builder addEndFlagField(byte[] endFlag) {
            fields.add(new ProtocolField(endFlag, ProtocolFieldEnum.END_FLAG));
            return this;
        }
        
        public Builder addBodyField() {
            fields.add(new ProtocolField(0, ProtocolFieldEnum.BODY));
            return this;
        }

        public Builder addLengthField(int length, ProtocolLengthField.ProtocolLengthFieldEnum lengthFieldEnum) {
            fields.add(new ProtocolLengthField(length, ProtocolFieldEnum.LENGTH, lengthFieldEnum));
            return this;
        }
        
        public TcpProtocolDefinition build() {
            if (fields.isEmpty()) throw new IllegalStateException("Field not set");
            return new TcpProtocolDefinition(this);
        }
    }

}