package com.bmtech.utils;

import com.bmtech.utils.log.LogHelper;

public abstract class RoundCheckThread extends Thread {
	private int checkItvMs;
	protected LogHelper log;
	private int roundNum;

	public RoundCheckThread(int checkItvMs) {
		this.checkItvMs = checkItvMs;
		this.log = new LogHelper(this.getClass().getSimpleName());
	}

	protected abstract void doWork() throws Exception;

	public void run() {
		while (true) {
			roundNum++;
			try {
				doWork();
			} catch (Exception e) {
				// e.printStackTrace();
				log.error(e, "when do work, roundNum = %s", roundNum);
			}
			try {
				Thread.sleep(this.getRoundSleepItvMs());
			} catch (InterruptedException e) {
				log.error(e, "when sleep between round, roundNum = %s", roundNum);
			}
		}
	}

	protected int getRoundSleepItvMs() {
		return this.checkItvMs;
	}

	public int getRoundNum() {
		return roundNum;
	}

}
