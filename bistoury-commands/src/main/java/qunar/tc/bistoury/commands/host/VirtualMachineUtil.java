/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package qunar.tc.bistoury.commands.host;

import com.google.common.base.Preconditions;
import com.sun.management.OperatingSystemMXBean;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.JavaVersionUtils;
import qunar.tc.bistoury.common.NamedThreadFactory;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.management.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: leix.xie
 * @date: 2018/11/21 11:55
 * @describe：
 */
public class VirtualMachineUtil {

    private static final Logger logger = LoggerFactory.getLogger(VirtualMachineUtil.class);

    private static final String LOCAL_CONNECTOR_ADDRESS_PROP = "com.sun.management.jmxremote.localConnectorAddress";

    private static final ReentrantLock LOCK = new ReentrantLock();

    private static volatile VMConnector vmConnector = null;

    private static int pid = -1;

    private static volatile long expireTime = 0L;

    static {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("vm-connect-close"));
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                LOCK.lock();
                try {
                    if (System.currentTimeMillis() > expireTime && vmConnector != null) {
                        try {
                            vmConnector.close();
                        } catch (IOException e) {
                            //ignore, the application corresponding to this PID may have been closed
                        }
                        vmConnector = null;
                    }
                } finally {
                    LOCK.unlock();
                }
            }
        }, 5, 5, TimeUnit.MINUTES);
    }


    public static VMConnector connect(final int newPid) {
        LOCK.lock();
        try {
            expireTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15);
            if (pid != newPid && vmConnector != null) {
                try {
                    vmConnector.close();
                } catch (IOException e) {
                    //ignore, the application corresponding to this PID may have been closed
                }
                vmConnector = null;
            }
            if (vmConnector == null) {
                pid = newPid;
                vmConnector = doConnect(pid);
            }
            return vmConnector;
        } finally {
            LOCK.unlock();
        }
    }


    private static VMConnector doConnect(int pid) {
        VirtualMachine vm = null;
        try {
            vm = VirtualMachine.attach(String.valueOf(pid));
            final String address = getLocalConnectorAddress(vm);
            JMXServiceURL url = new JMXServiceURL(address);
            JMXConnector connector = JMXConnectorFactory.connect(url);
            return new VMConnector(connector);
        } catch (Exception e) {
            logger.error("attach to tomcat vm {} error", pid, e);
            throw new IllegalStateException("attach to tomcat vm " + pid + " error", e);
        } finally {
            if (vm != null) {
                try {
                    vm.detach();
                } catch (IOException e) {
                    logger.error("detach vm error");
                }
            }
        }
    }

    /**
     * VirtualMachine保证JMX Agent已启动, 并提供连接地址地址，未启动时，尝试强行启动
     * 样例：service:jmx:rmi://127.0.0.1/stub/rO0ABXN9AAAAAQAl...
     */
    private static String getLocalConnectorAddress(VirtualMachine vm) throws IOException {
        try {
            // 1. 检查smartAgent是否已启动
            Properties agentProps = vm.getAgentProperties();
            String address = (String) agentProps.get(LOCAL_CONNECTOR_ADDRESS_PROP);

            if (address != null) {
                return address;
            }

            // 2. 未启动，尝试启动
            // JDK8后有更直接的vm.startLocalManagementAgent()方法
            if (JavaVersionUtils.isGreaterThanOrEqualToJava8()) {
                try {
                    //jdk8以后才有这个方法
                    Method startLocalManagementAgentMethod = vm.getClass().getMethod("startLocalManagementAgent");
                    Object result = startLocalManagementAgentMethod.invoke(vm);
                    if (result != null && (address = result.toString()) != null) {
                        return address;
                    }
                } catch (Exception e) {
                    logger.error("jdk greater than or equal to jdk8， but start local management agent fail ", e);
                }
            }

            //jdk8以前会手动尝试启动，jdk8调用方法启动失败后也会尝试手动启动
            String home = vm.getSystemProperties().getProperty("java.home");

            // Normally in ${java.home}/jre/lib/management-agent.jar but might
            // be in ${java.home}/lib in build environments.

            String agentPath = home + File.separator + "jre" + File.separator + "lib" + File.separator
                    + "management-agent.jar";
            File f = new File(agentPath);
            if (!f.exists()) {
                agentPath = home + File.separator + "lib" + File.separator + "management-agent.jar";
                f = new File(agentPath);
                if (!f.exists()) {
                    throw new IOException("Management agent not found");
                }
            }

            agentPath = f.getCanonicalPath();
            try {
                vm.loadAgent(agentPath, "com.sun.management.jmxremote");
            } catch (AgentLoadException x) {
                IOException ioe = new IOException(x.getMessage());
                ioe.initCause(x);
                throw ioe;
            } catch (AgentInitializationException x) {
                IOException ioe = new IOException(x.getMessage());
                ioe.initCause(x);
                throw ioe;
            }

            // 3. 再次获取connector address
            agentProps = vm.getAgentProperties();
            address = (String) agentProps.get(LOCAL_CONNECTOR_ADDRESS_PROP);

            if (address == null) {
                throw new IOException("Fails to find connector address");
            }

            return address;
        } finally {
            vm.detach();
        }
    }

    static class VMConnector implements Closeable {
        private final JMXConnector connector;

        VMConnector(JMXConnector connector) {
            Preconditions.checkNotNull(connector);
            this.connector = connector;
        }

        public MBeanServerConnection getConnection() throws IOException {
            return connector.getMBeanServerConnection();
        }

        public RuntimeMXBean getRuntimeMXBean() throws IOException {
            return ManagementFactory.newPlatformMXBeanProxy(getConnection(), ManagementFactory.RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);
        }

        public OperatingSystemMXBean getOperatingSystemMXBean() throws IOException {
            return ManagementFactory.newPlatformMXBeanProxy(getConnection(), ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
        }

        public MemoryMXBean getMemoryMXBean() throws IOException {
            return ManagementFactory.newPlatformMXBeanProxy(getConnection(), ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);
        }

        public ThreadMXBean getThreadMXBean() throws IOException {
            return ManagementFactory.newPlatformMXBeanProxy(getConnection(), ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);
        }

        public ClassLoadingMXBean getClassLoadingMXBean() throws IOException {
            return ManagementFactory.newPlatformMXBeanProxy(getConnection(), ManagementFactory.CLASS_LOADING_MXBEAN_NAME, ClassLoadingMXBean.class);
        }

        public List<GarbageCollectorMXBean> getGarbageCollectorMXBeans() throws IOException, MalformedObjectNameException {
            Set<ObjectName> gcNames = getConnection().queryNames(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=*"), null);
            List<GarbageCollectorMXBean> gcMxBeans = new ArrayList<>();
            for (ObjectName gc : gcNames) {
                gcMxBeans.add(ManagementFactory.newPlatformMXBeanProxy(getConnection(), gc.toString(), GarbageCollectorMXBean.class));
            }
            return gcMxBeans;
        }

        public List<MemoryPoolMXBean> getMemoryPoolMXBeans() throws Exception {
            Set<ObjectName> gcNames = getConnection().queryNames(new ObjectName(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE + ",name=*"), null);
            List<MemoryPoolMXBean> memoryPoolMXBeans = new ArrayList<>();
            for (ObjectName gc : gcNames) {
                memoryPoolMXBeans.add(ManagementFactory.newPlatformMXBeanProxy(getConnection(), gc.toString(), MemoryPoolMXBean.class));
            }
            return memoryPoolMXBeans;
        }

        @Override
        public void close() throws IOException {
            connector.close();
        }
    }
}
