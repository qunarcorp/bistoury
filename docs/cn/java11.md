# Bistoury支持Java 11
部分公司已经开始使用Java11，bistoury开始对Java 11进行支持，如果在使用过程中遇到问题可以在issues中反馈。

为了支持Java11，bistoury在功能上进行了取舍，bistoury 不再支持qjtop、qjmap、qjmxcli命令。
- qjtop用于查看JVM指标及繁忙线程，删除这个命令后可到主机信息页面查看 JVM 指标及繁忙线程
- qjmap用于分代打印内存信息，删除之后可以使用heapdump命令dump内存信息
- qjmxcli JMX 查看工具，删除之后可以使用mbean命令查看mxbean信息

**注意**：当Java版本高于Java8时，启动agent时，请在启动参数添加` --add-opens=java.base/jdk.internal.perf=ALL-UNNAMED`

**注意** 当agent和应用的java版本不一致时，可能会出错，建议agent和应用使用相同的大版本。
