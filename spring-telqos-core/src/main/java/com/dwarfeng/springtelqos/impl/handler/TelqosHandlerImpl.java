package com.dwarfeng.springtelqos.impl.handler;

import com.dwarfeng.springtelqos.sdk.util.Constants;
import com.dwarfeng.springtelqos.sdk.util.TelqosExceptionHelper;
import com.dwarfeng.springtelqos.stack.command.Command;
import com.dwarfeng.springtelqos.stack.command.CommandDescriptor;
import com.dwarfeng.springtelqos.stack.command.CommandExecutor;
import com.dwarfeng.springtelqos.stack.handler.TelqosHandler;
import com.dwarfeng.springtelqos.stack.struct.TelqosConfig;
import com.dwarfeng.subgrade.sdk.interceptor.analyse.BehaviorAnalyse;
import com.dwarfeng.subgrade.stack.exception.HandlerException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TelqosHandlerImpl implements TelqosHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelqosHandlerImpl.class);

    private static final String PACKAGE_NAME_BUILTIN_COMMAND = "com.dwarfeng.springtelqos.impl.command.builtin";

    private final ApplicationContext ctx;

    private final ThreadPoolTaskExecutor executor;

    private final TelqosConfig config;

    private final Map<String, Command> commandMap = new HashMap<>();
    private final Map<String, StringBuilder> commandBufferMap = new HashMap<>();
    private final Map<String, Channel> channelMap = new HashMap<>();
    private final Map<String, InteractionInfo> interactionMap = new HashMap<>();
    private final Map<String, CommandExecutionTask> taskMap = new HashMap<>();
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;

    private boolean startedFlag = false;

    public TelqosHandlerImpl(
            ApplicationContext ctx, ThreadPoolTaskExecutor executor, TelqosConfig config
    ) {
        this.ctx = ctx;
        this.executor = executor;
        this.config = config;
    }

    @Override
    @BehaviorAnalyse
    public boolean isStarted() {
        lock.lock();
        try {
            return startedFlag;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @BehaviorAnalyse
    public void start() throws HandlerException {
        lock.lock();
        try {
            internalStart();
        } catch (Exception e) {
            throw TelqosExceptionHelper.parse(e);
        } finally {
            lock.unlock();
        }
    }

    private void internalStart() throws Exception {
        if (startedFlag) {
            return;
        }

        // 注册指令。
        registerBuiltinCommands();
        registerCustomCommands();

        // 新建负责接收客户端连接线程。
        bossGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        // 新建负责处理客户端 IO 事件、task 任务、监听任务组。
        workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

        // 启动 NIO 服务的辅助启动类
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);

        // 配置 Channel
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new TelqosChannelInitializer());

        // 是否启用心跳保活机制
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

        // 绑定服务端口监听。
        channel = bootstrap.bind(config.getPort()).sync().channel();

        // 变更状态。
        startedFlag = true;
    }

    private void registerBuiltinCommands() throws Exception {
        List<String> classNames = new ArrayList<>();
        String packagePath = PACKAGE_NAME_BUILTIN_COMMAND.replace('.', '/');
        ClassLoader classLoader = Optional.ofNullable(ctx.getClassLoader())
                .orElse(Thread.currentThread().getContextClassLoader());
        Enumeration<URL> resources = classLoader.getResources(packagePath);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                String filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8.name());
                collectClassNamesFromFile(classNames, PACKAGE_NAME_BUILTIN_COMMAND, filePath);
            } else if ("jar".equals(protocol)) {
                collectClassNamesFromJar(classNames, packagePath, url);
            }
        }

        for (String className : classNames) {
            Class<?> clazz = Class.forName(className);
            if (!Command.class.isAssignableFrom(clazz) || clazz.isInterface() ||
                    Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }
            Field instanceField = clazz.getField("INSTANCE");
            if (!Modifier.isStatic(instanceField.getModifiers())) {
                throw new IllegalArgumentException("内置命令类的 INSTANCE 字段不是静态字段: " + className);
            }
            Object instance = instanceField.get(null);
            if (!(instance instanceof Command)) {
                throw new IllegalArgumentException("内置命令类的 INSTANCE 字段不是 Command 类型: " + className);
            }
            Command command = (Command) instance;
            String identity = command.getIdentity();
            if (Objects.isNull(identity)) {
                throw new IllegalArgumentException("内置命令 command.getIdentity() 不能为 null: " + className);
            }
            if (commandMap.containsKey(identity)) {
                throw new IllegalArgumentException("重复的命令标识符: " + identity);
            }
            if (identityInvalid(identity)) {
                throw new IllegalArgumentException("非法的命令标识符: " + identity);
            }
            commandMap.put(identity, command);
        }
    }

    private void collectClassNamesFromFile(List<String> classNames, String packageName, String filePath) {
        java.io.File dir = new java.io.File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        java.io.File[] files = dir.listFiles();
        if (Objects.isNull(files)) {
            return;
        }
        for (java.io.File file : files) {
            if (file.isDirectory()) {
                collectClassNamesFromFile(classNames, packageName + "." + file.getName(), file.getAbsolutePath());
                continue;
            }
            String fileName = file.getName();
            if (!fileName.endsWith(".class") || fileName.contains("$")) {
                continue;
            }
            classNames.add(packageName + "." + fileName.substring(0, fileName.length() - 6));
        }
    }

    private void collectClassNamesFromJar(List<String> classNames, String packagePath, URL url) throws IOException {
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        try (JarFile jarFile = jarURLConnection.getJarFile()) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.startsWith(packagePath) || !name.endsWith(".class") || name.contains("$")) {
                    continue;
                }
                classNames.add(name.substring(0, name.length() - 6).replace('/', '.'));
            }
        }
    }

    private void registerCustomCommands() {
        for (Command command : config.getCommands()) {
            String identity = command.getIdentity();
            if (Objects.isNull(identity)) {
                throw new IllegalArgumentException("command.getIdentity() 不能为 null");
            }
            if (commandMap.containsKey(identity)) {
                throw new IllegalArgumentException("重复的命令标识符: " + identity);
            }
            if (identityInvalid(identity)) {
                throw new IllegalArgumentException("非法的命令标识符: " + identity);
            }
            commandMap.put(identity, command);
        }
    }

    private boolean identityInvalid(String identity) {
        if (Objects.isNull(identity)) return false;
        return !identity.matches(Constants.COMMAND_IDENTITY_FORMAT);
    }

    @Override
    @BehaviorAnalyse
    public void stop() throws HandlerException {
        lock.lock();
        try {
            internalStop();
        } catch (Exception e) {
            throw TelqosExceptionHelper.parse(e);
        } finally {
            lock.unlock();
        }
    }

    private void internalStop() {
        if (!startedFlag) {
            return;
        }

        // 主动关闭注册的所有连接。
        Collection<String> addresses = new HashSet<>(channelMap.keySet());
        for (String address : addresses) {
            kick(address);
        }

        // 优雅的关闭 Channel 以及对应的 EventLoopGroup。
        channel.close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        // 取消注册指令。
        unregisterCommands();

        // 变更状态。
        startedFlag = false;
    }

    private void unregisterCommands() {
        commandMap.clear();
    }

    private void kick(String address) {
        if (!channelMap.containsKey(address)) return;
        Channel channel = channelMap.get(address);
        channel.writeAndFlush(ChannelUtil.line("服务端主动与您中断连接"));
        channel.writeAndFlush(ChannelUtil.line("再见!"));
        channel.close();
    }

    private void buildUpChannelInfo(String address, Channel channel) {
        taskMap.put(address, null);
        channelMap.put(address, channel);
        commandBufferMap.put(address, new StringBuilder());
        Lock lock = new ReentrantLock();
        interactionMap.put(
                address, new InteractionInfo(lock, lock.newCondition(), InteractionStatus.WAITING_COMMAND, null, false)
        );
    }

    private void sweepUpChannelInfo(String address) {
        InteractionInfo interactionInfo = interactionMap.get(address);
        // 非空判断，避免偶发的多次调用时抛出 NullPointerException。
        if (Objects.nonNull(interactionInfo)) {
            interactionInfo.getLock().lock();
            try {
                interactionInfo.setTermination(true);
                interactionInfo.getCondition().signalAll();
            } finally {
                interactionInfo.getLock().unlock();
            }
            CommandExecutionTask task = taskMap.get(address);
            if (Objects.nonNull(task)) {
                task.awaitFinish();
            }
        }
        taskMap.remove(address);
        channelMap.remove(address);
        commandBufferMap.remove(address);
        interactionMap.remove(address);
    }

    /**
     * Telqos 管道初始化处理器。
     *
     * @author DwArFeng
     * @since 1.0.0
     */
    private class TelqosChannelInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) {
            // 管道注册 handler。
            ChannelPipeline pipeline = socketChannel.pipeline();
            // 转码通道处理。
            pipeline.addLast("encode", new StringEncoder(Charset.forName(config.getCharset())));
            // 处理拆包、粘包的问题。
            pipeline.addLast("unpack", new LineBasedFrameDecoder(Integer.MAX_VALUE));
            // 编码通道处理。
            pipeline.addLast("decode", new StringDecoder(Charset.forName(config.getCharset())));
            // 聊天服务通道处理。
            pipeline.addLast("chat", new TelqosChannelHandler());
        }
    }

    /**
     * Telqos 管道处理器。
     *
     * @author DwArFeng
     * @since 1.0.0
     */
    private class TelqosChannelHandler extends SimpleChannelInboundHandler<String> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String commandLine) {
            Channel channel = ctx.channel();
            String address = ChannelUtil.getAddress(channel);

            lock.lock();
            try {
                // 对空字符串进行处理。
                if (StringUtils.isEmpty(commandLine)) {
                    return;
                }

                // 获取命令行的 StringBuilder。
                StringBuilder stringBuilder = commandBufferMap.get(address);

                // 处理多行命令。
                boolean endFlag = false;
                if (commandLine.charAt(commandLine.length() - 1) == Constants.MULTI_LINE_COMMAND_INDICATOR) {
                    stringBuilder.append(commandLine, 0, commandLine.length() - 1);
                } else {
                    endFlag = true;
                    stringBuilder.append(commandLine);
                }

                // 如果命令还没有输入完成，则退出，等待下一次输入。
                if (!endFlag) {
                    return;
                }

                // 构造命令，并且重置 StringBuilder。
                commandLine = commandBufferMap.getOrDefault(address, new StringBuilder()).toString();
                commandBufferMap.put(address, new StringBuilder());

                // 获取交互信息并交互。
                InteractionInfo interactionInfo = interactionMap.get(address);
                interaction(address, channel, interactionInfo, commandLine);
            } finally {
                lock.unlock();
            }
        }

        private void interaction(String address, Channel channel, InteractionInfo interactionInfo, String commandLine) {
            // 通过交互信息中的交互状态分别执行不同的指令。
            interactionInfo.getLock().lock();
            try {
                switch (interactionInfo.getInteractionStatus()) {
                    case WAITING_COMMAND:
                        CommandLineParseResult commandLineParseResult = parseCommandLine(commandLine);
                        // 命令非法时执行拒绝动作。
                        if (!commandLineParseResult.isValidFlag()) {
                            String[] invalidDescriptions = commandLineParseResult.getInvalidDescriptions();
                            int total = invalidDescriptions.length;
                            channel.writeAndFlush(ChannelUtil.line("输入的命令不合法，共 " + total + " 处错误"));
                            for (int i = 0; i < total; i++) {
                                channel.writeAndFlush(ChannelUtil.line(
                                        String.format("%d/%d: %s", i + 1, total, invalidDescriptions[i])
                                ));
                            }
                            channel.writeAndFlush(ChannelUtil.line(""));
                            return;
                        }
                        // 命令合法时，搜索相应的 Command。
                        String identity = commandLineParseResult.getIdentity();
                        String option = commandLineParseResult.getOption();
                        Command command = commandMap.get(identity);
                        // Command 不存在时执行拒绝动作。
                        if (Objects.isNull(command)) {
                            channel.writeAndFlush(ChannelUtil.line("未知的命令: " + identity));
                            channel.writeAndFlush(
                                    ChannelUtil.line("输入 " + Constants.COMMAND_IDENTITY_LIST_COMMAND + " 查看所有指令")
                            );
                            channel.writeAndFlush(ChannelUtil.line(""));
                            return;
                        }
                        // 同步执行交互任务。
                        executor.execute(new CommandExecutionTask(
                                interactionInfo, command, identity, address, option, commandLine, channel
                        ));
                        break;
                    case WAITING_MESSAGE:
                        interactionInfo.setNextMessage(commandLine);
                        interactionInfo.getCondition().signalAll();
                        break;
                    case BUSY:
                        channel.writeAndFlush(ChannelUtil.line("系统正忙，请稍候"));
                        break;
                }
            } finally {
                interactionInfo.getLock().unlock();
            }
        }

        private CommandLineParseResult parseCommandLine(String commandLine) {
            CommandLineParseResult commandLineParseResult = new CommandLineParseResult();

            String identity;
            String option;

            int firstSpaceIndex = commandLine.indexOf(' ');
            if (firstSpaceIndex == -1) {
                identity = commandLine;
                option = "";
            } else {
                identity = commandLine.substring(0, firstSpaceIndex);
                option = commandLine.substring(firstSpaceIndex + 1);
            }

            commandLineParseResult.setIdentity(identity);
            commandLineParseResult.setOption(option);

            if (identityInvalid(identity)) {
                commandLineParseResult.setValidFlag(false);
                commandLineParseResult.setInvalidDescriptions(new String[]{"非法的指令标识符: " + identity});
            } else {
                commandLineParseResult.setValidFlag(true);
                commandLineParseResult.setInvalidDescriptions(new String[0]);
            }

            return commandLineParseResult;
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            Channel channel = ctx.channel();
            String address = ChannelUtil.getAddress(channel);

            lock.lock();
            try {
                showBanner(channel);

                if (!checkAddress(address)) {
                    LOGGER.info("设备 {} 尝试访问本服务，由于黑/白名单规则被禁止", address);
                    channel.writeAndFlush(ChannelUtil.line("该服务设置了黑/白名单，您所在的设备禁止访问此服务"));
                    channel.writeAndFlush(ChannelUtil.line("再见!"));
                    channel.close();
                    return;
                }

                mayReplaceExistsChannel(address);

                channel.writeAndFlush(ChannelUtil.line("欢迎您 " + address));
                channel.writeAndFlush(ChannelUtil.line(""));
                channel.writeAndFlush(ChannelUtil.line(""));
                buildUpChannelInfo(address, channel);
                LOGGER.info("设备 {} 尝试访问本服务，并登录成功", address);
            } finally {
                lock.unlock();
            }
        }

        private void showBanner(Channel channel) throws Exception {
            Resource resource = ctx.getResource(config.getBannerUrl());
            try (
                    InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                    Scanner scanner = new Scanner(reader)
            ) {
                while (scanner.hasNextLine()) {
                    channel.write(ChannelUtil.line(scanner.nextLine()));
                }
            }
            channel.flush();
        }

        private boolean checkAddress(String address) {
            String blacklistRegex = config.getBlacklistRegex();
            String whitelistRegex = config.getWhitelistRegex();

            // 先做一个大概率情形判断。
            if (StringUtils.isEmpty(blacklistRegex) && StringUtils.isEmpty(whitelistRegex)) {
                return true;
            }

            // 判断标准化地址是否能通过黑白名单。
            if (StringUtils.isNotEmpty(blacklistRegex) && address.matches(blacklistRegex)) {
                return false;
            }
            if (StringUtils.isEmpty(whitelistRegex)) {
                return true;
            }
            return address.matches(whitelistRegex);
        }

        private void mayReplaceExistsChannel(String address) {
            if (channelMap.containsKey(address)) {
                Channel channel = channelMap.get(address);
                channel.writeAndFlush(ChannelUtil.line("此设备 (" + address + ") 在其它进程登录，此进程将停止"));
                channel.writeAndFlush(ChannelUtil.line("再见!"));
                channel.close();
                sweepUpChannelInfo(address);
                LOGGER.info("设备 {} 在其它进程登录，其它登录进程停止", address);
            }
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
            Channel channel = ctx.channel();
            String address = ChannelUtil.getAddress(channel);

            LOGGER.info("设备 {} 与本服务断开连接", address);

            lock.lock();
            try {
                sweepUpChannelInfo(address);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
            Channel channel = ctx.channel();
            String address = ChannelUtil.getAddress(channel);

            LOGGER.warn("设备 {} 在通讯时发生异常，将中断连接，异常信息如下:", address, e);
            lock.lock();
            try {
                channel.writeAndFlush(ChannelUtil.line("不小心发生异常了，将中断连接, 请留意服务端日志"));
                channel.writeAndFlush(ChannelUtil.line("再见!"));
            } catch (Exception ex) {
                LOGGER.warn("向设备 {} 发送消息时发生异常，异常信息如下:", address, ex);
            } finally {
                channel.close();
                sweepUpChannelInfo(address);
                lock.unlock();
            }
        }
    }

    /**
     * 指令执行任务。
     *
     * @author DwArFeng
     * @since 1.0.0
     */
    private class CommandExecutionTask implements Runnable {

        private final InteractionInfo interactionInfo;
        private final Command command;
        private final String identity;
        private final String address;
        private final String option;
        private final String commandLine;
        private final Channel channel;

        private boolean finishFlag = false;

        public CommandExecutionTask(
                InteractionInfo interactionInfo, Command command, String identity, String address, String option,
                String commandLine, Channel channel
        ) {
            this.interactionInfo = interactionInfo;
            this.command = command;
            this.identity = identity;
            this.address = address;
            this.option = option;
            this.commandLine = commandLine;
            this.channel = channel;
        }

        @Override
        public void run() {
            // 执行指令，将结果通过反序列化器输出，并妥善处理异常。
            try {
                // 变量记录、输出日志。
                commandBufferMap.put(address, new StringBuilder());
                LOGGER.info("设备 {} 尝试执行指令: {}", address, commandLine);

                // 变更交互状态。
                interactionInfo.getLock().lock();
                try {
                    interactionInfo.setInteractionStatus(InteractionStatus.BUSY);
                } finally {
                    interactionInfo.getLock().unlock();
                }
                // 设置客户端当前任务。
                lock.lock();
                try {
                    taskMap.put(address, this);
                } finally {
                    lock.unlock();
                }
                // 执行指令。
                executeCommand();
            } finally {
                // 变更交互状态。
                interactionInfo.getLock().lock();
                try {
                    interactionInfo.setInteractionStatus(InteractionStatus.WAITING_COMMAND);
                } finally {
                    interactionInfo.getLock().unlock();
                }
                lock.lock();
                try {
                    finishFlag = true;
                    condition.signalAll();
                } finally {
                    lock.unlock();
                }
            }
        }

        private void executeCommand() {
            try {
                CommandExecutor commandExecutor = command.newCommandExecutor();
                commandExecutor.init(
                        new CommandExecutorContextImpl(identity, address, option, interactionInfo, channel)
                );
                commandExecutor.execute();
                channel.writeAndFlush(ChannelUtil.line("OK"));
                channel.writeAndFlush(ChannelUtil.line(""));
            } catch (Exception e) {
                LOGGER.warn("执行指令时发生异常，异常信息如下", e);
                try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
                    e.printStackTrace(pw);
                    channel.writeAndFlush(ChannelUtil.line("Exception"));
                    channel.writeAndFlush(ChannelUtil.line(sw.toString()));
                    channel.writeAndFlush(ChannelUtil.line(""));
                } catch (Exception e1) {
                    LOGGER.warn("获取异常 StackTrace 时发生异常，异常信息如下", e);
                }
            }
        }

        public void awaitFinish() {
            lock.lock();
            try {
                while (!finishFlag) {
                    condition.awaitUninterruptibly();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 指令描述器上下文：在查询某条命令的说明时，向描述器提供该命令的标识符。
     *
     * @author DwArFeng
     * @since 2.0.0
     */
    private static final class CommandDescriptorContextImpl implements CommandDescriptor.Context {

        private final String identity;

        public CommandDescriptorContextImpl(String identity) {
            this.identity = identity;
        }

        @Override
        public String getIdentity() {
            return identity;
        }
    }

    /**
     * 指令执行器上下文的实现，为单次连接上的指令执行提供地址、选项、列指令与交互能力。
     *
     * @author DwArFeng
     * @since 1.0.0
     */
    private class CommandExecutorContextImpl implements CommandExecutor.Context {

        private final String identity;
        private final String address;
        private final String option;
        private final InteractionInfo interactionInfo;
        private final Channel channel;

        public CommandExecutorContextImpl(
                String identity, String address, String option, InteractionInfo interactionInfo, Channel channel
        ) {
            this.identity = identity;
            this.address = address;
            this.option = option;
            this.interactionInfo = interactionInfo;
            this.channel = channel;
        }

        @Override
        public String getIdentity() {
            return identity;
        }

        @Override
        public String getAddress() {
            return address;
        }

        @Override
        public String getOption() {
            return option;
        }

        @Override
        public List<String> getCommandIdentities() {
            lock.lock();
            try {
                ArrayList<String> list = new ArrayList<>(commandMap.keySet());
                list.sort(String::compareTo);
                return list;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public String getCommandDescription(String identity) throws Exception {
            lock.lock();
            try {
                Command c = commandMap.get(identity);
                if (Objects.isNull(c)) {
                    return null;
                }
                CommandDescriptor d = c.newCommandDescriptor();
                d.init(new CommandDescriptorContextImpl(identity));
                return d.getDescription();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public String getCommandManual(String identity) throws Exception {
            lock.lock();
            try {
                Command c = commandMap.get(identity);
                if (Objects.isNull(c)) {
                    return null;
                }
                CommandDescriptor d = c.newCommandDescriptor();
                d.init(new CommandDescriptorContextImpl(identity));
                return d.getManual();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void sendMessage(String message) {
            channel.writeAndFlush(ChannelUtil.line(message));
        }

        @Override
        public String receiveMessage() throws Exception {
            interactionInfo.getLock().lock();
            try {
                interactionInfo.setInteractionStatus(InteractionStatus.WAITING_MESSAGE);
                interactionInfo.setNextMessage(null);

                while (Objects.isNull(interactionInfo.getNextMessage()) && !interactionInfo.isTermination()) {
                    interactionInfo.getCondition().awaitUninterruptibly();
                }

                if (interactionInfo.isTermination()) {
                    throw new IOException("连接已中断");
                }

                interactionInfo.setInteractionStatus(InteractionStatus.BUSY);
                return interactionInfo.getNextMessage();
            } finally {
                interactionInfo.getLock().unlock();
            }
        }

        @Override
        public void quit() {
            lock.lock();
            try {
                kick(address);
            } finally {
                lock.unlock();
            }
        }
    }
}
