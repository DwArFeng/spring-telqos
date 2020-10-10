package com.dwarfeng.springtelqos.sdk.command;

import com.dwarfeng.springtelqos.stack.command.Command;

/**
 * 抽象指令。
 * <p>
 * 指令的抽象实现。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public abstract class AbstractCommand implements Command {

    protected final String identity;

    public AbstractCommand(String identity) {
        this.identity = identity;
    }

    @Override
    public String getIdentify() {
        return identity;
    }
}
