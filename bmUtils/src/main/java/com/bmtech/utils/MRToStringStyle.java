package com.bmtech.utils;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class MRToStringStyle extends RecursiveToStringStyle {

	private static final long serialVersionUID = 1L;
	private int indent = 2;

	private int spaces = 2;

	public MRToStringStyle() {
		resetIndent();
	}

	private void resetIndent() {
		setArrayStart("{" + SystemUtils.LINE_SEPARATOR + spacer(spaces));
		setArraySeparator("," + SystemUtils.LINE_SEPARATOR + spacer(spaces));
		setArrayEnd(SystemUtils.LINE_SEPARATOR + spacer(spaces - indent) + "}");

		setContentStart("[" + SystemUtils.LINE_SEPARATOR + spacer(spaces));
		setFieldSeparator("," + SystemUtils.LINE_SEPARATOR + spacer(spaces));
		setContentEnd(SystemUtils.LINE_SEPARATOR + spacer(spaces - indent) + "]");
	}

	private StringBuilder spacer(int spaces) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < spaces; i++) {
			sb.append(" ");
		}
		return sb;
	}

	@Override
	public void appendDetail(StringBuffer buffer, String fieldName, Object value) {
		if (value == null) {
			super.appendDetail(buffer, fieldName, "null");
		} else {
			String packName = value.getClass().getPackage().getName();

			if (value instanceof Collections) {
				buffer.append("[");
				int pos = 0;
				for (Object o : (Collection<?>) value) {
					if (pos > 0)
						buffer.append(", ");

					appendDetail(buffer, "", o);
					pos++;
				}
				buffer.append("] Collection-size = ");
				buffer.append(((Collection<?>) value).size());
				buffer.append(" ");

			} else if (!packName.startsWith("java.") && !packName.startsWith("sun.") && !packName.startsWith("javax.")
					&& !packName.startsWith("jdk.") && (!isPrimitiveWrapper(value.getClass()))
					&& (!String.class.equals(value.getClass())) && (accept(value.getClass()))) {
				spaces += indent;
				resetIndent();
				buffer.append(ReflectionToStringBuilder.toString(value, this));
				spaces -= indent;
				resetIndent();
			} else {
				super.appendDetail(buffer, fieldName, value.toString());
			}
		}
	}

	@Override
	protected void appendDetail(StringBuffer buffer, String fieldName, Object[] array) {
		spaces += indent;
		resetIndent();
		super.appendDetail(buffer, fieldName, array);
		spaces -= indent;
		resetIndent();
	}

	@Override
	protected void reflectionAppendArrayDetail(StringBuffer buffer, String fieldName, Object array) {
		spaces += indent;
		resetIndent();
		if (array.getClass().isArray()) {

			int len = java.lang.reflect.Array.getLength(array);
			spaces += indent;
			resetIndent();
			{
				buffer.append(getArrayStart());
				for (int i = 0; i < len; i++) {
					if (i > 0) {
						buffer.append(getArraySeparator());
					}
					appendDetail(buffer, fieldName, java.lang.reflect.Array.get(array, i));
				}
				buffer.append(getArrayEnd());
			}
			spaces -= indent;
			resetIndent();

		} else {
			super.appendDetail(buffer, fieldName, array);
		}
		spaces -= indent;
		resetIndent();
	}

	@Override
	protected void appendDetail(StringBuffer buffer, String fieldName, long[] array) {
		spaces += indent;
		resetIndent();
		super.appendDetail(buffer, fieldName, array);
		spaces -= indent;
		resetIndent();
	}

	@Override
	protected void appendDetail(StringBuffer buffer, String fieldName, int[] array) {
		spaces += indent;
		resetIndent();
		super.appendDetail(buffer, fieldName, array);
		spaces -= indent;
		resetIndent();
	}

	@Override
	protected void appendDetail(StringBuffer buffer, String fieldName, short[] array) {
		spaces += indent;
		resetIndent();
		super.appendDetail(buffer, fieldName, array);
		spaces -= indent;
		resetIndent();
	}

	@Override
	protected void appendDetail(StringBuffer buffer, String fieldName, byte[] array) {
		spaces += indent;
		resetIndent();
		super.appendDetail(buffer, fieldName, array);
		spaces -= indent;
		resetIndent();
	}

	@Override
	protected void appendDetail(StringBuffer buffer, String fieldName, char[] array) {
		spaces += indent;
		resetIndent();
		super.appendDetail(buffer, fieldName, array);
		spaces -= indent;
		resetIndent();
	}

	@Override
	protected void appendDetail(StringBuffer buffer, String fieldName, double[] array) {
		spaces += indent;
		resetIndent();
		super.appendDetail(buffer, fieldName, array);
		spaces -= indent;
		resetIndent();
	}

	@Override
	protected void appendDetail(StringBuffer buffer, String fieldName, float[] array) {
		spaces += indent;
		resetIndent();
		super.appendDetail(buffer, fieldName, array);
		spaces -= indent;
		resetIndent();
	}

	@Override
	protected void appendDetail(StringBuffer buffer, String fieldName, boolean[] array) {
		spaces += indent;
		resetIndent();
		super.appendDetail(buffer, fieldName, array);
		spaces -= indent;
		resetIndent();
	}

}
