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

import org.objectweb.asm.*;
import qunar.tc.bistoury.instrument.client.common.Access;
import qunar.tc.bistoury.instrument.client.util.DescDeal;
import qunar.tc.bistoury.instrument.spy.BistourySpys1;

/**
 * @author: leix.xie
 * @date: 2018/12/26 19:34
 * @describe：
 */
public class MonitorMethodVisitor extends MethodVisitor implements Opcodes {

    private static final String AGENT_GENERATED_DESC = Type.getDescriptor(AgentGenerated.class);

    private final String MONITOR_KEY;
    private static final String SPY_NAME = Type.getInternalName(BistourySpys1.class);
    private static final String RUNTIME_EXCEPTION = Type.getInternalName(RuntimeException.class);
    private static final String START_DESC = Type.getMethodDescriptor(Type.getType(Long.class));
    private static final String STOP_DESC = Type.getMethodDescriptor(Type.getType(void.class), Type.getType(String.class), Type.getType(Long.class));
    private static final String EXCEPTION_DESC = Type.getMethodDescriptor(Type.getType(void.class), Type.getType(String.class));


    private final MethodVisitor monitorMethod;
    private final int newExceptionsLen;
    private String[] newMethodExceptions;
    private final Type[] parameterTypes;
    private final Type returnType;
    private final boolean hasReturn;
    private final String desc;
    private final String className;
    private final String methodName;
    private final int totalParameterSize;
    private final int startOfVarIndex;

    public MonitorMethodVisitor(int access, String desc, String signature, String[] exceptions, String className, String method, int line, ClassVisitor cv) {
        super(ASM5, cv.visitMethod(Access.of(access).remove(ACC_PUBLIC).remove(ACC_PROTECTED).remove(ACC_SYNCHRONIZED).add(ACC_PRIVATE).add(ACC_FINAL).get(), DescDeal.generateNewName(method), desc, signature, exceptions));
        this.className = className;
        this.parameterTypes = Type.getArgumentTypes(desc);
        this.returnType = Type.getReturnType(desc);
        this.hasReturn = this.returnType != Type.VOID_TYPE;
        this.desc = desc;
        this.methodName = method;
        this.MONITOR_KEY = className.replaceAll("\\/", ".") + "#" + method + "(" + DescDeal.getSimplifyMethodDesc(desc) + ")";

        this.totalParameterSize = computeTotalParameterSize(parameterTypes);
        this.startOfVarIndex = Access.of(access).contain(Opcodes.ACC_STATIC) ? 0 : 1;
        if (exceptions == null) {
            newMethodExceptions = new String[]{RUNTIME_EXCEPTION};
        } else {
            newMethodExceptions = new String[exceptions.length + 1];
            newMethodExceptions[0] = RUNTIME_EXCEPTION;
            System.arraycopy(exceptions, 0, newMethodExceptions, 1, exceptions.length);
        }
        newExceptionsLen = newMethodExceptions.length;
        monitorMethod = cv.visitMethod(access, method, desc, signature, newMethodExceptions);
        addGeneratedAnnotation();
    }

    private void addGeneratedAnnotation() {
        AnnotationVisitor av = monitorMethod.visitAnnotation(AGENT_GENERATED_DESC, true);
        av.visitEnd();
        av = mv.visitAnnotation(AGENT_GENERATED_DESC, true);
        av.visitEnd();
    }

    @Override
    public void visitParameter(String name, int access) {
        super.visitParameter(name, access);
        monitorMethod.visitParameter(name, access);
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return monitorMethod.visitAnnotationDefault();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return monitorMethod.visitAnnotation(desc, visible);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return monitorMethod.visitTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        return monitorMethod.visitParameterAnnotation(parameter, desc, visible);
    }

    @Override
    public void visitAttribute(Attribute attr) {
        super.visitAttribute(attr);
    }

