package qunar.tc.bistoury.instrument.client.spring.el;


/**
 * Parses expression strings into compiled expressions that can be evaluated.
 * Supports parsing templates as well as standard expression strings.
 *
 * @author Keith Donald
 * @author Andy Clement
 * @since 3.0
 */
interface ExpressionParser {

    /**
     * Parse the expression string and return an Expression object you can use for repeated evaluation.
     * <p>Some examples:
     * <pre class="code">
     *     3 + 4
     *     name.firstName
     * </pre>
     *
     * @param expressionString the raw expression string to parse
     * @return an evaluator for the parsed expression
     * @throws ParseException an exception occurred during parsing
     */
    Expression parseExpression(String expressionString) throws ParseException;

    /**
     * Parse the expression string and return an Expression object you can use for repeated evaluation.
     * <p>Some examples:
     * <pre class="code">
     *     3 + 4
     *     name.firstName
     * </pre>
     *
     * @param expressionString the raw expression string to parse
     * @param context          a context for influencing this expression parsing routine (optional)
     * @return an evaluator for the parsed expression
     * @throws ParseException an exception occurred during parsing
     */
    Expression parseExpression(String expressionString, ParserContext context) throws ParseException;

}
