package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.protocol.TcpProtocolDefinition;

public abstract class AbstractProtocol implements Protocol {

    protected TcpProtocolDefinition tcpProtocolDefinition;

    public AbstractProtocol(TcpProtocolDefinition tcpProtocolDefinition) {
        this.tcpProtocolDefinition = tcpProtocolDefinition;
    }

    @Override
    public void registerFrame(Frame frame) {
        frame.setProtocolDefinition(tcpProtocolDefinition);
        FrameFactory.registerFrame(frame);
    }
}
