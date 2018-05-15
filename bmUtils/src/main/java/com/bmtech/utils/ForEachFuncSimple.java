package com.bmtech.utils;

@FunctionalInterface
public interface ForEachFuncSimple<T> {
	public void forEach(T t) throws Exception;
}