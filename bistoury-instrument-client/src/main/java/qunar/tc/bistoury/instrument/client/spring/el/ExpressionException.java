package qunar.tc.bistoury.instrument.client.spring.el;

/**
 * Super class for exceptions that can occur whilst processing expressions.
 *
 * @author Andy Clement
 * @author Phil Webb
 * @since 3.0
 */
@SuppressWarnings("serial")
class ExpressionException extends RuntimeException {

    protected String expressionString;

    protected int position;  // -1 if not known; should be known in all reasonable cases


    /**
     * Construct a new expression exception.
     *
     * @param message a descriptive message
     */
    public ExpressionException(String message) {
        super(message);
    }

    /**
     * Construct a new expression exception.
     *
     * @param message a descriptive message
     * @param cause   the underlying cause of this exception
     */
    public ExpressionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct a new expression exception.
     *
     * @param expressionString the expression string
     * @param message          a descriptive message
     */
    public ExpressionException(String expressionString, String message) {
        super(message);
        this.expressionString = expressionString;
        this.position = -1;
    }

    /**
     * Construct a new expression exception.
     *
     * @param expressionString the expression string
     * @param position         the position in the expression string where the problem occurred
     * @param message          a descriptive message
     */
    public ExpressionException(String expressionString, int position, String message) {
        super(message);
        this.expressionString = expressionString;
        this.position = position;
    }

    /**
     * Construct a new expression exception.
     *
     * @param position the position in the expression string where the problem occurred
     * @param message  a descriptive message
     */
    public ExpressionException(int position, String message) {
        super(message);
        this.position = position;
    }

    /**
     * Construct a new expression exception.
     *
     * @param position the position in the expression string where the problem occurred
     * @param message  a descriptive message
     * @param cause    the underlying cause of this exception
     */
    public ExpressionException(int position, String message, Throwable cause) {
        super(message, cause);
        this.position = position;
    }


    /**
     * Return the expression string.
     */
    public final String getExpressionString() {
        return this.expressionString;
    }

    /**
     * Return the position in the expression string where the problem occurred.
     */
    public final int getPosition() {
        return this.position;
    }

    /**
     * Return the exception message.
     * As of Spring 4.0, this method returns the same result as {@link #toDetailedString()}.
     *
     * @see #getSimpleMessage()
     * @see Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        return toDetailedString();
    }

    /**
     * Return a detailed description of this exception, including the expression
     * String and position (if available) as well as the actual exception message.
     */
    public String toDetailedString() {
        if (this.expressionString != null) {
            StringBuilder output = new StringBuilder();
            output.append("Expression [");
            output.append(this.expressionString);
            output.append("]");
            if (this.position >= 0) {
                output.append(" @");
                output.append(this.position);
            }
            output.append(": ");
            output.append(getSimpleMessage());
            return output.toString();
        } else {
            return getSimpleMessage();
        }
    }

    /**
     * Return the exception simple message without including the expression
     * that caused the failure.
     *
     * @since 4.0
     */
    public String getSimpleMessage() {
        return super.getMessage();
    }

}