package com.bmtech.utils;

import java.util.ArrayList;
import java.util.List;

public class CombinMulti<T> {
	private List<List<T>> arr;
	private int desiredDepth;

	private long total = 1;
	private FinishableQueue<List<T>> queue = new FinishableQueue<List<T>>(10000);

	public CombinMulti(List<List<T>> arr) {
		this.arr = arr;
		this.desiredDepth = arr.size();
		for (int x = desiredDepth; x > 0; x--) {
			total = total * arr.get(x - 1).size();
		}
	}

	public FinishableQueue<List<T>> combin() {
		Thread t = new Thread() {
			public void run() {

				try {
					combinSelected(1, null);
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

	private void combinSelected(int crtDepth, List<T> lst) throws InterruptedException {
		if (crtDepth == 1) {
			lst = new ArrayList<T>(desiredDepth);
		}
		List<T> arr = this.arr.get(crtDepth - 1);
		for (int x = 0; x < arr.size(); x += 1) {
			T rx = arr.get(x);
			if (crtDepth == desiredDepth) {
				List<T> fList = new ArrayList<T>(lst.size() + 1);
				fList.addAll(lst);
				fList.add(rx);
				queue.put(fList);
			} else {

				List<T> fList = new ArrayList<T>(lst);
				fList.add(rx);
				combinSelected(crtDepth + 1, fList);
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		List<Integer> lstSub = new ArrayList<>();
		List<List<Integer>> lst;
		{
			lst = new ArrayList<>();
			for (int x = 0; x < 100; x++)
				lstSub.add(x);
			lst.add(lstSub);
		}
		{
			lstSub = new ArrayList<>();
			for (int x = 0; x < 10; x++)
				lstSub.add(x);
			lst.add(lstSub);
		}
		{
			lstSub = new ArrayList<>();
			for (int x = 0; x < 10; x++)
				lstSub.add(x);
			lst.add(lstSub);
		}
		CombinMulti<Integer> cmp = new CombinMulti<>(lst);
		FinishableQueue<List<Integer>> q = cmp.combin();
		int req = 0;
		System.out.println(cmp.total);
		while (true) {
			List<Integer> i = q.get();
			if (i == null) {
				break;
			}
			req++;
			System.out.println(i);
			if (req % 5000000 == 0) {
				System.out.println(req + "\t" + i);
			}
		}
		System.out.println(cmp.total);
		System.out.println("finished:" + req);

	}

}
