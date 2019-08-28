* [获取ip错误](#1)
* [can not find lib class](#2)
* [windows环境](#3)
* [日志目录](#4)
* [github仓库源码查看](#5)
* [jdk版本](#6)
* [端口问题](#7)
    * [默认占用端口](#7-1)
    * [修改默认端口](#7-2)
* [在线debug显示object size greater than ***kb](#8)

<h3 id="1">获取ip错误</h3>
---
本机可能存在多个ip，可能导致获取的ip不是当前正在使用的ip，导致出错，可以通过quick_start.sh脚本中的-i参数指定当前的ip。

例子 :
```
./quick_start.sh -i 127.0.0.1 -p 1024 start
```


<h3 id="2">can not find lib class</h3>
---

 agent 需要根据应用内部加载的类获取一些应用相关的信息( 默认:org.springframework.web.servlet.DispatcherServlet ),但不是每个项目都会用到spring mvc,此时需要用户手动指定这个类（不能使用Bistoury agent中用到的类，推荐使用公司内部中间件的jar包或Spring相关包中的，agent不可能使用到的类)

 例子 :
 ```
 ./quick_start.sh -c org.springframework.web.servlet.DispatcherServlet 127.0.0.1 -p 1024 start
 ```

 <h3 id="3">windows环境</h3>
---

暂时不包含windows下的一键启动脚本,但是可以通过ide来启动.

<h3 id="4">日志目录</h3>
---
1. agent日志目录  解压缩目录/bistoury-agent-bin/logs 
2. proxy日志目录  解压缩目录/bistoury-proxy-bin/logs 
3. ui日志目录     解压缩目录/bistoury-ui-bin/logs 
4. arthas日志目录 ~/logs/arthas
5. attach到应用中输出的日志日志目录 ~/logs/bistoury

<h3 id="5">github仓库源码查看</h3>
---
暂时只支持gitlab仓库的源代码查看.

<h3 id="6">jdk版本</h3>
---
暂时只支持包括jdk7\jdk8在内的版本,没有测试过低于jdk7的版本,jdk9以上改动比较大,暂时不支持.

<h3 id="7">端口问题</h3>
---
<h4 id="7-1">默认占用端口</h4> 

1. 9880 
2. 9881 
3. 9090 
4. 9091 
5. 9092

<h4 id="7-2">修改默认端口</h4>
9880 ->  `解压缩目录/bistoury-proxy-bin/conf/global.properties`中的`agent.newport`值（agent和proxy通信默认使用9880端口）
9881 -> `解压缩目录/bistoury-proxy-bin/conf/global.properties`中的`server.port`值和`quick_start.sh`中`PROXY_WEBSOCKET_PORT`的值（ui和proxy通信默认使用9881端口）
9090 -> `解压缩目录/bistoury-proxy-bin/conf/server.properties`中的`tomcat.port`值和`quick_start.sh`中`PROXY_TOMCAT_PORT`的值（proxy默认使用9090端口）
9091 -> `解压缩目录/bistoury-ui-bin/conf/server.properties`中的`tomcat.port`值（ui默认使用9091端口）
解压缩目录
9092 —-> `解压缩目录/bistoury-ui-bin/conf/server.properties`中的`tomcat.port`值(h2数据库默认使用9092端口)


<h3 id="8">在线debug显示object size greater than ***kb</h3>
---
序列化有大小限制，超过`10m`的对象就不会序列化，直接抛出异常.（解压缩目录/conf/agent_config.properties中debug.json.limit.kb属性可有配置其阈值）