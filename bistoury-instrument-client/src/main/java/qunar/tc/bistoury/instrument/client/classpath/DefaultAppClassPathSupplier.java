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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import qunar.tc.bistoury.attach.file.FileOperateFactory;
import qunar.tc.bistoury.attach.file.JarStorePathUtil;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author zhenyu.nie created on 2019 2019/2/19 20:03
 */
public class DefaultAppClassPathSupplier implements AppClassPathSupplier {

    private final AppClassPathSupplier supplier;

    public DefaultAppClassPathSupplier(AppLibClassSupplier appLibClassSupplier) {
        Class<?> appLibClass = appLibClassSupplier.get();
        URL url = appLibClass
                .getProtectionDomain()
                .getCodeSource()
                .getLocation();

        //调用该方法触发解压
        FileOperateFactory.replaceJarWithUnPackDir(url.toString());

        String libJarPath = url.getPath();
        String appLibPath = new File(libJarPath).getParentFile().getAbsolutePath();

        String appSourcePath = System.getProperty("bistoury.app.classes.path");
        if (!Strings.isNullOrEmpty(appSourcePath)) {
            //这两个路径用于spring boot，springboot会先将文件解压后放在缓存文件夹下，读取时可以从里面读取
            String jarLibPath = JarStorePathUtil.getJarLibPath();
            String jarSourcePath = JarStorePathUtil.getJarSourcePath();
            ImmutableList<String> list = ImmutableList.of(appLibPath, appSourcePath, jarLibPath, jarSourcePath);
            supplier = new SettableAppClassPathSupplier(list);
        } else {
            Iterator<AppClassPathSupplierFactory> factoryIterator = ServiceLoader.load(AppClassPathSupplierFactory.class).iterator();
            if (factoryIterator.hasNext()) {
                supplier = factoryIterator.next().create(appLibPath);
            } else {
                supplier = new WebAppClassPathSupplier(appLibPath);
            }
        }
    }

    @Override
    public List<String> get() {
        return supplier.get();
    }
}
