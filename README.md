# Bistoury

![GitHub](https://img.shields.io/github/license/qunarcorp/bistoury) 

`Bistoury` 是去哪儿网开源的一个对应用透明，无侵入的Java诊断工具，Bistoury在公司内部原有agent的基础上集成Alibaba开源的[Arthas](https://github.com/alibaba/arthas)和唯品会开源的[vjtools](https://github.com/vipshop/vjtools)，提供更加丰富的功能。

## 背景

在已经有arthas和vjtools的情况下，我们为什么还要开发Bistoury？

Arthas和vjtools提供了很多的功能，可以有效的进行java问题诊断，但是，二者均依赖于命令行，也就说使用这个工具需要登录上主机上才能使用，尽管arthas提供了ide插件及web console，但是使用时也需要提供主机的ip及端口，这给我们使用带来了很大的不便；部分命令的参数复杂；Arthas提供的watch命令观测方法执行数据仅可观测返回值、抛出异常、入参，无法获取方法执行的中间状态。monitor命令对方法的监控数据在命令退出后就监控数据就消失了。

基于以上问题，Bistoury除了兼容arthas和vjtools的所有命令外，还对arthas和vitools进行优化和扩展，

Bistoury为所有主机提供统一的入口，在主机上安装好bistoury-agent后就可以通过web页面使用所有功能。

Bistoury在线debug功能去掉了复杂的参数，只需要在页面进行点击即可完成在线debug，bistoury debug可以收集到断点处的运行情况，能收集到的范围为：成员变量、局部变量、静态变量和方法调用栈，体验媲美IDE的在先debug。

Bistoury动态监控可以监控方法的调用次数、异常次数及执行时间，并且保留最近三天的监控数据。

Bistoury可以针对统一应用下的多台主机进行操作，支持多机的命令有ls、head、tail、grep、zgrep。

Bistoury提供了线程级cpu使用率监控，可监控jvm进程下每个线程的cpu使用率，并且提供最近三天的历史数据查询。

Bistoury提供可视化页面实时查看主机信息和jvm监控，查看范围为：主机内存及磁盘使用情况、cpu load、应用使用的配置文件、应用依赖的jar包、JVM版本信息、JVM参数、jvm内存使用情况和GC情况等。

Bistoury提供实时线程dump、线程死锁检测。

使用`Bistoury`你可以
- 查看应用日志
- 查看主机运行状态
- 在线debug
- 动态监控
- 线程级cpu使用率监控
- JVM运行状态监控
- thread dump
- jstack
- jmap
- JVM数据紧急收集，一键收集jstack、jmap以及GC日志等相关信息
- 能查看这个类从哪个 jar 包加载的，为什么会报各种类相关的 Exception。
- 从全局视角来查看系统的运行状况。
- 觉得代码和想的不一样？反编译class试试


## Usage

- [部署](docs/cn/deploy.md)
- [命令使用文档](docs/cn/commands.md)
- [在线debug使用文档](docs/cn/debug.md)
- [动态监控使用文档](docs/cn/monitor.md)
- [线程级cpu使用率监控](docs/cn/jstack.md)
- [堆内存对象监控](docs/cn/jmap.md)

## 即将发布

更完善的文档

通过一个脚本在单机部署整套Bistoury服务

## Screenshots

命令行交互
![console](docs/image/console.png)

JVM运行信息
![jvm](docs/image/jvm.png)

在线debug
![debug](docs/image/debug_panel.png)

动态监控
![monitor](docs/image/monitor.png)

线程dump
![thread_dump](docs/image/thread_dump.png)

线程级cpu监控
![jstack_dump](docs/image/jstack.png)