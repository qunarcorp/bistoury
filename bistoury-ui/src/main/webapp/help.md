---
title: Bistory 帮助文档
description: Bistory 帮助文档
html:
    offline: false
    toc: true
toc:
    depth_from: 1
    depth_to: 2
---
@import "css/bootstrap.min.css"
@import "css/header.css"
@import "css/help.less"
@import "js/jquery-3.3.1.min.js"
@import "js/bootstrap.min.js"
<!-- 
    文档说明：
    1、使用vscode+Markdown Preview Enhanced插件
    2、打开Enable Script Execution开关生成侧边栏目录
    3、导出html
    4、（如果没有使用数学表达式，可以不操作此步）修改css file:///C:\Users\root\.vscode\extensions\shd101wyy.markdown-preview-enhanced-0.3.13\node_modules\@shd101wyy\mume\dependencies\katex\katex.min.css 为 css/katex.min.css
 -->

<script type="application/javascript">
 $(document).ready(function () {
     $("body").prepend('<div class="col-md-12 header" id="header"></div>');
 })
</script>

@import "js/header.js"

[在线Debug使用说明](/api/url/redirect.do?name=debug.help.url)

[动态监控使用说明](/api/url/redirect.do?name=monitor.help.url)

[线程级cpu使用率监控使用说明](/api/url/redirect.do?name=jstack.help.url)

