package com.dwarfeng.springtelqos.impl.command;

import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.stack.command.Context;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import org.apache.commons.cli.CommandLine;

/**
 * 退出指令。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class QuitCommand extends CliCommand {

    private static final String IDENTITY = "quit";
    private static final String DESCRIPTION = "退出";
    private static final String CMD_LINE_SYNTAX = "quit";

    public QuitCommand() {
        super(IDENTITY, DESCRIPTION, CMD_LINE_SYNTAX);
    }

    @Override
    protected void executeWithCmd(Context context, CommandLine cmd) throws TelqosException {
        context.sendMessage("Bye");
        context.quit();
    }
}
