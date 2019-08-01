package qunar.tc.bistoury.instrument.client.spring.el;


import org.objectweb.asm.MethodVisitor;

/**
 * Expression language AST node that represents a float literal.
 *
 * @author Satyapal Reddy
 * @author Andy Clement
 * @since 3.2
 */
class FloatLiteral extends Literal {

    private final TypedValue value;


    public FloatLiteral(String payload, int pos, float value) {
        super(payload, pos);
        this.value = new TypedValue(value);
        this.exitTypeDescriptor = "F";
    }


    @Override
    public TypedValue getLiteralValue() {
        return this.value;
    }

    @Override
    public boolean isCompilable() {
        return true;
    }

    @Override
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        mv.visitLdcInsn(this.value.getValue());
        cf.pushDescriptor(this.exitTypeDescriptor);
    }

}
