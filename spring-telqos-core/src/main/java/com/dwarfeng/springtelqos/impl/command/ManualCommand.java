package com.dwarfeng.springtelqos.impl.command;

import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.stack.command.Context;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 显示指令的详细帮助。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class ManualCommand extends CliCommand {

    private static final String IDENTITY = "man";
    private static final String DESCRIPTION = "显示指令的详细信息";
    private static final String CMD_LINE_SYNTAX = "man [command]";

    public ManualCommand() {
        super(IDENTITY, DESCRIPTION, CMD_LINE_SYNTAX);
    }

    @Override
    protected List<Option> buildOptions() {
        return Collections.emptyList();
    }

    @Override
    protected void executeWithCmd(Context context, CommandLine cmd) throws TelqosException {
        String identity = cmd.getArgList().stream().findFirst().orElse(null);
        if (StringUtils.isEmpty(identity)) {
            context.sendMessage(getManual());
            return;
        }
        String manual = context.getCommandManual(identity);
        if (StringUtils.isEmpty(manual)) {
            context.sendMessage("未能找到指令 " + identity + " 的详细帮助");
            return;
        }
        context.sendMessage(manual);
    }
}
