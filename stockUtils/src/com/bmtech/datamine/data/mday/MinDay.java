package com.bmtech.datamine.data.mday;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;

import com.bmtech.datamine.Statics;
import com.bmtech.datamine.Stock;
import com.bmtech.datamine.data.DataType;
import com.bmtech.datamine.data.FieldType;
import com.bmtech.datamine.data.mday.loader.MinDayLoader;
import com.bmtech.utils.ForEach;
import com.bmtech.utils.TripleValue;

public class MinDay extends KLinePoint {

	public void updateTime(MinDay other) {
		updateTime(other.getDay(), other.startTime, other.endTime);
	}

	public void updateTime(long startTimeMs, long endTimeMs) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(endTimeMs);
		int day = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);

		this.setDay(year * 10000 + month * 100 + day);
		this.setEndTime(Statics.getTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));

		cal.setTimeInMillis(startTimeMs);
		this.setStartTime(Statics.getTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));

	}

	public void updateTime(int day, int startTime, int endTime) {
		this.setDay(day);
		this.startTime = startTime;
		this.endTime = endTime;

	}

	private int startTime;
	private int endTime;

	public MinDay() {

	}

	public MinDay(String[] vars, int startTime, int endTime) {
		super(vars);
		this.setStartTime(startTime);
		this.setEndTime(endTime);
	}

	public String getStartTimeStr() {
		return String.format("%02d:%02d", hour(this.startTime), minute(this.startTime));
	}

	public String getEndTimeStr() {
		return String.format("%02d:%02d", hour(this.endTime), minute(this.endTime));
	}

	public int getStartTime() {
		return this.startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getEndTime() {
		return this.endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public MinDay copy() {
		MinDay ret = new MinDay();
		ret.endTime = this.endTime;
		ret.startTime = this.startTime;
		// ret.setKRange(this.getKRanage());
		ret.setCode(this.getCode());
		ret.setDay(this.getDay());
		ret.setOpen(this.getOpen());
		ret.setHigh(this.getHigh());
		ret.setLow(this.getLow());
		ret.setClose(this.getClose());
		ret.setVolumn(this.getVolumn());
		ret.setAmount(this.getAmountDeprec());

		return ret;
	}

	private void merge(MinDay that) {
		this.endTime = that.endTime;
		this.setHigh(Math.max(this.getHigh(), that.getHigh()));
		this.setLow(Math.min(this.getLow(), that.getLow()));
		this.setClose(that.getClose());
		this.setVolumn(this.getVolumn() + that.getVolumn());
		this.setAmount(this.getAmountDeprec() + that.getAmountDeprec());
	}

	public static MinDay merge(ListIterator<MinDay> list) {
		MinDay ret = null;// = list.get(0).copy();
		while (list.hasNext()) {
			if (ret == null) {
				ret = list.next().copy();
			} else {
				ret.merge(list.next());
			}
		}
		return ret;
	}

	public static MinDay merge(List<MinDay> list) {
		MinDay ret = list.get(0).copy();
		for (int x = 1; x < list.size(); x++) {
			ret.merge(list.get(x));
		}
		return ret;
	}

	public boolean isBigBill(long volume) {
		return volume > 5000;
	}

	public int getCloseHour() {
		return hour(this.endTime);
	}

	public int getCloseMinutes() {
		return minute(this.getEndTime());
	}

	public static int hour(long time) {
		return (int) (time / 1000 / 60 / 60 % 24 + 8);
	}

	public static int minute(long time) {
		return (int) (time / 1000 / 60 % 60);
	}

	public int getOpenHour() {
		return hour(this.startTime);
	}

	public int getOpenMinutes() {
		return minute(this.startTime);
	}

	public boolean isRed() {
		return body() / this.getClose() > 0.001;
	}

	public double[] shadowRatio() {
		return new double[] { (this.getHigh() - this.getClose()) / this.swing(), (this.getOpen() - this.getLow()) / this.swing() };
	}

	public double body() {
		return this.getClose() - this.getOpen();
	}

	public double bodyRatio() {
		return body() / swing();
	}

	private double swing() {
		return this.getHigh() - this.getLow();
	}

	public static double getMaxValue(List<MinDay> lst, FieldType ft) {
		return getMax(lst, ft).get(ft);
	}

	public static double getMinValue(List<MinDay> lst, FieldType ft) {
		return getMin(lst, ft).get(ft);
	}

	public static MinDay getMax(List<MinDay> lst, FieldType ft) {
		return getMax(lst, ft, true);
	}

	public static MinDay getMin(List<MinDay> lst, FieldType ft) {
		return getMax(lst, ft, false);
	}

	private static MinDay getMax(List<MinDay> lst, FieldType ft, boolean max) {
		MinDay ret = null;
		for (MinDay m : lst) {
			if (ret == null)
				ret = m;
			else {
				if (max) {
					if (m.get(ft) > ret.get(ft)) {
						ret = m;
					}
				} else {
					if (m.get(ft) < ret.get(ft)) {
						ret = m;
					}
				}
			}
		}
		return ret;
	}

	public static MinDay parseMinDay(final String line, int orgBarRangeSeconds, Stock stk) {
		// System.out.println("```" + stk + "\t" + line);
		try {
			String tokens[] = line.split("	");
			/**
			 * private int m_Date; private double m_Open; private double m_High; private double m_Low; private double m_Close; private
			 * double m_Amount; private long m_Volume;
			 * 
			 */
			long longDate = Long.parseLong(tokens[0]);
			double m_Open = Double.parseDouble(tokens[1]);
			double m_High = Double.parseDouble(tokens[2]);
			double m_Low = Double.parseDouble(tokens[3]);
			double m_Close = Double.parseDouble(tokens[4]);
			double m_Amount = Double.parseDouble(tokens[5]);
			long m_Volume = (long) Double.parseDouble(tokens[6]);
			// 201609200932
			int minute = (int) (longDate % 100);
			longDate = longDate / 100;
			int hour = (int) (longDate % 100);
			longDate = longDate / 100;
			int day = (int) longDate;
			int startTime;// = ((hour - 8) * 60 + minute) * 60 * 1000;
			int endTime;//
			if (orgBarRangeSeconds == 60) {
				startTime = ((hour - 8) * 60 + minute) * 60 * 1000;
				endTime = startTime + orgBarRangeSeconds * 1000;
			} else if (orgBarRangeSeconds == Statics.min05BarSeconds) {
				endTime = ((hour - 8) * 60 + minute) * 60 * 1000;
				startTime = endTime - orgBarRangeSeconds * 1000;
			} else {
				throw new RuntimeException("unkown how to caculate end time");
			}
			MinDay md = new MinDay();
			md.setVolumn(m_Volume);
			md.setClose(m_Close);
			md.setCode(stk.getCode());
			md.setDay(day);
			md.setEndTime(endTime);
			md.setHigh(m_High);
			// md.setKRange(orgBarRangeSeconds);
			md.setLow(m_Low);
			md.setOpen(m_Open);
			md.setStartTime(startTime);

			md.setAmount(m_Amount);
			return md;
		} catch (Exception e) {
			throw new RuntimeException("when  parse '" + line + "'", e);
		}

	}

	public static List<MinDay> readDayFile(File file, Stock stk) throws IOException {
		List<MinDay> ret = new ArrayList<>();
		ForEach.forEachLine(file, (line, lineI) -> {
			try {
				MinDay mday = MinDay.parseDayLine(line, stk);
				ret.add(mday);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return ret;
	}

	public static List<MinDay> readMin05File(Stock stk) throws IOException {
		return readMinFile(DataType.min05.orgBarRangeMinutes(), stk);
	}

	public static List<MinDay> readMinFile(int minutes, Stock stk) throws IOException {
		File file = MinDayLoader.getDataFile(minutes, stk.getCode());
		return readMinFile(file, 60 * minutes, stk);
	}

	public static List<MinDay> readMin05File(File file, int minSecondsPerbar, Stock stk) throws IOException {
		return readMinFile(file, DataType.min05.orgBarRangeSeconds(), stk);
	}

	public static List<MinDay> readMinFile(File file, int minSecondsPerbar, Stock stk) throws IOException {
		List<MinDay> ret = new ArrayList<>();
		ForEach.forEachLine(file, (line, lineI) -> {
			MinDay mday = MinDay.parseMinDay(line, minSecondsPerbar, stk);
			ret.add(mday);
		});
		return ret;
	}

	public static MinDay parseDayLine(String line, Stock stk) {

		String tokens[] = line.split("	");

		int day = Integer.parseInt(tokens[0]);
		if (day < 20010101 || day > 21000101)
			throw new RuntimeException("error day " + line);
		double m_Open = Double.parseDouble(tokens[1]);
		double m_High = Double.parseDouble(tokens[2]);
		double m_Low = Double.parseDouble(tokens[3]);
		double m_Close = Double.parseDouble(tokens[4]);
		double m_Amount = Double.parseDouble(tokens[5]);
		long m_Volume = (long) Double.parseDouble(tokens[6]);
		MinDay md = new MinDay();
		md.setVolumn(m_Volume);
		md.setClose(m_Close);
		md.setCode(stk.getCode());
		md.setDay(day);
		md.setEndTime(Statics.m15_00);
		md.setHigh(m_High);
		md.setLow(m_Low);
		md.setOpen(m_Open);
		md.setStartTime(Statics.m09_30);

		md.setAmount(m_Amount);
		return md;

	}

	public String toLine() {
		if (this.endTime - this.startTime == Statics.tradeDayMsIncludeNoon)
			return toDayLine(this);
		else
			return toMinuteLine(this);
	}

	public static String toMinuteLine(MinDay mday) {
		String ret = String.format("%s%02d%02d\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%d", mday.getDay(), mday.getCloseHour(),
				mday.getCloseMinutes(), round(mday.getOpen()), round(mday.getHigh()), round(mday.getLow()), round(mday.getClose()),
				round(mday.getAmountDeprec()), mday.getVolumn());
		return ret;
	}

	public static String toDayLine(MinDay mday) {
		String ret = String.format("%s\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%d", mday.getDay(), round(mday.getOpen()), round(mday.getHigh()),
				round(mday.getLow()), round(mday.getClose()), round(mday.getAmountDeprec()), mday.getVolumn());
		return ret;
	}

	public int timeSpanMs() {
		return this.endTime - this.startTime;
	}

	public TripleValue compareTime(int day, int hour, int minute) {

		int dayDiff = this.getDay() - day;
		if (dayDiff == 0) {
			int myHour = hour(this.endTime);
			int hourDiff = myHour - hour;
			if (hourDiff == 0) {
				int myMinute = minute(this.endTime);
				int minDiff = myMinute - minute;
				return TripleValue.get(minDiff);
			} else {
				return TripleValue.get(hourDiff);
			}
		} else {
			return TripleValue.get(dayDiff);
		}
	}

	public TripleValue compareTime(int thatTime) {
		return TripleValue.get(this.endTime - thatTime);
	}

	public TripleValue compareTime(MinDay that) {
		int thisTimeSpan = this.timeSpanMs();
		int thatTimeSpan = that.timeSpanMs();
		if (thisTimeSpan != thatTimeSpan) {
			throw new IllegalArgumentException("time span not match! this  " + thisTimeSpan + ", that " + thatTimeSpan);
		}
		int dayDiff = this.getDay() - that.getDay();
		if (dayDiff == 0)
			return TripleValue.get(this.endTime - that.endTime);
		else
			return TripleValue.get(dayDiff);
	}

	public void setCode(Stock stk) {
		this.setCode(stk.getCode());
	}

	@Override
	public String toString() {
		return this.getCode() + ":" + this.toLine();
	}

	public static List<MinDay> merge2HalfHour(List<MinDay> mday05) {
		int endIdx = 0;
		List<MinDay> ret = new ArrayList<>();
		int pos = 0;
		for (; pos < mday05.size(); pos++) {
			MinDay mday = mday05.get(pos);
			int minute = mday.getOpenMinutes();
			if (minute == 0 || minute == 30) {
				break;
			}
		}
		for (; pos < mday05.size(); pos += 6) {
			int start = pos;
			endIdx = pos + 6;
			if (endIdx > mday05.size())
				break;
			List<MinDay> sub = mday05.subList(start, endIdx);
			MinDay half = MinDay.merge(sub);
			ret.add(half);
		}
		return ret;
	}

	public long dayTimeEnd() {
		return getDay() * 10000L + hour(this.endTime) * 100 + minute(this.endTime);
	}

	public long dayTimeStart() {
		return getDay() * 10000L + hour(this.startTime) * 100 + minute(this.startTime);
	}

}
