package com.dwarfeng.springtelqos.sdk.command;

import com.dwarfeng.springtelqos.stack.command.CommandDescriptor;
import com.dwarfeng.springtelqos.stack.command.CommandExecutor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CliCommand 单元测试。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
class CliCommandTest {

    @Test
    void generatesManualWithCurrentCommonsCliHelpFormatter() throws Exception {
        CliCommand command = new TestCliCommand();
        CommandDescriptor descriptor = command.newCommandDescriptor();
        descriptor.init(new CommandDescriptor.Context() {

            @Override
            public String getIdentity() {
                return "test-command";
            }

            @Override
            public String getRuntimeIdentity() {
                return "runtime-test-command";
            }
        });

        String manual = descriptor.getManual();

        assertAll(
                () -> assertTrue(manual.contains("runtime-test-command [-v]")),
                () -> assertTrue(manual.contains("Test command")),
                () -> assertTrue(manual.contains("-v")),
                () -> assertTrue(manual.contains("Display version")),
                () -> assertFalse(manual.contains("Since"))
        );
    }

    /**
     * 用于验证手册生成逻辑的测试命令。
     */
    private static final class TestCliCommand extends CliCommand {

        private TestCliCommand() {
            super("test-command");
        }

        @Override
        protected DescriptionProvider provideDescriptionProvider() {
            return context -> "Test command";
        }

        @Override
        protected CliSyntaxProvider provideCliSyntaxProvider() {
            return context -> context.getRuntimeIdentity() + " [-v]";
        }

        @Override
        protected List<Option> provideOptions() {
            return List.of(Option.builder("v").desc("Display version").get());
        }

        @Override
        protected void executeWithCmd(CommandExecutor.Context context, CommandLine cmd) {
        }
    }
}
