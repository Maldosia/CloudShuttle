package com.maldosia.cloudshuttle.core;

import java.util.List;

/**
 * @author Maldosia
 * @since 2025/6/22
 */
public interface ConnectionManager {





    Connection getAndCreateIfAbsent(Url url);

    Connection getAndCreateIfAbsent(Url url, String poolKey);

    Connection get(String poolKey);

    List<Connection> getAll(String poolKey);
    
    void remove(Connection connection);
    
    void remove(String poolKey);
    
    void remove(String poolKey, Connection connection);
}