package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.exception.LifeCycleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TCP 客户端
 * 
 * @author Maldosia
 * @since 2025/6/22
 */
public class TcpClient extends AbstractClient {

    private static final Logger log = LoggerFactory.getLogger(TcpClient.class);
    
    private ConnectionManager connectionManager;
    
    public TcpClient() {
        
    }

    @Override
    public void startup() throws LifeCycleException {
        super.startup();

        connectionManager = new DefaultConnectionManager(new DefaultConnectionFactory(new CloudShuttleCodec(), new CloudShuttleHandler()));
    }

    @Override
    public void shutdown() throws LifeCycleException {
        super.shutdown();
    }

    @Override
    public void sendSync() {
        
    }

    @Override
    public void sendAsync() {

    }
}