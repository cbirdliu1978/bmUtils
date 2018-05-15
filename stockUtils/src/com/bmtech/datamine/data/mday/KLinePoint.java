package com.bmtech.datamine.data.mday;

import com.bmtech.datamine.Stock;
import com.bmtech.datamine.data.FieldType;
import com.bmtech.utils.Misc;

public class KLinePoint {

	private String code;
	/** day in yyyyMMdd **/
	private int day;
	/** 开盘价 **/
	private double open;
	/** 最高价 **/
	private double high;
	/** 最低价 **/
	private double low;
	/** 收盘价 **/
	private double close;
	/** 成交量 **/
	private long volumn;
	/** 成交金额 **/
	private double amount;

	public KLinePoint() {

	}

	// public double avePrice() {
	// return this.amount / this.volumn;
	// }

	public KLinePoint(String[] vars) {
		/** day in yyyyMMdd **/
		this.day = Integer.parseInt(vars[0]);
		this.open = Double.parseDouble(vars[1]);
		/** 最高价 **/
		this.high = Double.parseDouble(vars[2]);
		/** 最低价 **/
		this.low = Double.parseDouble(vars[3]);
		/** 收盘价 **/
		this.close = Double.parseDouble(vars[4]);
		/** 成交量 **/
		this.volumn = Long.parseLong(vars[5]);
		/** 成交金额 **/
		this.amount = Double.parseDouble(vars[6]);
	}

	public int getDay() {
		return this.day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public double getOpen() {
		return this.open;
	}

	public void setOpen(double open) {
		this.open = round(open);
		updateHiLow();
	}

	public void updateHiLow() {
		double vs[] = { high, low };
		for (double v : vs) {
			updateHiLow(v);
		}
	}

	private void updateHiLow(double v) {
		if (v > this.high) {
			this.setHigh(v);
		} else if (v < this.low) {
			this.setLow(v);
		}
	}

	public double getHigh() {
		return this.high;
	}

	public void setHigh(double high) {
		this.high = round(high);
	}

	public double getLow() {
		return this.low;
	}

	public void setLow(double low) {
		this.low = round(low);

	}

	public double getClose() {
		return this.close;
	}

	public void setClose(double close) {
		this.close = round(close);
		updateHiLow(close);
	}

	public long getVolumn() {
		return this.volumn;
	}

	public void setVolumn(long volumn) {
		this.volumn = volumn;
	}

	public double getAmountDeprec() {
		return this.amount;
	}

	public void setAmount(double amount) {
		this.amount = round(amount);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = Stock.formatStockCode(code);
	}

	public double get(FieldType type) {
		switch (type) {
		case OPEN:
			return this.getOpen();
		case HIGH:
			return this.getHigh();
		case LOW:
			return this.getLow();
		case CLOSE:
			return this.getClose();
		case VOLUMN:
			return this.getVolumn();
		case AVE_OPEN_CLOSE:
			return (this.getOpen() + this.getClose()) / 2;
		case AVE_HIGH_LOW:
			return (this.getHigh() + this.getLow()) / 2;
		case AVE_HIGH_OPEN:
			return (this.getHigh() + this.getOpen()) / 2;
		case AVE_HIGH_CLOSE:
			return (this.getHigh() + this.getClose()) / 2;
		case AVE_LOW_OPEN:
			return (this.getLow() + this.getOpen()) / 2;
		case AVE_LOW_CLOSE:
			return (this.getLow() + this.getClose()) / 2;
		default:
			throw new RuntimeException("unknown field type " + type);
		}
	}

	public static double round(double r) {
		return Misc.round(r, 100);
	}
}
