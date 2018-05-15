package com.bmtech.utils;

public class CircularArrayQueue<T> {

	private static final float growStep = 0.5f;
	private static final int capacity = 4;
	private Object[] Q;
	private int f = 0;
	private int r = 0;

	public CircularArrayQueue() {
		this(capacity + 1);
	}

	public CircularArrayQueue(int capacity) {
		Q = new Object[capacity];
	}

	@SuppressWarnings("unchecked")
	public T get(int index) {
		if (index > this.size()) {
			throw new RuntimeException("out of range");
		}

		return (T) this.Q[(this.f + index) % this.Q.length];
	}

	public T getLast() {
		if (this.size() == 0)
			return null;
		return get(this.size() - 1);
	}

	public T getFirst() {
		if (this.size() == 0)
			return null;
		return get(0);
	}

	public int size() {
		if (r >= f)
			return r - f;
		return this.Q.length - f + r;
	}

	public boolean isEmpty() {
		return (r == f) ? true : false;
	}

	private boolean isFull() {
		int diff = r - f;
		if (diff == -1 || diff == (this.Q.length - 1))
			return true;
		return false;
	}

	public void enqueue(T obj) {
		if (isFull()) {
			expand();
			enqueue(obj);
		} else {
			Q[r] = obj;
			r++;
			if (r >= this.Q.length)
				r = 0;
		}
	}

	private void expand() {
		int newSize = (int) (this.Q.length * (1 + growStep));
		CircularArrayQueue<T> newQueue = new CircularArrayQueue<T>(newSize);
		while (!this.isEmpty()) {
			newQueue.enqueue(this.dequeue());
		}
		this.Q = newQueue.Q;
		this.f = newQueue.f;
		this.r = newQueue.r;
	}

	@SuppressWarnings("unchecked")
	public T dequeue() {
		T item;
		if (isEmpty()) {
			return null;
		} else {
			item = (T) Q[f];
			Q[f] = null;
			f++;
			if (f >= this.Q.length) {
				f = 0;
			}
		}
		return item;
	}

	public static void main(String[] args) {
		CircularArrayQueue<Integer> queue = new CircularArrayQueue<Integer>(10);
		for (int x = 0; x < 33; x++) {

			if (x % 3 == 0) {
				Object o = queue.dequeue();
				System.out.println("dequeue:" + o);

				;
			}
			System.out.println(" size " + queue.size());
			queue.enqueue(x);
		}
		for (Object o : queue.Q) {
			System.out.println(o);
		}

		for (int x = 0; x < queue.size(); x++) {
			Consoler.println("index %s, value %s", x, queue.get(x));
		}

		Consoler.println("getFirst %s", queue.getFirst());
		Consoler.println("getLast %s", queue.getLast());
	}

}