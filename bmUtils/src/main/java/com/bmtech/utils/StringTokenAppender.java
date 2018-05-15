package com.bmtech.utils;

import java.util.Collection;

public class StringTokenAppender {
	private final StringBuilder sb;

	public StringTokenAppender() {
		this(new StringBuilder());
	}

	public StringTokenAppender(StringBuilder sb) {
		this.sb = sb;
	}

	public void appendCollection(Collection<?> var) {
		for (Object a : var) {
			append(a);
		}
	}

	public void appendArray(Object[] var) {
		for (Object a : var) {
			append(a);
		}
	}

	public void appendArray(String[] var) {
		for (String a : var) {
			append(a);
		}
	}

	public void appendArray(long[] var) {
		for (long a : var) {
			append(a);
		}
	}

	public void appendArray(double[] var) {
		for (double a : var) {
			append(a);
		}
	}

	public void appendArray(short[] var) {
		for (short a : var) {
			append(a);
		}
	}

	public void appendArray(int[] var) {
		for (int a : var) {
			append(a);
		}
	}

	public void append(Object v) {
		if (sb.length() > 0)
			sb.append("\t");
		sb.append(v.toString());
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}
