package com.dwarfeng.springtelqos.impl.command;

import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.sdk.configuration.TelqosCommand;
import com.dwarfeng.springtelqos.stack.command.CommandExecutor;
import org.apache.commons.cli.CommandLine;

/**
 * 包扫描演示指令。
 *
 * @author DwArFeng
 * @since 1.1.7
 */
@TelqosCommand
public class PackageScanDemoCommand extends CliCommand {

    @SuppressWarnings({"SpellCheckingInspection", "GrazieInspectionRunner", "RedundantSuppression"})
    public static final String IDENTITY = "pkgscan";

    private static final String OUTPUT_MESSAGE = "当您看到这条消息时，说明您已经成功使用了包扫描功能。";

    public PackageScanDemoCommand() {
        super(IDENTITY);
    }

    @Override
    protected DescriptionProvider provideDescriptionProvider() {
        return ctx -> "使用包扫描功能加载的指令";
    }

    @Override
    protected void executeWithCmd(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        // 向通道发送包扫描演示说明消息。
        context.sendMessage(OUTPUT_MESSAGE);
    }
}
