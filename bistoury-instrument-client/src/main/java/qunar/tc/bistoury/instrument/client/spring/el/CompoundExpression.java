package qunar.tc.bistoury.instrument.client.spring.el;


import org.objectweb.asm.MethodVisitor;

/**
 * Represents a DOT separated expression sequence, such as 'property1.property2.methodOne()'
 *
 * @author Andy Clement
 * @since 3.0
 */
class CompoundExpression extends SpelNodeImpl {

    public CompoundExpression(int pos, SpelNodeImpl... expressionComponents) {
        super(pos, expressionComponents);
        if (expressionComponents.length < 2) {
            throw new IllegalStateException("Do not build compound expressions with less than two entries: " +
                    expressionComponents.length);
        }
    }


    @Override
    protected ValueRef getValueRef(ExpressionState state) throws EvaluationException {
        if (getChildCount() == 1) {
            return this.children[0].getValueRef(state);
        }

        SpelNodeImpl nextNode = this.children[0];
        try {
            TypedValue result = nextNode.getValueInternal(state);
            int cc = getChildCount();
            for (int i = 1; i < cc - 1; i++) {
                try {
                    state.pushActiveContextObject(result);
                    nextNode = this.children[i];
                    result = nextNode.getValueInternal(state);
                } finally {
                    state.popActiveContextObject();
                }
            }
            try {
                state.pushActiveContextObject(result);
                nextNode = this.children[cc - 1];
                return nextNode.getValueRef(state);
            } finally {
                state.popActiveContextObject();
            }
        } catch (SpelEvaluationException ex) {
            // Correct the position for the error before re-throwing
            ex.setPosition(nextNode.getStartPosition());
            throw ex;
        }
    }

    /**
     * Evaluates a compound expression. This involves evaluating each piece in turn and the
     * return value from each piece is the active context object for the subsequent piece.
     *
     * @param state the state in which the expression is being evaluated
     * @return the final value from the last piece of the compound expression
     */
    @Override
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        ValueRef ref = getValueRef(state);
        TypedValue result = ref.getValue();
        this.exitTypeDescriptor = this.children[this.children.length - 1].exitTypeDescriptor;
        return result;
    }

    @Override
    public void setValue(ExpressionState state, Object value) throws EvaluationException {
        //getValueRef(state).setValue(value);
    }

    @Override
    public boolean isWritable(ExpressionState state) throws EvaluationException {
        return false;
    }

    @Override
    public String toStringAST() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getChildCount(); i++) {
            if (i > 0) {
                sb.append(".");
            }
            sb.append(getChild(i).toStringAST());
        }
        return sb.toString();
    }

    @Override
    public boolean isCompilable() {
        for (SpelNodeImpl child : this.children) {
            if (!child.isCompilable()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        for (int i = 0; i < this.children.length; i++) {
            this.children[i].generateCode(mv, cf);
        }
        cf.pushDescriptor(this.exitTypeDescriptor);
    }

}
