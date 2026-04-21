# Netty in TelqosServiceImpl - TelqosServiceImpl 中 Netty 使用原理

## 综述

`spring-telqos` 的 telnet 服务由 `TelqosServiceImpl` 提供，底层网络通信基于 Netty 实现。

从代码职责上看，该类做了三件事：

- 使用 Netty 启动/关闭 TCP 服务端。
- 通过 `ChannelPipeline` 把字节流转换为文本命令，并交给业务处理。
- 管理每个连接的交互状态，使“命令执行”和“用户输入”可以正确协作。

对于不熟悉 Netty 的同学，可以先把它理解为：
Netty 负责高性能网络 I/O，`TelqosServiceImpl` 在其之上实现了“命令行会话层”。

## 总体架构与调用链路

`TelqosServiceImpl` 的核心链路如下：

1. Spring 初始化完成后，调用 `afterPropertiesSet()`。
2. `afterPropertiesSet()` 内部调用 `internalOnline()`，启动 Netty 服务。
3. 客户端建立连接后，进入 `handlerAdded()`，完成 banner、准入校验、会话建档。
4. 客户端输入命令后，进入 `channelRead0()`，完成多行拼接和命令解析。
5. 合法命令被提交到 `telqosConfig.getExecutor()`，由 `CommandExecutionTask` 异步执行。
6. 指令通过 `Context` 与客户端交互（发送消息、阻塞等待输入、退出连接）。
7. 连接断开或异常时，执行会话清理，释放相关状态。

这条链路体现了一个重要分层：
Netty 线程主要处理“收发与分发”，业务线程池处理“命令执行”。

## 服务启动原理（online 过程）

`internalOnline()` 是服务启动的核心方法，关键步骤如下：

1. 创建 `bossGroup` 与 `workerGroup`。
   - `bossGroup`：负责接受新连接。
   - `workerGroup`：负责已建立连接的 I/O 事件处理。
2. 构建 `ServerBootstrap`，并通过 `group(bossGroup, workerGroup)` 绑定线程模型。
3. 指定服务端通道类型为 `NioServerSocketChannel`。
4. 指定子通道初始化器 `TelqosChannelInitializer`。
5. 设置 `SO_KEEPALIVE=true`，开启 TCP KeepAlive。
6. 调用 `bind(port).sync().channel()` 绑定端口并阻塞等待绑定完成。
7. 设置 `onlineFlag=true`，标记服务在线。

对应源码中的关键语句如下：

```java
private void internalOnline() throws Exception {
    // ...
    bossGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
    workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(bossGroup, workerGroup);
    bootstrap.channel(NioServerSocketChannel.class);
    bootstrap.childHandler(new TelqosChannelInitializer());
    bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

    channel = bootstrap.bind(telqosConfig.getPort()).sync().channel();
    onlineFlag = true;
}
```

## ChannelPipeline 设计与编解码链

`TelqosChannelInitializer#initChannel()` 中注册了 4 个处理器：

1. `encode`：`StringEncoder(charset)`，用于把字符串编码为字节并写出。
2. `unpack`：`LineBasedFrameDecoder(Integer.MAX_VALUE)`，按行拆帧，解决粘包/拆包。
3. `decode`：`StringDecoder(charset)`，把字节帧转换为字符串。
4. `chat`：`TelqosChannelHandler`，执行 telqos 会话逻辑。

关键点：

- 入站方向（客户端 -> 服务端）主要经过 `unpack -> decode -> chat`。
- 出站方向（服务端 -> 客户端）主要使用 `encode`。
- `LineBasedFrameDecoder` 以换行符为边界，天然适合 telnet 文本命令协议。

## 命令接收与多行输入处理

命令输入在 `TelqosChannelHandler#channelRead0()` 中处理，流程如下：

1. 忽略空行输入。
2. 使用 `commandBufferMap[address]` 保存当前连接的命令缓冲。
3. 如果当前行以 `\` 结尾，则去掉结尾符并继续拼接，等待下一行。
4. 如果不是 `\` 结尾，表示命令输入结束，进入解析阶段。
5. 调用 `parseCommandLine()`：
   - 以首个空格切分 `identity` 与 `option`。
   - 用 `Constants.COMMAND_IDENTITY_FORMAT` 校验标识符合法性。
6. 合法后在 `commandMap` 查找命令实例，不存在则返回“未知命令”。
7. 命令存在时，把执行任务提交到线程池异步执行。

这里的多行输入指示符来自：

```java

@SuppressWarnings({"UnusedAssignment", "ProtectedMemberInFinalClass"})
protected void channelRead0(ChannelHandlerContext ctx, String commandLine) {
    // ...
    boolean endFlag = false;
    if (commandLine.charAt(commandLine.length() - 1) == Constants.MULTI_LINE_COMMAND_INDICATOR) {
        stringBuilder.append(commandLine, 0, commandLine.length() - 1);
    } else {
        endFlag = true;
        stringBuilder.append(commandLine);
    }
    // ...
}
```

因此，用户在一行末尾输入 `\`，可以把同一条指令跨多行输入。

## 交互状态机与并发协作模型

每个连接都有一个 `InteractionInfo`，其 `interactionStatus` 有 3 个状态：

- `WAITING_COMMAND`：等待新命令。
- `WAITING_MESSAGE`：命令执行中，正在等待用户输入（`receiveMessage()`）。
- `BUSY`：命令执行中，但当前不接受新命令覆盖。

状态协作规则：

1. 默认状态为 `WAITING_COMMAND`。
2. 命令开始执行时，切换为 `BUSY`。
3. 命令调用 `receiveMessage()` 时，切换为 `WAITING_MESSAGE` 并阻塞等待输入。
4. I/O 线程收到新行时：
   - 若状态为 `WAITING_MESSAGE`，将输入写入 `nextMessage` 并 `signalAll()` 唤醒执行线程。
   - 若状态为 `BUSY`，直接回复“系统正忙，请稍候”。
5. 命令完成后回到 `WAITING_COMMAND`。

锁模型分为两层：

- 服务级 `lock`：保护 `commandMap/channelMap/taskMap` 等全局共享结构。
- 会话级 `interactionInfo.lock`：保护单连接状态机与阻塞唤醒过程。

## 指令执行线程模型（I/O 线程与任务线程池分工）

在 `interaction()` 中，命令不会直接在 Netty I/O 线程里执行，而是：

```java
private void interaction(String address, Channel channel, InteractionInfo interactionInfo, String commandLine) {
    // ...
    telqosConfig.getExecutor().execute(new CommandExecutionTask(
            interactionInfo, command,
            address,
            new ContextImpl(address, option, interactionMap.get(address), channel),
            commandLine,
            channel
    ));
    // ...
}
```

这带来两个收益：

- 避免慢命令阻塞 Netty I/O 事件循环。
- 可以通过 `task-pool` 配置控制并发度与拒绝策略。

`CommandExecutionTask#run()` 的处理要点：

