package com.bmtech.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bmtech.utils.io.LineReader;
import com.bmtech.utils.io.diskMerge.gzipbased.GZIPRecordFactoryTest.E;

public abstract class ForEach {

	public static <T> void ascWithException(List<T> list, ForEachFuncBreakableSimple<T> t) throws Exception {
		int size = list.size();
		for (int x = 0; x < size; x++) {
			if (t.forEach(list.get(x))) {
				break;
			}
		}
	}

	public static <T> void ascWithException(Collection<T> c, ForEachFuncSimple<T> t) throws Exception {
		Iterator<T> itr = c.iterator();
		while (itr.hasNext()) {
			T x = itr.next();
			t.forEach(x);
		}
	}

	public static <T> void asc(Iterator<T> itr, ForEachFuncSimple<T> func) {
		try {
			ascWithException(itr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void ascWithException(Iterator<T> itr, ForEachFuncSimple<T> func) throws Exception {
		while (itr.hasNext()) {
			T t = itr.next();
			func.forEach(t);
		}
	}

	public static <T> void ascWithException(Collection<T> c, ForEachFuncBreakableSimple<T> t) throws Exception {
		Iterator<T> itr = c.iterator();
		while (itr.hasNext()) {
			T x = itr.next();
			if (t.forEach(x)) {
				break;
			}
		}
	}

	public static <T> void ascWithException(List<T> list, ForEachFuncSimple<T> t) throws Exception {
		int size = list.size();
		for (int x = 0; x < size; x++) {
			t.forEach(list.get(x));
		}

	}

	public static <T> void ascWithException(T[] array, ForEachFuncBreakableSimple<T> t) throws Exception {
		for (int x = 0; x < array.length; x++) {
			if (t.forEach(array[x])) {
				break;
			}
		}
	}

	public static <T> void ascWithException(T[] array, ForEachFuncSimple<T> func) throws Exception {
		for (int x = 0; x < array.length; x++) {
			func.forEach(array[x]);
		}
	}

	public static <T> void ascWithException(int[] arr, ForEachFuncSimple<Integer> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			func.forEach(arr[x]);
		}
	}

	public static <T> void ascWithException(int[] arr, ForEachFuncBreakableSimple<Integer> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			if (func.forEach(arr[x]))
				break;
		}
	}

	public static <T> void ascWithException(short[] arr, ForEachFuncSimple<Short> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			func.forEach(arr[x]);
		}
	}

