# Usage Guide - 使用指南

## 综述

本使用指南旨在帮助开发者快速上手 spring-telqos 框架，掌握如何配置和使用该框架构建自己的 telnet QOS 服务平台。

spring-telqos 是一款基于 Spring 框架的 telnet QOS 服务框架，它提供了：

- 通过配置快速搭建 QOS 平台，拥有基础指令。
- 通过继承 `Command` 接口，实现自定义指令，并通过配置文件轻松注册。
- 自定义 Banner 展示，提升用户体验。

本指南将详细介绍如何配置框架、开发自定义指令，以及框架的高级用法和最佳实践。

## 快速开始

### 添加依赖

首先，在项目的 `pom.xml` 文件中添加 spring-telqos 的依赖：

```xml
<?xml version="1.0" encoding="UTF-8"?>

<!--suppress MavenModelInspection, MavenModelVersionMissed -->
<project
        xmlns="http://maven.apache.org/POM/4.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
        http://maven.apache.org/xsd/maven-4.0.0.xsd"
>

    <!-- 省略其他配置 -->
    <dependencies>
        <!-- 省略其他配置 -->
        <dependency>
            <groupId>com.dwarfeng</groupId>
            <artifactId>spring-telqos-core</artifactId>
            <version>${spring-telqos.version}</version>
        </dependency>
        <!-- 省略其他配置 -->
    </dependencies>
    <!-- 省略其他配置 -->
</project>
```

如果需要使用框架提供的集成指令（如 Dubbo、Log4j2 等），还需要添加：

```xml
<?xml version="1.0" encoding="UTF-8"?>

<!--suppress MavenModelInspection, MavenModelVersionMissed -->
<project
        xmlns="http://maven.apache.org/POM/4.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
        http://maven.apache.org/xsd/maven-4.0.0.xsd"
>

    <!-- 省略其他配置 -->
    <dependencies>
        <!-- 省略其他配置 -->
        <dependency>
            <groupId>com.dwarfeng</groupId>
            <artifactId>spring-telqos-api</artifactId>
            <version>${spring-telqos.version}</version>
        </dependency>
        <!-- 省略其他配置 -->
    </dependencies>
    <!-- 省略其他配置 -->
</project>
```

### 基本配置

在 Spring 配置文件中添加 telqos 配置。以下是一个最小化配置示例：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:telqos="http://dwarfeng.com/schema/spring-telqos"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://dwarfeng.com/schema/spring-telqos
        http://dwarfeng.com/schema/spring-telqos/spring-telqos.xsd"
>

    <telqos:config>
        <telqos:connection-setting/>
        <telqos:command>
            <telqos:command-impl ref="myCommand"/>
        </telqos:command>
    </telqos:config>

    <!--suppress SpringXmlModelInspection -->
    <bean name="myCommand" class="com.example.telqos.MyCommand"/>
</beans>
```

### 创建第一个指令

创建一个简单的指令类，继承 `CliCommand`：

```java
package com.example.telqos;

import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.stack.command.Command.Context;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import org.apache.commons.cli.CommandLine;
import org.springframework.stereotype.Component;

@Component
public class MyCommand extends CliCommand {

    private static final String IDENTITY = "hello";
    private static final String DESCRIPTION = "输出欢迎信息";
    private static final String CMD_LINE_SYNTAX = "hello";

    public MyCommand() {
        super(IDENTITY, DESCRIPTION, CMD_LINE_SYNTAX);
    }

    @Override
    protected void executeWithCmd(Context context, CommandLine cmd) throws TelqosException {
        context.sendMessage("Hello, welcome to telqos!");
    }
}
```

### 启动和测试

启动 Spring 应用后，使用 telnet 客户端连接：

```bash
telnet localhost 23
```

连接成功后，您将看到 Banner 信息，然后可以输入指令：

```
lc          # 列出所有可用指令
hello       # 执行我们创建的指令
man hello   # 查看指令的详细帮助
quit        # 退出连接
```

## 配置详解

### 连接设置

`connection-setting` 元素用于配置 telnet 连接的相关参数：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:telqos="http://dwarfeng.com/schema/spring-telqos"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://dwarfeng.com/schema/spring-telqos
        http://dwarfeng.com/schema/spring-telqos/spring-telqos.xsd"
>

    <telqos:config>
        <telqos:connection-setting
                port="${telqos.port}"
                charset="${telqos.charset}"
                banner-url="classpath:telqos/my-banner.txt"
                whitelist-regex="${telqos.whitelist_regex}"
                blacklist-regex="${telqos.blacklist_regex}"
        />
    </telqos:config>
</beans>
```

