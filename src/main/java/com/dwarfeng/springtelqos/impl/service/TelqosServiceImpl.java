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
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.nio.charset.Charset;

public class TelqosServiceImpl implements TelqosService, InitializingBean, DisposableBean {

    private TelqosConfig telqosConfig;
    private CliHandler cliHandler;
    private Serializer serializer;
    private Deserializer deserializer;

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private Channel channel;

    public TelqosServiceImpl() {
    }

    public TelqosServiceImpl(
            TelqosConfig telqosConfig, CliHandler cliHandler, Serializer serializer, Deserializer deserializer) {
        this.telqosConfig = telqosConfig;
        this.cliHandler = cliHandler;
        this.serializer = serializer;
        this.deserializer = deserializer;
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
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {
            // 获取管道处理器上下文中的管道地址和管道本身，并将其添加进入管道映射中进行维护。
//            Channel channel = ctx.channel();
//            Object remoteAddress = channel.remoteAddress();
//            channelMap.put(remoteAddress, channel);
//
//            // 处理器进行客户端连接调度。
//            sconnProcessor.onClientConnected(remoteAddress);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
//            SyncMapModel<Object, Channel> channelMap = nettySconn.getChannelMap();
//            ServerSconnProcessor sconnProcessor = nettySconn.getServerContext().getSconnProcessor();
//
//            // 获取管道处理器上下文中的管道地址和管道本身，并将其添加进入管道映射中进行维护。
//            Channel channel = ctx.channel();
//            Object remoteAddress = channel.remoteAddress();
//            channelMap.remove(remoteAddress);
//
//            // 处理器进行客户端连接调度。
//            sconnProcessor.onClientDisconnected(remoteAddress);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
//            // TODO 临时方法，需要更改。
//            Channel channel = ctx.channel();
//            System.out.println("[" + channel.remoteAddress() + "] leave the room");
//            ctx.close().sync();
        }
    }
}
