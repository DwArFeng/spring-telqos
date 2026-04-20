package com.dwarfeng.springtelqos.impl.command.builtin;

import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.sdk.util.CliCommandUtil;
import com.dwarfeng.springtelqos.sdk.util.Constants;
import com.dwarfeng.springtelqos.stack.command.Command;
import com.dwarfeng.springtelqos.stack.command.CommandExecutor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 列出所有指令的指令。
 *
 * @author DwArFeng
 * @since 1.2.0
 */
public class ListCommandCommand extends CliCommand {

    public static final Command INSTANCE = new ListCommandCommand();

    // region 指令选项

    private static final String COMMAND_SUB_OPTION_PREFIX = "p";

    // endregion

    private ListCommandCommand() {
        super(Constants.COMMAND_IDENTITY_LIST_COMMAND);
    }

    @Override
    protected DescriptionProvider provideDescriptionProvider() {
        return context -> "列出指令";
    }

    @Override
    protected CliSyntaxProvider provideCliSyntaxProvider() {
        return context -> Constants.COMMAND_IDENTITY_LIST_COMMAND + " [" +
                CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_PREFIX) + " prefix]";
    }

    @Override
    protected List<Option> provideOptions() {
        List<Option> list = new ArrayList<>();
        list.add(Option.builder(COMMAND_SUB_OPTION_PREFIX).optionalArg(true).type(String.class).hasArg(true)
                .desc("列出包含指定前缀的指令").build());
        return list;
    }

    @Override
    protected void executeWithCmd(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        List<String> identities = context.getCommandRuntimeIdentities();
        if (cmd.hasOption(COMMAND_SUB_OPTION_PREFIX)) {
            String prefix = cmd.getOptionValue(COMMAND_SUB_OPTION_PREFIX);
            identities = identities.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
        }
        int index = 0;
        int maxIdentityLength = 0;
        int maxDescriptionLength = 0;
        for (String identity : identities) {
            if (identity.length() > maxIdentityLength) maxIdentityLength = identity.length();
        }
        for (String identity : identities) {
            String description = String.format("%-3d %-" + (maxIdentityLength + 2) + "s %s",
                    ++index, identity, context.getCommandDescription(identity));
            if (description.length() > maxDescriptionLength) maxDescriptionLength = description.length();
            context.sendMessage(description);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < maxDescriptionLength; i++) {
            stringBuilder.append('-');
        }
        context.sendMessage(stringBuilder.toString());
        context.sendMessage("共 " + identities.size() + " 条");
    }
}
