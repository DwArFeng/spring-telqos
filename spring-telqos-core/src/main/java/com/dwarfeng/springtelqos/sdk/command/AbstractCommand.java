package com.dwarfeng.springtelqos.sdk.command;

import com.dwarfeng.springtelqos.stack.command.Command;
import com.dwarfeng.springtelqos.stack.command.CommandDescriptor;
import com.dwarfeng.springtelqos.stack.command.CommandExecutor;
import com.dwarfeng.springtelqos.stack.exception.CommandException;

/**
 * 抽象指令。
 *
 * <p>
 * 指令的抽象实现。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public abstract class AbstractCommand implements Command {

    protected final String identity;

    public AbstractCommand(String identity) {
        this.identity = identity;
    }

    @Override
    public String getIdentity() {
        return identity;
    }

    @Override
    public CommandDescriptor newCommandDescriptor() throws CommandException {
        try {
            return doNewCommandDescriptor();
        } catch (CommandException e) {
            throw e;
        } catch (Exception e) {
            throw new CommandException(e);
        }
    }

    /**
     * 生成一个新的指令描述器。
     *
     * @return 新生成的指令描述器。
     * @throws Exception 方法执行过程中发生的任何异常。
     */
    protected abstract CommandDescriptor doNewCommandDescriptor() throws Exception;

    @Override
    public CommandExecutor newCommandExecutor() throws CommandException {
        try {
            return doNewCommandExecutor();
        } catch (CommandException e) {
            throw e;
        } catch (Exception e) {
            throw new CommandException(e);
        }
    }

    /**
     * 生成一个新的指令执行器。
     *
     * @return 新生成的指令执行器。
     * @throws Exception 方法执行过程中发生的任何异常。
     */
    protected abstract CommandExecutor doNewCommandExecutor() throws Exception;

    @Override
    public String toString() {
        return "AbstractCommand{" +
                "identity='" + identity + '\'' +
                '}';
    }
}
