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

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import qunar.tc.bistoury.instrument.client.location.Location;
import qunar.tc.bistoury.instrument.spy.BistourySpys1;

import java.util.Collection;

/**
 * @author keli.wang
 * @since 2017/3/15
 * DefaultDebugger形式使用的字节码修改类
 */
class DebuggerMethodVisitor extends AdviceAdapter {

    private static final String SPY_NAME = Type.getInternalName(BistourySpys1.class);
    private static final String THROWABLE_INTERNALNAME = Type.getInternalName(Throwable.class);

    private static final String DEFAULT_DEBUG_ADDKV_DESC = "(Ljava/lang/String;Ljava/lang/Object;)V";
    private static final String DUMP_METHOD_DESC = "(Ljava/lang/String;I)V";
    private static final String ENDRECEIVE_METHOD_DESC = "(Ljava/lang/String;I)V";

    private final String source;
    private final String className;
    private final String methodUniqueName;
    private final int access;
    private final ClassMetadata classMetadata;

    DebuggerMethodVisitor(final String source,
                          final String className,
                          final String methodName,
                          final String desc,
                          final int access,
                          final ClassMetadata classMetadata,
                          final MethodVisitor mv) {
        super(ASM5, mv, access, methodName, desc);
        this.source = source;
        this.className = className;
        this.methodUniqueName = methodName + desc;
        this.access = access;
        this.classMetadata = classMetadata;
    }

    /**
     * @param line  a line number. This number refers to the source file from
     *              which the class was compiled.
     * @param start the first instruction corresponding to this line number.
     */
    @Override
    public void visitLineNumber(final int line, final Label start) {
        super.visitLineNumber(line, start);
        Location location = new Location(source, line);
        if (GlobalDebugContext.hasBreakpointSet(location)) {
            final Label breakpointLabel = new Label();
            breakpointSwitch(source, line, breakpointLabel);
            captureSnapshot(line);
            isHit(source, line, breakpointLabel);
            processForBreakpoint(source, line);
            super.visitLabel(breakpointLabel);
        }
    }

    private void breakpointSwitch(String source, int line, Label breakpointLabel) {
        super.visitLdcInsn(source);
        super.visitLdcInsn(line);
        super.visitMethodInsn(INVOKESTATIC, SPY_NAME, "hasBreakpointSet",
                "(Ljava/lang/String;I)Z", false);
        super.visitJumpInsn(IFEQ, breakpointLabel);
    }

    private void captureSnapshot(int line) {
        addLocals(line);
        addStaticFields();
        addFields();
    }

    private void isHit(final String source, final int line, final Label breakpointLabel) {
        super.visitLdcInsn(source);
        super.visitLdcInsn(line);
        super.visitMethodInsn(INVOKESTATIC, SPY_NAME, "isHit",
                "(Ljava/lang/String;I)Z", false);
        super.visitJumpInsn(IFEQ, breakpointLabel);
    }

    private void processForBreakpoint(final String source, final int line) {
        final Label theEnd = new Label();
        dump(source, line);
        dumpStackTrace(source, line);
        endReceive(source, line);
        super.visitLabel(theEnd);
    }

    private void dump(final String source, final int line) {
        super.visitLdcInsn(source);
        super.visitLdcInsn(line);
        super.visitMethodInsn(INVOKESTATIC, SPY_NAME, "dump", DUMP_METHOD_DESC, false);
    }


    private void addLocals(final int line) {
        final Collection<LocalVariable> variables = classMetadata.getVariables().get(methodUniqueName);
        for (final LocalVariable var : variables) {
            if (line >= var.getStart() && line < var.getEnd()) {
                super.visitLdcInsn(var.getName());
                super.visitVarInsn(Type.getType(var.getDesc()).getOpcode(ILOAD), var.getIndex());
                boxingIfShould(var.getDesc());
                super.visitMethodInsn(INVOKESTATIC, SPY_NAME,
                        "putLocalVariable", DEFAULT_DEBUG_ADDKV_DESC, false);

            }
        }
    }

    private void addStaticFields() {
        for (final ClassField field : classMetadata.getStaticFields()) {
            final String name = field.getName();
            final String desc = field.getDesc();
            super.visitLdcInsn(name);
            super.visitFieldInsn(GETSTATIC, className, name, desc);
            boxingIfShould(desc);
            super.visitMethodInsn(INVOKESTATIC, SPY_NAME,
                    "putStaticField", DEFAULT_DEBUG_ADDKV_DESC, false);
        }
    }

    private void addFields() {
        if ((access & Opcodes.ACC_STATIC) == 0) {
            for (final ClassField field : classMetadata.getFields()) {
                final String name = field.getName();
                final String desc = field.getDesc();
                super.visitLdcInsn(name);
                super.visitVarInsn(ALOAD, 0);
                super.visitFieldInsn(GETFIELD, className, name, desc);
                boxingIfShould(desc);
                super.visitMethodInsn(INVOKESTATIC, SPY_NAME,
                        "putField", DEFAULT_DEBUG_ADDKV_DESC, false);
            }
        }
    }

    private void dumpStackTrace(final String source, int line) {
        super.visitLdcInsn(source);
        super.visitLdcInsn(line);
        super.visitTypeInsn(NEW, THROWABLE_INTERNALNAME);
        super.visitInsn(DUP);
        super.visitMethodInsn(INVOKESPECIAL, THROWABLE_INTERNALNAME,
                "<init>",
                "()V",
                false);

        super.visitMethodInsn(INVOKESTATIC, SPY_NAME,
                "fillStacktrace",
                "(Ljava/lang/String;ILjava/lang/Throwable;)V", false);
    }

    private void endReceive(final String source, final int line) {
        super.visitLdcInsn(source);
        super.visitLdcInsn(line);
        super.visitMethodInsn(INVOKESTATIC, SPY_NAME,
                "endReceive", ENDRECEIVE_METHOD_DESC, false);
    }


    private void boxingIfShould(final String desc) {
        switch (Type.getType(desc).getSort()) {
            case Type.BOOLEAN:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
                break;
            case Type.BYTE:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
                break;
            case Type.CHAR:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
                break;
            case Type.SHORT:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
                break;
            case Type.INT:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                break;
            case Type.FLOAT:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                break;
            case Type.LONG:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                break;
            case Type.DOUBLE:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                break;
        }
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(Math.max(maxStack, 4), maxLocals);
    }

}
