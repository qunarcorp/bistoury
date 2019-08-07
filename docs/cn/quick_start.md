# 快速开始

为了能够早本地运行并查看运行效果，现提供脚本快速启动。

##使用

### 下载

先从release页面下载快速启动包，然后解压

### 准备

你需要准备的有：

- 目前仅支持linux或unix环境，所以需要一个linux或unix环境
- Bistoury依赖zk，所以需要在本机启动一个zk服务，端口为2181，你也可以在bistoury-ui-{proxject.version}-bin/conf/registry.properties和bistoury-proxy-{proxject.version}-bin/conf/registry.properties中修改zk地址
- 不占用8080，8081端口，proxy使用8080端口，ui使用8081端口
- 在本机启动一个java应用，用于bistoury attach，且这个Java应用需要使用了org.springframework.web.servlet.DispatcherServlet类

如果需要使用debug和动态监控，还需要准备的有：

- 在bistoury-ui-{proxject.version}-bin/conf/config.properties文件中配置gitlab.endpoint属性，值为gitlab地址，注意，bistoury仅支持gitlab api v3
- 新建一个/tmp/releaseInfo.properties文件，内容如下：
```properties
    #gitlab项目名
    project=tc/bistoury
    #项目所属module，没有module时值为英文句号[.]
    module=bistoury-ui
    #应用运行的版本号/分支/tag
    output=master
```

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