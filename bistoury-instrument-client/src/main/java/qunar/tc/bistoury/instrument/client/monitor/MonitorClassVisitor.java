package qunar.tc.bistoury.instrument.client.monitor;

import com.taobao.middleware.logger.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import qunar.tc.bistoury.attach.common.BistouryLoggger;

/**
 * @author: leix.xie
 * @date: 2018/12/26 19:34
 * @describe：
 */
public class MonitorClassVisitor extends ClassVisitor {
    private static final Logger logger = BistouryLoggger.getLogger();
    private String source;
    private String className;
    private String methodName;
    private String methodDesc;
    private int line;

    public MonitorClassVisitor(final ClassVisitor cv, final String source, final String methodName, final String methodDesc, final int line) {
        super(Opcodes.ASM5, cv);
        this.source = source;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        this.line = line;
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
        if (name.equals(methodName) && desc.equals(methodDesc)) {
            logger.debug("visit method, name: {}, desc: {}", name, desc);
            MonitorMethodVisitor monitorMV = new MonitorMethodVisitor(access, desc, signature, exceptions, className, name, line, cv);
            return monitorMV;
        } else {
            final MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
            return methodVisitor;
        }
    }
}
