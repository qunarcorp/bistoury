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

package qunar.tc.bistoury.magic.loader;

import qunar.tc.bistoury.magic.classes.MagicClasses;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author zhenyu.nie created on 2018 2018/11/30 13:44
 * MagicClassLaoder用来加载自己修改了依赖jar包代码的类
 */
public class MagicClassLoader extends URLClassLoader {

    public MagicClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            if (resolve) {
                resolveClass(loadedClass);
            }
            return loadedClass;
        }

        if (MagicClasses.isMagicClass(name)) {
            Class<?> aClass = findClass(name);
            if (resolve) {
                resolveClass(aClass);
            }
            return aClass;
        } else {
            return super.loadClass(name, resolve);
        }
    }
}
