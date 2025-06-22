package com.maldosia.cloudshuttle.core;

public interface ConnectionFactory {

    void init();
    
    Connection createConnection(Url url) throws Exception;
    
    Connection createConnection(Url url, int connectTimeout) throws Exception;
    
}
