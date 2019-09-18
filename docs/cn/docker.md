# docker支持

> 使用docker部署,包含全部模块,包括ui模块、proxy模块、包含agent模块的demo应用.

* [使用远程仓库镜像运行](#使用远程仓库镜像运行)
* [手动构建镜像运行](#手动构建镜像运行)
* [镜像相关文件](#镜像相关文件)
* [镜像对应的模块](#镜像对应的模块)
* [demo运行脚本介绍](#demo运行脚本介绍)
* [镜像参数](#镜像参数)

# 使用远程仓库镜像运行
> `./remote_repositories_start.sh  宿主机ip`    

访问 [127.0.0.1:9091](http://127.0.0.1:9091/),即可使用到bistoury的所有功能  
`第一次访问可能需要等待20秒左右`(初始化agent与proxy的连接)

# 手动构建镜像运行
### 第一步: 使用脚本生成镜像 
> 运行脚本 `./build.sh`  *(script/docker目录下)*

### 第二步: 运行镜像
> 运行脚本 `./demo_docker_start.sh  宿主机ip` *(script/docker目录下)*
>> 访问 [127.0.0.1:9091](http://127.0.0.1:9091/),即可使用到bistoury的所有功能  
`第一次访问可能需要等待20秒左右`(初始化agent与proxy的连接)

## 镜像相关文件
打包镜像相关的文件都位于script/docker目录下,如果想自行部署,可参考相关文件


## 镜像对应的模块
|镜像名称|对应模块|
|:---:|:---:|
|bistoury-proxy|proxy模块|
|bistoury-ui|ui模块|
|bistoury-agent|agent模块|
|bistoury-demo|包含了agent模块的demo应用|
|bistoury-db|数据库相关|

> 使用到的端口,都与[快速开始](https://github.com/qunarcorp/bistoury/blob/master/docs/cn/quick_start.md)定义的相对应


## demo运行脚本介绍
### 创建网络
> 首先会创建一个名为bistoury的网络,子网为172.19.0.0/16

### 启动的容器
1. proxy模块容器
2. ui模块容器
3. db模块容器
4. 两个demo应用
5. zk容器

### 指定的容器ip
|镜像|指定的ip|
|:---:|:---:|
|zk 镜像|172.19.0.2|
|bistoury-proxy 镜像|172.19.0.3|
|ui 镜像|172.19.0.4|
|bistoury-demo镜像|172.19.0.5、172.19.0.6|
|bistoury-db镜像|172.19.0.7|

## 镜像参数
###  proxy 镜像
    
|参数|作用|
|:---|:---:|
|--real-ip|宿主机的ip|
|--zk-address|使用到的zk地址|  
|--parse-agent-id|是否解析从agent传输过来的ip,docker里面直接获取到的ip可能是gateway的ip|
|--proxy-jdbc-url|指定proxy连接的数据库url|
### ui 镜像
    
|参数|作用|
|:---|:---:|
|--zk-address|使用到的zk地址|
|--proxy-jdbc-url|指定ui连接的数据库url|  

### demo 镜像

|参数|作用|
|:---|:---:|
|--proxy-host|指定连接的proxy的host|
|--app-class|指定应用的某个类(参考 [快速启动](https://github.com/qunarcorp/bistoury/blob/master/docs/cn/quick_start.md#%E5%90%AF%E5%8A%A8%E5%8F%82%E6%95%B0) -c参数)|  

`默认设置了 --cap-add=SYS_PTRACE, 用来开启ptrace(如果不开启,和attach相关的不能使用)`

