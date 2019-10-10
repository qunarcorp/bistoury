#/bin/bash
#创建网络
echo "start create network"
docker network create --subnet=172.19.0.0/16 bistoury
#mysql 镜像
echo "start run mysql image"
docker run --name mysql -p 3307:3306 -e MYSQL_ROOT_PASSWORD=root -d -i --net bistoury --ip 172.19.0.7  registry.cn-hangzhou.aliyuncs.com/bistoury/bistoury-db
#zk 镜像
echo "start run zk image"
docker run -d -p 2181:2181 -it --net bistoury --ip 172.19.0.2 registry.cn-hangzhou.aliyuncs.com/bistoury/zk:latest
sleep 30
#proxy 镜像
echo "start run proxy module"
docker run -d -p 9880:9880 -p 9881:9881 -p 9090:9090 -i --net bistoury --ip 172.19.0.3 registry.cn-hangzhou.aliyuncs.com/bistoury/bistoury-proxy --real-ip $1 --zk-address 172.19.0.2:2181 --proxy-jdbc-url jdbc:mysql://172.19.0.7:3306/bistoury
#ui 镜像
echo "start run ui module"
docker run -p 9091:9091  -it -d --net bistoury --ip 172.19.0.4 registry.cn-hangzhou.aliyuncs.com/bistoury/bistoury-ui --zk-address 172.19.0.2:2181 --ui-jdbc-url jdbc:mysql://172.19.0.7:3306/bistoury
#boot 镜像
echo "start run demo application"
docker  run -it -d  -p 8686:8686 -i --net bistoury --ip 172.19.0.5 registry.cn-hangzhou.aliyuncs.com/bistoury/bistoury-demo --proxy-host $1:9090
docker  run -it -d  -p 8687:8686 -i --net bistoury --ip 172.19.0.6 registry.cn-hangzhou.aliyuncs.com/bistoury/bistoury-demo --proxy-host $1:9090
