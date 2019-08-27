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

import java.lang.instrument.Instrumentation;

/**
 * @author zhenyu.nie created on 2019 2019/3/4 16:22
 */
public class DefaultAppLibClassSupplier implements AppLibClassSupplier {

    private final Instrumentation instrumentation;

    public DefaultAppLibClassSupplier(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    @Override
    public Class<?> get() {
        return findOneAppLibClass(instrumentation);
    }

    private static Class<?> findOneAppLibClass(Instrumentation instrumentation) {
        Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
        final String libClass = System.getProperty("bistoury.app.lib.class");
        if (Strings.isNullOrEmpty(libClass)) {
            System.err.println("can not find lib class, [" + libClass + "]");
            throw new IllegalStateException("can not find lib class, [" + libClass + "]");
        }
        for (Class clazz : allLoadedClasses) {
            if (libClass.equals(clazz.getName())) {
                return clazz;
            }
        }
        System.err.println("can not find lib class, [" + libClass + "]");
        throw new IllegalStateException("can not find lib class, [" + libClass + "]");
    }
}
