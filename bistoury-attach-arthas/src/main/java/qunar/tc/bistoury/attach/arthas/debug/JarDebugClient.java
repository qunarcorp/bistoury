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

package qunar.tc.bistoury.attach.arthas.debug;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.arthas.instrument.InstrumentClient;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.attach.file.FileOperateFactory;
import qunar.tc.bistoury.attach.file.URLUtil;
import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author: leix.xie
 * @date: 2019/2/28 10:46
 * @describe：
 */
public class JarDebugClient implements InstrumentClient {

    private static final Logger logger = BistouryLoggger.getLogger();

    private static final String FILE_PROTOCOL = "file:";

    private final InstrumentInfo instrumentInfo;

    private volatile Map<String, ClassInfo> classNameToClassInfoMapping = Maps.newHashMap();

    JarDebugClient(InstrumentInfo instrumentInfo) {
        this.instrumentInfo = instrumentInfo;
        logger.info("start init jar debugg client");
        try {
            classNameToClassInfoMapping = initAllClassInfo(instrumentInfo, true);
            logger.info("success init jar decompiler client");
        } catch (Exception e) {
            destroy();
            logger.error("", "error init jar decompiler client", e);
            throw new IllegalStateException("jar decompiler client init error", e);
        }
    }

    private Map<String, ClassInfo> initAllClassInfo(InstrumentInfo instrumentInfo, boolean isLoadAll) {
        Class[] loadedClasses = instrumentInfo.getInstrumentation().getAllLoadedClasses();
        Map<String, ClassInfo> classInfoMap;
        if (isLoadAll) {
            classInfoMap = Maps.newHashMap();
        } else {
            classInfoMap = Maps.newHashMap(classNameToClassInfoMapping);
        }
        Map<String, JarFile> jarFileMap = new HashMap<>();
        Map<String, Optional<Properties>> mavenInfoMap = new HashMap<>();
        try {
            for (Class clazz : loadedClasses) {
                final String clazzName = clazz.getName();
                if (Strings.isNullOrEmpty(clazzName) || clazzName.startsWith("[") || clazzName.contains("$")) {
                    continue;
                }

                if (!isLoadAll && classInfoMap.containsKey(clazzName)) {
                    continue;
                }

                final ClassLoader classLoader = clazz.getClassLoader();
                if (classLoader == null || InstrumentInfo.IGNORE_CLASS.contains(classLoader.getClass().getName())) {
                    continue;
                }
                ClassInfo classInfo = getClassInfo(clazz, jarFileMap, mavenInfoMap);
                if (classInfo != null) {
                    classInfoMap.put(clazzName, classInfo);
                }
            }
            return ImmutableMap.copyOf(classInfoMap);
        } finally {
            for (Map.Entry<String, JarFile> entry : jarFileMap.entrySet()) {
                try {
                    entry.getValue().close();
                } catch (IOException e) {
                    logger.error("", "close jar file error，{}", entry.getKey(), e);
                }
            }
        }
    }

    public Set<String> getAllClass() {
        return ImmutableSet.copyOf(classNameToClassInfoMapping.keySet());
    }

    /**
     * 所有类全部重新加载
     *
     * @return
     */
    public boolean reloadAllClass() {
        logger.info("begin reload all class");
        classNameToClassInfoMapping = initAllClassInfo(instrumentInfo, true);
        logger.info("end reload all class");
        return true;
    }

    /**
     * 只加载没有加载的类
     *
     * @return
     */
    public boolean reLoadNewClass() {
        logger.info("begin reload new class");
        classNameToClassInfoMapping = initAllClassInfo(instrumentInfo, false);
        logger.info("end reload new class");
        return true;
    }


    public ClassInfo getClassPath(final String className) {
        return classNameToClassInfoMapping.get(className);
    }

    private ClassInfo getClassInfo(Class clazz, Map<String, JarFile> jarFileMap, Map<String, Optional<Properties>> mavenInfoMap) {
        final String classPath = clazz.getName().replace('.', '/') + ".class";
        try {
            ClassInfo classInfo = getClassInfoBySource(clazz, classPath, jarFileMap, mavenInfoMap);
            if (classInfo == null) {
                classInfo = getClassInfoByDomain(clazz, classPath, jarFileMap, mavenInfoMap);
            }

            return classInfo;
        } catch (Exception e) {
            logger.error("", "get class info error, class: {}, class path: {}", clazz, classPath, e);
            return null;
        }
    }

