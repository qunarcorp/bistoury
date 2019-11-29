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

/**
 * @author leix.xie
 * @date 2019-07-22 15:46
 * @describe
 */
public class BistouryStore {

    private static final String DEFAULT_CHILD = "default";
    private static final String STORE_PATH;

    static {
        String path = System.getProperty("bistoury.store.path", null);

        if (path == null) {
            path = System.getProperty("catalina.base");
            if (path == null) {
                path = System.getProperty("java.io.tmpdir");
            }
            path = path + File.separator + "cache";
            System.setProperty("bistoury.store.path", path);
        }
        STORE_PATH = path;
    }

    public static String getStorePath(final String child) {
        return FileUtil.dealPath(STORE_PATH, child);
    }

    public static volatile String PROFILER_ROOT_PATH = BistouryStore.getStorePath("bistoury-profiler");

    public static volatile String PROFILER_TEMP_PATH = BistouryStore.getStorePath("bistoury-profiler" + File.separator + "tmp");

    public static String getDefaultStorePath() {
        return getStorePath(DEFAULT_CHILD);
    }
}
