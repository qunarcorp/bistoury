package qunar.tc.bistoury.agent.common;

/**
 * @author zhenyu.nie created on 2019 2019/7/16 16:56
 */
public class ClosableProcesses {

    public static ClosableProcess wrap(Process process) {
        if (isUnixProcess(process)) {
            return new UnixProcess(process);
        } else {
            return new NormalProcess(process);
        }
    }

    private static boolean isUnixProcess(Process process) {
        try {
            Class<? extends Process> clazz = process.getClass();
            return clazz.getName().equals("java.lang.UNIXProcess");
        } catch (Exception e) {
            return false;
        }
    }
}
