package qunar.tc.bistoury.agent.task.proc;


import org.junit.Test;

import java.io.IOException;

/**
 * @author cai.wen
 * @date 19-1-17
 */
public class StatParserTest {
    @Test
    public void testParser() throws IOException {
        System.out.println(StatParser.getInstance().parseCpuInfo());
        System.out.println(StatParser.getInstance().parseProcessInfo(3552));
        System.out.println(StatParser.getInstance().parseThreadInfo(3552, 22715));
        System.out.println(StatParser.getInstance().parseThreadInfo(3552, 22445));
        System.out.println(StatParser.getInstance().parseThreadInfo(3552, 3566));
        System.out.println(StatParser.getInstance().parseThreadInfo(3552, 3638));
    }

    @Test
    public void formatInfo() {
        System.out.println(ProcUtil.formatJiffies(10000001));
    }

}
