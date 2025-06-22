package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.exception.LifeCycleException;

/**
 * 生命周期
 *
 * @author Maldosia
 * @since 2025/6/22
 */
public interface LifeCycle {

    /**
     * 启动
     * @throws LifeCycleException 如果已经启动，则抛出异常
     */
    void startup() throws LifeCycleException;

    /**
     * 关闭
     * @throws LifeCycleException 如果已经关闭，则抛出异常
     */
    void shutdown() throws LifeCycleException;

    /**
     * 是否已经启动
     * 
     * @return 是否已经启动
     */
    boolean isStarted();
    
}
