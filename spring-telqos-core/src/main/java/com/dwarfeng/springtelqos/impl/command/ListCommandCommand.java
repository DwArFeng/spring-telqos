package com.dwarfeng.springtelqos.impl.command;

import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.stack.command.Context;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 列出所有指令的命令。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class ListCommandCommand extends CliCommand {

    private static final String IDENTITY = "lc";
    private static final String DESCRIPTION = "列出指令";
    private static final String CMD_LINE_SYNTAX = "lc [-p prefix|--prefix prefix]";

    public ListCommandCommand() {
        super(IDENTITY, DESCRIPTION, CMD_LINE_SYNTAX);
    }

    @Override
    protected List<Option> buildOptions() {
        List<Option> list = new ArrayList<>();
        list.add(Option.builder("p").longOpt("prefix").optionalArg(true).type(String.class)
                .argName("prefix").hasArg(true).desc("列出包含指定前缀的命令").build());
        return list;
    }

    @Override
    protected void executeWithCmd(Context context, CommandLine cmd) throws TelqosException {
        List<String> identities = context.getCommandIdentities();
        if (cmd.hasOption("p")) {
            String prefix = cmd.getOptionValue("p");
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
