package com.dwarfeng.springtelqos.sdk.command;

import com.dwarfeng.springtelqos.stack.command.CommandDescriptor;
import com.dwarfeng.springtelqos.stack.command.CommandExecutor;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 通过 CLI 框架实现的指令。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public abstract class CliCommand extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CliCommand.class);

    private final Lock lazyLock = new ReentrantLock();

    private DescriptionProvider descriptionProvider;
    private CliSyntaxProvider cliSyntaxProvider;
    private Options options;

    public CliCommand(String identity) {
        super(identity);
    }

    @Override
    protected CommandDescriptor doNewCommandDescriptor() {
        return new CliCommandDescriptor();
    }

    @Override
    protected CommandExecutor doNewCommandExecutor() {
        return new CliCommandExecutor();
    }

    // region 供子类覆盖的构建方法

    protected DescriptionProvider provideDescriptionProvider() {
        return CommandDescriptor.Context::getIdentity;
    }

    protected CliSyntaxProvider provideCliSyntaxProvider() {
        return CommandDescriptor.Context::getIdentity;
    }

    protected List<Option> provideOptions() {
        return Collections.emptyList();
    }

    protected List<OptionGroup> provideOptionGroups() {
        return Collections.emptyList();
    }

    /**
     * 将 {@link CommandExecutor.Context#getOption()} 解析为 {@link CommandLine} 之后，执行指令的抽象方法。
     *
     * @param cmd 已解析的指令行。
     * @throws Exception 方法执行过程中发生的任何异常。
     */
    protected abstract void executeWithCmd(CommandExecutor.Context context, CommandLine cmd) throws Exception;

    // endregion

    // region 懒加载

    private DescriptionProvider lazyGetDescriptionProvider() {
        if (Objects.isNull(descriptionProvider)) {
            lazyLock.lock();
            try {
                if (Objects.isNull(descriptionProvider)) {
                    descriptionProvider = provideDescriptionProvider();
                }
            } finally {
                lazyLock.unlock();
            }
        }
        return descriptionProvider;
    }

    private CliSyntaxProvider lazyGetCliSyntaxProvider() {
        if (Objects.isNull(cliSyntaxProvider)) {
            lazyLock.lock();
            try {
                if (Objects.isNull(cliSyntaxProvider)) {
                    cliSyntaxProvider = provideCliSyntaxProvider();
                }
            } finally {
                lazyLock.unlock();
            }
        }
        return cliSyntaxProvider;
    }

    private Options lazyGetOptions() {
        if (Objects.isNull(options)) {
            lazyLock.lock();
            try {
                if (Objects.isNull(options)) {
                    options = new Options();
                    for (Option option : provideOptions()) {
                        options.addOption(option);
                    }
                    for (OptionGroup optionGroup : provideOptionGroups()) {
                        options.addOptionGroup(optionGroup);
                    }
                }
            } finally {
                lazyLock.unlock();
            }
        }
        return options;
    }

    // endregion

    // region 内部接口与内部类

    /**
     * 提供指令描述的接口。
     *
     * @author DwArFeng
     * @since 2.0.0
     */
    protected interface DescriptionProvider {

        /**
         * 提供指令描述。
         *
         * @return 指令描述。
         * @throws Exception 方法执行过程中发生的任何异常。
         */
        String provideDescription(CommandDescriptor.Context context) throws Exception;
    }

    /**
     * 提供指令指令行语法的接口。
     *
     * @author DwArFeng
     * @since 2.0.0
     */
    protected interface CliSyntaxProvider {

        /**
         * 提供指令指令行语法。
         *
         * @return 指令指令行语法。
         * @throws Exception 方法执行过程中发生的任何异常。
         */
        String provideCliSyntax(CommandDescriptor.Context context) throws Exception;
    }

    /**
     * CliCommand 的 CommandDescriptor 实现。
     *
     * @author DwArFeng
     * @since 2.0.0
     */
    private final class CliCommandDescriptor extends AbstractCommandDescriptor {

        @Override
        protected String doGetDescription() throws Exception {
            return lazyGetDescriptionProvider().provideDescription(context);
        }

        @Override
        protected String doGetManual() throws Exception {
            String cliSyntax = lazyGetCliSyntaxProvider().provideCliSyntax(context);
            Options options = lazyGetOptions();
            HelpFormatter helpFormatter = new HelpFormatter();
            try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
                helpFormatter.printHelp(
                        pw, HelpFormatter.DEFAULT_WIDTH, cliSyntax, getDescription(),
                        options, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null
                );
                return sw.toString();
            } catch (Exception e) {
                LOGGER.warn("生成详细帮助时发生异常，异常信息如下", e);
                return "发生错误，无法显示详细帮助";
            }
        }
    }

    /**
     * CliCommand 的 CommandExecutor 实现。
     *
     * @author DwArFeng
     * @since 2.0.0
     */
    private final class CliCommandExecutor extends AbstractCommandExecutor {

        @Override
        protected void doExecute() throws Exception {
            Options options = lazyGetOptions();
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd;
            try {
                cmd = parser.parse(options, option2Args(context.getOption()));
            } catch (Exception e) {
                LOGGER.warn("解析指令选项时发生异常，异常信息如下", e);
                context.sendMessage("指令选项不正确，请检查指令选项: " + context.getOption());
                return;
            }
            executeWithCmd(context, cmd);
        }

        private String[] option2Args(String option) {
            boolean quoteFlag = false;
            List<Integer> spaceIndexes = new ArrayList<>();
            for (int i = 0; i < option.length(); i++) {
                char ch = option.charAt(i);
                if (ch == '\"') {
                    quoteFlag = !quoteFlag;
                }
                if (ch == ' ' && !quoteFlag) {
                    spaceIndexes.add(i);
                }
            }
            String[] args = new String[spaceIndexes.size() + 1];
            int j = 0;
            int beginIndex = 0;
            for (int endIndex : spaceIndexes) {
                args[j++] = option.substring(beginIndex, endIndex);
                beginIndex = endIndex + 1;
            }
            args[j] = option.substring(beginIndex);
            return args;
        }
    }

    // endregion
}
