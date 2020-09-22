package com.dwarfeng.springtelqos.stack.command;

import com.dwarfeng.springtelqos.stack.exception.ConnectionTerminatedException;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import com.dwarfeng.springtelqos.stack.service.TelqosService;

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
     * @param telqosService telqos服务上下文。
     * @param address       执行指令的客户端地址。
     * @param cio           命令IO，用于交互。
     * @param option        指令的选项。
     * @return 指令返回的结果。
     * @throws TelqosException               Telqos异常。
     * @throws ConnectionTerminatedException 连接中断异常。
     */
    Object execute(
            TelqosService telqosService, String address, Cio cio, String option)
            throws TelqosException, ConnectionTerminatedException;
}
