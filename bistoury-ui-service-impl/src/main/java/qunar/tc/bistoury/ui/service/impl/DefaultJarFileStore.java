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
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.collect.Maps;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.AsyncHttpClientHolder;
import qunar.tc.bistoury.serverside.configuration.DynamicConfig;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.serverside.configuration.local.LocalDynamicConfig;
import qunar.tc.bistoury.serverside.metrics.Metrics;
import qunar.tc.bistoury.serverside.util.BistouryFileStoreUtil;
import qunar.tc.bistoury.ui.exception.SourceFileNotFoundException;
import qunar.tc.bistoury.ui.model.MavenInfo;
import qunar.tc.bistoury.ui.service.JarFileStore;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author zhenyu.nie created on 2019 2019/4/25 19:21
 */
@Service
public class DefaultJarFileStore implements JarFileStore {

    private static final Logger logger = LoggerFactory.getLogger(DefaultJarFileStore.class);

    private static final AsyncHttpClient httpClient = AsyncHttpClientHolder.getInstance();

    private static final String URL_SEPARATOR = "/";

    private static final String JAR_SUFFIX = ".jar";

    private static final String SOURCE_JAR = "-sources" + JAR_SUFFIX;

    private static final String TEMP_SUFFIX = ".tmp";

    private static final String TEMP_JAR = SOURCE_JAR + TEMP_SUFFIX;

    private String storeDir = BistouryFileStoreUtil.getBistouryStore() + File.separator + "jar" + File.separator + "source";

    private String mavenHost;

    private int jarGuaranteePeriodDays;

    private LoadingCache<MavenInfo, String> cache;

    @PostConstruct
    public void init() {


        DynamicConfig<LocalDynamicConfig> dynamicConfig = DynamicConfigLoader.load("config.properties");
        dynamicConfig.addListener(config -> {
            mavenHost = config.getString("maven.nexus.url", "");
            jarGuaranteePeriodDays = config.getInt("jar.guarantee.period.days", 2);
        });

        this.cache = CacheBuilder
                .newBuilder()
                .expireAfterAccess(jarGuaranteePeriodDays, TimeUnit.DAYS)
                .removalListener((RemovalListener<MavenInfo, String>) notification -> {
                    File file = new File(notification.getValue());
                    if (file.exists()) {
                        if (!file.delete()) {
                            logger.warn("clear file [{}] fail", file.getAbsolutePath());
                        }
                    }
                })
                .build(new CacheLoader<MavenInfo, String>() {
                    @Override
                    public String load(MavenInfo mavenInfo) throws Exception {
                        return fetchSourceJar(mavenInfo);
                    }
                });

        ensureDirCreate(storeDir);
        loadExistJarFiles(storeDir);
        clearTempFiles(storeDir);
    }

    private void loadExistJarFiles(String dir) {
        try {
            Files.find(Paths.get(dir),
                    Integer.MAX_VALUE,
                    (path, attr) -> !attr.isDirectory() && path.toString().endsWith(JAR_SUFFIX))
                    .forEach((path) -> {
                        String absolutePath = path.toAbsolutePath().toString();
                        cache.put(parseMavenInfo(absolutePath), absolutePath);
                    });
        } catch (Exception e) {
            throw new IllegalStateException("load dir error, " + dir, e);
        }
    }

    private MavenInfo parseMavenInfo(String path) {
        File file = new File(path).getParentFile();
        String version = file.getName();
        file = file.getParentFile();
        String artifactId = file.getName();
        file = file.getParentFile();
        String groupId = file.getName();
        return new MavenInfo(artifactId, groupId, version);
    }

    private void clearTempFiles(String dir) {
        try {
            Files.find(Paths.get(dir),
                    Integer.MAX_VALUE,
                    (path, attr) -> !attr.isDirectory() && path.toString().endsWith(TEMP_SUFFIX))
                    .forEach((path) -> path.toFile().delete());
        } catch (Exception e) {
            throw new IllegalStateException("load dir error, " + dir, e);
        }
    }

