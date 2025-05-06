package com.dwarfeng.springtelqos.impl.command;

import com.dwarfeng.springtelqos.node.config.TelqosCommand;
import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.stack.command.Context;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import org.apache.commons.cli.CommandLine;

/**
 * 包扫描演示指令。
 *
 * @author DwArFeng
 * @since 1.1.7
 */
@TelqosCommand
public class PackageScanDemoCommand extends CliCommand {

    // 指令标识符，系专有术语，忽略相关的拼写检查。
    @SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
    private static final String IDENTITY = "pkgscan";
    private static final String DESCRIPTION = "使用包扫描功能加载的指令";
    // 指令标识符，系专有术语，忽略相关的拼写检查。
    @SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
    private static final String CMD_LINE_SYNTAX = "pkgscan";

    public PackageScanDemoCommand() {
        super(IDENTITY, DESCRIPTION, CMD_LINE_SYNTAX);
    }

    @Override
    protected void executeWithCmd(Context context, CommandLine cmd) throws TelqosException {
        context.sendMessage("当您看到这条消息时，说明您已经成功使用了包扫描功能。");
    }
}
