package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.protocol.CommonProtocolDefinition;

public abstract class AbstractProtocol implements Protocol {

    protected CommonProtocolDefinition commonProtocolDefinition;

    public AbstractProtocol(CommonProtocolDefinition commonProtocolDefinition) {
        this.commonProtocolDefinition = commonProtocolDefinition;
    }

    @Override
    public void registerFrame(Message message) {
        FrameFactory.registerFrame(message);
    }
}
