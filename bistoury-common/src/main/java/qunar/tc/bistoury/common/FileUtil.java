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

package qunar.tc.bistoury.common;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: leix.xie
 * @date: 2018/12/13 11:55
 * @describe：
 */
public final class FileUtil {

    private static final String USER_HOME = System.getProperty("user.home");

    private FileUtil() {
    }

    /**
     * 遍历文件夹
     *
     * @param file
     * @return
     */
    public static List<String> listFile(File file) {
        List<String> result = new ArrayList<>();
        listFile(result, file);
        return result;
    }

    public static List<File> listFile(File file, Predicate<File> filter) {
        List<File> result = Lists.newArrayList();
        listFile(result, file, filter);
        return result;
    }

    private static void listFile(List<String> resultNames, File file) {
        List<File> resultFiles = Lists.newArrayList();
        listFile(resultFiles, file, Predicates.<File>alwaysTrue());
        for (File resultFile : resultFiles) {
            resultNames.add(resultFile.getName());
        }
    }

    private static void listFile(List<File> resultFiles, File file, Predicate<File> filter) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File f : files) {
                listFile(resultFiles, f, filter);
            }
        } else if (filter.apply(file)) {
            resultFiles.add(file);
        }
    }

    /**
     * 合并文件路径
     *
     * @param basePath
     * @param paramPath
     * @return
     */
    public static String dealPath(String basePath, String paramPath) {
        Path path = Paths.get(parseUserHomePath(basePath));
        return path.resolve(parseUserHomePath(paramPath)).normalize().toString();
    }

    private static String parseUserHomePath(String path) {
        if (path.startsWith("~")) {
            return USER_HOME + path.substring(1);
        }
        return path;
    }

    /**
     * 将字节流缓存至字节组。
     *
     * @param in A InputStream
     * @return byte array
     * @throws java.io.IOException
     */
    public static byte[] readBytes(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte buf[] = new byte[1024];
            for (int i = 0; (i = in.read(buf)) != -1; )
                baos.write(buf, 0, i);
        } finally {
            in.close();
        }
        return baos.toByteArray();
    }

    /**
     * 将文件二进制内容读至字节组。
     *
     * @param file A File
     * @return byte array
     * @throws java.io.IOException
     */
    public static byte[] readBytes(File file) throws IOException {
        return readBytes(new FileInputStream(file));
    }

    public static String read(InputStream in) throws IOException {
        return readString(in, Charsets.UTF_8.name());
    }

    public static String readFile(File file) throws IOException {
        return Files.asCharSource(file, Charsets.UTF_8).read();
    }

    public static String readString(File file, Charset charset) throws IOException {
        return readString(file, charset.name());
    }

    public static String readString(File file, String charset) throws IOException {
        return readString(new FileInputStream(file), charset);
    }

    public static String readString(InputStream in, String charset) throws IOException {
        return readString(new InputStreamReader(in, charset));
    }

    public static String readString(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        try {
            char[] buf = new char[1024];
            for (int i = 0; (i = reader.read(buf)) != -1; )
                sb.append(buf, 0, i);
        } finally {
            reader.close();
        }
        return sb.toString();
    }
}
