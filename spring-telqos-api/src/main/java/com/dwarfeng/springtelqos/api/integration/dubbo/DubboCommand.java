package com.dwarfeng.springtelqos.api.integration.dubbo;

import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.sdk.configuration.TelqosCommand;
import com.dwarfeng.springtelqos.sdk.util.CliCommandUtil;
import com.dwarfeng.springtelqos.stack.command.CommandDescriptor;
import com.dwarfeng.springtelqos.stack.command.CommandExecutor;
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
 * Dubbo 指令。
 *
 * @author DwArFeng
 * @since 1.1.0
 */
@TelqosCommand
public class DubboCommand extends CliCommand {

    @SuppressWarnings({"SpellCheckingInspection", "GrazieInspectionRunner", "RedundantSuppression"})
    private static final String IDENTITY = "dubbo";

    // region 指令选项

    private static final String COMMAND_OPTION_ONLINE = "online";
    private static final String COMMAND_OPTION_OFFLINE = "offline";
    private static final String COMMAND_OPTION_LIST = "ls";

    private static final String[] COMMAND_OPTION_ARRAY = new String[]{
            COMMAND_OPTION_ONLINE,
            COMMAND_OPTION_OFFLINE,
            COMMAND_OPTION_LIST
    };

    // endregion

    private static final BaseCommand DUBBO_COMMAND_ONLINE =
            ExtensionLoader.getExtensionLoader(BaseCommand.class).getExtension("online");
    private static final BaseCommand DUBBO_COMMAND_OFFLINE =
            ExtensionLoader.getExtensionLoader(BaseCommand.class).getExtension("offline");
    private static final BaseCommand DUBBO_COMMAND_LS =
            ExtensionLoader.getExtensionLoader(BaseCommand.class).getExtension("ls");

    private static final String OK_MESSAGE = "OK";

    public DubboCommand() {
        super(IDENTITY);
    }

    @Override
    protected DescriptionProvider provideDescriptionProvider() {
        return ctx -> "分布式服务上线/下线";
    }

    @Override
    protected CliSyntaxProvider provideCliSyntaxProvider() {
        return this::cliSyntaxProvider;
    }

    private String cliSyntaxProvider(CommandDescriptor.Context context) throws Exception {
        final String[] patterns = new String[]{
                context.getRuntimeIdentity() + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_ONLINE) +
                        " [service-name]",
                context.getRuntimeIdentity() + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_OFFLINE) +
                        " [service-name]",
                context.getRuntimeIdentity() + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_LIST)
        };
        return CliCommandUtil.cliSyntax(patterns);
    }

    @Override
    protected List<Option> provideOptions() {
        List<Option> list = new ArrayList<>();
        list.add(Option.builder(COMMAND_OPTION_ONLINE).optionalArg(true).hasArg(true).desc("上线服务").build());
        list.add(Option.builder(COMMAND_OPTION_OFFLINE).optionalArg(true).hasArg(true).desc("下线服务").build());
        list.add(Option.builder(COMMAND_OPTION_LIST).optionalArg(true).hasArg(false).desc("列出服务").build());
        return list;
    }

    @Override
    protected void executeWithCmd(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        Pair<String, Integer> pair = CliCommandUtil.analyseCommand(cmd, COMMAND_OPTION_ARRAY);
        if (pair.getRight() != 1) {
            context.sendMessage(CliCommandUtil.optionMismatchMessage(COMMAND_OPTION_ARRAY));
            context.sendMessage(context.getCommandManual(context.getRuntimeIdentity()));
            return;
        }
        switch (pair.getLeft()) {
            case COMMAND_OPTION_ONLINE:
                handleOnline(context, cmd);
                break;
            case COMMAND_OPTION_OFFLINE:
                handleOffline(context, cmd);
                break;
            case COMMAND_OPTION_LIST:
                handleLs(context);
                break;
            default:
                throw new IllegalStateException("不应该执行到此处, 请联系开发人员");
        }
    }

    private void handleOnline(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        String optionValue = cmd.getOptionValue("online");
        if (StringUtils.isEmpty(optionValue)) optionValue = ".*";
        String[] args = new String[]{optionValue};
        CommandContext commandContext = new CommandContext("online", args, false);
        String result = DUBBO_COMMAND_ONLINE.execute(commandContext, args);
        if (!result.equals(OK_MESSAGE)) context.sendMessage(result);
    }

    private void handleOffline(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        String optionValue = cmd.getOptionValue("offline");
        if (StringUtils.isEmpty(optionValue)) optionValue = ".*";
        String[] args = new String[]{optionValue};
        CommandContext commandContext = new CommandContext("offline", args, false);
        String result = DUBBO_COMMAND_OFFLINE.execute(commandContext, args);
        if (!result.equals(OK_MESSAGE)) context.sendMessage(result);
    }

    private void handleLs(CommandExecutor.Context context) throws Exception {
        String[] args = new String[0];
        CommandContext commandContext = new CommandContext("ls", args, false);
        String result = DUBBO_COMMAND_LS.execute(commandContext, args);
        // 去除多余的换行符。
        result = result.substring(0, result.length() - System.lineSeparator().length());
        context.sendMessage(result);
    }
}
