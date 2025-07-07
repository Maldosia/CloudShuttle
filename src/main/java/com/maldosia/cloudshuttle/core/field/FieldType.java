package com.maldosia.cloudshuttle.core.field;

/**
 * 字段类型枚举 - 定义协议中不同类型的字段
 */
public enum FieldType {
    START_FLAG,         //起始标志字段
    END_FLAG,           //结束标志字段
    HEADER,             //普通头字段
    FUNCTION_CODE,      //功能码字段
    LENGTH,             //长度字段
    BODY,               //报文体字段
    CUSTOM              //自定义字段
}
