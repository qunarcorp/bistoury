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

import com.google.common.base.Splitter;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.attach.file.AbstractFileService;
import qunar.tc.bistoury.attach.file.URLUtil;
import qunar.tc.bistoury.attach.file.bean.FileBean;
import qunar.tc.bistoury.common.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author leix.xie
 * @date 2019-07-26 10:54
 * @describe
 */
public class JarFileServiceImpl extends AbstractFileService {

    private static final Logger logger = BistouryLoggger.getLogger();

    private static final String JAR_FILE_PATH_SPLITTER = "!/";

    private static final Splitter JAR_PATH_SPLITTER = Splitter.on(JAR_FILE_PATH_SPLITTER);

    private JarFileServiceWrapper jarFileServiceWrapper = new JarFileServiceWrapper();


    @Override
    public String replaceJarWithUnPackDir(String url) {
        List<String> list = JAR_PATH_SPLITTER.splitToList(url);
        if (isNestJar(url)) {
            return cleanSuffix(getNestJarUrl(list.get(0), list.get(1), list.get(2)));
        } else {
            return cleanSuffix(url);
        }
    }

    @Override
    public String readFile(URL url) {
        try {
            if (isNestJar(url.toString())) {
                return readFileInNestJar(url);
            } else {
                return readFileInJar(url);
            }
        } catch (Exception e) {
            logger.error("", "read file in jar error", e);
        }
        return null;
    }

    private String readFileInJar(URL url) throws IOException {
        InputStream in = null;
        try {
            in = url.openStream();
            return FileUtil.read(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private String readFileInNestJar(final URL url) throws IOException {
        List<String> list = JAR_PATH_SPLITTER.splitToList(url.toString());
        String nestJarUrl = getNestJarUrl(list.get(0), list.get(1), list.get(2));
        return readFileInJar(new URL(nestJarUrl));
    }

    private String getNestJarUrl(final String jarFile, final String jarFileChild, final String filePath) {
        String jarPath = jarFileServiceWrapper.getJarPath(jarFile);
        if (jarFileChild.endsWith("jar")) {
            return "jar:file:" + jarPath + File.separator + jarFileChild + JAR_FILE_PATH_SPLITTER + filePath;
        } else {
            return "file:" + jarPath + File.separator + jarFileChild + File.separator + filePath;
        }
    }

    private String cleanSuffix(String path) {
        if (path.endsWith("!")) {
            return path.substring(0, path.length() - 1);
        } else if (path.endsWith("!/")) {
            return path.substring(0, path.length() - 2);
        }
        return path;
    }

    @Override
    public List<FileBean> listFiles(URL url) {
        return listFiles(Collections.<String>emptySet(), Collections.<String>emptySet(), url);
    }

    @Override
    public List<FileBean> listFiles(Set<String> exclusionFileSuffix, Set<String> exclusionFile, URL url) {
        List<FileBean> result = new ArrayList<>();
        final String jarFilePath = URLUtil.removeProtocol(url.toString());
        final String[] array = jarFilePath.split(JAR_FILE_PATH_SPLITTER);
        final String jarPath = array[0];
        final String libPath = array[1];
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry element = entries.nextElement();
                if (!element.isDirectory() && element.getName().startsWith(libPath) && !isExclusionFile(exclusionFileSuffix, exclusionFile, element.getName())) {
                    result.add(new FileBean(jarPath + JAR_FILE_PATH_SPLITTER + element.getName(), element.getTime(), element.getSize()));
                }
            }
        } catch (Exception e) {
            logger.error("", "list file error, url: {}", url, e);
        }
        return result;
    }

    private boolean isNestJar(String url) {
        List<String> list = JAR_PATH_SPLITTER.splitToList(url);
        if (list.size() <= 2) {
            return false;
        } else {
            String jarFileChild = list.get(1);
            if (jarFileChild.endsWith(".jar") || jarFileChild.endsWith("!") || jarFileChild.endsWith("!/") || jarFileChild.endsWith("classes")) {
                return true;
            }
            return false;
        }
    }
}
