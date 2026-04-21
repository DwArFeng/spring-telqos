package com.dwarfeng.springtelqos.stack.exception;

import com.dwarfeng.subgrade.stack.exception.HandlerException;

/**
 * 指令异常。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class CommandException extends HandlerException {

    private static final long serialVersionUID = -4838062480230393206L;

    public CommandException() {
    }

    public CommandException(String message) {
        super(message);
    }

    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandException(Throwable cause) {
        super(cause);
    }
}