    /**
     * 将原来的方法重命名为一个private方法，然后在原来未改名的方法里调用原来的方法
     */
    @Override
    public void visitCode() {
        super.visitCode();
        monitorMethod.visitCode();

        int scopeVarIndex = startOfVarIndex + totalParameterSize;
        Label startOfTryCatch = new Label();
        Label endOfTryCatch = new Label();
        Label[] exceptionHandlers = new Label[newExceptionsLen];

        //catchs
        for (int i = 0, length = exceptionHandlers.length; i < length; i++) {
            monitorMethod.visitTryCatchBlock(startOfTryCatch, endOfTryCatch, exceptionHandlers[i] = new Label(), newMethodExceptions[i]);
        }

        //finally
        Label endOfFinally = new Label();
        Label handlerOfFinally = new Label();
        monitorMethod.visitTryCatchBlock(startOfTryCatch, endOfTryCatch, handlerOfFinally, null);
        monitorMethod.visitTryCatchBlock(exceptionHandlers[0], endOfFinally, handlerOfFinally, null);

        startTrace(scopeVarIndex);

        int returnVarIndex = scopeVarIndex + 1;

        //try{
        //call original method
        //}catch(...){
        monitorMethod.visitLabel(startOfTryCatch);
        callOriginal(returnVarIndex, startOfVarIndex, monitorMethod);
        monitorMethod.visitLabel(endOfTryCatch);

        //attachReturnValue(scopeVarIndex, returnVarIndex);

        endTrace(scopeVarIndex);

        Label end = null;
        if (!hasReturn) {
            end = new Label();
            monitorMethod.visitJumpInsn(GOTO, end);
        } else {
            monitorMethod.visitVarInsn(returnType.getOpcode(ILOAD), returnVarIndex);
            monitorMethod.visitInsn(returnType.getOpcode(IRETURN));
        }

        emitCatchBlocks(scopeVarIndex, exceptionHandlers);

        monitorMethod.visitLabel(handlerOfFinally);
        monitorMethod.visitVarInsn(ASTORE, returnVarIndex);
        //finally
        monitorMethod.visitLabel(endOfFinally);

        endTrace(scopeVarIndex);

        monitorMethod.visitVarInsn(ALOAD, returnVarIndex);
        monitorMethod.visitInsn(ATHROW);

        if (!hasReturn) {
            monitorMethod.visitLabel(end);
            monitorMethod.visitInsn(RETURN);
        }
    }

    private void emitCatchBlocks(int scopeVarIndex, Label[] exceptionHandlers) {
        //catch blocks
        for (int i = 0; i < newExceptionsLen; i++) {
            monitorMethod.visitLabel(exceptionHandlers[i]);
            //ex
            int exceptionVarIndex = scopeVarIndex + 1;
            monitorMethod.visitVarInsn(ASTORE, exceptionVarIndex);

            monitorMethod.visitLdcInsn(MONITOR_KEY);
            monitorMethod.visitMethodInsn(INVOKESTATIC, SPY_NAME, "exception", EXCEPTION_DESC, false);
            //throw ex
            monitorMethod.visitVarInsn(ALOAD, exceptionVarIndex);
            monitorMethod.visitInsn(ATHROW);
        }
    }

    private void endTrace(int scopeVarIndex) {
        //AgentMonitor.stop(key,startTime);
        monitorMethod.visitLdcInsn(MONITOR_KEY);
        //load startTime from local variable
        monitorMethod.visitVarInsn(ALOAD, scopeVarIndex);
        monitorMethod.visitMethodInsn(INVOKESTATIC, SPY_NAME, "stop", STOP_DESC, false);
    }

    private void startTrace(int scopeVarIndex) {
        //long startTime = AgentMonitor.start(key);
        //monitorMethod.visitLdcInsn(MONITOR_KEY);
        monitorMethod.visitMethodInsn(INVOKESTATIC, SPY_NAME, "start", START_DESC, false);
        //report startTime to local variable
        monitorMethod.visitVarInsn(ASTORE, scopeVarIndex);

    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack, maxLocals);
        int computeMaxLocals = computeMaxLocals();
        monitorMethod.visitMaxs(Math.max(maxStack, Math.max(computeMaxLocals, 4)), computeMaxLocals);
    }

    /**
     * @return this(1) + parameters size(n * per size) + scope(1) + hasReturn ? return size : exception(1)
     */
    private int computeMaxLocals() {
        return startOfVarIndex + totalParameterSize + 1 + (hasReturn ? returnType.getSize() : 1);
    }


    private void callOriginal(int returnVarIndex, int defaultSize, MethodVisitor traceMethod) {
        //this
        if (defaultSize == 1) {
            traceMethod.visitVarInsn(ALOAD, 0);
        }
        //load parameters to stack
        for (int i = 0, index = 0, preSize = defaultSize; i < parameterTypes.length; i++) {
            index += preSize;
            Type parameterType = parameterTypes[i];
            traceMethod.visitVarInsn(parameterType.getOpcode(ILOAD), index);
            preSize = parameterType.getSize();
        }
        traceMethod.visitMethodInsn(defaultSize == 1 ? INVOKEVIRTUAL : INVOKESTATIC, this.className, DescDeal.generateNewName(this.methodName), desc, false);
        if (hasReturn) {
            traceMethod.visitVarInsn(returnType.getOpcode(ISTORE), returnVarIndex);
        }
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        monitorMethod.visitEnd();
    }

    private int computeTotalParameterSize(Type[] parameterTypes) {
        int result = 0;
        int parameterCount = parameterTypes.length;
        for (int i = 0; i < parameterCount; i++) {
            result += parameterTypes[i].getSize();
        }
        return result;
    }

}