#### 端口配置

- **属性名**：`port`。
- **类型**：`Integer`。
- **默认值**：`23`。
- **说明**：Telnet 服务监听的端口号。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:telqos="http://dwarfeng.com/schema/spring-telqos"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://dwarfeng.com/schema/spring-telqos
        http://dwarfeng.com/schema/spring-telqos/spring-telqos.xsd"
>

    <telqos:config>
        <telqos:connection-setting port="22000"/>
    </telqos:config>
</beans>
```

#### 字符集配置

- **属性名**：`charset`。
- **类型**：`String`。
- **默认值**：`UTF-8`。
- **说明**：服务端返回字符串的字符集。对于中文 Windows 系统，可能需要设置为 `GBK`。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:telqos="http://dwarfeng.com/schema/spring-telqos"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://dwarfeng.com/schema/spring-telqos
        http://dwarfeng.com/schema/spring-telqos/spring-telqos.xsd"
>

    <telqos:config>
        <telqos:connection-setting charset="GBK"/>
    </telqos:config>
</beans>
```

#### Banner 配置

- **属性名**：`banner-url`。
- **类型**：`String`。
- **默认值**：`classpath:telqos/banner.txt`。
- **说明**：Banner 文件的地址，新连接接入时向客户端发送的欢迎文本。

Banner 可以是项目资源文件或文件系统路径：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:telqos="http://dwarfeng.com/schema/spring-telqos"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://dwarfeng.com/schema/spring-telqos
        http://dwarfeng.com/schema/spring-telqos/spring-telqos.xsd"
>

    <!-- 使用 classpath 资源。 -->
    <telqos:config service-id="telqosService1" config-id="telqosConfig1">
        <telqos:connection-setting banner-url="classpath:telqos/my-banner.txt"/>
    </telqos:config>

    <!-- 使用文件系统路径 -->
    <telqos:config service-id="telqosService2" config-id="telqosConfig2">
        <telqos:connection-setting banner-url="file:/path/to/banner.txt"/>
    </telqos:config>
</beans>
```

**自定义 Banner**

您可以使用字符画生成工具创建自定义 Banner。推荐使用 [字符画生成工具](https://www.bootschool.net/ascii)，
默认的 banner 使用 `broadway` 字体生成。

生成字符画后，将其保存为文本文件，放置在项目的 `src/main/resources/telqos/` 目录下，然后在配置中指定文件路径。

#### 白名单/黑名单配置

- **属性名**：`whitelist-regex` / `blacklist-regex`。
- **类型**：`String`。
- **默认值**：空字符串（表示不启用）。
- **说明**：使用正则表达式匹配客户端 IP 地址，控制连接访问。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:telqos="http://dwarfeng.com/schema/spring-telqos"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://dwarfeng.com/schema/spring-telqos
        http://dwarfeng.com/schema/spring-telqos/spring-telqos.xsd"
>

    <!-- 只允许本地连接。 -->
    <telqos:config service-id="telqosService1" config-id="telqosConfig1">
        <telqos:connection-setting whitelist-regex="127\.0\.0\.1|::1"/>
    </telqos:config>

    <!-- 禁止特定 IP 段。 -->
    <telqos:config service-id="telqosService2" config-id="telqosConfig2">
        <telqos:connection-setting blacklist-regex="192\.168\.1\..*"/>
    </telqos:config>
</beans>
```

### 任务池配置

`task-pool` 元素用于配置指令执行的任务池。您可以选择引用外部线程池或直接配置线程池参数。

#### 引用外部线程池

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:telqos="http://dwarfeng.com/schema/spring-telqos"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://dwarfeng.com/schema/spring-telqos
        http://dwarfeng.com/schema/spring-telqos/spring-telqos.xsd"
