# How to Install Telnet Client on CentOS 7 - 如何在 CentOS 7 上安装 telnet 客户端

## 更换 CentOS 7 安装源

1. 使用 SFTP 等方式，备份 `/etc/yum.repos.d/CentOS-Base.repo` 文件。
2. 使用 SFTP 等方式，将以下内容保存为 `/etc/yum.repos.d/CentOS-Base.repo` 文件：

   ```ini
   # CentOS-Base.repo
   #
   # The mirror system uses the connecting IP address of the client and the
   # update status of each mirror to pick mirrors that are updated to and
   # geographically close to the client.  You should use this for CentOS updates
   # unless you are manually picking other mirrors.
   #
   # If the mirrorlist= does not work for you, as a fall back you can try the 
   # remarked out baseurl= line instead.
   #
   #
   
   [base]
   name=CentOS-$releasever - Base
   #mirrorlist=http://mirrorlist.centos.org/?release=$releasever&arch=$basearch&repo=os&infra=$infra
   baseurl=http://vault.centos.org/7.9.2009/os/$basearch/
   gpgcheck=1
   gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7
   
   #released updates 
   [updates]
   name=CentOS-$releasever - Updates
   #mirrorlist=http://mirrorlist.centos.org/?release=$releasever&arch=$basearch&repo=updates&infra=$infra
   baseurl=http://vault.centos.org/7.9.2009/updates/$basearch/
   gpgcheck=1
   gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7
   
   #additional packages that may be useful
   [extras]
   name=CentOS-$releasever - Extras
   #mirrorlist=http://mirrorlist.centos.org/?release=$releasever&arch=$basearch&repo=extras&infra=$infra
   baseurl=http://vault.centos.org/7.9.2009/extras/$basearch/
   gpgcheck=1
   gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7
   
   #additional packages that extend functionality of existing packages
   [centosplus]
   name=CentOS-$releasever - Plus
   #mirrorlist=http://mirrorlist.centos.org/?release=$releasever&arch=$basearch&repo=centosplus&infra=$infra
   baseurl=http://vault.centos.org/7.9.2009/centosplus/$basearch/
   gpgcheck=1
   enabled=0
   gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7
   
   ```

## 安装 telnet 客户端

1. 执行以下命令安装 telnet 客户端：

   ```bash
   sudo yum install -y telnet
   ```

## 验证 telnet 客户端

1. 执行以下命令验证 telnet 客户端是否安装成功：

   ```bash
   rpm -q telnet
   ```

2. 如果命令输出软件包名称与版本号，则说明 telnet 客户端已经安装成功。
