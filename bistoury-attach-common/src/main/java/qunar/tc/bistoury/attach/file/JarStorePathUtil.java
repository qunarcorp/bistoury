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

import qunar.tc.bistoury.clientside.common.store.BistouryStore;

import java.io.File;

/**
 * @author leix.xie
 * @date 2019/8/24 01:27
 * @describe
 */
public class JarStorePathUtil {

    private static final String STORE_PATH = BistouryStore.getStorePath("tomcat_webapp");

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
        return System.getProperty("bistoury.jar.lib.path", new File(getJarStorePath(), "BOOT-INF/lib/").getPath());
    }

    public static String getJarSourcePath() {
        return System.getProperty("bistoury.jar.source.path", new File(getJarStorePath(), "BOOT-INF/classes/").getPath());
    }
}
