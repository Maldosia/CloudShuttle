package com.maldosia.cloudshuttle.core;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {

    private static final Map<FunctionCode, Command> commands = new HashMap<>();

    public static void registerCommands(FunctionCode functionCode, Command command) {
        commands.put(functionCode, command);
    }

    public static Command createCommand(FunctionCode functionCode) {
        return commands.get(functionCode);
    }
}
