package com.dwarfeng.springtelqos.api.integration.dubbo;

import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.stack.command.Context;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.qos.command.BaseCommand;
import org.apache.dubbo.qos.command.CommandContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Dubbo 命令。
 *
 * @author DwArFeng
 * @since 1.1.0
 */
public class DubboCommand extends CliCommand {

    private static final String IDENTITY = "dubbo";
    private static final String DESCRIPTION = "分布式服务上线/下线";
    private static final String CMD_LINE_SYNTAX_ONLINE = "dubbo -online [service-name]";
    private static final String CMD_LINE_SYNTAX_OFFLINE = "dubbo -offline [service-name]";
    private static final String CMD_LINE_SYNTAX_LIST = "dubbo -ls";
    private static final String CMD_LINE_SYNTAX = CMD_LINE_SYNTAX_ONLINE + System.lineSeparator() +
            CMD_LINE_SYNTAX_OFFLINE + System.lineSeparator() + CMD_LINE_SYNTAX_LIST;

    private static final BaseCommand DUBBO_COMMAND_ONLINE =
            ExtensionLoader.getExtensionLoader(BaseCommand.class).getExtension("online");
    private static final BaseCommand DUBBO_COMMAND_OFFLINE =
            ExtensionLoader.getExtensionLoader(BaseCommand.class).getExtension("offline");
    private static final BaseCommand DUBBO_COMMAND_LS =
            ExtensionLoader.getExtensionLoader(BaseCommand.class).getExtension("ls");

    private static final String OK_MESSAGE = "OK";

    public DubboCommand() {
        super(IDENTITY, DESCRIPTION, CMD_LINE_SYNTAX);
    }

    @Override
    protected List<Option> buildOptions() {
        List<Option> list = new ArrayList<>();
        list.add(Option.builder("online").optionalArg(true).hasArg(true).desc("上线服务").build());
        list.add(Option.builder("offline").optionalArg(true).hasArg(true).desc("下线服务").build());
        list.add(Option.builder("ls").optionalArg(true).hasArg(false).desc("列出服务").build());
        return list;
    }

    @Override
    protected void executeWithCmd(Context context, CommandLine cmd) throws TelqosException {
        try {
            Pair<String, Integer> pair = analyseCommand(cmd);
            if (pair.getRight() != 1) {
                context.sendMessage("下列选项必须且只能含有一个: -online -offline -ls");
                context.sendMessage(CMD_LINE_SYNTAX);
                return;
            }
            switch (pair.getLeft()) {
                case "online":
                    handleOnline(context, cmd);
                    break;
                case "offline":
                    handleOffline(context, cmd);
                    break;
                case "ls":
                    handleLs(context);
                    break;
            }
        } catch (Exception e) {
            throw new TelqosException(e);
        }
    }

    private void handleOnline(Context context, CommandLine cmd) throws Exception {
        String optionValue = cmd.getOptionValue("online");
        if (StringUtils.isEmpty(optionValue)) optionValue = ".*";
        String[] args = new String[]{optionValue};
        CommandContext commandContext = new CommandContext("online", args, false);
        String result = DUBBO_COMMAND_ONLINE.execute(commandContext, args);
        if (!result.equals(OK_MESSAGE)) context.sendMessage(result);
    }

    private void handleOffline(Context context, CommandLine cmd) throws Exception {
        String optionValue = cmd.getOptionValue("offline");
        if (StringUtils.isEmpty(optionValue)) optionValue = ".*";
        String[] args = new String[]{optionValue};
        CommandContext commandContext = new CommandContext("offline", args, false);
        String result = DUBBO_COMMAND_OFFLINE.execute(commandContext, args);
        if (!result.equals(OK_MESSAGE)) context.sendMessage(result);
    }

    private void handleLs(Context context) throws Exception {
        String[] args = new String[0];
        CommandContext commandContext = new CommandContext("ls", args, false);
        String result = DUBBO_COMMAND_LS.execute(commandContext, args);
        // 去除多余的换行符。
        result = result.substring(0, result.length() - System.lineSeparator().length());
        context.sendMessage(result);
    }

    private Pair<String, Integer> analyseCommand(CommandLine cmd) {
        int i = 0;
        String subCmd = null;
        if (cmd.hasOption("online")) {
            i++;
            subCmd = "online";
        }
        if (cmd.hasOption("offline")) {
            i++;
            subCmd = "offline";
        }
        if (cmd.hasOption("ls")) {
            i++;
            subCmd = "ls";
        }
        return Pair.of(subCmd, i);
    }
}
