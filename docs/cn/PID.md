# pid获取
bistoury的agent在工作过程中会和应用进行通信，此时会使用到应用的pid，默认提供三种pid获取方法。并按照以下优先级获取
- 系统参数指定
- jps -l获取
- ps aux|grep java 获取

pid获取类：`qunar.tc.bistoury.agent.common.pid.PidUtils`
## 系统参数指定
该方法是在agent启动时需要在bistoury-agent-bin/conf/bistoury-agent-env.sh中自行配置系统参数`bistoury.user.pid`指定pid，也可在脚本启动时使用-p参数指定pid，使用这种方式在应用重启后也需要重启agent。

实现类：`qunar.tc.bistoury.agent.common.pid.impl.PidBySystemPropertyHandler`
## jps -l获取
该方法可以动态获取pid，应用重启不需要重启agent，以下是`jps -l`命令执行结果
```bash
28499 org.h2.tools.Server
28533 qunar.tc.bistoury.ui.container.Bootstrap
28522 qunar.tc.bistoury.indpendent.agent.Main
28330 org.jetbrains.jps.cmdline.Launcher
28331 qunar.tc.githubtest.GithubTestApplication
28510 qunar.tc.bistoury.proxy.container.Bootstrap
28655 sun.tools.jps.Jps
```
在agent启动时需要在bistoury-agent-bin/conf/bistoury-agent-env.sh中自行配置系统参数`bistoury.pid.handler.jps.symbol.class`，这个的值为`jps -l`执行结果的第二列，即上面执行结果的`qunar.tc.githubtest.GithubTestApplication`等，默认值为`org.apache.catalina.startup.Bootstrap`

实现类：`qunar.tc.bistoury.agent.common.pid.impl.PidByJpsHandler`
## ps aux|grep java 获取
该方法可以动态获取pid，应用重启不需要重启agent，以下是`ps aux|grep java`命令执行结果
```
tomcat         28331   0.0  0.9  7206632 311744   ??  S     3:55下午   0:38.93 /Library/Java/JavaVirtualMachines/jdk1.8.0_211.jdk/Contents/Home/bin/java -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:52127,suspend=y,server=n
tomcat         28330   0.0  0.6  8536264 201216   ??  S     3:55下午   0:04.00 /Library/Java/JavaVirtualMachines/jdk1.8.0_211.jdk/Contents/Home/bin/java -Xmx700m -Djava.awt.headless=true -Djava.endorsed.dirs="" 
```
在bistoury-proxy/conf/agent_config.properties中配置`tomcat.user`和`tomcat.command`.

`tomcat.user`的值为运行java应用的用户名，[ps aux | grep java] 结果的第1列，即上面执行结果的`tomcat`，默认为 `tomcat`

`tomcat.command`的值为运行Java应用的Java命令，[ps aux | grep java] 结果的第11列，即上面执行结果的`/Library/Java/JavaVirtualMachines/jdk1.8.0_211.jdk/Contents/Home/bin/java`，默认值 /home/java/default/bin/java

实现类：`qunar.tc.bistoury.agent.common.pid.impl.PidByPsHandler`

## 自定义pid获取方法
为了使用公司业务，也可以自定义实现pid获取方法，实现方法如下：
- 在`qunar.tc.bistoury.agent.common.pid.impl.Priority`中定义将要实现的获取方法的优先级。
- 继承`qunar.tc.bistoury.agent.common.pid.impl.AbstractPidHandler`类实现pid获取的逻辑
- 在`qunar.tc.bistoury.agent.common.pid.PidUtils#initPidHandler`中将实现的pid获取逻辑加入到获取逻辑列表中。
