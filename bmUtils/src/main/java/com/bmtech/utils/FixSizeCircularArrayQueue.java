package com.bmtech.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class FixSizeCircularArrayQueue<T> {
	private LinkedList<T> q = new LinkedList<T>();
	private int size;

	public FixSizeCircularArrayQueue(int size) {
		this.size = size;
	}

	public synchronized T enqueue(T t) {
		q.add(t);
		T ret = null;
		while (q.size() > size) {
			ret = q.removeFirst();
		}
		return ret;
	}

	public boolean isFull() {
		return this.size == q.size();
	}

	public T getFirst() {
		return q.getFirst();
	}

	public T getLast() {
		return getLast(0);
	}

	public T getLast(int last) {
		return q.get(q.size() - 1 - last);
	}

	public T get(int index) {
		return q.get(index);
	}

	public ListIterator<T> iterator() {
		return q.listIterator();

	}

	public int size() {
		return q.size();
	}

	@Override
	public String toString() {
		return this.q.toString();
	}

	public ArrayList<T> copyQueue() {
		ArrayList<T> lst = new ArrayList<T>();
		lst.addAll(this.q);
		return lst;
	}

	public FixSizeCircularArrayQueue<T> copy() {
		FixSizeCircularArrayQueue<T> ret = new FixSizeCircularArrayQueue<>(this.size);
		ret.q.addAll(this.q);
		return ret;
	}

	public List<T> toList() {
		List<T> ret = new ArrayList<>(q);
		return ret;
	}
}
