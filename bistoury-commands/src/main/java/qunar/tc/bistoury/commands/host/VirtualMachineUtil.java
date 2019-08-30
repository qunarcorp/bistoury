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

import com.sun.management.OperatingSystemMXBean;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.IOException;
import java.lang.management.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author: leix.xie
 * @date: 2018/11/21 11:55
 * @describe：
 */
public class VirtualMachineUtil {
    private static final Logger logger = LoggerFactory.getLogger(VirtualMachineUtil.class);

    private static final String LOCAL_CONNECTOR_ADDRESS_PROP = "com.sun.management.jmxremote.localConnectorAddress";

    public static VMConnector connect(int pid) {
        VirtualMachine vm = null;
        try {
            vm = VirtualMachine.attach(String.valueOf(pid));
            final String address = getLocalConnectorAddress(vm);
            JMXServiceURL url = new JMXServiceURL(address);
            JMXConnector connector = JMXConnectorFactory.connect(url);
            return new VMConnector(connector);
        } catch (Exception e) {
            logger.error("attach to tomcat vm error ", e);
            return null;
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

    static class VMConnector {
        private final JMXConnector connector;

        VMConnector(JMXConnector connector) {
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


        public void disconnect() throws IOException {
            if (connector != null) {
                connector.close();
            }
        }

        public JMXConnector getConnector() {
            return connector;
        }
    }
}
