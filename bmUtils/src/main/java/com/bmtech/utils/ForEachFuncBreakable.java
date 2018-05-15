package com.bmtech.utils;

@FunctionalInterface
public interface ForEachFuncBreakable<T> {
	public boolean forEach(T t, int index) throws Exception;
}