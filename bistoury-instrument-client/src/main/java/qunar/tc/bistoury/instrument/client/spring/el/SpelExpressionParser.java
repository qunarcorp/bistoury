package qunar.tc.bistoury.instrument.client.spring.el;

/**
 * SpEL parser. Instances are reusable and thread-safe.
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public class SpelExpressionParser extends TemplateAwareExpressionParser {

    private final SpelParserConfiguration configuration;


    /**
     * Create a parser with default settings.
     */
    public SpelExpressionParser() {
        this.configuration = new SpelParserConfiguration();
    }

    /**
     * Create a parser with the specified configuration.
     *
     * @param configuration custom configuration options
     */
    public SpelExpressionParser(SpelParserConfiguration configuration) {
        Assert.notNull(configuration, "SpelParserConfiguration must not be null");
        this.configuration = configuration;
    }


    public SpelExpression parseRaw(String expressionString) throws ParseException {
        return doParseExpression(expressionString, null);
    }

    @Override
    protected SpelExpression doParseExpression(String expressionString, ParserContext context) throws ParseException {
        return new InternalSpelExpressionParser(this.configuration).doParseExpression(expressionString, context);
    }

}
