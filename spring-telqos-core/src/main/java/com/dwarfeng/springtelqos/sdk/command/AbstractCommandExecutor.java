package com.dwarfeng.springtelqos.sdk.command;

import com.dwarfeng.springtelqos.stack.command.CommandExecutor;
import com.dwarfeng.springtelqos.stack.exception.CommandExecutorException;

/**
 * 指令执行器的抽象实现。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public abstract class AbstractCommandExecutor implements CommandExecutor {

    protected Context context;

    @Override
    public void init(Context context) {
        this.context = context;
    }

    @Override
    public void execute() throws CommandExecutorException {
        try {
            doExecute();
        } catch (CommandExecutorException e) {
            throw e;
        } catch (Exception e) {
            throw new CommandExecutorException(e);
        }
    }

    /**
     * 执行指令。
     *
     * @throws Exception 方法执行过程中发生的任何异常。
     */
    protected abstract void doExecute() throws Exception;

    @Override
    public String toString() {
        return "AbstractCommandExecutor{" +
                "context=" + context +
                '}';
    }
}
