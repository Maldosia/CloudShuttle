package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.exception.ConnectionException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Maldosia
 * @since 2025/6/22
 */
public class DefaultConnectionManager implements ConnectionManager {
    
    protected ConnectionFactory connectionFactory;

    protected ConcurrentMap<String, ConnectionPool> connectionPools;

    public DefaultConnectionManager(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.connectionPools = new ConcurrentHashMap<>();
    }

    @Override
    public Connection create(Url url) {
        try {
            return this.connectionFactory.createConnection(url);
        } catch (Exception e) {
            throw new ConnectionException("Create connection failed. The address is " + url.getRemoteUrl(), e);
        }
    }

    @Override
    public Connection create(Url url, int connectTimeout) {
        try {
            return this.connectionFactory.createConnection(url, connectTimeout);
        } catch (Exception e) {
            throw new ConnectionException("Create connection failed. The address is " + url.getRemoteUrl(), e);
        }
    }

    @Override
    public void add(Connection connection) {
        this.add(connection, connection.getPoolKey());
    }

    @Override
    public void add(Connection connection, String poolKey) {
        ConnectionPool connectionPool = this.connectionPools.computeIfAbsent(connection.getPoolKey(), k -> new ConnectionPool());
        connectionPool.add(connection);
    }

    @Override
    public Connection get(String poolKey) {
        ConnectionPool pool = this.connectionPools.get(poolKey);
        return null == pool ? null : pool.get();
    }

    @Override
    public List<Connection> getAll(String poolKey) {
        ConnectionPool pool = this.connectionPools.get(poolKey);
        return null == pool ? new ArrayList<>() : pool.getAll();
    }

    @Override
    public void remove(Connection connection) {
        if (null == connection) {
            return;
        }
        this.remove(connection.getPoolKey(), connection);
    }

    @Override
    public void remove(String poolKey) {
        ConnectionPool pool = this.connectionPools.get(poolKey);
        pool.removeAllAndTryClose();
    }

    @Override
    public void remove(String poolKey, Connection connection) {
        if (null == connection) {
            return;
        }
        ConnectionPool pool = this.connectionPools.get(poolKey);
        if (null != pool) {
            pool.removeAndTryClose(connection);
        }
    }
}