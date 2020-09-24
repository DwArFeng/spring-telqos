package com.dwarfeng.springtelqos.stack.command;

import com.dwarfeng.springtelqos.stack.exception.ConnectionTerminatedException;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;

import java.util.List;

/**
 * 命令上下文。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public interface Context {

    /**
     * 获取执行指令的客户端地址。
     *
     * @return 客户端地址。
     */
    String getAddress();

    /**
     * 获取指令执行的选项。
     *
     * @return 指令执行的选项。
     */
    String getOption();

    /**
     * 列出指令。
     *
     * @return 所有指令的标识符组成的列表。
     */
    List<String> getCommandIdentities();

    /**
     * 获取指令的描述。
     *
     * @param identity 指定指令的标识符。
     * @return 指令的描述。
     */
    String getCommandDescription(String identity);

    /**
     * 获取指令的详细说明。
     *
     * @param identity 指定指令的标识符。
     * @return 指令的详细说明。
     */
    String getCommandManual(String identity);

    /**
     * 向客户端发送一条信息（换行）。
     *
     * @param message 指定的信息。
     * @throws TelqosException Telqos异常。
     */
    void sendMessage(String message) throws TelqosException;

    /**
     * 接收用户的输入信息，并在输入之前一直阻塞。
     * <p>
     * 请注意：{@link Command#execute(Context)} 调用该方法时不能捕获 ConnectionTerminatedException 异常，必须抛出。
     *
     * @return 用户输入的信息。
     * @throws TelqosException               Telqos异常。
     * @throws ConnectionTerminatedException 在用户输入完成之前连接中断。
     */
    String receiveMessage() throws TelqosException, ConnectionTerminatedException;

    /**
     * 退出。
     *
     * @throws TelqosException Telqos异常。
     */
    void quit() throws TelqosException;
}
