# Spring Telqos Namespace Reference - Spring Telqos 命名空间参考

## 综述

`spring-telqos` 提供了 Spring XML 命名空间，用于在 Spring 容器中声明 Telqos 的连接配置、指令集合、
处理器以及对外服务。

本文是命名空间的配置参考，重点说明每个 XML 元素和属性的含义。如果您需要从零开始搭建一个可运行示例，
可以先阅读 [Usage Guide](./UsageGuide.md) 或 [Quick Start](./QuickStart.md)。

命名空间中的核心元素有三个：

| 元素             | 说明                                                     |
|:-----------------|:---------------------------------------------------------|
| `telqos:config`  | 定义 Telqos 连接、指令、命名策略等基础配置。             |
| `telqos:handler` | 定义 Telqos 处理器，用于启动底层 telnet 服务。           |
| `telqos:qos`     | 定义 Telqos QOS 服务，对外提供启动、停止、状态查询能力。 |

最小可用配置通常由一个线程池、一个 `telqos:config`、一个 `telqos:handler` 和一个 `telqos:qos` 组成：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:task="http://www.springframework.org/schema/task"
        xmlns:telqos="http://dwarfeng.com/schema/spring-telqos"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/task
        http://www.springframework.org/schema/task/spring-task.xsd
        http://dwarfeng.com/schema/spring-telqos
        http://dwarfeng.com/schema/spring-telqos/spring-telqos.xsd"
>

    <task:executor
            id="executor"
            pool-size="20-40"
            queue-capacity="100"
            keep-alive="120"
            rejection-policy="CALLER_RUNS"
    />

    <telqos:config>
        <telqos:connection-setting port="${telqos.port}" charset="${telqos.charset}"/>
    </telqos:config>

    <telqos:handler/>
    <telqos:qos/>
</beans>
```

需要注意的是，`telqos:qos` 默认会引用名为 `mapServiceExceptionMapper` 的 `ServiceExceptionMapper` bean。
如果您的工程中没有使用默认名称，需要通过 `sem-ref` 指向实际的异常映射器。

## 命名空间声明

在 XML 中使用 Telqos 命名空间时，需要声明 `xmlns:telqos`，并在 `xsi:schemaLocation` 中加入对应的 XSD 地址。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- 以下注释用于抑制 idea 中 .md 的警告，实际并无错误，在使用时可以连同本注释一起删除。 -->
<!--suppress XmlUnusedNamespaceDeclaration -->
<beans
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:telqos="http://dwarfeng.com/schema/spring-telqos"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://dwarfeng.com/schema/spring-telqos
        http://dwarfeng.com/schema/spring-telqos/spring-telqos.xsd"
>

    <!-- 在此处声明 telqos 相关元素。 -->
</beans>
```

该 XSD 通过项目 jar 包中的 `META-INF/spring.schemas` 映射到本地资源，因此正常情况下不需要访问远程地址。

## telqos:config

`telqos:config` 用于配置 Telqos 的基础运行参数，并向 Spring 容器中导出一个 `TelqosConfig` bean。

| 属性          | 类型     | 默认值         | 说明                              |
|:--------------|:---------|:---------------|:----------------------------------|
| `config-name` | `String` | `telqosConfig` | 导出的 `TelqosConfig` bean 名称。 |

`telqos:config` 支持三个子元素：

| 子元素               | 说明                                 |
|:---------------------|:-------------------------------------|
| `connection-setting` | 配置端口、字符集、banner、黑白名单。 |
| `command`            | 配置需要注册的自定义指令。           |
| `naming-strategy`    | 配置自定义指令的运行时命名策略。     |

示例：

```xml
<telqos:config config-name="telqosConfig">
    <telqos:connection-setting
            port="${telqos.port}"
            charset="${telqos.charset}"
            whitelist-regex="${telqos.whitelist_regex}"
            blacklist-regex="${telqos.blacklist_regex}"
    />
    <telqos:command>
        <telqos:command-impl command-ref="helloWorldCommand"/>
    </telqos:command>
</telqos:config>
```

需要注意的是，`config-name` 需要在当前 Spring 容器中保持唯一。如果同时声明多个 `telqos:config`，
需要为它们设置不同的 `config-name`。

## connection-setting

`connection-setting` 是 `telqos:config` 的子元素，用于配置 telnet 连接相关参数。

| 属性              | 类型      | 默认值                        | 说明                                             |
|:------------------|:----------|:------------------------------|:-------------------------------------------------|
| `port`            | `Integer` | `23`                          | Telnet 服务监听端口。                            |
| `charset`         | `String`  | `UTF-8`                       | 服务端与客户端交互使用的字符集。                 |
| `banner-url`      | `String`  | `classpath:telqos/banner.txt` | 新连接接入时输出的 banner 资源路径。             |
| `whitelist-regex` | `String`  | 空字符串                      | 连接白名单正则表达式，空字符串表示不启用白名单。 |
| `blacklist-regex` | `String`  | 空字符串                      | 连接黑名单正则表达式，空字符串表示不启用黑名单。 |

