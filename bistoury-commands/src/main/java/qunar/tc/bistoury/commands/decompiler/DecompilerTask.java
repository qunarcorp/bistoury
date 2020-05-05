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

package qunar.tc.bistoury.commands.decompiler;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.ning.http.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.agent.common.job.BytesJob;
import qunar.tc.bistoury.agent.common.job.ContinueResponseJob;
import qunar.tc.bistoury.clientside.common.store.BistouryStore;
import qunar.tc.bistoury.common.*;
import qunar.tc.bistoury.remoting.command.DecompilerCommand;
import qunar.tc.bistoury.remoting.netty.AgentRemotingExecutor;
import qunar.tc.bistoury.remoting.netty.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author: leix.xie
 * @date: 2019/3/1 10:31
 * @describe：
 */
public class DecompilerTask implements Task {
    private static final Logger logger = LoggerFactory.getLogger(DecompilerTask.class);

    private static final String JAR = "jar";
    private static final String JAVA_FILE_SUFFIX = ".java";
    private static final String CLASS_FILE_SUFFIX = ".class";
    private static final String JAR_FILE_URL_PREFIX = "jar:file:";
    private static final String FILE_URL_PREFIX = "file:";
    private static final String JAR_FILE_URL_SPLITTER = ".jar!";
    private static final File DECOMPILER_RESULT_SAVER_DIRECTORY = new File(BistouryStore.getStorePath("decompiled"));

    private final String id;
    private final DecompilerCommand command;
    private final ResponseHandler handler;
    private final long maxRunningMs;

    private final SettableFuture<Integer> future = SettableFuture.create();

    public DecompilerTask(String id, DecompilerCommand command, ResponseHandler handler, long maxRunningMs) {
        this.id = id;
        this.command = command;
        this.handler = handler;
        this.maxRunningMs = maxRunningMs;
        if (!DECOMPILER_RESULT_SAVER_DIRECTORY.exists() || !DECOMPILER_RESULT_SAVER_DIRECTORY.isDirectory()) {
            DECOMPILER_RESULT_SAVER_DIRECTORY.mkdirs();
        }
    }

    @Override
    public ContinueResponseJob createJob() {
        return new Job();
    }

    @Override
    public ListenableFuture<Integer> getResultFuture() {
        return future;
    }

    private class Job extends BytesJob {

        private Job() {
            super(id, handler, future);
        }

        @Override
        protected byte[] getBytes() throws Exception {
            Decompiler decompiler = new Decompiler(DECOMPILER_RESULT_SAVER_DIRECTORY);
            TypeResponse<String> typeResponse = new TypeResponse<>();
            CodeProcessResponse<String> response = new CodeProcessResponse<>();
            typeResponse.setData(response);
            typeResponse.setType("decompilerclass");
            try {
                final String className = command.getClassName();
                final String classPath = command.getClassPath();
                decompile(decompiler, className, classPath, response);
            } catch (Exception e) {
                response.setCode(-1);
                response.setMessage("反编译失败，" + e.getMessage());
                logger.error("decompiler error, command: {} ", command, e);
            }
            return JacksonSerializer.serializeToBytes(typeResponse);
        }

        @Override
        public ListeningExecutorService getExecutor() {
            return AgentRemotingExecutor.getExecutor();
        }
    }

    public synchronized void decompile(Decompiler decompiler, final String className, final String classPath, final CodeProcessResponse<String> response) throws IOException {
        String replace = classPath.replace("\\", "/");
        URL url = new URL(replace);
        String simpleName = className.substring(className.lastIndexOf(".") + 1);
        String classFileName = simpleName + UUID.randomUUID().toString();

        if (JAR.equals(url.getProtocol()) || url.getFile().indexOf(JAR_FILE_URL_SPLITTER) > 0) {
            decompilerJar(decompiler, classFileName, className, url);
        } else {
            classFileName = simpleName;

            //处理内部类
            File source = new File(url.getFile());
            List<File> innerClasses = findInnerClasses(source);
            for (File innerClass : innerClasses) {
                decompiler.addSource(innerClass);
            }

            decompiler.addSource(source);
            decompiler.decompileContext();
        }

        File file = new File(DECOMPILER_RESULT_SAVER_DIRECTORY, classFileName + JAVA_FILE_SUFFIX);
        try {
            if (file.exists() && file.isFile()) {
                response.setCode(0);
                String fileContent = FileUtil.readFile(file);
                response.setData(Base64.encode(CharsetUtils.toUTF8Bytes(fileContent)));
                response.setId(simpleName + JAVA_FILE_SUFFIX);
            } else {
                response.setCode(-1);
                response.setMessage("反编译失败");
            }
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void decompilerJar(Decompiler decompiler, final String classFileName, final String className, final URL url) throws IOException {
        final String filePath = url.getFile();
        List<InputStream> jarFileStreams = Lists.newArrayList();

        int beginIndex = 0;
        if (filePath.startsWith(FILE_URL_PREFIX)) {
            beginIndex = 5;
        } else if (filePath.startsWith(JAR_FILE_URL_PREFIX)) {
            beginIndex = 9;
        }

        try (JarFile jarFile = new JarFile(filePath.substring(beginIndex, filePath.indexOf(JAR_FILE_URL_SPLITTER) + 4))) {
            Enumeration<JarEntry> entries = jarFile.entries();
            PathInfo pathInfo = new PathInfo(className);
            //处理内部类
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.isDirectory()) {
                    continue;
                }
                String name = jarEntry.getName();
                if (pathInfo.isInnerClass(name)) {
                    InputStream inputStream = jarFile.getInputStream(jarEntry);
                    jarFileStreams.add(inputStream);
                    decompiler.addStream(inputStream, name.replace("/", "."), JAR_FILE_URL_PREFIX + jarFile.getName() + "!/" + name);
                } else if (pathInfo.isTheClass(name)) {
                    InputStream inputStream = jarFile.getInputStream(jarEntry);
                    jarFileStreams.add(inputStream);
                    decompiler.addStream(inputStream, classFileName + CLASS_FILE_SUFFIX, url.getFile());
                }
            }
            decompiler.decompileContext();
        } finally {
            for (InputStream inputStream : jarFileStreams) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    logger.error("close jar input stream error", e);
                }
            }
        }
    }


    private List<File> findInnerClasses(File source) {
        final String fileName = source.getName();
        int endIndex = fileName.lastIndexOf(".");
        if (endIndex < 0) {
            endIndex = fileName.length();
        }
        final String className = fileName.substring(0, endIndex);
        return FileUtil.listFile(source.getParentFile(), new Predicate<File>() {
            @Override
            public boolean apply(File file) {
                return file != null && file.getName() != null && file.getName().startsWith(className + "$");
            }
        });
    }

    private static class PathInfo {
        private final String innerClassPathPrefix;
        private final String classPath;

        private PathInfo(String className) {
            String classPathWithoutSuffix = className.replace(".", "/");
            this.innerClassPathPrefix = classPathWithoutSuffix + "$";
            this.classPath = classPathWithoutSuffix + CLASS_FILE_SUFFIX;
        }

        boolean isInnerClass(String name) {
            return !Strings.isNullOrEmpty(name) && name.startsWith(innerClassPathPrefix);
        }

        boolean isTheClass(String name) {
            return !Strings.isNullOrEmpty(name) && name.equals(classPath);
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getMaxRunningMs() {
        return maxRunningMs;
    }
}