>

    <telqos:config>
        <telqos:task-pool ref="executor"/>
    </telqos:config>

    <!--suppress SpringXmlModelInspection -->
    <bean name="executor" class="org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor">
        <!--suppress SpringXmlModelInspection -->
        <property name="corePoolSize" value="10"/>
        <!--suppress SpringXmlModelInspection -->
        <property name="maxPoolSize" value="20"/>
        <!--suppress SpringXmlModelInspection -->
        <property name="queueCapacity" value="100"/>
    </bean>
</beans>
```

#### 直接配置线程池参数

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:telqos="http://dwarfeng.com/schema/spring-telqos"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://dwarfeng.com/schema/spring-telqos
        http://dwarfeng.com/schema/spring-telqos/spring-telqos.xsd"
>

    <telqos:config>
        <telqos:task-pool pool-size="20" queue-capacity="100" keep-alive="60" rejection-policy="ABORT"/>
    </telqos:config>
</beans>
```

**参数说明**：

| 参数名                | 类型        | 说明                                                        |
|:-------------------|:----------|:----------------------------------------------------------|
| `pool-size`        | `Integer` | 线程池核心线程数和最大线程数                                            |
| `queue-capacity`   | `Integer` | 任务队列容量                                                    |
| `keep-alive`       | `Integer` | 线程空闲保持时间（秒）                                               |
| `rejection-policy` | `String`  | 拒绝策略，可选值：`ABORT`、`CALLER_RUNS`、`DISCARD`、`DISCARD_OLDEST` |

### 指令注册

`command` 元素用于注册自定义指令。框架提供了三种注册方式：

#### 使用 ref 引用

通过 Spring bean 的引用名称注册指令：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:telqos="http://dwarfeng.com/schema/spring-telqos"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://dwarfeng.com/schema/spring-telqos
        http://dwarfeng.com/schema/spring-telqos/spring-telqos.xsd"
>

    <telqos:config>
        <telqos:command>
            <telqos:command-impl ref="myCommand"/>
        </telqos:command>
    </telqos:config>

    <!--suppress SpringXmlModelInspection -->
    <bean name="myCommand" class="com.example.telqos.MyCommand"/>
</beans>
```

#### 使用 package-scan 扫描

扫描指定包下的所有带 `@TelqosCommand` 注解的类：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:telqos="http://dwarfeng.com/schema/spring-telqos"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://dwarfeng.com/schema/spring-telqos
        http://dwarfeng.com/schema/spring-telqos/spring-telqos.xsd"
>

    <telqos:config>
        <telqos:command>
            <telqos:command-impl package-scan="com.example.telqos"/>
        </telqos:command>
    </telqos:config>
</beans>
```

使用这种方式时，指令类需要使用 `@TelqosCommand` 注解：

```java
package com.example.telqos;

@TelqosCommand
public class MyCommand extends CliCommand {
    // ...
}
```

#### 使用 class 直接指定

直接指定指令类的全限定名（使用无参构造方法）：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:telqos="http://dwarfeng.com/schema/spring-telqos"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://dwarfeng.com/schema/spring-telqos
        http://dwarfeng.com/schema/spring-telqos/spring-telqos.xsd"
>

    <telqos:config>
        <telqos:command>
            <telqos:command-impl class="com.example.telqos.MyCommand"/>
        </telqos:command>
    </telqos:config>
</beans>
```

**注意**：所有配置属性均支持 Spring 的占位符表达式（`${property.name}`）。

## 自定义指令开发

### 指令接口说明

所有自定义指令都必须实现 `com.dwarfeng.springtelqos.stack.command.Command` 接口：

```java
public interface Command {

    /**
     * 获取命令的标识。
     *
     * @return 命令的标识。
     */
    String getIdentify();

    /**
     * 获取命令的简短描述。
     *
     * @return 命令的简短描述。
     */
    String getDescription();

    /**
     * 获取命令的详细帮助。
     *
     * @return 命令的详细帮助。
     */
    String getManual();

