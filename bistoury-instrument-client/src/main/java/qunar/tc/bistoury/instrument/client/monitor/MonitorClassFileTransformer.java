package qunar.tc.bistoury.instrument.client.monitor;

import com.google.common.base.Objects;
import com.taobao.middleware.logger.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.CheckClassAdapter;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.instrument.client.common.ClassFileBuffer;
import qunar.tc.bistoury.instrument.client.debugger.Transformer;
import qunar.tc.bistoury.instrument.client.location.ResolvedSourceLocation;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.concurrent.locks.Lock;

/**
 * @author: leix.xie
 * @date: 2018/12/26 19:48
 * @describe：
 */
public class MonitorClassFileTransformer extends Transformer {
    private static final Logger LOG = BistouryLoggger.getLogger();

    private final ClassFileBuffer classFileBuffer;
    private final String source;
    private String monitorClassName;
    private String methodName;
    private String methodDesc;
    private int line;

    MonitorClassFileTransformer(ClassFileBuffer classFileBuffer, final String source, final ResolvedSourceLocation location, final int line) {
        this.classFileBuffer = classFileBuffer;
        this.source = source;
        this.methodDesc = location.getMethodDesc();
        this.methodName = location.getMethodName();
        this.line = line;
        this.monitorClassName = signatureToClassName(location.getClassSignature());
    }

    @Override
    protected byte[] transform(final String className,
                               final Class<?> classBeingRedefined,
                               final ProtectionDomain protectionDomain,
                               final byte[] classBytes) throws IllegalClassFormatException {
        if (!Objects.equal(className, monitorClassName)) {
            return null;
        }
        LOG.info("monitor class: {}", className);
        Lock lock = classFileBuffer.getLock();
        lock.lock();
        try {
            final ClassReader classReader = new ClassReader(classFileBuffer.getClassBuffer(classBeingRedefined, classBytes));
            final ClassWriter classWriter = new ClassWriter(computeFlag(classReader));
            final ClassVisitor classVisitor = new MonitorClassVisitor(new CheckClassAdapter(classWriter), source, methodName, methodDesc, line);
            classReader.accept(classVisitor, ClassReader.SKIP_FRAMES);
            byte[] bytes = classWriter.toByteArray();
            classFileBuffer.setClassBuffer(classBeingRedefined, bytes);
            return bytes;
        } finally {
            lock.unlock();
        }
    }

    private String signatureToClassName(final String signature) {
        return Type.getType(signature).getInternalName();
    }
}
