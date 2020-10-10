package com.dwarfeng.springtelqos.sdk.command;

/**
 * 基础指令。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public abstract class BasicCommand extends AbstractCommand {

    protected final String description;
    protected final String manual;

    public BasicCommand(String identity, String description, String manual) {
        super(identity);
        this.description = description;
        this.manual = manual;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getManual() {
        return manual;
    }
}
