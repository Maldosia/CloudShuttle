package com.maldosia.cloudshuttle.core;

import io.netty.buffer.ByteBuf;

import java.lang.reflect.Field;
import java.util.*;

public class FrameFactory {

    private static final Map<FunctionCode, Frame> frames = new HashMap<>();

    public static void registerFrame(Frame frame) {
        frames.put(frame.getFunctionCode(), frame);
    }

    public static Frame createFrame(Map<String, ByteBuf> fields, Map<String, byte[]> delimiters, ByteBuf body) {
//        return frames.get(functionCode);
        return null;
    }

    /**
     * 递归获取类及其所有父类的属性
     * @param clazz 目标类
     * @return 包含所有属性的列表（包括父类）
     */
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        // 递归遍历直到Object类
        while (clazz != null && clazz != Object.class) {
            Collections.addAll(fields, clazz.getDeclaredFields());
            clazz = clazz.getSuperclass(); // 获取父类继续处理
        }
        return fields;
    }
}
