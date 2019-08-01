package qunar.tc.bistoury.instrument.client.spring.el;

/**
 * Represent an exception that occurs during expression parsing.
 *
 * @author Andy Clement
 * @since 3.0
 */
@SuppressWarnings("serial")
public class ParseException extends ExpressionException {

	/**
     * Create a new expression parsing exception.
	 * @param expressionString the expression string that could not be parsed
	 * @param position the position in the expression string where the problem occurred
	 * @param message description of the problem that occurred
	 */
	public ParseException(String expressionString, int position, String message) {
		super(expressionString, position, message);
	}

	/**
	 * Create a new expression parsing exception.
	 * @param position the position in the expression string where the problem occurred
	 * @param message description of the problem that occurred
	 * @param cause the underlying cause of this exception
	 */
	public ParseException(int position, String message, Throwable cause) {
		super(position, message, cause);
	}

	/**
	 * Create a new expression parsing exception.
	 * @param position the position in the expression string where the problem occurred
	 * @param message description of the problem that occurred
	 */
	public ParseException(int position, String message) {
		super(position, message);
	}

}
