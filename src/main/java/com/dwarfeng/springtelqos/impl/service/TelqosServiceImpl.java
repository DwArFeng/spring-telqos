package com.dwarfeng.springtelqos.impl.service;

import com.dwarfeng.springtelqos.sdk.util.Constants;
import com.dwarfeng.springtelqos.stack.bean.TelqosConfig;
import com.dwarfeng.springtelqos.stack.command.Command;
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

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
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
    private final Map<String, Map<String, Object>> variableMap = new HashMap<>();
    private final Map<String, StringBuilder> commandBufferMap = new HashMap<>();
    private final Map<String, Channel> channelMap = new HashMap<>();
    private final Lock lock = new ReentrantLock();

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
    public void destroy() throws Exception {
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

    private void internalOffline() throws Exception {
        if (!onlineFlag) {
            return;
        }

        //主动关闭注册的所有连接。
        Collection<String> addresses = new HashSet<>(channelMap.keySet());
        for (String address : addresses) {
            internalKick(address);
        }

        //优雅的关闭 Channel 以及对应的 EventLoopGroup。
        channel.close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        onlineFlag = false;
    }

    @Override
    public Collection<Command> getCommands() {
        lock.lock();
        try {
            return Collections.unmodifiableCollection(commandMap.values());
        } finally {
            lock.unlock();
        }
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
        return false;
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

    @Override
    public Command getCommand(String identity) {
        lock.lock();
        try {
            return commandMap.get(identity);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Collection<String> getAddresses() {
        lock.lock();
        try {
            return channelMap.keySet();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void kick(String address) throws TelqosException {
        lock.lock();
        try {
            internalKick(address);
        } catch (Exception e) {
            throw new TelqosException(e);
        } finally {
            lock.unlock();
        }
    }

    private void internalKick(String address) throws InterruptedException, ExecutionException {
        if (!channelMap.containsKey(address)) return;
        Channel channel = channelMap.get(address);
        channel.writeAndFlush(ChannelUtil.line("服务端主动与您中断连接")).get();
        channel.writeAndFlush(ChannelUtil.line("再见!")).get();
        channel.close();
        sweepUpChannelInfo(address);
    }

    private void buildUpChannelInfo(String address, Channel channel) {
        channelMap.put(address, channel);
        commandBufferMap.put(address, new StringBuilder());
        variableMap.put(address, new HashMap<>());
    }

    private void sweepUpChannelInfo(String address) {
        channelMap.remove(address);
        commandBufferMap.remove(address);
        variableMap.remove(address);
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
    public void setApplicationContext(ApplicationContext applicationContext) {
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
        protected void channelRead0(ChannelHandlerContext ctx, String commandLine) throws Exception {
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
                variableMap.get(address).put(Constants.VARIABLE_IDENTITY_LAST_INPUT, commandLine);
                commandBufferMap.put(address, new StringBuilder());
                LOGGER.info("设备 " + address + " 尝试执行指令: " + commandLine);

                //解析命令，获取命令结构。
                CommandStruct commandStruct = parseCommandLine(commandLine);
                //命令非法时执行拒绝动作。
                if (!commandStruct.isValidFlag()) {
                    String[] invalidDescriptions = commandStruct.getInvalidDescriptions();
                    int total = invalidDescriptions.length;
                    channel.writeAndFlush(ChannelUtil.line("输入的命令不合法，共 " + total + " 处错误"));
                    for (int i = 0; i < total; i++) {
                        channel.writeAndFlush(ChannelUtil.line(String.format("%d/%d: %s", i + 1, total, invalidDescriptions[i])));
                    }
                    return;
                }

                //命令合法时，搜索相应的Command。
                String identity = commandStruct.getIdentity();
                Object[] params = commandStruct.getParams();
                Command command = commandMap.get(identity);
                //Command不存在时执行拒绝动作。
                if (Objects.isNull(command)) {
                    channel.writeAndFlush(ChannelUtil.line("未知的命令: " + identity));
                    return;
                }

                //执行指令，将结果通过反序列化器输出，并妥善处理异常。
                try {
                    long spentTime = -System.currentTimeMillis();
                    Object lastResult = command.execute(TelqosServiceImpl.this, address, params);
                    spentTime += System.currentTimeMillis();
                    variableMap.get(address).put(Constants.VARIABLE_IDENTITY_LAST_RESULT, lastResult);
                    String lastResultString = telqosConfig.getDeserializer().deserialize(lastResult);
                    channel.writeAndFlush(ChannelUtil.line("OK, " + spentTime + "ms."));
                    channel.writeAndFlush(ChannelUtil.line("Last result:"));
                    channel.writeAndFlush(ChannelUtil.line(lastResultString));
                    channel.writeAndFlush(ChannelUtil.line(""));
                } catch (Exception e) {
                    LOGGER.warn("执行指令时发生异常，异常信息如下", e);
                    variableMap.get(address).put(Constants.VARIABLE_IDENTITY_LAST_RESULT, null);
                    try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
                        e.printStackTrace(pw);
                        channel.writeAndFlush(ChannelUtil.line("Exception"));
                        channel.writeAndFlush(ChannelUtil.line(sw.toString()));
                        channel.writeAndFlush(ChannelUtil.line(""));
                    }
                }
            } finally {
                lock.unlock();
            }
        }

        private CommandStruct parseCommandLine(String commandLine) {
            CommandStruct commandStruct = new CommandStruct();

            final List<String> invalidDescriptions = new ArrayList<>();
            final List<Object> params = new ArrayList<>();

            boolean quoteFlag = false;
            boolean backQuoteFlag = false;
            boolean escapeFlag = false;
            boolean numericFlag = false;
            boolean variableFlag = false;
            boolean textFlag = false;
            int argIndex = -1;
            StringBuilder sb = new StringBuilder();

//            for (int i = 0; i < commandLine.toCharArray().length; i++) {
//                char ch = commandLine.charAt(i);
//
//                //出现空格时的判定逻辑。
//                if (ch == ' ' && !quoteFlag && !backQuoteFlag) {
//                    String aimString = sb.toString();
//                    sb = new StringBuilder();
//                    //如果空格前跟着转义字符，则不合法。
//                    if (escapeFlag) {
//                        commandStruct.setValidFlag(false);
//                        invalidDescriptions.add(String.format("第%d个字符: 不完整的转义字符", i - 1));
//                    }
//                    if(argIndex == -1){
//                        commandStruct.setIdentity(aimString);
//                    }else{
//
//                    }
//                }
//
//                //CommandIdentify的提取与合法判定。
//                if (argIndex == -1) {
//                    if (
//                        // 首位不允许是数字，且任何部位不能是
//                            (sb.length() == 0 && !Character.isLetter(ch)) ||
//                                    (!Character.isLetter(ch) && !Character.isDigit(ch) && ch != '_')) {
//
//                    }
//                }
//
//                //前一位字符是转义字符是的判定逻辑。
//
//            }

            //TODO
            commandStruct.setIdentity("list");
            commandStruct.setValidFlag(false);
            commandStruct.setInvalidDescriptions(new String[]{
                    "我故意的",
                    "用来测试效果的",
                    "看看是否可行"
            });
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
                    LOGGER.info("设备 " + address + " 尝试访问本服务，由于黑/白名单规则被禁止");
                    channel.writeAndFlush(ChannelUtil.line("该服务设置了黑/白名单，您所在的设备禁止访问此服务")).get();
                    channel.writeAndFlush(ChannelUtil.line("再见!")).get();
                    channel.close();
                    return;
                }

                mayReplaceExistsChannel(address);

                channel.writeAndFlush(ChannelUtil.line("欢迎您 " + address));
                channel.writeAndFlush(ChannelUtil.line(""));
                channel.writeAndFlush(ChannelUtil.line(""));
                buildUpChannelInfo(address, channel);
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

        private void mayReplaceExistsChannel(String address) throws ExecutionException, InterruptedException {
            if (channelMap.containsKey(address)) {
                Channel channel = channelMap.get(address);
                channel.writeAndFlush(ChannelUtil.line("此地址 (" + address + ") 在其它进程登录，此进程将停止")).get();
                channel.writeAndFlush(ChannelUtil.line("再见!")).get();
                channel.close();
                sweepUpChannelInfo(address);
            }
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
            Channel channel = ctx.channel();
            String address = ChannelUtil.getAddress(channel);

            lock.lock();
            try {
                channel.writeAndFlush(ChannelUtil.line("再见!"));
                channel.close();
                sweepUpChannelInfo(address);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
            Channel channel = ctx.channel();
            String address = ChannelUtil.getAddress(channel);

            lock.lock();
            LOGGER.warn("设备 " + address + " 在通讯时发生异常，将中断连接，异常信息如下:", e);
            try {
                channel.writeAndFlush(ChannelUtil.line("不小心发生异常了，将中断连接, 请留意服务端日志")).get();
                channel.writeAndFlush(ChannelUtil.line("再见!")).get();
            } catch (Exception ex) {
                LOGGER.warn("向设备 " + address + " 发送消息时发生异常，异常信息如下:", ex);
            } finally {
                channel.close();
                sweepUpChannelInfo(address);
                lock.unlock();
            }
        }
    }
}