    /**
     * 执行指令。
     *
     * @param context 指令上下文。
     * @throws TelqosException               Telqos 异常。
     * @throws ConnectionTerminatedException 连接中断异常。
     */
    @SuppressWarnings("JavadocReference")
    void execute(Context context) throws TelqosException, ConnectionTerminatedException;
}
```

### 实现方式选择

框架提供了多个抽象类，您可以根据需求选择合适的实现方式：

| 抽象类               | 说明                                                      | 适用场景                 |
|:------------------|:--------------------------------------------------------|:---------------------|
| `AbstractCommand` | 提供 `getIdentify()` 的基础实现                                | 需要完全自定义指令逻辑          |
| `BasicCommand`    | 提供 `getIdentify()`、`getDescription()`、`getManual()` 的实现 | 需要自定义执行逻辑，但描述和帮助是静态的 |
| `CliCommand`      | 基于 Apache Commons CLI 的实现，支持命令行选项解析                     | **推荐**，大多数场景下使用      |

### 使用 CliCommand 实现指令

`CliCommand` 是最常用的实现方式，它基于 Apache Commons CLI 框架，提供了强大的命令行选项解析能力。

#### 基本实现步骤

1. 继承 `CliCommand` 类。
2. 在构造函数中调用 `super()`，传入指令标识、描述和命令行语法。
3. 实现 `executeWithCmd()` 方法，处理指令逻辑。
4. 可选实现 `buildOptions()` 方法，定义命令行选项。

#### 简单指令示例

以下是一个无参数的简单指令：

```java
package com.example.telqos;

@TelqosCommand
public class MyCommand extends CliCommand {

    private static final String IDENTITY = "hello";
    private static final String DESCRIPTION = "输出欢迎信息";
    private static final String CMD_LINE_SYNTAX = "hello";

    public MyCommand() {
        super(IDENTITY, DESCRIPTION, CMD_LINE_SYNTAX);
    }

    @Override
    protected void executeWithCmd(Context context, CommandLine cmd) throws TelqosException {
        context.sendMessage("Hello, welcome to telqos!");
    }
}
```

#### 带选项的指令示例

以下是一个带命令行选项的指令：

```java
package com.example.telqos;

@TelqosCommand
public class FoobarCommand extends CliCommand {

    private static final String COMMAND_OPTION_GET = "get";
    private static final String COMMAND_OPTION_SET = "set";
    private static final String COMMAND_OPTION_VALUE = "value";

    private static final String[] COMMAND_OPTION_ARRAY = new String[]{
            COMMAND_OPTION_GET,
            COMMAND_OPTION_SET
    };

    private static final String IDENTITY = "foobar";
    private static final String DESCRIPTION = "Foobar 操作指令";
    private static final String CMD_LINE_SYNTAX = "foobar [-get|--get] [-set|--set] [-value|--value value]";

    public FoobarCommand() {
        super(IDENTITY, DESCRIPTION, CMD_LINE_SYNTAX);
    }

    @Override
    protected List<Option> buildOptions() {
        List<Option> list = new ArrayList<>();
        list.add(Option.builder().longOpt(COMMAND_OPTION_GET).desc("获取值").build());
        list.add(Option.builder().longOpt(COMMAND_OPTION_SET).desc("设置值").build());
        list.add(Option.builder(COMMAND_OPTION_VALUE).longOpt(COMMAND_OPTION_VALUE)
                .desc("设置的值").hasArg().type(String.class).build());
        return list;
    }

    @Override
    protected void executeWithCmd(Context context, CommandLine cmd) throws TelqosException {
        try {
            if (cmd.hasOption(COMMAND_OPTION_GET)) {
                // 处理获取操作
                context.sendMessage("当前值: example");
            } else if (cmd.hasOption(COMMAND_OPTION_SET)) {
                // 处理设置操作
                if (cmd.hasOption(COMMAND_OPTION_VALUE)) {
                    String value = (String) cmd.getParsedOptionValue(COMMAND_OPTION_VALUE);
                    context.sendMessage("设置值: " + value);
                } else {
                    context.sendMessage("错误: 设置操作需要指定 -value 参数");
                }
            } else {
                context.sendMessage("请指定操作: -get 或 -set");
                context.sendMessage(CMD_LINE_SYNTAX);
            }
        } catch (Exception e) {
            throw new TelqosException(e);
        }
    }
}
```

#### 交互式指令示例

以下是一个需要用户交互的指令：

```java
package com.example.telqos;

