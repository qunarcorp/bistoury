package qunar.tc.test;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AnalyzerAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.commons.Method;
import qunar.tc.bistoury.instrument.client.common.Access;
import qunar.tc.bistoury.instrument.client.util.DescDeal;
import qunar.tc.bistoury.instrument.spy.BistourySpys1;

import java.util.List;

/**
 * @author leix.xie
 * @date 2019/10/9 19:46
 * @describe
 */
public class MonitorAdviceAdapter extends MethodVisitor implements Opcodes {

    private final String MONITOR_KEY;

    private final Type[] parameterTypes;
    private final Type returnType;
    private final boolean hasReturn;
    private final String desc;
    private final String className;
    private final String methodName;
    private final int totalParameterSize;
    private final int startOfVarIndex;
    private int scopeVarIndex;

    private final Type SPY_TYPE = Type.getType(BistourySpys1.class);
    private final Method start = new Method("start", Type.getMethodDescriptor(Type.getType(Long.class)));
    private final Method stop = new Method("stop", Type.getMethodDescriptor(Type.getType(void.class), Type.getType(String.class), Type.getType(Long.class)));
    private final Method exception = new Method("exception", Type.getMethodDescriptor(Type.getType(void.class), Type.getType(String.class)));

    private static final String SPY_NAME = Type.getInternalName(BistourySpys1.class);
    private static final String RUNTIME_EXCEPTION = Type.getInternalName(RuntimeException.class);
    private static final String START_DESC = Type.getMethodDescriptor(Type.getType(Long.class));
    private static final String STOP_DESC = Type.getMethodDescriptor(Type.getType(void.class), Type.getType(String.class), Type.getType(Long.class));
    private static final String EXCEPTION_DESC = Type.getMethodDescriptor(Type.getType(void.class), Type.getType(String.class));


    private static final String THROWABLE_EXCEPTION = Type.getInternalName(Throwable.class);

    private LocalVariablesSorter localVariablesSorter;
    private AnalyzerAdapter analyzerAdapter;

    private int maxStack;

    protected MonitorAdviceAdapter(int api, MethodVisitor methodVisitor, int access, String name, String desc, String className, String[] exceptions) {
        super(ASM7, methodVisitor);
        this.className = className;
        this.methodName = name;
        this.desc = desc;

        this.MONITOR_KEY = className.replaceAll("\\/", ".") + "#" + name + "(" + DescDeal.getSimplifyMethodDesc(desc) + ")";

        this.returnType = Type.getReturnType(desc);
        this.hasReturn = this.returnType != Type.VOID_TYPE;

        this.parameterTypes = Type.getArgumentTypes(desc);
        this.totalParameterSize = computeTotalParameterSize(parameterTypes);

        this.startOfVarIndex = Access.of(access).contain(Opcodes.ACC_STATIC) ? 0 : 1;
        scopeVarIndex = startOfVarIndex + totalParameterSize;

        beginLabel = new Label();
        endLabel = new Label();
        throwableLabel = new Label();
    }


    private Label beginLabel;
    private Label endLabel;
    private Label throwableLabel;

    @Override
    public void visitCode() {
        mv.visitCode();
        onMethodEnter();
    }

    protected void onMethodEnter() {
        //catch
        mv.visitTryCatchBlock(beginLabel, endLabel, throwableLabel, THROWABLE_EXCEPTION);

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

        int computeMaxLocals = computeMaxLocals();
        //新建了一个throwable变量，所以maxLocals需要加1
        mv.visitMaxs(Math.max(maxStack, 4), maxLocals + 1);
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

    private void startMonitor() {
        //long startTime=AgentMonitor.start();

        mv.visitMethodInsn(INVOKESTATIC, SPY_NAME, "start", START_DESC, false);

        this.scopeVarIndex = localVariablesSorter.newLocal(Type.getType(Long.class));
        mv.visitVarInsn(ASTORE, scopeVarIndex);
        maxStack = 4;
    }

    private void endMonitor() {
        //AgentMonitor.stop(key,startTime);
        mv.visitLdcInsn(MONITOR_KEY);
        mv.visitVarInsn(ALOAD, scopeVarIndex);
        mv.visitMethodInsn(INVOKESTATIC, SPY_NAME, "stop", STOP_DESC, false);

    }

    private void exceptionMonitor() {
        mv.visitLdcInsn(MONITOR_KEY);
        mv.visitMethodInsn(INVOKESTATIC, SPY_NAME, "exception", EXCEPTION_DESC, false);
    }

    private int computeTotalParameterSize(Type[] parameterTypes) {
        int result = 0;
        int parameterCount = parameterTypes.length;
        for (int i = 0; i < parameterCount; i++) {
            result += parameterTypes[i].getSize();
        }
        return result;
    }

    private int computeMaxLocals() {
        return startOfVarIndex + totalParameterSize + 1 + (hasReturn ? returnType.getSize() : 1);
    }

    public void setLocalVariablesSorter(LocalVariablesSorter localVariablesSorter) {
        this.localVariablesSorter = localVariablesSorter;
    }

    public void setAnalyzerAdapter(AnalyzerAdapter analyzerAdapter) {
        this.analyzerAdapter = analyzerAdapter;
    }
}