[堆内存对象监控使用说明](/api/url/redirect.do?name=jmap.help.url)
# Bistoury
![license](https://img.shields.io/github/license/qunarcorp/bistoury)
![release](https://img.shields.io/github/v/release/qunarcorp/bistoury)
## Bistoury是什么？

`Bistoury` 是去哪儿网开源的一个对应用透明，无侵入的java应用诊断工具，用于提升开发人员的诊断效率和能力。

`Bistoury` 的目标是一站式java应用诊断解决方案，让开发人员无需登录机器或修改系统，就可以从日志、内存、线程、类信息、调试、机器和系统属性等各个方面对应用进行诊断，提升开发人员诊断问题的效率和能力。

`Bistoury` 在公司内部原有agent的基础上集成Alibaba开源的[arthas](https://github.com/alibaba/arthas)和唯品会开源的[vjtools](https://github.com/vipshop/vjtools)，提供了更加丰富的功能，感谢他们做出的优秀工作。

## 简介

Arthas和vjtools已经是很优秀的工具，我们为什么还要开发Bistoury？

Arthas和vjtools通过命令行或类似的方式使用，不可否认命令行在很多时候具有比较高的效率；但图形化界面也有其自身的优点，特别是在参数复杂时使用起来更加简单，效率更高。Bistoury在保留命令行界面的基础上，还对很多命令提供了图形化界面，方面用户使用。

Arthas和vjtools针对单台机器，从机器的维度对系统进行诊断，没有提供全局的视角；而在线应用往往部署在多台机器，Bistoury可以和使用方应用中心整合，从应用的维度对系统进行诊断，提供了更多的可能。

Arthas和vjtools在使用上，要么登录机器，要么需要使用者提供相应的ip和端口；Bistoury去掉各种设置，提供统一的web入口，从页面上选择应用和机器即可使用。

除了这些针对性优化，Bistoury在保留arthas和vjtools的所有功能之外，还提供了更加丰富的功能。

Bistoury的[在线debug功能](/api/url/redirect.do?name=debug.help.url)去掉了各种复杂参数，模拟ide调试体验，通过web界面提供断点调试的功能，可以在不阻塞应用的情况下捕获断点处的信息（包括本地变量、成员变量、静态变量和方法调用栈）。

Bistoury提供了[线程级cpu使用率监控](/api/url/redirect.do?name=jstack.help.url)，可以监控系统每个线程的分钟级cpu使用率，并提供最近几天的历史数据查询。

Bistoury可以[动态对方法添加监控](/api/url/redirect.do?name=monitor.help.url)，监控方法的调用次数、异常次数和执行时间，同时也保留最近几天的监控数据。

Bistoury提供了日志查看功能，可以使用tail、grep等命令对单台或同时对多台机器的日志进行查看。

Bistoury提供可视化页面实时查看机器和应用的各种信息，包括主机内存和磁盘使用、cpu使用率和load、系统配置文件、jar包信息、jvm信息、内存使用和gc等等。


## Bistoury 能做什么？
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


Bistoury采用命令行交互模式，同时提供丰富的自动补全功能，进一步方便进行问题的定位和诊断。

可以通过 [Web Console](/) 使用 Bistoury

---
# 安装
## 下载
我们推荐你直接下载编译好的文件来运行bistoury。
## 应用注册
联系管理员在应用中心注册应用，然后再管理中将自己的服务器添加到对应的应用中，并在后续及时维护应用、服务器信息
**注意：** 一台主机（ip）只可绑定到一个应用
## 配置
### 发布信息配置
你需要在日志目录下创建<span id="release_path"></span>文件（注意：文件路径可能是相对日志目录的相对路径，也可能是绝对路径），存放应用发布信息，默认格式如下：
```properties
#gitlab项目名
project=tc/bistoury
#项目所属module，没有module时值为英文句号[.]
module=bistoury-ui
#应用运行的版本号/分支/tag
output=master
```
### JVM参数配置
Agent启动前需要在bin/bistoury-agent-env.sh的JAVA_OPTS设置以下参数

|参数名称|是否必须|默认值|说明|
|-------|---|---|----|
|bistoury.store.path|否|/home/bistoury/store|bistoury agent数据存放路径，包括rocksdb存放的监控、jstack及jmap数据和反编译代码临时文件的存放|
|bistoury.proxy.host|是||proxy的域名，具体值请联系管理员，agent依赖该值获取proxy的连接配置信息|
|bistoury.app.lib.class|是||应用依赖的jar包中的一个类（推荐使用公司内部中间件的jar包或Spring相关包中的类，如org.springframework.web.servlet.DispatcherServlet），agent通过该类获取加载应用类的classloader|
|bistoury.pid.handler.jps.symbol.class|否|org.apache.catalina.startup.Bootstrap|attach的应用入口类，用于使用jps -l命令获取应用pid|
|bistoury.pid.handler.jps.enable|否|true|是否打开通过jps -l获取pid的开关|
|bistoury.pid.handler.ps.enable|否|true|是否打开通过ps aux|grep java 获取pid的开关|
|bistoury.app.classes.path|否|bistoury.app.lib.class对应jar包目录同级的classes目录|项目代码编译后字节码存放目录，一般情况下为classes目录|
|bistoury.agent.workgroup.num|否|2|agent netty work group 线程数|
|bistoury.agent.thread.num|否|16|agent执行命令的线程数|

运行bin目录下的脚本进行启动，可以在bistoury-agent-env.sh中的JAVA_OPTS里配置JVM相关参数，GC相关配置已配置，
## 启动bistoury agent
在启动是可以通过-p指定pid确定agent attach特定的java进程，不指定时会通过jps -l和ps aux|grep java 命令及proxy中配置的参数解析pid，优先级依次降低。

使用 `./bistoury-agent.sh -h` 查看脚本参数信息

+ 启动
```shell
./bistoury-agent.sh -p 100 start
./bistoury-agent.sh start
```
+ 停止
```shell
./bistoury-agent.sh stop
```
+ 重启
```shell
./bistoury-agent.sh -p 101 restart
./bistoury-agent.sh restart
```

---
# 快速入门

## 基础命令
>+ [ls](#ls) 列出文件列表
>+ [cat](#cat) 输出文件内容
>+ [pwd](#pwd) 返回当前工作目录
>+ [history](#history) 打印arthas的历史命令
>+ [tail](#tail) 查看文件内容    
>+ [grep](#grep) 搜索满足条件的结果。
>+ [zgrep](#zgrep) 在压缩文件中搜索满足条件的结果

## jvm相关
>+ [dashboard](#dashboard) 当前系统的实时数据面板
>+ [jstack](#jstack) 查看当前 java 进程的堆栈状态
>+ [jstat](#jstat) 统计gc信息
>+ [thread](#thread) 查看当前 JVM 的线程堆栈信息
>+ [jvm](#jvm) 查看当前 JVM 的信息
>+ [mbean](#mbean) 查看 Mbean 的信息
>+ [sysprop](#sysprop) 查看和修改JVM的系统属性
>+ [sysenv](#sysenv) 查看当前JVM的环境属性
>+ [vmoption](#vmoption) 查看，更新VM诊断相关的参数
>+ [getstatic](#getstatic) 查看类的静态属性
>+ [heapdump](#heapdump) dump java heap, 类似jmap命令的heap dump功能。
>+ [logger](#logger) 查看logger信息，更新logger level
>+ [qjdump](#qjdump) 线上紧急收集JVM数据
>+ [qjtop](#qjtop)  不再支持 qjtop 命令，可到[主机信息](/machine.html)页面查看 JVM 指标及繁忙线程
>+ [qjmap](#qjmap)  不再支持 qjmap 命令，请使用 [heapdump](#heapdump) 命令 dump 内存信息
>+ [qjmxcli](#qjmxcli)  不再支持 qjmxcli 命令，请使用 [mbean](#mbean) 命令查看 MBean 信息，使用 [jstat](#jstat) 命令查看 GC 信息


## class/classloader相关
>+ [sc](#sc) 查看JVM已加载的类信息
>+ [sm](#sm) 查看已加载类的方法信息
>+ [dump](#dump) dump已加载类的byte code到特定目录
>+ [redefine](#redefine) 加载外部的.class文件，redefine到JVM里
>+ [jad](#jad) 反编译指定已加载类的源码
>+ [mc](#mc) 内存编绎器，内存编绎.java文件为.class文件
>+ [classloader](#classloader) 查看classloader的继承树，urls，类加载信息，使用classloader去getResource

## monitor/watch/trace相关
>+ [monitor](#monitor) 方法执行监控
>+ [watch](#watch) 方法执行数据观测
>+ [trace](#trace) 方法内部调用路径，并输出方法路径上的每个节点上耗时
>+ [stack](#stack) 输出当前方法被调用的调用路径
>+ [tt](#tt) 方法执行数据的时空隧道，记录下指定方法每次调用的入参和返回信息，并能对这些不同的时间下调用进行观测

## options
>options——查看或设置Bistoury全局开关

## Web Console
>可以通过 [Web Console](/qconsole.html) 使用`Bistoury`
# 命令列表

## ls
**ls命令** 用来显示目标列表，参数与linux中的参数一样。
>当选择一台机器时，该命令在一台机器上执行，当选择多台机器时，该命令可以同时在多台机器上执行，未选择机器则在所有机器上执行。
### 语法
> ls (选项) (参数)

### 选项
    -a：显示所有档案及目录（ls内定将档案名或目录名称为“.”的视为影藏，不会列出）；
    -A：显示除影藏文件“.”和“..”以外的所有文件列表；
    -C：多列显示输出结果。这是默认选项；
    -l：与“-C”选项功能相反，所有输出信息用单列格式输出，不输出为多列；
    -F：在每个输出项后追加文件的类型标识符，具体含义：“\*”表示具有可执行权限的普通文件，“/”表示目录，“@”表示符号链接，“|”表示命令管道FIFO，“=”表示sockets套接字。当文件为普通文件时，不输出任何标识符；
    -b：将文件中的不可输出的字符以反斜线“”加字符编码的方式输出；
    -c：与“-lt”选项连用时，按照文件状态时间排序输出目录内容，排序的依据是文件的索引节点中的ctime字段。与“-l”选项连用时，则排序的一句是文件的状态改变时间；
    -d：仅显示目录名，而不显示目录下的内容列表。显示符号链接文件本身，而不显示其所指向的目录列表；
    -f：此参数的效果和同时指定“aU”参数相同，并关闭“lst”参数的效果；
    -i：显示文件索引节点号（inode）。一个索引节点代表一个文件；
    --file-type：与“-F”选项的功能相同，但是不显示“\*”；
    -k：以KB（千字节）为单位显示文件大小；
    -l：以长格式显示目录下的内容列表。输出的信息从左到右依次包括文件名，文件类型、权限模式、硬连接数、所有者、组、文件大小和文件的最后修改时间等；
    -m：用“,”号区隔每个文件和目录的名称；
    -n：以用户识别码和群组识别码替代其名称；
    -r：以文件名反序排列并输出目录内容列表；
    -s：显示文件和目录的大小，以区块为单位；
    -t：用文件和目录的更改时间排序；
    -L：如果遇到性质为符号链接的文件或目录，直接列出该链接所指向的原始文件或目录；
    -R：递归处理，将指定目录下的所有文件及子目录一并处理；
    --full-time：列出完整的日期与时间；
    --color[=WHEN]：使用不同的颜色高亮显示不同类型的。

### 参数
>目录：指定要显示列表的目录，也可以是具体的文件。

## cat
**cat** 命令连接文件并打印到标准输出设备上，cat经常用来显示文件的内容
### 语法
>cat (选项) (参数)

### 选项
    -n或-number：有1开始对所有输出的行数编号；
    -b或--number-nonblank：和-n相似，只不过对于空白行不编号；
    -s或--squeeze-blank：当遇到有连续两行以上的空白行，就代换为一行的空白行；
    -A：显示不可打印字符，行尾显示“$”；
    -e：等价于"-vE"选项；
    -t：等价于"-vT"选项；

### 参数
> 文件列表：指定要连接的文件列表。

### 实例
>cat m1 （在屏幕上显示文件ml的内容）
cat m1 m2 （同时显示文件ml和m2的内容）
cat m1 m2 > file （将文件ml和m2合并后放入文件file中）

## pwd
>返回当前的工作目录，和linux命令类似

## history
>打印arthas的历史命令

## tail
>- **tail**命令用于输入文件中的尾部内容。tail命令默认在屏幕上显示指定文件的末尾10行。如果给定的文件不止一个，则在显示的每个文件前面加一个文件名标题。如果没有指定文件或者文件名为“-”，则读取标准输入。
>- 当选择一台机器时，该命令在一台机器上执行，当选择多台机器时，该命令可以同时在多台机器上执行，未选择机器则在所有机器上执行。
>- **注意**：如果表示字节或行数的N值之前有一个”+”号，则从文件开头的第N项开始显示，而不是显示文件的最后N项。N值后面可以有后缀：b表示512，k表示1024，m表示1 048576(1M)。、

### 语法
>tail(选项)(参数)

### 选项
    --retry：即是在tail命令启动时，文件不可访问或者文件稍后变得不可访问，都始终尝试打开文件。使用此选项时需要与选项“——follow=name”连用；
    -c<N>或——bytes=<N>：输出文件尾部的N（N为整数）个字节内容；
    -f <name/descriptor>或；--follow<nameldescript>：显示文件最新追加的内容。“name”表示以文件名的方式监视文件的变化。“-f”与“-fdescriptor”等效；
    -F：与选项“-follow=name”和“--retry"连用时功能相同；
    -n <N>或——line=<N>：输出文件的尾部N（N位数字）行内容。
    --pid=<进程号>：与“-f”选项连用，当指定的进程号的进程终止后，自动退出tail命令；
    -q或——quiet或——silent：当有多个文件参数时，不输出各个文件名；
    -s<秒数>或——sleep-interal=<秒数>：与“-f”选项连用，指定监视文件变化时间隔的秒数；
    -v或——verbose：当有多个文件参数时，总是输出各个文件名；
    --help：显示指令的帮助信息；
    --version：显示指令的版本信息。

### 参数
>文件列表：指定要显示尾部内容的文件列表。

### 实例
>tail file （显示文件file的最后10行）
tail +20 file （显示文件file的内容，从第20行至文件末尾）
tail -c 10 file （显示文件file的最后10个字符）

## zgrep
**zgrep**命令可以不解压过滤压缩文件中内容。
>当选择一台机器时，该命令在一台机器上执行，当选择多台机器时，该命令可以同时在多台机器上执行，未选择机器则在所有机器上执行。
###常用参数
|参数名称|参数说明|
|-------|-------|
|-i|忽略字符大小写区别
|-v|显示示不包含正则的所有行
|-a|将文件当作文本文件处理
###实例
```shell
root@local.example.com@bistoury:\>zgrep 12  log.2018-11-05.log.gz
2018-11-05 00:00:12.021 INFO  [-thread-1] qunar.tc.Test ? [?] current message offset: 2215513824, action offset: 8079227
2018-11-05 00:01:12.027 INFO  [-thread-1] qunar.tc.Test ? [?] current message offset: 2215513824, action offset: 8079227
2018-11-05 00:02:12.033 INFO  [-thread-1] qunar.tc.Test ? [?] current message offset: 2215513824, action offset: 8079227
2018-11-05 00:03:12.039 INFO  [-thread-1] qunar.tc.Test ? [?] current message offset: 2215513824, action offset: 8079227
...
```

## dashboard
>dashboard命令用于展示当前系统的实时数据面板，按 ctrl+c 退出。
会显示当前tomcat的实时信息，如HTTP请求的qps, rt, 错误数, 线程池信息等等。

### 实例
```shell
root@local.example.com@bistoury:\>dashboard
ID     NAME                GROUP        PRIORI STATE %CPU   TIME   INTER DAEMON
155    Timer-for-arthas-da system       10     RUNNA 61     0:0    false true   
137    New I/O boss #39    main         5      RUNNA 8      0:0    false true   
102    DubboResponseTimeou main         5      TIMED 4      0:11   false true   
49     Hashed wheel timer  main         5      TIMED 2      0:3    false false  
127    Hashed wheel timer  main         5      TIMED 2      0:3    false false  
31     New I/O worker #10  main         5      RUNNA 2      0:0    false true   
128    New I/O worker #31  main         5      RUNNA 2      0:0    false true   
19     qconfig-loader work main         5      RUNNA 2      0:0    false true   
24     Hashed wheel timer  main         5      TIMED 1      0:3    false false  
33     Hashed wheel timer  main         5      TIMED 1      0:3    false false  
Memory            used  total  max    usage     GC                                      
heap              186M  1957M  1957M  9.53%     gc.ps_scavenge.count     6   
ps_eden_space     108M  498M   498M   21.68%    gc.ps_scavenge.time(ms)  307                
ps_survivor_space 71M   93M    93M    76.34%    gc.ps_marksweep.count    0
ps_old_gen        5M    1365M  1365M  0.42%     gc.ps_marksweep.time(ms) 0                   
nonheap           66M   262M  304M    25.19%                                      
Runtime                                                                         
os.name             Linux                                                       
os.version          2.6.32-358.23.2.el6.x86_64                                                    
java.version        1.7.0_45  
```
数据说明

    - ID: Java级别的线程ID，注意这个ID不能跟jstack中的nativeID一一对应
    - NAME: 线程名
    - GROUP: 线程组名
    - PRIORITY: 线程优先级, 1~10之间的数字，越大表示优先级越高
    - STATE: 线程的状态
    - CPU%: 线程消耗的cpu占比，采样100ms，将所有线程在这100ms内的cpu使用量求和，再算出每个线程的cpu使用占   比。
    - TIME: 线程运行总时间，数据格式为分：秒
    - INTERRUPTED: 线程当前的中断位状态
    - DAEMON: 是否是daemon线程

## jstack
>查看应用进程的堆栈状态
命令执行完成后，除了在界面上查看，还可以使用执行结果中提示的链接进行下载。
### 实例
```shell
root@local.example.com@bistoury:\>jstack
2018-11-13 16:06:58
Full thread dump Java HotSpot(TM) 64-Bit Server VM (24.45-b08 mixed mode):

"DubboSharedHandler-thread-37774" daemon prio=10 tid=0x00007f8f64108800 nid=0x2ce8 waiting on condition [0x00007f8ef56de000]
   java.lang.Thread.State: TIMED_WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x000000078e7ad858> (a java.util.concurrent.SynchronousQueue$TransferStack)
	at java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:226)
	at java.util.concurrent.SynchronousQueue$TransferStack.awaitFulfill(SynchronousQueue.java:460)
	at java.util.concurrent.SynchronousQueue$TransferStack.transfer(SynchronousQueue.java:359)
	at java.util.concurrent.SynchronousQueue.poll(SynchronousQueue.java:942)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1068)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1130)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
	at java.lang.Thread.run(Thread.java:744)

"DubboSharedHandler-thread-37773" daemon prio=10 tid=0x00007f8f6439f800 nid=0x2ce7 waiting on condition [0x00007f8f68bce000]
   java.lang.Thread.State: TIMED_WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x000000078e7ad858> (a java.util.concurrent.SynchronousQueue$TransferStack)
	at java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:226)
	at java.util.concurrent.SynchronousQueue$TransferStack.awaitFulfill(SynchronousQueue.java:460)
	at java.util.concurrent.SynchronousQueue$TransferStack.transfer(SynchronousQueue.java:359)
	at java.util.concurrent.SynchronousQueue.poll(SynchronousQueue.java:942)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1068)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1130)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
	at java.lang.Thread.run(Thread.java:744)
......
......

"VM Thread" prio=10 tid=0x00007f8f90068800 nid=0x6fe6 runnable

"GC task thread#0 (ParallelGC)" prio=10 tid=0x00007f8f9001f000 nid=0x6fe2 runnable

"GC task thread#1 (ParallelGC)" prio=10 tid=0x00007f8f90021000 nid=0x6fe3 runnable

"GC task thread#2 (ParallelGC)" prio=10 tid=0x00007f8f90022800 nid=0x6fe4 runnable

"GC task thread#3 (ParallelGC)" prio=10 tid=0x00007f8f90024800 nid=0x6fe5 runnable

"VM Periodic Task Thread" prio=10 tid=0x00007f8f900a8000 nid=0x6fed waiting on condition

JNI global references: 238
```
## jstat
统计gc信息
相当于 jstat -gcutil pid 1000 1000
### 实例
```shell
root@local.example.com@bistoury:\>jstat
  S0     S1     E      O      P     YGC     YGCT    FGC    FGCT     GCT   
 37.50   0.00  25.84  64.93  31.10 2082417 29736.276  2386 1911.213 31647.489
 37.50   0.00  46.20  64.93  31.10 2082417 29736.276  2386 1911.213 31647.489
 37.50   0.00  68.09  64.93  31.10 2082417 29736.276  2386 1911.213 31647.489
 37.50   0.00  90.23  64.93  31.10 2082417 29736.276  2386 1911.213 31647.489
  0.00  75.00  11.87  64.94  31.10 2082418 29736.289  2386 1911.213 31647.502
  ...
```
## thread
>查看当前线程信息，查看线程的堆栈

### 参数说明
|参数名称|	参数说明|
---------|----------|
|id	     |线程id|
|[n:]	 |指定最忙的前N个线程并打印堆栈
|[b]	 |找出当前阻塞其他线程的线程
|[i `<value>`]|指定cpu占比统计的采样间隔，单位为毫秒
cpu占比是如何统计出来的？
>这里的cpu统计的是，一段采样间隔内，当前JVM里各个线程所占用的cpu时间占总cpu时间的百分比。其计算方法为： 首先进行一次采样，获得所有线程的cpu的使用时间(调用的是java.lang.management.ThreadMXBean#getThreadCpuTime这个接口)，然后睡眠一段时间，默认100ms，可以通过-i参数指定，然后再采样一次，最后得出这段时间内各个线程消耗的cpu时间情况，最后算出百分比。

<font color="red">注意</font>：这个统计也会产生一定的开销（<font color="red">JDK这个接口本身开销比较大</font>），因此会看到as的线程占用一定的百分比，为了降低统计自身的开销带来的影响，可以把采样间隔拉长一些，比如5000毫秒。

### 实例
1、支持一键展示当前最忙的前N个线程并打印堆栈
```shell
root@local.example.com@bistoury:\>thread -n 2
"as-command-execute-daemon" Id=166 cpuUsage=68% RUNNABLE
    at sun.management.ThreadImpl.dumpThreads0(Native Method)
    at sun.management.ThreadImpl.getThreadInfo(ThreadImpl.java:440)
    at com.taobao.arthas.core.command.monitor200.ThreadCommand.processTopBusyThreads(ThreadCommand.java:133)
    at com.taobao.arthas.core.command.monitor200.ThreadCommand.process(ThreadCommand.java:79)
    at com.taobao.arthas.core.shell.command.impl.AnnotatedCommandImpl.process(AnnotatedCommandImpl.java:82)
    at com.taobao.arthas.core.shell.command.impl.AnnotatedCommandImpl.access$100(AnnotatedCommandImpl.java:18)
    at com.taobao.arthas.core.shell.command.impl.AnnotatedCommandImpl$ProcessHandler.handle(AnnotatedCommandImpl.java:111)
    at com.taobao.arthas.core.shell.command.impl.AnnotatedCommandImpl$ProcessHandler.handle(AnnotatedCommandImpl.java:108)
    at com.taobao.arthas.core.shell.system.impl.ProcessImpl$CommandProcessTask.run(ProcessImpl.java:370)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
    at java.lang.Thread.run(Thread.java:744)

    Number of locked synchronizers = 1
    - java.util.concurrent.ThreadPoolExecutor$Worker@597d9745


"DubboResponseTimeoutScanTimer" Id=102 cpuUsage=6% TIMED_WAITING
    at java.lang.Thread.sleep(Native Method)
    at com.alibaba.dubbo.remoting.exchange.support.DefaultFuture$RemotingInvocationTimeoutScan.run(DefaultFuture.java:315)
    at java.lang.Thread.run(Thread.java:744)


Affect(row-cnt:0) cost in 166 ms.
```
2、当没有参数时，显示所有线程的信息。
```shell
root@local.example.com@bistoury:\>thread
thread
Threads Total: 125, NEW: 0, RUNNABLE: 63, BLOCKED: 0, WAITING: 39, TIMED_WAITING: 23, TERMINATED: 0                                                             
ID     NAME                GROUP        PRIORI STATE %CPU   TIME   INTER DAEMON
169    as-command-execute- system       10     RUNNA 52     0:0    false true   
102    DubboResponseTimeou main         5      TIMED 6      0:42   false true   
162    nioEventLoopGroup-2 system       10     RUNNA 5      0:0    false false  
38     Hashed wheel timer  main         5      TIMED 4      0:14   false false  
48     New I/O boss #21    main         5      RUNNA 3      0:2    false false  
24     Hashed wheel timer  main         5      TIMED 2      0:14   false false  
136    Hashed wheel timer  main         5      TIMED 2      0:14   false false  
167    nioEventLoopGroup-2 system       10     RUNNA 2      0:0    false false  
17     qconfig-loader boss main         5      RUNNA 2      0:2    false true   
33     Hashed wheel timer  main         5      TIMED 1      0:14   false false  
49     Hashed wheel timer  main         5      TIMED 1      0:14   false false  
127    Hashed wheel timer  main         5      TIMED 1      0:14   false false  
137    New I/O boss #39    main         5      RUNNA 1      0:2    false true   
39     New I/O worker #13  main         5      RUNNA 1      0:2    false true   
41     New I/O worker #15  main         5      RUNNA 1      0:2    false true   
```
3、thread id， 显示指定线程的运行堆栈
```shell
root@local.example.com@bistoury:\>thread 170
"as-command-execute-daemon" Id=170 RUNNABLE
    at sun.management.ThreadImpl.dumpThreads0(Native Method)
    at sun.management.ThreadImpl.getThreadInfo(ThreadImpl.java:440)
    at com.taobao.arthas.core.command.monitor200.ThreadCommand.processThread(ThreadCommand.java:146)
    at com.taobao.arthas.core.command.monitor200.ThreadCommand.process(ThreadCommand.java:77)
    at com.taobao.arthas.core.shell.command.impl.AnnotatedCommandImpl.process(AnnotatedCommandImpl.java:82)
    at com.taobao.arthas.core.shell.command.impl.AnnotatedCommandImpl.access$100(AnnotatedCommandImpl.java:18)
    at com.taobao.arthas.core.shell.command.impl.AnnotatedCommandImpl$ProcessHandler.handle(AnnotatedCommandImpl.java:111)
    at com.taobao.arthas.core.shell.command.impl.AnnotatedCommandImpl$ProcessHandler.handle(AnnotatedCommandImpl.java:108)
    at com.taobao.arthas.core.shell.system.impl.ProcessImpl$CommandProcessTask.run(ProcessImpl.java:370)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
    at java.lang.Thread.run(Thread.java:744)

    Number of locked synchronizers = 1
    - java.util.concurrent.ThreadPoolExecutor$Worker@2ac0801d

Affect(row-cnt:0) cost in 60 ms.
```
4、thread -b, 找出当前阻塞其他线程的线程

有时候我们发现应用卡住了， 通常是由于某个线程拿住了某个锁， 并且其他线程都在等待这把锁造成的。 为了排查这类问题， Bistoury提供了thread -b， 一键找出那个罪魁祸首。

```shell
root@local.example.com@bistoury:\>thread -b
"http-bio-8080-exec-4" Id=27 TIMED_WAITING
    at java.lang.Thread.sleep(Native Method)
    at test.arthas.TestThreadBlocking.doGet(TestThreadBlocking.java:22)
    -  locked java.lang.Object@725be470 <---- but blocks 4 other threads!
    at javax.servlet.http.HttpServlet.service(HttpServlet.java:624)
    at javax.servlet.http.HttpServlet.service(HttpServlet.java:731)
    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:303)
    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:208)
    at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:52)
    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:241)
    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:208)
    at test.filter.TestDurexFilter.doFilter(TestDurexFilter.java:46)
    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:241)
    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:208)
    at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:220)
    at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:122)
    at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:505)
    at com.taobao.tomcat.valves.ContextLoadFilterValve$FilterChainAdapter.doFilter(ContextLoadFilterValve.java:191)
    at com.taobao.eagleeye.EagleEyeFilter.doFilter(EagleEyeFilter.java:81)
    at com.taobao.tomcat.valves.ContextLoadFilterValve.invoke(ContextLoadFilterValve.java:150)
    at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:170)
    at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:103)
    at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:116)
    at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:429)
    at org.apache.coyote.http11.AbstractHttp11Processor.process(AbstractHttp11Processor.java:1085)
    at org.apache.coyote.AbstractProtocol$AbstractConnectionHandler.process(AbstractProtocol.java:625)
    at org.apache.tomcat.util.net.JIoEndpoint$SocketProcessor.run(JIoEndpoint.java:318)
    -  locked org.apache.tomcat.util.net.SocketWrapper@7127ee12
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
    at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
    at java.lang.Thread.run(Thread.java:745)

    Number of locked synchronizers = 1
    - java.util.concurrent.ThreadPoolExecutor$Worker@31a6493e
```
> <font color="red">注意</font>: 目前只支持找出synchronized关键字阻塞住的线程， 如果是java.util.concurrent.Lock， 目前还不支持。

5、thread -i, 指定采样时间间隔
```shell
root@local.example.com@bistoury:\>thread -n 2 -i 1000
"DubboResponseTimeoutScanTimer" Id=102 cpuUsage=13% TIMED_WAITING

    at java.lang.Thread.sleep(Native Method)
    at com.alibaba.dubbo.remoting.exchange.support.DefaultFuture$RemotingInvocationTimeoutScan.run(DefaultFuture.java:315)
    at java.lang.Thread.run(Thread.java:744)


"as-command-execute-daemon" Id=172 cpuUsage=10% RUNNABLE
    at sun.management.ThreadImpl.dumpThreads0(Native Method)
    at sun.management.ThreadImpl.getThreadInfo(ThreadImpl.java:440)
    at com.taobao.arthas.core.command.monitor200.ThreadCommand.processTopBusyThreads(ThreadCommand.java:133)
    at com.taobao.arthas.core.command.monitor200.ThreadCommand.process(ThreadCommand.java:79)
    at com.taobao.arthas.core.shell.command.impl.AnnotatedCommandImpl.process(AnnotatedCommandImpl.java:82)
    at com.taobao.arthas.core.shell.command.impl.AnnotatedCommandImpl.access$100(AnnotatedCommandImpl.java:18)
    at com.taobao.arthas.core.shell.command.impl.AnnotatedCommandImpl$ProcessHandler.handle(AnnotatedCommandImpl.java:111)
    at com.taobao.arthas.core.shell.command.impl.AnnotatedCommandImpl$ProcessHandler.handle(AnnotatedCommandImpl.java:108)
    at com.taobao.arthas.core.shell.system.impl.ProcessImpl$CommandProcessTask.run(ProcessImpl.java:370)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
    at java.lang.Thread.run(Thread.java:744)

    Number of locked synchronizers = 1
    - java.util.concurrent.ThreadPoolExecutor$Worker@65b3f958


Affect(row-cnt:0) cost in 1074 ms.
```

## jvm
>查看当前JVM信息
### 实例
```shell
root@local.example.com@bistoury:\>jvm
 RUNTIME                                                                                                                                              
------------------------------------------------------------------------------------------------------------------------------------------------------
 MACHINE-NAME                               16770@local                                                                           
 JVM-START-TIME                             2019-03-26 13:04:45                                                                                       
 MANAGEMENT-SPEC-VERSION                    1.2                                                                                                       
 SPEC-NAME                                  Java Virtual Machine Specification                                                                        
 SPEC-VENDOR                                Oracle Corporation                                                                                        
 SPEC-VERSION                               1.8                                                                                                       
 VM-NAME                                    Java HotSpot(TM) 64-Bit Server VM                                                                         
 VM-VENDOR                                  Oracle Corporation                                                                                        
 VM-VERSION                                 25.171-b11                                                                                                
 INPUT-ARGUMENTS                            -Djava.util.logging.config.file=/home/user/tomcat/www/bistoury/conf/logging.properties                 
                                            -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager                                         
                                            -Xms2048m                                                                                                 
                                            -Xmx2048m                                                                                                 
                                            -XX:NewSize=256m                                                                                          
                                            -XX:PermSize=256m                                                                                         
                                            -XX:+DisableExplicitGC                                                                                    
                                            -verbose:gc                                                                                               
                                            -XX:+PrintGCDateStamps                                                                                    
                                            -XX:+PrintGCDetails                                                                                       
                                            -Xloggc:/home/user/tomcat/www/bistoury/logs/gc.log                                                     
                                            -Djava.endorsed.dirs=/home/user/tomcat/tomcat/endorsed                                                              
                                            -Dcatalina.base=/home/user/tomcat/www/bistoury                                                         
                                            -Dcatalina.home=/home/user/tomcat/tomcat                                                                            
                                            -Djava.io.tmpdir=/home/user/tomcat/www/bistoury/temp                                                   
                                                                                                                                                      
 CLASS-PATH                                 /home/user/tomcat/tomcat/bin/bootstrap.jar:/home/user/tomcat/tomcat/bin/tomcat-juli.jar                                       
 BOOT-CLASS-PATH                            /home/user/tomcat/java/jdk1.8.0_171/jre/lib/resources.jar:/home/user/tomcat/java/jdk1.8.0_171/jre/lib/rt.jar:/home/user/tomcat/java/jdk 
                                            1.8.0_171/jre/lib/sunrsasign.jar:/home/user/tomcat/java/jdk1.8.0_171/jre/lib/jsse.jar:/home/user/tomcat/java/jdk1.8.0_171/jre 
                                            /lib/jce.jar:/home/user/tomcat/java/jdk1.8.0_171/jre/lib/charsets.jar:/home/user/tomcat/java/jdk1.8.0_171/jre/lib/jfr.jar:/ho 
                                            me/q/java/jdk1.8.0_171/jre/classes                                                                        
 LIBRARY-PATH                               /usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib                                              
                                                                                                                                                      
------------------------------------------------------------------------------------------------------------------------------------------------------
 CLASS-LOADING                                                                                                                                        
------------------------------------------------------------------------------------------------------------------------------------------------------
 LOADED-CLASS-COUNT                         12159                                                                                                     
 TOTAL-LOADED-CLASS-COUNT                   12261                                                                                                     
 UNLOADED-CLASS-COUNT                       102                                                                                                       
 IS-VERBOSE                                 false                                                                                                     
                                                                                                                                                      
------------------------------------------------------------------------------------------------------------------------------------------------------
 COMPILATION                                                                                                                                          
------------------------------------------------------------------------------------------------------------------------------------------------------
 NAME                                       HotSpot 64-Bit Tiered Compilers                                                                           
 TOTAL-COMPILE-TIME                         127648(ms)                                                                                                
                                                                                                                                                      
------------------------------------------------------------------------------------------------------------------------------------------------------
 GARBAGE-COLLECTORS                                                                                                                                   
------------------------------------------------------------------------------------------------------------------------------------------------------
 PS Scavenge                                358/5908(ms)                                                                                              
 [count/time]                                                                                                                                         
 PS MarkSweep                               11/1375(ms)                                                                                               
 [count/time]                                                                                                                                         
                                                                                                                                                      
------------------------------------------------------------------------------------------------------------------------------------------------------
 MEMORY-MANAGERS                                                                                                                                      
------------------------------------------------------------------------------------------------------------------------------------------------------
 CodeCacheManager                           Code Cache                                                                                                
                                                                                                                                                      
 Metaspace Manager                          Metaspace                                                                                                 
                                            Compressed Class Space                                                                                    
                                                                                                                                                      
 PS Scavenge                                PS Eden Space                                                                                             
                                            PS Survivor Space                                                                                         
                                                                                                                                                      
 PS MarkSweep                               PS Eden Space                                                                                             
                                            PS Survivor Space                                                                                         
                                            PS Old Gen                                                                                                
                                                                                                                                                      
                                                                                                                                                      
------------------------------------------------------------------------------------------------------------------------------------------------------
 MEMORY                                                                                                                                               
------------------------------------------------------------------------------------------------------------------------------------------------------
 HEAP-MEMORY-USAGE                          2098200576/2147483648/2098200576/445919704                                                                
 [committed/init/max/used]                                                                                                                            
 NO-HEAP-MEMORY-USAGE                       120127488/2555904/-1/107844936                                                                            
 [committed/init/max/used]                                                                                                                            
 PENDING-FINALIZE-COUNT                     0                                                                                                         
                                                                                                                                                      
------------------------------------------------------------------------------------------------------------------------------------------------------
 OPERATING-SYSTEM                                                                                                                                     
------------------------------------------------------------------------------------------------------------------------------------------------------
 OS                                         Linux                                                                                                     
 ARCH                                       amd64                                                                                                     
 PROCESSORS-COUNT                           4                                                                                                         
 LOAD-AVERAGE                               0.0                                                                                                       
 VERSION                                    1111                                                                                
                                                                                                                                                      
------------------------------------------------------------------------------------------------------------------------------------------------------
 THREAD                                                                                                                                               
------------------------------------------------------------------------------------------------------------------------------------------------------
 COUNT                                      270                                                                                                       
 DAEMON-COUNT                               258                                                                                                       
 LIVE-COUNT                                 275                                                                                                       
 STARTED-COUNT                              2218                                                                                                      
Affect(row-cnt:0) cost in 12 ms.
$
```
THREAD相关

- COUNT: JVM当前活跃的线程数
- DAEMON-COUNT: JVM当前活跃的守护线程数
- PEAK-COUNT: 从JVM启动开始曾经活着的最大线程数
- STARTED-COUNT: 从JVM启动开始总共启动过的线程次数
- DEADLOCK-COUNT: JVM当前死锁的线程数

文件描述符相关

- MAX-FILE-DESCRIPTOR-COUNT：JVM进程最大可以打开的文件描述符数
- OPEN-FILE-DESCRIPTOR-COUNT：JVM当前打开的文件描述符数

## mbean
>查看 Mbean 的信息，这个命令可以便捷的查看或监控 Mbean 的属性信息。
### 参数说明
|参数名称|参数说明|
|-------|-------|
|name-pattern|名称表达式匹配|
|attribute-pattern|属性名表达式匹配|
|[m]|查看元信息|
|[i:]|刷新属性值的时间间隔 (ms)|
|[n:]|刷新属性值的次数|
|[E]|开启正则表达式匹配，默认为通配符匹配。仅对属性名有效|

### 实例
+ 列出所有 Mbean 的名称：
```shell
root@local.example.com@qconfig:\>mbean
Catalina:type=Service
Catalina:type=StringCache
Catalina:type=Valve,host=localhost,context=/,name=NonLoginAuthenticator
Catalina:type=ThreadPool,name="ajp-nio-8009"
Catalina:j2eeType=Filter,WebModule=//localhost/manager,name=SetCharacterEncoding,J2EEApplication=none,J2EEServer=none
JMImplementation:type=MBeanServerDelegate
java.lang:type=Runtime
java.lang:type=Threading
java.lang:type=MemoryPool,name=Code Cache
Catalina:j2eeType=Filter,WebModule=//localhost/,name=Tomcat WebSocket (JSR356) Filter,J2EEApplication=none,J2EEServer=none
java.nio:type=BufferPool,name=direct
Catalina:j2eeType=Filter,WebModule=//localhost/,name=watcher,J2EEApplication=none,J2EEServer=none
Catalina:type=NamingResources
Catalina:j2eeType=Filter,WebModule=//localhost/,name=htmlLoginValidateFilter,J2EEApplication=none,J2EEServer=none
java.lang:type=MemoryManager,name=CodeCacheManager
Catalina:type=Valve,host=localhost,name=ErrorReportValve
java.lang:type=ClassLoading
Catalina:type=ParallelWebappClassLoader,host=localhost,context=/
java.lang:type=GarbageCollector,name=PS MarkSweep
Catalina:j2eeType=Filter,WebModule=//localhost/manager,name=CSRF,J2EEApplication=none,J2EEServer=none
java.lang:type=MemoryPool,name=Metaspace
Catalina:type=JspMonitor,WebModule=//localhost/manager,name=jsp,J2EEApplication=none,J2EEServer=none
java.lang:type=MemoryPool,name=PS Old Gen
Catalina:type=Valve,name=StandardEngineValve
Catalina:j2eeType=WebModule,name=//localhost/,J2EEApplication=none,J2EEServer=none
java.lang:type=GarbageCollector,name=PS Scavenge
Users:type=UserDatabase,database=UserDatabase
Catalina:type=ProtocolHandler,port=8009
java.lang:type=MemoryPool,name=PS Eden Space
Catalina:type=RequestProcessor,worker="http-nio-8181",name=HttpRequest3
Catalina:type=Realm,realmPath=/realm0
Catalina:type=ThreadPool,name="http-nio-8181"
Catalina:type=GlobalRequestProcessor,name="http-nio-8181"
Catalina:type=Deployer,host=localhost
Catalina:j2eeType=Filter,WebModule=//localhost/,name=encodingFilter,J2EEApplication=none,J2EEServer=none
Catalina:type=Manager,host=localhost,context=/manager
java.nio:type=BufferPool,name=mapped
Catalina:j2eeType=Servlet,WebModule=//localhost/,name=springmvc,J2EEApplication=none,J2EEServer=none
java.lang:type=MemoryPool,name=PS Survivor Space
com.sun.management:type=DiagnosticCommand
Catalina:j2eeType=Servlet,WebModule=//localhost/,name=jsp,J2EEApplication=none,J2EEServer=none
Catalina:type=Server
Catalina:type=WebResourceRoot,host=localhost,context=/
Catalina:type=Valve,host=localhost,context=/manager,name=StandardContextValve
Catalina:j2eeType=Servlet,WebModule=//localhost/manager,name=default,J2EEApplication=none,J2EEServer=none
com.sun.management:type=HotSpotDiagnostic
Catalina:type=RequestProcessor,worker="http-nio-8181",name=HttpRequest4
Catalina:type=Valve,host=localhost,context=/,name=StandardContextValve
Catalina:j2eeType=Filter,WebModule=//localhost/,name=userContextFilter,J2EEApplication=none,J2EEServer=none
Catalina:type=NamingResources,host=localhost,context=/
Catalina:j2eeType=Filter,WebModule=//localhost/,name=jsonLoginValidateFilter,J2EEApplication=none,J2EEServer=none
Catalina:j2eeType=Servlet,WebModule=//localhost/manager,name=Status,J2EEApplication=none,J2EEServer=none
Catalina:type=RequestProcessor,worker="http-nio-8181",name=HttpRequest2
Catalina:type=Mapper
Catalina:j2eeType=Filter,WebModule=//localhost/manager,name=Tomcat WebSocket (JSR356) Filter,J2EEApplication=none,J2EEServer=none
Catalina:type=Valve,host=localhost,name=StandardHostValve
Catalina:j2eeType=Servlet,WebModule=//localhost/,name=default,J2EEApplication=none,J2EEServer=none
java.lang:type=OperatingSystem
Catalina:type=Realm,realmPath=/realm0/realm0
java.lang:type=Compilation
Catalina:type=Resource,resourcetype=Global,class=org.apache.catalina.UserDatabase,name="UserDatabase"
Catalina:type=Host,host=localhost
Catalina:type=GlobalRequestProcessor,name="ajp-nio-8009"
Catalina:j2eeType=Servlet,WebModule=//localhost/manager,name=JMXProxy,J2EEApplication=none,J2EEServer=none
Catalina:j2eeType=Servlet,WebModule=//localhost/manager,name=HTMLManager,J2EEApplication=none,J2EEServer=none
Catalina:type=WebResourceRoot,host=localhost,context=/,name=Cache
Catalina:type=Engine
java.util.logging:type=Logging
Catalina:type=ProtocolHandler,port=8181
Catalina:type=RequestProcessor,worker="http-nio-8181",name=HttpRequest5
java.lang:type=MemoryManager,name=Metaspace Manager
Catalina:type=MBeanFactory
Catalina:type=Valve,host=localhost,context=/manager,name=RemoteAddrValve
Catalina:type=Manager,host=localhost,context=/
Catalina:type=Valve,host=localhost,context=/manager,name=BasicAuthenticator
Catalina:type=RequestProcessor,worker="http-nio-8181",name=HttpRequest6
Catalina:j2eeType=WebModule,name=//localhost/manager,J2EEApplication=none,J2EEServer=none
Catalina:type=Connector,port=8009
Catalina:type=Valve,host=localhost,name=AccessLogValve
Catalina:type=Loader,host=localhost,context=/
java.lang:type=MemoryPool,name=Compressed Class Space
Catalina:j2eeType=Servlet,WebModule=//localhost/manager,name=jsp,J2EEApplication=none,J2EEServer=none
java.lang:type=Memory
Catalina:type=NamingResources,host=localhost,context=/manager
Catalina:type=WebResourceRoot,host=localhost,context=/manager,name=Cache
Catalina:type=RequestProcessor,worker="http-nio-8181",name=HttpRequest1
Catalina:type=ParallelWebappClassLoader,host=localhost,context=/manager
Catalina:j2eeType=Servlet,WebModule=//localhost/manager,name=Manager,J2EEApplication=none,J2EEServer=none
Catalina:type=WebResourceRoot,host=localhost,context=/manager
Catalina:type=Loader,host=localhost,context=/manager
Catalina:type=JspMonitor,WebModule=//localhost/,name=jsp,J2EEApplication=none,J2EEServer=none
Catalina:type=Connector,port=8181
```
+ 查看 Mbean 的元信息：
```shell
root@local.example.com@bistoury:\>mbean -m java.lang:type=Threading
 NAME                   VALUE                                                                                                                         
------------------------------------------------------------------------------------------------------------------------------------------------------
 MBeanInfo                                                                                                                                            
 Info:                                                                                                                                                
 ObjectName             java.lang:type=Threading                                                                                                      
 ClassName              sun.management.ThreadImpl                                                                                                     
 Description            Information on the management interface of the MBean                                                                          
 Info Descriptor:                                                                                                                                     
 immutableInfo          true                                                                                                                          
 interfaceClassName     com.sun.management.ThreadMXBean                                                                                               
 mxbean                 true                                                                                                                          
 MBeanAttributeInfo                                                                                                                                   
 Attribute:                                                                                                                                           
 Name                   ThreadAllocatedMemoryEnabled                                                                                                  
 Description            ThreadAllocatedMemoryEnabled                                                                                                  
 Readable               true                                                                                                                          
 Writable               true                                                                                                                          
 Is                     true                                                                                                                          
 Type                   boolean                                                                                                                       
 Attribute Descriptor:                                                                                                                                
 openType               javax.management.openmbean.SimpleType(name=java.lang.Boolean)                                                                 
 originalType           boolean                                                                                                                       
 MBeanAttributeInfo                                                                                                                                   
 Attribute:                                                                                                                                           
 Name                   ThreadAllocatedMemorySupported                                                                                                
 Description            ThreadAllocatedMemorySupported                                                                                                
 Readable               true                                                                                                                          
 Writable               false                                                                                                                         
 Is                     true                                                                                                                          
 Type                   boolean                                                                                                                       
 Attribute Descriptor:                                                                                                                                
 openType               javax.management.openmbean.SimpleType(name=java.lang.Boolean)                                                                 
 originalType           boolean                                                                                                                       
```
+ 查看mbean属性信息：
```shell
 mbean java.lang:type=Threading 
```
+ mbean的name支持通配符匹配：
```shell
mbean java.lang:type=Th*
```
>注意：ObjectName 的匹配规则与正常的通配符存在差异，详细参见：[javax.management.ObjectName](https://docs.oracle.com/javase/8/docs/api/javax/management/ObjectName.html?is-external=true)
+ 通配符匹配特定的属性字段：
```shell
mbean java.lang:type=Threading *Count
```
+ 使用-E命令切换为正则匹配
```shell
mbean -E java.lang:type=Threading PeakThreadCount|ThreadCount|DaemonThreadCount
```
+ 使用-i命令实时监控：
```shell
root@local.example.com@bistoury:\>mbean -i 1000 java.lang:type=Threading *Count
 NAME                     VALUE                                                                                                                       
--------------------------------                                                                                                                      
 ThreadCount              97                                                                                                                          
 TotalStartedThreadCount  116                                                                                                                         
 DaemonThreadCount        84                                                                                                                          
 PeakThreadCount          97                                                                                                                          

 NAME                     VALUE                                                                                                                       
--------------------------------                                                                                                                      
 ThreadCount              97                                                                                                                          
 TotalStartedThreadCount  116                                                                                                                         
 DaemonThreadCount        84                                                                                                                          
 PeakThreadCount          97                                                                                                                          

 NAME                     VALUE                                                                                                                       
--------------------------------                                                                                                                      
 ThreadCount              97                                                                                                                          
 TotalStartedThreadCount  116                                                                                                                         
 DaemonThreadCount        84                                                                                                                          
 PeakThreadCount          97                       
```
## sysprop
>查看和修改当前JVM的系统属性(`System Property`)

### 实例
查看所有属性
```shell
root@local.example.com@bistoury:\>sysprop
 KEY                           VALUE                                                                                                                  
------------------------------------------------------------------------------------------------------------------------------------------------------
 java.vendor                   Oracle Corporation                                                                                                     
 sun.java.launcher             SUN_STANDARD                                                                                                           
 catalina.base                 /home/user/tomcat/www/local.example.com                                                                                      
 sun.management.compiler       HotSpot 64-Bit Tiered Compilers                                                                                        
 sun.nio.ch.bugLevel                                                                                                                                  
 catalina.useNaming            true                                                                                                                   
 os.name                       Linux                                                                                                                  
 sun.boot.class.path           /home/user/tomcat/java/jdk1.8.0_171/jre/lib/resources.jar:/home/user/tomcat/java/jdk1.8.0_171/jre/lib/rt.jar:/home/user/tomcat/java/jdk1.8.0_171/jre 
                               /lib/sunrsasign.jar:/home/user/tomcat/java/jdk1.8.0_171/jre/lib/jsse.jar:/home/user/tomcat/java/jdk1.8.0_171/jre/lib/jce.jar:/home/user/tomcat/java/ 
                               jdk1.8.0_171/jre/lib/charsets.jar:/home/user/tomcat/java/jdk1.8.0_171/jre/lib/jfr.jar:/home/user/tomcat/java/jdk1.8.0_171/jre/classes      
 java.util.logging.config.fil  /home/user/tomcat/www/local.example.com/conf/logging.properties                                                              
 e                                                                                                                                                    
 qconfig.symbol.beta           beta                                                                                                                   
 java.vm.specification.vendor  Oracle Corporation                                                                                                     
 java.runtime.version          1.8.0_171-b11                                                                                                          
 user.name                     tomcat                                                                                                                 
 shared.loader                                                                                                                                        
 tomcat.util.buf.StringCache.  true                                                                                                                   
 byte.enabled                                                                                                                                         
 user.language                 en                                                                                                                     
 java.naming.factory.initial   org.apache.naming.java.javaURLContextFactory                                                          
 sun.boot.library.path         /home/user/tomcat/java/jdk1.8.0_171/jre/lib/amd64                                                                                
 java.version                  1.8.0_171                                                                                                              
 java.util.logging.manager     org.apache.juli.ClassLoaderLogManager                                                                                  
 user.timezone                 PRC                                                                                                                    
 sun.arch.data.model           64                                                                                                                     
 java.endorsed.dirs            /home/user/tomcat/tomcat/endorsed                                                                                                
 java.rmi.server.randomIDs     true                                                                                                                   
 sun.cpu.isalist                                                                                                                                      
 sun.jnu.encoding              UTF-8                                                                                                                  
 file.encoding.pkg             sun.io                                                                                                                 
 package.access                sun.,org.apache.catalina.,org.apache.coyote.,org.apache.tomcat.,org.apache.jasper.,sun.beans.                          
 file.separator                /                                                                                                                      
 java.specification.name       Java Platform API Specification                                                                                        
 java.class.version            52.0                                                                                                                   
 user.country                  US                                                                                                                     
 java.home                     /home/user/tomcat/java/jdk1.8.0_171/jre                                                                                          
 java.vm.info                  mixed mode                                                                                                             
 os.version                    2.6.32-358.23.2.el6.x86_64                 
 path.separator                :                                                                                                                      
 java.vm.version               25.171-b11                                                                                                             
 java.awt.printerjob           sun.print.PSPrinterJob                                                                                                 
 sun.io.unicode.encoding       UnicodeLittle                                                                                                          
 qconfig.symbol.resources      resources                                                                                                              
 awt.toolkit                   sun.awt.X11.XToolkit                                                                                                   
 package.definition            sun.,java.,org.apache.catalina.,org.apache.coyote.,org.apache.tomcat.,org.apache.jasper                                                     java.naming.factory.url.pkgs  org.apache.naming                                                                                                      
 java.specification.vendor     Oracle Corporation                                                                                                     
 java.library.path             /usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib                                                           
 java.vendor.url               http://java.oracle.com/                                                                                                
 qconfig.server.app            qconfig                                                                                                                
 java.vm.vendor                Oracle Corporation                                                                                                     
 common.loader                 ${catalina.base}/lib,${catalina.base}/lib/*.jar,${catalina.home}/lib,${catalina.home}/lib/*.jar                        
 java.runtime.name             Java(TM) SE Runtime Environment                                                                                                 
 sun.java.command              org.apache.catalina.startup.Bootstrap start                                                                            
 java.class.path               /home/user/tomcat/tomcat/bin/bootstrap.jar:/home/user/tomcat/tomcat/bin/tomcat-juli.jar                                                    
 java.vm.specification.name    Java Virtual Machine Specification                                                                                 
 java.vm.specification.versio  1.8                                                                                                                    
 n                                                                                                                                                    
 catalina.home                 /home/user/tomcat/tomcat                                                                                                         
 sun.cpu.endian                little                                                                                                                 
 sun.os.patch.level            unknown                                                                                                                
 java.io.tmpdir                /home/user/tomcat/www/local.example.com/temp                                                                                 
 java.vendor.url.bug           http://bugreport.sun.com/bugreport/                                                                                                
 java.awt.graphicsenv          sun.awt.X11GraphicsEnvironment                                                                                         
 os.arch                       amd64                                                                                                                  
 java.ext.dirs                 /home/user/tomcat/java/jdk1.8.0_171/jre/lib/ext:/usr/java/packages/lib/ext                                                       
 user.dir                      /tmp/hsperfdata_tomcat                                                                                                 
 qconfig.symbol.prod           prod                                                                                                                   
 line.separator                                                                                                                                       
                                                                                                                                                      
 java.vm.name                  Java HotSpot(TM) 64-Bit Server VM                                                            
 common.server.subEnv                                                                                                                                 
 file.encoding                 UTF-8                                                                                                                  
 qconfig.symbol.buildgroup     buildGroup                                                                                                             
 curator-dont-log-connection-  false                                                                                                                  
 problems                                                                                                                                             
 java.specification.version    1.8                                                                                                                    
 common.server.env             dev                                                                                                                    
$ 
```
查看单个属性
```shell
root@local.example.com@bistoury:\>sysprop java.version
java.version=1.7.0_45
```

修改单个属性
```shell
root@local.example.com@bistoury:\>sysprop user.country
user.country=CN
root@local.example.com@bistoury:\>sysprop user.country US
Successfully changed the system property.
user.country=US
root@local.example.com@bistoury:\>sysprop user.country
user.country=US
```
## sysenv
>查看当前JVM的环境属性(System Environment Variables)
### 使用参考
```shell
 USAGE:
   sysenv [-h] [env-name]

 SUMMARY:
   Display the system env.

 EXAMPLES:
   sysenv
   sysenv USER

 WIKI:
   https://alibaba.github.io/arthas/sysenv

 OPTIONS:
 -h, --help                                                 this help
 <env-name>                                                 env name
```
### 实例
查看所有环境变量
```shell
root@local.example.com@bistoury:\>sysenv
 KEY                           VALUE                                                                                                                  
------------------------------------------------------------------------------------------------------------------------------------------------------
 USERDOMAIN_ROAMINGPROFILE     LEIXIE                                                                                                                 
 PROCESSOR_LEVEL               6                                                                                                                      
 _RUNJAVA                      "C:\Program Files\Java\jdk1.8.0_201\bin\java.exe"                                                                      
 JSSE_OPTS                     "-Djdk.tls.ephemeralDHKeySize=2048"                                                                                    
 SESSIONNAME                   Console                                                                                                                
 ALLUSERSPROFILE               C:\ProgramData                                                                                                         
 JAVA_OPTS                     suspend=y,server=n -javaagent:C:\Users\root\.IntelliJId 
                               ea2018.3\system\captureAgent\debugger-agent.jar -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -ve 
                               rbose:gc -Xloggc:E:/logs/gc.log  -Dcom.sun.management.jmxremote= -Dcom.sun.management.jmxremote.port=1191 -Dcom.sun.ma 
                               nagement.jmxremote.ssl=false -Dcom.sun.management.jmxremote.password.file=C:\Users\root\.IntelliJIdea2018.3\system 
                               \tomcat\Unnamed_realtimelogviewer\jmxremote.password -Dcom.sun.management.jmxremote.access.file=C:\Users\root\.Int 
                               elliJIdea2018.3\system\tomcat\Unnamed_realtimelogviewer\jmxremote.access -Djava.rmi.server.hostname=127.0.0.1 "-Djdk.t 
                               ls.ephemeralDHKeySize=2048" -Djava.protocol.handler.pkgs=org.apache.catalina.webresources                              
 PROCESSOR_ARCHITECTURE        AMD64                                                                                                                  
 PSModulePath                  C:\Program Files\WindowsPowerShell\Modules;C:\Windows\system32\WindowsPowerShell\v1.0\Modules                          
 SystemDrive                   C:                                                                                                                     
 ENDORSED_PROP                 ignore.endorsed.dirs                                                                                                   
 JRE_HOME                      C:\Program Files\Java\jdk1.8.0_201                                                                                     
 =E:                           E:\software\tomcat\apache-tomcat-8.5.38\bin                                                                            
 MAVEN_HOME                    E:\software\maven\apache-maven-3.5.4                                                                                   
 USERNAME                      root                                                                                                               
 ProgramFiles(x86)             C:\Program Files (x86)                                                                                                 
 FPS_BROWSER_USER_PROFILE_STR  Default                                                                                                                
 ING                                                                                                                                                  
 PATHEXT                       .COM;.EXE;.BAT;.CMD;.VBS;.VBE;.JS;.JSE;.WSF;.WSH;.MSC;.PY;.PYW                                                         
 DriverData                    C:\Windows\System32\Drivers\DriverData                                                                                 
 OneDriveConsumer              C:\Users\root\OneDrive                                                                                             
 ProgramData                   C:\ProgramData                                                                                                         
 ProgramW6432                  C:\Program Files                                                                                                       
 HOMEPATH                      \Users\root                                                                                                        
 _RUNJDB                       "C:\Program Files\Java\jdk1.8.0_201\bin\jdb.exe"                                                                       
 PROCESSOR_IDENTIFIER          Intel64 Family 6 Model 142 Stepping 9, GenuineIntel                                                                    
 LOGGING_CONFIG                -Djava.util.logging.config.file="C:\Users\root\.IntelliJIdea2018.3\system\tomcat\Unnamed_realtimelogviewer\conf\lo 
                               gging.properties"                                                                                                      
 ProgramFiles                  C:\Program Files                                                                                                       
 PUBLIC                        C:\Users\Public                                                                                                        
 CURRENT_DIR                   E:\software\tomcat\apache-tomcat-8.5.38\bin                                                                            
 windir                        C:\Windows                                                                                                             
 =::                           ::\                                                                                                                    
 ZOOKEEPER_HOME                E:\software\zookeeper-3.4.13                                                                                           
 LOCALAPPDATA                  C:\Users\root\AppData\Local                                                                                        
 IntelliJ IDEA                 D:\Program Files\JetBrains\IntelliJ IDEA 2018.3.5\bin;                                                                 
 CATALINA_TMPDIR               E:\software\tomcat\apache-tomcat-8.5.38\temp                                                                           
 USERDOMAIN                    ROOT                                                                                                                 
 FPS_BROWSER_APP_PROFILE_STRI  Internet Explorer                                                                                                      
 NG                                                                                                                                                   
 LOGONSERVER                   \\ROOT                                                                                                               
 JAVA_HOME                     C:\Program Files\Java\jdk1.8.0_201                                                                                     
 PROMPT                        $P$G                                                                                                                   
 CATALINA_BASE                 C:\Users\root\.IntelliJIdea2018.3\system\tomcat\Unnamed_realtimelogviewer                                          
 OneDrive                      C:\Users\root\OneDrive                                                                                             
 APPDATA                       C:\Users\root\AppData\Roaming                                                                                      
 _EXECJAVA                     "C:\Program Files\Java\jdk1.8.0_201\bin\java.exe"                                                                      
 CommonProgramFiles            C:\Program Files\Common Files                                                                                          
 Path                          D:\Program Files\python37\Scripts\;D:\Program Files\python37\;D:\Program Files\NetSarang\;C:\Program Files (x86)\Commo 
                               n Files\Oracle\Java\javapath;C:\Windows\system32;C:\Windows;C:\Windows\System32\Wbem;C:\Windows\System32\WindowsPowerS 
                               hell\v1.0\;C:\Windows\System32\OpenSSH\;C:\Program Files\Java\jdk1.8.0_201\bin;C:\Program Files\Git\cmd;E:\software\ma 
                               ven\apache-maven-3.5.4\bin;E:\software\zookeeper-3.4.13\bin;;D:\Program Files\UltraEdit;C:\Program Files\IDM Computer  
                               Solutions\UltraCompare;C:\Program Files\nodejs\;C:\Users\root\AppData\Local\Microsoft\WindowsApps;;D:\Program File 
                               s\JetBrains\IntelliJ IDEA 2018.3.5\bin;;D:\Program Files\Microsoft VS Code\bin;C:\Users\root\AppData\Local\Program 
                               s\EmEditor;C:\Users\root\AppData\Roaming\npm                                                                       
 OS                            Windows_NT                                                                                                             
 COMPUTERNAME                  LEIXIE                                                                                                                 
 CATALINA_HOME                 E:\software\tomcat\apache-tomcat-8.5.38                                                                                
 MAINCLASS                     org.apache.catalina.startup.Bootstrap                                                                                  
 JDK_JAVA_OPTIONS               --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.rmi/sun.rmi.tr 
                               ansport=ALL-UNNAMED                                                                                                    
 LOGGING_MANAGER               -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager                                                      
 PROCESSOR_REVISION            8e09                                                                                                                   
 CLASSPATH                     E:\software\tomcat\apache-tomcat-8.5.38\bin\bootstrap.jar;E:\software\tomcat\apache-tomcat-8.5.38\bin\tomcat-juli.jar  
 CommonProgramW6432            C:\Program Files\Common Files                                                                                          
 ComSpec                       C:\Windows\system32\cmd.exe                                                                                            
 CLASS_PATH                    .;C:\Program Files\Java\jdk1.8.0_201\lib;                                                                              
 SystemRoot                    C:\Windows                                                                                                             
 TEMP                          C:\Users\root\AppData\Local\Temp                                                                                   
 ACTION                        start                                                                                                                  
 HOMEDRIVE                     C:                                                                                                                     
 USERPROFILE                   C:\Users\root                                                                                                      
 TMP                           C:\Users\root\AppData\Local\Temp                                                                                   
 CommonProgramFiles(x86)       C:\Program Files (x86)\Common Files                                                                                    
 NUMBER_OF_PROCESSORS          4                                                                                                                      
$ 
```
查看单个环境变量
```shell
root@local.example.com@bistoury:\>sysenv MAINCLASS
MAINCLASS=org.apache.catalina.startup.Bootstrap
$
```
## vmoption
> 查看，更新VM诊断相关的参数

### 实例
查看所有option
```shell
admin@local@bistoury_demo_app:\>vmoption
 KEY                                   VALUE                                ORIGIN                                WRITEABLE                           
------------------------------------------------------------------------------------------------------------------------------------------------------
 HeapDumpBeforeFullGC                  false                                DEFAULT                               true                                
 HeapDumpAfterFullGC                   false                                DEFAULT                               true                                
 HeapDumpOnOutOfMemoryError            false                                DEFAULT                               true                                
 HeapDumpPath                                                               DEFAULT                               true                                

 PrintClassHistogram                   false                                DEFAULT                               true                                
 MinHeapFreeRatio                      40                                   DEFAULT                               true                                
 MaxHeapFreeRatio                      70                                   DEFAULT                               true                                
 PrintConcurrentLocks                  false                                DEFAULT                               true                                
 CMSAbortablePrecleanWaitMillis        100                                  DEFAULT                               true                                
 CMSWaitDuration                       2000                                 DEFAULT                               true                                
 CMSTriggerInterval                    -1                                   DEFAULT                               true 
```
查看指定option
```shell
admin@local@bistoury_demo_app:\>vmoption PrintClassHistogram
 KEY                                   VALUE                                ORIGIN                                WRITEABLE                           
------------------------------------------------------------------------------------------------------------------------------------------------------
 PrintClassHistogram                   false                                DEFAULT                               true                                
```
更新指定option
```shell
admin@local@bistoury_demo_app:\>vmoption PrintClassHistogram true
Successfully updated the vm option.
PrintClassHistogram=true
```
## ognl
>执行ognl表达式
### 参数说明
|参数名称|参数说明|
|-------|--------|
|`express`|执行表达式|
|`[c:]`|执行表达式的 ClassLoader 的 hashcode，默认值是SystemClassLoader|
|`[x]`|结果对象的展开层次，默认值1|
### 使用参考
OGNL特殊用法请参考：[https://github.com/alibaba/arthas/issues/71](https://github.com/alibaba/arthas/issues/71)
OGNL表达式官方指南：[https://commons.apache.org/proper/commons-ognl/language-guide.html](https://commons.apache.org/proper/commons-ognl/language-guide.html)

### 实例
调用静态函数
```shell
root@local.example.com@bistoury:\>ognl '@java.lang.System@out.println("hello")'
null
$ 
```
获取静态类的静态字段
```shell
root@local.example.com@bistoury:\>ognl '@demo.MathGame@random'
@Random[
    serialVersionUID=@Long[3905348978240129619],
    seed=@AtomicLong[125451474443703],
    multiplier=@Long[25214903917],
    addend=@Long[11],
    mask=@Long[281474976710655],
    DOUBLE_UNIT=@Double[1.1102230246251565E-16],
    BadBound=@String[bound must be positive],
    BadRange=@String[bound must be greater than origin],
    BadSize=@String[size must be non-negative],
    seedUniquifier=@AtomicLong[-3282039941672302964],
    nextNextGaussian=@Double[0.0],
    haveNextNextGaussian=@Boolean[false],
    serialPersistentFields=@ObjectStreamField[][isEmpty=false;size=3],
    unsafe=@Unsafe[sun.misc.Unsafe@28ea5898],
    seedOffset=@Long[24],
]
```
执行多行表达式，赋值给临时变量，返回一个List：
```shell
root@local.example.com@bistoury:\>ognl '#value1=@System@getProperty("java.home"), #value2=@System@getProperty("java.runtime.name"), {#value1, #value2}'
ognl '#value1=@System@getProperty("java.home"), #value2=@System@getProperty("j 
ava.runtime.name"), {#value1, #value2}'
@ArrayList[
    @String[C:\Program Files\Java\jdk1.8.0_201\jre],
    @String[Java(TM) SE Runtime Environment],
]
$ 
```
## getstatic
>通过getstatic命令可以方便的查看类的静态属性。使用方法为getstatic class_name field_name
如果该静态属性是一个复杂对象，还可以支持在该属性上通过ognl表示进行遍历，过滤，访问对象的内部属性等操作。

例如，假设map是一个Map，Map的Key是一个Enum，我们想过滤出Map中Key为某个Enum的值，可以写如下命令
```shell
root@local.example.com@bistoury:\> getstatic qunar.tc.Test map 'entrySet().iterator.{? #this.key.name()=="STOP"}'
field: n
@ArrayList[
    @Node[STOP=bbb],
]
Affect(row-cnt:1) cost in 68 ms.

root@local.example.com@bistoury:\> getstatic qunar.tc.Test map 'entrySet().iterator.{? #this.key=="a"}'
field: m
@ArrayList[
    @Node[a=aaa],
]
```

## heapdump
> dump java heap, 类似jmap命令的heap dump功能。<font color=red>请摘掉流量使用</font>

### 实例
dump到指定文件
```shell
admin@local@bistoury_demo_app:\>heapdump /home/root/Desktop/bistoury-2.0.7/dump1.hprof
Dumping heap to /home/root/Desktop/bistoury-2.0.7/dump1.hprof...
Heap dump file created
```
只dump live 对象
```shell
admin@local@bistoury_demo_app:\>heapdump --live /home/root/Desktop/bistoury-2.0.7/dump2.hprof
Dumping heap to /home/root/Desktop/bistoury-2.0.7/dump2.hprof...
Heap dump file created
```
dump 到临时文件
```shell
admin@local@bistoury_demo_app:\>heapdump
Dumping heap to /var/folders/c5/yvdt09ls5xv2825vp_pqy4200000gn/T/heapdump2019-09-23-20-218597155241814313750.hprof...
Heap dump file created
```

## logger
>查看logger信息，更新logger level
### 实例
以下面的logbook.xml为例
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="APPLICATION" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>app.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>mylog-%d{yyyy-MM-dd}.%i.txt</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>60</maxHistory>
            <totalSizeCap>2GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%logger{35} - %msg%n</pattern>
        </encoder>
    </appender>
 
    <appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="APPLICATION" />
    </appender>
 
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%-4relative [%thread] %-5level %logger{35} - %msg %n
            </pattern>
            <charset>utf8</charset>
        </encoder>
    </appender>
 
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="ASYNC" />
    </root>
</configuration>
```
使用logger命令打印的结果是：
```shell
admin@local@bistoury_demo_app:\>logger
 name                                   ROOT
 class                                  ch.qos.logback.classic.Logger
 classLoader                            sun.misc.Launcher$AppClassLoader@2a139a55
 classLoaderHash                        2a139a55
 level                                  INFO
 effectiveLevel                         INFO
 additivity                             true
 codeSource                             file:/Users/hengyunabc/.m2/repository/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.jar
 appenders                              name            CONSOLE
                                        class           ch.qos.logback.core.ConsoleAppender
                                        classLoader     sun.misc.Launcher$AppClassLoader@2a139a55
                                        classLoaderHash 2a139a55
                                        target          System.out
                                        name            APPLICATION
                                        class           ch.qos.logback.core.rolling.RollingFileAppender
                                        classLoader     sun.misc.Launcher$AppClassLoader@2a139a55
                                        classLoaderHash 2a139a55
                                        file            app.log
                                        name            ASYNC
                                        class           ch.qos.logback.classic.AsyncAppender
                                        classLoader     sun.misc.Launcher$AppClassLoader@2a139a55
                                        classLoaderHash 2a139a55
                                        appenderRef     [APPLICATION]
```
从appenders的信息里，可以看到
- CONSOLE logger的target是System.out
- APPLICATION logger是RollingFileAppender，它的file是app.log
- ASYNC它的appenderRef是APPLICATION，即异步输出到文件里

查看指定名字的logger信息
```shell
admin@local@bistoury_demo_app:\>logger -n org.springframework.web
 name                                   org.springframework.web
 class                                  ch.qos.logback.classic.Logger
 classLoader                            sun.misc.Launcher$AppClassLoader@2a139a55
 classLoaderHash                        2a139a55
 level                                  null
 effectiveLevel                         INFO
 additivity                             true
 codeSource                             file:/Users/hengyunabc/.m2/repository/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.jar
```
查看指定classloader的logger信息
```shell
admin@local@bistoury_demo_app:\>logger -c 2a139a55
 name                                   ROOT
 class                                  ch.qos.logback.classic.Logger
 classLoader                            sun.misc.Launcher$AppClassLoader@2a139a55
 classLoaderHash                        2a139a55
 level                                  DEBUG
 effectiveLevel                         DEBUG
 additivity                             true
 codeSource                             file:/Users/hengyunabc/.m2/repository/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.jar
 appenders                              name            CONSOLE
                                        class           ch.qos.logback.core.ConsoleAppender
                                        classLoader     sun.misc.Launcher$AppClassLoader@2a139a55
                                        classLoaderHash 2a139a55
                                        target          System.out
                                        name            APPLICATION
                                        class           ch.qos.logback.core.rolling.RollingFileAppender
                                        classLoader     sun.misc.Launcher$AppClassLoader@2a139a55
                                        classLoaderHash 2a139a55
                                        file            app.log
                                        name            ASYNC
                                        class           ch.qos.logback.classic.AsyncAppender
                                        classLoader     sun.misc.Launcher$AppClassLoader@2a139a55
                                        classLoaderHash 2a139a55
                                        appenderRef     [APPLICATION]

```
更新logger level
```shell
admin@local@bistoury_demo_app:\>logger --name ROOT --level debug
update logger level success.
```
## qjdump
>qjdump是线上JVM数据紧急收集脚本。它可以在<font color=red>紧急场景</font>下（比如马上要对进程进行重启），一键收集jstack、jmap以及GC日志等相关信息，并以zip包保存(默认在目录/tmp/bistoury/qjtools/qjdump/${PID}下)，保证在紧急情况下仍能收集足够的问题排查信息，减轻运维团队的工作量，以及与开发团队的沟通成本。

收集数据包括：
>- thread dump数据：jstack -l \$PID
>- jinfo -flags $PID
>- jmap histo 堆对象统计数据：jmap -histo \$PID & jmap -histo:live \$PID
>- GC日志(如果JVM有设定GC日志输出)
>- heap dump数据（需指定--liveheap开启）：jmap -dump:live,format=b,file=\${DUMP_FILE} \$PID

执行命令后，可以登录机器，去执行结果中提示的目录下载zip文件。
### 实例
```shell
# 对应用进行急诊
qjdump
# 额外收集heap dump信息（jmap -dump:live的信息）
qjdump --liveheap
```
## qjtop
 Bistoury 不再支持 qjtop 命令，可到主机信息页面查看 JVM 指标及繁忙线程
## qjmap
Bistoury 不再支持 qjmap 命令，请使用 [heapdump](#heapdump) 命令 dump 内存信息
## qjmxcli
Bistoury 不再支持 qjmxcli 命令，请使用 mbean 命令查看 [MBean](#mbean) 信息，使用 [jstat](#jstat) 命令查看 GC 信息
## sc
>查看JVM已加载的类信息
“Search-Class” 的简写，这个命令能搜索出所有已经加载到 JVM 中的 Class 信息，这个命令支持的参数有 [d]、[E]、[f] 和 [x:]。

### 参数说明
|参数名称      |	参数说明|
|--------------|-------------
|class-pattern | 类名表达式匹配
|method-pattern|方法名表达式匹配
|[d] 	       |输出当前类的详细信息，包括这个类所加载的原始文件来源、类的声明、加载的ClassLoader等详细信息。如果一个类被多个ClassLoader所加载，则会出现多次|
|[E]	       |开启正则表达式匹配，默认为通配符匹配|
|[f]	       |输出当前类的成员变量信息（需要配合参数-d一起使用）|
|[x:]	       |指定输出静态变量时属性的遍历深度，默认为 0，即直接使用 toString 输出|

>class-pattern支持全限定名，如com.qunar.test.AAA，也支持com/qunar/test/AAA这样的格式，这样，我们从异常堆栈里面把类名拷贝过来的时候，不需要在手动把/替换为.啦。
sc 默认开启了子类匹配功能，也就是说所有当前类的子类也会被搜索出来。
### 实例
查看类的静态变量信息， 可以用sc -df class-name
```shell
root@local.example.com@bistoury:\>sc -df org.apache.commons.lang.StringUtils
 class-info        org.apache.commons.lang.StringUtils                          
 code-source       /home/user/tomcat/www/qconfig-admin/webapps/ROOT/WEB-INF/lib/commons-l
                   ang-2.5.jar                                                  
 name              org.apache.commons.lang.StringUtils                          
 isInterface       false                                                        
 isAnnotation      false                                                        
 isEnum            false                                                        
 isAnonymousClass  false                                                        
 isArray           false                                                        
 isLocalClass      false                                                        
 isMemberClass     false                                                        
 isPrimitive       false                                                        
 isSynthetic       false                                                        
 simple-name       StringUtils                                                  
 modifier          public                                                       
 annotation                                                                     
 interfaces                                                                     
 super-class       +-java.lang.Object                                           
 class-loader      +-WebappClassLoader                                          
                       context:                                                 
                       delegate: false                                          
                       repositories:                                            
                         /WEB-INF/classes/                                      
                     ----------> Parent Classloader:                            
                     org.apache.catalina.loader.StandardClassLoader@4b7d94f8    

                     +-org.apache.catalina.loader.StandardClassLoader@4b7d94f8  
                       +-sun.misc.Launcher$AppClassLoader@23137792              
                         +-sun.misc.Launcher$ExtClassLoader@530f243b            
 classLoaderHash   711a924b                                                     
 fields            modifierfinal,public,static                                  
                   type    java.lang.String                                     
                   name    EMPTY                                                
                   value                                                        

                   modifierfinal,public,static                                  
                   type    int                                                  
                   name    INDEX_NOT_FOUND                                      
                   value   -1                                                   

                   modifierfinal,private,static                                 
                   type    int                                                  
                   name    PAD_LIMIT                                            
                   value   8192                                                 


Affect(row-cnt:1) cost in 56 ms.
```
## sm
>查看已加载类的方法信息
“Search-Method” 的简写，这个命令能搜索出所有已经加载了 Class 信息的方法信息。
sm 命令只能看到由当前类所声明 (declaring) 的方法，父类则无法看到。
### 参数说明
|参数名称	   |参数说明|
|--------------|--------|
|class-pattern |类名表达式匹配
|method-pattern|方法名表达式匹配
|[d]	       |展示每个方法的详细信息
|[E]	       |开启正则表达式匹配，默认为通配符匹配
### 实例
```shell
root@local.example.com@bistoury:\>sm org.apache.catalina.connector.Connector
org.apache.catalina.connector.Connector-><init>
org.apache.catalina.connector.Connector->setProperty
org.apache.catalina.connector.Connector->getProperty
org.apache.catalina.connector.Connector->toString
org.apache.catalina.connector.Connector->resume
org.apache.catalina.connector.Connector->getScheme
org.apache.catalina.connector.Connector->getProtocol
org.apache.catalina.connector.Connector->getPort
org.apache.catalina.connector.Connector->setService
org.apache.catalina.connector.Connector->setPort
org.apache.catalina.connector.Connector->getService
org.apache.catalina.connector.Connector->getAttribute
org.apache.catalina.connector.Connector->setAttribute
org.apache.catalina.connector.Connector->getLocalPort
org.apache.catalina.connector.Connector->pause
org.apache.catalina.connector.Connector->setProtocol
org.apache.catalina.connector.Connector->initInternal
org.apache.catalina.connector.Connector->setSecure
org.apache.catalina.connector.Connector->getSecure
org.apache.catalina.connector.Connector->startInternal
org.apache.catalina.connector.Connector->stopInternal
org.apache.catalina.connector.Connector->setScheme
org.apache.catalina.connector.Connector->createRequest
org.apache.catalina.connector.Connector->getDomainInternal
org.apache.catalina.connector.Connector->getProtocolHandler
org.apache.catalina.connector.Connector->setURIEncoding
org.apache.catalina.connector.Connector->findSslHostConfigs
org.apache.catalina.connector.Connector->destroyInternal
org.apache.catalina.connector.Connector->getObjectNameKeyProperties
org.apache.catalina.connector.Connector->getAllowTrace
org.apache.catalina.connector.Connector->setAllowTrace
org.apache.catalina.connector.Connector->getAsyncTimeout
org.apache.catalina.connector.Connector->setAsyncTimeout
org.apache.catalina.connector.Connector->getEnableLookups
org.apache.catalina.connector.Connector->setEnableLookups
org.apache.catalina.connector.Connector->getMaxCookieCount
...
```
```shell
root@local.example.com@bistoury:\>sm org.apache.catalina.connector.Connector -d
 declaring-class   org.apache.catalina.connector.Connector
 constructor-name  <init>
 modifier          public
 annotation
 parameters
 exceptions

 declaring-class   org.apache.catalina.connector.Connector
 constructor-name  <init>
 modifier          public
 annotation
 parameters        java.lang.String
 exceptions

 declaring-class  org.apache.catalina.connector.Connector
 method-name      setProperty
 modifier         public
 annotation
 parameters       java.lang.String
                  java.lang.String
 return           boolean
 exceptions
 ......
```
## dump
>dump 已加载类的 bytecode 到特定目录
### 参数说明
|参数名称|参数说明|
|--------|--------|
|class-pattern|	类名表达式匹配
|[c:]	|类所属 ClassLoader 的 hashcode
|[E]	|开启正则表达式匹配，默认为通配符匹配
```shell
root@local.example.com@bistoury:\>dump -E org.apache.commons.lang.StringUtils
 HASHCODE  CLASSLOADER                                                  LOCATIO
                                                                        N       
 711a924b  +-WebappClassLoader                                          /home/t
               context:                                                 omcat/l
               delegate: false                                          ogs/art
               repositories:                                            has/cla
                 /WEB-INF/classes/                                      ssdump/
             ----------> Parent Classloader:                            org.apa
             org.apache.catalina.loader.StandardClassLoader@4b7d94f8    che.cat
                                                                        alina.l
             +-org.apache.catalina.loader.StandardClassLoader@4b7d94f8  oader.W
               +-sun.misc.Launcher$AppClassLoader@23137792              ebappCl
                 +-sun.misc.Launcher$ExtClassLoader@530f243b            assLoad
                                                                        er-711a
                                                                        924b/or
                                                                        g/apach
                                                                        e/commo
                                                                        ns/lang
                                                                        /String
                                                                        Utils.c
                                                                        lass    
Affect(row-cnt:1) cost in 49 ms.
```
## redefine
>加载外部的.class文件，redefine jvm已加载的类

<font color="red">注意：</font>redefine后的原来的类不能恢复，redefine有可能失败（比如增加了新的field），参考jdk本身的文档。
### 参数说明
|参数名称	|参数说明|
|---|---|
|[c:]|	ClassLoader的hashcode
|[p:]|	外部的.class文件的完整路径，支持多个
### 实例
```shell
root@local.example.com@bistoury:\>redefine -p /Test.class
redefine -p /Test.class
redefine success, size: 0
```
## mc
>Memory Compiler/内存编译器，编译.java文件生成.class。
### 实例
编译类
```shell
mc /tmp/Test.java
```
可以通过`-c`参数指定classloader
```shell
mc -c 32ba647b /tmp/Test.java
```
可以通过-d命令指定输出目录：
```shell
mc -d /tmp/output /tmp/ClassA.java /tmp/ClassB.java
```
编译生成.class文件之后，可以结合[redefine](#redefine)命令实现热更新代码。
 
<font color=red>注意</font>：mc命令有可能失败。如果编译失败可以在本地编译好.class文件，再上传到服务器。具体参考[redefine](#redefine)命令说明。

## jad
>反编译指定已加载类的源码
jad 命令将 JVM 中实际运行的 class 的 byte code 反编译成 java 代码，便于你理解业务逻辑；
当然，反编译出来的 java 代码可能会存在语法错误，但不影响你进行阅读理解
### 参数说明
|参数名称|参数说明
|---|---|
|class-pattern|	类名表达式匹配
|[c:]	|类所属 ClassLoader 的 hashcode
|[E]	|开启正则表达式匹配，默认为通配符匹配
>当有多个 ClassLoader 都加载了这个类时，jad 命令会输出对应 ClassLoader 实例的 hashcode，然后你只需要重新执行 jad 命令，并使用参数 -c <hashcode> 就可以反编译指定 ClassLoader 加载的那个类了；
### 实例
```java
root@local.example.com@bistoury:\>jad org.apache.log4j.Logger

ClassLoader:                                                                    
+-WebappClassLoader                                                             
    context:                                                                    
    delegate: false                                                             
    repositories:                                                               
      /WEB-INF/classes/                                                         
  ----------> Parent Classloader:                                               
  org.apache.catalina.loader.StandardClassLoader@4b7d94f8                       

  +-org.apache.catalina.loader.StandardClassLoader@4b7d94f8                     
    +-sun.misc.Launcher$AppClassLoader@23137792                                 
      +-sun.misc.Launcher$ExtClassLoader@530f243b                               

Location:                                                                       
/home/user/tomcat/www/qconfig-admin/webapps/ROOT/WEB-INF/lib/log4j-over-slf4j-1.7.5.jar   

/*
 * Decompiled with CFR 0_132.
 */
package org.apache.log4j;

import org.apache.log4j.Category;
import org.apache.log4j.Log4jLoggerFactory;
import org.apache.log4j.spi.LoggerFactory;
import org.slf4j.Marker;

public class Logger
extends Category {
    private static final String LOGGER_FQCN = Logger.class.getName();

    public static Logger getRootLogger() {
        return Log4jLoggerFactory.getLogger((String)"ROOT");
    }

    public boolean isTraceEnabled() {
        return this.slf4jLogger.isTraceEnabled();
    }

    protected Logger(String name) {
        super(name);
    }

    public static Logger getLogger(Class clazz) {
        return Logger.getLogger(clazz.getName());
    }

    public static Logger getLogger([String name) {
        return Log4jLoggerFactory.getLogger(([String)name);
    }

    public static Logger getLogger(String name, LoggerFactory loggerFactory) [{
        return Log4jLoggerFactory.getLogger((String)name, (LoggerFactory)loggerFactory);
    }

    public void trace(Object message, Throwable t) {
        this.differentiatedLog(null, LOGGER_FQCN, 0, message, null);
    }

    public void trace(Object message) {
        this.differentiatedLog(null, LOGGER_FQCN, 0, message, null);
    }
}
```
## classloader
>查看classloader的继承树，urls，类加载信息
classloader 命令将 JVM 中所有的classloader的信息统计出来，并可以展示继承树，urls等。
可以让指定的classloader去getResources，打印出所有查找到的resources的url。对于ResourceNotFoundException比较有用。
### 参数说明
|参数名称|	参数说明|
|---|---|
|[l]|按类加载实例进行统计
|[t]|打印所有ClassLoader的继承树
|[a]|列出所有ClassLoader加载的类，请谨慎使用
|[c:]|ClassLoader的hashcode
|[c: r:]|用ClassLoader去查找resource
### 实例
按类加载类型查看统计信息
```shell
root@local.example.com@bistoury:\>classloader
 name                                            numberOfInstances  loadedCountTotal   
 org.apache.catalina.loader.WebappClassLoader    1                  6279        
 BootstrapClassLoader                            1                  2977        
 com.taobao.arthas.agent.ArthasClassloader       1                  1710        
 org.apache.catalina.loader.StandardClassLoader  1                  721         
 sun.reflect.DelegatingClassLoader               177                177         
 sun.misc.Launcher$AppClassLoader                1                  27          
 sun.misc.Launcher$ExtClassLoader                1                  5   
```
按类加载实例查看统计信息
```shell
root@local.example.com@bistoury:\>classloader -l
 name                                                     loadedCount  hash     
 BootstrapClassLoader                                     2977         null     
 com.taobao.arthas.agent.ArthasClassloader@3409d1b1       1713         3409d1b1
 org.apache.catalina.loader.StandardClassLoader@4b7d94f8  721          4b7d94f8
 WebappClassLoader                                        6279         77ab3f0  
   context:                                                                     
   delegate: false                                                              
   repositories:                                                                
     /WEB-INF/classes/                                                          
 ----------> Parent Classloader:                                                
 org.apache.catalina.loader.StandardClassLoader@4b7d94f8                        

 sun.misc.Launcher$AppClassLoader@23137792                27           23137792
 sun.misc.Launcher$ExtClassLoader@530f243b                5            530f243b
```
查看ClassLoader的继承树
```shell
root@local.example.com@bistoury:\>classloader -t
+-BootstrapClassLoader                                                          
+-sun.misc.Launcher$ExtClassLoader@530f243b                                     
  +-com.taobao.arthas.agent.ArthasClassloader@3409d1b1                          
  +-sun.misc.Launcher$AppClassLoader@23137792                                   
    +-org.apache.catalina.loader.StandardClassLoader@4b7d94f8                   
      +-WebappClassLoader                                                       
          context:                                                              
          delegate: false                                                       
          repositories:                                                         
            /WEB-INF/classes/                                                   
        ----------> Parent Classloader:                                         
        org.apache.catalina.loader.StandardClassLoader@4b7d94f8    
```
查看URLClassLoader实际的urls
```shell
root@local.example.com@bistoury:\>classloader -c 4b7d94f8
file:/home/user/tomcat/tomcat/lib/                                                        
file:/home/user/tomcat/tomcat/lib/ecj-4.2.2.jar                                           
file:/home/user/tomcat/tomcat/lib/servlet-api.jar                                         
file:/home/user/tomcat/tomcat/lib/websocket-api.jar                                       
file:/home/user/tomcat/tomcat/lib/tomcat-util.jar                                         
file:/home/user/tomcat/tomcat/lib/tomcat7-websocket.jar                                   
file:/home/user/tomcat/tomcat/lib/el-api.jar                                              
file:/home/user/tomcat/tomcat/lib/tomcat-jdbc.jar                                         
file:/home/user/tomcat/tomcat/lib/jsp-api.jar                                             
file:/home/user/tomcat/tomcat/lib/tomcat-api.jar                                          
file:/home/user/tomcat/tomcat/lib/catalina-tribes.jar                                     
file:/home/user/tomcat/tomcat/lib/annotations-api.jar                                     
file:/home/user/tomcat/tomcat/lib/catalina-ha.jar                                         
file:/home/user/tomcat/tomcat/lib/jasper.jar                                              
file:/home/user/tomcat/tomcat/lib/tomcat-i18n-es.jar                                      
file:/home/user/tomcat/tomcat/lib/catalina.jar                                            
file:/home/user/tomcat/tomcat/lib/tomcat-coyote.jar                                       
file:/home/user/tomcat/tomcat/lib/jasper-el.jar                                           
file:/home/user/tomcat/tomcat/lib/tomcat-i18n-ja.jar                                      
file:/home/user/tomcat/tomcat/lib/catalina-ant.jar                                        
file:/home/user/tomcat/tomcat/lib/tomcat-i18n-fr.jar                                      
file:/home/user/tomcat/tomcat/lib/tomcat-dbcp.jar     
```
使用ClassLoader去查找resource
```shell
root@local.example.com@bistoury:\> classloader -c 4b7d94f8 -r META-INF/MANIFEST.MF
 jar:file:/home/user/tomcat/tomcat/bin/bootstrap.jar!/META-INF/MANIFEST.MF                
 jar:file:/home/user/tomcat/tomcat/bin/commons-daemon.jar!/META-INF/MANIFEST.MF           
 jar:file:/home/user/tomcat/tomcat/bin/tomcat-juli.jar!/META-INF/MANIFEST.MF              
 jar:file:/home/user/tomcat/www/qconfig-admin/webapps/ROOT/WEB-INF/lib/qtracer-instrument
 -agent-1.4.2.jar!/META-INF/MANIFEST.MF                                         
 jar:file:/home/user/tomcat/qflume-ng/arthas/lib/arthas-agent.jar!/META-INF/MANIFEST.MF   
 jar:file:/home/user/tomcat/tomcat/lib/ecj-4.2.2.jar!/META-INF/MANIFEST.MF                
 jar:file:/home/user/tomcat/tomcat/lib/servlet-api.jar!/META-INF/MANIFEST.MF              
 jar:file:/home/user/tomcat/tomcat/lib/websocket-api.jar!/META-INF/MANIFEST.MF            
 jar:file:/home/user/tomcat/tomcat/lib/tomcat-util.jar!/META-INF/MANIFEST.MF              
 jar:file:/home/user/tomcat/tomcat/lib/tomcat7-websocket.jar!/META-INF/MANIFEST.MF        
 jar:file:/home/user/tomcat/tomcat/lib/el-api.jar!/META-INF/MANIFEST.MF                   
 jar:file:/home/user/tomcat/tomcat/lib/tomcat-jdbc.jar!/META-INF/MANIFEST.MF              
 jar:file:/home/user/tomcat/tomcat/lib/jsp-api.jar!/META-INF/MANIFEST.MF          
 ...
```
查找类的class文件：
```shell
root@local.example.com@bistoury:\>classloader -c 4b7d94f8  -r java/lang/String.class
 jar:file:/home/user/tomcat/java/jdk1.7.0_45/jre/lib/rt.jar!/java/lang/String.class
```
## monitor
>方法执行监控
对匹配 class-pattern／method-pattern的类、方法的调用进行监控。

>monitor 命令是一个非实时返回命令.
实时返回命令是输入之后立即返回，而非实时返回的命令，则是不断的等待目标 Java 进程返回信息，直到用户输入 Ctrl+C 为止。
服务端是以任务的形式在后台跑任务，植入的代码随着任务的中止而不会被执行，所以任务关闭后，不会对原有性能产生太大影响，而且原则上，该命令不会引起原有业务逻辑的改变。
### 监控维度说明
|监控项	    |说明
|-----------|------|
|timestamp	|时间戳
|class	    |Java类
|method	    |方法（构造方法、普通方法）
|total	    |调用次数
|success	|成功次数
|fail	    |失败次数
|rt	        |平均RT
|fail-rate  |失败率
### 参数说明
|参数名称	    |参数说明
|---------------|---------|
|class-pattern	|类名表达式匹配
|method-pattern	|方法名表达式匹配
|[E]	        |开启正则表达式匹配，默认为通配符匹配
|[c:]	        |统计周期，默认值为120秒
### 实例
```shell
root@local.example.com@bistoury:\>monitor -c 5 qunar.tc.Test query
monitor -c 5 qunar.tc.Test query
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 86 ms.
timestamp            class           method      total  success  fail  rt    fail-rate
-----------------------------------------------------------------------------------------------------------------------------------
 2018-11-07 10:56:40  qunar.tc.Test  query       10     10       0     2.00  0.00%

 timestamp            class          method      total  success  fail  rt    fail-rate
-----------------------------------------------------------------------------------------------------------------------------------
 2018-11-07 10:56:45  qunar.tc.Test  query       11     11       0     2.18  0.00%

 timestamp            class          method      total  success  fail  rt    fail-rate
-----------------------------------------------------------------------------------------------------------------------------------
 2018-11-07 10:56:50  qunar.tc.Test  query        0      0        0     0.00  0.00%                                                      
```

## watch
>方法执行数据观测
让你能方便的观察到指定方法的调用情况。能观察到的范围为：返回值、抛出异常、入参，通过编写 OGNL 表达式进行对应变量的查看。
###参数列表
|参数名称	       |参数说明|
|------------------|---------|
|class-pattern	   |类名表达式匹配
|method-pattern	   |方法名表达式匹配
|express	       |观察表达式
|condition-express |条件表达式
|[b]	           |在方法调用之前观察
|[e]	           |在方法异常之后观察
|[s]	           |在方法返回之后观察
|[f]	           |在方法结束之后(正常返回和异常返回)观察
|[E]	           |开启正则表达式匹配，默认为通配符匹配
|[x:]	           |指定输出结果的属性遍历深度，默认为 1，当没有输出时，可以适当增加遍历深度
|\#cost	           |方法执行耗时

这里重点要说明的是观察表达式，观察表达式的构成主要由 ognl表达式组成，所以你可以这样写`{params,returnObj}`， 只要是一个合法的ognl表达式，都能被正常支持。

观察的维度也比较多，主要体现在参数 advice 的数据结构上。Advice 参数最主要是封装了通知节点的所有信息。请参考[表达式核心变量](https://alibaba.github.io/arthas/advice-class.html)中关于该节点的描述。

特殊用法请参考：[https://github.com/alibaba/arthas/issues/71](https://github.com/alibaba/arthas/issues/71)
OGNL表达式官网：https://commons.apache.org/proper/commons-ognl/language-guide.html

>注意：很多时候我们只想看到某个方法的rt大于某个时间之后的watch结果，可以按照方法执行的耗时来进行过滤了，例如watch qunar.tc.Test query {params,returnObj} -x 10 #cost>100，#cost>100表示当执行时间超过100ms的时候，才会输出watch的结果。

### 实例
```shell
root@local.example.com@bistoury:\>watch qunar.tc.Test query {params,returnObj} -x 10
watch qunar.tc.Test query
s {params,returnObj} -x 10
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 83 ms.
ts=2018-11-07 17:43:56;result=@ArrayList[
    @Object[][
        @String[test],
    ],
    @EmptyList[isEmpty=true;size=0],
]
```
## trace
>方法内部调用路径，并输出方法路径上的每个节点上耗时
trace 命令能主动搜索 class-pattern／method-pattern 对应的方法调用路径，渲染和统计整个调用链路上的所有性能开销和追踪调用链路。

这里重点要说明的是观察表达式，观察表达式的构成主要由 ognl表达式组成，所以你可以这样写`{params,returnObj}`， 只要是一个合法的ognl表达式，都能被正常支持。

观察的维度也比较多，主要体现在参数 advice 的数据结构上。Advice 参数最主要是封装了通知节点的所有信息。请参考[表达式核心变量](https://alibaba.github.io/arthas/advice-class.html)中关于该节点的描述。

特殊用法请参考：[https://github.com/alibaba/arthas/issues/71](https://github.com/alibaba/arthas/issues/71)
OGNL表达式官网：[https://commons.apache.org/proper/commons-ognl/language-guide.html](https://commons.apache.org/proper/commons-ognl/language-guide.html)
><font color="red">注意：</font>trace 能方便的帮助你定位和发现因 RT高而导致的性能问题缺陷，但其每次只能跟踪一级方法的调用链路。

>注意：很多时候我们只想看到某个方法的rt大于某个时间之后的trace结果，可以按照方法执行的耗时来进行过滤了，例如trace *StringUtils isBlank #cost>100表示当执行时间超过100ms的时候，才会输出trace的结果。
### 参数说明
|参数名称	       |参数说明、
|------------------|--------|
|class-pattern     |类名表达式匹配
|method-pattern    |方法名表达式匹配
|condition-express |条件表达式
|[E]	           |开启正则表达式匹配，默认为通配符匹配
|[n:]	           |命令执行次数
|\#cost	           |方法执行耗时

### 实例
监测方法
```shell
root@local.example.com@bistoury:\>trace qunar.tc.Test query  params.length==1
trace qunar.tc.Test query
s  params.length==1
Press Ctrl+C to abort.

Affect(class-cnt:1 , method-cnt:1) cost in 140 ms.
`---ts=2018-11-07 19:39:13;thread_name=http-bio-8080-exec-6;id=c9;is_daemon=true;priority=5;TCCL=org.apache.catalina.loader.WebappClassLoader@2fe83622
    `---[15.44114ms] qunar.tc.Test:query()
        `---[0.026753ms] qunar.tc.Test:query()
```
按照耗时过滤：
```shell
root@local.example.com@bistoury:\>trace qunar.tc.Test query #cost>100
trace qunar.tc.Test query
s #cost<100
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 118 ms.
`---ts=2018-11-07 19:32:42;thread_name=http-bio-8080-exec-6;id=c9;is_daemon=true;priority=5;TCCL=org.apache.catalina.loader.WebappClassLoader@2fe83622
    `---[17.131568ms] qunar.tc.Test:query()
        `---[0.026861ms] qunar.tc.Test:query()
```
只会展示耗时大于100ms的调用路径，有助于在排查问题的时候，只关注异常情况
## stack
>输出当前方法被调用的调用路径
很多时候我们都知道一个方法被执行，但这个方法被执行的路径非常多，或者你根本就不知道这个方法是从那里被执行了，此时你需要的是 stack 命令。
### 参数说明
|参数名称	       |参数说明
|------------------|--------|
|class-pattern	   |类名表达式匹配
|method-pattern	   |方法名表达式匹配
|condition-express |条件表达式
|[E]	           |开启正则表达式匹配，默认为通配符匹配
|[n:]	           |执行次数限制
|\#cost	           |方法执行耗时

这里重点要说明的是观察表达式，观察表达式的构成主要由 ognl表达式组成，所以你可以这样写`{params,returnObj}`， 只要是一个合法的ognl表达式，都能被正常支持。

观察的维度也比较多，主要体现在参数 advice 的数据结构上。Advice 参数最主要是封装了通知节点的所有信息。请参考[表达式核心变量](https://alibaba.github.io/arthas/advice-class.html)中关于该节点的描述。

特殊用法请参考：[https://github.com/alibaba/arthas/issues/71](https://github.com/alibaba/arthas/issues/71)
OGNL表达式官网：[https://commons.apache.org/proper/commons-ognl/language-guide.html](https://commons.apache.org/proper/commons-ognl/language-guide.html)

>注意：很多时候我们只想看到某个方法的rt大于某个时间之后的stack结果，可以按照方法执行的耗时来进行过滤了，例如stack qunar.tc.Test query #cost>100表示当执行时间超过100ms的时候，才会输出stack的结果。
###实例
注意：如果表达式里面包含了引号，那么需要把整个表达式用引号括起来，如果表达式中没有包含引号，那么可以不用引号。当然，一个好的习惯是，不管表达式中有没有引号，都使用引号括起来。
```shell
root@local.example.com@bistoury:\>stack qunar.tc.Test query  params.length==1
stack qunar.tc.Test query
s  params.length==1
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 147 ms.
ts=2018-11-07 19:51:48;thread_name=http-bio-8080-exec-7;id=ca;is_daemon=true;priority=5;TCCL=org.apache.catalina.loader.WebappClassLoader@2fe83622
    @qunar.tc.Test.query()
        at sun.reflect.NativeMethodAccessorImpl.invoke0(NativeMethodAccessorImpl.java:-2)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at org.springframework.web.method.support.InvocableHandlerMethod.invoke(InvocableHandlerMethod.java:215)
        at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:132)
        at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:104)
        at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandleMethod(RequestMappingHandlerAdapter.java:745)
        at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:686)
        at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:80)
        at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:925)
        at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:856)
        at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:953)
        at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:844)
        at javax.servlet.http.HttpServlet.service(HttpServlet.java:621)
        at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:829)
        at javax.servlet.http.HttpServlet.service(HttpServlet.java:728)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:305)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:210)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:243)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:210)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:243)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:210)
        at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:243)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:210)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:243)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:210)
        at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:88)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:243)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:210)
        at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:222)
        at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:123)
        at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:502)
        at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:171)
        at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:100)
        at org.apache.catalina.valves.AccessLogValve.invoke(AccessLogValve.java:953)
        at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:118)
        at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:408)
        at org.apache.coyote.http11.AbstractHttp11Processor.process(AbstractHttp11Processor.java:1041)
        at org.apache.coyote.AbstractProtocol$AbstractConnectionHandler.process(AbstractProtocol.java:603)
        at org.apache.tomcat.util.net.JIoEndpoint$SocketProcessor.run(JIoEndpoint.java:310)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
        at java.lang.Thread.run(Thread.java:745)
```
按照耗时查询:
```shell
root@local.example.com@bistoury:\>stack qunar.Test query  #cost>10
stack qunar.tc.Test query
s  #cost>10
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 130 ms.
ts=2018-11-07 19:57:15;thread_name=http-bio-8080-exec-8;id=cb;is_daemon=true;priority=5;TCCL=org.apache.catalina.loader.WebappClassLoader@2fe83622
    @qunar.tc.Test.query()
        at sun.reflect.NativeMethodAccessorImpl.invoke0(NativeMethodAccessorImpl.java:-2)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at org.springframework.web.method.support.InvocableHandlerMethod.invoke(InvocableHandlerMethod.java:215)
        at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:132)
        at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:104)
        at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandleMethod(RequestMappingHandlerAdapter.java:745)
        at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:686)
        at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:80)
        at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:925)
        at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:856)
        at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:953)
        at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:844)
        at javax.servlet.http.HttpServlet.service(HttpServlet.java:621)
        at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:829)
        at javax.servlet.http.HttpServlet.service(HttpServlet.java:728)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:305)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:210)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:243)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:210)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:243)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:210)
        at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:243)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:210)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:243)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:210)
        at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:88)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:243)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:210)
        at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:222)
        at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:123)
        at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:502)
        at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:171)
        at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:100)
        at org.apache.catalina.valves.AccessLogValve.invoke(AccessLogValve.java:953)
        at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:118)
        at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:408)
        at org.apache.coyote.http11.AbstractHttp11Processor.process(AbstractHttp11Processor.java:1041)
        at org.apache.coyote.AbstractProtocol$AbstractConnectionHandler.process(AbstractProtocol.java:603)
        at org.apache.tomcat.util.net.JIoEndpoint$SocketProcessor.run(JIoEndpoint.java:310)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
        at java.lang.Thread.run(Thread.java:745)
