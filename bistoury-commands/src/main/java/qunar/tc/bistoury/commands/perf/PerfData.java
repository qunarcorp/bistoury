package qunar.tc.bistoury.commands.perf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.JavaVersionUtils;
import sun.management.counter.Counter;
import sun.management.counter.LongCounter;
import sun.management.counter.perf.PerfInstrumentation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author leix.xie
 * @date 2019/9/19 16:15
 * @describe
 */
public class PerfData {
    private static final Logger logger = LoggerFactory.getLogger(PerfData.class);
    public static long NANOS_TO_MILLS = 1000 * 1000;

    private static final Object PERF_INSTANCE;
    private static final Method ATTACH_METHOD;

    static {
        try {
            Class<?> perfClass;
            //Perf perf = Perf.getPerf();
            if (JavaVersionUtils.isLessThanJava9()) {
                perfClass = Class.forName("sun.misc.Perf");
            } else {
                perfClass = Class.forName("jdk.internal.perf.Perf");
            }
            Method getPerfMethod = perfClass.getMethod("getPerf");
            PERF_INSTANCE = getPerfMethod.invoke(null);

            ATTACH_METHOD = perfClass.getMethod("attach", int.class, String.class);

        } catch (ClassNotFoundException e) {
            logger.error("reflect perf class error", e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error("pref instance init error", e);
            throw new RuntimeException(e);
        }
    }

    private final PerfInstrumentation instr;
    // PerfData中的时间相关数据以tick表示，每个tick的时长与计算机频率相关
    private final double nanosPerTick;

    private final Map<String, Counter> counters;

    public static PerfData connect(int pid) {
        try {
            return new PerfData(pid);
        } catch (ThreadDeath | OutOfMemoryError e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException("Cannot perf data for process " + pid, e);
        }
    }

    private PerfData(int pid) throws IOException, InvocationTargetException, IllegalAccessException {
        ByteBuffer bb = (ByteBuffer) ATTACH_METHOD.invoke(PERF_INSTANCE, pid, "r");
        instr = new PerfInstrumentation(bb);
        counters = buildAllCounters();

        long hz = (Long) counters.get("sun.os.hrt.frequency").getValue();
        nanosPerTick = ((double) TimeUnit.SECONDS.toNanos(1)) / hz;
    }

    private Map<String, Counter> buildAllCounters() {
        Map<String, Counter> result = new HashMap<>(512);

        for (Counter c : instr.getAllCounters()) {
            result.put(c.getName(), c);
        }

        return result;
    }

    public Map<String, Counter> getAllCounters() {
        return counters;
    }

    public Counter findCounter(String counterName) {
        return counters.get(counterName);
    }

    public long tickToMills(LongCounter tickCounter) {
        if (tickCounter.getUnits() == sun.management.counter.Units.TICKS) {
            return (long) ((nanosPerTick * tickCounter.longValue()) / NANOS_TO_MILLS);
        } else {
            throw new IllegalArgumentException(tickCounter.getName() + " is not a ticket counter");
        }
    }
}
