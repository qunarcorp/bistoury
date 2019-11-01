package qunar.tc.bistoury.commands;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sun.management.OperatingSystemMXBean;
import com.sun.tools.attach.VirtualMachine;
import qunar.tc.bistoury.agent.common.job.ContinueResponseJob;
import qunar.tc.bistoury.commands.host.HostTask;
import qunar.tc.bistoury.commands.perf.PerfData;
import sun.management.counter.Counter;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * @author: leix.xie
 * @date: 2018/11/19 15:08
 * @describe：
 */
public class JVMTest {
    private static short KB = 1024;

    public JVMTest() {
    }

    /**
     * 主方法
     */
    public static void main(String[] args) throws Exception {
        //new JVMTest();
        //getJvmInfo();
        final CountDownLatch latch = new CountDownLatch(1);
        HostTask hostTask = new HostTask(UUID.randomUUID().toString(), 10995, new ConsoleResponseHandler(), 5000L);
        ContinueResponseJob job = hostTask.createJob();
        ListenableFuture<Integer> future = hostTask.getResultFuture();
        Futures.addCallback(future, new FutureCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                System.out.println(result);
                latch.countDown();
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                latch.countDown();
            }
        });
        latch.await();

    }

    public static void getJvmInfo() {
        PerfData perfData = PerfData.connect(64896);
        Map<String, Counter> jvmInfo = perfData.getAllCounters();
        Set<String> keySet = jvmInfo.keySet();
        List<String> keys = new ArrayList<>(keySet);
        Collections.sort(keys);
        for (String key : keys) {
            System.out.println(key + "\t" + jvmInfo.get(key).getValue());
        }
    }

    public static void getVmInfo() throws Exception {
        VirtualMachine vm = VirtualMachine.attach(String.valueOf(10248));
        // 获得连接地址
        Properties properties = vm.getAgentProperties();
        String address = (String) properties.get("com.sun.management.jmxremote.localConnectorAddress");
        System.out.println(address);
        JMXServiceURL url = new JMXServiceURL(address);
        JMXConnector connector = JMXConnectorFactory.connect(url);
        RuntimeMXBean rmxb = ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(), "java.lang:type=Runtime", RuntimeMXBean.class);
        MemoryMXBean memoryMXBean = ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(), ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(), ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
        System.out.println(operatingSystemMXBean.getSystemCpuLoad());
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
        Map<String, Object> result = new HashMap<>();
        //堆提交内存
        result.put("heapCommitedMemory", memoryUsage.getCommitted() / KB);
        //当前堆内存
        result.put("heapUsedMemory", memoryUsage.getUsed() / KB);
        //最大堆大小
        result.put("heapMaxMemory", memoryUsage.getMax() / KB);

        memoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        //非堆提交内存
        result.put("nonHeapCommitedMemory", memoryUsage.getCommitted() / KB);
        //当前非堆内存
        result.put("nonHeapUsedMemory", memoryUsage.getUsed() / KB);
        //最大非堆大小
        result.put("nonHeapMaxMemory", memoryUsage.getMax() / KB);
        System.out.println(result);
        vm.detach();
    }
}
