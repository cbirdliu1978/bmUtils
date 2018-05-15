package com.bmtech.utils;

@FunctionalInterface
public interface ForEachFunc<T> {
	public void forEach(T t, int index) throws Exception;
}