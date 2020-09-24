package com.dwarfeng.springtelqos.impl.command;

import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.stack.command.Context;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.util.Collections;
import java.util.List;

/**
 * 复制变量的命令。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class CopyVariableCommand extends CliCommand {

    private static final String IDENTITY = "cpv";
    private static final String DESCRIPTION = "复制变量";
    private static final String CMD_LINE_SYNTAX = "cpv source target";

    public CopyVariableCommand() {
        super(IDENTITY, DESCRIPTION, CMD_LINE_SYNTAX);
    }

    @Override
    protected List<Option> buildOptions() {
        return Collections.emptyList();
    }

    @Override
    protected Object executeWithCmd(Context context, CommandLine cmd) throws TelqosException {
        List<String> argList = cmd.getArgList();
        if (argList.size() != 2) {
            context.sendMessage("命令格式错误，应该为: " + CMD_LINE_SYNTAX);
            return null;
        }
        String sourceIdentity = argList.get(0);
        String targetIdentity = argList.get(1);

        Object value = context.getVariable(sourceIdentity);
        context.setVariable(targetIdentity, value);
        context.sendMessage("成功");
        return value;
    }
}
