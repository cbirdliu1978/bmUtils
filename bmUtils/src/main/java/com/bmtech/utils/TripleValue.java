package com.bmtech.utils;

public enum TripleValue {
	eq(0), neg(-1), pos(1);

	public final int value;

	private TripleValue(int value) {
		this.value = value;
	}

	public static TripleValue get(int i) {
		if (i == 0) {
			return eq;
		} else if (i < 0)
			return neg;
		else
			return pos;
	}
}
