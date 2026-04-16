# ChangeLog

## Release_1.2.0_20260420_build_A

### 功能构建

- 优化开发环境支持。
  - 在 .gitignore 中添加 Vibe Coding 相关文件的忽略规则。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.16_20251231_build_A

### 功能构建

- 更新 README.md。

- Wiki 更新。
  - docs/wiki/zh-CN/UsageGuide.md。
  - docs/wiki/zh-CN/Introduction.md。

- `spring-telqos-core` 子模块类优化注释、文档注释格式、代码换行格式。
  - com.dwarfeng.springtelqos.impl.service.TelqosServiceImpl。
  - com.dwarfeng.springtelqos.node.config.ParserUtil。
  - com.dwarfeng.springtelqos.node.config.SpringTelqosDefinitionParser。
  - com.dwarfeng.springtelqos.sdk.command.CliCommand。
  - com.dwarfeng.springtelqos.stack.bean.TelqosConfig。
  - com.dwarfeng.springtelqos.stack.command.Command。
  - com.dwarfeng.springtelqos.stack.command.Context。
  - com.dwarfeng.springtelqos.stack.exception.TelqosException。
  - com.dwarfeng.springtelqos.stack.service.TelqosService。

- 优化文件格式。
  - 优化 `application-context-*.xml` 文件的格式。

- 依赖升级。
  - 升级 `netty` 依赖版本为 `4.2.9.Final` 并解决兼容性问题，以规避漏洞。
  - 升级 `log4j2` 依赖版本为 `2.25.3` 以规避漏洞。
  - 升级 `dutil` 依赖版本为 `0.4.0.a-beta` 以规避漏洞。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.15_20251105_build_A

### 功能构建

- Wiki 更新。
  - docs/wiki/zh-CN/VersionBlacklist.md。

- 优化 `docs/wiki` 目录结构。
  - 将 `docs/wiki/en_US` 目录重命名为 `en-US`，以符合 rfc5646 规范。
  - 将 `docs/wiki/zh_CN` 目录重命名为 `zh-CN`，以符合 rfc5646 规范。
  - 更新 `docs/wiki/README.md` 中的链接指向。
  - 更新 `README.md` 中的链接指向。

- 优化 `api` 模块的配置文件。
  - 优化 `spring/application-context-dubbo.xml` 中部分配置项的名称。
  - 优化 `dubbo/connection.properties` 中部分配置项的名称。
  - 在 `spring/application-context-dubbo.xml` 中增加超时时间的配置选项。
  - 在 `dubbo/connection.properties` 中增加超时时间的配置选项。
  - 在 `spring/application-context-dubbo.xml` 中增加分组配置。
  - 在 `dubbo/connection.properties` 中增加分组配置。

- 用户体验优化。
  - 优化未知命令提示信息，引导用户使用命令列表查看可用指令。

- 代码优化。
  - 提取核心命令类的命令标识符至 Constants 常量类中，提升代码可维护性。

- 新增集成指令。
  - com.dwarfeng.springtelqos.api.integration.system.JmxRemoteCommand。
  - com.dwarfeng.springtelqos.api.integration.system.UptimeCommand。

- 优化开发环境支持。
  - 在 .gitignore 中添加 VSCode 相关文件的忽略规则。
  - 在 .gitignore 中添加 Cursor IDE 相关文件的忽略规则。

- 依赖升级。
  - 升级 `zookeeper` 依赖版本为 `3.9.4` 以规避漏洞。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.14_20250729_build_A

### 功能构建

- Wiki 编写。
  - docs/wiki/zh_CN/InstallBySourceCode.md。

- 依赖升级。
  - 升级 `commons-lang3` 依赖版本为 `3.18.0` 以规避漏洞。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.13_20250506_build_A

### 功能构建

- Wiki 编写。
  - docs/wiki/zh_CN/VersionBlacklist.md。

- 优化所有 util 类，禁止其实例化。
  - com.dwarfeng.springtelqos.impl.service.ChannelUtil。
  - com.dwarfeng.springtelqos.node.config.ParserUtil。

