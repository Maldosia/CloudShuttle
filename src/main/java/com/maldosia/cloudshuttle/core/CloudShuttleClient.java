package com.maldosia.cloudshuttle.core;

public interface CloudShuttleClient {

    /**
     * 单播，同步发送，需要等待响应
     */
    void unicastSync(Object message);
    void unicastSync(Object message, String poolKey);
    void unicastSync(Object message, String poolKey, String key);

    /**
     * 单播，异步发送，不需要等待响应
     */
    void unicastAsync(Object message);
    void unicastAsync(Object message, String poolKey);
    void unicastAsync(Object message, String poolKey, String key);


    /**
     * 广播，同步发送，需要等待响应
     */
    void broadcastSync(Object message);
    void broadcastSync(Object message, String poolKey);

    /**
     * 广播，异步发送，不需要等待响应
     */
    void broadAsync(Object message);
    void broadAsync(Object message, String poolKey);

}
