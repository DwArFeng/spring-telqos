package com.dwarfeng.springtelqos.stack.command;

import com.dwarfeng.springtelqos.stack.exception.CommandDescriptorException;

/**
 * 指令描述器。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public interface CommandDescriptor {

    /**
     * 初始化指令描述器。
     *
     * <p>
     * 该方法会在指令描述器初始化后调用，请将 context 存放在指令描述器的字段中。<br>
     * 当指令描述器被触发后，执行上下文中的相应方法即可。
     *
     * @param context 指令描述器的上下文。
     */
    void init(Context context);

    /**
     * 获取指令的简短描述。
     *
     * @return 指令的简短描述。
     * @throws CommandDescriptorException 指令描述器异常。
     */
    String getDescription() throws CommandDescriptorException;

    /**
     * 获取指令的详细帮助。
     *
     * @return 指令的详细帮助。
     * @throws CommandDescriptorException 指令描述器异常。
     */
    String getManual() throws CommandDescriptorException;

    /**
     * 指令描述器上下文。
     *
     * @author DwArFeng
     * @since 2.0.0
     */
    interface Context {

        /**
         * 获取指令的标识。
         *
         * @return 指令的标识。
         * @throws Exception 方法执行过程中发生的任何异常。
         */
        String getIdentity() throws Exception;
    }
}