示例：

```xml
<telqos:config>
    <telqos:connection-setting
            port="22000"
            charset="UTF-8"
            banner-url="classpath:telqos/my-banner.txt"
            whitelist-regex="127\.0\.0\.1|::1"
            blacklist-regex="192\.168\.1\..*"
    />
</telqos:config>
```

端口号需要在 `0 - 65535` 之间。能否绑定成功还取决于操作系统权限和端口占用情况，
例如在部分系统上监听 `23` 端口可能需要更高权限。

`charset` 影响 telnet 服务端与客户端之间的文本编解码。如果您在中文 Windows 系统的 telnet 客户端中看到乱码，
可以尝试将该值设置为 `GBK`。

`banner-url` 使用 Spring Resource 语义，常见写法包括 `classpath:` 和 `file:`。banner 文件建议使用 UTF-8 编码保存，
客户端实际显示效果仍受 `charset` 配置影响。

黑白名单会匹配客户端地址。黑名单优先判断，若命中黑名单则拒绝连接；白名单为空时不限制来源；
白名单非空时，只有匹配白名单的客户端才能连接。

## command

`command` 是 `telqos:config` 的子元素，用于注册自定义指令。框架内置的 `lc`、`man`、`quit` 指令会自动注册，
不需要写在 `command` 中。

`command` 下可以声明多个 `command-impl`：

```xml
<telqos:config>
    <telqos:command>
        <telqos:command-impl command-ref="helloWorldCommand"/>
        <telqos:command-impl package-scan="com.example.telqos"/>
        <telqos:command-impl command-name="myCommand" class="com.example.telqos.MyCommand"/>
    </telqos:command>
</telqos:config>
```

`command-impl` 支持以下属性：

| 属性           | 类型     | 说明                                  |
|:---------------|:---------|:--------------------------------------|
| `command-ref`  | `String` | 引用已经存在的 `Command` bean。       |
| `package-scan` | `String` | 扫描指定包下的指令类。                |
| `class`        | `String` | 指令实现类的全限定名。                |
| `command-name` | `String` | 使用 `class` 注册指令时的 bean 名称。 |

解析时，如果同一个 `command-impl` 同时配置了多个注册属性，优先级为：

1. `command-ref`
2. `package-scan`
3. `class`

推荐一次只使用一种注册方式，以避免配置意图不清晰。

### 使用 command-ref 引用

通过 Spring bean 的引用名称注册指令：

```xml
<telqos:config>
    <telqos:command>
        <telqos:command-impl command-ref="helloWorldCommand"/>
    </telqos:command>
</telqos:config>

<bean name="helloWorldCommand" class="com.example.telqos.HelloWorldCommand"/>
```

### 使用 package-scan 扫描

扫描指定包下的所有 Telqos 指令：

```xml
<telqos:config>
    <telqos:command>
        <telqos:command-impl package-scan="com.example.telqos"/>
    </telqos:command>
</telqos:config>
```

使用 `package-scan` 时，目标类需要同时满足两个条件：

1. 使用 `@TelqosCommand` 注解。
2. 实现 `com.dwarfeng.springtelqos.stack.command.Command` 接口。

### 使用 class 直接指定

通过全限定类名注册指令：

```xml
<telqos:config>
    <telqos:command>
        <telqos:command-impl command-name="helloWorldCommand" class="com.example.telqos.HelloWorldCommand"/>
    </telqos:command>
</telqos:config>
```

使用这种方式时，指令实现类需要能够由 Spring 正常实例化。通常情况下，指令类应提供可用的构造方法，
并为 `command-impl` 显式设置 `command-name`。

## naming-strategy

`naming-strategy` 是 `telqos:config` 的子元素，用于配置自定义指令的运行时标识映射策略。

未配置该元素时，框架使用默认命名策略，运行时指令标识与 `Command#getIdentity()` 返回值保持一致。

示例：

```xml
<telqos:config>
    <telqos:command>
        <telqos:command-impl package-scan="com.example.telqos"/>
    </telqos:command>
    <telqos:naming-strategy value="#{T(com.example.telqos.MyNamingStrategy).INSTANCE}"/>
</telqos:config>
```

`value` 需要能够提供一个 `NamingStrategy` 实例。常见做法是在命名策略类中提供静态 `INSTANCE` 字段，
并通过 SpEL 表达式引用它。

需要注意的是，命名策略应用于 `telqos:config` 中注册的自定义指令。内置指令 `lc`、`man`、`quit`
会保留自身标识。

命名策略返回的运行时标识需要满足指令标识符格式，并且不能与已经注册的指令重复。合法标识符规则如下：

- 首字符必须是字母或下划线。
- 后续字符可使用字母、数字、下划线。
- 支持使用 `:`、`.`、`-` 进行分段，且分隔符后必须跟随至少一个合法片段。

## telqos:handler

