package com.dwarfeng.springtelqos.stack.exception;

/**
 * 指令执行器异常。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class CommandExecutorException extends CommandException {

    private static final long serialVersionUID = -5104330174671143920L;

    public CommandExecutorException() {
    }

    public CommandExecutorException(String message) {
        super(message);
    }

    public CommandExecutorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandExecutorException(Throwable cause) {
        super(cause);
    }
}