@TelqosCommand
public class InteractiveCommand extends CliCommand {

    private static final String IDENTITY = "interactive";
    private static final String DESCRIPTION = "交互式指令示例";
    private static final String CMD_LINE_SYNTAX = "interactive";

    public InteractiveCommand() {
        super(IDENTITY, DESCRIPTION, CMD_LINE_SYNTAX);
    }

    @Override
    protected void executeWithCmd(Context context, CommandLine cmd)
            throws TelqosException, ConnectionTerminatedException {
        context.sendMessage("请输入您的姓名:");
        String name = context.receiveMessage();

        context.sendMessage("请输入您的年龄:");
        String ageStr = context.receiveMessage();

        int age = Integer.parseInt(ageStr);
        context.sendMessage("您好，" + name + "！您今年 " + age + " 岁。");
    }
}
```

### 指令注册

指令可以通过以下方式注册到框架中：

#### 使用 @TelqosCommand 注解

`@TelqosCommand` 注解是 `@Component` 的别名，用于自动扫描注册：

```java
package com.example.telqos;

@TelqosCommand
public class MyCommand extends CliCommand {
    // ...
}
```

在配置中使用 `package-scan` 扫描：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:telqos="http://dwarfeng.com/schema/spring-telqos"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://dwarfeng.com/schema/spring-telqos
        http://dwarfeng.com/schema/spring-telqos/spring-telqos.xsd"
>

    <telqos:config>
        <telqos:command>
            <telqos:command-impl package-scan="com.example.telqos"/>
        </telqos:command>
    </telqos:config>
</beans>
```

#### 使用 @Component 注解

也可以直接使用 Spring 的 `@Component` 注解：

```java
package com.example.telqos;

@Component
public class MyCommand extends CliCommand {
    // ...
}
```

#### 手动注册

在 Spring 配置文件中手动定义 bean：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:telqos="http://dwarfeng.com/schema/spring-telqos"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://dwarfeng.com/schema/spring-telqos
        http://dwarfeng.com/schema/spring-telqos/spring-telqos.xsd"
>

    <telqos:config>
        <telqos:command>
            <telqos:command-impl ref="myCommand"/>
        </telqos:command>
    </telqos:config>

    <!--suppress SpringXmlModelInspection -->
    <bean name="myCommand" class="com.example.telqos.MyCommand"/>
</beans>
```

## Context 接口详解

`Context` 接口提供了指令执行时的上下文信息，以及与客户端交互的方法。

```java
public interface Context {

    /**
     * 获取执行指令的客户端地址。
     *
     * @return 客户端地址。
     */
    String getAddress();

    /**
     * 获取指令执行的选项。
     *
     * @return 指令执行的选项。
     */
    String getOption();

    /**
     * 列出指令。
     *
     * @return 所有指令的标识符组成的列表。
     */
    List<String> getCommandIdentities();

    /**
     * 获取指令的描述。
     *
     * @param identity 指定指令的标识符。
     * @return 指令的描述。
     */
    String getCommandDescription(String identity);

    /**
     * 获取指令的详细说明。
     *
     * @param identity 指定指令的标识符。
     * @return 指令的详细说明。
     */
    String getCommandManual(String identity);

    /**
     * 向客户端发送一条信息（换行）。
     *
     * @param message 指定的信息。
     * @throws TelqosException Telqos 异常。
     */
    @SuppressWarnings("JavadocReference")
    void sendMessage(String message) throws TelqosException;

    /**
     * 接收用户的输入信息，并在输入之前一直阻塞。
     * <p>
     * 请注意：{@link Command#execute(Context)} 调用该方法时不能捕获 ConnectionTerminatedException 异常，必须抛出。
     *
     * @return 用户输入的信息。
     * @throws TelqosException               Telqos 异常。
     * @throws ConnectionTerminatedException 在用户输入完成之前连接中断。
     */
    @SuppressWarnings("JavadocReference")
    String receiveMessage() throws TelqosException, ConnectionTerminatedException;