```
## tt
方法执行数据的时空隧道，记录下指定方法每次调用的入参和返回信息，并能对这些不同的时间下调用进行观测
>watch 虽然很方便和灵活，但需要提前想清楚观察表达式的拼写，这对排查问题而言要求太高，因为很多时候我们并不清楚问题出自于何方，只能靠蛛丝马迹进行猜测。
这个时候如果能记录下当时方法调用的所有入参和返回值、抛出的异常会对整个问题的思考与判断非常有帮助。
于是乎，TimeTunnel 命令就诞生了。
### 记录方法的调用
- 基本用法
对于一个最基本的使用来说，就是记录下当前方法的每次调用环境现场。
```shell
root@local.example.com@bistoury:\>tt -t -n 3 *.Test query
tt -t -n 3 *.Test query
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 131 ms.
 INDEX               TIMESTAMP COST(ms)  IS-RET IS-EXP OBJECT     CLASS  METHOD    
---------------------------------------------------------------------------------
 1002  2018-11-07 20:02:58     11.438431 true   false  0x7aadaf6b Test   query   
 1003  2018-11-07 20:03:02     9.692588  true   false  0x7aadaf6b Test   query   
```

- 命令参数解析
-t
tt 命令有很多个主参数，-t 就是其中之一。这个参数的表明希望记录下类 *.MessageTopologyServiceImpl 的 queryConsumers 方法的每次执行情况。
-n 3
当你执行一个调用量不高的方法时可能你还能有足够的时间用 CTRL+C 中断 tt 命令记录的过程，但如果遇到调用量非常大的方法，瞬间就能将你的 JVM 内存撑爆。
此时你可以通过 -n 参数指定你需要记录的次数，当达到记录次数时 Bistoury 会主动中断tt命令的记录过程，避免人工操作无法停止的情况。

- 表格字段说明

|表格字段	|字段解释|
|-----------|--------|
|INDEX	    |时间片段记录编号，每一个编号代表着一次调用，后续tt还有很多命令都是基于此编号指定记录操作，非常重要。|
|TIMESTAMP	|方法执行的本机时间，记录了这个时间片段所发生的本机时间
|COST(ms)	|方法执行的耗时
|IS-RET	    |方法是否以正常返回的形式结束
|IS-EXP	    |方法是否以抛异常的形式结束
|OBJECT	    |执行对象的hashCode()，注意，曾经有人误认为是对象在JVM中的内存地址，但很遗憾他不是。但他能帮助你简单的标记当前执行方法的类实体
|CLASS	    |执行的类名|
|METHOD	    |执行的方法名|


- 条件表达式
不知道大家是否有在使用过程中遇到以下困惑
Bistoury 似乎很难区分出重载的方法
我只需要观察特定参数，但是 tt 却全部都给我记录了下来
条件表达式也是用OGNL 来编写，核心的判断对象依然是 Advice 对象。除了 tt 命令之外，watch、trace、stack 命令也都支持条件表达式。

- 解决方法重载
tt -t *Test print params[0].length==1
通过制定参数个数的形式解决不同的方法签名，如果参数个数一样，你还可以这样写
tt -t *Test print 'params[1] instanceof Integer'

- 解决指定参数
tt -t *Test print params[0].mobile=="13989838402"
构成条件表达式的 Advice 对象
前边看到了很多条件表达式中，都适用了 params[0]，有关这个变量的介绍，请参考表达式核心变量

### 查看调用信息
对于具体一个时间片的信息而言，你可以通过 -i 参数后边跟着对应的 INDEX 编号查看到他的详细信息。
```shell
root@local.example.com@bistoury:\>tt -i 1000
 INDEX          1000                                                            
 GMT-CREATE     2018-11-07 20:01:49                                             
 COST(ms)       9.752976                                                        
 OBJECT         0x7aadaf6b                                                      
 CLASS          qunar.tc.Test                                                   
 METHOD         query                                                  
 IS-RETURN      true                                                            
 IS-EXCEPTION   false                                                           
 PARAMETERS[0]  @String[test]                                      
 RETURN-OBJ     @EmptyList[isEmpty=true;size=0]                                 
