package com.dwarfeng.springtelqos.sdk.command;

import com.dwarfeng.springtelqos.stack.command.Context;
import com.dwarfeng.springtelqos.stack.exception.ConnectionTerminatedException;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通过CLI框架实现的指令。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public abstract class CliCommand extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CliCommand.class);
    private static final Pattern PATTERN = Pattern.compile("\"([^\"]*?)\"|(\\S+)");

    protected final String description;
    protected final String cmdLineSyntax;
    private final Options options = new Options();

    public CliCommand(String identity, String description, String cmdLineSyntax) {
        super(identity);
        this.description = description;
        this.cmdLineSyntax = cmdLineSyntax;
        buildOptions().forEach(options::addOption);
        buildOptionGroups().forEach(options::addOptionGroup);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getManual() {
        HelpFormatter helpFormatter = new HelpFormatter();
        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
            helpFormatter.printHelp(pw, HelpFormatter.DEFAULT_WIDTH, cmdLineSyntax, getDescription(),
                    options, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
            return sw.toString();
        } catch (Exception e) {
            LOGGER.warn("生成详细帮助时发生异常，异常信息如下", e);
            return "发生错误，无法显示详细帮助";
        }
    }

    @Override
    public void execute(Context context) throws TelqosException, ConnectionTerminatedException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, option2Args(context.getOption()));
        } catch (Exception e) {
            LOGGER.warn("解析命令选项时发生异常，异常信息如下", e);
            context.sendMessage("命令选项不正确，请检查命令选项: " + context.getOption());
        }
        executeWithCmd(context, cmd);
    }

    private String[] option2Args(String option) {
        Matcher matcher = PATTERN.matcher(option);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list.toArray(new String[0]);
    }

    /**
     * 构建 Option 列表。
     *
     * <p>该方法默认返回空列表，如需要，请重写该方法。
     *
     * @return Option 组成的列表。
     */
    protected List<Option> buildOptions() {
        return Collections.emptyList();
    }

    /**
     * 构建 OptionGroup 列表。
     *
     * <p>该方法默认返回空列表，如需要，请重写该方法。
     *
     * @return OptionGroup 组成的列表。
     */
    protected List<OptionGroup> buildOptionGroups() {
        return Collections.emptyList();
    }

    /**
     * 将 {@link Context#getOption()} 解析为 {@link CommandLine} 之后，执行指令的抽象方法。
     *
     * @param context 指令上下文。
     * @param cmd     被解析的 CommandLine
     * @throws TelqosException               Telqos异常。
     * @throws ConnectionTerminatedException 连接中断异常。
     */
    protected abstract void executeWithCmd(Context context, CommandLine cmd) throws TelqosException, ConnectionTerminatedException;
}
