![](../image/design.png)
- 1、proxy启动并将自己的ip端口信息注册到zookeeper
- 2、agent启动并通过proxy的域名向proxy请求netty连接信息
- 3、收到请求的proxy返回自身的netty连接信息
- 4、agent根据获取到的netty连接信息连接peoxy，并保持一个心跳
- 5、ui从zk上获取所有peoxy的ip端口信息
- 6、请求proxy，确认我要连接的agent是否在当前peoxy上
- 7、返回agent是否连接在本机的结果
- 8、ui使用websocket发送命令到peoxy
- 9、proxy对名进行初步加工校验后将命令转发到agent
- 10、agent返回命令的处理结果
- 11、proxy返回命令结果