Affect(row-cnt:1) cost in 1 ms.
```
### 重做一次调用
当你稍稍做了一些调整之后，你可能需要前端系统重新触发一次你的调用，此时得求爷爷告奶奶的需要前端配合联调的同学再次发起一次调用。而有些场景下，这个调用不是这么好触发的。

tt 命令由于保存了当时调用的所有现场信息，所以我们可以自己主动对一个 INDEX 编号的时间片自主发起一次调用，从而解放你的沟通成本。此时你需要 -p 参数。
```shell
root@local.example.com@bistoury:\>tt -i 1000 -p
 RE-INDEX       1000                                                            
 GMT-REPLAY     2018-11-08 10:11:51                                             
 OBJECT         0x7aadaf6b                                                      
 CLASS          qunar.tc.Test      
 METHOD         query                                                  
 PARAMETERS[0]  @String[test]                                      
 IS-RETURN      true                                                            
 IS-EXCEPTION   false                                                           
 RETURN-OBJ     @EmptyList[isEmpty=true;size=0]                                 
Time fragment[1000] successfully replayed.
Affect(row-cnt:1) cost in 8 ms.
```
调用的结果虽然一样，但调用的路径发生了变化，由原来的程序发起变成了 Bistoury 自己的内部线程发起的调用了。

<font color="red">需要强调的点</font>

- ThreadLocal 信息丢失
很多框架偷偷的将一些环境变量信息塞到了发起调用线程的 ThreadLocal 中，由于调用线程发生了变化，这些 ThreadLocal 线程信息无法通过 Arthas 保存，所以这些信息将会丢失。

- 引用的对象
需要强调的是，tt 命令是将当前环境的对象引用保存起来，但仅仅也只能保存一个引用而已。如果方法内部对入参进行了变更，或者返回的对象经过了后续的处理，那么在 tt 查看的时候将无法看到当时最准确的值。这也是为什么 watch 命令存在的意义。
## options
全局开关
|名称|默认值|说明
|---|------|---|
|unsafe	             |false	|是否支持对系统级别的类进行增强，<font color="red">打开该开关可能导致把JVM搞挂，请慎重选择！</font>
|dump	               |false	|是否支持被增强了的类dump到外部文件中，如果打开开关，class文件会被dump到/${application dir}/arthas-class-dump/目录下，具体位置详见控制台输出
|batch-re-transform	 |true	|是否支持批量对匹配到的类执行retransform操作
|json-format	       |false	|是否支持json化的输出
|disable-sub-class	 |false	|是否禁用子类匹配，默认在匹配目标类的时候会默认匹配到其子类，如果想精确匹配，可以关闭此开关
|debug-for-asm	     |false	|打印ASM相关的调试信息
|save-result	       |false	|是否打开执行结果存日志功能，打开之后所有命令的运行结果都将保存到/home/admin/logs/arthas/arthas.log中
|job-timeout	       |1d	  |异步后台任务的默认超时时间，超过这个时间，任务自动停止；比如设置 1d, 2h, 3m, 25s，分别代表天、小时、分、秒
### 实例
想打开执行结果存日志功能，输入如下命令即可：
```shell
root@local.example.com@bistoury:\>options save-result true
 NAME         BEFORE-VALUE  AFTER-VALUE                                         
