# Quick Start - 快速开始

## 确认系统需求

- CPU：2 核以上。
- 内存：2G 以上。
- 硬盘：50G 以上。
- JDK 8。
- Maven 3。

## telnet 客户端初始化

妥善安装好 telnet 客户端。

如果您的 Centos 7 系统中没有安装 telnet 客户端，可以按照以下的教程，在 10 分钟之内完成安装：

- [How to Install Telnet Client on CentOS 7](./HowToInstallTelnetClientOnCentos7.md) - 如何在 CentOS 7 上安装 telnet
  客户端。

## 源码下载

使用 git 进行源码下载。

```shell
git clone git@github.com:DwArFeng/spring-telqos.git
```

对于中国用户，可以使用 gitee 进行高速下载。

```shell
git clone git@gitee.com:dwarfeng/spring-telqos.git
```

## 最小化配置

### spring-telqos-core 子模块

在下载的源码目录下，定位 `spring-telqos-core/conf/test.telqos` 目录，如不存在可以创建。

随后，将 `spring-telqos-core/src/test/resources/telqos/connection.properties` 文件复制到该目录下。

同时，需要将以下配置项更改为 telqos 服务的实际值。

```properties
# Telnet 的端口号。
telqos.port=23
# 字符集。
telqos.charset=UTF-8
# 白名单表达式。
telqos.whitelist_regex=
# 黑名单表达式。
telqos.blacklist_regex=
```

### spring-telqos-api 子模块

在下载的源码目录下，定位 `spring-telqos-api/conf/test.telqos` 目录，如不存在可以创建。

随后，将 `spring-telqos-api/src/test/resources/telqos/connection.properties` 文件复制到该目录下。

同时，需要将以下配置项更改为 telqos 服务的实际值。

```properties
# Telnet 的端口号。
telqos.port=23
# 字符集。
telqos.charset=UTF-8
# 白名单表达式。
telqos.whitelist_regex=
# 黑名单表达式。
telqos.blacklist_regex=
```

## 效果体验

运行 `src/test` 下的示例以观察全部特性。

| 示例类名                                           | 说明      |
|------------------------------------------------|---------|
| com.dwarfeng.springtelqos.api.example.Example  | API 示例  |
| com.dwarfeng.springtelqos.node.example.Example | Core 示例 |

需要注意的是，示例类会让您打开 telnet 客户端并连接本机的 `23` 端口，如果您使用 windows 平台，
您可能需要提前安装好 telnet 客户端，并根据控制台输出选择合适的字符集配置。
