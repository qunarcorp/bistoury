FROM openjdk:8u222-jdk-slim
LABEL maintainer="wenbeicai@gmail.com"

WORKDIR .

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY bin /home/q/bistoury/proxy/bin
COPY lib /home/q/bistoury/proxy/lib
COPY conf /home/q/bistoury/proxy/conf
COPY webapp /home/q/bistoury/proxy/webapp
COPY proxy_start.sh /home/q/bistoury/proxy/proxy_start.sh

EXPOSE 9880
EXPOSE 9881
EXPOSE 9090

ENTRYPOINT ["/home/q/bistoury/proxy/proxy_start.sh","--proxy-java-home","/usr/local/openjdk-8"]