----------------------------------------                                        
 save-result  false         true                                             
```
查看某个开关的值，输入如下命令即可
```shell
root@local.example.com@bistoury:\>options save-result
options save-result
 LEVEL TYPE     NAME         VALUE  SUMMARY          DESCRIPTION                      
--------------------------------------------------------------------------------
 1     boolean  save-result  false  Option to print  This option enables to save each
                                    command's resu   command's result to log file, w
                                    lt to log file   hich path is ${user.home}/logs/a
                                                     rthas-cache/result.log.     
```
## stop/shutdown
停止bistoury-agent attach 到应用中的部分

```shell
admin@local@bistoury_demo_app:\>stop
Bistoury Server is going to shut down...
```
```shell
admin@local@bistoury_demo_app:\>shutdown
Bistoury Server is going to shut down...
```
stop和shutdown执行效果完全一样
<script type="application/javascript">
    $(document).ready(function () {
        $.ajax({
            url: "/version.do",
            success: function (version) {
                $("#version").html("（目前最新版本为" + version + "）");
            },
            error: function () {
                console.log("版本号查询失败")
            }
        });
        $.ajax({
            url: "api/release/info/path.do",
            success: function (ret) {
                if(ret.status==0){
                    $("#release_path").text(ret.data);
                } else {
                    console.log(ret.message)
                    $("#release_path").text("../webapps/releaseInfo.properties");
                }
            },
            error: function () {
                console.log("路径查询失败，使用默认路径")
                ("#release_path").text("../webapps/releaseInfo.properties");
            }
        });
    });
</script>