# Install by Source Code - 通过源码安装本项目

## 获取源码

从 Github 或者 Gitee 中获取项目源码。

使用 git 进行源码下载。

```shell
git clone git@github.com:DwArFeng/spring-telqos.git
```

对于中国用户，可以使用 gitee 进行高速下载。

```shell
git clone git@gitee.com:dwarfeng/spring-telqos.git
```

## 切换版本

进入项目根目录，查看该项目所有的版本：

```shell
git tag --sort=-creatordate -l
```

切换到您想要安装的版本：

在切换版本的时候，请您参考 [Version Blacklist](./VersionBlacklist.md) 文件中的内容，避开具有重大缺陷的版本。

```shell
git checkout version-tag-here
```

## 安装非中央仓库的依赖

该项目的部分依赖不在中央仓库中，您需要手动下载这些依赖的源码并安装到本地仓库中，
或者使用私有制品仓库进行安装。

如果您没有对应的私有制品仓库，您可以使用如下命令手动下载这些依赖的源码并进行安装。

需要注意的是，依赖项目中也有可能含有不在中央仓库中的依赖，因此，当依赖项目安装失败时，您应该按照依赖项目的相关文档，
检查依赖项目的依赖是否安装成功。

手动安装依赖是一个繁琐的过程，在这个过程中，您需要保持耐心。

如有必要，请联系本项目的主要开发人员索要制品仓库地址或离线文件。

### dutil

使用 git 进行源码下载。

```shell
git clone git@github.com:DwArFeng/dutil.git
```

对于中国用户，可以使用 gitee 进行高速下载。

```shell
git clone git@gitee.com:dwarfeng/dutil.git
```

进入项目根目录，查看该项目所有的版本：

```shell
git tag --sort=-creatordate -l
```

切换到本项目需要的版本：

```shell
git checkout version-tag-here
```

使用 maven 进行安装：

```shell
mvn clean source:jar install
```

### subgrade

使用 git 进行源码下载。

```shell
git clone git@github.com:DwArFeng/subgrade.git
```

对于中国用户，可以使用 gitee 进行高速下载。

```shell
git clone git@gitee.com:dwarfeng/subgrade.git
```

进入项目根目录，查看该项目所有的版本：

```shell
git tag --sort=-creatordate -l
```

切换到本项目需要的版本：

```shell
git checkout version-tag-here
```

使用 maven 进行安装：

```shell
mvn clean source:jar install
```

## 尝试安装

使用 maven 进行安装：

```shell
mvn clean source:jar install
```

## Enjoy it

如果一切顺利，那么您已经成功安装了该项目，并可以在您的项目中使用该项目了。

如果安装失败，请不要气馁，您可以分析 maven 的报错信息，或者联系本项目的主要开发人员进行咨询。

## 参阅

- [Version Blacklist](./VersionBlacklist.md) - 版本黑名单，列出了本项目的版本黑名单，请注意避免使用这些版本。
