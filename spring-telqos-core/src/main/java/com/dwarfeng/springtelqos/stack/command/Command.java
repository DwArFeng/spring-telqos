package com.dwarfeng.springtelqos.stack.command;

import com.dwarfeng.springtelqos.stack.exception.CommandException;

/**
 * 指令。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public interface Command {

    /**
     * 获取指令的标识。
     *
     * @return 指令的标识。
     */
    String getIdentity();

    /**
     * 生成一个新的指令描述器。
     *
     * @return 新生成的指令描述器。
     * @throws CommandException 指令异常。
     */
    CommandDescriptor newCommandDescriptor() throws CommandException;

    /**
     * 生成一个新的指令执行器。
     *
     * @return 新生成的指令执行器。
     * @throws CommandException 指令异常。
     */
    CommandExecutor newCommandExecutor() throws CommandException;
}
