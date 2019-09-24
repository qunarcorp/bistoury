FROM centos
LABEL maintainer="wenbeicai@gmail.com"

WORKDIR .

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY  --from=registry.cn-hangzhou.aliyuncs.com/bistoury/jdk8:v0.1 /home/q/bistoury/java /home/q/bistoury/java

ENV JAVA_HOME /home/q/bistoury/java

COPY bin /home/q/bistoury/ui/bin
COPY lib /home/q/bistoury/ui/lib
COPY conf /home/q/bistoury/ui/conf
COPY webapp /home/q/bistoury/ui/webapp

ADD ui_start.sh /home/q/bistoury/ui/ui_start.sh

ENTRYPOINT ["/home/q/bistoury/ui/ui_start.sh","--ui-java-home","/home/q/bistoury/java"]
EXPOSE 9091