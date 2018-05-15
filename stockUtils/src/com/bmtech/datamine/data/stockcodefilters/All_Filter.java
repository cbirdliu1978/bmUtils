package com.bmtech.datamine.data.stockcodefilters;

import com.bmtech.datamine.Stock;

public class All_Filter extends StockCodeFilter {

	@Override
	public boolean accept(Stock stock) {
		return true;
	}

	@Override
	public String toString() {
		return "all";
	}

}
