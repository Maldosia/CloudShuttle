package com.maldosia.cloudshuttle.core.options;

import com.maldosia.cloudshuttle.core.Option;

/**
 * 性能调优配置
 * 
 * @author Maldosia
 * @since 2025/6/22
 */
public class PerformanceOptions {
    
    public static final Option<Integer> BUFFER_SIZE =
            new Option.Builder<>("bufferSize", Integer.class)
                    .defaultValue(8192) // 8KB
                    .validator(value -> {
                        if (value <= 0) {
                            throw new IllegalArgumentException("Buffer size must be positive");
                        }
                    })
                    .build();

    public static final Option<Boolean> DIRECT_BUFFERS =
            new Option.Builder<>("directBuffers", Boolean.class)
                    .defaultValue(true)
                    .build();
}