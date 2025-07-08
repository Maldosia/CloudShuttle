package com.maldosia.cloudshuttle.core.message;

import com.maldosia.cloudshuttle.core.protocol.Protocol;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class MessageAutoRegistrar {
    public static void registerAll(Protocol protocol, String basePackage) {
        Set<Class<?>> messageClasses = scanPackage(basePackage);
        for (Class<?> clazz : messageClasses) {
            if (clazz.isAnnotationPresent(MessageType.class) && Message.class.isAssignableFrom(clazz)) {
                MessageType type = clazz.getAnnotation(MessageType.class);
                try {
                    protocol.registerMessage(type.code(), (Class<? extends Message>) clazz, () -> {
                        try {
                            return (Message) clazz.getDeclaredConstructor().newInstance();
                        } catch (Exception e) {
                            throw new RuntimeException("自动实例化消息失败: " + clazz.getName(), e);
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException("自动注册消息失败: " + clazz.getName(), e);
                }
            }
        }
    }

    // 简单包扫描实现（仅支持同一classpath下的类）
    private static Set<Class<?>> scanPackage(String basePackage) {
        Set<Class<?>> classes = new HashSet<>();
        String path = basePackage.replace('.', '/');
        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                // 这里只处理文件协议
                if (resource.getProtocol().equals("file")) {
                    java.io.File dir = new java.io.File(resource.getFile());
                    for (String file : dir.list()) {
                        if (file.endsWith(".class")) {
                            String className = basePackage + '.' + file.substring(0, file.length() - 6);
                            try {
                                classes.add(Class.forName(className));
                            } catch (ClassNotFoundException ignored) {}
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return classes;
    }
} 