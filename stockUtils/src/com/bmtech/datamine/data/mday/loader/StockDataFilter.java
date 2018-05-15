package com.bmtech.datamine.data.mday.loader;

import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.log.LogHelper;

public abstract class StockDataFilter {
	protected LogHelper log;

	public StockDataFilter() {
		log = new LogHelper(this.getClass().getSimpleName());
	}

	public abstract String getLogName();

	public abstract boolean accept(MinDay mday);

	@Override
	public String toString() {
		return this.getLogName();
	}

	public boolean needContinue() {
		return true;
	}

}
