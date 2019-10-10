FROM debian:buster-slim
LABEL maintainer="wenbeicai@gmail.com"

WORKDIR .

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY  --from=bistoury-agent:latest  /home/q/bistoury  /home/q/bistoury

#openjdk 有些功能不支持,使用oracle jdk
COPY  --from=registry.cn-hangzhou.aliyuncs.com/bistoury/jdk8:latest /home/q/bistoury/java /home/q/bistoury/java

COPY  --from=registry.cn-hangzhou.aliyuncs.com/bistoury/bistoury-demo:latest  /home/q/www/spring-mvc-boot-0.0.1-SNAPSHOT.jar /home/q/www/spring-mvc-boot-0.0.1-SNAPSHOT.jar
ENV PATH="/home/q/bistoury/java/bin:${PATH}"
ENV JAVA_HOME /home/q/bistoury/java

COPY boot_start.sh /home/q/www/boot_start.sh

EXPOSE 8686

ENTRYPOINT ["/home/q/www/boot_start.sh","--agent-java-home","/home/q/bistoury/java"]




