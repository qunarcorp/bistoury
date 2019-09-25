# Bistoury支持Java 11
部分公司已经开始使用Java11，bistoury开始对Java 11进行支持，如果在使用过程中遇到问题可以在issues中反馈。

为了支持Java11，bistoury在功能上进行了取舍，bistoury 不再支持qjtop、qjmap、qjmxcli命令。
- qjtop用于查看JVM指标及繁忙线程，删除这个命令后可到主机信息页面查看 JVM 指标及繁忙线程
- qjmap用于分代打印内存信息，删除之后可以使用heapdump命令dump内存信息
- qjmxcli JMX 查看工具，删除之后可以使用mbean命令查看mxbean信息

## 打包
代码新增两个profile，BigJavaVersion和SmallJavaVersion，其中BigJavaVersion在Java版本大于等于Java9时使用，SmallJavaVersion在Java版本小于Java9时使用。

在使用script/build.sh和script/quick_start_build.sh打包时，根据maven运行时的`Java version`选择profile进行打包，即` ./mvnw -v`命令的返回结果中的`Java version`的值。



### 打包步骤
- 在bistoury/pom.xml中修改java_source_version、java_target_version（agent的编译版本）和server_java_source_version、server_java_target_version（ui和proxy的编译版本）的值。agent和ui、server的版本不能一个是1.8及以下版本另一个是1.8以上的版本
- 运行script/build.sh和script/quick_start_build.sh进行打包。

**注意**：当Java版本高于Java8时，如果你需要自己启动agent，请在启动参数添加` --add-opens=java.base/jdk.internal.perf=ALL-UNNAMED`
