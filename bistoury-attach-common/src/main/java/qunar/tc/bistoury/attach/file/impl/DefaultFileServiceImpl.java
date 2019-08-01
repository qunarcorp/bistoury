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
        return listFiles(Collections.emptySet(), Collections.emptySet(), url);
    }

    @Override
    public List<FileBean> listFiles(final Set<String> exclusionFileSuffix, final Set<String> exclusionFile, final URL url) {
        final String filePath = url.getPath();
        if (Strings.isNullOrEmpty(filePath)) {
            return Collections.emptyList();
        }
        List<FileBean> result = new ArrayList<>();
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
