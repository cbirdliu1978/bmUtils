package com.bmtech.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A Queue that stores the biggest values.<br>
 * A PriorityQueue maintains a partial ordering of its elements such that the least element can always be found in constant time. Put()'s
 * and pop()'s require log(size) time.
 */
@SuppressWarnings("unchecked")
public abstract class FixSizePriorityQueue<E> implements Comparator<E> {
	private int size;
	private int maxSize;
	protected Object[] heap;

	public FixSizePriorityQueue(int maxSize) {
		this.initialize(maxSize);
	}

	/**
	 * return a - b
	 */
	@Override
	public abstract int compare(E a, E b);

	/**
	 * Determines the ordering of objects in this priority queue. Subclasses must define this one method. <br>
	 * if a < b return true then the topList(false) will return a list as a-->b order,toList(true) will return a list in order b-->a
	 */
	protected boolean lessThan(E a, E b) {
		return this.compare(a, b) < 0;
	}

	/** Subclass constructors must call this. */
	protected final void initialize(int maxSize) {
		size = 0;
		int heapSize;
		if (0 == maxSize)
			// We allocate 1 extra to avoid if statement in top()
			heapSize = 2;
		else
			heapSize = maxSize + 1;
		heap = new Object[heapSize];
		this.maxSize = maxSize;
	}

	/**
	 * Adds an Object to a PriorityQueue in log(size) time. If one tries to add more objects than maxSize from initialize a RuntimeException
	 * (ArrayIndexOutOfBound) is thrown.
	 */
	private final void put(E element) {
		size++;
		heap[size] = element;
		upHeap();
	}

	/**
	 * Adds element to the PriorityQueue in log(size) time if either the PriorityQueue is not full, or not lessThan(element, top()).
	 * 
	 * @param element
	 * @return true if element is added, false otherwise.
	 */
	public boolean insert(E element) {
		return insertWithOverflow(element) != element;
	}

	/**
	 * insertWithOverflow() is the same as insert() except its return value: it returns the object (if any) that was dropped off the heap
	 * because it was full. This can be the given parameter (in case it is smaller than the full heap's minimum, and couldn't be added), or
	 * another object that was previously the smallest value in the heap and now has been replaced by a larger one, or null if the queue
	 * wasn't yet full with maxSize elements.
	 */
	public E insertWithOverflow(E element) {
		if (size < maxSize) {
			put(element);
			return null;
		} else if (size > 0 && !lessThan(element, (E) heap[1])) {
			E ret = (E) heap[1];
			heap[1] = element;
			adjustTop();
			return ret;
		} else {
			return element;
		}
	}

	/** Returns the least element of the PriorityQueue in constant time. */
	public final E top() {
		// We don't need to check size here: if maxSize is 0,
		// then heap is length 2 array with both entries null.
		// If size is 0 then heap[1] is already null.
		return (E) heap[1];
	}

	/**
	 * Removes and returns the least element of the PriorityQueue in log(size) time.
	 */
	public final E pop() {
		if (size > 0) {
			Object result = heap[1]; // save first value
			heap[1] = heap[size]; // move last to first
			heap[size] = null; // permit GC of objects
			size--;
			downHeap(); // adjust heap
			return (E) result;
		} else
			return null;
	}

	/**
	 * Should be called when the Object at top changes values. Still log(n) worst case, but it's at least twice as fast to
	 * 
	 * <pre>
	 * {
	 * 	pq.top().change();
	 * 	pq.adjustTop();
	 * }
	 * </pre>
	 * 
	 * instead of
	 * 
	 * <pre>
	 * {
	 * 	o = pq.pop();
	 * 	o.change();
	 * 	pq.push(o);
	 * }
	 * </pre>
	 */
	private final void adjustTop() {
		downHeap();
	}

	/** Returns the number of elements currently stored in the PriorityQueue. */
	public final int size() {
		return size;
	}

	/** Removes all entries from the PriorityQueue. */
	public final void clear() {
		for (int i = 0; i <= size; i++)
			heap[i] = null;
		size = 0;
	}

	private final void upHeap() {
		int i = size;
		E node = (E) heap[i]; // save bottom node
		int j = i >>> 1;
		while (j > 0 && lessThan(node, (E) heap[j])) {
			heap[i] = heap[j]; // shift parents down
			i = j;
			j = j >>> 1;
		}
		heap[i] = node; // install saved node
	}

	private final void downHeap() {
		int i = 1;
		E node = (E) heap[i]; // save top node
		int j = i << 1; // find smaller child
		int k = j + 1;
		if (k <= size && lessThan((E) heap[k], (E) heap[j])) {
			j = k;
		}
		while (j <= size && lessThan((E) heap[j], node)) {
			heap[i] = heap[j]; // shift up child
			i = j;
			j = i << 1;
			k = j + 1;
			if (k <= size && lessThan((E) heap[k], (E) heap[j])) {
				j = k;
			}
		}
		heap[i] = node; // install saved node
	}

	/**
	 * get the biggest values, return a list that big value first, such as [9, 8, 7, 6, ...]
	 * 
	 * @return
	 */
	public List<E> topList() {

		List<E> lst = new ArrayList<E>(size);
		int x = 0;
		while (++x <= size) {
			lst.add((E) heap[x]);
		}
		lst.sort((a, b) -> {
			return this.compare(b, a);
		});
		return lst;
	}

	public static void main(String[] p) {
		FixSizePriorityQueue<String> queue = new FixSizePriorityQueue<String>(10) {
			@Override
			public int compare(String o1, String o2) {
				return o1.charAt(0) - o2.charAt(0);
			}

		};
		Set<Character> s = new HashSet<>();
		for (int x = 0; x < 100; x++) {
			int rnd = Misc.randInt(0, 25);
			char c = (char) ('a' + rnd);
			if (s.contains(c))
				continue;
			s.add(c);
			queue.insert("" + c);
			System.out.println(Arrays.toString(queue.heap));
		}
		System.out.println(queue.topList());
	}
}
