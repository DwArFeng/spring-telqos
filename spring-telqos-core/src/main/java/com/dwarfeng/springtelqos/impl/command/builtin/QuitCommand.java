package com.dwarfeng.springtelqos.impl.command.builtin;

import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.sdk.util.Constants;
import com.dwarfeng.springtelqos.stack.command.Command;
import com.dwarfeng.springtelqos.stack.command.CommandExecutor;
import org.apache.commons.cli.CommandLine;

/**
 * 退出指令。
 *
 * @author DwArFeng
 * @since 1.2.0
 */
public class QuitCommand extends CliCommand {

    public static final Command INSTANCE = new QuitCommand();

    private QuitCommand() {
        super(Constants.COMMAND_IDENTITY_QUIT);
    }

    @Override
    protected DescriptionProvider provideDescriptionProvider() {
        return ctx -> "退出";
    }

    @Override
    protected void executeWithCmd(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        context.sendMessage("Bye");
        context.quit();
    }
}
