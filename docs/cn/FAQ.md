* [获取ip错误](#获取ip错误)
* [can not find lib class](#can-not-find-lib-class)
* [windows 环境暂时不支持](#windows-环境暂时不支持)
* [日志目录](#日志目录)
* [github仓库暂时不支持查看源码](#github仓库暂时不支持查看源码)
* [jdk版本](#jdk版本)
    * [应用jdk版本要求](#应用jdk版本要求)
    * [bistoury自带模块的jdk版本](#bistoury自带模块的jdk版本)
* [端口问题](#端口问题)
    * [默认占用端口](#默认占用端口)
    * [修改默认端口](#修改默认端口)
* [在线debug对象值显示 `object size greater than ***kb`](#在线debug对象值显示-object-size-greater-than-kb)

### 获取ip错误

---
本机可能存在多个ip，导致获取的ip不是当前正在使用的ip，从而出错，可以通过`quick_start.sh`脚本中的-i参数指定当前的ip。

例子 :
```
./quick_start.sh -i 127.0.0.1 -p 1024 start
```

# can not find lib class

---

 agent 需要根据应用内部加载的类获取一些应用相关的信息( 默认:org.springframework.web.servlet.DispatcherServlet ),但不是每个项目都会用到spring mvc,此时需要用户手动指定这个类
 > 不能使用Bistoury agent中用到的类，推荐使用公司内部中间件的jar包或Spring相关包中的类

 例子 :
 ```
 ./quick_start.sh -c org.springframework.web.servlet.DispatcherServlet -p 1024 start
 ```

 ### windows 环境暂时不支持

---

暂时不包含windows下的一键启动脚本, 但是支持通过ide来启动.

### 日志目录

---

|      模块            | 输出的具体文件夹                         |
|:---------------------|:------------------------------|
| 1. agent          | 解压缩目录/bistoury-agent-bin/logs |
| 2. proxy         | 解压缩目录/bistoury-proxy-bin/logs |
| 3. ui             | 解压缩目录/bistoury-ui-bin/logs    |
| 4. arthas         | ~/logs/arthas                 |
| 5. attach到应用中的部分 | ~/logs/bistoury               |

### github仓库暂时不支持查看源码

---
暂时只支持 gitlab 仓库的源代码查看，github 仓库源码暂不支持。

### jdk版本

---
#### 应用jdk版本要求
> 暂时只支持包括jdk7\jdk8在内的应用, 没有测试过低于jdk7的版本, jdk9以上改动比较大, 暂时不支持.

#### bistoury自带模块的jdk版本
|      模块            | jdk版本                         |
|:---------------------|:------------------------------|
| 1. agent          | jdk7|
| 2. proxy         | jdk8 |
| 3. ui             | jdk8   |


### 端口问题

---
#### 默认占用端口


|      端口          | 作用                         |
|:---------------------|:------------------------------|
|1. 9880 |agent和proxy通信默认使用9880端口|
|2. 9881 |ui和proxy通信默认使用9881端口|
|3. 9090|proxy默认使用9090端口|
|4. 9091 |ui默认使用9091端口|
|5. 9092|h2数据库默认使用9092端口|

#### 修改默认端口

|      端口          | 端口定义的位置                         |
|:---------------------|:------------------------------|
| 9880 | `解压缩目录/bistoury-proxy-bin/conf/global.properties`中的`agent.newport`值|
| 9881 | `解压缩目录/bistoury-proxy-bin/conf/global.properties`中的`server.port`值和`quick_start.sh`中|`PROXY_WEBSOCKET_PORT`的值|
| 9090 | `解压缩目录/bistoury-proxy-bin/conf/server.properties`中的`tomcat.port`值和`quick_start.sh`中`PROXY_TOMCAT_PORT`的值|
| 9091 | `解压缩目录/bistoury-ui-bin/conf/server.properties`中的`tomcat.port`值|
| 9092 | `解压缩目录/h2/h2.sh`中的H2_PORT值|


### 在线debug对象值显示 `object size greater than ***kb`

---
序列化有大小限制（默认10240kb），超过阈值的对象会停止序列化，对象值显示为`object size greater than ***kb`

> 解压缩目录/conf/agent_config.properties中debug.json.limit.kb属性可以配置其阈值