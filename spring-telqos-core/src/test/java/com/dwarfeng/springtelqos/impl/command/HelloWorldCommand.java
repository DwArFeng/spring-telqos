package com.dwarfeng.springtelqos.impl.command;

import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.stack.command.Context;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class HelloWorldCommand extends CliCommand {

    private static final String IDENTITY = "hello";
    private static final String DESCRIPTION = "输出 Hello World!";
    private static final String CMD_LINE_SYNTAX = "hello";

    public HelloWorldCommand() {
        super(IDENTITY, DESCRIPTION, CMD_LINE_SYNTAX);
    }

    @Override
    protected List<Option> buildOptions() {
        return Collections.emptyList();
    }

    @Override
    protected void executeWithCmd(Context context, CommandLine cmd) throws TelqosException {
        context.sendMessage("Hello World!");
    }
}
