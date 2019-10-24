package qunar.tc.bistoury.instrument.client.profiler.sampling;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import qunar.tc.bistoury.instrument.client.debugger.Transformer;
import qunar.tc.bistoury.instrument.client.profiler.instrumentation.transformers.ProfilingClassAdapter;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.jar.JarFile;

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;

/**
 * @author cai.wen created on 2019/10/17 10:49
 */
public class Main {
    public static void premain(String args, Instrumentation inst) {
        System.out.println("run premain method");
        addJarFile(inst);

        inst.addTransformer(new Transformer() {
            @Override
            protected byte[] transform(String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                ClassReader reader = new ClassReader(classfileBuffer);
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                ClassVisitor adapter = new FinallyBlock.LettuceCV(writer, className);
                reader.accept(adapter, EXPAND_FRAMES);
                // 生成新类字节码
                return writer.toByteArray();
            }
        });


//        Manager.init(180,10);
    }

    public static void agentmain(String args, Instrumentation inst) {
        System.out.println("run agentmain method");

        addJarFile(inst);

        inst.addTransformer(new Transformer() {
            @Override
            protected byte[] transform(String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                ClassReader reader = new ClassReader(classfileBuffer);
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                ClassVisitor adapter = new FinallyBlock.LettuceCV(writer, className);
                reader.accept(adapter, EXPAND_FRAMES);
                // 生成新类字节码
                return writer.toByteArray();
            }
        });
//        Manager.init(180,10);
    }

    private static void addJarFile(Instrumentation inst) {
        try {
            inst.appendToSystemClassLoaderSearch(new JarFile("C:\\Users\\cai.wen\\.m2\\repository\\com\\google\\guava\\guava\\18.0\\guava-18.0.jar"));
            inst.appendToSystemClassLoaderSearch(new JarFile("C:\\code\\github\\bistoury\\bistoury-instrument-client\\target\\bistoury-instrument-client-2.0.6.jar"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
