package com.maldosia.cloudshuttle.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Maldosia
 * @since 2025/6/22
 */
public class ConnectionPool {

    private final CopyOnWriteArrayList<Connection> connections;

    public ConnectionPool() {
        this.connections = new CopyOnWriteArrayList<Connection>();
    }

    public void add(Connection connection) {
        if (null == connection) {
            return;
        }
        boolean res = connections.addIfAbsent(connection);
        if (res) {
            connection.increaseRef();
        }
    }

    public boolean contains(Connection connection) {
        return connections.contains(connection);
    }

    public void removeAndTryClose(Connection connection) {
        if (null == connection) {
            return;
        }
        boolean res = connections.remove(connection);
        if (res) {
            connection.decreaseRef();
        }
        if (connection.noRef()) {
            connection.close();
        }
    }

    /**
     * remove all connections
     */
    public void removeAllAndTryClose() {
        for (Connection conn : connections) {
            removeAndTryClose(conn);
        }
        connections.clear();
    }

    public Connection get() {
        // TODO 使用选择策略
        return connections.get(0);
    }

    public List<Connection> getAll() {
        return new ArrayList<>(connections);
    }
}