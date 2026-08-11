package com.dwarfeng.springtelqos.impl.command.builtin;

import com.dwarfeng.springtelqos.core.internal.i18n.CoreMessageKey;
import com.dwarfeng.springtelqos.core.internal.i18n.CoreMessages;
import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.sdk.util.Constants;
import com.dwarfeng.springtelqos.stack.command.Command;
import com.dwarfeng.springtelqos.stack.command.CommandExecutor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.StringUtils;

/**
 * 显示指令的详细帮助。
 *
 * @author DwArFeng
 * @since 1.2.0
 */
public class ManualCommand extends CliCommand {

    public static final Command INSTANCE = new ManualCommand();

    private ManualCommand() {
        super(Constants.COMMAND_IDENTITY_MANUAL);
    }

    @Override
    protected DescriptionProvider provideDescriptionProvider() {
        return context -> CoreMessages.message(CoreMessageKey.COMMAND_MANUAL_DESCRIPTION);
    }

    @Override
    protected CliSyntaxProvider provideCliSyntaxProvider() {
        return context -> Constants.COMMAND_IDENTITY_MANUAL + " [command]";
    }

    @Override
    protected void executeWithCmd(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        String runtimeIdentity = cmd.getArgList().stream().findFirst().orElse(null);
        if (StringUtils.isEmpty(runtimeIdentity)) {
            context.sendMessage(context.getCommandManual(Constants.COMMAND_IDENTITY_MANUAL));
            return;
        }
        String manual = context.getCommandManual(runtimeIdentity);
        if (StringUtils.isEmpty(manual)) {
            context.sendMessage(CoreMessages.message(CoreMessageKey.COMMAND_MANUAL_NOT_FOUND, runtimeIdentity));
            return;
        }
        context.sendMessage(manual);
    }
}
