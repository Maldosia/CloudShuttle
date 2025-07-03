package com.maldosia.cloudshuttle.core.util;

/**
 * @author Maldosia
 * @since 2025/7/4
 */
public class ByteUtil {


    /**
     * 将4字节的字节数组按大端序转换为int
     * @param bytes 字节数组（必须为4字节）
     * @return 转换后的整数
     * @throws IllegalArgumentException 如果输入不为4字节
     */
    public static int toIntBigEndian(byte[] bytes) {
        if (bytes.length != 4) 
            throw new IllegalArgumentException("输入字节数组长度必须为4，实际长度: " + bytes.length);
        // 依次取前4个字节，分别左移24、16、8、0位后合并
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) <<  8) |
                ((bytes[3] & 0xFF) <<  0);
    }
    

    /**
     * 将4字节的字节数组按小端序转换为int
     * @param bytes 字节数组（必须为4字节）
     * @return 转换后的整数
     * @throws IllegalArgumentException 如果输入不为4字节
     */
    public static int toIntLittleEndian(byte[] bytes) {
        if (bytes.length != 4) 
               throw new IllegalArgumentException("输入字节数组长度必须为4，实际长度: " + bytes.length);
        // 依次取后4个字节的逆序，分别左移24、16、8、0位后合并
        return ((bytes[3] & 0xFF) << 24) |
                ((bytes[2] & 0xFF) << 16) |
                ((bytes[1] & 0xFF) <<  8) |
                ((bytes[0] & 0xFF) <<  0);
    }

}