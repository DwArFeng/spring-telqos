package com.dwarfeng.springtelqos.api.integration.springterminator;

import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.stack.command.Context;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import com.dwarfeng.springterminator.stack.handler.Terminator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 关闭/重启程序命令。
 *
 * @author DwArFeng
 * @since 1.1.0
 */
public class ShutdownCommand extends CliCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownCommand.class);

    private static final String IDENTITY = "shutdown";
    private static final String DESCRIPTION = "关闭/重启程序";
    private static final String CMD_LINE_SYNTAX = "shutdown [-s/-r] [-e exit-code] [-c comment]";

    public ShutdownCommand() {
        super(IDENTITY, DESCRIPTION, CMD_LINE_SYNTAX);
    }

    private Terminator terminator;
    private boolean restartEnabled = true;

    @Override
    protected List<Option> buildOptions() {
        List<Option> list = new ArrayList<>();
        list.add(Option.builder("e").optionalArg(true).type(Number.class).hasArg(true)
                .argName("exit-code").desc("退出代码").build());
        list.add(Option.builder("c").optionalArg(true).type(String.class).hasArg(true)
                .argName("comment").desc("备注").build());
        list.add(Option.builder("s").optionalArg(true).desc("退出程序").build());
        list.add(Option.builder("r").optionalArg(true).desc("重启程序").build());
        return list;
    }

    @Override
    protected void executeWithCmd(Context context, CommandLine cmd) throws TelqosException {
        try {
            // 判断命令行非法情形。
            if (cmd.hasOption("s") && cmd.hasOption("r")) {
                context.sendMessage("命令行非法: -s 和 -r 不能同时存在，正确的格式为");
                context.sendMessage(CMD_LINE_SYNTAX);
                return;
            }
            if (!restartEnabled && cmd.hasOption("r")) {
                context.sendMessage("-r 选项已被禁用");
                return;
            }

            // 解析参数。
            int exitCode = 0;
            String comment = null;
            boolean restartFlag = false;
            if (cmd.hasOption("e")) {
                exitCode = ((Number) cmd.getParsedOptionValue("e")).intValue();
            }
            if (cmd.hasOption("c")) {
                comment = (String) cmd.getParsedOptionValue("c");
            }
            if (cmd.hasOption("r")) {
                restartFlag = true;
            }
            // 二次确认。
            boolean confirmFlag;
            a:
            do {
                context.sendMessage("服务将会关闭，您可能需要登录远程主机才能重新启动该服务，是否继续? Y/N");
                String confirmMessage = context.receiveMessage();
                switch (confirmMessage.toUpperCase()) {
                    case "Y":
                        confirmFlag = true;
                        break a;
                    case "N":
                        confirmFlag = false;
                        break a;
                    default:
                        context.sendMessage("输入信息非法，请输入 Y 或者 N");
                        break;
                }
            } while (true);
            // 判断确认结果以及执行关闭动作。
            if (confirmFlag) {
                context.sendMessage("已确认请求，服务即将关闭...");
                if (StringUtils.isEmpty(comment)) {
                    LOGGER.warn("设备 {} 通过 QOS 系统关闭了该服务，退出代码设置为 {}，备注未填",
                            context.getAddress(), exitCode);
                } else {
                    LOGGER.warn("设备 {} 通过 QOS 系统关闭了该服务，退出代码设置为 {}，备注为 {}",
                            context.getAddress(), exitCode, comment);
                }
                context.quit();
                // 根据标记退出或重启程序。
                if (restartFlag) {
                    terminator.exitAndRestart(exitCode);
                } else {
                    terminator.exit(exitCode);
                }
            } else {
                context.sendMessage("已确认请求，服务不会不关闭...");
            }
        } catch (Exception e) {
            throw new TelqosException(e);
        }
    }

    public Terminator getTerminator() {
        return terminator;
    }

    public void setTerminator(Terminator terminator) {
        this.terminator = terminator;
    }

    public boolean isRestartEnabled() {
        return restartEnabled;
    }

    public void setRestartEnabled(boolean restartEnabled) {
        this.restartEnabled = restartEnabled;
    }
}
