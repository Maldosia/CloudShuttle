package com.maldosia.cloudshuttle.core;

import java.io.Serializable;

public interface Command extends Serializable {

    FunctionCode getFunctionCode();

    void serializeContent();

    void deserializeContent();
}