    private ClassInfo getClassInfoBySource(Class clazz, final String classPath, Map<String, JarFile> jarFileMap, Map<String, Optional<Properties>> mavenInfoMap) {
        URL url = ClassLoader.getSystemResource(classPath);
        if (url != null) {
            return generateClassInfo(url, clazz, jarFileMap, mavenInfoMap);
        }
        url = clazz.getClassLoader().getResource(classPath);
        if (url != null) {
            return generateClassInfo(url, clazz, jarFileMap, mavenInfoMap);
        }
        return null;
    }

    private ClassInfo getClassInfoByDomain(Class clazz, final String classPath, Map<String, JarFile> jarFileMap, Map<String, Optional<Properties>> mavenInfoMap) {
        CodeSource source = clazz.getProtectionDomain().getCodeSource();
        if (source != null) {
            URL url = source.getLocation();
            if (url != null) {
                String ur = url.toString();
                ClassInfo classInfo = generateClassInfo(url, clazz, jarFileMap, mavenInfoMap);
                if (ur.endsWith(".jar")) {
                    classInfo.setClassPath(ur + "!" + File.separator + classPath);
                } else {
                    classInfo.setClassPath(ur + classPath);
                }
                return classInfo;
            }
        }
        return null;
    }

    private ClassInfo generateClassInfo(URL url, final Class clazz, Map<String, JarFile> jarFileMap, Map<String, Optional<Properties>> mavenInfoMap) {
        final ClassInfo classInfo = new ClassInfo();
        String newUrl = FileOperateFactory.replaceJarWithUnPackDir(url.toString());
        classInfo.setClassPath(newUrl);
        try {
            url = new URL(newUrl);
            if (isJarFile(url)) {
                final CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
                String path;
                if (codeSource == null || Strings.isNullOrEmpty(path = URLDecoder.decode(codeSource.getLocation().getPath(), "utf-8"))) {
                    return classInfo;
                }

                URI uri = new URI(path);
                if (Strings.isNullOrEmpty(uri.getScheme())) {
                    path = FileOperateFactory.replaceJarWithUnPackDir(FILE_PROTOCOL + path);
                } else {
                    path = FileOperateFactory.replaceJarWithUnPackDir(path);
                }
                path = URLUtil.removeProtocol(path);

                JarFile jarFile = jarFileMap.get(path);
                if (jarFile == null) {
                    jarFile = new JarFile(path);
                    jarFileMap.put(path, jarFile);
                }

                classInfo.setJarName(jarFile.getName());
                Optional<Properties> mavenInfo = mavenInfoMap.get(path);
                if (mavenInfo == null) {
                    Properties properties = getMavenInfo(jarFile);
                    if (properties != null) {
                        classInfo.setMavenInfo(properties);
                        classInfo.setMaven(true);
                        mavenInfoMap.put(path, Optional.of(properties));
                    } else {
                        mavenInfoMap.put(path, Optional.<Properties>absent());
                    }
                } else {
                    if (mavenInfo.isPresent()) {
                        classInfo.setMavenInfo(mavenInfo.get());
                        classInfo.setMaven(true);
                    }
                }
            }
            return classInfo;
        } catch (Exception e) {
            logger.error("", "{} get pom.properties fail,", url, e);
            return classInfo;
        }
    }

    private Properties getMavenInfo(JarFile jarFile) throws IOException {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (jarEntry.getName().endsWith("/pom.properties")) {
                Properties properties = readJarFile(jarFile, jarEntry);
                return properties;
            }
        }
        return null;
    }

    private Properties readJarFile(JarFile jarFile, JarEntry jarEntry) throws IOException {
        try (InputStream stream = jarFile.getInputStream(jarEntry)) {
            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        }
    }

    private boolean isJarFile(URL resource) {
        return "jar".equalsIgnoreCase(resource.getProtocol()) || resource.getFile().endsWith(".jar");
    }

    @Override
    public void destroy() {
    }
}
