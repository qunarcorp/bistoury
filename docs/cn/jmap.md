堆内存对象监控
当线上遇到OOM时，我们首先想到的是heap dump，然后通过分析dump文件定位问题，但是如果有人先你一步重启了应用，导致现场破坏，下一次遇到这个问题不知道是什么时候了。

堆内存对象监控可以有效的解决这个问题，堆对象监控监控每分钟会抓取一次每个class的实例数目、内存占用、类全名信息，提供最近三天的数据查询，这样，在应用重启后，我们依然可以获取到出现堆内对象使用情况。

## 实现
为了支持历史的数据查询，bistoury会在agent上启动一个定时任务，每分钟进行一次jmap，考虑到jmap -histo:live会触发一次full gc，所以使用jmap -histo获取对内存对象信息，将占用内存空间最大的100个对象信息存入rocksDB。

进行jmap时，bistoury会先通过pid attach到jvm，然后调用HotSpotVirtualMachine#heapHisto获取堆内存对象信息。

## 使用
- 进入主机信息页面
- 选择需要查看的应用、服务器
- 点击【jvm信息】
- 点击【堆对象统计】
- 点击刷新图标查询数据

**注意** -live参数会统计堆中存活的对象信息，回触发一次full gc，请谨慎选择。 

![jmap_panel](../image/jmap_panel.png)