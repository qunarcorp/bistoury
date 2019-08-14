# 快速开始

为了能够快速启动bistoury，bistoury提供快速启动脚本在单机部署整套bistoury服务。

 ### 构建

使用script/quick_start_build.sh 脚本构建快速启动包，运行完成后会在script目录下生成一个压缩包，解压并运行quick_start.sh脚本，参数可通过quick_start.sh -h查看
 
 ### 准备
- 目前仅支持linux或unix环境，所以需要一个linux或unix环境
- 不占用8080，8081，20880，8899端口，proxy使用8080端口，ui使用8081端口，agent和proxy通信使用20880端口，ui和proxy通信使用8899端口
- 在本机启动一个java应用，用于bistoury attach，且这个Java应用需要使用了org.springframework.web.servlet.DispatcherServlet类

 ### 快速开始

 通过quick_start.sh脚本快速开始
```sell
   -p  通过-p参数执行bistoury-agent将要attch的应用pid
   -j  通过-j参数执行java_home
```

 - 启动

```jshelllanguage
./demo-start.sh -p 91572 -j /home/java start
```
- 停止

 ```jshelllanguage
./demo-start.sh stop
```

 ### 访问
通过[http://localhost:8081/](http://localhost:8081/)访问