package com.maldosia.mobile.protocol;

import com.maldosia.cloudshuttle.core.annotation.ProtocolField;

public class DetectRequest {

    @ProtocolField(order = 1)
    private byte[] startFlag = new byte[4];

    @ProtocolField(order = 2, isFunctionCodeField = true)
    private byte[] functionCode = new byte[4];
    
    @ProtocolField(order = 3)
    private byte[] length = new byte[4];
    
    @ProtocolField(order = 4)
    private byte[] reserved = new byte[4];
    
    @ProtocolField(order = 5)
    private byte[] body = new byte[]{};

    public byte[] getStartFlag() {
        return startFlag;
    }

    public void setStartFlag(byte[] startFlag) {
        this.startFlag = startFlag;
    }

    public byte[] getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(byte[] functionCode) {
        this.functionCode = functionCode;
    }

    public byte[] getLength() {
        return length;
    }

    public void setLength(byte[] length) {
        this.length = length;
    }

    public byte[] getReserved() {
        return reserved;
    }

    public void setReserved(byte[] reserved) {
        this.reserved = reserved;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
