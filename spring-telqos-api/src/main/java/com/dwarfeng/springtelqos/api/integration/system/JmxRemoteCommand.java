package com.dwarfeng.springtelqos.api.integration.system;

import com.dwarfeng.springtelqos.api.internal.i18n.ApiMessageKey;
import com.dwarfeng.springtelqos.api.internal.i18n.ApiMessages;
import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.sdk.configuration.TelqosCommand;
import com.dwarfeng.springtelqos.sdk.util.CliCommandUtil;
import com.dwarfeng.springtelqos.stack.command.CommandDescriptor;
import com.dwarfeng.springtelqos.stack.command.CommandExecutor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * JMX 远程管理操作指令。
 *
 * @author DwArFeng
 * @since 1.1.15
 */
@TelqosCommand
public class JmxRemoteCommand extends CliCommand {

    @SuppressWarnings({"SpellCheckingInspection", "GrazieInspectionRunner", "RedundantSuppression"})
    private static final String IDENTITY = "jmxremote";

    private static final Logger LOGGER = LoggerFactory.getLogger(JmxRemoteCommand.class);

    // region 指令选项

    private static final String COMMAND_OPTION_START = "start";
    private static final String COMMAND_OPTION_STOP = "stop";
    private static final String COMMAND_OPTION_STATUS = "status";

    private static final String[] COMMAND_OPTION_ARRAY = new String[]{
            COMMAND_OPTION_START,
            COMMAND_OPTION_STOP,
            COMMAND_OPTION_STATUS
    };

    private static final String COMMAND_SUB_OPTION_PORT = "p";

    // endregion

    private static final int DEFAULT_PORT = 9999;

    private static final String SYS_PROP_JMX_PORT = "com.sun.management.jmxremote.port";

    private final ReentrantLock lock = new ReentrantLock();

    private JMXConnectorServer connectorServer;
    private Registry rmiRegistry;
    private int currentPort;

    public JmxRemoteCommand() {
        super(IDENTITY);
    }

    @Override
    protected DescriptionProvider provideDescriptionProvider() {
        return ctx -> ApiMessages.message(ApiMessageKey.COMMAND_JMX_DESCRIPTION);
    }

    @Override
    protected CliSyntaxProvider provideCliSyntaxProvider() {
        return this::cliSyntaxProvider;
    }

