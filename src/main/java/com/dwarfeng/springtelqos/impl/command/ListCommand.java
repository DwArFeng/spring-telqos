package com.dwarfeng.springtelqos.impl.command;

import com.dwarfeng.springtelqos.stack.command.Cio;
import com.dwarfeng.springtelqos.stack.command.Command;
import com.dwarfeng.springtelqos.stack.exception.ConnectionTerminatedException;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
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
    public Object execute(
            TelqosService telqosService, String address, Cio cio, String option) throws TelqosException, ConnectionTerminatedException {
        List<Command> commands = new ArrayList<>(telqosService.getCommands());
        commands.sort(CommandIdentityComparator.INSTANCE);
        for (Command command : commands) {
            String sb = command.getIdentify() + "\t" + command.getDescription();
            cio.send(sb);
        }
        cio.send("随便说点啥。。。");
        String receive = cio.receive();
        cio.send("你刚才说的是: " + receive);
        return null;
    }

    private static final class CommandIdentityComparator implements Comparator<Command> {

        public static final CommandIdentityComparator INSTANCE = new CommandIdentityComparator();

        @Override
        public int compare(Command o1, Command o2) {
            return o1.getIdentify().compareTo(o2.getIdentify());
        }
    }
}
