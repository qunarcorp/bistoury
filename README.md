# Bistoury

![GitHub](https://img.shields.io/github/license/qunarcorp/bistoury) 

`Bistoury` 去哪网开源的一个对应用透明，无侵入的Java诊断工具，Bistoury在公司内部原有agent的基础上集成Alibaba开源的[Arthas](https://github.com/alibaba/arthas)和唯品会开源的[vjtools](https://github.com/vipshop/vjtools)，提供更加丰富的功能。

使用`Bistoury`你可以
- 查看应用日志
- 查看主机运行状态
- 在线debug
- 动态监控
- JVM运行状态监控
- thread dump
- jstack
- 能查看这个类从哪个 jar 包加载的，为什么会报各种类相关的 Exception。
- 能查看的代码中的方法有没有执行到。
- 命令行工具，命令列表请查看[命令使用文档](docs/cn/commands.md)

## Usage

- [部署](docs/cn/deploy.md)
- [命令使用文档](docs/cn/commands.md)
- [在线debug使用文档](docs/cn/debug.md)
- [动态监控使用文档](docs/cn/monitor.md)
- [定时jstack和jmap -histo使用文档](docs/cn/jstack_jmap.md)

## Screenshots
![console](docs/image/console.png)

![jvm](docs/image/jvm.png)

![debug](docs/image/debug_panel.png)

![monitor](docs/image/monitor.png)

![thread_dump](docs/image/thread_dump.png)

![jstacl_dump](docs/image/jstack.png)