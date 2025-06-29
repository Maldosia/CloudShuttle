package com.maldosia.cloudshuttle.core.protocol;

import java.util.List;

public interface ProtocolDefinition {

    List<Field> getFields();

    String getLengthFieldName();

    String getBodyFieldName();

    int getFixedHeaderLength();
    
}
