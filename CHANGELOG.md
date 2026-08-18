# ChangeLog

## Release_2.0.3_20260818_build_A

### 功能构建

- 优化部分示例的控制台输出方法。
  - com.dwarfeng.springtelqos.api.example.Example。
  - com.dwarfeng.springtelqos.node.example.Example。

- 依赖升级。
  - 升级 `subgrade` 依赖版本为 `1.8.4.a` 以规避漏洞。

- 优化文件格式。
  - 优化 `pom.xml` 文件的格式。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_2.0.2_20260527_build_A

### 功能构建

- Wiki 编写。
  - docs/wiki/zh-CN/UseWithMaven.md。

- 依赖升级。
  - 升级 `subgrade` 依赖版本为 `1.8.3.a` 以规避漏洞。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_2.0.1_20260517_build_A

### 功能构建

- Wiki 编写。
  - docs/wiki/zh-CN/QuickStart.md。

- 优化 `spring-telqos-api` 子模块中测试文件中的 telqos 配置项。
  - src/test/resources/spring/application-context-telqos.xml。

- 优化 `spring-telqos-core` 子模块中测试文件中的 telqos 配置项。
  - src/test/resources/spring/application-context-telqos.xml。
  - src/test/resources/telqos/connection.properties。

- 优化 xsd 配置项解析机制。
  - 优化 `telqos:command-impl` 的 `package-scan` 解析逻辑，使多包扫描时支持不同包同类名的指令实现类共存。

### Bug 修复

- 补全 `.gitignore` 文件中缺失的配置。

### 功能移除

- (无)

---

## Release_2.0.0_20260506_build_A

### 功能构建

- 更新 README.md。

- Wiki 更新。
  - docs/wiki/zh-CN/InstallBySourceCode.md。
  - docs/wiki/zh-CN/UsageGuide.md。
  - docs/wiki/zh-CN/Introduction.md。

- 优化 `spring-telqos-api` 模块的预设集成指令，使其支持包扫描机制。
  - com.dwarfeng.springtelqos.api.integration.dubbo.DubboCommand。
  - com.dwarfeng.springtelqos.api.integration.log4j2.Log4j2Command。
  - com.dwarfeng.springtelqos.api.integration.system.JmxRemoteCommand。
  - com.dwarfeng.springtelqos.api.integration.system.MemoryCommand。
  - com.dwarfeng.springtelqos.api.integration.system.UptimeCommand。

- 优化 xsd 配置项解析机制。
  - 将更多的 xsd 配置常量定义在 `com.dwarfeng.springtelqos.sdk.util.Constants` 中。
  - 调整 `META-INF/spring-telqos.xsd` 中的默认值，使用 SpEL 表达式引用 `Constants` 中的常量。
  - 在 `com.dwarfeng.springtelqos.sdk.util.BeanDefinitionParserUtil` 中增加必要的工具方法。

- 优化 xsd 配置项名称。
  - 将 `telqos:config:config-id` 配置项更名为 `telqos:config:config-name`。
  - 将 `telqos:config:command-impl:id` 配置项更名为 `telqos:config:command-impl:command-name`。
  - 将 `telqos:config:command-impl:ref` 配置项更名为 `telqos:config:command-impl:command-ref`。
  - 将 `telqos:qos:handler-name` 配置项更名为 `telqos:qos:handler-ref`。

- Telqos 处理器功能优化。
  - `TelqosHandlerImpl` 中指令执行器上下文内部实现的列出指令的运行时标识方法返回的结果列表排序方法优化。

- 新增命名策略。
  - 新增 `com.dwarfeng.springtelqos.stack.naming.NamingStrategy` 接口，实现指令标识符映射。
  - 新增 `com.dwarfeng.springtelqos.stack.struct.TelqosConfig` 中相关的配置项。
  - 调整 `com.dwarfeng.springtelqos.node.configuration.SpringTelqosConfigDefinitionParser` 中相关的配置解析逻辑。
  - 调整 `com.dwarfeng.springtelqos.impl.handler.TelqosHandlerImpl` 的内部实现，在相应的逻辑处理中应用命名策略。

- 指令标识符能力增强。
  - 扩展 `Constants.COMMAND_IDENTITY_FORMAT`，在原有规则基础上支持通过 `:`、`.`、`-` 进行分段命名。
  - 新增 `com.dwarfeng.springtelqos.sdk.util.CommandIdentityFormatTest` 测试用例。

- 项目架构重构。
  - 项目构型对齐至 subgrade 工具工程构型。

- 增加依赖。
  - 增加依赖 `subgrade` 以应用其新功能，版本为 `1.8.2.a`。

### Bug 修复

- (无)

### 功能移除

- 移除项目与 `spring-terminator` 的集成。

- 移除不需要的依赖。
  - 移除 `spring-terminator` 依赖。

---

## 更早的版本

[View all changelogs](./changelogs)
