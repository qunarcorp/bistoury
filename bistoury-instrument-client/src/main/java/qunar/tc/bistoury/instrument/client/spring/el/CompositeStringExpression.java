package qunar.tc.bistoury.instrument.client.spring.el;


/**
 * Represents a template expression broken into pieces. Each piece will be an Expression
 * but pure text parts to the template will be represented as LiteralExpression objects.
 * An example of a template expression might be:
 *
 * <pre class="code">
 * &quot;Hello ${getName()}&quot;
 * </pre>
 * <p>
 * which will be represented as a CompositeStringExpression of two parts. The first part
 * being a LiteralExpression representing 'Hello ' and the second part being a real
 * expression that will call {@code getName()} when invoked.
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
class CompositeStringExpression implements Expression {

    private final String expressionString;

    /**
     * The array of expressions that make up the composite expression
     */
    private final Expression[] expressions;


    public CompositeStringExpression(String expressionString, Expression[] expressions) {
        this.expressionString = expressionString;
        this.expressions = expressions;
    }


    @Override
    public final String getExpressionString() {
        return this.expressionString;
    }

    @Override
    public String getValue() throws EvaluationException {
        StringBuilder sb = new StringBuilder();
        for (Expression expression : this.expressions) {
            String value = expression.getValue(String.class);
            if (value != null) {
                sb.append(value);
            }
        }
        return sb.toString();
    }

    @Override
    public String getValue(Object rootObject) throws EvaluationException {
        StringBuilder sb = new StringBuilder();
        for (Expression expression : this.expressions) {
            String value = expression.getValue(rootObject, String.class);
            if (value != null) {
                sb.append(value);
            }
        }
        return sb.toString();
    }

    @Override
    public String getValue(EvaluationContext context) throws EvaluationException {
        StringBuilder sb = new StringBuilder();
        for (Expression expression : this.expressions) {
            String value = expression.getValue(context, String.class);
            if (value != null) {
                sb.append(value);
            }
        }
        return sb.toString();
    }

    @Override
    public String getValue(EvaluationContext context, Object rootObject) throws EvaluationException {
        StringBuilder sb = new StringBuilder();
        for (Expression expression : this.expressions) {
            String value = expression.getValue(context, rootObject, String.class);
            if (value != null) {
                sb.append(value);
            }
        }
        return sb.toString();
    }

    @Override
    public Class<?> getValueType(EvaluationContext context) {
        return String.class;
    }

    @Override
    public Class<?> getValueType() {
        return String.class;
    }

    @Override
    public TypeDescriptor getValueTypeDescriptor(EvaluationContext context) {
        return TypeDescriptor.valueOf(String.class);
    }

    @Override
    public TypeDescriptor getValueTypeDescriptor() {
        return TypeDescriptor.valueOf(String.class);
    }

    @Override
    public void setValue(EvaluationContext context, Object value) throws EvaluationException {
//		throw new EvaluationException(this.expressionString, "Cannot call setValue on a composite expression");
    }

    @Override
    public <T> T getValue(EvaluationContext context, Class<T> expectedResultType) throws EvaluationException {
        Object value = getValue(context);
        return ExpressionUtils.convertTypedValue(context, new TypedValue(value), expectedResultType);
    }

    @Override
    public <T> T getValue(Class<T> expectedResultType) throws EvaluationException {
        Object value = getValue();
        return ExpressionUtils.convertTypedValue(null, new TypedValue(value), expectedResultType);
    }

    @Override
    public boolean isWritable(EvaluationContext context) {
        return false;
    }

    public Expression[] getExpressions() {
        return this.expressions;
    }


    @Override
    public <T> T getValue(Object rootObject, Class<T> desiredResultType) throws EvaluationException {
        Object value = getValue(rootObject);
        return ExpressionUtils.convertTypedValue(null, new TypedValue(value), desiredResultType);
    }

    @Override
    public <T> T getValue(EvaluationContext context, Object rootObject, Class<T> desiredResultType)
            throws EvaluationException {
        Object value = getValue(context, rootObject);
        return ExpressionUtils.convertTypedValue(context, new TypedValue(value), desiredResultType);
    }

    @Override
    public Class<?> getValueType(Object rootObject) throws EvaluationException {
        return String.class;
    }

    @Override
    public Class<?> getValueType(EvaluationContext context, Object rootObject) throws EvaluationException {
        return String.class;
    }

    @Override
    public TypeDescriptor getValueTypeDescriptor(Object rootObject) throws EvaluationException {
        return TypeDescriptor.valueOf(String.class);
    }

    @Override
    public TypeDescriptor getValueTypeDescriptor(EvaluationContext context, Object rootObject) throws EvaluationException {
        return TypeDescriptor.valueOf(String.class);
    }

    @Override
    public boolean isWritable(EvaluationContext context, Object rootObject) throws EvaluationException {
        return false;
    }

    @Override
    public void setValue(EvaluationContext context, Object rootObject, Object value) throws EvaluationException {
//		throw new EvaluationException(this.expressionString, "Cannot call setValue on a composite expression");
    }

    @Override
    public boolean isWritable(Object rootObject) throws EvaluationException {
        return false;
    }

    @Override
    public void setValue(Object rootObject, Object value) throws EvaluationException {
//		throw new EvaluationException(this.expressionString, "Cannot call setValue on a composite expression");
    }

}
