package qunar.tc.bistoury.instrument.client.spring.el;


/**
 * Common utility functions that may be used by any Expression Language provider.
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
abstract class ExpressionUtils {

	/**
     * Determines if there is a type converter available in the specified context and
	 * attempts to use it to convert the supplied value to the specified type. Throws an
	 * exception if conversion is not possible.
	 * @param context the evaluation context that may define a type converter
	 * @param typedValue the value to convert and a type descriptor describing it
	 * @param targetType the type to attempt conversion to
	 * @return the converted value
	 * @throws EvaluationException if there is a problem during conversion or conversion
	 * of the value to the specified type is not supported
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convertTypedValue(EvaluationContext context, TypedValue typedValue, Class<T> targetType) {
		Object value = typedValue.getValue();
		if (targetType == null) {
			return (T) value;
		}
		if (context != null) {
			return (T) context.getTypeConverter().convertValue(
					value, typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(targetType));
		}
		if (ClassUtils.isAssignableValue(targetType, value)) {
			return (T) value;
		}
		throw new EvaluationException("Cannot convert value '" + value + "' to type '" + targetType.getName() + "'");
	}

	/**
	 * Attempt to convert a typed value to an int using the supplied type converter.
	 */
	public static int toInt(TypeConverter typeConverter, TypedValue typedValue) {
		return (Integer) typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(),
				TypeDescriptor.valueOf(Integer.class));
	}

	/**
	 * Attempt to convert a typed value to a boolean using the supplied type converter.
	 */
	public static boolean toBoolean(TypeConverter typeConverter, TypedValue typedValue) {
		return (Boolean) typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(),
				TypeDescriptor.valueOf(Boolean.class));
	}

	/**
	 * Attempt to convert a typed value to a double using the supplied type converter.
	 */
	public static double toDouble(TypeConverter typeConverter, TypedValue typedValue) {
		return (Double) typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(),
				TypeDescriptor.valueOf(Double.class));
	}

	/**
	 * Attempt to convert a typed value to a long using the supplied type converter.
	 */
	public static long toLong(TypeConverter typeConverter, TypedValue typedValue) {
		return (Long) typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(),
				TypeDescriptor.valueOf(Long.class));
	}

	/**
	 * Attempt to convert a typed value to a char using the supplied type converter.
	 */
	public static char toChar(TypeConverter typeConverter, TypedValue typedValue) {
		return (Character) typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(),
				TypeDescriptor.valueOf(Character.class));
	}

	/**
	 * Attempt to convert a typed value to a short using the supplied type converter.
	 */
	public static short toShort(TypeConverter typeConverter, TypedValue typedValue) {
		return (Short) typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(),
				TypeDescriptor.valueOf(Short.class));
	}

	/**
	 * Attempt to convert a typed value to a float using the supplied type converter.
	 */
	public static float toFloat(TypeConverter typeConverter, TypedValue typedValue) {
		return (Float) typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(),
				TypeDescriptor.valueOf(Float.class));
	}

	/**
	 * Attempt to convert a typed value to a byte using the supplied type converter.
	 */
	public static byte toByte(TypeConverter typeConverter, TypedValue typedValue) {
		return (Byte) typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(),
				TypeDescriptor.valueOf(Byte.class));
	}

}
