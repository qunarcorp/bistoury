package qunar.tc.bistoury.commands;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * @author: leix.xie
 * @date: 2018/11/20 21:30
 * @describe：
 */
public class ThreadDumpTest {
    /**
     * 主方法
     */
    public static void main(String[] args) throws Exception {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] ids = threadMXBean.getAllThreadIds();
        ThreadInfo[] threadInfo = threadMXBean.getThreadInfo(ids);
        for (ThreadInfo info : threadInfo) {
            System.out.println(info.getThreadId() + "\t" + info.getThreadState() + "\t" + info.getThreadName()+"\t");
        }
    }
}
