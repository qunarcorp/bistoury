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

package qunar.tc.bistoury.instrument.agent;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author zhenyu.nie created on 2018 2018/11/19 19:45
 */
public class BistouryClassloader extends URLClassLoader {

    private ClassLoader magicClassLoader;


    private final ClassLoader resourceSearchParent = getSystemClassLoader().getParent();

    public BistouryClassloader(URL[] urls, ClassLoader classLoader) {
        super(urls, classLoader);
    }

    public void setMagicClassSetting(ClassLoader magicClassLoader) {
        this.magicClassLoader = magicClassLoader;
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return getResolvedClass(loadedClass, resolve);
        }

        // 优先从parent（SystemClassLoader）里加载系统类和spy类，避免抛出ClassNotFoundException
        if (name != null && (name.startsWith("sun.")
                || name.startsWith("java.")
                || name.startsWith("qunar.tc.bistoury.instrument.spy.")
                || name.startsWith("one.profiler."))) {
            return super.loadClass(name, resolve);
        }

        Class<?> magicClass = loadFromMagic(name);
        if (magicClass != null) {
            return getResolvedClass(magicClass, resolve);
        }

        try {
            Class<?> theClass = findClass(name);
            return getResolvedClass(theClass, resolve);
        } catch (Exception e) {
            // ignore
        }
        return super.loadClass(name, resolve);
    }

    @Override
    public URL getResource(String name) {
        URL url = resourceSearchParent.getResource(name);
        if (url != null) {
            return url;
        }
        return findResource(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Enumeration<URL> getResources(String name) throws IOException {
        Enumeration<URL>[] tmp = (Enumeration<URL>[]) new Enumeration<?>[2];
        tmp[0] = resourceSearchParent.getResources(name);
        tmp[1] = findResources(name);
        return new CompoundEnumeration<>(tmp);
    }

    private Class<?> getResolvedClass(Class<?> loadedClass, boolean resolve) {
        if (resolve) {
            resolveClass(loadedClass);
        }
        return loadedClass;
    }

    private Class<?> loadFromMagic(String name) {
        try {
            if (magicClassLoader != null && isMagicClass(name)) {
                return magicClassLoader.loadClass(name);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isMagicClass(String name) {
        return name != null && MagicClasses.isMagicClass(name);
    }

    /**
     * copy form {@link qunar.tc.bistoury.magic.classes.MagicClasses }，
     * Java11 使用反射调用{@link qunar.tc.bistoury.magic.classes.MagicClasses }时会出现 java.lang.ClassCircularityError: jdk/internal/reflect/MethodAccessorImpl
     */
    static class MagicClasses {

        private static final Set<String> MAGIC_CLASS_NAME_SET;

        private static final Set<String> MAGIC_CLASS_PREFIX_SET;

        static {
            Set<String> nameSet = new HashSet<>();
            nameSet.add("com.fasterxml.jackson.databind.ser.BeanSerializerFactory");
            nameSet.add("com.taobao.arthas.core.advisor.Enhancer");
            nameSet.add("com.taobao.arthas.core.shell.term.impl.Helper");

            Set<String> namePrefixSet = new HashSet<>();
            for (String name : nameSet) {
                namePrefixSet.add(name + "$");
            }

            MAGIC_CLASS_NAME_SET = nameSet;
            MAGIC_CLASS_PREFIX_SET = namePrefixSet;
        }

        public static boolean isMagicClass(String name) {
            if (name == null || name.isEmpty()) {
                return false;
            }

            if (MAGIC_CLASS_NAME_SET.contains(name)) {
                return true;
            }

            for (String prefix : MAGIC_CLASS_PREFIX_SET) {
                if (name.startsWith(prefix)) {
                    return true;
                }
            }

            return false;
        }
    }
}

final class CompoundEnumeration<E> implements Enumeration<E> {
    private final Enumeration<E>[] enums;
    private int index;

    public CompoundEnumeration(Enumeration<E>[] enums) {
        this.enums = enums;
    }

    private boolean next() {
        while (index < enums.length) {
            if (enums[index] != null && enums[index].hasMoreElements()) {
                return true;
            }
            index++;
        }
        return false;
    }

    public boolean hasMoreElements() {
        return next();
    }

    public E nextElement() {
        if (!next()) {
            throw new NoSuchElementException();
        }
        return enums[index].nextElement();
    }
}
