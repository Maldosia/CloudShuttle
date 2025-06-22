package com.maldosia.cloudshuttle.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Maldosia
 * @since 2025/6/22
 */
public class Connection {

    private static final Logger log = LoggerFactory.getLogger(Connection.class);
    
    private String key;
    
    private String poolKey;
    
    private final Channel channel;
    
    private final Url url;

    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final AtomicInteger referenceCount = new AtomicInteger();

    //没有引用
    private static final int NO_REFERENCE     = 0;

    public Connection(Channel channel, Url url) {
        this(channel, url, "default-pool-key", "default-key");
    }

    public Connection(Channel channel, Url url, String poolKey, String key) {
        this.channel = channel;
        this.url = url;
        this.poolKey = poolKey;
        this.key = key;
    }

    public Channel getChannel() {
        return channel;
    }

    public Url getUrl() {
        return url;
    }

    public String getKey() {
        return key;
    }

    public String getPoolKey() {
        return poolKey;
    }

    public void increaseRef() {
        this.referenceCount.getAndIncrement();
    }
    
    public void decreaseRef() {
        this.referenceCount.getAndDecrement();
    }
    
    public boolean noRef() {
        return this.referenceCount.get() == NO_REFERENCE;
    }

    public void close() {
        if (closed.compareAndSet(false, true)) {
            try {
                if (this.getChannel() != null) {
                    this.getChannel().close().addListener(new ChannelFutureListener() {

                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (log.isInfoEnabled()) {
                                log.info("Close the connection to remote address={}, result={}", getUrl().getRemoteUrl(), future.isSuccess(), future.cause());
                            }
                        }

                    });
                }
            } catch (Exception e) {
                log.warn("Exception caught when closing connection {}", getUrl().getRemoteUrl(), e);
            }
        }
    }
}