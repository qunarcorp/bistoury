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

package qunar.tc.bistoury.attach.arthas.config;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.arthas.instrument.InstrumentClient;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.attach.file.FileOperateFactory;
import qunar.tc.bistoury.attach.file.bean.FileBean;
import qunar.tc.bistoury.clientside.common.meta.MetaStore;
import qunar.tc.bistoury.clientside.common.meta.MetaStores;
import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;

import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author: leix.xie
 * @date: 2019/3/4 20:58
 * @describe：
 */
public class AppConfigClient implements InstrumentClient {

    private static final Logger logger = BistouryLoggger.getLogger();

    private static final String DEFAULT_EXCLUSION_SUFFIX_LINE = "class,vm,css,js,jar";

    private static final String POINT = ".";

    private static final Splitter COMMA_SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();

    private static final String BASE_PATH = System.getProperty("catalina.base");

    private static final Set<String> DEFAULT_EXCLUSION_FILE_SUFFIX = parseExclusionFileSuffix(DEFAULT_EXCLUSION_SUFFIX_LINE);

    private final URI uri;
    private MetaStore metaStore = MetaStores.getMetaStore();
    private List<String> filesPath = new ArrayList<>();

    public AppConfigClient(InstrumentInfo instrumentInfo) {
        logger.info("start init app config client");
        URI theUri = null;
        try {
            URL resource = instrumentInfo.getSystemClass().getClassLoader().getResource("/");
            theUri = resource.toURI();
            logger.info("init app config client success");
        } catch (Throwable e) {
            logger.error("", "app config client init error", e);
        }
        uri = theUri;
    }

    /**
     * 获取应用配置文件
     *
     * @return
     */
    public synchronized List<FileBean> listAppConfigFiles() {
        filesPath = new ArrayList<>();
        if (uri == null) {
            return Collections.emptyList();
        }
        List<FileBean> result = new ArrayList<>();
        final Set<String> exclusionFileSuffix = getExclusionFileSuffix();
        final Set<String> exclusionFile = getExclusionFile();
        List<FileBean> webAppConfigFiles = FileOperateFactory.listFiles(exclusionFileSuffix, exclusionFile, Paths.get(uri.toString()).getParent().toString());
        List<FileBean> tomcatConfigFiles = FileOperateFactory.listFiles(exclusionFileSuffix, exclusionFile, BASE_PATH + "/conf");
        result.addAll(webAppConfigFiles);
        result.addAll(tomcatConfigFiles);
        for (FileBean fileBean : result) {
            filesPath.add(fileBean.getName());
        }
        return result;
    }

    public synchronized String queryFileByPath(String path) {
        if (Strings.isNullOrEmpty(path)) {
            return null;
        }
        if (filesPath.contains(path)) {
            return FileOperateFactory.getFile(path);
        }
        throw new RuntimeException("仅支持获取列表中文件的内容！！！");
    }

    private Set<String> getExclusionFileSuffix() {
        String fileSuffixes = metaStore.getStringProperty("app.config.exclusion.file.suffix");
        if (Strings.isNullOrEmpty(fileSuffixes)) {
            return DEFAULT_EXCLUSION_FILE_SUFFIX;
        }

        return parseExclusionFileSuffix(fileSuffixes);
    }

    private static Set<String> parseExclusionFileSuffix(String fileSuffixes) {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        Iterable<String> exclusionFileSuffix = COMMA_SPLITTER.split(fileSuffixes);
        for (String suffix : exclusionFileSuffix) {
            builder.add(POINT + suffix);
        }
        return builder.build();
    }

    private Set<String> getExclusionFile() {
        String files = metaStore.getStringProperty("app.config.exclusion.file.equal");
        if (Strings.isNullOrEmpty(files)) {
            return ImmutableSet.of();
        }
        return ImmutableSet.copyOf(COMMA_SPLITTER.split(files));
    }

    @Override
    public synchronized void destroy() {
        filesPath.clear();
    }
}
