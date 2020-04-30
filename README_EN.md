# Bistoury

![license](https://img.shields.io/github/license/qunarcorp/bistoury)
![release](https://img.shields.io/github/v/release/qunarcorp/bistoury)

`Bistoury` is a transparent and non-invasive Java application diagnostic tool open-source by qunar, which is used to improve the diagnostic efficiency and ability of developers.

The goal of `bistoury` is a one-stop Java application diagnosis solution, which enables developers to diagnose applications from log, memory, thread, class information, debugging, machine and system properties and other aspects without logging in to the machine or modifying the system, so as to improve the efficiency and ability of developers to diagnose problems.

`Bistoury` integrates the [Arthas](https://github.com/alibaba/arthas) of Alibaba and [vjtools](https://github.com/vipshop/vjtools) of vipshop on the basis of the original agent in the company, providing more rich functions. Thank them for their excellent work.

## Introduction

Arthas and vjtools are already excellent tools. Why should we develop bistoury?

Arthas and vjtools are used by command line or similar. It is undeniable that the command line is more efficient in many cases; but the graphical interface has its own advantages, especially when the parameters are complex and easier to use and more efficient. Based on the reservation of the command line interface, Bistoury also provides a graphical interface for many commands, which is used by users.

Arthas and vjtools diagnose the system from  a single machine dimension, without providing a global perspective; however, online applications are often deployed on multiple machines, and bistoury can integrate with the application center to diagnose the system from the application dimension, providing more possibilities.

Arthas and vjtools are used, either to log in to the machine or require the user to provide the appropriate ip and port; Bistoury throw away the various settings, provides a unified web portal, only selects applications and machines from the page to use the bistoury.

In addition to these targeted optimizations, Bistoury offers a richer set of features in addition to all the features of arthas and vjtools.

Bistoury's [online debug feature](docs/en/debug.md) removes complex parameters, simulates the ide debugging experience, provides breakpoint debugging through the web interface, and captures breakpoint information (including local variables, member variables, statics variables and methods call stack) without blocking the application.

Bistoury provides [thread-level cpu usage monitoring](docs/en/jstack.md), which monitors the each minute cpu usage of each thread in the system and provides historical data queries for the last few days.

Bistoury can [dynamically add monitoring for a method](docs/en/monitor.md), monitor the number of calls, the number of exceptions, and the execution time, while also retaining the monitoring data for the last few days.

Bistoury provides a log view function, which can use the tail, grep and other commands to view logs on single machine, or to view logs on multiple machines at the same time.

Bistoury provides a visual page to view machine and application information in real time, including host memory and disk usage, cpu usage and load, system configuration files, jar package information, jvm information, memory usage, and gc.

## Quick start
Bistoury provides a [quick start script](docs/en/quick_start.md) that launches bistoury in a short time and quickly experiences bistoury functionality.

## Usage
- [Quick start](docs/en/quick_start.md)
- [Git and maven deploy](docs/en/gitlab_maven.md)
- [Debug onlie](docs/en/debug.md)
- [Thread-level cpu usage monitoring](docs/en/jstack.md)
- [Dynamically add monitoring for a method](docs/en/monitor.md)
- [Application center](docs/en/application.md)
- [Online deploy](docs/en/deploy.md)
- [FAQ](docs/en/FAQ.md)
- [Design](docs/en/design/design.md)

## Java
ui、proxy use jdk1.8+, agent use jdk1.7+, Because the agent will attach to the application, the application also needs to use jdk1.7+. It is best to keep the application and agent versions consistent. Click [here](docs/en/java11.md) to run bistoury using Java11.

## OS
You can run bistoury on linxu and macos.

## Porject
Welcome to star, fork, issue, pull request. If you feel OK, you can also order star!

## Q & A
If you have any problems using bistoury, please click [here](docs/en/FAQ.md) first.

## Screenshots
Through the command line interface, bistoury can view logs and use various functions of Arthas and vjtools
![console](docs/image/console.png)

Debug online, on line application debugging artifact
![debug](docs/image/debug_panel.png)

Thread-level cpu usage monitoring，Help you master the CPU usage of each thread
![jstack_dump](docs/image/jstack.png)

View JVM running information and various other information in the web interface
![jvm](docs/image/jvm.png)

Dynamically add monitoring for a method
![monitor](docs/image/monitor.png)

thread dump
![thread_dump](docs/image/thread_dump.png)