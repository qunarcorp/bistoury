package qunar.tc.bistoury.instrument.client.debugger;


import com.taobao.middleware.logger.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import qunar.tc.bistoury.attach.common.BistouryLoggger;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * Created by zhaohui.yu
 * 16/6/2
 */
public abstract class Transformer implements ClassFileTransformer {
    private static final Logger logger = BistouryLoggger.getLogger();

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            return transform(className, classBeingRedefined, protectionDomain, classfileBuffer);
        } catch (Throwable e) {
            System.err.print("Class: ");
            System.err.print(className);
            System.err.print(", ClassLoader: ");
            System.err.print(loader);
            System.err.print(" transform failed.\n");
            e.printStackTrace(System.err);
            logger.error("", "transform failed", "Classs: {}, ClassLoader: {} transform failed.", className, loader, e);
            return null;
        }
    }

    protected int computeFlag(ClassReader classReader) {
        int flag = ClassWriter.COMPUTE_MAXS;
        //如果低于1.7版本，还是用compute maxs吧
        short version = classReader.readShort(6);
        if (version >= Opcodes.V1_7) {
            flag = ClassWriter.COMPUTE_FRAMES;
        }

        return flag;
    }

    protected abstract byte[] transform(String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException;
}
