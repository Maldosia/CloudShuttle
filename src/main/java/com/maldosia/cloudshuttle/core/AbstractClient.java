package com.maldosia.cloudshuttle.core;

/**
 * @author Maldosia
 * @since 2025/6/22
 */
public abstract class AbstractClient extends AbstractLifeCycle implements CloudShuttleClient {
    
    public final BaseOptions options;

    public AbstractClient() {
        this.options = new BaseOptions();
    }

    @Override
    public <T> void option(Option<T> key, T value) {

    }

    @Override
    public <T> T option(Option<T> key) {
        return null;
    }
}