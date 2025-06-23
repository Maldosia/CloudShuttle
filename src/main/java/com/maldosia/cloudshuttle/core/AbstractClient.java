package com.maldosia.cloudshuttle.core;

/**
 * @author Maldosia
 * @since 2025/6/22
 */
public abstract class AbstractClient extends AbstractLifeCycle {
    
    public final BaseOptions options;

    public AbstractClient() {
        this.options = new BaseOptions();
    }
}