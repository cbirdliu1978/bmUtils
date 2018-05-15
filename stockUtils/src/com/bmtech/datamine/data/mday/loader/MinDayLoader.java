package com.bmtech.datamine.data.mday.loader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.bmtech.datamine.AllStock;
import com.bmtech.datamine.Statics;
import com.bmtech.datamine.Stock;
import com.bmtech.datamine.data.DataType;
import com.bmtech.datamine.data.inc.IncDataIO;
import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.Misc;
import com.bmtech.utils.io.LineReader;
import com.bmtech.utils.log.LogHelper;

public class MinDayLoader extends KDayLoader {

	public class KLineMerger {
		int itvSeconds;

		private class Merger {
			private List<MinDay> mdList = new ArrayList<>();
			int startTime;
			int endTime;

			public Merger(int startTime) {
				super();
				this.startTime = startTime;
				this.endTime = startTime + itvSeconds * 1000;
			}

			public Merger(MinDay day) {
				this(findStartTime(day));
			}

			public boolean accept(MinDay day) {
				return day.getStartTime() >= this.startTime && day.getEndTime() <= this.endTime;
			}

			public MinDay toMinDay() {
				return MinDay.merge(this.mdList);
			}

			private boolean isEnd(MinDay day) {
				return day.getEndTime() == this.endTime;
			}
		}

		private int findStartTime(MinDay day) {
			int now = day.getStartTime();
			int started = now - Statics.startMarketTime;
			boolean isExp = false;
			if (started > 2 * 60 * 60 * 1000) {
				started = started - 90 * 60 * 1000;
				isExp = true;
			}
			int periodNum = started / (itvSeconds * 1000);
			return Statics.startMarketTime + periodNum * itvSeconds * 1000 + (isExp ? (90 * 60 * 1000) : 0);
		}

		Merger crt;

		public KLineMerger(int itvSeconds) {
			this.itvSeconds = itvSeconds;
		}

		MinDay feed(MinDay day) {
			if (day.getStartTime() < Statics.startMarketTime) {
				return null;
			}
			if (crt == null) {
				crt = new Merger(day);
			}
			if (crt.accept(day)) {
				crt.mdList.add(day);
				if (crt.isEnd(day)) {
					Merger ret = this.crt;
					crt = null;
					return ret.toMinDay();
				} else {
					return null;
				}
			} else {
				Merger ret = this.crt;

				this.crt = new Merger(day);
				this.crt.mdList.add(day);
				if (ret.mdList.size() > 0) {
					MinDay retMd = ret.toMinDay();
					{
						int itv = day.getEndTime() - day.getStartTime();
						if (itv == 60000) {
							if (day.getStartTime() != Statics.m09_30 && day.getStartTime() != Statics.m13_00) {
								retMd.setClose(day.getOpen());

							}
						}
					}
					return retMd;
				} else {
					return null;
				}
			}

		}

	}

	public static File getDataFile(int minutes, String code) {
		File f;
		File base = new File("/ext");

		if (minutes == Statics.dayBarMinutes) {
			base = new File(base, "day");
			f = new File(base, code + ".day");
		} else if (minutes == Statics.min05Minutes) {
			base = new File(base, "min5");
			f = new File(base, code + ".mn5");
		} else {
			throw new RuntimeException("unknown dataType with min " + minutes + " for code " + code);
		}
		return f;
	}

	Stock stk;

	KLineMerger merger;
	private List<MinDay> mkdLst = new ArrayList<>();
	private int readIndex = 0;
	private int orgBarRangeSeconds;
	boolean isDay;
	private int targetSecondsPerBar;

	private StockDataFilter flt;

	private MinDay theLastOne;

	public MinDayLoader(Stock stk, StockDataFilter flt, int orgBarRangeMinutes) throws IOException {
		this(stk, flt, orgBarRangeMinutes * 60, orgBarRangeMinutes);
	}

	public MinDayLoader(Stock stk, StockDataFilter flt, int targetSecondsPerBar, int orgBarRangeMinutes, List<MinDay> orgList)
			throws IOException {
		initParas(stk, flt, targetSecondsPerBar, orgBarRangeMinutes);
		init(orgList.listIterator());
	}

	public MinDayLoader(Stock stk, int orgBarRangeMinutes) throws IOException {
		this(stk, new AllAcceptFilter(), orgBarRangeMinutes * 60, orgBarRangeMinutes);
	}

	public MinDayLoader(Stock stk, int targetSecondsPerBar, int orgBarRangeMinutes) throws IOException {
		this(stk, new AllAcceptFilter(), targetSecondsPerBar, orgBarRangeMinutes);
	}

	public MinDayLoader(Stock stk, StockDataFilter flt, int targetSecondsPerBar, int orgBarRangeMinutes) throws IOException {
		this(stk, flt, targetSecondsPerBar, orgBarRangeMinutes, (File) null);
	}

	private LogHelper log;

	public MinDayLoader(Stock stk, StockDataFilter flt, int targetSecondsPerBar, int orgBarRangeMinutes, File orgFile) throws IOException {
		log = new LogHelper("mindayLoader-" + stk + "_by_" + flt);
		if (targetSecondsPerBar > 2 * 60 * 60) {
			targetSecondsPerBar += 90 * 60;// noon 11:30-15:00
		}
		initParas(stk, flt, targetSecondsPerBar, orgBarRangeMinutes);

		File f;
		if (orgFile == null) {
			f = getDataFile(orgBarRangeMinutes, stk.getCode());
		} else {
			f = orgFile;
		}
		init(f);

	}

