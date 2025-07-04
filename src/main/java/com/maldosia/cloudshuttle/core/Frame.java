package com.maldosia.cloudshuttle.core;

public abstract class Frame {

    protected ProtocolDefinition protocolDefinition;

    protected void setProtocolDefinition(ProtocolDefinition protocolDefinition) {
        this.protocolDefinition = protocolDefinition;
    }

    public ProtocolDefinition getProtocolDefinition() {
        return protocolDefinition;
    }

    abstract protected void serializeBody();

    abstract protected void deserializeBody();
}