    private String cliSyntaxProvider(CommandDescriptor.Context context) throws Exception {
        final String[] patterns = new String[]{
                context.getRuntimeIdentity() + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_START) + " [" +
                        CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_PORT) + " port]",
                context.getRuntimeIdentity() + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_STOP),
                context.getRuntimeIdentity() + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_STATUS)
        };
        return CliCommandUtil.cliSyntax(patterns);
    }

    @Override
    protected List<Option> provideOptions() {
        List<Option> list = new ArrayList<>();
        list.add(Option.builder(COMMAND_OPTION_START)
                .desc(ApiMessages.message(ApiMessageKey.COMMAND_JMX_START_OPTION)).get());
        list.add(Option.builder(COMMAND_OPTION_STOP)
                .desc(ApiMessages.message(ApiMessageKey.COMMAND_JMX_STOP_OPTION)).get());
        list.add(Option.builder(COMMAND_OPTION_STATUS)
                .desc(ApiMessages.message(ApiMessageKey.COMMAND_JMX_STATUS_OPTION)).get());
        list.add(Option.builder(COMMAND_SUB_OPTION_PORT)
                .desc(ApiMessages.message(ApiMessageKey.COMMAND_JMX_PORT_OPTION)).hasArg().type(Number.class).get());
        return list;
    }

    @Override
    protected void executeWithCmd(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        Pair<String, Integer> pair = CliCommandUtil.analyseCommand(cmd, COMMAND_OPTION_ARRAY);
        if (pair.getRight() != 1) {
            context.sendMessage(CliCommandUtil.optionMismatchMessage(COMMAND_OPTION_ARRAY));
            context.sendMessage(context.getCommandManual(context.getRuntimeIdentity()));
            return;
        }
        switch (pair.getLeft()) {
            case COMMAND_OPTION_START:
                handleStart(context, cmd);
                break;
            case COMMAND_OPTION_STOP:
                handleStop(context);
                break;
            case COMMAND_OPTION_STATUS:
                handleStatus(context);
                break;
            default:
                throw new IllegalStateException("不应该执行到此处, 请联系开发人员");
        }
    }

    private void handleStart(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        lock.lock();
        try {
            handleStart0(context, cmd);
        } finally {
            lock.unlock();
        }
    }

    private void handleStart0(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        // 检查系统属性是否已启用 JMX 远程管理。
        if (isSystemPropertyEnabled()) {
            String portStr = System.getProperty(SYS_PROP_JMX_PORT);
            context.sendMessage(ApiMessages.message(ApiMessageKey.COMMAND_JMX_SYSTEM_PROPERTY_ENABLED,
                    SYS_PROP_JMX_PORT, portStr));
            context.sendMessage(ApiMessages.message(ApiMessageKey.COMMAND_JMX_NO_ACTION));
            return;
        }

        // 检查是否已经启动。
        if (Objects.nonNull(connectorServer) && connectorServer.isActive()) {
            context.sendMessage(ApiMessages.message(ApiMessageKey.COMMAND_JMX_ALREADY_RUNNING, currentPort));
            return;
        }

        // 解析主机和端口。
        int port = parsePort(context, cmd);

        try {
            // 创建 RMI 注册。
            rmiRegistry = LocateRegistry.createRegistry(port);

            // 创建 JMXServiceURL。
            @SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:" + port + "/jmxrmi");

            // 获取平台 MBeanServer。
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

            // 创建并启动 JMXConnectorServer。
            connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbeanServer);
            connectorServer.start();

            // 保存当前主机和端口。
            currentPort = port;

            context.sendMessage(ApiMessages.message(ApiMessageKey.COMMAND_JMX_STARTED));
            context.sendMessage(ApiMessages.message(ApiMessageKey.COMMAND_JMX_PORT, port));
            @SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
            String jmxServiceUrlDescription = ApiMessages.message(ApiMessageKey.COMMAND_JMX_SERVICE_URL,
                    "service:jmx:rmi:///jndi/rmi://:" + port + "/jmxrmi");
            context.sendMessage(jmxServiceUrlDescription);
        } catch (Exception e) {
            // 清理资源。
            try {
                if (Objects.nonNull(rmiRegistry)) {
                    UnicastRemoteObject.unexportObject(rmiRegistry, true);
                }
            } catch (Exception ex) {
                LOGGER.warn("清理 RMI 注册失败, 异常信息如下: ", ex);
            }
            connectorServer = null;
            rmiRegistry = null;
            currentPort = 0;
            // 抛出异常。
            throw e;
        }
    }

    private void handleStop(CommandExecutor.Context context) throws Exception {
        lock.lock();
        try {
            handleStop0(context);
        } finally {
            lock.unlock();
        }
    }

    private void handleStop0(CommandExecutor.Context context) throws Exception {
        // 检查系统属性是否已启用 JMX 远程管理。
        if (isSystemPropertyEnabled()) {
            String portStr = System.getProperty(SYS_PROP_JMX_PORT);
            context.sendMessage(ApiMessages.message(ApiMessageKey.COMMAND_JMX_SYSTEM_PROPERTY_ENABLED,
                    SYS_PROP_JMX_PORT, portStr));
            context.sendMessage(ApiMessages.message(ApiMessageKey.COMMAND_JMX_NO_ACTION));
            return;
        }

        // 检查是否已经启动。
        if (Objects.isNull(connectorServer) || !connectorServer.isActive()) {
            context.sendMessage(ApiMessages.message(ApiMessageKey.COMMAND_JMX_NOT_STARTED));
            return;
        }

        try {
            // 停止 JMXConnectorServer。
            connectorServer.stop();

            // 取消绑定 RMI 注册。
            if (Objects.nonNull(rmiRegistry)) {
                UnicastRemoteObject.unexportObject(rmiRegistry, true);
            }

            context.sendMessage(ApiMessages.message(ApiMessageKey.COMMAND_JMX_STOPPED));
        } finally {
            // 清理资源。
            connectorServer = null;
            rmiRegistry = null;
            currentPort = 0;
        }
    }

    private void handleStatus(CommandExecutor.Context context) throws Exception {
        lock.lock();
        try {
            handleStatus0(context);
        } finally {
            lock.unlock();
        }
    }

    private void handleStatus0(CommandExecutor.Context context) throws Exception {
        // 检查系统属性是否已启用 JMX 远程管理。
        if (isSystemPropertyEnabled()) {
            String portStr = System.getProperty(SYS_PROP_JMX_PORT);
            try {
                int port = Integer.parseInt(portStr.trim());
                context.sendMessage(ApiMessages.message(ApiMessageKey.COMMAND_JMX_STATUS_SYSTEM_PROPERTY));
                context.sendMessage(ApiMessages.message(ApiMessageKey.COMMAND_JMX_SYSTEM_PROPERTY,
                        SYS_PROP_JMX_PORT, portStr));
                context.sendMessage(ApiMessages.message(ApiMessageKey.COMMAND_JMX_PORT, port));
                @SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
                String jmxServiceUrlDescription = ApiMessages.message(ApiMessageKey.COMMAND_JMX_SERVICE_URL,
                        "service:jmx:rmi:///jndi/rmi://:" + port + "/jmxrmi");
                context.sendMessage(jmxServiceUrlDescription);
            } catch (NumberFormatException e) {
                context.sendMessage(ApiMessages.message(ApiMessageKey.COMMAND_JMX_STATUS_SYSTEM_PROPERTY));
                context.sendMessage(ApiMessages.message(ApiMessageKey.COMMAND_JMX_SYSTEM_PROPERTY_INVALID,
                        SYS_PROP_JMX_PORT, portStr));
            }
            return;
        }

        // 如果系统属性不存在，检查命令管理的连接器服务器。
        if (Objects.isNull(connectorServer) || !connectorServer.isActive()) {
            context.sendMessage(ApiMessages.message(ApiMessageKey.COMMAND_JMX_NOT_STARTED));
        } else {
            context.sendMessage(ApiMessages.message(ApiMessageKey.COMMAND_JMX_STATUS_RUNNING));
            context.sendMessage(ApiMessages.message(ApiMessageKey.COMMAND_JMX_PORT, currentPort));
            @SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
            String jmxServiceUrlDescription = ApiMessages.message(ApiMessageKey.COMMAND_JMX_SERVICE_URL,
                    "service:jmx:rmi:///jndi/rmi//:" + currentPort + "/jmxrmi");
            context.sendMessage(jmxServiceUrlDescription);
        }
    }

    private boolean isSystemPropertyEnabled() {
        String portStr = System.getProperty(SYS_PROP_JMX_PORT);
        return !StringUtils.isBlank(portStr);
    }

    private int parsePort(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        // 优先级 1: 命令行参数。
        if (cmd.hasOption(COMMAND_SUB_OPTION_PORT)) {
            Number portNumber = cmd.getParsedOptionValue(COMMAND_SUB_OPTION_PORT);
            if (Objects.nonNull(portNumber)) {
                return portNumber.intValue();
            }
        }
        // 优先级 2: 询问用户。
        String promptMessage = ApiMessages.message(ApiMessageKey.COMMAND_JMX_PORT_PROMPT, DEFAULT_PORT);
        context.sendMessage(promptMessage);
        String userInput = context.receiveMessage();
        // 如果用户输入为空，使用默认值。
        if (!StringUtils.isBlank(userInput)) {
            try {
                return Integer.parseInt(userInput.trim());
            } catch (NumberFormatException e) {
                throw new Exception(ApiMessages.message(ApiMessageKey.COMMAND_JMX_INVALID_PORT, userInput), e);
            }
        }
        // 优先级 3: 默认值。
        return DEFAULT_PORT;
    }
}