- 功能优化。
  - 优化一个项目中存在多个 `telqos:config` 配置时，项目中 Spring Bean 的命名规则。

- 优化部分文件中的内容/注释格式。
  - spring-telqos.xsd。
  - SpringTelqosDefinitionParser.java
  - README.md。
  - docs/wiki/zh_CN/Introduction.md。

### Bug 修复

- 修复一个项目中存在多个 `telqos:config` 配置时，程序阻塞的 bug。
  - 修复 com.dwarfeng.springtelqos.node.config.SpringTelqosDefinitionParser 中的相关 bug。

- 修正 pom.xml 中不正确的配置。

### 功能移除

- (无)

---

## Release_1.1.12_20250504_build_A

### 功能构建

- 更新 README.md。

- Wiki 编写。
  - 构建 wiki 目录结构。
  - docs/wiki/en_US/Contents.md。
  - docs/wiki/en_US/Introduction.md。
  - docs/wiki/zh_CN/Contents.md。
  - docs/wiki/zh_CN/Introduction.md。

- 依赖升级。
  - 升级 `netty` 依赖版本为 `4.1.119.Final` 以规避漏洞。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.11_20241117_build_B

### 功能构建

- 依赖优化。
  - 优化部分依赖的排除项，以避免潜在的 `netty` 版本冲突问题。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.11_20241117_build_A

### 功能构建

- 依赖升级。
  - 升级 `spring` 依赖版本为 `5.3.39` 以规避漏洞。
  - 升级 `netty` 依赖版本为 `4.1.115.Final` 以规避漏洞。
  - 升级 `zookeeper` 依赖版本为 `3.9.3` 以规避漏洞。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.10_20240730_build_A

### 功能构建

- 依赖升级。
  - 升级 `spring` 依赖版本为 `5.3.37` 以规避漏洞。
  - 升级 `slf4j` 依赖版本为 `1.7.36` 以规避漏洞。
  - 升级 `netty` 依赖版本为 `4.1.108.Final` 以规避漏洞。
  - 升级 `spring-terminator` 依赖版本为 `1.0.13.a` 以规避漏洞。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.9_20240415_build_A

### 功能构建

- 优化文件格式。
  - 优化 `application-context-*.xml` 文件的格式。
  - 优化 `*.properties` 文件的格式。

- 新增集成指令。
  - com.dwarfeng.springtelqos.api.integration.log4j2.Log4j2Command。

- 优化部分指令文案。
  - com.dwarfeng.springtelqos.api.integration.springterminator.ShutdownCommand。

- 依赖升级。
  - 升级 `guava` 依赖版本为 `32.0.1-jre` 以规避漏洞。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.8_20231227_build_A

### 功能构建

- 依赖升级。
  - 升级 `spring` 依赖版本为 `5.3.31` 以规避漏洞。
  - 升级 `netty` 依赖版本为 `4.1.104.Final` 以规避漏洞。
  - 升级 `spring-terminator` 依赖版本为 `1.0.12.a` 以规避漏洞。
  - 升级 `dubbo` 依赖版本为 `2.7.22` 以规避漏洞。
  - 升级 `zookeeper` 依赖版本为 `3.7.2` 以规避漏洞。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.7_20230810_build_A

### 功能构建

- 实现 package-scan 解析指令的功能。
  - 在 xml 配置中，使用 `<telqos:command-impl package-scan="xxx"/>` 配置，以扫描指定包下的所有指令。
  - 需要被扫描的类必须拥有 `@TelqosCommand` 注解。
  - 需要被扫描的类必须实现 `TelqosCommand` 接口。

- 依赖升级。
  - 升级 `spring` 依赖版本为 `32.0.0-jre`。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.6_20230420_build_A

### 功能构建

- 依赖升级。
  - 升级 `spring` 依赖版本为 `5.3.27`。
  - 升级 `netty` 依赖版本为 `4.1.86.Final`。
  - 升级 `spring-terminator` 依赖版本为 `1.0.11.a`。
  - 升级 `snakeyaml` 依赖版本为 `2.0`。
  - 升级 `dubbo` 依赖版本为 `2.7.21`。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.5_20221118_build_A

