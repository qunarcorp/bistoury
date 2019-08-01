package qunar.tc.bistoury.instrument.client.spring.el;


import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * Represents the elvis operator ?:. For an expression "a?:b" if a is not null, the value
 * of the expression is "a", if a is null then the value of the expression is "b".
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
class Elvis extends SpelNodeImpl {

	public Elvis(int pos, SpelNodeImpl... args) {
        super(pos, args);
	}


	/**
	 * Evaluate the condition and if not null, return it.
	 * If it is null, return the other value.
	 * @param state the expression state
	 * @throws EvaluationException if the condition does not evaluate correctly
	 * to a boolean or there is a problem executing the chosen alternative
	 */
	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		TypedValue value = this.children[0].getValueInternal(state);
		// If this check is changed, the generateCode method will need changing too
		if (!StringUtils.isEmpty(value.getValue())) {
			return value;
		}
		else {
			TypedValue result = this.children[1].getValueInternal(state);
			computeExitTypeDescriptor();
			return result;
		}
	}

	@Override
	public String toStringAST() {
		return getChild(0).toStringAST() + " ?: " + getChild(1).toStringAST();
	}

	@Override
	public boolean isCompilable() {
		SpelNodeImpl condition = this.children[0];
		SpelNodeImpl ifNullValue = this.children[1];
		return (condition.isCompilable() && ifNullValue.isCompilable() &&
				condition.exitTypeDescriptor != null && ifNullValue.exitTypeDescriptor != null);
	}

	@Override
	public void generateCode(MethodVisitor mv, CodeFlow cf) {
		// exit type descriptor can be null if both components are literal expressions
		computeExitTypeDescriptor();
		this.children[0].generateCode(mv, cf);
		CodeFlow.insertBoxIfNecessary(mv, cf.lastDescriptor().charAt(0));
		Label elseTarget = new Label();
		Label endOfIf = new Label();
		mv.visitInsn(DUP);
		mv.visitJumpInsn(IFNULL, elseTarget);
		// Also check if empty string, as per the code in the interpreted version
		mv.visitInsn(DUP);
		mv.visitLdcInsn("");
		mv.visitInsn(SWAP);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z",false);
		mv.visitJumpInsn(IFEQ, endOfIf);  // if not empty, drop through to elseTarget
		mv.visitLabel(elseTarget);
		mv.visitInsn(POP);
		this.children[1].generateCode(mv, cf);
		if (!CodeFlow.isPrimitive(this.exitTypeDescriptor)) {
			CodeFlow.insertBoxIfNecessary(mv, cf.lastDescriptor().charAt(0));
		}
		mv.visitLabel(endOfIf);
		cf.pushDescriptor(this.exitTypeDescriptor);
	}

	private void computeExitTypeDescriptor() {
		if (this.exitTypeDescriptor == null && this.children[0].exitTypeDescriptor != null &&
				this.children[1].exitTypeDescriptor != null) {
			String conditionDescriptor = this.children[0].exitTypeDescriptor;
			String ifNullValueDescriptor = this.children[1].exitTypeDescriptor;
			if (conditionDescriptor.equals(ifNullValueDescriptor)) {
				this.exitTypeDescriptor = conditionDescriptor;
			}
			else {
				// Use the easiest to compute common1 super type
				this.exitTypeDescriptor = "Ljava/lang/Object";
			}
		}
	}

}
