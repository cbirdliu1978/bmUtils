package com.bmtech.utils;

@FunctionalInterface
public interface ForEachFuncMap<K, V> {
	public void forEach(K key, V value) throws Exception;
}