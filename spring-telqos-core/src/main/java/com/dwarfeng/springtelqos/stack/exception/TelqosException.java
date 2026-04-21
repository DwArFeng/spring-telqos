package com.dwarfeng.springtelqos.stack.exception;

import com.dwarfeng.subgrade.stack.exception.HandlerException;

/**
 * Telqos 异常。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class TelqosException extends HandlerException {

    private static final long serialVersionUID = -3687454851223175408L;

    public TelqosException() {
    }

    public TelqosException(String message) {
        super(message);
    }

    public TelqosException(String message, Throwable cause) {
        super(message, cause);
    }

    public TelqosException(Throwable cause) {
        super(cause);
    }
}
