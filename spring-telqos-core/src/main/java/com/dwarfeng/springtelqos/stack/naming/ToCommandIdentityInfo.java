package com.dwarfeng.springtelqos.stack.naming;

import com.dwarfeng.springtelqos.stack.command.Command;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * 转换指令标识信息。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public record ToCommandIdentityInfo(CommandInfo commandInfo, Map<String, CommandInfo> registeredCommandInfos) {

    @Override
    public @NotNull String toString() {
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
    public record CommandInfo(String identify, Class<? extends Command> commandClass) {

        @Override
        public @NotNull String toString() {
            return "CommandInfo{" +
                    "identify='" + identify + '\'' +
                    ", commandClass=" + commandClass +
                    '}';
        }
    }
}
