#agent docker file
FROM alpine:3.2
#标明著作人的名称和邮箱 
MAINTAINER wenbeicai wenbeicai@gmail.com
#测试一下网络环境
RUN ping -c 1 www.baidu.com

WORKDIR .
COPY bin /home/q/bistoury/agent/bin
COPY lib /home/q/bistoury/agent/lib