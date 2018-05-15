package com.bmtech.datamine.data.stockcodefilters;

import com.bmtech.datamine.Stock;

public class SH60xxxxFilter extends StockCodeFilter {

	@Override
	public boolean accept(Stock stock) {
		return stock.is60x();
	}

	@Override
	public String toString() {
		return "sh";
	}

}
