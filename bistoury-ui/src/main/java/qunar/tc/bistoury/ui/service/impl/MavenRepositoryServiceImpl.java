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

package qunar.tc.bistoury.ui.service.impl;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.FileUtil;
import qunar.tc.bistoury.ui.exception.SourceFileNotFoundException;
import qunar.tc.bistoury.ui.exception.SourceFileReadException;
import qunar.tc.bistoury.ui.model.MavenInfo;
import qunar.tc.bistoury.ui.service.JarFileStore;
import qunar.tc.bistoury.ui.service.MavenRepositoryService;

import javax.annotation.Resource;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author: leix.xie
 * @date: 2019/4/3 10:57
 * @describe：
 */
@Service
public class MavenRepositoryServiceImpl implements MavenRepositoryService {

    private static final Logger logger = LoggerFactory.getLogger(MavenRepositoryServiceImpl.class);

    @Resource
    private JarFileStore jarFileStore;

    @Override
    public String getSourceFile(MavenInfo mavenInfo, String className) {
        try {
            return getSourceFile(mavenInfo, className, (info) -> jarFileStore.getJarFileIfPresent(info));
        } catch (Exception e) {
            logger.error("load source file error, {}, {}", mavenInfo, className);
            throw e;
        }
    }

    @Override
    public String downSourceFile(MavenInfo mavenInfo, String className) {
        try {
            return getSourceFile(mavenInfo, className, (info) -> jarFileStore.getJarFile(info));
        } catch (Exception e) {
            logger.error("源码下载失败：maven: {}, class: {}", mavenInfo, className, e);
            throw e;
        }
    }

    private String getSourceFile(MavenInfo mavenInfo, String className, Function<MavenInfo, String> getJarFunc) {
        String jarFile = getJarFunc.apply(mavenInfo);
        if (Strings.isNullOrEmpty(jarFile)) {
            throw new SourceFileNotFoundException("源文件不存在");
        } else {
            return loadClassSourceFile(className, jarFile);
        }
    }

    private String loadClassSourceFile(String className, String jarPath) {
        String classPath = getClassPath(className);
        try (JarFile jarFile = new JarFile(jarPath)) {
            JarEntry entry = jarFile.getJarEntry(classPath);
            if (entry != null && !entry.isDirectory()) {
                return FileUtil.read(jarFile.getInputStream(entry));
            } else {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            throw new SourceFileReadException("源文件读取失败", e);
        }
    }

    private String getClassPath(String className) {
        return className.replace('.', '/') + ".java";
    }
}
