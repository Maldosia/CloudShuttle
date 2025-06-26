package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.exception.CommandException;

import java.lang.reflect.Field;
import java.util.*;

public class CommandFactory {

    private static final Map<FunctionCode, Command> commands = new HashMap<>();

    public static void registerCommands(FunctionCode functionCode, Command command) {
        commands.put(functionCode, command);
    }

    public static Command createCommand(FunctionCode functionCode) {
        return commands.get(functionCode);
    }

    public static Command getCommandTemplate() {
        ArrayList<Command> commandList = new ArrayList<>(commands.values());
        if (commandList.isEmpty()) {
            throw new CommandException("No command found");
        } else {
            return commandList.get(0);
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