### 功能构建

- 增加依赖。
  - 增加依赖 `gson` 以规避漏洞，版本为 `2.8.9`。
  - 增加依赖 `snakeyaml` 以规避漏洞，版本为 `1.33`。

- 依赖升级。
  - 升级 `junit` 依赖版本为 `4.13.2`。
  - 升级 `slf4j` 依赖版本为 `1.7.5`。
  - 升级 `commons-lang3` 依赖版本为 `3.12.0`。
  - 升级 `spring-terminator` 依赖版本为 `1.0.10.a`。
  - 升级 `dubbo` 依赖版本为 `2.7.18`。
  - 升级 `curator` 依赖版本为 `4.3.0`。
  - 升级 `dutil` 依赖版本为 `beta-0.3.1.a` 以规避漏洞。

### Bug 修复

- 修正部分 properties 配置文件错误的字符集。
- 修正所有 xml 配置文件的格式错误。

### 功能移除

- (无)

---

## Release_1.1.4_20220905_build_A

### 功能构建

- 插件升级。
  - 升级 `maven-deploy-plugin` 插件版本为 `2.8.2`。

- 依赖升级。
  - 升级 `spring-terminator` 依赖版本为 `1.0.9.a`。
  - 升级 `dutil` 依赖版本为 `beta-0.3.1.a` 以规避漏洞。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.3_20220606_build_A

### 功能构建

- 依赖升级，并修改过时的 API 调用。
  - 升级 `junit` 依赖版本为 `4.13.1` 以规避漏洞。
  - 升级 `spring` 依赖版本为 `5.3.20` 以规避漏洞。
  - 升级 `log4j2` 依赖版本为 `2.17.2` 以规避漏洞。
  - 升级 `netty` 依赖版本为 `4.1.77.Final` 以规避漏洞。
  - 升级 `spring-terminator` 依赖版本为 `1.0.7.a` 以规避漏洞。
  - 升级 `dubbo` 依赖版本为 `2.7.15` 以规避漏洞。
  - 升级 `dutil` 依赖版本为 `beta-0.2.2.a` 以规避漏洞。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.2_20201212_build_A

### 功能构建

- 升级 `log4j2` 依赖版本为 `2.15.0` 以规避 `CVE-2021-44228` 漏洞。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.1_20201013_build_A

### 功能构建

- 新增集成指令。
  - com.dwarfeng.springtelqos.api.integration.system.MemoryCommand
- 为 spring-telqos-api 模块添加测试用代码。

### Bug 修复

- 修正 spring-telqos.xsd 中不正确的约束以及注释。
- 修正 ShutdownCommand 中错误的命令描述。

### 功能移除

- (无)

---

## Release_1.1.0_20201011_build_A

### 功能构建

- 将项目主体代码移动至子模块 spring-telqos-core。
- 添加模块 spring-telqos-api。
  - 与 spring-terminator 进行集成。
  - 与 dubbo 进行集成。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.0.3_20201007_build_A

### 功能构建

- 为命令任务的执行增加了 5ms 的延时时间，解决了某些指令执行过快造成部分 telnet 客户端换行显示异常的问题。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.0.2_20201004_build_A

### 功能构建

- 完善 CliCommand 类，补全构造入口。
- 美化 ListCommandCommand 的输出样式。
- 完善设备登录以及连接中断时的日志记录。

### Bug 修复

- 修复 CliCommand 处理带引号的参数时行为不正常的 bug。
- 修复 CliCommand 解析命令行发生异常后仍然会执行 executeWithCmd 方法的bug。
- 修复 TelqosServiceImpl 处理中断连接事件时引发潜在的 NullPointerException 的 bug。

### 功能移除

- (无)

---

## Release_1.0.1_20200927_build_A

### 功能构建

- 优化 TelqosServiceImpl 中几处冗余的代码。
- 将测试文件更名为 Example。
- 去除示例文件对 spring-terminator 的依赖。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.0.0_20200924_build_A

### 功能构建

- 实现项目所有的预期功能。

### Bug 修复

- (无)

### 功能移除

- (无)
