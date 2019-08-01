package qunar.tc.bistoury.agent.task.proc;


import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author cai.wen
 * @date 19-1-17
 */
class StatParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatParser.class);

    private static final Splitter SPACE_SPLITTER = Splitter.on(" ").omitEmptyStrings();
    private static final StatParser INSTANCE = new StatParser();
    private static final String PROC_PATH = "/proc";

    private StatParser() {
    }

    public CpuState parseCpuInfo() throws IOException {
        File procDir = new File(PROC_PATH);
        if (!procDir.exists()) {
            throw new IllegalStateException("can't get proc directory");
        }
        return CpuState.parse(getInfoList(new File(procDir, "stat")));
    }

    public ThreadState parseThreadInfo(int pid, int tid) throws IOException {
        File pidDir = new File(PROC_PATH, String.valueOf(pid));
        if (!pidDir.exists()) {
            throw new IllegalStateException("can not open pid directory : " + pidDir.getAbsoluteFile());
        }
        File tidDir = new File(pidDir, "task/" + String.valueOf(tid));
        if (!tidDir.exists()) {
            LOGGER.info("can not open tid directory : {}", tidDir.getAbsoluteFile());
            return null;
        }
        return ThreadState.parse(getInfoList(new File(tidDir, "stat")));
    }

    public ProcessState parseProcessInfo(int pid) throws IOException {
        File pidDir = new File(PROC_PATH, String.valueOf(pid));
        if (!pidDir.exists()) {
            throw new IllegalStateException("can not open pid directory : " + pidDir.getAbsoluteFile());
        }
        return ProcessState.parse(getInfoList(new File(pidDir, "stat")));
    }

    private List<String> getInfoList(File file) throws IOException {
        return SPACE_SPLITTER.splitToList(Files.toString(file, Charsets.UTF_8));
    }

    public static StatParser getInstance() {
        return INSTANCE;
    }
}
