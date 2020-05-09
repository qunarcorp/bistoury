/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package qunar.tc.bistoury.attach.file.impl;

import com.google.common.io.ByteSink;
import com.google.common.io.Files;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.attach.file.JarStorePathUtil;
import qunar.tc.bistoury.attach.file.URLUtil;
import qunar.tc.bistoury.common.FileUtil;

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

    private static final AtomicBoolean STARTED = new AtomicBoolean(false);

    public JarFileServiceWrapper() {

    }

    /**
     * 通过jarFile获取解压后jar包存储位置
     *
     * @param jarFile
     * @return
     */
    public String getJarPath(final String jarFile) {
        File file = new File(JarStorePathUtil.getJarStorePath());
        if (!file.exists() || !file.isDirectory() || !STARTED.get()) {
            FileUtil.deleteDirectory(file, false);
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
}
