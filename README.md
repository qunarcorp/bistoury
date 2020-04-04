# Bistoury

![GitHub](https://img.shields.io/github/license/qunarcorp/bistoury) 

`Bistoury` 是去哪儿网开源的一个对应用透明，无侵入的java应用诊断工具，用于提升开发人员的诊断效率和能力。

`Bistoury` 的目标是一站式java应用诊断解决方案，让开发人员无需登录机器或修改系统，就可以从日志、内存、线程、类信息、调试、机器和系统属性等各个方面对应用进行诊断，提升开发人员诊断问题的效率和能力。

`Bistoury` 在公司内部原有agent的基础上集成Alibaba开源的[arthas](https://github.com/alibaba/arthas)和唯品会开源的[vjtools](https://github.com/vipshop/vjtools)，提供了更加丰富的功能，感谢他们做出的优秀工作。

## 简介

Arthas和vjtools已经是很优秀的工具，我们为什么还要开发Bistoury？

Arthas和vjtools通过命令行或类似的方式使用，不可否认命令行在很多时候具有比较高的效率；但图形化界面也有其自身的优点，特别是在参数复杂时使用起来更加简单，效率更高。Bistoury在保留命令行界面的基础上，还对很多命令提供了图形化界面，方面用户使用。

Arthas和vjtools针对单台机器，从机器的维度对系统进行诊断，没有提供全局的视角；而在线应用往往部署在多台机器，Bistoury可以和使用方应用中心整合，从应用的维度对系统进行诊断，提供了更多的可能。

Arthas和vjtools在使用上，要么登录机器，要么需要使用者提供相应的ip和端口；Bistoury去掉各种设置，提供统一的web入口，从页面上选择应用和机器即可使用。

除了这些针对性优化，Bistoury在保留arthas和vjtools的所有功能之外，还提供了更加丰富的功能。

Bistoury的[在线debug功能](docs/cn/debug.md)去掉了各种复杂参数，模拟ide调试体验，通过web界面提供断点调试的功能，可以在不阻塞应用的情况下捕获断点处的信息（包括本地变量、成员变量、静态变量和方法调用栈）。

Bistoury提供了[线程级cpu使用率监控](docs/cn/jstack.md)，可以监控系统每个线程的分钟级cpu使用率，并提供最近几天的历史数据查询。

Bistoury可以[动态对方法添加监控](docs/cn/monitor.md)，监控方法的调用次数、异常次数和执行时间，同时也保留最近几天的监控数据。

Bistoury提供了日志查看功能，可以使用tail、grep等命令对单台或同时对多台机器的日志进行查看。

Bistoury提供可视化页面实时查看机器和应用的各种信息，包括主机内存和磁盘使用、cpu使用率和load、系统配置文件、jar包信息，jvm信息、内存使用和gc等等。

## 快速上手

也许你正面对一个难以捉摸的线上问题束手无策，不妨来试试Bistoury的[快捷部署脚本](docs/cn/quick_start.md)，在一分钟内启动Bistoury然后[插入断点开始调试](docs/cn/debug.md)吧！ 

## 使用文档
- [快速开始](docs/cn/quick_start.md)
- [git及maven配置](docs/cn/gitlab_maven.md)
- [在线debug](docs/cn/debug.md)
- [线程级cpu使用率监控](docs/cn/jstack.md)
- [命令使用文档](docs/cn/commands.md)
- [动态监控](docs/cn/monitor.md)
- [应用中心](docs/cn/application.md)
- [生产部署](docs/cn/deploy.md)
- [常见问题汇总](docs/cn/FAQ.md)
- [设计文档](docs/cn/design/design.md)

## java版本要求

ui、proxy使用Java1.8，agent使用java1.7或java1.8，由于agent会attach到应用中，所以应用也需要使用Java1.7或Java1.8。Java9及后续版本由于改动较大，会在以后陆续支持。

## 系统要求

目前只支持linux系统（支持mac os）

## project

欢迎大家各种star，fork，提issue，pull request，感觉还可以就点个star吧！

## Q & A

- 前端有的地方似乎有点不那么好看，实现的似乎也不太棒 

    所有的前端代码都是后端同学兼职完成，欢迎各位前端大牛贡献相关代码。
    
## 常见问题汇总

使用Bistoury出现各种问题请先点[这里](docs/cn/FAQ.md)
    
## 技术支持

qq群：717242486

![QQ](docs/image/bistoury_qq_small.png)

## Screenshots

通过命令行界面查看日志，使用arthas和vjtools的各项功能
![console](docs/image/console.png)

在线debug，在线应用调试神器
![debug](docs/image/debug_panel.png)

线程级cpu监控，帮助你掌握线程级cpu使用率
![jstack_dump](docs/image/jstack.png)

在web界面查看JVM运行信息，以及各种其它信息
![jvm](docs/image/jvm.png)

动态给方法添加监控
![monitor](docs/image/monitor.png)

线程dump
![thread_dump](docs/image/thread_dump.png)
