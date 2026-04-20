package com.dwarfeng.springtelqos.impl.command.builtin;

import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.sdk.util.Constants;
import com.dwarfeng.springtelqos.stack.command.Context;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import org.apache.commons.cli.CommandLine;

/**
 * 退出指令。
 *
 * @author DwArFeng
 * @since 1.2.0
 */
public class QuitCommand extends CliCommand {

    public static final QuitCommand INSTANCE = new QuitCommand();

    private static final String DESCRIPTION = "退出";
    private static final String CMD_LINE_SYNTAX = Constants.COMMAND_QUIT;

    private QuitCommand() {
        super(Constants.COMMAND_QUIT, DESCRIPTION, CMD_LINE_SYNTAX);
    }

    @Override
    protected void executeWithCmd(Context context, CommandLine cmd) throws TelqosException {
        context.sendMessage("Bye");
        context.quit();
    }
}
