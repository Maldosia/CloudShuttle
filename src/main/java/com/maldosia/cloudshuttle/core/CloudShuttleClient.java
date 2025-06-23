package com.maldosia.cloudshuttle.core;

public interface CloudShuttleClient extends OptionContainer{

    void connect(Url url);

    void connect(Url url, int connectTimeout);

    void disconnect();

    void reconnect(Url url);

    /**
     * 同步发送，需要等待响应
     */
    void sendSync(Object message);

    /**
     * 异步发送，不需要等待响应
     */
    void sendAsync(Object message);
    
}
