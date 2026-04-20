package com.dwarfeng.springtelqos.stack.command;

import com.dwarfeng.springtelqos.stack.exception.CommandExecutorException;

import java.util.List;

/**
 * 指令执行器。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public interface CommandExecutor {

    /**
     * 初始化指令执行器。
     *
     * <p>
     * 该方法会在指令执行器初始化后调用，请将 context 存放在指令执行器的字段中。<br>
     * 当指令执行器被触发后，执行上下文中的相应方法即可。
     *
     * @param context 指令执行器的上下文。
     */
    void init(Context context);

    /**
     * 执行指令。
     *
     * @throws CommandExecutorException 指令执行器异常。
     */
    void execute() throws CommandExecutorException;

    /**
     * 指令执行器上下文。
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

        /**
         * 获取指令的运行时标识。
         *
         * <p>
         * 指令的运行时标识是指，在指令被注册后，由指令的标识与命名策略共同作用生成的标识。<br>
         * 如果命名策略为本征命名策略，则指令的运行时标识与指令的标识相同。
         *
         * @return 指令的运行时标识。
         * @throws Exception 方法执行过程中发生的任何异常。
         */
        String getRuntimeIdentity() throws Exception;

        /**
         * 获取执行指令的客户端地址。
         *
         * @return 客户端地址。
         * @throws Exception 方法执行过程中发生的任何异常。
         */
        String getAddress() throws Exception;

        /**
         * 获取指令执行的选项。
         *
         * @return 指令执行的选项。
         * @throws Exception 方法执行过程中发生的任何异常。
         */
        String getOption() throws Exception;

        /**
         * 列出指令的运行时标识。
         *
         * @return 所有指令的标识组成的列表。
         * @throws Exception 方法执行过程中发生的任何异常。
         */
        List<String> getCommandRuntimeIdentities() throws Exception;

        /**
         * 获取指令的描述。
         *
         * @param runtimeIdentity 指定指令的运行时标识符。
         * @return 指令的描述。
         * @throws Exception 方法执行过程中发生的任何异常。
         */
        String getCommandDescription(String runtimeIdentity) throws Exception;

        /**
         * 获取指令的详细说明。
         *
         * @param runtimeIdentity 指定指令的运行时标识符。
         * @return 指令的详细说明。
         * @throws Exception 方法执行过程中发生的任何异常。
         */
        String getCommandManual(String runtimeIdentity) throws Exception;

        /**
         * 向客户端发送一条信息（换行）。
         *
         * @param message 指定的信息。
         * @throws Exception 方法执行过程中发生的任何异常。
         */
        void sendMessage(String message) throws Exception;

        /**
         * 接收用户的输入信息，并在输入之前一直阻塞。
         *
         * @return 用户输入的信息。
         * @throws Exception 方法执行过程中发生的任何异常。
         */
        String receiveMessage() throws Exception;

        /**
         * 退出。
         *
         * @throws Exception 方法执行过程中发生的任何异常。
         */
        void quit() throws Exception;
    }
}
