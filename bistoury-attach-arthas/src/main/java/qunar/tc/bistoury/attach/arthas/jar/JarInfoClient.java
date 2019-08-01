package qunar.tc.bistoury.attach.arthas.jar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.arthas.instrument.InstrumentClient;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.attach.file.FileOperateFactory;
import qunar.tc.bistoury.attach.file.bean.FileBean;
import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;

import java.io.File;
import java.util.List;

/**
 * @author: leix.xie
 * @date: 2019/2/12 16:59
 * @describe：
 */
public class JarInfoClient implements InstrumentClient {

    private static final Logger logger = BistouryLoggger.getLogger();

    private final List<String> jarInfos;

    JarInfoClient(InstrumentInfo instrumentInfo) {
        logger.info("start init jar info client");
        List<String> jarInfoList = ImmutableList.of();
        try {
            jarInfoList = listJar(instrumentInfo);
            logger.info("init jar info client success");
        } catch (Throwable e) {
            destroy();
            logger.error("", "jar info client init error", e);
        }
        jarInfos = jarInfoList;
    }

    public List<String> jarInfo() {
        return jarInfos;
    }

    private List<String> listJar(InstrumentInfo instrumentation) {
        return readJarInfos(instrumentation.getSystemClass());
    }

    private static List<String> readJarInfos(Class clazz) {
        String serverManagerJarPath = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        File jarDir = new File(serverManagerJarPath).getParentFile();
        List<FileBean> fileBeans = FileOperateFactory.listFiles(jarDir.getPath());
        logger.info("agent 扫描 jar 完成");
        List<String> result = Lists.transform(fileBeans, file -> {
            String name = file.getName();
            return name.substring(name.lastIndexOf(File.separatorChar) + 1);
        });
        return result;
    }

    @Override
    public void destroy() {

    }
}
