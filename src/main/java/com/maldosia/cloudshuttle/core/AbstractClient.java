package com.maldosia.cloudshuttle.core;

/**
 * @author Maldosia
 * @since 2025/6/22
 */
public abstract class AbstractClient extends AbstractLifeCycle implements Client {
    
    public final BaseOptions options;

    public AbstractClient() {
        this.options = new BaseOptions();
    }

    @Override
    public <T> OptionContainer option(Option<T> option, T value) {
        options.option(option, value);
        return this;
    }

    @Override
    public <T> T option(Option<T> option) {
        return options.option(option);
    }
}