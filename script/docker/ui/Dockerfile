FROM openjdk:8u222-jdk-slim
LABEL maintainer="wenbeicai@gmail.com"

WORKDIR .

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY bin /home/q/bistoury/ui/bin
COPY lib /home/q/bistoury/ui/lib
COPY conf /home/q/bistoury/ui/conf
COPY webapp /home/q/bistoury/ui/webapp

ADD ui_start.sh /home/q/bistoury/ui/ui_start.sh

ENTRYPOINT ["/home/q/bistoury/ui/ui_start.sh","--ui-java-home","/usr/local/openjdk-8"]
EXPOSE 9091