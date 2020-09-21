package com.dwarfeng.springtelqos.stack.exception;

/**
 * Telqos异常。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class TelqosException extends Exception {

    private static final long serialVersionUID = -1406171292595480908L;

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
