package com.bmtech.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.bmtech.utils.log.LogHelper;

public class FinishableQueue<T> {
	/**
	 * 
	 */
	private transient boolean isFinished = false;
	private LinkedBlockingQueue<T> queue;
	protected AtomicLong num = new AtomicLong();
	protected int printWhenDelivery = 100;

	public FinishableQueue(int capacity) {
		queue = new LinkedBlockingQueue<T>(capacity);
	}

	private boolean isFinished() {
		return isFinished && queue.isEmpty();
	}

	/**
	 * get a element from the queue, wait util a element available. If the queue is finished, return null
	 * 
	 * @return an element. if null returned means the queue is finished
	 * @throws InterruptedException
	 */
	public T get() {
		try {
			while (true) {
				if (this.isFinished()) {
					LogHelper.iInfo("FinishableQueue has done, total task %s", num);
					return null;
				}
				T t = queue.poll(100, TimeUnit.MILLISECONDS);
				if (t != null) {
					long delivery = num.addAndGet(1);
					if (delivery % printWhenDelivery == 0) {
						LogHelper.iInfo("FinishableQueue has delevery task %s", delivery);
					}
					return t;
				}

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retrieves and removes the head of this queue, or returns null if this queue is empty.
	 * 
	 * @return
	 */
	public T directGet() {
		return this.queue.poll();
	}

	public void setFinished() {
		this.isFinished = true;
	}

	/**
	 * put a element to queue, if queue is full, the queue will be blocked util there is new space available
	 * 
	 * @param t
	 */
	public synchronized void put(T t) {
		try {
			queue.put(t);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public int size() {
		return this.queue.size();
	}

	public void feed(QueueFeeder r) throws Exception {
		try {
			r.feed();

		} finally {
			setFinished();
		}
	}

	public static interface QueueFeeder {
		public void feed() throws Exception;
	}

	public void clear() {
		this.queue.clear();
	}
}
