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

package qunar.tc.bistoury.instrument.client.monitor;

import com.taobao.middleware.logger.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AnalyzerAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;
import qunar.tc.bistoury.attach.common.BistouryLoggger;

/**
 * @author: leix.xie
 * @date: 2018/12/26 19:34
 * @describe：
 */
public class MonitorClassVisitor extends ClassVisitor {
    private static final Logger logger = BistouryLoggger.getLogger();
    private String className;
    private String methodName;
    private String methodDesc;

    MonitorClassVisitor(final ClassVisitor cv, final String methodName, final String methodDesc) {
        super(Opcodes.ASM7, cv);
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    @Override
    public void visit(final int version,
                      final int access,
                      final String name,
                      final String signature,
                      final String superName,
                      final String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        final MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(methodName) && desc.equals(methodDesc)) {
            logger.debug("visit method, name: {}, desc: {}", name, desc);

            MonitorMethodVisitor monitorMV = new MonitorMethodVisitor(methodVisitor, access, name, desc, className);

            AnalyzerAdapter analyzerAdapter = new AnalyzerAdapter(className, access, name, desc, monitorMV);
            monitorMV.setAnalyzerAdapter(analyzerAdapter);
            LocalVariablesSorter localVariablesSorter = new LocalVariablesSorter(access, desc, analyzerAdapter);
            monitorMV.setLocalVariablesSorter(localVariablesSorter);

            return localVariablesSorter;
        } else {
            return methodVisitor;
        }
    }
}
