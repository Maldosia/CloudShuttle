package com.maldosia.cloudshuttle.core;

/**
 * 字节工具类 - 提供字节转换功能
 */
public class Bytes {
    /**
     * 将字节数组转换为整数
     * @param bytes 字节数组
     * @return 整数值
     */
    public static int toInt(byte[] bytes) {
        int value = 0;
        for (byte b : bytes) {
            value = (value << 8) | (b & 0xFF);
        }
        return value;
    }

    /**
     * 将整数转换为字节数组
     * @param value 整数值
     * @param length 字节数组长度
     * @return 字节数组
     */
    public static byte[] fromInt(int value, int length) {
        byte[] bytes = new byte[length];
        for (int i = length - 1; i >= 0; i--) {
            bytes[i] = (byte) (value & 0xFF);
            value >>>= 8;
        }
        return bytes;
    }

    /**
     * 将字节数组转换为长整数
     * @param bytes 字节数组
     * @return 长整数值
     */
    public static long toLong(byte[] bytes) {
        long value = 0;
        for (byte b : bytes) {
            value = (value << 8) | (b & 0xFF);
        }
        return value;
    }

    /**
     * 将长整数转换为字节数组
     * @param value 长整数值
     * @param length 字节数组长度
     * @return 字节数组
     */
    public static byte[] fromLong(long value, int length) {
        byte[] bytes = new byte[length];
        for (int i = length - 1; i >= 0; i--) {
            bytes[i] = (byte) (value & 0xFF);
            value >>>= 8;
        }
        return bytes;
    }

    /**
     * 将字节数组转换为短整数
     * @param bytes 字节数组
     * @return 短整数值
     */
    public static short toShort(byte[] bytes) {
        short value = 0;
        for (byte b : bytes) {
            value = (short) ((value << 8) | (b & 0xFF));
        }
        return value;
    }
}
