package com.bmtech.datamine.data.stockcodefilters;

import com.bmtech.datamine.Stock;

public class _60_00_Filter extends StockCodeFilter {

	@Override
	public boolean accept(Stock stock) {
		if (stock == null)
			return false;
		return stock.is00x() || stock.is60x();
	}

	@Override
	public String toString() {
		return "shsz";
	}

}
