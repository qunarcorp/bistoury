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

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.clientside.common.store.BistouryStore;
import qunar.tc.bistoury.common.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author leix.xie
 * @date 2019/8/24 01:27
 * @describe
 */
public class JarStorePathUtil {
    private static final Logger logger = BistouryLoggger.getLogger();

    private static final String STORE_PATH = BistouryStore.getStorePath("tomcat_webapp");

    private static final Manifest DEFAULT_MANIFEST = new Manifest("BOOT-INF/lib/", "BOOT-INF/classes/");

    private static final Splitter.MapSplitter MANINFSET_SPLITTER = Splitter.on("\n").trimResults().omitEmptyStrings().withKeyValueSeparator(":");

    private static final String SPRING_BOOT_CLASSES_KEY = "Spring-Boot-Classes";
    private static final String SPRING_BOOT_LIB_KEY = "Spring-Boot-Lib";

    private static Manifest maninest = null;

    static {
        File file = new File(STORE_PATH);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
    }

    public static String getJarStorePath() {
        return new File(STORE_PATH).getPath();
    }

    public static String getJarLibPath() {
        return System.getProperty("bistoury.jar.lib.path", new File(getJarStorePath(), getMavenInfo().getSpringBootLib()).getPath());
    }

    public static String getJarSourcePath() {
        return System.getProperty("bistoury.jar.source.path", new File(getJarStorePath(), getMavenInfo().getSpringBootClasses()).getPath());
    }

    private synchronized static Manifest getMavenInfo() {
        if (maninest == null) {
            maninest = getManifest();
            logger.info("user manifest: {}", maninest);
        }
        return maninest;
    }


    private static Manifest getManifest() {
        List<File> files = FileUtil.listFile(new File(getJarStorePath()), new Predicate<File>() {
            @Override
            public boolean apply(File input) {
                return "MANIFEST.MF".equalsIgnoreCase(input.getName());
            }
        });
        if (files == null || files.isEmpty()) {
            logger.warn("cannot find MANIFEST.MF，user default manifest: {}", DEFAULT_MANIFEST);
            return DEFAULT_MANIFEST;
        }
        for (File file : files) {
            try {
                String fileContent = FileUtil.readString(file, Charsets.UTF_8);
                Map<String, String> maninfsetMap = MANINFSET_SPLITTER.split(fileContent);
                String springBootClassesValue = maninfsetMap.get(SPRING_BOOT_CLASSES_KEY);
                String springBootLibValue = maninfsetMap.get(SPRING_BOOT_LIB_KEY);
                if (Strings.isNullOrEmpty(springBootClassesValue) || Strings.isNullOrEmpty(springBootLibValue)) {
                    continue;
                } else {
                    return new Manifest(springBootClassesValue, springBootLibValue);
                }
            } catch (IOException e) {
                //ignore
            }
        }
        logger.warn("read MANIFEST.MF fail, user default manifest: {}", DEFAULT_MANIFEST);
        return DEFAULT_MANIFEST;
    }

    private static class Manifest {
        private String springBootClasses;
        private String springBootLib;

        public Manifest(String springBootClasses, String springBootLib) {
            this.springBootClasses = springBootClasses;
            this.springBootLib = springBootLib;
        }

        public String getSpringBootClasses() {
            return springBootClasses;
        }

        public void setSpringBootClasses(String springBootClasses) {
            this.springBootClasses = springBootClasses;
        }

        public String getSpringBootLib() {
            return springBootLib;
        }

        public void setSpringBootLib(String springBootLib) {
            this.springBootLib = springBootLib;
        }

        @Override
        public String toString() {
            return "Manifest{" +
                    "springBootClasses='" + springBootClasses + '\'' +
                    ", springBootLib='" + springBootLib + '\'' +
                    '}';
        }
    }
}
