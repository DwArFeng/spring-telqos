package com.dwarfeng.springtelqos.stack.command;

import com.dwarfeng.springtelqos.stack.exception.ConnectionTerminatedException;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;

/**
 * 指令IO。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public interface Cio {

    void send(String message) throws TelqosException;

    String receive() throws TelqosException, ConnectionTerminatedException;
}
