package com.maldosia.cloudshuttle.core.options;

import com.maldosia.cloudshuttle.core.Option;

/**
 * 网络配置
 * 
 * @author Maldosia
 * @since 2025/6/22
 */
public class NetworkOptions {

    public static final Option<Boolean> RECONNECT_SWITCH =
            new Option.Builder<>("reconnectSwitch", Boolean.class)
                    .defaultValue(Boolean.TRUE) // 默认开启重连
                    .build();
    
    public static final Option<Integer> CONNECTION_TIMEOUT =
            new Option.Builder<>("connectionTimeout", Integer.class)
                    .defaultValue(5000) // 默认5秒
                    .validator(value -> {
                        if (value <= 0) {
                            throw new IllegalArgumentException("Connection timeout must be positive");
                        }
                    })
                    .build();

    public static final Option<Integer> MAX_CONNECTIONS =
            new Option.Builder<>("maxConnections", Integer.class)
                    .defaultValue(1000)
                    .validator(value -> {
                        if (value <= 0) {
                            throw new IllegalArgumentException("Max connections must be positive");
                        }
                    })
                    .build();

    public static final Option<Boolean> KEEP_ALIVE =
            new Option.Builder<>("keepAlive", Boolean.class)
                    .defaultValue(true)
                    .build();
}