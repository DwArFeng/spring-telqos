package com.dwarfeng.springtelqos.stack.service;

import com.dwarfeng.springtelqos.stack.command.Command;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;

import java.util.Collection;

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

    Collection<Command> getCommands();

    void registerCommand(Command command) throws TelqosException;

    void unregisterCommand(String identity) throws TelqosException;

    Command getCommand(String identity) throws TelqosException;

    Collection<String> getAddresses();

    void kick(String address) throws TelqosException;
}
