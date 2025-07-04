package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.exception.ProtocolException;
import com.maldosia.cloudshuttle.core.protocol.ProtocolFunctionCode;
import io.netty.buffer.ByteBufUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

public class FrameFactory {
    private static final Logger log = LoggerFactory.getLogger(FrameFactory.class);

    private static final Map<byte[], Frame> frames = new HashMap<>();

    public static void registerFrame(Frame frame) {
        //解析帧注解
        byte[] functionCode = getFunctionCode(frame);
        frames.put(functionCode, frame);
    }

    //创建对应的frame
    public static Frame createFrame(byte[] functionCode, List<byte[]> fieldValues) {
        Frame frame = frames.get(functionCode);
        if (frame != null) {
            fillFields(frame, fieldValues);
            frame.deserializeBody();
            return frame;
        } else {
            log.error("No frame found for function code {}", ByteBufUtil.hexDump(functionCode));
            return null;
        }
    }

    private static byte[] getFunctionCode(Frame frame) {
        ProtocolFunctionCode annotation = frame.getClass().getAnnotation(ProtocolFunctionCode.class);
        if (annotation != null) {
            return annotation.value();
        } else {
            throw new ProtocolException("No ProtocolFunctionCode annotation found");
        }
    }

    private static void fillFields(Frame frame, List<byte[]> fieldValues) {
        //1.获取frame所有的属性注解
        List<Field> allFields = getAllFields(frame.getClass());
        for (int i = 0; i < allFields.size(); i++) {
            Field field = allFields.get(i);
            byte[] value = fieldValues.get(i);

            ProtocolField annotation = field.getAnnotation(ProtocolField.class);
            if (annotation != null) {
                int length = annotation.length();
                if (value == null || value.length != length) {
                    throw new ProtocolException("Length of field " + field.getName() + " is incorrect");
                }

            }
        }

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
