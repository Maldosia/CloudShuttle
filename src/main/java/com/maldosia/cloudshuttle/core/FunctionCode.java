package com.maldosia.cloudshuttle.core;

/**
 * 功能码接口 - 为消息提供功能码
 */
public interface FunctionCode {
    /**
     * 获取消息的功能码
     * @return 功能码字节数组
     */
    byte[] getCode();
}