	public static <T> void ascWithException(short[] arr, ForEachFuncBreakableSimple<Short> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			if (func.forEach(arr[x]))
				break;
		}
	}

	public static void ascWithException(float[] arr, ForEachFuncSimple<Float> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			func.forEach(arr[x]);
		}
	}

	public static void ascWithException(boolean[] arr, ForEachFuncSimple<Boolean> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			func.forEach(arr[x]);
		}
	}

	public static <T> void ascWithException(boolean[] arr, ForEachFuncBreakableSimple<Boolean> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			if (func.forEach(arr[x]))
				break;
		}
	}

	public static <T> void ascWithException(long[] arr, ForEachFuncSimple<Long> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			func.forEach(arr[x]);
		}
	}

	public static <T> void ascWithException(long[] arr, ForEachFuncBreakableSimple<Long> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			if (func.forEach(arr[x]))
				break;
		}
	}

	public static <T> void ascWithException(double[] arr, ForEachFuncSimple<Double> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			func.forEach(arr[x]);
		}
	}

	public static <T> void ascWithException(double[] arr, ForEachFuncBreakableSimple<Double> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			if (func.forEach(arr[x]))
				break;
		}
	}

	public static void ascWithException(byte[] arr, ForEachFuncSimple<Byte> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			func.forEach(arr[x]);
		}
	}

	public static void ascWithException(byte[] arr, ForEachFuncBreakableSimple<Byte> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			if (func.forEach(arr[x]))
				break;
		}
	}

	public static void ascWithException(char[] arr, ForEachFuncSimple<Character> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			func.forEach(arr[x]);
		}
	}

	public static void ascWithException(char[] arr, ForEachFuncBreakableSimple<Character> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			if (func.forEach(arr[x])) {
				break;
			}
		}
	}

	public static <T> void descWithException(List<T> list, ForEachFuncSimple<T> func) throws Exception {
		for (int x = list.size() - 1; x >= 0; x--) {
			func.forEach(list.get(x));
		}
	}

	public static <T> void descWithException(List<T> list, ForEachFuncBreakableSimple<T> func) throws Exception {

		for (int x = list.size() - 1; x >= 0; x--) {
			if (func.forEach(list.get(x)))
				break;
		}

	}

	public static <T> void descWithException(T[] array, ForEachFuncSimple<T> func) throws Exception {
		for (int x = array.length - 1; x >= 0; x--) {
			func.forEach(array[x]);
		}
	}

	public static <T> void descWithException(T[] array, ForEachFuncBreakableSimple<T> func) throws Exception {
		for (int x = array.length - 1; x >= 0; x--) {
			if (func.forEach(array[x]))
				break;
		}
	}

	public static void descWithException(int[] arr, ForEachFuncSimple<Integer> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			func.forEach(arr[x]);
		}
	}

	public static void descWithException(int[] arr, ForEachFuncBreakableSimple<Integer> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			if (func.forEach(arr[x]))
				break;
		}
	}

	public static <T> void descWithException(long[] arr, ForEachFuncSimple<Long> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			func.forEach(arr[x]);
		}
	}

	public static <T> void descWithException(long[] arr, ForEachFuncBreakableSimple<Long> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			if (func.forEach(arr[x]))
				break;
		}
	}

	public static <T> void descWithException(double[] arr, ForEachFuncSimple<Double> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			func.forEach(arr[x]);
		}
	}

	public static <T> void descWithException(double[] arr, ForEachFuncBreakableSimple<Double> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			if (func.forEach(arr[x]))
				break;
		}
	}

	public static void descWithException(byte[] arr, ForEachFuncSimple<Byte> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			func.forEach(arr[x]);
		}
	}

	public static void descWithException(byte[] arr, ForEachFuncBreakableSimple<Byte> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			if (func.forEach(arr[x]))
				break;
		}
	}

	public static void descWithException(char[] arr, ForEachFuncSimple<Character> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			func.forEach(arr[x]);
		}
	}

	public static void descWithException(char[] arr, ForEachFuncBreakableSimple<Character> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			if (func.forEach(arr[x])) {
				break;
			}
		}
	}

	public static void descWithException(short[] arr, ForEachFuncSimple<Short> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			func.forEach(arr[x]);
		}
	}

	public static void descWithException(short[] arr, ForEachFuncBreakableSimple<Short> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			if (func.forEach(arr[x])) {
				break;
			}
		}
	}

	public static void descWithException(boolean[] arr, ForEachFuncSimple<Boolean> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			func.forEach(arr[x]);
		}
	}

	public static void descWithException(boolean[] arr, ForEachFuncBreakableSimple<Boolean> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			if (func.forEach(arr[x])) {
				break;
			}
		}
	}

	public static <T> void asc(List<T> list, ForEachFuncBreakableSimple<T> t) {
		try {
			ascWithException(list, t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void asc(List<T> list, ForEachFuncSimple<T> t) {
		try {
			ascWithException(list, t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static <T> void asc(Collection<T> c, ForEachFuncSimple<T> t) {
		try {
			ascWithException(c, t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static <T> void asc(Collection<T> c, ForEachFuncBreakableSimple<T> t) {
		try {
			ascWithException(c, t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static <T> void asc(T[] array, ForEachFuncBreakableSimple<T> t) {
		try {
			ascWithException(array, t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void asc(T[] array, ForEachFuncSimple<T> func) {
		try {
			ascWithException(array, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void asc(int[] arr, ForEachFuncSimple<Integer> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void asc(int[] arr, ForEachFuncBreakableSimple<Integer> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void asc(short[] arr, ForEachFuncSimple<Short> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void asc(short[] arr, ForEachFuncBreakableSimple<Short> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void asc(boolean[] arr, ForEachFuncSimple<Boolean> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void asc(float[] arr, ForEachFuncSimple<Float> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void asc(boolean[] arr, ForEachFuncBreakableSimple<Boolean> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void asc(long[] arr, ForEachFuncSimple<Long> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void asc(long[] arr, ForEachFuncBreakableSimple<Long> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void asc(double[] arr, ForEachFuncSimple<Double> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void asc(double[] arr, ForEachFuncBreakableSimple<Double> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void asc(byte[] arr, ForEachFuncSimple<Byte> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void asc(byte[] arr, ForEachFuncBreakableSimple<Byte> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void asc(char[] arr, ForEachFuncSimple<Character> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void asc(Iterator<E> itr, ForEachFuncBreakableSimple<E> func) {
		try {
			while (itr.hasNext()) {
				E e = itr.next();
				func.forEach(e);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void asc(char[] arr, ForEachFuncBreakableSimple<Character> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void desc(List<T> list, ForEachFuncSimple<T> func) {
		try {
			descWithException(list, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void desc(List<T> list, ForEachFuncBreakableSimple<T> func) {

		try {
			descWithException(list, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static <T> void desc(T[] array, ForEachFuncSimple<T> func) {
		try {
			descWithException(array, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void desc(T[] array, ForEachFuncBreakableSimple<T> func) {
		try {
			descWithException(array, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(int[] arr, ForEachFuncSimple<Integer> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(int[] arr, ForEachFuncBreakableSimple<Integer> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void desc(long[] arr, ForEachFuncSimple<Long> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void desc(long[] arr, ForEachFuncBreakableSimple<Long> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void desc(double[] arr, ForEachFuncSimple<Double> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void desc(double[] arr, ForEachFuncBreakableSimple<Double> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(byte[] arr, ForEachFuncSimple<Byte> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(byte[] arr, ForEachFuncBreakableSimple<Byte> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(char[] arr, ForEachFuncSimple<Character> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(char[] arr, ForEachFuncBreakableSimple<Character> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(short[] arr, ForEachFuncSimple<Short> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(short[] arr, ForEachFuncBreakableSimple<Short> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(boolean[] arr, ForEachFuncSimple<Boolean> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(boolean[] arr, ForEachFuncBreakableSimple<Boolean> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void forRange(int to, ForRangeFunc func) {
		forRange(0, to, func);
	}

	public static void forRange(int from, int to, ForRangeFunc func) {
		try {
			for (int index = from; index < to; index++) {
				func.forIndex(index);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void ascWithException(List<T> list, ForEachFuncBreakable<T> t) throws Exception {
		int size = list.size();
		for (int x = 0; x < size; x++) {
			if (t.forEach(list.get(x), x)) {
				break;
			}
		}
	}

	public static <T> void ascWithException(Collection<T> c, ForEachFunc<T> t) throws Exception {
		Iterator<T> itr = c.iterator();
		int index = 0;
		while (itr.hasNext()) {
			T x = itr.next();
			t.forEach(x, index++);
		}
	}

	public static <T> void asc(Iterator<T> itr, ForEachFunc<T> func) {
		try {
			ascWithException(itr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void ascWithException(Iterator<T> itr, ForEachFunc<T> func) throws Exception {
		int i = 0;
		while (itr.hasNext()) {
			T t = itr.next();
			func.forEach(t, i++);
		}
	}

	public static <T> void ascWithException(Collection<T> c, ForEachFuncBreakable<T> t) throws Exception {
		Iterator<T> itr = c.iterator();
		int index = 0;
		while (itr.hasNext()) {
			T x = itr.next();
			if (t.forEach(x, index++)) {
				break;
			}
		}
	}

	public static <T> void ascWithException(List<T> list, ForEachFunc<T> t) throws Exception {
		int size = list.size();
		for (int x = 0; x < size; x++) {
			t.forEach(list.get(x), x);
		}

	}

	public static <T> void ascWithException(T[] array, ForEachFuncBreakable<T> t) throws Exception {
		for (int x = 0; x < array.length; x++) {
			if (t.forEach(array[x], x)) {
				break;
			}
		}
	}

	public static <T> void ascWithException(T[] array, ForEachFunc<T> func) throws Exception {
		for (int x = 0; x < array.length; x++) {
			func.forEach(array[x], x);
		}
	}

	public static <T> void ascWithException(int[] arr, ForEachFunc<Integer> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			func.forEach(arr[x], x);
		}
	}

	public static <T> void ascWithException(int[] arr, ForEachFuncBreakable<Integer> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			if (func.forEach(arr[x], x))
				break;
		}
	}

	public static <T> void ascWithException(short[] arr, ForEachFunc<Short> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			func.forEach(arr[x], x);
		}
	}

	public static <T> void ascWithException(short[] arr, ForEachFuncBreakable<Short> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			if (func.forEach(arr[x], x))
				break;
		}
	}

	public static void ascWithException(float[] arr, ForEachFunc<Float> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			func.forEach(arr[x], x);
		}
	}

	public static void ascWithException(boolean[] arr, ForEachFunc<Boolean> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			func.forEach(arr[x], x);
		}
	}

	public static <T> void ascWithException(boolean[] arr, ForEachFuncBreakable<Boolean> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			if (func.forEach(arr[x], x))
				break;
		}
	}

	public static <T> void ascWithException(long[] arr, ForEachFunc<Long> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			func.forEach(arr[x], x);
		}
	}

	public static <T> void ascWithException(long[] arr, ForEachFuncBreakable<Long> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			if (func.forEach(arr[x], x))
				break;
		}
	}

	public static <T> void ascWithException(double[] arr, ForEachFunc<Double> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			func.forEach(arr[x], x);
		}
	}

	public static <T> void ascWithException(double[] arr, ForEachFuncBreakable<Double> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			if (func.forEach(arr[x], x))
				break;
		}
	}

	public static void ascWithException(byte[] arr, ForEachFunc<Byte> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			func.forEach(arr[x], x);
		}
	}

	public static void ascWithException(byte[] arr, ForEachFuncBreakable<Byte> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			if (func.forEach(arr[x], x))
				break;
		}
	}

	public static void ascWithException(char[] arr, ForEachFunc<Character> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			func.forEach(arr[x], x);
		}
	}

	public static void ascWithException(char[] arr, ForEachFuncBreakable<Character> func) throws Exception {
		for (int x = 0; x < arr.length; x++) {
			if (func.forEach(arr[x], x)) {
				break;
			}
		}
	}

	public static <T> void descWithException(List<T> list, ForEachFunc<T> func) throws Exception {
		for (int x = list.size() - 1; x >= 0; x--) {
			func.forEach(list.get(x), x);
		}
	}

	public static <T> void descWithException(List<T> list, ForEachFuncBreakable<T> func) throws Exception {

		for (int x = list.size() - 1; x >= 0; x--) {
			if (func.forEach(list.get(x), x))
				break;
		}

	}

	public static <T> void descWithException(T[] array, ForEachFunc<T> func) throws Exception {
		for (int x = array.length - 1; x >= 0; x--) {
			func.forEach(array[x], x);
		}
	}

	public static <T> void descWithException(T[] array, ForEachFuncBreakable<T> func) throws Exception {
		for (int x = array.length - 1; x >= 0; x--) {
			if (func.forEach(array[x], x))
				break;
		}
	}

	public static void descWithException(int[] arr, ForEachFunc<Integer> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			func.forEach(arr[x], x);
		}
	}

	public static void descWithException(int[] arr, ForEachFuncBreakable<Integer> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			if (func.forEach(arr[x], x))
				break;
		}
	}

	public static <T> void descWithException(long[] arr, ForEachFunc<Long> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			func.forEach(arr[x], x);
		}
	}

	public static <T> void descWithException(long[] arr, ForEachFuncBreakable<Long> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			if (func.forEach(arr[x], x))
				break;
		}
	}

	public static <T> void descWithException(double[] arr, ForEachFunc<Double> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			func.forEach(arr[x], x);
		}
	}

	public static <T> void descWithException(double[] arr, ForEachFuncBreakable<Double> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			if (func.forEach(arr[x], x))
				break;
		}
	}

	public static void descWithException(byte[] arr, ForEachFunc<Byte> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			func.forEach(arr[x], x);
		}
	}

	public static void descWithException(byte[] arr, ForEachFuncBreakable<Byte> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			if (func.forEach(arr[x], x))
				break;
		}
	}

	public static void descWithException(char[] arr, ForEachFunc<Character> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			func.forEach(arr[x], x);
		}
	}

	public static void descWithException(char[] arr, ForEachFuncBreakable<Character> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			if (func.forEach(arr[x], x)) {
				break;
			}
		}
	}

	public static void descWithException(short[] arr, ForEachFunc<Short> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			func.forEach(arr[x], x);
		}
	}

	public static void descWithException(short[] arr, ForEachFuncBreakable<Short> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			if (func.forEach(arr[x], x)) {
				break;
			}
		}
	}

	public static void descWithException(boolean[] arr, ForEachFunc<Boolean> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			func.forEach(arr[x], x);
		}
	}

	public static void descWithException(boolean[] arr, ForEachFuncBreakable<Boolean> func) throws Exception {
		for (int x = arr.length - 1; x >= 0; x--) {
			if (func.forEach(arr[x], x)) {
				break;
			}
		}
	}

	public static <T> void asc(List<T> list, ForEachFuncBreakable<T> t) {
		try {
			ascWithException(list, t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void asc(List<T> list, ForEachFunc<T> t) {
		try {
			ascWithException(list, t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static <T> void asc(Collection<T> c, ForEachFunc<T> t) {
		try {
			ascWithException(c, t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static <T> void asc(Collection<T> c, ForEachFuncBreakable<T> t) {
		try {
			ascWithException(c, t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static <T> void asc(T[] array, ForEachFuncBreakable<T> t) {
		try {
			ascWithException(array, t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void asc(T[] array, ForEachFunc<T> func) {
		try {
			ascWithException(array, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void asc(int[] arr, ForEachFunc<Integer> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void asc(int[] arr, ForEachFuncBreakable<Integer> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void asc(short[] arr, ForEachFunc<Short> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void asc(short[] arr, ForEachFuncBreakable<Short> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void asc(boolean[] arr, ForEachFunc<Boolean> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void asc(float[] arr, ForEachFunc<Float> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void asc(boolean[] arr, ForEachFuncBreakable<Boolean> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void asc(long[] arr, ForEachFunc<Long> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void asc(long[] arr, ForEachFuncBreakable<Long> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void asc(double[] arr, ForEachFunc<Double> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void asc(double[] arr, ForEachFuncBreakable<Double> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void asc(byte[] arr, ForEachFunc<Byte> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void asc(byte[] arr, ForEachFuncBreakable<Byte> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void asc(char[] arr, ForEachFunc<Character> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void asc(Iterator<E> itr, ForEachFuncBreakable<E> func) {
		try {
			int i = 0;
			while (itr.hasNext()) {
				E e = itr.next();
				func.forEach(e, i++);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void asc(char[] arr, ForEachFuncBreakable<Character> func) {
		try {
			ascWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void desc(List<T> list, ForEachFunc<T> func) {
		try {
			descWithException(list, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void desc(List<T> list, ForEachFuncBreakable<T> func) {

		try {
			descWithException(list, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static <T> void desc(T[] array, ForEachFunc<T> func) {
		try {
			descWithException(array, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void desc(T[] array, ForEachFuncBreakable<T> func) {
		try {
			descWithException(array, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(int[] arr, ForEachFunc<Integer> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(int[] arr, ForEachFuncBreakable<Integer> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void desc(long[] arr, ForEachFunc<Long> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void desc(long[] arr, ForEachFuncBreakable<Long> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void desc(double[] arr, ForEachFunc<Double> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void desc(double[] arr, ForEachFuncBreakable<Double> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(byte[] arr, ForEachFunc<Byte> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(byte[] arr, ForEachFuncBreakable<Byte> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(char[] arr, ForEachFunc<Character> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(char[] arr, ForEachFuncBreakable<Character> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(short[] arr, ForEachFunc<Short> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(short[] arr, ForEachFuncBreakable<Short> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(boolean[] arr, ForEachFunc<Boolean> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void desc(boolean[] arr, ForEachFuncBreakable<Boolean> func) {
		try {
			descWithException(arr, func);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		String[] strs = new String[] { "str1", "str2", "str3", "str4", };

		ForEach.asc(strs, (s, v) -> {
			System.out.println(s + "@" + v);
		});

		List<String> lst = new ArrayList<>();
		lst.add("str1");
		lst.add("str11");
		lst.add("str111");
		ForEach.asc(lst, (s, v) -> {
			System.out.println(s + "@" + v);
		});

		int[] arr = new int[] { 9, 5, 2, 7 };
		ForEach.asc(arr, (s, v) -> {
			System.out.println(s + "@" + v);
			return true;
		});
		ForEach.desc(arr, (s, v) -> {
			System.out.println(s + "@" + v);

		});
	}

	public static int forEachLine(String file, ForEachFuncSimple<String> func) throws IOException {
		return forEachLine(new File(file), func);
	}

	public static int forEachLine(File file, ForEachFuncSimple<String> func) throws IOException {
		return forEachLine(file, Charset.defaultCharset(), func);
	}

	public static int forEachLine(String file, ForEachFunc<String> func) throws IOException {
		return forEachLine(new File(file), func);
	}

	public static int forEachLine(File file, ForEachFunc<String> func) throws IOException {
		return forEachLine(file, Charset.defaultCharset(), func);
	}

	/**
	 * trim line, and 0-length lines OR start-with '#' lines will be ignored(but index still will group)
	 * 
	 * @param file
	 * @param func
	 * @return
	 * @throws Exception
	 */

	private static int forEachLine(File file, Charset cs, ForEachFunc<String> func) throws IOException {

		LineReader lr = new LineReader(file, cs);

		int index = 0;
		try {

			while (lr.hasNext()) {
				try {
					String line = lr.next();
					func.forEach(line, index);
				} finally {
					index++;
				}
			}
		} catch (IOException iot) {
			throw iot;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
			lr.close();
		}
		return index;
	}

	private static int forEachLine(File file, Charset cs, ForEachFuncSimple<String> func) throws IOException {
		LineReader lr = new LineReader(file, cs);

		int index = 0;
		try {

			while (lr.hasNext()) {
				try {
					String line = lr.next();
					func.forEach(line);
				} finally {
					index++;
				}
			}
		} catch (IOException iot) {
			throw iot;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
			lr.close();
		}
		return index;
	}

	public static int forEachLine(String file, ForEachFuncBreakable<String> func) throws IOException {
		return forEachLine(new File(file), func);
	}

	public static int forEachLine(File file, ForEachFuncBreakable<String> func) throws IOException {
		return forEachLine(file, Charset.defaultCharset(), func);
	}

	public static int forEachLine(String file, Charset cs, ForEachFuncBreakable<String> func) throws IOException {
		return forEachLine(new File(file), cs, func);
	}

	public static int forEachLine(File file, Charset cs, ForEachFuncBreakable<String> func) throws IOException {
		LineReader lr = new LineReader(file, cs);
		int index = 0;
		try {

			while (lr.hasNext()) {
				String line = lr.next();

				boolean ret = func.forEach(line, index);
				if (ret)
					break;
				index++;
			}
		} catch (IOException e) {
			throw e;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
			lr.close();
		}
		return index;
	}

	public static <K, V> void map(Map<K, V> dayMap, ForEachFuncMap<K, V> func) {
		dayMap.forEach((key, value) -> {
			try {
				func.forEach(key, value);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

}
