package qunar.tc.bistoury.attach.file.impl;

import com.google.common.io.ByteSink;
import com.google.common.io.Files;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.attach.file.URLUtil;
import qunar.tc.bistoury.clientside.common.store.BistouryStore;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author leix.xie
 * @date 2019-07-29 16:46
 * @describe
 */
public class JarFileServiceWrapper {

    private static final Logger logger = BistouryLoggger.getLogger();

    private static final String STORE_PATH = BistouryStore.getStorePath("tomcat_webapp");

    private static final AtomicBoolean STARTED = new AtomicBoolean(false);

    public JarFileServiceWrapper() {
        File file = new File(STORE_PATH);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
    }

    /**
     * 通过jarFile获取解压后jar包存储位置
     *
     * @param jarFile
     * @return
     */
    public String getJarPath(final String jarFile) {
        final String jarFilePath = jarFile.substring(jarFile.lastIndexOf(File.separatorChar) + 1).replace(".jar", "");
        File file = new File(STORE_PATH, jarFilePath);
        if (!file.exists() || !file.isDirectory() || !STARTED.get()) {
            deleteDirectory(file);
            unPackJar(jarFile, file);
            STARTED.compareAndSet(false, true);
        }
        return file.getPath();
    }

    private void unPackJar(final String jarFilePath, final File target) {
        try (JarFile jarFile = new JarFile(URLUtil.removeProtocol(jarFilePath))) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    new File(target, entry.getName()).mkdirs();
                } else {
                    File file = new File(target, entry.getName());
                    if (file.createNewFile()) {
                        try (InputStream inputStream = jarFile.getInputStream(entry)) {
                            ByteSink byteSink = Files.asByteSink(file);
                            byteSink.writeFrom(inputStream);
                        }

                    }
                }

            }
        } catch (Exception e) {
            logger.error("", "unpack jar error", e);
        }
    }

    private void deleteDirectory(File file) {
        if (file == null) {
            return;
        }
        if (file.isDirectory()) {
            String[] list = file.list();
            if (list == null) {
                return;
            }
            for (int i = 0; i < list.length; i++) {
                deleteDirectory(new File(file, list[i]));
            }
        } else {
            file.delete();
        }
    }

}
