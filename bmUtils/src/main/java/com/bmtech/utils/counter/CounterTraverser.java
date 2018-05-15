package com.bmtech.utils.counter;

import java.util.List;
import java.util.Map.Entry;

public abstract class CounterTraverser<T> {
	final Counter<T> t;

	public CounterTraverser(Counter<T> t) {
		this.t = t;
	}

	public void top() {
		this.top(Integer.MAX_VALUE);
	}

	public void top(int num) {
		List<Entry<T, NumCount>> lst = t.topEntry(num);
		for (Entry<T, NumCount> x : lst) {
			T t = x.getKey();
			int value = x.getValue().intValue();
			forObject(t, value);
		}
	}

	public abstract void forObject(T t, int count);

}