	private void initParas(Stock stk, StockDataFilter flt, int targetSecondsPerBar, int orgBarRangeMinutes) {
		this.orgBarRangeSeconds = orgBarRangeMinutes * 60;
		this.targetSecondsPerBar = targetSecondsPerBar;
		isDay = orgBarRangeMinutes == Statics.dayBarMinutes;
		this.stk = stk;
		this.setFlt(flt);
	}

	@Override
	public List<MinDay> getDayList() {
		return getDayList(this.mkdLst, Statics.dayBarSeconds / this.targetSecondsPerBar);
	}

	public static List<MinDay> getDayList(List<MinDay> mkdLst, int barPerDay) {
		barPerDay = barPerDay < 1 ? 1 : barPerDay;

		List<MinDay> ret = new ArrayList<>();
		List<MinDay> tmp = null;
		for (MinDay day : mkdLst) {
			if (tmp == null) {
				if (day.getOpenHour() != 9 || day.getOpenMinutes() != 30) {
					throw new RuntimeException("expect open time at 9:30, but real is " + day.getOpenHour() + ":" + day.getOpenMinutes());
				}
				tmp = new ArrayList<>();
			}
			tmp.add(day);
			if (day.getCloseHour() == 15) {
				if (tmp.size() != barPerDay) {
					throw new RuntimeException("expect " + barPerDay + ", but real is " + tmp.size());
				}
				MinDay n = MinDay.merge(tmp);
				ret.add(n);
				tmp = null;

			}
		}
		return ret;
	}

	private boolean appendDataLoaded = false;

	public void appendIncData() throws IOException {
		try {
			if (!appendDataLoaded) {
				appendDataLoaded = true;
				DataType dataType = DataType.getDataTypeByBarSeconds(this.orgBarRangeSeconds);
				// get max time of now, load segments
				IncDataIO holder = new IncDataIO(dataType, stk);
				// get increment datas, NOT include day segment
				List<MinDay> inc = holder.getFinishedList(theLastOne);

				init(inc.listIterator());
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	private void init(Iterator<MinDay> itr) {
		if (null == merger)
			merger = new KLineMerger(targetSecondsPerBar);
		while (itr.hasNext()) {
			MinDay mdRaw = itr.next();
			feed(mdRaw);
		}
	}

	private void init(File f) throws IOException {
		if (f.exists()) {
			log.debug("loading from file %s", f);
			LineReader lr = new LineReader(f);
			if (null == merger)
				merger = new KLineMerger(targetSecondsPerBar);

			while (lr.hasNext()) {
				String line = lr.next();
				MinDay mdRaw;
				if (isDay) {
					mdRaw = parseDayLine(line);
				} else {
					mdRaw = makeMineDay(line);
				}
				feed(mdRaw);
			}
			lr.close();
		} else {
			log.warn("MISSING loading file %s", f);
		}
	}

	private void feed(MinDay mdRaw) {
		theLastOne = mdRaw;
		if (isDay) {
			if (getFlt().accept(mdRaw)) {
				mkdLst.add(mdRaw);
			}
		} else {
			if (getFlt().accept(mdRaw)) {
				MinDay md = merger.feed(mdRaw);
				if (md != null) {
					mkdLst.add(md);
				}
			}
		}
	}

	private MinDay parseDayLine(String line) {
		return MinDay.parseDayLine(line, stk);
	}

	private MinDay makeMineDay(String line) {
		return MinDay.parseMinDay(line, orgBarRangeSeconds, stk);
	}

	@Override
	public boolean hasNext() {
		return readIndex < this.mkdLst.size();
	}

	public MinDay peek() {
		if (readIndex < this.mkdLst.size()) {
			return this.mkdLst.get(readIndex);
		} else {
			return null;
		}
	}

	@Override
	public MinDay next() {
		return this.mkdLst.get(readIndex++);
	}

	public StockDataFilter getFlt() {
		return flt;
	}

	public void setFlt(StockDataFilter flt) {
		this.flt = flt == null ? new AllAcceptFilter() : flt;
	}

	public MinDay getTheLastOne() {
		return theLastOne;
	}

	public List<MinDay> getList() {
		return mkdLst;
	}

	public static List<MinDay> loadMin05(File file, boolean appendInc) throws IOException {
		Stock stk = AllStock.getStockByCode(Misc.substring(file.getName(), null, "."));
		return loadMin05(stk, file, appendInc);
	}

	public static List<MinDay> loadMin05(Stock stk, boolean appendInc) throws IOException {
		return loadMin05(stk, null, appendInc);
	}

	public static List<MinDay> loadMin05(Stock stk, File fromFile, boolean appendInc) throws IOException {
		MinDayLoader loader = new MinDayLoader(stk, new AllAcceptFilter(), Statics.min05BarSeconds, Statics.min05Minutes, fromFile);
		if (appendInc)
			loader.appendIncData();
		return loader.getList();
	}

	public static void main(String[] args) throws IOException {
		loadMin05(new File("/tmp/min5/002635.mn5"), false);
	}
}
