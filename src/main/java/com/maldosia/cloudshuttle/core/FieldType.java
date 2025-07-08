package com.maldosia.cloudshuttle.core;

/**
 * 协议字段类型枚举。
 * <ul>
 *   <li>START_FLAG - 起始标志字段</li>
 *   <li>END_FLAG - 结束标志字段</li>
 *   <li>HEADER - 普通头字段</li>
 *   <li>FUNCTION_CODE - 功能码字段</li>
 *   <li>LENGTH - 长度字段</li>
 *   <li>BODY - 报文体字段</li>
 *   <li>CUSTOM - 自定义字段</li>
 * </ul>
 */
public enum FieldType {
    START_FLAG,         // 起始标志字段
    END_FLAG,           // 结束标志字段
    HEADER,             // 普通头字段
    FUNCTION_CODE,      // 功能码字段
    LENGTH,             // 长度字段
    BODY,               // 报文体字段
    CUSTOM              // 自定义字段
}
