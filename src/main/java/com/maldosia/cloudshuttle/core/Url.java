package com.maldosia.cloudshuttle.core;

/**
 * @author Maldosia
 * @since 2025/6/23
 */
public class Url {

    private final String id;

    private final String remoteIp;
    
    private final int remotePort;
    
    private final String remoteUrl;

    public Url(String remoteIp, int remotePort) {
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.id = remoteIp + ":" + remotePort;
        this.remoteUrl = remoteIp + ":" + remotePort;
    }

    public String getId() {
        return id;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }
}