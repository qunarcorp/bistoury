package qunar.tc.bistoury.proxy.service;

import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author cai.wen created on 2019/10/30 10:24
 */
@Service
public class StubProfilerDataManager implements ProfilerDataManager {

    //测试
    //模拟proxy向agent请求性能分析的文件
    @Override
    public void requestData(String profilerId) {
        String command1 = "mkdir C:\\Users\\cai.wen\\AppData\\Local\\Temp\\bistoury-profiler\\tmp\\" + profilerId;
        String command2 = " copy C:\\tmp\\bistoury-profiler\\" + profilerId
                + " C:\\Users\\cai.wen\\AppData\\Local\\Temp\\bistoury-profiler\\tmp\\" + profilerId;
        try {
            new ProcessBuilder()
                    .redirectErrorStream(true)
                    .redirectError(new File("c:\\tmp\\test.log"))
                    .redirectOutput(new File("c:\\tmp\\test.log"))
                    .command("cmd", "/c", command1 + "|" + command2)
                    .start()
                    .waitFor();
        } catch (Exception e) {
            //ignore
        }
    }
}
