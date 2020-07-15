package com.dwarfeng.springtelqos.impl.service;

import com.dwarfeng.springtelqos.stack.bean.TelqosConfig;
import com.dwarfeng.springtelqos.stack.handler.CliHandler;
import com.dwarfeng.springtelqos.stack.serialize.Deserializer;
import com.dwarfeng.springtelqos.stack.serialize.Serializer;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TelqosServiceImpl implements TelqosService, InitializingBean, DisposableBean, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelqosServiceImpl.class);

    private TelqosConfig telqosConfig;
    private CliHandler cliHandler;
    private Serializer serializer;
    private Deserializer deserializer;
    private ApplicationContext applicationContext;

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private Channel channel;

    public TelqosServiceImpl() {
    }

    public TelqosServiceImpl(
            TelqosConfig telqosConfig, CliHandler cliHandler, Serializer serializer, Deserializer deserializer) {
        this(telqosConfig, cliHandler, serializer, deserializer, null);
    }

    public TelqosServiceImpl(
            TelqosConfig telqosConfig, CliHandler cliHandler, Serializer serializer, Deserializer deserializer,
            ApplicationContext applicationContext) {
        this.telqosConfig = telqosConfig;
        this.cliHandler = cliHandler;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
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

        // BACKLOG用于构造服务端套接字ServerSocket对象，
        // 标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
        bootstrap.option(ChannelOption.SO_BACKLOG, telqosConfig.getSoBacklog());
        // 是否启用心跳保活机制
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

        // 绑定服务端口监听。
        channel = bootstrap.bind(telqosConfig.getPort()).sync().channel();
    }

    @Override
    public void destroy() throws Exception {
        //优雅的关闭 Channel 以及对应的 EventLoopGroup。
        channel.close().get();
        bossGroup.shutdownGracefully().get();
        workerGroup.shutdownGracefully().get();
    }

    public TelqosConfig getTelqosConfig() {
        return telqosConfig;
    }

    public void setTelqosConfig(TelqosConfig telqosConfig) {
        this.telqosConfig = telqosConfig;
    }

    public CliHandler getCliHandler() {
        return cliHandler;
    }

    public void setCliHandler(CliHandler cliHandler) {
        this.cliHandler = cliHandler;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    public Deserializer getDeserializer() {
        return deserializer;
    }

    public void setDeserializer(Deserializer deserializer) {
        this.deserializer = deserializer;
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
        protected void channelRead0(ChannelHandlerContext ctx, String message) {
            // 获取管道处理器上下文中的管道地址和管道本身。
            Channel channel = ctx.channel();
            Object remoteAddress = channel.remoteAddress();

            //TODO

            System.out.println(message);
            channel.writeAndFlush(ChannelUtil.line("message"));
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            Channel channel = ctx.channel();
            String address = ChannelUtil.getAddress(channel);
            showBanner(channel);
            channel.writeAndFlush(ChannelUtil.line("欢迎您 " + address));
            if (!checkAddress(address)) {
                LOGGER.info("设备 " + address + " 尝试访问本服务，由于黑/白名单规则被禁止");
                channel.writeAndFlush(ChannelUtil.line("该服务设置了黑/白名单，您所在的设备禁止访问此服务")).get();
                ctx.channel().writeAndFlush(ChannelUtil.line("再见!")).get();
                channel.close();
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

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
            ctx.channel().writeAndFlush(ChannelUtil.line("再见!"));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
            Channel channel = ctx.channel();
            String address = ChannelUtil.getAddress(channel);

            LOGGER.warn("设备 " + address + " 在通讯时发生异常，将中断连接，异常信息如下:", e);
            try {
                channel.writeAndFlush(ChannelUtil.line("不小心发生异常了，将连接终端, 请留意服务端日志")).get();
                ctx.channel().writeAndFlush(ChannelUtil.line("再见!")).get();
            } catch (Exception ex) {
                LOGGER.warn("向设备 " + address + " 发送消息时发生异常，异常信息如下:", ex);
            } finally {
                channel.close();
            }
        }
    }
}
