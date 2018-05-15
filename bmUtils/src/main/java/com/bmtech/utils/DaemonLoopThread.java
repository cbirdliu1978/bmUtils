package com.bmtech.utils;

import com.bmtech.utils.log.LogHelper;

/**
 * a loop daemon thread! excetion will be catched and not cause thread stop. call {@link setStop} to stop loop
 * 
 * @author liying
 *
 */
public abstract class DaemonLoopThread extends Thread {
	protected LogHelper log;
	private int checkItvMs;
	private boolean stop = false;

	public DaemonLoopThread() {
		this.init(this.getClass().getSimpleName(), 0);
	}

	public DaemonLoopThread(String name, int checkItvMs) {
		this.init(name, checkItvMs);
	}

	private void init(String name, int checkItvMs) {
		this.setDaemon(true);
		this.log = new LogHelper(name);
		this.checkItvMs = checkItvMs;
	}

	@Override
	public void run() {
		while (!isStop()) {
			try {
				boolean toStop = singleRound();
				if (toStop)
					break;
			} catch (Exception e) {
				log.error(e, "when daemon checking round");
				e.printStackTrace();
			} finally {
				int itv = this.checkItvMs;
				if (itv > 0) {
					try {
						sleep(this.checkItvMs);
					} catch (InterruptedException e) {
						log.error(e, "when daemon sleeping");
					}
				}
			}
		}
	}

	protected void setCheckItvMs(int checkItvMs) {
		this.checkItvMs = checkItvMs;
	}

	protected abstract boolean singleRound() throws Exception;

	public boolean isStop() {
		return stop;
	}

	/**
	 * eric stop flag! thread will be stop for next loop
	 */
	public void setStop() {
		this.stop = true;
	}

}
