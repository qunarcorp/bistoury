FROM centos
#标明著作人的名称和邮箱 
MAINTAINER wenbeicai wenbeicai@gmail.com
#测试一下网络环境
RUN ping -c 1 www.baidu.com

WORKDIR .

COPY  --from=bistoury-agent:latest  /home/q/bistoury  /home/q/bistoury
COPY  --from=registry.cn-hangzhou.aliyuncs.com/bistoury/jdk8:v0.1 /home/q/bistoury/java /home/q/bistoury/java

COPY spring-mvc-boot-0.0.1-SNAPSHOT.jar /home/q/www/spring-mvc-boot-0.0.1-SNAPSHOT.jar

COPY boot_start.sh /home/q/www/boot_start.sh

EXPOSE 8686

ENTRYPOINT ["/home/q/www/boot_start.sh","--agent-java-home","/home/q/bistoury/java"]




