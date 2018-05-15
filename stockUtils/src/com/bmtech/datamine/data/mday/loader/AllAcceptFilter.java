package com.bmtech.datamine.data.mday.loader;

import com.bmtech.datamine.data.mday.MinDay;

public class AllAcceptFilter extends StockDataFilter {

	public AllAcceptFilter() {
		super();
	}

	@Override
	public boolean accept(MinDay mday) {
		return true;
	}

	@Override
	public String getLogName() {
		return "allAcceptLoaderFilter";
	}

}
