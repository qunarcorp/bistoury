package qunar.tc.test;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * @author: leix.xie
 * @date: 2018/12/29 17:06
 * @describe：
 */
public class MethodVistorTest extends AdviceAdapter {


    /**
     * Creates a new {@link AdviceAdapter}.
     *
     * @param api    the ASM API version implemented by this visitor. Must be one
     *               of {@link Opcodes#ASM4} or {@link Opcodes#ASM5}.
     * @param mv     the method visitor to which this adapter delegates calls.
     * @param access the method's access flags (see {@link Opcodes}).
     * @param name   the method's name.
     * @param desc   the method's descriptor (see {@link Type Type}).
     */
    protected MethodVistorTest(int api, MethodVisitor mv, int access, String name, String desc) {
        super(api, mv, access, name, desc);
    }
}
