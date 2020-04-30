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

package qunar.tc.bistoury.attach.arthas.instrument;

import com.taobao.arthas.core.advisor.Enhancer;
import com.taobao.arthas.core.util.affect.EnhancerAffect;
import qunar.tc.bistoury.instrument.client.common.ClassFileBuffer;

import java.io.File;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * @author zhenyu.nie created on 2019 2019/2/19 15:47
 */
public class DefaultClassFileBuffer implements ClassFileBuffer {

    private static final ClassFileBuffer INSTANCE = new DefaultClassFileBuffer();

    private final Map<Class<?>, byte[]> classBytesCache;

    private DefaultClassFileBuffer() {
        classBytesCache = Enhancer.classBytesCache;

    }

    public static ClassFileBuffer getInstance() {
        return INSTANCE;
    }

    @Override
    public byte[] getClassBuffer(Class clazz, byte[] defaultBuffer) {
        byte[] bytes = classBytesCache.get(clazz);
        if (bytes != null && bytes.length > 0) {
            return bytes;
        }
        return defaultBuffer;
    }

    @Override
    public void setClassBuffer(Class clazz, byte[] buffer) {
        dumpClassIfNecessary(clazz.getName(), buffer);
        classBytesCache.put(clazz, buffer);
    }

    private void dumpClassIfNecessary(String className, byte[] buffer) {
        Enhancer.dumpClassIfNecessary(className.replace(".", File.separator), buffer, new EnhancerAffect());
    }

    @Override
    public Lock getLock() {
        return Enhancer.lock;
    }

    @Override
    public void destroy() {
        classBytesCache.clear();
    }
}
