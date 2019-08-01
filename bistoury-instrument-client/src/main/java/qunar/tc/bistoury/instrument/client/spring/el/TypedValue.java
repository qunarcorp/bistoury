package qunar.tc.bistoury.instrument.client.spring.el;


/**
 * Encapsulates an object and a {@link TypeDescriptor} that describes it.
 * The type descriptor can contain generic declarations that would not
 * be accessible through a simple {@code getClass()} call on the object.
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
class TypedValue {

	public static final TypedValue NULL = new TypedValue(null);


	private final Object value;

	private TypeDescriptor typeDescriptor;


	/**
     * Create a {@link TypedValue} for a simple object. The {@link TypeDescriptor}
	 * is inferred from the object, so no generic declarations are preserved.
	 * @param value the object value
	 */
	public TypedValue(Object value) {
		this.value = value;
		this.typeDescriptor = null;  // initialized when/if requested
	}

	/**
	 * Create a {@link TypedValue} for a particular value with a particular
	 * {@link TypeDescriptor} which may contain additional generic declarations.
	 * @param value the object value
	 * @param typeDescriptor a type descriptor describing the type of the value
	 */
	public TypedValue(Object value, TypeDescriptor typeDescriptor) {
		this.value = value;
		this.typeDescriptor = typeDescriptor;
	}


	public Object getValue() {
		return this.value;
	}

	public TypeDescriptor getTypeDescriptor() {
		if (this.typeDescriptor == null && this.value != null) {
			this.typeDescriptor = TypeDescriptor.forObject(this.value);
		}
		return this.typeDescriptor;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TypedValue)) {
			return false;
		}
		TypedValue otherTv = (TypedValue) other;
		// Avoid TypeDescriptor initialization if not necessary
		return (ObjectUtils.nullSafeEquals(this.value, otherTv.value) &&
				((this.typeDescriptor == null && otherTv.typeDescriptor == null) ||
						ObjectUtils.nullSafeEquals(getTypeDescriptor(), otherTv.getTypeDescriptor())));
	}

	@Override
	public int hashCode() {
		return ObjectUtils.nullSafeHashCode(this.value);
	}

	@Override
	public String toString() {
		return "TypedValue: '" + this.value + "' of [" + getTypeDescriptor() + "]";
	}

}
