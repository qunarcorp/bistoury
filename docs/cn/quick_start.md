# 快速开始

Bistoury具有多个模块，并且和公司自身环境有一定的关联，想要合理部署需要进行一些相关的配置。

为了能够快速启动和体验Bistoury，我们提供快速部署脚本在单机部署整套Bistoury服务。

>使用快速部署脚本，会在本机部署一整套Bistoury服务，其中包括ui、proxy、agent。

注意，这里提供的快速部署脚本仅适用于快速上手进行单机诊断，想要获得完整的体验，还是需要进行合理的部署。

目前在我们公司内部的使用方式，也是推荐的部署方式为：

- ui独立多机部署，并提供独立的域名

- proxy独立多机部署

- agent在测试环境全环境自动部署，线上环境提供单机一键部署，以及应用下所有机器一键部署

- 独立的应用中心，管理所有功能内部应用和机器信息，这是一个和Bistoury相独立的系统

### 构建

#### 获取快速部署包

- 我们在项目Release页面提供了已经构建好的快速部署包，你也可以直接下载。

- 你也可以下载源码然后自己构建快速启动包，这同样很简单。首先clone项目到本地，运行script/quick_start_build.sh，运行完成后script目录下会生成相应的快速部署包，名字格式为bistoury-quick-start.tar.gz

#### 准备

- 目前仅支持linux环境，所以需要一个linux环境

- 本机已安装jdk1.8+，并且设置了JAVA_HOME环境变量，如果没有设置也可以在启动脚本中传递参数，详情建下文

- 本机9090，9091，9880，9881端口未被占用，这些端口会被Bistoury使用，如果已占用需要进行配置，详情见下文

- 本机已经启动一个待诊断java应用，如果是spring web应用不需要做处理，非spring web应用需要配置启动脚本的-c参数，详情见下文 

#### 启动

首先我们将快速启动包bistoury-quick-start.tar.gz拷贝到想要安装的位置。

然后解压启动包：

```bash
tar -zxvf bistoury-quick-start.tar.gz
cd bistoury
```

最后是启动Bistoury，因为Bistoury会用到jstack等操作，为了保证所有功能可用，需要使用和待诊断java应用相同的用户启动。

假设应用进程id为1024

- 如果应用以本人用户启动，可以直接运行

```bash
./quick_start.sh -p 1024 start
```

- 如果应用以其它帐号启动，比如tomcat，需要指定一下用户然后运行
```bash
sudo -u tomcat ./quick_start.sh -p 1024 start
```

- 停止运行

```bash
./quick_start.sh stop
```

### 访问
可以通过http://ip:9091来对ui进行访问，比如部署的机器ip为192.168.1.20，则可以通过[http://192.168.1.20:9091/](http://192.168.1.20:9091/)访问，初始化用户名密码均为admin

### 启动参数

quick_start.sh可以设置一些启动参数，

|参数名称|是否必填|默认值|说明|
|-------|------|-----|---|
|-j    |选填|环境变量JAVA_HOME|指定jdk路径|
|-l    |选填|/tmp|应用的日志目录，Bistoury命令执行的目录，比如ls，tail等都会默认在此目录下执行|
|-p    |必填|    |应用进程id，因为是脚本快速启动，所以需要使用该参数指定对哪个java进程进行诊断|
|-c    |选填|org.springframework.web.servlet.DispatcherServlet|用于获取一些应用信息，应填写为应用自身代码或依赖的jar包中的一个类（不能使用Bistoury agent中用到的类，推荐使用公司内部中间件的jar包或Spring相关包中的，agent不可能使用到的类，如org.springframework.web.servlet.DispatcherServlet）|
|-h   | 选填||查看帮助文档

### 问题解决

- 当端口冲突了怎么解决

Bistoury快捷部署脚本默认会占用一些端口，其中proxy默认使用9090端口，ui默认使用9091端口，agent和proxy通信默认使用9880端口，ui和proxy通信默认使用9881端口，h2数据库默认使用9092端口，端口冲突解决方法如下：
   - 修改自己占用的端口
   - 9090端口占用修改位置：`bistoury/bistoury-proxy-bin/conf/server.properties`中的`tomcat.port`值和quick_start.sh中`PROXY_TOMCAT_PORT`的值
   - 9091端口占用修改位置：`bistoury/bistoury-ui-bin/conf/server.properties`中的`tomcat.port`值
   - 9880端口占用修改位置：`bistoury/bistoury-proxy-bin/conf/global.properties`中的`agent.newport`值
   - 9881端口占用修改位置：`bistoury/bistoury-proxy-bin/conf/global.properties`中的`server.port`值和quick_start.sh中`PROXY_WEBSOCKET_PORT`的值
   - 9092端口占用修改位置：`bistoury/h2/h2.sh`中的`H2_PORT`的值

- 提示not find proxy for agent
   - 到agent启动日志中检查agent是否启动成功，检查日志中是否存在`bistoury netty client start success`字样日志，如果没有，检查jvm参数`bistoury.proxy.host`是否配置为正确的proxy域名或ip:prot，如果存在这样的日志（`bistoury netty client start success, ProxyConfig{ip='192.168.2.22', port=9880, heartbeatSec=30}`），按照日志后面的ip到对应的proxy上进行后续检查
   - 访问proxy下proxyIp:port/proxy.html，检查agent对应的IP是否注册到proxy下
   - 注意：可能会出现这种情况，当一台机器存在多个ip时，可能会出现agent注册到proxy的ip与应用中心的ip不一致，此时只需要在应用中心将ip改为注册到proxy的IP即可。
   
- 应用重启，进程id变了怎么办
   - 需要先使用quick_start.sh脚本先stop，然后更改-p参数start

### 快速启动脚本做了什么

我们在本机上部署了全套的Bistoury，包括ui，proxy和agent。

使用并初始化一个h2数据库，并初始化了一些数据用来对本机应用做诊断。

包括创建一个用户名密码均为admin的用户，一个名为bistoury_demo_app的应用，并将本机注册到bistoury_demo_app下作为一台服务器。
