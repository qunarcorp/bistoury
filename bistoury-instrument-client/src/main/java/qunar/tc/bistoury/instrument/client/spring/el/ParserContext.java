package qunar.tc.bistoury.instrument.client.spring.el;

/**
 * Input provided to an expression parser that can influence an expression
 * parsing/compilation routine.
 *
 * @author Keith Donald
 * @author Andy Clement
 * @since 3.0
 */
interface ParserContext {

    /**
     * Whether or not the expression being parsed is a template. A template expression
     * consists of literal text that can be mixed with evaluatable blocks. Some examples:
     * <pre class="code">
     * 	   Some literal text
     *     Hello #{name.firstName}!
     *     #{3 + 4}
     * </pre>
     *
     * @return true if the expression is a template, false otherwise
     */
    boolean isTemplate();

    /**
     * For template expressions, returns the prefix that identifies the start of an
     * expression block within a string. For example: "${"
     *
     * @return the prefix that identifies the start of an expression
     */
    String getExpressionPrefix();

    /**
     * For template expressions, return the prefix that identifies the end of an
     * expression block within a string. For example: "}"
     *
     * @return the suffix that identifies the end of an expression
     */
    String getExpressionSuffix();


    /**
     * The default ParserContext implementation that enables template expression parsing
     * mode. The expression prefix is #{ and the expression suffix is }.
     *
     * @see #isTemplate()
     */
    public static final ParserContext TEMPLATE_EXPRESSION = new ParserContext() {

        @Override
        public String getExpressionPrefix() {
            return "#{";
        }

        @Override
        public String getExpressionSuffix() {
            return "}";
        }

        @Override
        public boolean isTemplate() {
            return true;
        }

    };

}
