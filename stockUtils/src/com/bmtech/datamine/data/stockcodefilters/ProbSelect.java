package com.bmtech.datamine.data.stockcodefilters;

import com.bmtech.datamine.Stock;
import com.bmtech.utils.Misc;

public class ProbSelect extends StockCodeFilter {

	double percent;

	public ProbSelect(double percent) {
		this.percent = percent;
	}

	@Override
	public boolean accept(Stock stock) {
		return Misc.prob(percent);
	}
}
