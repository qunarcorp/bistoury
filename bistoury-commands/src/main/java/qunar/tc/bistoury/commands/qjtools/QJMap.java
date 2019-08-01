package qunar.tc.bistoury.commands.qjtools;

import com.sun.tools.attach.VirtualMachine;
import com.vip.vjtools.vjmap.VJMap;
import sun.jvm.hotspot.HotSpotAgent;
import sun.tools.attach.HotSpotVirtualMachine;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * @author cai.wen
 * @date 18-12-17
 * <p>
 * 分代版的jmap（新生代，存活区，老生代）
 * 复制VJMap类
 */
public class QJMap extends VJMap {
    private static PrintStream tty = System.out;

    public static void main(String[] args) {
        boolean orderByName = false;
        long minSize = -1;
        int minAge = 3;
        boolean live = false;
        if (!(args.length == 2 || args.length == 3)) {
            printHelp();
            return;
        }

        String modeFlag = args[0];

        String[] modeFlags = modeFlag.split(":");
        if (modeFlags.length > 1) {
            String[] addtionalFlags = modeFlags[1].split(",");
            for (String addtionalFlag : addtionalFlags) {
                if ("byname".equalsIgnoreCase(addtionalFlag)) {
                    orderByName = true;
                } else if (addtionalFlag.toLowerCase().startsWith("minsize")) {
                    String[] values = addtionalFlag.split("=");
                    if (values.length == 1) {
                        tty.println("parameter " + addtionalFlag + " is wrong");
                        return;
                    }
                    minSize = Long.parseLong(values[1]);
                } else if (addtionalFlag.toLowerCase().startsWith("minage")) {
                    String[] values = addtionalFlag.split("=");
                    if (values.length == 1) {
                        tty.println("parameter " + addtionalFlag + " is wrong");
                        return;
                    }
                    minAge = Integer.parseInt(values[1]);
                } else if (addtionalFlag.toLowerCase().startsWith("live")) {
                    live = true;
                }
            }
        }

        Integer pid = null;
        String executablePath = null;
        String coredumpPath = null;
        if (args.length == 2) {
            pid = Integer.valueOf(args[1]);
        } else {
            executablePath = args[1];
            coredumpPath = args[2];
        }

        if (live) {
            if (pid == null) {
                tty.println("only a running vm can be attached when live option is on");
                return;
            }
            triggerGc(pid);
        }

        HotSpotAgent agent = new HotSpotAgent();

        try {
            if (args.length == 2) {
                agent.attach(pid);
            } else {
                agent.attach(executablePath, coredumpPath);
            }
            long startTime = System.currentTimeMillis();
            if (modeFlag.startsWith("-all")) {
                runHeapVisitor(pid, orderByName, minSize);
            } else if (modeFlag.startsWith("-sur")) {
                runSurviorAccessor(minAge, orderByName, minSize);
            } else if (modeFlag.startsWith("-old")) {
                runOldGenAccessor(orderByName, minSize);
            } else if (modeFlag.startsWith("-address")) {
                printGenAddress();
            } else if (modeFlag.startsWith("-class")) {
                printLoadedClass();
            } else if (modeFlag.startsWith("-version")) {
                tty.println("qjmap version:" + VERSION);
                return;
            } else {
                printHelp();
                return;
            }
            long endTime = System.currentTimeMillis();
            double secs = (endTime - startTime) / 1000.0d;
            tty.printf("%n Heap traversal took %.1f seconds.%n", secs);
            tty.flush();
        } catch (Exception e) {
            tty.println("Error Happen:" + e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("Can't attach to the process")) {
                tty.println(
                        "Please use the same user of the target JVM to run qjmap");
            }
        } finally {
            agent.detach();
        }
    }

    private static void printHelp() {
        int leftLength = "-all:minsize=1024,byname".length();
        String format = " %-" + leftLength + "s  %s%n";
        tty.println("qjmap " + VERSION
                + " - prints per GC generation (Eden, Survivor, OldGen) object details of a given process.");
        tty.println("Usage: qjmap <options> (pid:<PID>)");
        tty.println("");
        tty.printf(format, "-all", "print all gens histogram, order by total size");
        tty.printf(format, "-all:minsize=1024", "print all gens histogram, total size>=1024");
        tty.printf(format, "-all:minsize=1024,byname",
                "print all gens histogram, total size>=1024, order by class name");

        tty.printf(format, "-old", "print oldgen histogram, order by oldgen size");
        tty.printf(format, "-old:live", "print oldgen histogram, live objects only");
        tty.printf(format, "-old:minsize=1024", "print oldgen histogram, oldgen size>=1024");
        tty.printf(format, "-old:minsize=1024,byname",
                "print oldgen histogram, oldgen size>=1024, order by class name");

        tty.printf(format, "-sur", "print survivor histogram, age>=3");
        tty.printf(format, "-sur:minage=4", "print survivor histogram, age>=4");
        tty.printf(format, "-sur:minsize=1024,byname",
                "print survivor histogram, age>=3, survivor size>=1024, order by class name");
        tty.printf(format, "-address", "print address for all gens");
        tty.printf(format, "-class", "print all loaded classes");
    }

    private static void triggerGc(Integer pid) {
        VirtualMachine vm = null;
        try {
            vm = VirtualMachine.attach(String.valueOf(pid));
            HotSpotVirtualMachine hvm = (HotSpotVirtualMachine) vm;
            try (InputStream in = hvm.executeJCmd("GC.run");) {
                byte b[] = new byte[256];
                int n;
                do {
                    n = in.read(b);
                    if (n > 0) {
                        String s = new String(b, 0, n, "UTF-8");
                        tty.print(s);
                    }
                } while (n > 0);
                tty.println();
            }
        } catch (Exception e) {
            tty.println(e.getMessage());
        } finally {
            if (vm != null) {
                try {
                    vm.detach();
                } catch (IOException e) {
                    tty.println(e.getMessage());
                }
            }
        }
    }
}