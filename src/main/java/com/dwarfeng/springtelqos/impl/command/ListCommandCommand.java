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
    private static final String CMD_LINE_SYNTAX = "lc [-pprefix|--prefix prefix]";

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
        int i = 1;
        for (String identity : identities) {
            context.sendMessage((i++) + ".\t" + identity + "\t" + context.getCommandDescription(identity));
        }
        context.sendMessage("----------------------");
        context.sendMessage("共 " + identities.size() + " 条");
    }
}
