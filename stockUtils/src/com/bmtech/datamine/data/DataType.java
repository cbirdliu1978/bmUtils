package com.bmtech.datamine.data;

import com.bmtech.datamine.Statics;

public enum DataType {
	min05(Statics.min05BarSeconds);
	// , day(Statics.dayBarSeconds);

	private final int orgBarSeconds;

	private DataType(int orgBarSeconds) {
		this.orgBarSeconds = orgBarSeconds;
	}

	public int orgBarRangeSeconds() {
		return orgBarSeconds;
	}

	public int orgBarRangeMinutes() {
		return orgBarSeconds / 60;
	}

	public static DataType getDataTypeByBarSeconds(int sec) {
		if (sec == Statics.min05BarSeconds) {
			return min05;
		}
		// else if (sec == Statics.dayBarSeconds) {
		// return day;
		// }
		else {
			throw new RuntimeException("find no DataType, unknown bar seconds! " + sec);
		}
	}
}