package qunar.tc.test;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AnalyzerAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.util.CheckClassAdapter;
import qunar.tc.bistoury.instrument.client.metrics.Metrics;
import qunar.tc.bistoury.instrument.client.metrics.MetricsReportor;
import qunar.tc.bistoury.instrument.client.metrics.QMonitorMetricsReportor;

import java.io.*;

import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.ASM7;

/**
 * @author: leix.xie
 * @date: 2018/12/29 16:58
 * @describe：
 */
public class ASMTest extends ClassVisitor {

    private String source;
    private String className;

    static final MetricsReportor reportor = new QMonitorMetricsReportor(Metrics.INSTANCE);
    //static final PrintWriter writer = new PrintWriter(System.out);

    public ASMTest(final ClassVisitor cv, final String source) {
        super(ASM5, cv);
        this.source = source;

    }

    /**
     * 主方法
     */
    public static void main(String[] args) throws Exception {
        try {
            final String source = "/Users/leix.xie/workspace/opensource/bistoury/bistoury-instrument-client/target/test-classes/qunar/tc/test/Test.class";
            final ClassReader classReader = new ClassReader(new FileInputStream(source));
            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            final ClassVisitor classVisitor = new ASMTest(new CheckClassAdapter(classWriter), source);
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
            byte[] bytes = classWriter.toByteArray();
            print(bytes);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        //startReport();
    }

    private static void print(byte[] bytes) {
        File file = new File("/Users/leix.xie/workspace/opensource/bistoury/bistoury-instrument-client/target/Test.class");
        try (FileOutputStream outputStream = new FileOutputStream(file);) {
            outputStream.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        System.out.println(name);
        final MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
        /*if (!"setCount".equals(name)) {
            return methodVisitor;
        }*/
        MonitorAdviceAdapter monitorMV = new MonitorAdviceAdapter(ASM7, methodVisitor, access, name, desc, className, exceptions);
        AnalyzerAdapter analyzerAdapter = new AnalyzerAdapter(className, access, name, desc, monitorMV);
        monitorMV.setAnalyzerAdapter(analyzerAdapter);
        LocalVariablesSorter localVariablesSorter = new LocalVariablesSorter(access, desc, analyzerAdapter);
        monitorMV.setLocalVariablesSorter(localVariablesSorter);
        //MonitorAdviceAdapter monitorMV = new MonitorAdviceAdapter(ASM7, new LocalVariablesSorter(access, desc, methodVisitor), access, name, desc, className, exceptions);

        return localVariablesSorter;
    }

   /* private AgentMethod getTraceMethod(final String methodName, final String desc) {
        Type[] types = Type.getArgumentTypes(desc);
        List<TraceArg> args = new ArrayList<>(types.length);
        String[] tracedArgs = new String[types.length];
        for (int i = 0; i < types.length; ++i) {
            String name = "arg" + i;
            tracedArgs[i] = name;
            TraceArg traceArg = new TraceArg(types[i].getDescriptor(), name);
            args.add(traceArg);
        }
        AgentMethod agentMethod = new AgentMethod(methodName, args);
        agentMethod.setType("MONITRO");
        agentMethod.setTracedArgs(tracedArgs);
        agentMethod.setTraceReturnValue(true);
        return agentMethod;
    }*/

   /* public static void startReport() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    reportor.report(writer, "");
                    writer.println("------------------");
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }*/
}
