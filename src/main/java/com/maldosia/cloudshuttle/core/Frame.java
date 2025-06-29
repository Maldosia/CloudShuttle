package com.maldosia.cloudshuttle.core;

import java.io.Serializable;

public interface Frame extends Serializable {

    FunctionCode getFunctionCode();

    void serializeBody();

    void deserializeBody();
}
