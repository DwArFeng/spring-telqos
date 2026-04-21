package com.dwarfeng.springtelqos.sdk.command;

import com.dwarfeng.springtelqos.stack.command.CommandDescriptor;
import com.dwarfeng.springtelqos.stack.exception.CommandDescriptorException;

/**
 * 指令描述器的抽象实现。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public abstract class AbstractCommandDescriptor implements CommandDescriptor {

    protected Context context;

    @Override
    public void init(Context context) {
        this.context = context;
    }

    @Override
    public String getDescription() throws CommandDescriptorException {
        try {
            return doGetDescription();
        } catch (CommandDescriptorException e) {
            throw e;
        } catch (Exception e) {
            throw new CommandDescriptorException(e);
        }
    }

    /**
     * 获取指令的简短描述。
     *
     * @return 指令的简短描述。
     * @throws Exception 方法执行过程中发生的任何异常。
     */
    protected abstract String doGetDescription() throws Exception;

    @Override
    public String getManual() throws CommandDescriptorException {
        try {
            return doGetManual();
        } catch (CommandDescriptorException e) {
            throw e;
        } catch (Exception e) {
            throw new CommandDescriptorException(e);
        }
    }

    /**
     * 获取指令的详细帮助。
     *
     * @return 指令的详细帮助。
     * @throws Exception 方法执行过程中发生的任何异常。
     */
    protected abstract String doGetManual() throws Exception;

    @Override
    public String toString() {
        return "AbstractCommandDescriptor{" +
                "context=" + context +
                '}';
    }
}
