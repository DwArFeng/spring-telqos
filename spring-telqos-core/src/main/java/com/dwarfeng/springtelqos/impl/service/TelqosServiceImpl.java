package com.dwarfeng.springtelqos.impl.service;

import com.dwarfeng.springtelqos.sdk.util.Constants;
import com.dwarfeng.springtelqos.stack.bean.TelqosConfig;
import com.dwarfeng.springtelqos.stack.command.Command;
import com.dwarfeng.springtelqos.stack.command.Context;
import com.dwarfeng.springtelqos.stack.exception.ConnectionTerminatedException;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import com.dwarfeng.springtelqos.stack.service.TelqosService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TelqosServiceImpl implements TelqosService, InitializingBean, DisposableBean,
        ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelqosServiceImpl.class);

    private TelqosConfig telqosConfig;
    private ApplicationContext applicationContext;

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private Channel channel;

    private final Map<String, Command> commandMap = new HashMap<>();
    private final Map<String, StringBuilder> commandBufferMap = new HashMap<>();
    private final Map<String, Channel> channelMap = new HashMap<>();
    private final Map<String, InteractionInfo> interactionMap = new HashMap<>();
    private final Map<String, CommandExecutionTask> taskMap = new HashMap<>();
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private boolean onlineFlag = false;

    public TelqosServiceImpl() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        lock.lock();
        try {
            for (Command command : telqosConfig.getCommands()) {
                internalRegisterCommand(command);
            }
            internalOnline();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void destroy() {
        lock.lock();
        try {
            internalOffline();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isOnline() {
        lock.lock();
        try {
            return onlineFlag;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void online() throws TelqosException {
        lock.lock();
        try {
            internalOnline();
        } catch (Exception e) {
            throw new TelqosException(e);
        } finally {
            lock.unlock();
        }
    }

    private void internalOnline() throws Exception {
        if (onlineFlag) {
            return;
        }
        // 新建负责接收客户端连接线程。
        bossGroup = new NioEventLoopGroup();
        // 新建负责处理客户端i/o事件、task任务、监听任务组。
        workerGroup = new NioEventLoopGroup();

        // 启动 NIO 服务的辅助启动类
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);

        // 配置 Channel
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new TelqosChannelInitializer());

        // 是否启用心跳保活机制
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

        // 绑定服务端口监听。
        channel = bootstrap.bind(telqosConfig.getPort()).sync().channel();
        onlineFlag = true;
    }

    @Override
    public void offline() throws TelqosException {
        lock.lock();
        try {
            internalOffline();
        } catch (Exception e) {
            throw new TelqosException(e);
        } finally {
            lock.unlock();
        }
    }

    private void internalOffline() {
        if (!onlineFlag) {
            return;
        }

        //主动关闭注册的所有连接。
        Collection<String> addresses = new HashSet<>(channelMap.keySet());
        for (String address : addresses) {
            kick(address);
        }

        //优雅的关闭 Channel 以及对应的 EventLoopGroup。
        channel.close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        onlineFlag = false;
    }

    @Override
    public void registerCommand(Command command) throws TelqosException {
        lock.lock();
        try {
            internalRegisterCommand(command);
        } catch (Exception e) {
            throw new TelqosException(e);
        } finally {
            lock.unlock();
        }
    }

    private void internalRegisterCommand(Command command) {
        String identity = command.getIdentify();
        if (Objects.isNull(identity)) {
            throw new IllegalArgumentException("command.getIdentify() 不能为 null");
        }
        if (commandMap.containsKey(identity)) {
            throw new IllegalArgumentException("重复的命令标识符: " + identity);
        }
        if (identityInvalid(identity)) {
            throw new IllegalArgumentException("非法的命令标识符: " + identity);
        }
        commandMap.put(identity, command);
    }

    private boolean identityInvalid(String identity) {
        if (Objects.isNull(identity)) return false;
        return !identity.matches(Constants.COMMAND_IDENTITY_FORMAT);
    }

    @Override
    public void unregisterCommand(String identity) {
        lock.lock();
        try {
            commandMap.remove(identity);
        } finally {
            lock.unlock();
        }
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
        interactionMap.put(address, new InteractionInfo(lock, lock.newCondition(),
                InteractionStatus.WAITING_COMMAND, null, false));
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

    public TelqosConfig getTelqosConfig() {
        return telqosConfig;
    }

    public void setTelqosConfig(TelqosConfig telqosConfig) {
        this.telqosConfig = telqosConfig;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
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
            // 管道注册handler
            ChannelPipeline pipeline = socketChannel.pipeline();
            // 转码通道处理
            pipeline.addLast("encode", new StringEncoder(Charset.forName(telqosConfig.getCharset())));
            // 处理拆包、粘包的问题。
            pipeline.addLast("unpack", new LineBasedFrameDecoder(Integer.MAX_VALUE));
            // 编码通道处理
            pipeline.addLast("decode", new StringDecoder(Charset.forName(telqosConfig.getCharset())));
            // 聊天服务通道处理
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
                //对空字符串进行处理。
                if (StringUtils.isEmpty(commandLine)) {
                    return;
                }

                //获取命令行的StringBuilder。
                StringBuilder stringBuilder = commandBufferMap.get(address);

                //处理多行命令。
                boolean endFlag = false;
                if (commandLine.charAt(commandLine.length() - 1) == Constants.MULTI_LINE_COMMAND_INDICATOR) {
                    stringBuilder.append(commandLine, 0, commandLine.length() - 1);
                } else {
                    endFlag = true;
                    stringBuilder.append(commandLine);
                }

                //如果命令还没有输入完成，则退出，等待下一次输入。
                if (!endFlag) {
                    return;
                }

                //构造命令，并且重置 StringBuilder。
                commandLine = commandBufferMap.getOrDefault(address, new StringBuilder()).toString();
                commandBufferMap.put(address, new StringBuilder());

                //获取交互信息并交互。
                InteractionInfo interactionInfo = interactionMap.get(address);
                interaction(address, channel, interactionInfo, commandLine);
            } finally {
                lock.unlock();
            }
        }

        private void interaction(String address, Channel channel, InteractionInfo interactionInfo, String commandLine) {
            //通过交互信息中的交互状态分别执行不同的指令。
            interactionInfo.getLock().lock();
            try {
                switch (interactionInfo.getInteractionStatus()) {
                    case WAITING_COMMAND:
                        CommandStruct commandStruct = parseCommandLine(commandLine);
                        //命令非法时执行拒绝动作。
                        if (!commandStruct.isValidFlag()) {
                            String[] invalidDescriptions = commandStruct.getInvalidDescriptions();
                            int total = invalidDescriptions.length;
                            channel.writeAndFlush(ChannelUtil.line("输入的命令不合法，共 " + total + " 处错误"));
                            for (int i = 0; i < total; i++) {
                                channel.writeAndFlush(ChannelUtil.line(String.format("%d/%d: %s", i + 1, total, invalidDescriptions[i])));
                            }
                            channel.writeAndFlush(ChannelUtil.line(""));
                            return;
                        }
                        //命令合法时，搜索相应的Command。
                        String identity = commandStruct.getIdentity();
                        String option = commandStruct.getOption();
                        Command command = commandMap.get(identity);
                        //Command不存在时执行拒绝动作。
                        if (Objects.isNull(command)) {
                            channel.writeAndFlush(ChannelUtil.line("未知的命令: " + identity));
                            channel.writeAndFlush(ChannelUtil.line(""));
                            return;
                        }
                        //同步执行交互任务。
                        telqosConfig.getExecutor().execute(new CommandExecutionTask(
                                interactionInfo, command,
                                address,
                                new ContextImpl(address, option, interactionMap.get(address), channel),
                                commandLine,
                                channel
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

        private CommandStruct parseCommandLine(String commandLine) {
            CommandStruct commandStruct = new CommandStruct();

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

            commandStruct.setIdentity(identity);
            commandStruct.setOption(option);

            if (identityInvalid(identity)) {
                commandStruct.setValidFlag(false);
                commandStruct.setInvalidDescriptions(new String[]{"非法的指令标识符: " + identity});
            } else {
                commandStruct.setValidFlag(true);
                commandStruct.setInvalidDescriptions(new String[0]);
            }

            return commandStruct;
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
            Resource resource = applicationContext.getResource(telqosConfig.getBannerUrl());
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
            String blacklistRegex = telqosConfig.getBlacklistRegex();
            String whitelistRegex = telqosConfig.getWhitelistRegex();

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
        private final String address;
        private final Context context;
        private final String commandLine;
        private final Channel channel;

        private boolean finishFlag = false;

        public CommandExecutionTask(
                InteractionInfo interactionInfo, Command command, String address, Context context,
                String commandLine, Channel channel) {
            this.interactionInfo = interactionInfo;
            this.command = command;
            this.address = address;
            this.context = context;
            this.commandLine = commandLine;
            this.channel = channel;
        }

        @Override
        public void run() {
            //执行指令，将结果通过反序列化器输出，并妥善处理异常。
            try {
                //变量记录、输出日志。
                commandBufferMap.put(address, new StringBuilder());
                LOGGER.info("设备 {} 尝试执行指令: {}", address, commandLine);

                //变更交互状态。
                interactionInfo.getLock().lock();
                try {
                    interactionInfo.setInteractionStatus(InteractionStatus.BUSY);
                } finally {
                    interactionInfo.getLock().unlock();
                }
                //设置客户端当前任务。
                lock.lock();
                try {
                    taskMap.put(address, this);
                } finally {
                    lock.unlock();
                }
                command.execute(context);
                channel.writeAndFlush(ChannelUtil.line("OK"));
                channel.writeAndFlush(ChannelUtil.line(""));
            } catch (ConnectionTerminatedException ignored) {
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
            } finally {
                //变更交互状态。
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
     * 指令上下文实现。
     *
     * @author DwArFeng
     * @since 1.0.0
     */
    private class ContextImpl implements Context {

        private final String address;
        private final String option;
        private final InteractionInfo interactionInfo;
        private final Channel channel;

        public ContextImpl(String address, String option, InteractionInfo interactionInfo, Channel channel) {
            this.address = address;
            this.option = option;
            this.interactionInfo = interactionInfo;
            this.channel = channel;
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
        public String getCommandDescription(String identity) {
            lock.lock();
            try {
                return Optional.ofNullable(commandMap.get(identity)).map(Command::getDescription).orElse(null);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public String getCommandManual(String identity) {
            lock.lock();
            try {
                return Optional.ofNullable(commandMap.get(identity)).map(Command::getManual).orElse(null);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void sendMessage(String message) {
            channel.writeAndFlush(ChannelUtil.line(message));
        }

        @Override
        public String receiveMessage() throws ConnectionTerminatedException {
            interactionInfo.getLock().lock();
            try {
                interactionInfo.setInteractionStatus(InteractionStatus.WAITING_MESSAGE);
                interactionInfo.setNextMessage(null);

                while (Objects.isNull(interactionInfo.getNextMessage()) && !interactionInfo.isTermination()) {
                    interactionInfo.getCondition().awaitUninterruptibly();
                }

                if (interactionInfo.isTermination()) {
                    throw new ConnectionTerminatedException();
                }

                interactionInfo.setInteractionStatus(InteractionStatus.BUSY);
                return interactionInfo.getNextMessage();
            } finally {
                interactionInfo.getLock().unlock();
            }
        }

        @Override
        public void quit() throws TelqosException {
            lock.lock();
            try {
                kick(address);
            } catch (Exception e) {
                throw new TelqosException(e);
            } finally {
                lock.unlock();
            }
        }
    }
}
