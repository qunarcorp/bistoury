#基础镜像使用 mysql:latest
FROM mysql:5.7.22

##标明著作人的名称和邮箱 
MAINTAINER wenbeicai wenbeicai@gmail.com

#定义会被容器自动执行的目录
ENV AUTO_RUN_DIR /docker-entrypoint-initdb.d

#定义初始化sql文件
ENV INSTALL_DB_SQL bistoury_init.sql

#把要执行的sql文件放到/home/q/bistoury/db目录下，容器会自动执行这个sql
COPY bistoury_init.sql $AUTO_RUN_DIR/

#给执行文件增加可执行权限
RUN chmod a+x $AUTO_RUN_DIR/$INSTALL_DB_SQL