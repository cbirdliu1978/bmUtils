package com.bmtech.datamine.data.mday.loader;

import com.bmtech.datamine.data.mday.MinDay;

public class DayRangeFilter extends StockDataFilter {
	private final int startDay;
	private final int endDay;

	public DayRangeFilter(int day) {
		this(day, day + 1);
	}

	public DayRangeFilter(int startDay, int endDay) {
		super();
		this.startDay = startDay;
		this.endDay = endDay;
		// log.info("loaderFilter starts with %s", this.getLogName());
	}

	private boolean needContinue = true;

	@Override
	public boolean accept(MinDay mday) {
		int day = mday.getDay();
		if (mday.getDay() > endDay)
			needContinue = false;
		return day >= startDay && day < endDay;
	}

	@Override
	public boolean needContinue() {
		return needContinue;
	}

	@Override
	public String getLogName() {
		return startDay + "-" + endDay;
	}

	public int getEndDay() {
		return endDay;
	}

	public int getStartDay() {
		return startDay;
	}

}
