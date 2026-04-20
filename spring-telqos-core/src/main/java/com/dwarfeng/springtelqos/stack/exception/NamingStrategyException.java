package com.dwarfeng.springtelqos.stack.exception;

import com.dwarfeng.subgrade.stack.exception.HandlerException;

/**
 * 命名策略异常。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class NamingStrategyException extends HandlerException {

    private static final long serialVersionUID = 8178270731820159354L;

    public NamingStrategyException() {
    }

    public NamingStrategyException(String message) {
        super(message);
    }

    public NamingStrategyException(String message, Throwable cause) {
        super(message, cause);
    }

    public NamingStrategyException(Throwable cause) {
        super(cause);
    }
}
