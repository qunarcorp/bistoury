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

package qunar.tc.bistoury.instrument.client.classpath;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.List;

/**
 * @author zhenyu.nie created on 2019 2019/2/19 14:44
 */
public class WebAppClassPathSupplier implements AppClassPathSupplier {

    private final List<String> classPath;

    public WebAppClassPathSupplier(String appLibPath) {
        this.classPath = findClassPath(appLibPath);
    }

    @Override
    public List<String> get() {
        return classPath;
    }

    private List<String> findClassPath(String appLibPath) {
        File libFile = new File(appLibPath);
        final File webRoot = libFile.getParentFile();
        String sourcePath = new File(webRoot, "classes").getAbsolutePath();
        String libPath = libFile.getAbsolutePath();
        return ImmutableList.of(sourcePath, libPath);
    }
}
