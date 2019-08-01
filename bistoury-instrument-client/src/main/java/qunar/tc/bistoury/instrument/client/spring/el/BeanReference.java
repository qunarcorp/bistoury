package qunar.tc.bistoury.instrument.client.spring.el;


/**
 * Represents a bean reference to a type, for example <tt>@foo</tt> or <tt>@'foo.bar'</tt>.
 * For a FactoryBean the syntax <tt>&foo</tt> can be used to access the factory itself.
 *
 * @author Andy Clement
 */
class BeanReference extends SpelNodeImpl {

    private final static String FACTORY_BEAN_PREFIX = "&";

    private final String beanName;


    public BeanReference(int pos, String beanName) {
        super(pos);
        this.beanName = beanName;
    }


    @Override
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        BeanResolver beanResolver = state.getEvaluationContext().getBeanResolver();
        if (beanResolver == null) {
            throw new SpelEvaluationException(
                    getStartPosition(), SpelMessage.NO_BEAN_RESOLVER_REGISTERED, this.beanName);
        }

        try {
            return new TypedValue(beanResolver.resolve(state.getEvaluationContext(), this.beanName));
        } catch (AccessException ex) {
            throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.EXCEPTION_DURING_BEAN_RESOLUTION,
                    this.beanName, ex.getMessage());
        }
    }

    @Override
    public String toStringAST() {
        StringBuilder sb = new StringBuilder();
        if (!this.beanName.startsWith(FACTORY_BEAN_PREFIX)) {
            sb.append("@");
        }
        if (!this.beanName.contains(".")) {
            sb.append(this.beanName);
        } else {
            sb.append("'").append(this.beanName).append("'");
        }
        return sb.toString();
    }

}
