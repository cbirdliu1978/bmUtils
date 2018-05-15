package com.bmtech.utils;

@FunctionalInterface
public interface ForEachFuncBreakableSimple<T> {
	public boolean forEach(T t) throws Exception;
}