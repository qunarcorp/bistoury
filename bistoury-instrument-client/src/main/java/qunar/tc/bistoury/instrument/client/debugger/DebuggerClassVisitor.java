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

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author keli.wang
 * @since 2017/3/15
 */
class DebuggerClassVisitor extends ClassVisitor {
    private final String source;
    private final ClassMetadata classMetadata;

    private String className;

    public DebuggerClassVisitor(final ClassVisitor cv,
                                final String source,
                                final ClassMetadata classMetadata) {
        super(Opcodes.ASM5, cv);
        this.source = source;
        this.classMetadata = classMetadata;
    }

    @Override
    public void visit(final int version,
                      final int access,
                      final String name,
                      final String signature,
                      final String superName,
                      final String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
    }

    @Override
    public MethodVisitor visitMethod(final int access,
                                     final String name,
                                     final String desc,
                                     final String signature,
                                     final String[] exceptions) {
        final MethodVisitor originalMV =
                super.visitMethod(access, name,
                        desc, signature,
                        exceptions);

        return new DebuggerMethodVisitor(
                source, className,
                name, desc,
                access,
                classMetadata, originalMV);
    }
}
