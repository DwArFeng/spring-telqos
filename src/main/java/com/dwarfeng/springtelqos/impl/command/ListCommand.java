package com.dwarfeng.springtelqos.impl.command;

import com.dwarfeng.springtelqos.stack.command.Command;
import com.dwarfeng.springtelqos.stack.service.TelqosService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 列出所有指令的命令。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class ListCommand implements Command {

    @Override
    public String getIdentify() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "列出所有指令";
    }

    @Override
    public String getManual() {
        return "";
    }

    @Override
    public Object execute(TelqosService telqosService, String address, Object[] params) {
        List<Command> commands = new ArrayList<>(telqosService.getCommands());
        commands.sort(CommandIdentityComparator.INSTANCE);
        StringBuilder sb = new StringBuilder();
        for (int i = 0, commandsSize = commands.size(); i < commandsSize; i++) {
            Command command = commands.get(i);
            sb
                    .append(command.getIdentify())
                    .append("\t")
                    .append(command.getDescription());
            if (i < commandsSize - 1) {
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    private static final class CommandIdentityComparator implements Comparator<Command> {

        public static final CommandIdentityComparator INSTANCE = new CommandIdentityComparator();

        @Override
        public int compare(Command o1, Command o2) {
            return o1.getIdentify().compareTo(o2.getIdentify());
        }
    }
}
