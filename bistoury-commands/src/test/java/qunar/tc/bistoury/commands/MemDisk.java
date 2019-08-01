package qunar.tc.bistoury.commands;

import com.sun.management.OperatingSystemMXBean;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author: leix.xie
 * @date: 2018/11/16 15:38
 * @describe：
 */
public class MemDisk {
    public static void main(String[] args) throws Exception {
        /*System.out.println(System.getProperty("os.name"));
        System.out.println(System.getProperty("os.version"));
        System.out.println(System.getProperty("os.arch"));

        getMemInfo();
        System.out.println();
        getDiskInfo();
        System.out.println(System.getProperty("sun.cpu.isalist"));*/
        getJvmMemPool();
    }

    public static void getJvmMemPool() {
        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
            MemoryUsage usage = memoryPoolMXBean.getUsage();
            System.out.println(memoryPoolMXBean.getName() + "\t" + memoryPoolMXBean.getUsage());
        }
    }

    public static void getDiskInfo() {
        File[] disks = File.listRoots();
        for (File file : disks) {
            System.out.print(file.getPath() + "    ");
            System.out.print("空闲未使用 = " + file.getFreeSpace() / 1024 / 1024 + "M" + "    ");// 空闲空间
            System.out.print("已经使用 = " + file.getUsableSpace() / 1024 / 1024 + "M" + "    ");// 可用空间
            System.out.print("总容量 = " + file.getTotalSpace() / 1024 / 1024 + "M" + "    ");// 总空间
            System.out.println();
        }
    }

    public static void getMemInfo() throws Exception {
        OperatingSystemMXBean mem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        System.out.println("可供运行进程使用的虚拟内存量：" + mem.getCommittedVirtualMemorySize() / 1024 / 1024 + "M");
        System.out.println("总交换空间量：" + mem.getTotalSwapSpaceSize() / 1024 / 1024 + "M");
        System.out.println("可用交换空间量：" + mem.getFreeSwapSpaceSize() / 1024 / 1024 + "M");
        System.out.println("总物理内存：" + mem.getTotalPhysicalMemorySize() / 1024 / 1024 + "M");
        System.out.println("可用物理内存量：" + mem.getFreePhysicalMemorySize() / 1024 / 1024 + "M");
        for (int i = 0; i < 1; i++) {
            System.out.println("cpu使用率：" + ((int) ((mem.getSystemCpuLoad()) * 100000)) / 1000.0 + "%");
            Thread.sleep(10);
        }
    }

    public static String getCPURateForLinux() throws Exception {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader brStat = null;
        StringTokenizer tokenStat = null;
        String user = "";
        String linuxVersion = System.getProperty("os.version");
        try {
            System.out.println("Linux版本: " + linuxVersion);


            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", "top -b -n 1 "});
            is = process.getInputStream();
            isr = new InputStreamReader(is);
            brStat = new BufferedReader(isr);


            if (linuxVersion.equals("2.4")) {
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();


                tokenStat = new StringTokenizer(brStat.readLine());
                tokenStat.nextToken();
                tokenStat.nextToken();
                user = tokenStat.nextToken();
                tokenStat.nextToken();
                String system = tokenStat.nextToken();
                tokenStat.nextToken();
                String nice = tokenStat.nextToken();


                System.out.println(user + " , " + system + " , " + nice);


                user = user.substring(0, user.indexOf("%"));
                system = system.substring(0, system.indexOf("%"));
                nice = nice.substring(0, nice.indexOf("%"));


                float userUsage = new Float(user).floatValue();
                float systemUsage = new Float(system).floatValue();
                float niceUsage = new Float(nice).floatValue();
                return String.valueOf((userUsage + systemUsage + niceUsage) / 100);
            } else {
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();
                tokenStat = new StringTokenizer(brStat.readLine());
                tokenStat.nextToken();
                String userUsage = tokenStat.nextToken(); // 用户空间占用CPU百分比
                user = userUsage.substring(0, userUsage.indexOf("%"));
                System.out.println(user);
                process.destroy();
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            return "100";
        } finally {
        }
        return user; // host cpu占用率
    }
}