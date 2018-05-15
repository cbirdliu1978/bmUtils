package com.bmtech.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class RecursiveToStringStyle extends ToStringStyle {

	/**
	 * Required for serialization support.
	 * 
	 * @see java.io.Serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * <p>
	 * Constructor.
	 * </p>
	 */
	public RecursiveToStringStyle() {
		super();
	}

	@Override
	public void appendDetail(final StringBuffer buffer, final String fieldName, final Object value) {
		if (value != null && !isPrimitiveWrapper(value.getClass()) && !String.class.equals(value.getClass())
				&& accept(value.getClass())) {
			buffer.append(ReflectionToStringBuilder.toString(value, this));
		} else {
			super.appendDetail(buffer, fieldName, value);
		}
	}

	@Override
	protected void appendDetail(final StringBuffer buffer, final String fieldName,
			@SuppressWarnings("rawtypes") final Collection coll) {
		appendClassName(buffer, coll);
		appendIdentityHashCode(buffer, coll);
		appendDetail(buffer, fieldName, coll.toArray());
	}

	/**
	 * Returns whether or not to recursively format the given <code>Class</code>
	 * . By default, this method always returns {@code true}, but may be
	 * overwritten by sub-classes to filter specific classes.
	 *
	 * @param clazz
	 *            The class to test.
	 * @return Whether or not to recursively format the given <code>Class</code>
	 *         .
	 */
	protected boolean accept(final Class<?> clazz) {
		return true;
	}

	private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<Class<?>, Class<?>>();
	static {
		primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
		primitiveWrapperMap.put(Byte.TYPE, Byte.class);
		primitiveWrapperMap.put(Character.TYPE, Character.class);
		primitiveWrapperMap.put(Short.TYPE, Short.class);
		primitiveWrapperMap.put(Integer.TYPE, Integer.class);
		primitiveWrapperMap.put(Long.TYPE, Long.class);
		primitiveWrapperMap.put(Double.TYPE, Double.class);
		primitiveWrapperMap.put(Float.TYPE, Float.class);
		primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
	}
	private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = new HashMap<Class<?>, Class<?>>();
	static {
		for (final Class<?> primitiveClass : primitiveWrapperMap.keySet()) {
			final Class<?> wrapperClass = primitiveWrapperMap.get(primitiveClass);
			if (!primitiveClass.equals(wrapperClass)) {
				wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
			}
		}
	}

	public static boolean isPrimitiveOrWrapper(final Class<?> type) {
		if (type == null) {
			return false;
		}
		return type.isPrimitive() || isPrimitiveWrapper(type);
	}

	/**
	 * Returns whether the given {@code type} is a primitive wrapper (
	 * {@link Boolean}, {@link Byte}, {@link Character}, {@link Short},
	 * {@link Integer}, {@link Long}, {@link Double}, {@link Float}).
	 *
	 * @param type
	 *            The class to query or null.
	 * @return true if the given {@code type} is a primitive wrapper (
	 *         {@link Boolean}, {@link Byte}, {@link Character}, {@link Short},
	 *         {@link Integer}, {@link Long}, {@link Double}, {@link Float}).
	 * @since 3.1
	 */
	public static boolean isPrimitiveWrapper(final Class<?> type) {
		if (type == Object.class || type == java.lang.Class.class)
			return true;
		return wrapperPrimitiveMap.containsKey(type);
	}

}