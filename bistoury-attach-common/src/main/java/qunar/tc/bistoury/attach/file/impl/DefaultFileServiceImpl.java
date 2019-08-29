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

import com.google.common.base.Strings;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.attach.file.AbstractFileService;
import qunar.tc.bistoury.attach.file.URLUtil;
import qunar.tc.bistoury.attach.file.bean.FileBean;
import qunar.tc.bistoury.common.FileUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author leix.xie
 * @date 2019-07-25 19:59
 * @describe
 */
public class DefaultFileServiceImpl extends AbstractFileService {
    private static final Logger logger = BistouryLoggger.getLogger();

    @Override
    public String replaceJarWithUnPackDir(String url) {
        return url;
    }

    @Override
    public String readFile(final URL url) {
        String path = URLUtil.removeProtocol(url.toString());
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("文件 " + path + " 不存在");
        } else if (!file.isFile()) {
            throw new RuntimeException("文件 " + path + " 不是一个文件，可能是文件夹");
        } else {
            try {
                return FileUtil.readFile(file);
            } catch (IOException e) {
                throw new RuntimeException("读取文件失败: " + e.getMessage());
            }
        }
    }


    @Override
    public List<FileBean> listFiles(final URL url) {
        return listFiles(Collections.<String>emptySet(), Collections.<String>emptySet(), url);
    }

    @Override
    public List<FileBean> listFiles(final Set<String> exclusionFileSuffix, final Set<String> exclusionFile, final URL url) {
        final String filePath = url.getPath();
        if (Strings.isNullOrEmpty(filePath)) {
            return Collections.emptyList();
        }
        final List<FileBean> result = new ArrayList<>();
        Path path = Paths.get(filePath);
        SimpleFileVisitor<Path> finder = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file != null && file.getFileName() != null && !isExclusionFile(exclusionFileSuffix, exclusionFile, file.getFileName().toString())) {
                    File tempFile = file.toFile();
                    result.add(new FileBean(tempFile.toPath().toString(), tempFile.lastModified(), tempFile.length()));
                }
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return super.preVisitDirectory(dir, attrs);
            }
        };

        try {
            Files.walkFileTree(path, finder);
        } catch (IOException e) {
            logger.error("", "", "walk file tree error, rootPath:{}", path.toString(), e);
        }
        return result;
    }

}