    private void ensureDirCreate(String dir) {
        File file = new File(dir);
        if (!file.exists() || !file.isDirectory()) {
            boolean mkdirs = file.mkdirs();
            if (!mkdirs) {
                throw new RuntimeException("jar存储根目录创建失败，请创建：" + dir + " 目录后重试");
            }
        }
    }

    @Override
    public String getJarFile(MavenInfo mavenInfo) {
        String filePath = cache.getUnchecked(mavenInfo);
        if (fileExpired(filePath)) {
            cache.invalidate(mavenInfo);
            filePath = cache.getUnchecked(mavenInfo);
        }
        return filePath;
    }

    @Override
    public String getJarFileIfPresent(MavenInfo mavenInfo) {
        String filePath = cache.getIfPresent(mavenInfo);
        if (fileExpired(filePath)) {
            cache.invalidate(mavenInfo);
            return null;
        }
        return filePath;
    }

    private boolean fileExpired(String filePath) {
        return !Strings.isNullOrEmpty(filePath) && !new File(filePath).exists();
    }

    private String fetchSourceJar(MavenInfo mavenInfo) {
        try {

            if (Strings.isNullOrEmpty(mavenHost)) {
                throw new RuntimeException("maven 下载链接配置错误");
            }

            AsyncHttpClient.BoundRequestBuilder builder = httpClient.prepareGet(getUrl(mavenInfo));
            Response response = httpClient.executeRequest(builder.build()).get();
            if (response.getStatusCode() != 200) {
                logger.warn("getAll source code from maven repository fail, http code [{}]", response.getStatusCode());
                throw new RuntimeException();
            } else {
                InputStream inputStream = response.getResponseBodyAsStream();
                File tempFile = new File(getTempJarPath(mavenInfo));
                ensureDirExist(tempFile);

                try (FileOutputStream to = new FileOutputStream(tempFile)) {
                    ByteStreams.copy(inputStream, to);
                }

                //分析response Header，获取etag，解析文件hash
                String eTagInHeader = response.getHeader("ETag");
                ETag eTag = parseETag(eTagInHeader);
                if (eTag == null) {
                    logger.error("文件 hash 值解析错误, mavenInfo: {}, etag: {}", mavenInfo, eTagInHeader);
                    Metrics.counter("hash_parse_error").inc();
                    tempFile.delete();
                    throw new SourceFileNotFoundException("源文件下载错误, 文件hashValue值解析错误");
                }

                String hashValue = eTag.hashing(tempFile);
                if (!Objects.equals(eTag.getHashValue(), hashValue)) {
                    logger.error("文件 hash 值不匹配, mavenInfo: {}, algorithm: {}, etag: {}, file: {}", mavenInfo, eTag.getAlgorithm().name(), eTag.getHashValue(), hashValue);
                    Metrics.counter("hash_not_match").inc();
                    tempFile.delete();
                    throw new SourceFileNotFoundException(String.format("源文件下载错误，文件hashValue值不匹配, algorithm: %s, etag: %s, file: %s", eTag.getAlgorithm().name(), eTag.getHashValue(), hashValue));
                }

                File jarFile = new File(getSourceJarPath(mavenInfo));
                if (!tempFile.renameTo(jarFile)) {
                    logger.error("rename temp file error, temp [{}], target [{}]", tempFile, jarFile);
                    tempFile.delete();
                    if (!jarFile.exists()) {
                        throw new SourceFileNotFoundException("源文件下载失败");
                    }
                }

                return jarFile.getAbsolutePath();
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private void ensureDirExist(File tempFile) {
        File parentFile = tempFile.getParentFile();
        if (!parentFile.exists() || !parentFile.isDirectory()) {
            parentFile.mkdirs();
        }

        if (!parentFile.exists() || !parentFile.isDirectory()) {
            logger.warn("parent dir can not create, {}", parentFile);
            throw new IllegalStateException("parent dir can not create, " + parentFile);
        }
    }

    private String getSourceJarPath(MavenInfo mavenInfo) {
        return getJarFilePathPrefix(mavenInfo).append(SOURCE_JAR).toString();
    }

    private String getTempJarPath(MavenInfo mavenInfo) {
        return getJarFilePathPrefix(mavenInfo).append("-").append(UUID.randomUUID()).append(TEMP_JAR).toString();
    }

    private StringBuilder getJarFilePathPrefix(MavenInfo mavenInfo) {
        return new StringBuilder(storeDir).append(File.separator)
                .append(mavenInfo.getGroupId())
                .append(File.separator)
                .append(mavenInfo.getArtifactId())
                .append(File.separator)
                .append(mavenInfo.getVersion())
                .append(File.separator)
                .append(mavenInfo.getArtifactId()).append("-").append(mavenInfo.getVersion());
    }

    private String getUrl(MavenInfo mavenInfo) {
        return MessageFormat.format(mavenHost, mavenInfo.getGroupId().replace('.', '/'), mavenInfo.getArtifactId(), mavenInfo.getVersion());
    }

    private static ETag parseETag(String eTag) {
        if (Strings.isNullOrEmpty(eTag)) {
            return null;
        }

        int index = eTag.indexOf('{');
        if (index < 0) {
            return null;
        }
        int algorithmStartIndex = index + 1;
        int algorithmEndIndex = eTag.indexOf('{', algorithmStartIndex);
        if (algorithmEndIndex < 0) {
            return null;
        }
        String protocolName = eTag.substring(algorithmStartIndex, algorithmEndIndex).trim().toUpperCase();
        HashAlgorithm algorithm = HashAlgorithm.of(protocolName);
        if (algorithm == null) {
            return null;
        }

        int hashValueStartIndex = algorithmEndIndex + 1;
        int hashValueEndIndex = eTag.indexOf('}', hashValueStartIndex);
        if (hashValueEndIndex < 0) {
            return null;
        }
        String hashcode = eTag.substring(hashValueStartIndex, hashValueEndIndex).trim();
        if (Strings.isNullOrEmpty(hashcode)) {
            return null;
        }

        return new ETag(algorithm, hashcode);
    }

    private static class ETag {
        private HashAlgorithm algorithm;
        private String hashValue;

        public ETag(HashAlgorithm algorithm, String hashValue) {
            this.algorithm = algorithm;
            this.hashValue = hashValue;
        }

        public HashAlgorithm getAlgorithm() {
            return algorithm;
        }

        public String getHashValue() {
            return hashValue;
        }

        public String hashing(File file) {
            return algorithm.func.apply(file);
        }

        @Override
        public String toString() {
            return "ETag{" +
                    "algorithm=" + algorithm +
                    ", hash='" + hashValue + '\'' +
                    '}';
        }
    }

    private enum HashAlgorithm {

        MD5("MD5", hashWith(Hashing::md5)),
        SHA1("SHA1", hashWith(Hashing::sha1)),
        SHA256("SHA256", hashWith(Hashing::sha256));

        private static Map<String, HashAlgorithm> mapping = initMapping();

        private String algorithm;
        private Function<File, String> func;

        HashAlgorithm(String algorithm, Function<File, String> func) {
            this.algorithm = algorithm;
            this.func = func;
        }

        private static Map<String, HashAlgorithm> initMapping() {
            Map<String, HashAlgorithm> mapping = Maps.newHashMap();
            for (HashAlgorithm algorithm : HashAlgorithm.values()) {
                mapping.put(algorithm.name(), algorithm);
            }
            return mapping;
        }

        public static HashAlgorithm of(String value) {
            return mapping.get(value);
        }

        @Override
        public String toString() {
            return "HashAlgorithm{" +
                    "algorithm='" + algorithm + '\'' +
                    '}';
        }
    }

    private static Function<File, String> hashWith(Supplier<HashFunction> algorithm) {
        return (file) -> {
            try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
                Hasher hasher = algorithm.get().newHasher();
                int b;
                while ((b = inputStream.read()) != -1) {
                    hasher.putByte((byte) b);
                }
                return hasher.hash().toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
