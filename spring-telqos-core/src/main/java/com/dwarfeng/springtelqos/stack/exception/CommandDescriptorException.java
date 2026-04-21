package com.dwarfeng.springtelqos.stack.exception;

/**
 * 指令描述器异常。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class CommandDescriptorException extends CommandException {

    private static final long serialVersionUID = 932128099306715186L;

    public CommandDescriptorException() {
    }

    public CommandDescriptorException(String message) {
        super(message);
    }

    public CommandDescriptorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandDescriptorException(Throwable cause) {
        super(cause);
    }
}
