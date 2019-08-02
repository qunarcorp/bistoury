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

import com.google.common.collect.Maps;
import org.objectweb.asm.*;

import java.util.Map;


/**
 * @author keli.wang
 */
final class MetadataCollector extends ClassVisitor {
    private static final int ASM_VERSION = Opcodes.ASM5;
    private final ClassMetadata classMetadata;

    public MetadataCollector(final ClassMetadata classMetadata) {
        super(ASM_VERSION);
        this.classMetadata = classMetadata;
    }

    @Override
    public FieldVisitor visitField(final int access,
                                   final String name,
                                   final String desc,
                                   final String signature,
                                   final Object value) {
        classMetadata.addField(new ClassField(access, name, desc));
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(final int access,
                                     final String methodName,
                                     final String desc,
                                     final String signature,
                                     final String[] exceptions) {
        final MethodVisitor superMV = super.visitMethod(access,
                methodName,
                desc,
                signature,
                exceptions);

        final String methodUniqueName = methodName + desc;
        return new MethodVisitor(ASM_VERSION, superMV) {
            private final Map<String, Integer> labelLineMapping = Maps.newHashMap();

            @Override
            public void visitLineNumber(final int line, final Label start) {
                labelLineMapping.put(start.toString(), line);
            }

            @Override
            public void visitLocalVariable(final String name,
                                           final String desc,
                                           final String signature,
                                           final Label start,
                                           final Label end,
                                           final int index) {
                super.visitLocalVariable(name, desc, signature, start, end, index);
                classMetadata.addVariable(methodUniqueName,
                        new LocalVariable(
                                name, desc,
                                labelLine(start),
                                labelLine(end),
                                index));
            }

            private int labelLine(final Label label) {
                final String labelId = label.toString();
                if (labelLineMapping.containsKey(labelId)) {
                    return labelLineMapping.get(label.toString());
                }
                return Integer.MAX_VALUE;
            }
        };
    }
}
