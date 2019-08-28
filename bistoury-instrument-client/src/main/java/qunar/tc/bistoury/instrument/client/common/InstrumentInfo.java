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

package qunar.tc.bistoury.instrument.client.common;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.taobao.middleware.logger.Logger;
import org.objectweb.asm.Type;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.instrument.client.classpath.AppClassPathSupplier;
import qunar.tc.bistoury.instrument.client.classpath.AppLibClassSupplier;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

/**
 * @author zhenyu.nie created on 2019 2019/2/18 19:55
 */
public class InstrumentInfo {
    private static final Logger logger = BistouryLoggger.getLogger();
    public static final Set<String> IGNORE_CLASS = ImmutableSet.of("sun.reflect.DelegatingClassLoader", "qunar.tc.bistoury.instrument.agent.BistouryClassloader");

    private final Instrumentation instrumentation;

    private final Lock lock;

    private final ClassFileBuffer classFileBuffer;

    private final AppLibClassSupplier appLibClassSupplier;

    private final AppClassPathSupplier appClassPathSupplier;

    private final Set<Class> transformedClasses = Sets.newHashSet();

    private volatile boolean running = true;

    public InstrumentInfo(
            Instrumentation instrumentation,
            Lock lock,
            AppLibClassSupplier appLibClassSupplier,
            AppClassPathSupplier appClassPathSupplier,
            ClassFileBuffer classFileBuffer) {
        this.instrumentation = instrumentation;
        this.lock = lock;
        this.appLibClassSupplier = appLibClassSupplier;
        this.appClassPathSupplier = appClassPathSupplier;
        this.classFileBuffer = classFileBuffer;
    }

    public Instrumentation getInstrumentation() {
        return instrumentation;
    }

    public Lock getLock() {
        return lock;
    }

    public boolean isRunning() {
        return running;
    }

    public Class<?> getSystemClass() {
        return appLibClassSupplier.get();
    }

    public List<String> getClassPath() {
        return appClassPathSupplier.get();
    }

    public ClassFileBuffer getClassFileBuffer() {
        return classFileBuffer;
    }

    public Class<?> signatureToClass(final String signature)
            throws ClassNotFoundException {
        String className = Type.getType(signature).getClassName();

        Class[] classes = instrumentation.getAllLoadedClasses();
        for (Class clazz : classes) {
            ClassLoader classLoader = clazz.getClassLoader();
            if (className.equals(clazz.getName()) && classLoader != null && !IGNORE_CLASS.contains(classLoader.getClass().getName())) {
                return clazz;
            }
        }
        throw new ClassNotFoundException("can not found class: " + className);
    }

    public void addTransformedClasses(Class<?> clazz) {
        lock.lock();
        try {
            transformedClasses.add(clazz);
        } finally {
            lock.unlock();
        }
    }

    public void reset() {
        lock.lock();
        try {
            running = false;
            if (!transformedClasses.isEmpty()) {
                instrumentation.addTransformer(resetTransformer, true);
                instrumentation.retransformClasses(transformedClasses.toArray(new Class<?>[]{}));
            }
            classFileBuffer.destroy();
        } catch (Throwable e) {
            logger.error("", "reset instrumentInfo error", e);
        } finally {
            if (!transformedClasses.isEmpty()) {
                instrumentation.removeTransformer(resetTransformer);
                transformedClasses.clear();
            }
            lock.unlock();
        }
    }

    private static final ClassFileTransformer resetTransformer = new ClassFileTransformer() {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            return null;
        }
    };
}
