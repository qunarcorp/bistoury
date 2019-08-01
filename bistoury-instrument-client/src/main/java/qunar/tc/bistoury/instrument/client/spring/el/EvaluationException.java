package qunar.tc.bistoury.instrument.client.spring.el;

/**
 * Represent an exception that occurs during expression evaluation.
 *
 * @author Andy Clement
 * @since 3.0
 */
@SuppressWarnings("serial")
public
class EvaluationException extends ExpressionException {

	/**
     * Create a new expression evaluation exception.
	 * @param message description of the problem that occurred
	 */
	public EvaluationException(String message) {
		super(message);
	}

	/**
	 * Create a new expression evaluation exception.
	 * @param message description of the problem that occurred
	 * @param cause the underlying cause of this exception
	 */
	public EvaluationException(String message, Throwable cause) {
		super(message,cause);
	}

	/**
	 * Create a new expression evaluation exception.
	 * @param position the position in the expression where the problem occurred
	 * @param message description of the problem that occurred
	 */
	public EvaluationException(int position, String message) {
		super(position, message);
	}

	/**
	 * Create a new expression evaluation exception.
	 * @param expressionString the expression that could not be evaluated
	 * @param message description of the problem that occurred
	 */
	public EvaluationException(String expressionString, String message) {
		super(expressionString, message);
	}

	/**
	 * Create a new expression evaluation exception.
	 * @param position the position in the expression where the problem occurred
	 * @param message description of the problem that occurred
	 * @param cause the underlying cause of this exception
	 */
	public EvaluationException(int position, String message, Throwable cause) {
		super(position, message, cause);
	}

}
