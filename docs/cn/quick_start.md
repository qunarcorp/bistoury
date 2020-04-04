# 快速开始

Bistoury 具有多个模块，并且和公司自身环境有一定的关联，想要合理部署需要进行一些相关的配置。

为了能够快速启动和体验 Bistoury，我们提供快速部署脚本在单机部署整套 Bistoury 服务。

> 使用快速部署脚本，会在本机部署一整套 Bistoury 服务，其中包括 ui、proxy、agent。

注意，这里提供的快速部署脚本仅适用于快速上手进行单机诊断，想要获得完整的体验，还是需要进行合理的部署。

目前在我们公司内部的使用方式，也是推荐的部署方式为：

- ui 独立部署，推荐部署在多台机器，并提供独立的域名

- proxy 独立部署，推荐部署在多台机器，并提供独立的域名

- agent 需要和应用部署在同一台机器上。推荐在测试环境全环境自动部署，线上环境提供单机一键部署，以及应用下所有机器一键部署

- 独立的应用中心，管理所有功能内部应用和机器信息，这是一个和 Bistoury 相独立的系统，Bistoury 从中拿到不断更新的应用和机器信息

### 构建

#### 获取快速部署包

- 我们在项目 [Release](https://github.com/qunarcorp/bistoury/releases) 页面提供了已经构建好的快速部署包，你也可以直接下载。

- 你也可以下载源码然后自己构建快速启动包，这同样很简单。首先 clone 项目到本地，运行 script/quick_start_build.sh，运行完成后 script 目录下会生成相应的快速部署包，名字格式为 bistoury-quick-start.tar.gz

#### 准备

- 目前仅支持 Linux 环境，所以需要一个 Linux 环境

- 本机已安装 jdk1.8+，并且设置了 `JAVA_HOME` 环境变量，如果没有设置也可以在启动脚本中传递 `-j` 参数，详情见下文：[启动参数](#启动参数)

- 本机 9090，9091，9880，9881 端口未被占用，这些端口会被 Bistoury 使用，如果已占用需要进行配置，详情见下文：[当端口冲突了怎么解决](#当端口冲突了怎么解决)

- 本机已经启动一个待诊断 JAVA 应用，如果是 Spring Web 应用不需要做处理，非 Spring Web 应用需要配置启动脚本的 `-c` 参数，详情见下文：[启动参数](#启动参数)

#### 启动

首先我们将快速启动包 bistoury-quick-start.tar.gz 拷贝到想要安装的位置。

然后解压启动包：

```bash
tar -zxvf bistoury-quick-start.tar.gz
cd bistoury
```

最后是启动 Bistoury，因为 Bistoury 会用到 jstack 等操作，为了保证所有功能可用，需要使用和待诊断 JAVA 应用相同的用户启动。

假设应用进程 id 为 1024

- 如果应用以本人用户启动，可以直接运行

```bash
./quick_start.sh -p 1024 start
```

- 如果应用以其它帐号启动，比如 tomcat，需要指定一下用户然后运行

```bash
sudo -u tomcat ./quick_start.sh -p 1024 start
```

- 停止运行

```bash
./quick_start.sh stop
```

### 访问

可以通过 http://ip:9091 来对 ui 进行访问，比如部署的机器 ip 为 192.168.1.20，则可以通过 [http://192.168.1.20:9091/](http://192.168.1.20:9091/) 访问，初始化用户名密码均为 admin

### 启动参数

quick_start.sh 可以设置一些启动参数，如下表所示：

|参数名称|是否必填|默认值|说明|
|-------|------|-----|---|
|-i    |选填|ip 中列表的第一个|当本机存在多个 ip 时，指定一个可用 ip|
|-j    |选填|环境变量 JAVA_HOME |指定 jdk 路径|
|-l    |选填|/tmp|应用的日志目录，Bistoury 命令执行的目录，比如 ls，tail 等都会默认在此目录下执行|
|-p    |必填|    |应用进程 id，因为是脚本快速启动，所以需要使用该参数指定对哪个 JAVA 进程进行诊断|
|-c    |选填|org.springframework.web.servlet.DispatcherServlet|用于获取一些应用信息，应填写为依赖的 jar 包中的一个已加载的类（不能使用 Bistoury agent 中用到的类，推荐使用公司内部中间件的 jar 包或 Spring 相关包中的，agent 不可能使用到的类，如org.springframework.web.servlet.DispatcherServlet）|
|-h   | 选填||查看帮助文档

### 问题解决

#### 当端口冲突了怎么解决

Bistoury 快捷部署脚本默认会占用一些端口，其中 proxy 默认使用 9090 端口，ui 默认使用 9091 端口，agent 和 proxy 通信默认使用 9880 端口，agent 和应用通信使用的3668端口，ui 和 proxy 通信默认使用 9881 端口，h2 数据库默认使用 9092 端口，端口冲突解决方法如下：

- 修改自己占用的端口
- [修改 bistoury 的端口](https://github.com/qunarcorp/bistoury/blob/master/docs/cn/FAQ.md#%E7%AB%AF%E5%8F%A3%E9%97%AE%E9%A2%98)
#### 提示 not find proxy for agent

- 到 agent 启动日志中检查 agent 是否启动成功，检查日志中是否存在 `bistoury netty client start success` 字样日志，如果没有，检查 jvm 参数 `bistoury.proxy.host` 是否配置为正确的 proxy 域名或 ip:prot，如果存在这样的日志（`bistoury netty client start success, ProxyConfig{ip='192.168.2.22', port=9880, heartbeatSec=30}`），按照日志后面的 ip 到对应的 proxy 上进行后续检查
- 访问 proxy 下 proxyIp:port/proxy.html，检查 agent 对应的 ip 是否注册到 proxy 下
- 注意：可能会出现这种情况，当一台机器存在多个 ip 时，可能会出现 agent 注册到 proxy 的 ip 与应用中心的 ip 不一致，此时只需要在应用中心将 ip 改为注册到 proxy 的 ip 即可。

#### 应用重启，进程 id 变了怎么办

- 需要先使用 quick_start.sh 脚本先 stop，然后更改 `-p` 参数 start

### 快速启动脚本做了什么

1. 我们在本机上部署了全套的 Bistoury，包括 ui，proxy 和 agent。

2. 使用并初始化一个 h2 数据库，并初始化了一些数据用来对本机应用做诊断。

3. 包括创建一个用户名密码均为 admin 的用户，一个名为 bistoury_demo_app 的应用，并将本机注册到 bistoury_demo_app 下作为一台服务器。
