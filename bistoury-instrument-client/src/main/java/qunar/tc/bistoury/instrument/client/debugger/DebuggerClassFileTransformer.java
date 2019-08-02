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

package qunar.tc.bistoury.instrument.client.debugger;

import com.taobao.middleware.logger.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.CheckClassAdapter;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.instrument.client.common.ClassFileBuffer;
import qunar.tc.bistoury.instrument.client.location.ResolvedSourceLocation;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

/**
 * @author keli.wang
 * @since 2017/3/15
 */
class DebuggerClassFileTransformer extends Transformer {
    private static final Logger LOG = BistouryLoggger.getLogger();

    private final ClassFileBuffer classFileBuffer;
    private final String source;
    private final String debugClassName;

    DebuggerClassFileTransformer(ClassFileBuffer classFileBuffer, String source, ResolvedSourceLocation location) {
        this.classFileBuffer = classFileBuffer;
        this.source = source;
        this.debugClassName = signatureToClassName(location.getClassSignature());
    }

    @Override
    protected byte[] transform(final String className,
                               final Class<?> classBeingRedefined,
                               final ProtectionDomain protectionDomain,
                               final byte[] classBytes)
            throws IllegalClassFormatException {
        if (!Objects.equals(className, debugClassName)) {
            return null;
        }
        LOG.info("debug class: {}", className);
        Lock lock = classFileBuffer.getLock();
        lock.lock();
        try {
            final ClassReader classReader = new ClassReader(classFileBuffer.getClassBuffer(classBeingRedefined, classBytes));
            final ClassMetadata classMetadata = new ClassMetadata();
            classReader.accept(new MetadataCollector(classMetadata), ClassReader.SKIP_FRAMES);

            final ClassWriter classWriter = new ClassWriter(computeFlag(classReader));
            final ClassVisitor classVisitor = new DebuggerClassVisitor(new CheckClassAdapter(classWriter), source, classMetadata);
            classReader.accept(classVisitor, ClassReader.SKIP_FRAMES);
            byte[] bytes = classWriter.toByteArray();
            classFileBuffer.setClassBuffer(classBeingRedefined, bytes);
            return bytes;
        } finally {
            lock.unlock();
        }
    }

    private String signatureToClassName(final String signature) {
        return Type.getType(signature).getInternalName();
    }
}
