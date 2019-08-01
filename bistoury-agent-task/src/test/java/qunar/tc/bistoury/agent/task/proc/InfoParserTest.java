package qunar.tc.bistoury.agent.task.proc;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by cai.wen on 19-1-18.
 */
public class InfoParserTest {
    int pid = 3172;

    @Test
    public void cmdLine() throws IOException {
        File cmdLineFile = new File("/proc/" + pid, "cmdline");
        Splitter splitter = Splitter.on("\0");
        Joiner joiner = Joiner.on(" ");
        System.out.println(joiner.join(splitter.splitToList(Files.toString(cmdLineFile, Charsets.UTF_8))));
    }
}
