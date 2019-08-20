# 快速开始

为了能够快速启动bistoury，bistoury提供快速启动脚本在单机部署整套bistoury服务。

>使用快速启动脚本，会在本机启动一整套bistoury服务，其中包括ui、proxy、agent。

快速启动依赖与h2数据库，会在启动时依赖初始化以下数据 
- 一个用户名密码均为admin的用户
- 在admin用户下初始化一个tc_demo_app应用
- 将本机注册到tc_demo_app应用下作为一台服务器，如需要修改应用或服务器信息可以到[应用中心](application.md)修改

 ### 构建

使用script/quick_start_build.sh 脚本构建快速启动包，运行完成后会在script目录下生成一个压缩包，解压并运行quick_start.sh脚本，参数可通过quick_start.sh -h查看
 
 ### 准备
- 目前仅支持linux或unix环境，所以需要一个linux或unix环境
- 不占用9090，9091，9880，9999端口，proxy使用9090端口，ui使用9091端口，agent和proxy通信使用9880端口，ui和proxy通信使用9999端口
- 在本机启动一个java应用，用于bistoury attach，且这个Java应用需要使用了org.springframework.web.servlet.DispatcherServlet类|
### 问题解决
- 当端口冲突了怎么解决
   - 修改自己占用的端口
   - 9090端口占用修改位置：`bistoury-proxy-1.4.0-SNAPSHOT-bin/conf/server.properties`中的`tomcat.port`值和quick_start.sh中`PROXY_TOMCAT_PORT`的值
   - 9091端口占用修改位置：`bistoury-ui-1.4.0-SNAPSHOT-bin/conf/server.properties`中的`tomcat.port`值
   - 9880端口占用修改位置：`bistoury-proxy-1.4.0-SNAPSHOT-bin/conf/global.properties`中的`agent.newport`值
   - 9999端口占用修改位置：`bistoury-proxy-1.4.0-SNAPSHOT-bin/conf/global.properties`中的`server.port`值和quick_start.sh中`PROXY_WEBSOCKET_PORT`的值

- 提示not find proxy for agent
   - 到agent启动日志中检查agent是否启动成功，检查日志中是否存在`bistoury netty client start success`字样日志，如果没有，检查jvm参数`bistoury.proxy.host`是否配置为正确的proxy域名或ip:prot，如果存在这样的日志（`bistoury netty client start success, ProxyConfig{ip='192.168.2.22', port=9880, heartbeatSec=30}`），按照日志后面的ip到对应的proxy上进行后续检查
   - 访问proxy下proxyIp:port/proxy.html，检查agent对应的IP是否注册到proxy下
   - 注意：可能会出现这种情况，当一台机器存在多个ip时，可能会出现agent注册到proxy的ip与应用中心的ip不一致，此时只需要在应用中心将ip改为注册到peoxy的IP即可。
- 
 ### 快速开始

 通过quick_start.sh脚本快速开始
 
 - 参数

|参数名称|是否必填|默认值|说明|
|-------|------|-----|---|
|-j    |选填|环境变量JAVA_HOME|通过-j指定java home|
|-l    |选填|/tmp|应用日志目录，bistoury支持的命令执行目录|
|-p    |必填|    |应用pid，因为是脚本快速启动，所以需要使用该参数指定agent attach到那个java进程|
|-c    |选填|org.springframework.web.servlet.DispatcherServlet|通过-c指定应用依赖的jar包中的一个类（推荐使用公司内部中间件的jar包或Spring相关包中的类，如org.springframework.web.servlet.DispatcherServlet），用于获取应用lib目录|
|-h   | 选填||查看帮助文档

==**注意：**== 为了保证所有功能可用，请使用启动应用的用户来执行脚本
 - 启动

```jshelllanguage
./quick_start.sh -p 91572 -j /home/java start
```
- 停止
```jshelllanguage
./quick_start.sh stop
```

 ### 访问
通过[http://127.0.0.1:9091/](http://127.0.0.1:9091/)访问，初始化用户名密码均为admin