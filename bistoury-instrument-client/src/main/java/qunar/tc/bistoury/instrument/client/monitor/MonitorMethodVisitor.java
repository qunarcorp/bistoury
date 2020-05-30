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

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AnalyzerAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;
import qunar.tc.bistoury.instrument.client.util.DescDeal;
import qunar.tc.bistoury.instrument.spy.BistourySpys1;

import java.util.List;

/**
 * @author: leix.xie
 * @date: 2018/12/26 19:34
 * @describe：
 */
public class MonitorMethodVisitor extends MethodVisitor implements Opcodes {

    private static final String THROWABLE_CLASS_TYPE = Type.getInternalName(Throwable.class);
    private static final String SPY_CLASS_TYPE = Type.getInternalName(BistourySpys1.class);

    private static final String START_METHOD_NAME = "start";
    private static final String STOP_METHOD_NAME = "stop";
    private static final String EXCEPTION_METHOD_NAME = "exception";

    private static final String START_METHOD_DESC = Type.getMethodDescriptor(Type.getType(Long.class));
    private static final String STOP_METHOD_DESC = Type.getMethodDescriptor(Type.getType(void.class), Type.getType(String.class), Type.getType(Long.class));
    private static final String EXCEPTION_METHOD_DESC = Type.getMethodDescriptor(Type.getType(void.class), Type.getType(String.class));

    private final String MONITOR_KEY;

    private int scopeVarIndex;

    private Label beginLabel;
    private Label endLabel;
    private Label throwableLabel;

    private LocalVariablesSorter localVariablesSorter;
    private AnalyzerAdapter analyzerAdapter;

    private int maxStack;

    public MonitorMethodVisitor(MethodVisitor methodVisitor, int access, final String name, final String desc, final String className) {
        super(ASM7, methodVisitor);

        this.MONITOR_KEY = className.replaceAll("\\/", ".") + "#" + name + "(" + DescDeal.getSimplifyMethodDesc(desc) + ")";

        beginLabel = new Label();
        endLabel = new Label();
        throwableLabel = new Label();
    }

    @Override
    public void visitCode() {
        mv.visitCode();
        //catch
        mv.visitTryCatchBlock(beginLabel, endLabel, throwableLabel, THROWABLE_CLASS_TYPE);

        startMonitor();
        mv.visitLabel(beginLabel);
    }

    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= IRETURN && opcode <= RETURN)) {
            endMonitor();

            List<Object> stack = analyzerAdapter.stack;
            if (stack == null) {
                maxStack = Math.max(4, maxStack);
            } else {
                maxStack = Math.max(stack.size() + 4, maxStack);
            }
        }
        mv.visitInsn(opcode);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        mv.visitLabel(endLabel);

        emitCatchBlocks();

        //新建了一个throwable变量，maxLocals需要加1
        super.visitMaxs(Math.max(maxStack, this.maxStack), maxLocals + 1);
    }

    private void startMonitor() {
        //long startTime=AgentMonitor.start();

        mv.visitMethodInsn(INVOKESTATIC, SPY_CLASS_TYPE, START_METHOD_NAME, START_METHOD_DESC, false);

        this.scopeVarIndex = localVariablesSorter.newLocal(Type.getType(Long.class));
        mv.visitVarInsn(ASTORE, scopeVarIndex);

        maxStack = 4;
    }

    private void endMonitor() {
        //AgentMonitor.stop(key,startTime);
        mv.visitLdcInsn(MONITOR_KEY);
        mv.visitVarInsn(ALOAD, scopeVarIndex);
        mv.visitMethodInsn(INVOKESTATIC, SPY_CLASS_TYPE, STOP_METHOD_NAME, STOP_METHOD_DESC, false);

    }

    private void exceptionMonitor() {
        //AgentMonitor.exception(key);
        mv.visitLdcInsn(MONITOR_KEY);
        mv.visitMethodInsn(INVOKESTATIC, SPY_CLASS_TYPE, EXCEPTION_METHOD_NAME, EXCEPTION_METHOD_DESC, false);
    }

    private void emitCatchBlocks() {
        //catch blocks
        mv.visitLabel(throwableLabel);
        //ex
        int exceptionVarIndex = localVariablesSorter.newLocal(Type.getType(Object.class));
        mv.visitVarInsn(ASTORE, exceptionVarIndex);

        //异常处理中结束
        endMonitor();
        exceptionMonitor();

        //throw ex
        mv.visitVarInsn(ALOAD, exceptionVarIndex);
        mv.visitInsn(ATHROW);
    }

    public void setLocalVariablesSorter(LocalVariablesSorter localVariablesSorter) {
        this.localVariablesSorter = localVariablesSorter;
    }

    public void setAnalyzerAdapter(AnalyzerAdapter analyzerAdapter) {
        this.analyzerAdapter = analyzerAdapter;
    }
}
