package com.maldosia.cloudshuttle.core.message;

public abstract class AutoMessage implements Message {
    private FrameHeader header;
    @Override
    public void setFrameHeader(FrameHeader header) { this.header = header; }
    @Override
    public FrameHeader getFrameHeader() { return header; }
} 