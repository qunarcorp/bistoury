package qunar.tc.bistoury.instrument.client.debugger;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author keli.wang
 * @since 2017/3/15
 */
class DebuggerClassVisitor extends ClassVisitor {
    private final String source;
    private final ClassMetadata classMetadata;

    private String className;

    public DebuggerClassVisitor(final ClassVisitor cv,
                                final String source,
                                final ClassMetadata classMetadata) {
        super(Opcodes.ASM5, cv);
        this.source = source;
        this.classMetadata = classMetadata;
    }

    @Override
    public void visit(final int version,
                      final int access,
                      final String name,
                      final String signature,
                      final String superName,
                      final String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
    }

    @Override
    public MethodVisitor visitMethod(final int access,
                                     final String name,
                                     final String desc,
                                     final String signature,
                                     final String[] exceptions) {
        final MethodVisitor originalMV =
                super.visitMethod(access, name,
                        desc, signature,
                        exceptions);

        return new DebuggerMethodVisitor(
                source, className,
                name, desc,
                access,
                classMetadata, originalMV);
    }
}
