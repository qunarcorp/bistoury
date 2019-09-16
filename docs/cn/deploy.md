本文档介绍了如何编译、打包、部署Bistoury，Bistoury的部署较为复杂，如果想先简单体验Bistoury可以使用[快捷部署脚本](/docs/cn/quick_start.md)在一分钟内进行简易部署。
# 一、准备工作
## 1.1 说明
Bistoury一共分为ui、proxy、agent三个部分，ui是所有操作的入口、agent是部署在所有主机上来对ui请求进行处理，proxy是连接ui和中间连接层。

目前在我们公司内部的使用方式，也是推荐的部署方式为：

- ui 独立部署，推荐部署在多台机器，并提供独立的域名

- proxy 独立部署，推荐部署在多台机器，并提供独立的域名

- agent 需要和应用部署在同一台机器上。推荐在测试环境全环境自动部署，线上环境提供单机一键部署，以及应用下所有机器一键部署

- 独立的应用中心，管理所有功能内部应用和机器信息，这是一个和 Bistoury 相独立的系统，Bistoury 从中拿到不断更新的应用和机器信息

## 1.1 运行环境
### 1.1.1 OS
ui、proxy、agent脚本理论上能在所有linux发行版上运行。
### 1.1.2 Java
ui、proxy使用Java1.8，agent使用java1.7或java1.8，由于agent会attach到应用中，所以应用也需要使用Java1.7或Java1.8。Java9及后续版本由于改动较大，会在以后陆续支持。
## 1.2 注册中心
ui依赖注册中心发现存活的proxy，目前支持的注册中心为zookeeper，其余注册中心将会陆续支持

# 二、部署步骤
部署步骤共分为三步：
+ 1、初始化数据库

    Bistoury的ui和proxy依赖数据库，所以需要事先创建并完成初始化
+ 2、获取安装包

    Bistoury的安装包共三个，bistoury-ui、bistoury-proxy和bistoury-agent，通过源码构建安装包
+ 3、部署

    获取安装包后修改对应的配置文件后就可以通过脚本进行部署到测试和生产环境了
## 2.1 初始化数据库
数据库初始化文件位于bistoury-ui安装包的sql目录下，运行sql完成数据库初始化，完成后数据库中存在一个用户，用户名：admin、密码：admin
### 2.1.1 初始化数据库
执行sql语句完成初始化，数据库初始化完成后Bistoury下一共有5张表
+ bistoury_user
用户登录信息表，使用bistoury的所有用户的登录信息
+ bistoury_gitlab_token
gitlab peivate token配置表，存放每个用户对应的private token
+ bistoury_app
应用表，存放每个应用的信息
+ bistoury_server
存放的是每个应用下有哪些服务器，及该服务器上应用的配置信息
+ bistoury_user_app
存放应用owner表，存放每个用户用的哪些应用

## 2.2 获取安装包
项目通过选择不同的profile使用不同的配置，profile一共有local和prod两种，local是快速启动专用的，使用h2数据库；prod使用的是mysql数据库
### 2.2.1 通过源码构建
#### 2.2.1.1 配置数据库连接信息
Bistoury的ui和proxy需要知道如何连接到在上面创建的数据库，数据库连接信息配置位于解压后的ui和proxy的conf/jdbc.properties中，ui和proxy的jdbc连接需要保持一致。
#### 2.2.1.2 调整ui和proxy配置
在ui和proxy的安装包下都有一个conf目录，调整里面的配置文件，每个配置都有其说明，请根据说明修改配置。
>注：标有【动态更新】的配置在修改之后会在10s内自动生效，不需要重启应用
#### 2.2.1.2 执行编译、打包
+ 切换到script目录
+ 执行脚本
```shell
./build.sh
```
该脚本会依次打包bistoury-agent、bistoury-ui、bistoury-proxy。
#### 2.2.1.3 获取bistoury-agent安装包
位于bistoury-dist/target目录下的bistoury-agent-bin.tar.gz
#### 2.2.1.4 获取bistoury-ui安装包
位于bistoury-ui/target目录下的bistoury-ui-bin.tar.gz
#### 2.2.1.4 获取bistoury-proxy安装包
位于bistoury-proxy/target目录下的bistoury-proxy-bin.tar.gz

