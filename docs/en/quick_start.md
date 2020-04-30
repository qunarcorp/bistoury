# Quick start
Bistouri has multiple modules and is related to the company's own environment. To deploy properly, it needs to be configured.

In order to start and experience bistoury quickly, we provide a  quick start script to start  complete bistoury services on single machine.

**Attention:** The quick start script provided here is only suitable for quick start of stand-alone diagnosis. To get a complete experience, you need to deploy reasonably.

The internal use way of our company is also the recommended deploy way:
- UI is deployed independently. It is recommended to deploy on multiple machines and provide independent domain name
- Proxy is deployed independently. It is recommended to deploy on multiple machines and provide independent domain name
- Agent needs to be deployed on the same machine as application. It is recommended to deploy automatically in the test environment. Online environment provides single machine one-click deployment and all machines one-click deployment under the application
- The independent application center manages all the internal application and machine information of all functions. This is a system independent of bistoury, from which bistoury get the updated application and machine information.

## Build

### Get the package of quick start
- We have built the quick start package on the project [release page](https://github.com/qunarcorp/bistoury/releases), which you can download directly.
- You can also download the source code and build your own quick start package, which is also very simple. First, the clone project goes to you PC and runs script/quick_start_build.sh; After running, the script directory will generate the quick start package. The name format is bistoury_quick_start.tar.gz
### Prepare
- Currently, only linux environment is supported, so a linux environment is needed.
- This machine has jdk1.8+ installed and the `JAVA_HOME` environment variable has been set. If it is not set, the `-j` parameter can be passed in the startup script. For details, see the [startup parameter settings](#startup-parameter).
- The ports 9090, 9091, 9880, and 9881 are not occupied. These ports will be used by Bistoury. If they are occupied, you need to configure them. For details, see [How to resolve the port conflicts](How-to-resolve-the-port-conflicts).
- This machine has started a Java application to be diagnosed. you needn't to processe that if it is a spring web application , non spring web applications need to configure the `- c` parameter of the startup script. For details, see [startup parameters](#startup-parameter)

### Start up

First of all, We need copy the quick start package bistoury-quick-start.tar.gz to the location we want to install.

Then extract the  quick startup package:
```bash
tar -zxvf bistoury-quick-start.tar.gz
cd bistoury
```
The last is to start Bistoury, because the bistoury will use operations such as jstack, to ensure that all features are available, you need to use the same user startup bistoury as the JAVA application you want to diagnose.

e.g: You need to replace ${PID} with the PID of the application
- If the application is started by its own user, it can run directly.
```bash
./quick_start.sh -p ${PID} start
```
- If the application starts with another account, such as tomcat, you need to specify the user and run it
```bash
sudo -u tomcat ./quick_start.sh -p ${PID} start
```
- Stop running
```bash
./quick_start.sh stop
```
OR
```bash
sudo -u tomcat ./quick_start.sh stop
```

### Visit UI
You can visit the UI through http://IP:9091, For example, if the IP address of the deployed UI machine is 192.168.1.20, you can visit it through http://192.168.1.20:9091, Initialize username and password are admin.

### Startup parameter
quick_start.sh can set some startup parameters as shown in the following table:
|name|Required or not|Defaults|Description|
|----|---------------|--------|-----------|
|-i  |false          |The first in the list in ip|Specify an available ip|
|-j  |false          |JAVA_HOME in environment variable|Specify the jdk path|
|-l  |false          |/tmp|The log directory of the application and the directory where the bistoury command is executed, such as LS and tail, will be executed in this directory|
|-p  |true           | |Application process id, because it is a quick start script, you need to use this parameter to specify which JAVA process to diagnose|
|-c  |false          |org.springframework.web.servlet.DispatcherServlet|Used to get some application information, a loaded class in a dependent jar package(The classes used in bistoury agent cannot be used. It is recommended to use the classes in jar package of internal middleware or in spring related packages, such as org.springframework.web.servlet.dispatcherservlet)|
|-h  |false          | |View the help documentation|

### Solve problems
#### How to solve when the port conflicts
The bistoury quick start script will occupy some ports by default, among which, the default port is 9090 for proxy, 9091 for UI, 9880 for agent and proxy communication, 3668 for agent and application communication, 9881 for UI and proxy communication, and 9092 for H2 data base. The solution to port conflict is as follows:
- Modify the port that you occupy
- [Modify the port of bistoury](https://github.com/qunarcorp/bistoury/blob/master/docs/cn/FAQ.md#%E7%AB%AF%E5%8F%A3%E9%97%AE%E9%A2%98)

#### not find proxy for agent
