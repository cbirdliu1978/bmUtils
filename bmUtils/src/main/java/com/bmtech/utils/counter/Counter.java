package com.bmtech.utils.counter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bmtech.utils.Misc;
import com.bmtech.utils.var.VarLong;

public class Counter<T> {
	protected HashMap<T, NumCount> map = new HashMap<T, NumCount>();

	public Counter() {

	}

	public int count(T key) {
		return this.count(map, key, 1);
	}

	public int count(T key, int num) {
		return this.count(map, key, num);
	}

	public int count(T key, NumCount num) {
		return this.count(map, key, num.intValue());
	}

	public int get(T key) {
		NumCount k = this.map.get(key);
		if (k == null)
			return 0;
		return k.intValue();
	}

	public void count(HashMap<T, NumCount> map, T key, NumCount num) {
		count(map, key, num.intValue());
	}

	public int count(HashMap<T, NumCount> map, T key, int num) {
		NumCount cnt = map.get(key);
		if (cnt == null) {
			map.put(key, new NumCount(num));
			return num;
		} else {
			cnt.increaseCount(num);
			return cnt.intValue();
		}
	}

	public List<Entry<T, NumCount>> entryList() {
		return this.topEntry(map, map.size());
	}

	public List<Entry<T, NumCount>> topEntry(int num) {
		return this.topEntry(map, num);
	}

	public List<Entry<T, NumCount>> topEntry() {
		return this.topEntry(Integer.MAX_VALUE);
	}

	public List<T> topObjects(int num) {
		List<Entry<T, NumCount>> lst = this.topEntry(map, num);
		List<T> l = new ArrayList<T>(lst.size());
		for (Entry<T, NumCount> e : lst) {
			l.add(e.getKey());
		}
		return l;
	}

	class IEntry<K, V> implements Map.Entry<K, V> {
		final Map.Entry<K, V> e;

		IEntry(Map.Entry<K, V> e) {
			this.e = e;
		}

		@Override
		public K getKey() {
			return e.getKey();
		}

		@Override
		public V getValue() {
			return e.getValue();
		}

		@Override
		public V setValue(V value) {
			return e.setValue(value);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(e.getKey());
			sb.append('(');
			sb.append(e.getValue());
			sb.append(')');
			return sb.toString();
		}

	}

	public List<Entry<T, NumCount>> topEntry(HashMap<T, NumCount> map, int num) {
		ArrayList<Entry<T, NumCount>> lst = new ArrayList<Entry<T, NumCount>>(num > map.size() ? map.size() : num);
		for (Entry<T, NumCount> k : map.entrySet()) {
			lst.add(new IEntry<T, NumCount>(k));
		}

		Collections.sort(lst, new Comparator<Entry<T, NumCount>>() {
			@Override
			public int compare(Entry<T, NumCount> o1, Entry<T, NumCount> o2) {
				return o2.getValue().intValue() - o1.getValue().intValue();
			}
		});
		int range = lst.size() > num ? num : lst.size();

		return lst.subList(0, range);
	}

	public Map<T, NumCount> getMap() {
		return this.map;
	}

	public int valeOf(T t) {
		NumCount r = map.get(t);
		if (r == null)
			return 0;
		return r.intValue();
	}

	@Override
	public String toString() {
		List<Entry<T, NumCount>> lst = this.topEntry();
		lst.sort((a, b) -> {
			return a.getValue().intValue() - b.getValue().intValue();
		});
		StringBuilder sb = new StringBuilder();
		for (Entry<T, NumCount> k : lst) {
			sb.append(k.getKey() + "\t" + k.getValue());
			sb.append('\n');
		}
		return sb.toString();
	}

	public String toStringWithPercent() {
		List<Entry<T, NumCount>> lst = this.topEntry();
		lst.sort((a, b) -> {
			return a.getValue().intValue() - b.getValue().intValue();
		});
		StringBuilder sb = new StringBuilder();
		VarLong var = new VarLong();
		for (Entry<T, NumCount> k : lst) {
			var.value += k.getValue().intValue();
		}
		for (Entry<T, NumCount> k : lst) {
			double per = k.getValue().intValue() * 100 / var.value;
			Misc.round(per, 10);
			sb.append(k.getKey() + "\t" + k.getValue() + "\t" + per + "%");
			sb.append('\n');
		}
		return sb.toString();
	}

	public int size() {
		return this.map.size();
	}

	public void set(T key, int value) {
		map.put(key, new NumCount(value));
	}

}
