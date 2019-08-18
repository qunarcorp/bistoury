# Bistoury

![GitHub](https://img.shields.io/github/license/qunarcorp/bistoury) 

`Bistoury` 是去哪儿网开源的一个对应用透明，无侵入的java应用诊断工具，用于提升开发人员的诊断效率和能力。
  
Bistoury在公司内部原有agent的基础上集成Alibaba开源的[Arthas](https://github.com/alibaba/arthas)和唯品会开源的[vjtools](https://github.com/vipshop/vjtools)，提供了更加丰富的功能。

## 简介

Arthas和vjtools已经是很优秀的工具，我们为什么还要开发Bistoury？

Arthas和vjtools通过命令行或类似的方式使用，不可否认命令行在很多时候具有比较高的效率；但图形化界面也有自身的优点，特别是在参数复杂时使用起来更加简单，效率更高。Bistoury在保留命令行界面的基础上，还对很多命令提供了图形化界面，方面用户使用。

Arthas和vjtools针对单台机器，从机器的维度对系统进行诊断；但在线应用往往部署在多台机器，Bistoury可以和使用方应用中心整合，从应用的维度对系统进行诊断，提供了更多的可能。

Arthas和vjtools在使用上，要么登录机器，要么需要使用者提供相应的ip和端口；Bistoury去掉各种设置，提供统一的web入口，从页面上选择应用和机器即可使用。

除了这些针对性优化，Bistoury在保留arthas和vjtools的所有功能之外，还提供了更加丰富的功能。

Bistoury的[在线debug功能](docs/cn/debug.md)去掉了各种复杂参数，模拟ide调试体验，通过web界面提供断点调试的功能，可以在不阻塞应用的情况下捕获断点处的所有信息（包括本地变量、成员变量、静态变量和方法调用栈）。

Bistoury提供了[线程级cpu使用率监控](docs/cn/jstack.md)，可以监控系统每个线程的分钟级cpu使用率，并提供最近几天的历史数据查询。

Bistoury可以[动态对方法添加监控](docs/cn/monitor.md)，监控方法的调用次数、异常次数和执行时间，同时也保留最近几天的监控数据。

Bistoury提供了日志查看功能，可以使用tail、grep等命令对单台或多台机器的日志进行查看。

Bistoury提供可视化页面实时查看机器和应用的各种信息，包括主机内存和磁盘使用、cpu load和使用率、系统配置文件、jar包信息、jvm信息、内存使用和gc等等。

上述功能只是Bistoury所有功能的一部分，各项功能的使用和说明请参考具体的使用文档。

Bistoury的目标是一站式的java应用诊断方案，让开发人员无需登录机器，就可以从日志、机器和系统属性、内存、线程、类信息、调试等各个方面对应用进行诊断，提升开发人员诊断问题的效率和能力。

## Usage
- [快速开始](docs/cn/quick_start.md)
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