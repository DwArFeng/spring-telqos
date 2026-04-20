package com.dwarfeng.springtelqos.stack.naming;

import com.dwarfeng.springtelqos.stack.command.Command;

import java.util.Map;

/**
 * 转换指令标识信息。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public final class ToCommandIdentityInfo {

    private final CommandInfo commandInfo;
    private final Map<String, CommandInfo> registeredCommandInfos;

    public ToCommandIdentityInfo(CommandInfo commandInfo, Map<String, CommandInfo> registeredCommandInfos) {
        this.commandInfo = commandInfo;
        this.registeredCommandInfos = registeredCommandInfos;
    }

    public CommandInfo getCommandInfo() {
        return commandInfo;
    }

    public Map<String, CommandInfo> getRegisteredCommandInfos() {
        return registeredCommandInfos;
    }

    @Override
    public String toString() {
        return "ToCommandIdentityInfo{" +
                "commandInfo=" + commandInfo +
                ", registeredCommandInfos=" + registeredCommandInfos +
                '}';
    }

    /**
     * 指令信息。
     *
     * @author DwArFeng
     * @since 2.0.0
     */
    public static final class CommandInfo {

        private final String identify;
        private final Class<? extends Command> commandClass;

        public CommandInfo(String identify, Class<? extends Command> commandClass) {
            this.identify = identify;
            this.commandClass = commandClass;
        }

        public Class<? extends Command> getCommandClass() {
            return commandClass;
        }

        public String getIdentify() {
            return identify;
        }

        @Override
        public String toString() {
            return "CommandInfo{" +
                    "identify='" + identify + '\'' +
                    ", commandClass=" + commandClass +
                    '}';
        }
    }
}