`telqos:handler` 用于向 Spring 容器中注册 `TelqosHandler` bean。该 handler 负责启动底层 telnet 服务、
维护连接状态，并将客户端输入分发给对应的指令执行器。

| 属性           | 类型     | 默认值          | 说明                                   |
|:---------------|:---------|:----------------|:---------------------------------------|
| `handler-name` | `String` | `telqosHandler` | 导出的 `TelqosHandler` bean 名称。     |
| `config-ref`   | `String` | `telqosConfig`  | 引用 `telqos:config` 导出的配置 bean。 |
| `executor-ref` | `String` | `executor`      | 引用 `ThreadPoolTaskExecutor` bean。   |

示例：

```xml
<task:executor
        id="executor"
        pool-size="20-40"
        queue-capacity="100"
        keep-alive="120"
        rejection-policy="CALLER_RUNS"
/>

<telqos:config config-name="telqosConfig">
    <telqos:connection-setting port="${telqos.port}" charset="${telqos.charset}"/>
</telqos:config>

<telqos:handler
        handler-name="telqosHandler"
        config-ref="telqosConfig"
        executor-ref="executor"
/>
```

`executor-ref` 必须指向一个 `ThreadPoolTaskExecutor`。指令执行会提交到该线程池中运行，
因此线程池容量会影响并发指令的执行能力。

如果配置多个 Telqos 实例，每个 `telqos:handler` 都需要使用不同的 `handler-name`，
并通过 `config-ref` 指向对应的 `telqos:config`。

## telqos:qos

`telqos:qos` 用于向 Spring 容器中注册 `TelqosQosService` bean。该服务对外提供 `start()`、`stop()`、
`isStarted()` 等操作，并通过 `ServiceExceptionMapper` 将底层异常转换为服务异常。

| 属性           | 类型      | 默认值                      | 说明                                     |
|:---------------|:----------|:----------------------------|:-----------------------------------------|
| `service-name` | `String`  | `telqosService`             | 导出的 `TelqosQosService` bean 名称。    |
| `handler-ref`  | `String`  | `telqosHandler`             | 引用 `TelqosHandler` bean。              |
| `sem-ref`      | `String`  | `mapServiceExceptionMapper` | 引用 `ServiceExceptionMapper` bean。     |
| `auto-start`   | `Boolean` | `true`                      | 是否在 Spring 容器初始化时自动启动服务。 |

示例：

```xml
<telqos:handler
        handler-name="telqosHandler"
        config-ref="telqosConfig"
        executor-ref="executor"
/>

<telqos:qos
        service-name="telqosService"
        handler-ref="telqosHandler"
        sem-ref="mapServiceExceptionMapper"
        auto-start="true"
/>
```

当 `auto-start` 为 `true` 时，Spring 初始化该 bean 时会调用 `start()`，Telqos 服务会随容器启动。
当 `auto-start` 为 `false` 时，服务只完成 bean 注册，需要由调用方在合适的时机手动调用 `start()`。

## 常见问题

### 1. 出现重复的 bean 名称

如果一个 Spring 容器中声明多个 `telqos:config`、`telqos:handler` 或 `telqos:qos`，
不要全部使用默认名称。

可以分别设置 `config-name`、`handler-name`、`service-name`，并同步调整 `config-ref`、`handler-ref`。

### 2. 启动时找不到 executor

`telqos:handler` 默认通过 `executor-ref="executor"` 引用线程池。如果容器中没有名为 `executor`
的 `ThreadPoolTaskExecutor`，需要显式声明线程池，或者把 `executor-ref` 改成实际的 bean 名称。

### 3. handler 没有使用预期的配置

检查 `telqos:handler` 的 `config-ref` 是否指向正确的 `telqos:config`。多个 Telqos 实例同时存在时，
最容易出现配置 bean 与 handler 引用关系不一致的问题。

### 4. 服务启动时端口被占用

检查 `connection-setting` 中的 `port` 配置。多个 Telqos 实例不能监听同一个端口。

如果使用默认端口 `23`，还需要确认当前操作系统是否允许应用监听该端口。

### 5. package-scan 后指令没有出现

使用 `package-scan` 时，指令类需要使用 `@TelqosCommand` 注解，并实现 `Command` 接口。

还需要检查指令标识是否合法，以及经过命名策略转换后的运行时标识是否与其它指令重复。

### 6. banner 没有正常显示

检查 `banner-url` 是否能被 Spring Resource 正常解析。使用 classpath 资源时，确认文件已经放入运行时 classpath；
使用文件系统路径时，确认路径格式和文件权限正确。

banner 文件建议使用 UTF-8 编码保存。如果客户端显示乱码，再结合 telnet 客户端环境调整 `charset`。

## 参阅

- [Usage Guide](./UsageGuide.md) - 使用指南，详细介绍如何配置框架、开发自定义指令以及框架的高级用法。
- [Quick Start](./QuickStart.md) - 快速开始，用最快的方式体验本项目。
