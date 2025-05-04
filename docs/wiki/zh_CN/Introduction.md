# spring-telqos

一款基于Spring框架的 telnet QOS 服务框架，用于快速构建可用并美观的 QOS 服务平台。

## 特性

* 能够轻松地通过配置搭建一个空的 QOS 平台，拥有基础指令。
* 通过继承 `Command` 接口，实现自己需要的指令，并通过配置文件轻松地注册到 QOS 平台中。
* 自定义 Banner 展示，使得使用该 QOS 平台的人员进入平台后眼前一亮，增加软件的震撼度。

## 文档

该项目的文档位于 [docs](../../../docs) 目录下，包括：

### wiki

wiki 为项目的开发人员为本项目编写的详细文档，包含不同语言的版本，主要入口为：

1. [简介](./Introduction.md) - 即本文件。
2. [目录](./Contents.md) - 文档目录。

## maven坐标

1. 对于项目本体。
   ```xml
   <dependency>
       <groupId>com.dwarfeng</groupId>
       <artifactId>spring-telqos-core</artifactId>
       <version>${spring-telqos.version}</version>
   </dependency>
   ```

2. 对于项目的api。
   ```xml
   <dependency>
       <groupId>com.dwarfeng</groupId>
       <artifactId>spring-telqos-api</artifactId>
       <version>${spring-telqos.version}</version>
   </dependency>
   ```

## 使用方式

以下的操作步骤可以通过启动 `test` 源目录中的 `com.dwarfeng.springtelqos.impl.service.Example` 观察实际效果。

1. 添加依赖。

2. 在Spring中添加如下配置。
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
           <telqos:connection-setting port="${telqos.port}" charset="${telqos.charset}"/>
           <telqos:task-pool ref="executor"/>
           <telqos:command>
               <telqos:command-impl ref="shutdownCommand"/>
               <telqos:command-impl ref="dubboCommand"/>
               <telqos:command-impl ref="memoryCommand"/>
           </telqos:command>
       </telqos:config>
   
       <bean name="shutdownCommand" class="com.dwarfeng.springtelqos.api.integration.springterminator.ShutdownCommand">
           <!--suppress SpringXmlModelInspection -->
           <property name="terminator" ref="terminator"/>
           <property name="restartEnabled" value="false"/>
       </bean>
   
       <bean name="dubboCommand" class="com.dwarfeng.springtelqos.api.integration.dubbo.DubboCommand"/>
   
       <bean name="memoryCommand" class="com.dwarfeng.springtelqos.api.integration.system.MemoryCommand"/>
   </beans>
   ```
   注：该文件中的所有属性均支持 `Spring place-holder expression`。

3. 编写自定义指令类，继承 `Command`。

   `spring-telqos` 中提供了多种 `Command` 接口的抽象实现，合理地继承这些抽象实现能够提高开发的速度。

   ```java
   @Component
   public class HelloWorldCommand extends CliCommand {
   
       private static final String IDENTITY = "hello";
       private static final String DESCRIPTION = "输出 Hello World!";
       private static final String CMD_LINE_SYNTAX = "hello";
   
       public HelloWorldCommand() {
           super(IDENTITY, DESCRIPTION, CMD_LINE_SYNTAX);
       }
   
       @Override
       protected List<Option> buildOptions() {
           return Collections.emptyList();
       }
   
       @Override
       protected void executeWithCmd(Context context, CommandLine cmd) throws TelqosException {
           context.sendMessage("Hello World!");
       }
   }
   ```

4. 在 `ApplicationContext` 中注入`HelloWorldCommand`对象，并启动程序。

5. 打开 telnet 客户端，以 windows 平台举例。
   1. 运行 `powershell`
   2. 依次输入命令
      ```cmd
      telnet localhost 23
      lc
      hello
      quit
      ```
   3. 观察控制台输出
      ```
      ------------------------------------------------------------------------------------------------
      8888888 8888888888 8 8888888888   8 8888         ,o888888o.         ,o888888o.       d888888o.
            8 8888       8 8888         8 8888      . 8888     `88.    . 8888     `88.   .`8888:' `88.
            8 8888       8 8888         8 8888     ,8 8888       `8b  ,8 8888       `8b  8.`8888.   Y8
            8 8888       8 8888         8 8888     88 8888        `8b 88 8888        `8b `8.`8888.
            8 8888       8 888888888888 8 8888     88 8888         88 88 8888         88  `8.`8888.
            8 8888       8 8888         8 8888     88 8888     `8. 88 88 8888         88   `8.`8888.
            8 8888       8 8888         8 8888     88 8888      `8,8P 88 8888        ,8P    `8.`8888.
            8 8888       8 8888         8 8888     `8 8888       ;8P  `8 8888       ,8P 8b   `8.`8888.
            8 8888       8 8888         8 8888      ` 8888     ,88'8.  ` 8888     ,88'  `8b.  ;8.`8888
            8 8888       8 888888888888 8 888888888888 `8888888P'  `8.    `8888888P'     `Y8888P ,88P'
      ------------------------------------------------------------------------------------------------
      Telnet QOS 运维系统
      版本: x.x.x.x                                                                  Powered By 赵扶风
      -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
      
      
      欢迎您 [0:0:0:0:0:0:0:1]:52447
      
      
      lc
      1   hello   输出 Hello World!
      2   lc      列出指令
      3   man     显示指令的详细信息
      4   quit    退出
      ---------------------------
      共 4 条
      OK
      
      hello
      Hello World!
      OK
      
      quit
      Bye
      服务端主动与您中断连接
      再见!
      
      
      遗失对主机的连接。
      ```

## 附录

1. [字符画生成工具](https://www.bootschool.net/ascii) 默认的 banner 使用 `broadway` 字体生成。
