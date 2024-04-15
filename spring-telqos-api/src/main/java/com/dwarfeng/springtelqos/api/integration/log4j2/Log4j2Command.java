package com.dwarfeng.springtelqos.api.integration.log4j2;

import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.stack.command.Context;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.ArrayList;
import java.util.List;

/**
 * Log4j2 命令。
 *
 * @author DwArFeng
 * @since 1.1.9
 */
public class Log4j2Command extends CliCommand {

    private static final String IDENTITY = "log4j2";
    private static final String DESCRIPTION = "Log4j2 命令";

    private static final String COMMAND_OPTION_RECONFIGURE = "reconfigure";

    private static final String CMD_LINE_SYNTAX_RECONFIGURE = IDENTITY + " -" + COMMAND_OPTION_RECONFIGURE;

    private static final String CMD_LINE_SYNTAX = CMD_LINE_SYNTAX_RECONFIGURE;

    public Log4j2Command() {
        super(IDENTITY, DESCRIPTION, CMD_LINE_SYNTAX);
    }

    @Override
    protected List<Option> buildOptions() {
        List<Option> list = new ArrayList<>();
        list.add(Option.builder(COMMAND_OPTION_RECONFIGURE).desc("重新配置 Log4j2").build());
        return list;
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @Override
    protected void executeWithCmd(Context context, CommandLine cmd) throws TelqosException {
        try {
            Pair<String, Integer> pair = analyseCommand(cmd);
            if (pair.getRight() != 1) {
                context.sendMessage("下列选项必须且只能含有一个: " + COMMAND_OPTION_RECONFIGURE);
                context.sendMessage(CMD_LINE_SYNTAX);
                return;
            }
            switch (pair.getLeft()) {
                case COMMAND_OPTION_RECONFIGURE:
                    handleReconfigure(context);
                    break;
            }
        } catch (Exception e) {
            throw new TelqosException(e);
        }
    }

    private void handleReconfigure(Context context) throws Exception {
        Configurator.reconfigure();
        context.sendMessage("Log4j2 已重新配置!");
    }

    private Pair<String, Integer> analyseCommand(CommandLine cmd) {
        int i = 0;
        String subCmd = null;
        if (cmd.hasOption(COMMAND_OPTION_RECONFIGURE)) {
            i++;
            subCmd = COMMAND_OPTION_RECONFIGURE;
        }
        return Pair.of(subCmd, i);
    }
}
