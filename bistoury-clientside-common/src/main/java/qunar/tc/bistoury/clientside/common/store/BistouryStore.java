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

package qunar.tc.bistoury.clientside.common.store;

import qunar.tc.bistoury.common.FileUtil;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author leix.xie
 * @date 2019-07-22 15:46
 * @describe
 */
public class BistouryStore {

    private static final String DEFAULT_PATH = "/tmp/bistoury/store";

    private static final String DEFAULT_CHILD = "default";
    private static final String STORE_PATH;

    private static final AtomicReference<String> DEFAULT_PROFILER_STORE_PATH;

    private static final String DEFAULT_PROFILER_ROOT_DIR = "bistoury-profiler";

    private static final String DEFAULT_PROFILER_TEMP_DIR = "bistoury-profiler-tmp";

    static {
        new File(DEFAULT_PATH).mkdirs();
        String path = System.getProperty("bistoury.store.path", DEFAULT_PATH);

        if (path == null) {
            path = System.getProperty("catalina.base");
            if (path == null) {
                path = System.getProperty("java.io.tmpdir");
            }
            path = path + File.separator + "cache";
            System.setProperty("bistoury.store.path", path);
        }
        STORE_PATH = path;
        DEFAULT_PROFILER_STORE_PATH = new AtomicReference<>(path);
        FileUtil.ensureDirectoryExists(STORE_PATH);
    }

    public static String getStorePath(final String child) {
        return FileUtil.dealPath(STORE_PATH, child);
    }

    public static String getRootStorePath() {
        return STORE_PATH;
    }

    public static String getDumpFileStorePath() {
        return getStorePath("dump");
    }

    public static String getDefaultStorePath() {
        return getStorePath(DEFAULT_CHILD);
    }

    public static void changeProfilerStorePath(String profilerStorePath) {
        //todo 只允许修改一次
        DEFAULT_PROFILER_STORE_PATH.compareAndSet(STORE_PATH, profilerStorePath);
    }

    public static final String DEFAULT_PROFILER_ROOT_PATH = FileUtil.dealPath(DEFAULT_PROFILER_STORE_PATH.get(), DEFAULT_PROFILER_ROOT_DIR);

    public static final String DEFAULT_PROFILER_TEMP_PATH = FileUtil.dealPath(DEFAULT_PROFILER_STORE_PATH.get(), DEFAULT_PROFILER_TEMP_DIR);

    public static String getProfilerStorePath() {
        return DEFAULT_PROFILER_STORE_PATH.get();
    }

    public static String getProfilerRootPath() {
        return FileUtil.dealPath(DEFAULT_PROFILER_STORE_PATH.get(), DEFAULT_PROFILER_ROOT_DIR);
    }

    public static String getProfilerTempPath() {
        return FileUtil.dealPath(DEFAULT_PROFILER_STORE_PATH.get(), DEFAULT_PROFILER_TEMP_DIR);
    }

}
