package com.dwarfeng.springtelqos.stack.service;

import com.dwarfeng.springtelqos.stack.command.Command;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;

/**
 * Telqos Telnet 服务器。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public interface TelqosService {

    /**
     * 返回服务是否在线。
     *
     * @return 服务是否在线。
     */
    boolean isOnline();

    /**
     * 上线服务。
     *
     * @throws TelqosException Telqos异常。
     */
    void online() throws TelqosException;

    /**
     * 下线服务。
     *
     * @throws TelqosException Telqos异常。
     */
    void offline() throws TelqosException;

    /**
     * 注册指令。
     *
     * @param command 指定的指令。
     * @throws TelqosException Telqos异常。
     */
    void registerCommand(Command command) throws TelqosException;

    /**
     * 解除注册指令。
     *
     * @param identity 指令标识。
     * @throws TelqosException Telqos异常。
     */
    void unregisterCommand(String identity) throws TelqosException;
}
