package com.bmtech.datamine.data.stockcodefilters;

import com.bmtech.datamine.AllStock;
import com.bmtech.datamine.Stock;

public abstract class StockCodeFilter {

	public abstract boolean accept(Stock stock);

	public boolean accept(String code) {
		return this.accept(AllStock.getStockByCode(code));
	}
}