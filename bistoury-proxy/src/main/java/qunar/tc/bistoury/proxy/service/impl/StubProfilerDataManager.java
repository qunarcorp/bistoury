package qunar.tc.bistoury.proxy.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerDataManager;

import java.io.File;
import java.util.Objects;

/**
 * @author cai.wen created on 2019/10/30 10:24
 */
@Service
public class StubProfilerDataManager implements ProfilerDataManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfilerDataManager.class);

    //测试
    //todo 删除此文件
    //模拟proxy向agent请求性能分析的文件
    @Override
    public void requestData(String profilerId) {
        String profilerName = getProfilerName(profilerId);
        String command1 = "mkdir -p /tmp/bistoury-profiler/tmp/" + profilerName;
        String command2 = " cp  /tmp/test/bistoury-profiler/" + profilerName
                + "/* /tmp/bistoury-profiler/tmp/" + profilerName;
        try {
            new ProcessBuilder()
                    .redirectErrorStream(true)
                    .redirectError(new File("/tmp/test.log"))
                    .redirectOutput(new File("/tmp/test.log"))
                    .command("bash", "-c", command1 + "&&" + command2)
                    .start()
                    .waitFor();
        } catch (Exception e) {
            LOGGER.error("stub copy error.", e);
        }
    }

    private String getProfilerName(String profilerId) {
        File root = new File("/tmp/test/bistoury-profiler");
        File[] files = root.listFiles((dir, name) -> name.startsWith(profilerId));
        return Objects.requireNonNull(files)[0].getName();
    }
}