    /**
     * 退出。
     *
     * @throws TelqosException Telqos 异常。
     */
    @SuppressWarnings("JavadocReference")
    void quit() throws TelqosException;
}
```

### 核心方法

#### 发送消息

`sendMessage(String message)` 方法用于向客户端发送一条消息（自动换行）。

```java
private void doSomeBusiness(Context context, CommandLine cmd) throws Exception {
    context.sendMessage("这是一条消息");
}
```

#### 接收用户输入

`receiveMessage()` 方法用于接收用户的输入信息，在用户输入完成之前一直阻塞。

```java
private void doSomeBusiness(Context context, CommandLine cmd) throws Exception {
    String input = context.receiveMessage();
    // 处理用户输入
}
```

**注意**：调用该方法时不能捕获 `ConnectionTerminatedException` 异常，必须抛出。

#### 获取客户端地址

`getAddress()` 方法用于获取执行指令的客户端地址。

```java
private void doSomeBusiness(Context context, CommandLine cmd) throws Exception {
    String address = context.getAddress();
    // 例如: "127.0.0.1:52345"
    context.sendMessage("客户端地址: " + address);
}
```

#### 获取指令选项

`getOption()` 方法用于获取指令执行的选项（不包含指令标识本身）。

```java
private void doSomeBusiness(Context context, CommandLine cmd) throws Exception {
    // 如果用户输入: "foobar -get -value test"
    String option = context.getOption();
    // option = "-get -value test"
    context.sendMessage("选项: " + option);
}
```

#### 退出连接

`quit()` 方法用于退出当前连接。

```java
private void doSomeBusiness(Context context, CommandLine cmd) throws Exception {
    context.sendMessage("正在退出...");
    context.quit();
}
```

### 辅助方法

#### 列出所有指令标识

`getCommandIdentities()` 方法用于列出所有可用指令的标识符。

```java
private void doSomeBusiness(Context context, CommandLine cmd) throws Exception {
    List<String> identities = context.getCommandIdentities();
    for (String identity : identities) {
        context.sendMessage("指令: " + identity);
    }
}
```

#### 获取指令描述

`getCommandDescription(String identity)` 方法用于获取指定指令的描述。

```java
private void doSomeBusiness(Context context, CommandLine cmd) throws Exception {
    String description = context.getCommandDescription("hello");
    context.sendMessage("指令描述: " + description);
}
```

#### 获取指令详细帮助

`getCommandManual(String identity)` 方法用于获取指定指令的详细帮助。

```java
private void doSomeBusiness(Context context, CommandLine cmd) throws Exception {
    String manual = context.getCommandManual("hello");
    context.sendMessage(manual);
}
```

## 框架默认指令

框架提供了三个默认指令，无需配置即可使用：

### lc - 列出指令

列出所有可用的指令。

**用法**：

```
lc [-p prefix|--prefix prefix]
```

**选项**：

- `-p, --prefix`：列出包含指定前缀的命令。

**示例**：

```
lc              # 列出所有指令
lc -p foo       # 列出所有以 "foo" 开头的指令
```

### man - 显示帮助

显示指定指令的详细帮助信息。

**用法**：

```
man [command]
```

**参数**：

- `command`：可选，指定要查看的指令标识。如果不指定，则显示 `man` 指令本身的帮助。

**示例**：

```
man            # 显示 man 指令的帮助
man hello      # 显示 hello 指令的详细帮助
```

### quit - 退出连接

退出当前 telnet 连接。

**用法**：

```
quit
```

执行后，连接将被关闭。

## 最佳实践

### 指令命名规范

- 指令标识只能包含字母（`a-z`，`A-Z`）、数字（`0-9`）和下划线（`_`）。
- 指令标识不能以数字开头。
- 指令标识应该简洁明了，易于记忆。
- 避免使用过长的标识符。

**推荐**：

```
memory
uptime
shutdown
```

**不推荐**：

```
HelloWorldCommand
getConfigurationValue
very_long_command_name
```

**错误示例**：

```
123start      # 以数字开头
list-all      # 包含非法字符 '-'
```

### 错误处理建议

1. **捕获并转换异常**：将业务异常转换为 `TelqosException`。

   ```java
   package com.example.telqos;
   
   import com.dwarfeng.springtelqos.sdk.command.CliCommand;
   import com.dwarfeng.springtelqos.stack.command.Command.Context;
   import com.dwarfeng.springtelqos.stack.exception.TelqosException;
   import org.apache.commons.cli.CommandLine;
   import org.springframework.stereotype.Component;
   
   @Component
   public class MyCommand extends CliCommand {
   
       private static final String IDENTITY = "hello";
       private static final String DESCRIPTION = "输出欢迎信息";
       private static final String CMD_LINE_SYNTAX = "hello";
   
       public MyCommand() {
           super(IDENTITY, DESCRIPTION, CMD_LINE_SYNTAX);
       }
   
       @Override
       protected void executeWithCmd(Context context, CommandLine cmd) throws TelqosException {
           try {
               // 业务逻辑。
               doSomeBusiness(context, cmd);
           } catch (BusinessException e) {
               throw new TelqosException(e);
           }
       }
   }
   ```

2. **提供友好的错误信息**：向用户发送清晰的错误提示。

   ```java
   package com.example.telqos;
   
   import com.dwarfeng.springtelqos.sdk.command.CliCommand;
   import com.dwarfeng.springtelqos.stack.command.Command.Context;
   import com.dwarfeng.springtelqos.stack.exception.TelqosException;
   import org.apache.commons.cli.CommandLine;
   import org.springframework.stereotype.Component;
   
   @Component
   public class MyCommand extends CliCommand {
   
       private static final String IDENTITY = "hello";
       private static final String DESCRIPTION = "输出欢迎信息";
       private static final String CMD_LINE_SYNTAX = "hello";
   
       public MyCommand() {
           super(IDENTITY, DESCRIPTION, CMD_LINE_SYNTAX);
       }
   
       @Override
       protected void executeWithCmd(Context context, CommandLine cmd) throws TelqosException {
           try {
               // 业务逻辑。
               doSomeBusiness(context, cmd);
           } catch (BusinessException e) {
               context.sendMessage("错误: " + e.getMessage());
               context.sendMessage("请检查输入参数是否正确");
           }
       }
   }
   ```

3. **不要捕获 ConnectionTerminatedException**：该异常表示用户中断连接，应该直接抛出。

### 性能优化建议

1. **避免长时间阻塞**：指令执行应该尽可能快速，避免长时间占用线程。

2. **合理使用异步处理**：对于耗时操作，考虑使用异步方式处理。

3. **控制输出量**：避免一次性输出大量数据，考虑分页或流式输出。

### 安全注意事项

1. **访问控制**：使用白名单/黑名单限制连接来源。

2. **输入验证**：对用户输入进行严格验证，防止执行恶意逻辑。

3. **敏感信息**：不要在指令输出中暴露敏感信息（如密码、密钥等）。

4. **权限控制**：对于敏感操作，应该实现权限验证机制。

## 常见问题

### 字符编码问题

**问题**：在 Windows 系统上，中文字符显示乱码。

**解决方案**：将字符集配置为 `GBK`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:telqos="http://dwarfeng.com/schema/spring-telqos"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://dwarfeng.com/schema/spring-telqos
        http://dwarfeng.com/schema/spring-telqos/spring-telqos.xsd"
>

    <telqos:config>
        <telqos:connection-setting charset="GBK"/>
    </telqos:config>
</beans>
```

### 连接问题

**问题**：无法连接到 telnet 服务。

**解决方案**：

1. 检查端口是否正确配置。
2. 检查防火墙设置。
3. 检查白名单/黑名单配置是否阻止了连接。

### 指令不生效问题

**问题**：自定义指令无法被识别。

**解决方案**：

1. 检查指令类是否正确注册到 Spring 容器。
2. 检查 `package-scan` 配置的包路径是否正确。
3. 检查指令类是否使用了 `@TelqosCommand` 或 `@Component` 注解。
4. 检查指令的 `getIdentify()` 返回值是否符合规范（只能包含字母、数字和下划线，且不能以数字开头）。

### Banner 不显示问题

**问题**：Banner 文件配置了但没有显示。

**解决方案**：

1. 检查 Banner 文件路径是否正确。
2. 检查文件是否在 classpath 中（使用 `classpath:` 前缀）。
3. 检查文件编码是否与字符集配置一致。
