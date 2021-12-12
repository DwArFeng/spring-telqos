# ChangeLog

### Release_1.1.2_20201212_build_A

#### 功能构建

- 升级 `log4j2` 依赖版本为 `2.15.0` 以规避 `CVE-2021-44228` 漏洞。

#### Bug修复

- (无)

#### 功能移除

- (无)

---

### Release_1.1.1_20201013_build_A

#### 功能构建

- 新增集成指令。
  - com.dwarfeng.springtelqos.api.integration.system.MemoryCommand
- 为 spring-telqos-api 模块添加测试用代码。

#### Bug修复

- 修正 spring-telqos.xsd 中不正确的约束以及注释。
- 修正 ShutdownCommand 中错误的命令描述。

#### 功能移除

- (无)

---

### Release_1.1.0_20201011_build_A

#### 功能构建

- 将项目主体代码移动至子模块 spring-telqos-core。
- 添加模块 spring-telqos-api。
  - 与 spring-terminator 进行集成。
  - 与 dubbo 进行集成。

#### Bug修复

- (无)

#### 功能移除

- (无)

---

### Release_1.0.3_20201007_build_A

#### 功能构建

- 为命令任务的执行增加了 5ms 的延时时间，解决了某些指令执行过快造成部分 telnet 客户端换行显示异常的问题。

#### Bug修复

- (无)

#### 功能移除

- (无)

---

### Release_1.0.2_20201004_build_A

#### 功能构建

- 完善 CliCommand 类，补全构造入口。
- 美化 ListCommandCommand 的输出样式。
- 完善设备登录以及连接中断时的日志记录。

#### Bug修复

- 修复 CliCommand 处理带引号的参数时行为不正常的 bug。
- 修复 CliCommand 解析命令行发生异常后仍然会执行 executeWithCmd 方法的bug。
- 修复 TelqosServiceImpl 处理中断连接事件时引发潜在的 NullPointerException 的 bug。

#### 功能移除

- (无)

---

### Release_1.0.1_20200927_build_A

#### 功能构建

- 优化 TelqosServiceImpl 中几处冗余的代码。
- 将测试文件更名为 Example。
- 去除示例文件对 spring-terminator 的依赖。

#### Bug修复

- (无)

#### 功能移除

- (无)

---

### Release_1.0.0_20200924_build_A

#### 功能构建

- 实现项目所有的预期功能。

#### Bug修复

- (无)

#### 功能移除

- (无)
