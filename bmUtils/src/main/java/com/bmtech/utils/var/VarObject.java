package com.bmtech.utils.var;

public class VarObject<T> {
	public T value;

	public VarObject(T var) {
		this.value = var;
	}

	public VarObject() {
		this(null);
	}

	public String toString() {
		return String.valueOf(value);
	}
}
