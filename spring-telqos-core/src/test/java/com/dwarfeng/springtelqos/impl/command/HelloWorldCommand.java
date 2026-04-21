package com.dwarfeng.springtelqos.impl.command;

import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.stack.command.CommandExecutor;
import org.apache.commons.cli.CommandLine;
import org.springframework.stereotype.Component;

/**
 * Hello World 指令。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
@Component
public class HelloWorldCommand extends CliCommand {

    @SuppressWarnings({"SpellCheckingInspection", "GrazieInspectionRunner", "RedundantSuppression"})
    public static final String IDENTITY = "hello";

    private static final String OUTPUT_MESSAGE = "Hello World!";

    public HelloWorldCommand() {
        super(IDENTITY);
    }

    @Override
    protected DescriptionProvider provideDescriptionProvider() {
        return ctx -> "输出 Hello World!";
    }

    @Override
    protected void executeWithCmd(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        // 向通道发送问候消息。
        context.sendMessage(OUTPUT_MESSAGE);
    }
}
