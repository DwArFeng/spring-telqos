# ChangeLog

## Release_2.0.0_20260421_build_A

### 功能构建

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
