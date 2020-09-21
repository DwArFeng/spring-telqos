package com.dwarfeng.springtelqos.stack.command;

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
     * 命令执行。
     *
     * @param params 执行命令所需的参数。
     * @return 命令返回的结果。
     * @throws TelqosException Telqos异常。
     */
    Object execute(TelqosService telqosService, String address, Object[] params) throws TelqosException;
}
