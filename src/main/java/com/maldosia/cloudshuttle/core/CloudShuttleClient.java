package com.maldosia.cloudshuttle.core;

public interface CloudShuttleClient {

    /**
     * 同步发送，需要等待响应
     */
    void sendSync();

    /**
     * 异步发送，不需要等待响应
     */
    void sendAsync();
    
}
