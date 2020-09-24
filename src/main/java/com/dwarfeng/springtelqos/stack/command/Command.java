package com.dwarfeng.springtelqos.stack.command;

import com.dwarfeng.springtelqos.stack.exception.ConnectionTerminatedException;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;

/**
 * 指令。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public interface Command {

    /**
     * 获取命令的标识。
     *
     * @return 命令的标识。
     */
    String getIdentify();

    /**
     * 获取命令的简短描述。
     *
     * @return 命令的简短描述。
     */
    String getDescription();

    /**
     * 获取命令的详细帮助。
     *
     * @return 命令的详细帮助。
     */
    String getManual();

    /**
     * 执行指令。
     *
     * @param context 指令上下文。
     * @throws TelqosException               Telqos异常。
     * @throws ConnectionTerminatedException 连接中断异常。
     */
    void execute(Context context) throws TelqosException, ConnectionTerminatedException;
}