1. 记录日志并切换状态为 `BUSY`。
2. 执行 `command.execute(context)`。
3. 成功时回写 `OK` 和空行。
4. 捕获普通异常，回写 `Exception` 与堆栈信息。
5. `finally` 中恢复状态、标记任务完成并唤醒等待方。

`ContextImpl#receiveMessage()` 的原理：

1. 切换状态为 `WAITING_MESSAGE`，并清空 `nextMessage`。
2. 在 `Condition` 上阻塞，等待 I/O 线程投递输入。
3. 若连接终止标记为真，抛出 `ConnectionTerminatedException`。
4. 获取输入后切回 `BUSY`，返回字符串给命令逻辑。

## 连接生命周期管理（接入、断开、异常、下线）

### 接入：`handlerAdded()`

新连接进入时执行：

1. `showBanner(channel)` 输出 banner。
2. `checkAddress(address)` 执行黑白名单校验。
3. `mayReplaceExistsChannel(address)` 处理同地址重复登录（踢掉旧连接）。
4. 发送欢迎语。
5. `buildUpChannelInfo(address, channel)` 建立会话相关缓存结构。

### 断开：`handlerRemoved()`

连接断开时，调用 `sweepUpChannelInfo(address)` 执行会话清理。

### 异常：`exceptionCaught()`

通信异常时：

1. 记录日志。
2. 尝试向客户端发送异常提示。
3. 关闭连接并清理会话。

### 下线：`internalOffline()`

服务下线顺序为：

1. 遍历已连接地址并 `kick(address)` 主动通知断开。
2. 关闭主通道 `channel.close()`。
3. `bossGroup.shutdownGracefully()` 与 `workerGroup.shutdownGracefully()` 优雅停机。

`sweepUpChannelInfo()` 的一个关键设计是：
会先设置 `termination=true` 并唤醒等待输入的命令线程，再等待当前任务结束，最后再清理 map，减少资源悬挂与状态不一致。

## 配置项与 Netty 行为映射

以下配置最终都会映射到 `TelqosServiceImpl` 的运行行为：

- `port`
   - 作用：Netty 监听端口（`bootstrap.bind(port)`）。
   - 默认值：`23`。
- `charset`
   - 作用：`StringEncoder/StringDecoder` 使用的字符集。
   - 默认值：`UTF-8`。
- `banner-url`
   - 作用：连接建立后读取并输出 banner 内容。
   - 默认值：`classpath:telqos/banner.txt`。
- `whitelist-regex` / `blacklist-regex`
   - 作用：连接准入控制，`blacklist` 优先拒绝，`whitelist` 再决定是否放行。
   - 默认值：空字符串（不启用）。
- `task-pool`
   - 作用：命令执行线程池，对应 `telqosConfig.getExecutor()`。
   - 默认行为：若未配置，自动创建 `telqosExecutor`。

上述默认值来源于 `SpringTelqosDefinitionParser` 对 `connection-setting` 与 `task-pool` 的解析逻辑。

## 常见问题与排查建议

### 1. 出现“系统正忙，请稍候”

说明该连接处于 `BUSY` 状态，通常是上一条命令尚未结束。
排查建议：检查命令是否执行耗时过长，或是否在等待外部资源。

### 2. 交互命令输入无响应

如果命令内部调用了 `receiveMessage()`，连接应处于 `WAITING_MESSAGE`。
排查建议：确认客户端确实发送了换行结束符；无换行时 `LineBasedFrameDecoder` 不会产生命令帧。

### 3. 中文乱码

重点检查 `charset` 配置与客户端编码是否一致。
如果使用 Windows telnet，通常需要考虑 `GBK`。

### 4. 无法连接或连接后被立即断开

优先检查黑白名单正则是否命中。
注意地址格式：

- IPv4 形如 `127.0.0.1:52345`。
- IPv6 形如 `[0:0:0:0:0:0:0:1]:52345`。

### 5. 命令执行中连接断开导致异常

`receiveMessage()` 在连接终止时会抛出 `ConnectionTerminatedException`。
这是预期机制，命令实现应允许该异常向上抛出。

## 小结

`TelqosServiceImpl` 对 Netty 的使用并不复杂，但结构非常清晰：

- Netty 负责连接管理与文本帧收发。
- telqos 负责命令语义、会话状态机与交互协议。
- 线程池隔离了网络 I/O 与业务执行，提升了整体稳定性。

理解本文后，再阅读 `TelqosChannelHandler`、`CommandExecutionTask`、`ContextImpl` 三个内部类，
会更容易把握整个 telqos 服务端的运行机制。
