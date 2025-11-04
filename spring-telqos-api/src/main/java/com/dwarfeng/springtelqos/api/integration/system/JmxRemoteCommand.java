package com.dwarfeng.springtelqos.api.integration.system;

import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.stack.command.Context;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
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
 * JMX 远程管理操作命令。
 *
 * @author DwArFeng
 * @since 1.1.15
 */
public class JmxRemoteCommand extends CliCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmxRemoteCommand.class);

    @SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
    private static final String IDENTITY = "jmxremote";
    private static final String DESCRIPTION = "JMX 远程管理操作";

    private static final String COMMAND_OPTION_START = "start";
    private static final String COMMAND_OPTION_STOP = "stop";
    private static final String COMMAND_OPTION_STATUS = "status";

    private static final String COMMAND_OPTION_PORT = "p";

    private static final String CMD_LINE_SYNTAX_START = IDENTITY + " -" + COMMAND_OPTION_START + " [-" +
            COMMAND_OPTION_PORT + " port]";
    private static final String CMD_LINE_SYNTAX_STOP = IDENTITY + " -" + COMMAND_OPTION_STOP;
    private static final String CMD_LINE_SYNTAX_STATUS = IDENTITY + " -" + COMMAND_OPTION_STATUS;

    private static final String CMD_LINE_SYNTAX = CMD_LINE_SYNTAX_START + System.lineSeparator() +
            CMD_LINE_SYNTAX_STOP + System.lineSeparator() + CMD_LINE_SYNTAX_STATUS;

    private static final int DEFAULT_PORT = 9999;

    private static final String SYS_PROP_JMX_PORT = "com.sun.management.jmxremote.port";

    private final ReentrantLock lock = new ReentrantLock();

    private JMXConnectorServer connectorServer;
    private Registry rmiRegistry;
    private int currentPort;

    public JmxRemoteCommand() {
        super(IDENTITY, DESCRIPTION, CMD_LINE_SYNTAX);
    }

    @Override
    protected List<Option> buildOptions() {
        List<Option> list = new ArrayList<>();
        list.add(Option.builder(COMMAND_OPTION_START).desc("启动 JMX 远程管理").build());
        list.add(Option.builder(COMMAND_OPTION_STOP).desc("停止 JMX 远程管理").build());
        list.add(Option.builder(COMMAND_OPTION_STATUS).desc("查看 JMX 远程管理状态").build());
        list.add(Option.builder(COMMAND_OPTION_PORT).desc("JMX 远程管理端口号").hasArg().type(Number.class).build());
        return list;
    }

    @Override
    protected void executeWithCmd(Context context, CommandLine cmd) throws TelqosException {
        try {
            Pair<String, Integer> pair = analyseCommand(cmd);
            if (pair.getRight() != 1) {
                context.sendMessage("下列选项必须且只能含有一个: -" + COMMAND_OPTION_START + " -" +
                        COMMAND_OPTION_STOP + " -" + COMMAND_OPTION_STATUS);
                context.sendMessage(CMD_LINE_SYNTAX);
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
        } catch (Exception e) {
            throw new TelqosException(e);
        }
    }

    private Pair<String, Integer> analyseCommand(CommandLine cmd) {
        int i = 0;
        String subCmd = null;
        if (cmd.hasOption(COMMAND_OPTION_START)) {
            i++;
            subCmd = COMMAND_OPTION_START;
        }
        if (cmd.hasOption(COMMAND_OPTION_STOP)) {
            i++;
            subCmd = COMMAND_OPTION_STOP;
        }
        if (cmd.hasOption(COMMAND_OPTION_STATUS)) {
            i++;
            subCmd = COMMAND_OPTION_STATUS;
        }
        return Pair.of(subCmd, i);
    }

    private void handleStart(Context context, CommandLine cmd) throws Exception {
        lock.lock();
        try {
            handleStart0(context, cmd);
        } finally {
            lock.unlock();
        }
    }

    private void handleStart0(Context context, CommandLine cmd) throws Exception {
        // 检查系统属性是否已启用 JMX 远程管理。
        if (isSystemPropertyEnabled()) {
            String portStr = System.getProperty(SYS_PROP_JMX_PORT);
            context.sendMessage("JMX 远程管理已通过系统属性 " + SYS_PROP_JMX_PORT + " = " + portStr + " 启用");
            context.sendMessage("由于 JMX 远程管理已通过系统属性启用, 本指令将不会执行任何操作");
            return;
        }

        // 检查是否已经启动。
        if (Objects.nonNull(connectorServer) && connectorServer.isActive()) {
            context.sendMessage("JMX 远程管理已经在运行中, 端口: " + currentPort);
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

            context.sendMessage("JMX 远程管理启动成功");
            context.sendMessage("  端口: " + port);
            @SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
            String jmxServiceUrlDescription = "  服务地址: service:jmx:rmi:///jndi/rmi://:" + port + "/jmxrmi";
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

    private void handleStop(Context context) throws Exception {
        lock.lock();
        try {
            handleStop0(context);
        } finally {
            lock.unlock();
        }
    }

    private void handleStop0(Context context) throws Exception {
        // 检查系统属性是否已启用 JMX 远程管理。
        if (isSystemPropertyEnabled()) {
            String portStr = System.getProperty(SYS_PROP_JMX_PORT);
            context.sendMessage("JMX 远程管理已通过系统属性 " + SYS_PROP_JMX_PORT + " = " + portStr + " 启用");
            context.sendMessage("由于 JMX 远程管理已通过系统属性启用, 本指令将不会执行任何操作");
            return;
        }

        // 检查是否已经启动。
        if (Objects.isNull(connectorServer) || !connectorServer.isActive()) {
            context.sendMessage("JMX 远程管理未启动");
            return;
        }

        try {
            // 停止 JMXConnectorServer。
            connectorServer.stop();

            // 取消绑定 RMI 注册。
            if (Objects.nonNull(rmiRegistry)) {
                UnicastRemoteObject.unexportObject(rmiRegistry, true);
            }

            context.sendMessage("JMX 远程管理已停止");
        } finally {
            // 清理资源。
            connectorServer = null;
            rmiRegistry = null;
            currentPort = 0;
        }
    }

    private void handleStatus(Context context) throws Exception {
        lock.lock();
        try {
            handleStatus0(context);
        } finally {
            lock.unlock();
        }
    }

    private void handleStatus0(Context context) throws Exception {
        // 检查系统属性是否已启用 JMX 远程管理。
        if (isSystemPropertyEnabled()) {
            String portStr = System.getProperty(SYS_PROP_JMX_PORT);
            try {
                int port = Integer.parseInt(portStr.trim());
                context.sendMessage("JMX 远程管理状态: 已通过系统属性启用");
                context.sendMessage("  系统属性: " + SYS_PROP_JMX_PORT + " = " + portStr);
                context.sendMessage("  端口: " + port);
                @SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
                String jmxServiceUrlDescription = "  服务地址: service:jmx:rmi:///jndi/rmi://:" + port + "/jmxrmi";
                context.sendMessage(jmxServiceUrlDescription);
            } catch (NumberFormatException e) {
                context.sendMessage("JMX 远程管理状态: 已通过系统属性启用");
                context.sendMessage("  系统属性: " + SYS_PROP_JMX_PORT + " = " + portStr + " (无效的端口号)");
            }
            return;
        }

        // 如果系统属性不存在，检查命令管理的连接器服务器。
        if (Objects.isNull(connectorServer) || !connectorServer.isActive()) {
            context.sendMessage("JMX 远程管理未启动");
        } else {
            context.sendMessage("JMX 远程管理状态: 运行中");
            context.sendMessage("  端口: " + currentPort);
            @SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
            String jmxServiceUrlDescription = "  服务地址: service:jmx:rmi:///jndi/rmi://:" + currentPort + "/jmxrmi";
            context.sendMessage(jmxServiceUrlDescription);
        }
    }

    private boolean isSystemPropertyEnabled() {
        String portStr = System.getProperty(SYS_PROP_JMX_PORT);
        return !StringUtils.isBlank(portStr);
    }

    private int parsePort(Context context, CommandLine cmd) throws Exception {
        // 优先级 1: 命令行参数。
        if (cmd.hasOption(COMMAND_OPTION_PORT)) {
            Number portNumber = (Number) cmd.getParsedOptionValue(COMMAND_OPTION_PORT);
            if (Objects.nonNull(portNumber)) {
                return portNumber.intValue();
            }
        }
        // 优先级 2: 询问用户。
        String promptMessage = "请输入端口号（输入空格使用默认值 " + DEFAULT_PORT + "）:";
        context.sendMessage(promptMessage);
        String userInput = context.receiveMessage();
        // 如果用户输入为空，使用默认值。
        if (!StringUtils.isBlank(userInput)) {
            try {
                return Integer.parseInt(userInput.trim());
            } catch (NumberFormatException e) {
                throw new Exception("无效的端口号: " + userInput, e);
            }
        }
        // 优先级 3: 默认值。
        return DEFAULT_PORT;
    }
}

