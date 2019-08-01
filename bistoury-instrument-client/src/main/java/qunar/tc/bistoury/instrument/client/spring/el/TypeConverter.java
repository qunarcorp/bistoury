package qunar.tc.bistoury.instrument.client.spring.el;


/**
 * A type converter can convert values between different types encountered during
 * expression evaluation. This is an SPI for the expression parser; see
 * {@link ConversionService} for the primary
 * user API to Spring's conversion facilities.
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
interface TypeConverter {

	/**
     * Return {@code true} if the type converter can convert the specified type
	 * to the desired target type.
	 * @param sourceType a type descriptor that describes the source type
	 * @param targetType a type descriptor that describes the requested result type
	 * @return {@code true} if that conversion can be performed
	 */
	boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType);

	/**
	 * Convert (or coerce) a value from one type to another, for example from a
	 * {@code boolean} to a {@code String}.
	 * <p>The {@link TypeDescriptor} parameters enable support for typed collections:
	 * A caller may prefer a {@code List&lt;Integer&gt;}, for example, rather than
	 * simply any {@code List}.
	 * @param value the value to be converted
	 * @param sourceType a type descriptor that supplies extra information about the
	 * source object
	 * @param targetType a type descriptor that supplies extra information about the
	 * requested result type
	 * @return the converted value
	 * @throws EvaluationException if conversion failed or is not possible to begin with
	 */
	Object convertValue(Object value, TypeDescriptor sourceType, TypeDescriptor targetType);

}
