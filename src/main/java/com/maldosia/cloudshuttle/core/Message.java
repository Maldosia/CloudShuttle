package com.maldosia.cloudshuttle.core;

import java.io.Serializable;

public interface Message extends Serializable {

    FunctionCode getFunctionCode();

    void serializeBody();

    void deserializeBody();
}
