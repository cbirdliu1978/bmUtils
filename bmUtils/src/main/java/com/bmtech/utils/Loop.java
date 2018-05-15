package com.bmtech.utils;

import com.bmtech.utils.log.LogHelper;

public class Loop {

	public static void loop(LoopFunc loop) throws Exception {
		int roundId = 0;
		while (true) {
			if (roundId % 100 == 0)
				LogHelper.log.debug("loop round " + roundId);
			try {
				boolean isOk = loop.run(roundId);
				if (isOk) {
					break;
				}
			} finally {
				roundId++;
			}
		}
	}

	public static void loopWithCatch(LoopFunc loop) {
		int roundId = 0;
		while (true) {
			if (roundId % 100 == 0)
				LogHelper.log.debug("loop round " + roundId);
			try {
				boolean isOk = loop.run(roundId);
				if (isOk) {
					break;
				}
			} catch (Throwable e) {
				LogHelper.log.error(e, "when doing round " + roundId + ", for " + loop);
			} finally {
				roundId++;
			}
		}
	}

	public static void loop(LoopFuncWithNoReturnValue loop) throws Exception {
		int roundId = 0;
		while (true) {
			if (roundId % 100 == 0)
				LogHelper.log.debug("loop round " + roundId);
			try {
				loop.run(roundId);
			} finally {
				roundId++;
			}
		}
	}

	public static void loopWithCatch(LoopFuncWithNoReturnValue loop) {
		int roundId = 0;
		while (true) {
			if (roundId % 100 == 0)
				LogHelper.log.debug("loop round " + roundId);
			try {
				loop.run(roundId);
			} catch (Throwable e) {
				LogHelper.log.error(e, "when doing round " + roundId + ", for " + loop);
			} finally {
				roundId++;
			}
		}
	}
}
