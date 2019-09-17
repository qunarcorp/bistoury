# docker支持
* [怎样使用](#怎样使用)
* [生成的镜像](#生成的镜像)
* [默认启动的容器](*默认启动的容器)
* [镜像参数](#镜像参数)

> 使用docker部署,会包含整套服务,包括ui\proxy\两个demo应用.

> 使用到的脚本都在script目录下

## 怎样使用
打包镜像 
> ./quick_start_build.sh -d -p prod
>> 参数 -d代表docker模式  
>> 参数 -p代表maven的profile 默认为local,docker打包时需要使用prod,
用来使用mysql数据库

打包完之后,运行所有镜像
> ./demo_docker_start.sh  宿主机ip
>> 访问 [127.0.0.1:9091](http://127.0.0.1:9091/),即可使用到bistoury的所有功能  
`第一次访问可能需要等待20秒左右`

## 生成的镜像
|生成的镜像名称|模块|
|:---:|:---:|
|bistoury-proxy|proxy模块|
|bistoury-ui|ui模块|
|bistoury-agent|agent模块|
|bistoury-demo|包含了agent进程的demo应用|

对于端口,都与快速开始里面相对应 链接:
[快速开始](https://github.com/qunarcorp/bistoury/blob/master/docs/cn/quick_start.md)

## 默认启动的容器
启动后会运行6个容器,分别是
1. zk进程
2. proxy进程
3. ui进程
4. db进程
5. 两个demo应用

## 镜像参数
`demo_docker_start.sh`脚本
> 会创建一个名为bistoury的网络,子网为172.19.0.0/16


1. zk    镜像
> 指定的ip: 172.19.0.2
2. proxy 镜像
> 指定的ip: 172.19.0.3
>   
    --real-ip参数 宿主机的ip
    --zk-address参数 使用到的zk地址
    --parse-agent-id参数 是否解析从agent传输过来的ip,docker里面直接获取到的ip可能是gateway的ip
    --proxy-jdbc-url参数 指定proxy连接的数据库url 
3. ui 镜像
> 指定的ip: 172.19.0.4

    1.--zk-address参数 使用到的zk地址
    2.--proxy-jdbc-url参数 指定ui连接的数据库url 
4. demo 镜像
> 指定的ip为172.19.0.5\172.19.0.6               
    1.--proxy-host 指定连接的proxy的host
    (参考[指定应用的class ](https://github.com/qunarcorp/bistoury/blob/master/docs/cn/quick_start.md#%E5%90%AF%E5%8A%A8%E5%8F%82%E6%95%B0中-c参数))   
    2.--app-class参数    
    3.--cap-add=SYS_PTRACE 开启ptrace(如果不开启,和attach相关的会不能使用)
    
5. mysql 镜像
> 指定的ip: 172.19.0.7

