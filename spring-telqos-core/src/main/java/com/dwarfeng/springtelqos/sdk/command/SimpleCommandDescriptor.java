package com.dwarfeng.springtelqos.sdk.command;

/**
 * 简单指令描述器。
 *
 * <p>
 * 一个简单的指令描述器，直接将描述和帮助信息存储在字段中。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class SimpleCommandDescriptor extends AbstractCommandDescriptor {

    private final String description;
    private final String manual;

    public SimpleCommandDescriptor(String description, String manual) {
        this.description = description;
        this.manual = manual;
    }

    @Override
    protected String doGetDescription() {
        return description;
    }

    @Override
    protected String doGetManual() {
        return manual;
    }

    @Override
    public String toString() {
        return "SimpleCommandDescriptor{" +
                "description='" + description + '\'' +
                ", manual='" + manual + '\'' +
                ", context=" + context +
                '}';
    }
}
