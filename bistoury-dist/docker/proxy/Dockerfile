FROM centos
#标明著作人的名称和邮箱 
MAINTAINER wenbeicai wenbeicai@gmail.com
#测试一下网络环境
RUN ping -c 1 www.baidu.com

WORKDIR .

COPY  --from=registry.cn-hangzhou.aliyuncs.com/bistoury/jdk8:v0.1 /home/q/bistoury/java /home/q/bistoury/java

ENV JAVA_HOME /home/q/bistoury/java

COPY bin /home/q/bistoury/proxy/bin
COPY lib /home/q/bistoury/proxy/lib
COPY conf /home/q/bistoury/proxy/conf
COPY webapp /home/q/bistoury/proxy/webapp
COPY proxy_start.sh /home/q/bistoury/proxy/proxy_start.sh

EXPOSE 9880
EXPOSE 9881
EXPOSE 9090

ENTRYPOINT ["/home/q/bistoury/proxy/proxy_start.sh","--proxy-java-home","/home/q/bistoury/java"]

#使用时放置在快速启动的bistoury-proxy-bin目录下
#proxy 镜像
#docker run -d -it -p 9880:9880 -p 9881:9881 -p 9090:9090  bistoury/proxy
## --real-ip 192.168.31.58      (当前容器,可以访问的ip)
## --zk-address 172.20.0.2:2181 (zk注册中心)
## --parse-agent-id true        (是否解析agent传送过来的agentid,不解析默认为agnet ip)
## --proxy-jdbc-url jdbc:mysql://172.20.0.7:3306/bistoury (数据库jdbc链接)