## 2.3 部署

    proxy和ui启动时需要配置启动参数bistoury.conf，参数的值为对应配置文件所在文件夹的路径

### 2.3.1 bistoury-proxy部署
解压并调整完配置后运行bin目录下的脚本进行启动，可以在bistoury-proxy-env.sh中的JAVA_OPTS里配置JVM相关参数，GC相关配置已配置，
+ 启动
```shell
./bistoury-proxy.sh start
```
+ 停止
```shell
./bistoury-proxy.sh stop
```
+ 重启
```shell
./bistoury-proxy.sh restart
```
### 2.3.2 bistoury-ui部署
解压并调整完配置后运行bin目录下的脚本进行启动，可以在bistoury-ui-env.sh中的JAVA_OPTS里配置JVM相关参数，GC相关配置已配置，
+ 启动
```shell
./bistoury-ui.sh start
```
+ 停止
```shell
./bistoury-ui.sh stop
```
+ 重启
```shell
./bistoury-ui.sh restart
```

`bistoury-ui`默认端口为`9091`, 因此启动成功以后可以访问`http://127.0.0.1:9091`访问ui页面，用户名密码默认都为`admin`


### 2.3.3 bistoury-agent部署

Agent启动前需要在bin/bistoury-agent-env.sh的JAVA_OPTS设置以下参数

|参数名称|是否必须|默认值|说明|
|-------|---|---|----|
|bistoury.store.path|否|bistoury-agent/store|bistoury agent数据存放路径，包括rocksdb存放的监控、jstack及jmap数据和反编译代码临时文件的存放|
|bistoury.proxy.host|是||proxy的域名，具体值请联系管理员，agent依赖该值获取proxy的连接配置信息|
|bistoury.app.lib.class|是||应用依赖的jar包中的一个类（推荐使用公司内部中间件的jar包或Spring相关包中的类，如org.springframework.web.servlet.DispatcherServlet），agent通过该类获取应用jar包路径|
|bistoury.pid.handler.jps.symbol.class|否|org.apache.catalina.startup.Bootstrap|attach的应用入口类，用于使用jps -l命令获取应用pid|
|bistoury.pid.handler.jps.enable|否|true|是否打开通过jps -l获取pid的开关|
|bistoury.pid.handler.ps.enable|否|true|是否打开通过ps aux|grep java 获取pid的开关|
|bistoury.app.classes.path|否|bistoury.app.lib.class对应jar包目录同级的classes目录|项目代码编译后字节码存放目录，一般情况下为classes目录|
|bistoury.agent.workgroup.num|否|2|agent netty work group 线程数|
|bistoury.agent.thread.num|否|16|agent执行命令的线程数|

运行bin目录下的脚本进行启动，可以在bistoury-agent-env.sh中的JAVA_OPTS里配置JVM相关参数，GC相关配置已配置，

在启动在需要在指定目录下创建一个发布信息文件（默认路径为相对日志目录的相对路径../webapps/releaseInfo.properties），默认格式如下：
```properties
#gitlab项目名
project=tc/bistoury
#项目所属module，没有module时值为英文句号[.]
module=bistoury-ui
#应用运行的版本号/分支/tag
output=master
```
可以qunar.tc.bistoury.ui.util.ReleaseInfoParse接口自定义解析

+ 启动

在启动是可以通过-p指定pid确定agent attach特定的java进程，不指定时会通过jps -l和ps aux|grep java 命令及proxy中配置的参数解析pid，优先级依次降低。
>- -p    通过-p指定应用进程pid
>- -j    通过-j指定java home
>- -c    通过-c指定应用依赖的jar包中的一个类（推荐使用公司内部中间件的jar包或Spring相关包中的类，如org.springframework.web.servlet.DispatcherServlet），agent通过该类获取应用jar包路径
>- -h    通过-h查看命令帮助

```shell
./bistoury-agent.sh -p 100 start
./bistoury-agent.sh start
```
+ 停止
```shell
./bistoury-agent.sh stop
```
+ 重启
```shell
./bistoury-agent.sh -p 101 restart
./bistoury-agent.sh restart
```
