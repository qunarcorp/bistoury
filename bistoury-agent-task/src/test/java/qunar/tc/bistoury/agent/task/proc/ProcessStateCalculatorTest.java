package qunar.tc.bistoury.agent.task.proc;

import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Created by cai.wen on 19-1-18.
 */
public class ProcessStateCalculatorTest {
    private ProcessStateCalculator processStateCalculator = ProcessStateCalculator.getInstance();
    private final int pid = 5471;

    @Test
    public void testCalculatedValue() throws IOException, InterruptedException {
        while (true) {
            processStateCalculator.startRecordFullStat(pid);
            Thread.sleep(20000);
            System.out.println(processStateCalculator.endRecordFullStat(pid).get(0));
        }
    }

    @Test
    public void testPS() throws InterruptedException {
        FullState fullState = processStateCalculator.getCurrentFullState(pid);
        System.out.println(fullState.cpuState.totalTime());
        System.out.println(fullState.cpuState.idleTime);
        System.out.println(ProcUtil.formatJiffies(fullState.cpuState.totalTime()));
        System.out.println(fullState.processState.totalTime());
        System.out.println(ProcUtil.formatJiffies(fullState.processState.totalTime()));
        for (Map.Entry<Integer, ThreadState> entry : fullState.threadInfo.entrySet()) {
            System.out.println("线程id: " + entry.getKey() + " : " + ProcUtil.formatJiffies(entry.getValue().totalTime()));
        }
    }
}
