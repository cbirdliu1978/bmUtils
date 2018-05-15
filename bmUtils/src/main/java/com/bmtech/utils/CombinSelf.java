package com.bmtech.utils;

import java.util.ArrayList;
import java.util.List;

public class CombinSelf<T> {
	private List<T> arr;
	private int desiredDepth;

	private long total = 1;
	private FinishableQueue<List<T>> queue = new FinishableQueue<List<T>>(10000);

	public CombinSelf(List<T> arr, int desiredDepth) {
		this.arr = arr;
		this.desiredDepth = desiredDepth;
		int sub = 1;
		int size = arr.size();
		for (int x = desiredDepth; x > 0; x--) {
			total = total * (size - x + 1);
			sub = sub * x;
		}
		total = (total / sub);
	}

	public FinishableQueue<List<T>> combin() {
		Thread t = new Thread() {
			public void run() {

				try {
					combinSelected(0, 1, null);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);// e.printStackTrace();
				}
				queue.setFinished();
			}
		};
		t.setDaemon(true);
		t.start();
		return queue;
	}

	private void combinSelected(int fromIndex, int crtDepth, List<T> lst) throws InterruptedException {
		if (crtDepth == 1) {
			lst = new ArrayList<T>(desiredDepth);
		}
		for (int x = fromIndex; x < arr.size(); x += 1) {
			T rx = arr.get(x);
			if (crtDepth == desiredDepth) {
				List<T> fList = new ArrayList<T>(lst.size() + 1);
				fList.addAll(lst);
				fList.add(rx);
				queue.put(fList);
			} else {

				List<T> fList = new ArrayList<T>(lst);
				fList.add(rx);
				combinSelected(x + 1, crtDepth + 1, fList);
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		List<Integer> lst = new ArrayList<>();
		for (int x = 0; x < 100; x++)
			lst.add(x);
		CombinSelf<Integer> cmp = new CombinSelf<>(lst, 6);
		FinishableQueue<List<Integer>> q = cmp.combin();
		int req = 0;
		System.out.println(cmp.total);
		while (true) {
			List<Integer> i = q.get();
			if (i == null) {
				break;
			}
			req++;
			// System.out.println(i);
			if (req % 5000000 == 0) {
				System.out.println(req + "\t" + i);
			}
		}
		System.out.println(cmp.total);
		System.out.println("finished:" + req);

	}

}
