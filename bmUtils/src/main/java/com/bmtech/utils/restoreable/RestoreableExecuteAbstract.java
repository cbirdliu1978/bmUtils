package com.bmtech.utils.restoreable;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import com.bmtech.utils.FinishableQueue;
import com.bmtech.utils.Misc;
import com.bmtech.utils.log.LogHelper;

public abstract class RestoreableExecuteAbstract {

	Iterator<RItem> feeder;
	private ExcludeChecker checker;
	private int threadNum = 1;
	FinishableQueue<RItem> queue = new FinishableQueue<>(64);
	final LogHelper log;
	private int waitMsPerTaskForSingleThread = 0;
	private boolean hasExe = false;
	private Object exeLock = new Object();
	private Object numberLock = new Object();

	public RestoreableExecuteAbstract(ExcludeChecker checker, Iterator<RItem> feeder) throws Exception {
		this(checker, feeder, 1);
	}

	public RestoreableExecuteAbstract(ExcludeChecker checker, Iterator<RItem> feeder, int threadNum) {
		this.checker = checker;
		this.threadNum = threadNum;
		log = new LogHelper("restoreExe");
		this.feeder = feeder;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public void execute() {
		synchronized (exeLock) {
			if (hasExe) {
				log.error("has already started");
				throw new RuntimeException("has already started");
			}
			hasExe = true;

			for (int x = 0; x < this.threadNum; x++) {
				ConsumerThread t = new ConsumerThread(x + 1);
				t.start();
			}
			while (feeder.hasNext()) {
				RItem item = feeder.next();
				queue.put(item);
			}
			queue.setFinished();
		}
	}

	public synchronized boolean hasDone(String itemName) {
		if (checker.hasDone(itemName)) {
			return true;
		}
		return false;
	}

	private synchronized boolean hasDone(RItem item) {
		return hasDone(item.getItemName());
	}

	private synchronized void registerFinished(RItem item) {
		String name = item.getItemName();
		checker.registerFinished(name);
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	public int getWaitMsPerTaskForSingleThread() {
		return waitMsPerTaskForSingleThread;
	}

	public void setWaitMsPerTaskForSingleThread(int waitMsPerTaskForSingleThread) {
		this.waitMsPerTaskForSingleThread = waitMsPerTaskForSingleThread;
	}

	private AtomicInteger runningNum = new AtomicInteger();

	public class ConsumerThread extends Thread {
		LogHelper log;

		private ConsumerThread(int id) {
			log = new LogHelper("consumer-" + id);
		}

		@Override
		public void run() {
			runningNum.addAndGet(1);
			try {
				while (true) {
					RItem item = queue.get();
					if (item == null) {
						log.info("finished");
						break;
					}
					if (!hasDone(item)) {
						log.info("doing %s", item.getItemName());
						try {
							item.execute();
							registerFinished(item);
						} catch (Throwable r) {
							// r.printStackTrace();
							log.error(r, "when executing %s", item.getItemName());
						} finally {
							if (waitMsPerTaskForSingleThread > 0) {
								Misc.sleep(waitMsPerTaskForSingleThread);
							}
						}
					} else {
						log.debug("skip item with name %s", item.getItemName());
					}
				}

			} finally {
				runningNum.decrementAndGet();
				synchronized (numberLock) {
					numberLock.notifyAll();
				}
			}
		}

	}

	public void waitFinish() {
		while (true) {
			if (this.runningNum.get() == 0)
				return;
			synchronized (numberLock) {
				try {
					numberLock.wait(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
