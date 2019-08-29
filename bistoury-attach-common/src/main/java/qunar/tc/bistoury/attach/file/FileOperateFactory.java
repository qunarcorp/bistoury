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

package qunar.tc.bistoury.attach.file;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.attach.file.bean.FileBean;
import qunar.tc.bistoury.attach.file.impl.DefaultFileServiceImpl;
import qunar.tc.bistoury.attach.file.impl.JarFileServiceImpl;

import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author leix.xie
 * @date 2019-07-29 11:18
 * @describe
 */
public class FileOperateFactory {
    private static final Logger logger = BistouryLoggger.getLogger();

    private static final FileService DEFAULT_FILE_SERVICE = new DefaultFileServiceImpl();

    private static final FileService JAR_FILE_SERVICE = new JarFileServiceImpl();

    private static final String JAR_FILE_PATH_SPLITTER = "!/";

    private static final Splitter JAR_PATH_SPLITTER = Splitter.on(JAR_FILE_PATH_SPLITTER);

    /**
     * 当url中存在多层jar嵌套时，将第一层修改为解压后的目录
     *
     * @param url
     * @return
     */
    public static String replaceJarWithUnPackDir(String url) {
        FileService fileService = chooseFileService(url, 1);
        return fileService.replaceJarWithUnPackDir(url);
    }

    public static String getFile(String path) {
        FileService fileService = chooseFileService(path, 1);
        Optional<URL> url = convertPathToURL(path);
        if (url.isPresent()) {
            return fileService.readFile(url.get());
        } else {
            return null;
        }
    }

    public static List<FileBean> listFiles(String path) {
        return listFiles(Collections.<String>emptySet(), Collections.<String>emptySet(), path);
    }

    public static List<FileBean> listFiles(Set<String> exclusionFileSuffix, Set<String> exclusionFile, String path) {
        FileService fileService = chooseFileService(path, 1);
        Optional<URL> url = convertPathToURL(path);
        if (url.isPresent()) {
            return fileService.listFiles(exclusionFileSuffix, exclusionFile, url.get());
        }
        return ImmutableList.of();
    }


    /**
     * @param path
     * @param count 当以【/!】分割路径，分割后长度大于<code>count</code>则返回默认Service
     * @return
     */
    private static FileService chooseFileService(final String path, int count) {
        List<String> list = JAR_PATH_SPLITTER.splitToList(path);
        if (list.size() > count) {
            return JAR_FILE_SERVICE;
        } else {
            return DEFAULT_FILE_SERVICE;
        }

    }


    private static Optional<URL> convertPathToURL(String path) {
        try {
            URI uri = new URI(path);
            if (Strings.isNullOrEmpty(uri.getScheme())) {
                if (path.indexOf(JAR_FILE_PATH_SPLITTER) >= 0) {
                    return Optional.of(new URL("jar:file:" + path));
                } else {
                    return Optional.of(new URL("file:" + path));
                }
            } else {
                return Optional.of(uri.toURL());
            }
        } catch (Exception e) {
            logger.error("", "", "convert path to url error, path: {}", path, e);
            return Optional.absent();
        }
    }
}
