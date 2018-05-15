package com.bmtech.datamine.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bmtech.datamine.AllStock;
import com.bmtech.datamine.Statics;
import com.bmtech.datamine.Stock;
import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.datamine.data.mday.loader.AllAcceptFilter;
import com.bmtech.datamine.data.mday.loader.MinDayLoader;
import com.bmtech.utils.ForEach;
import com.bmtech.utils.Misc;
import com.bmtech.utils.io.LineWriter;

public class CheckDaysVsMin05 {

	Map<String, Integer> map = new HashMap<>();
	Map<String, Integer> mapRev = new HashMap<>();
	final int maxDay = 20180408;
	final int minDay = 20151201;
	public final static int sourceBarRangeMinutes = 5;
	public final static int secondsPerBar = Statics.target05MinSeconds;
	// public final File tmpMin05 = new File("/tmp/min5");
	// public final File tmpMin05 = new File("/tmp/min5-2");
	File from;
	File to;

	CheckDaysVsMin05() throws IOException {

		boolean first = true;
		if (first) {
			from = null;
			to = new File("/tmp/min5");
		} else {
			from = new File("/tmp/min5");
			to = new File("/tmp/min5-2");
		}
		Misc.besureDirExists(to);
	}

	public void loadDays(File dir) throws IOException {
		File[] fs = dir.listFiles();
		for (File f : fs) {
			String code = Misc.substring(f.getName(), null, ".");
			if (code.length() == 0)
				continue;

			Stock stk = AllStock.getStockByCode(code);
			List<MinDay> lst = MinDay.readDayFile(f, stk);
			for (MinDay mday : lst) {
				int day = mday.getDay();
				if (day > maxDay || day < minDay)
					continue;
				int closePriceCent = priceCent(mday.getOpen());
				map.put(code + "@" + day, closePriceCent);
			}
		}
	}

	public void checkMin05List(Stock stock, List<MinDay> lst, int maxDayToSave) throws IOException {

		List<List<MinDay>> dayLists = Statics.toDayList(lst);
		List<List<MinDay>> dayListsNew = new ArrayList<List<MinDay>>();

		for (List<MinDay> aDay : dayLists) {
			MinDay md = aDay.get(0);
			int day = md.getDay();
			int openPriceCent = priceCent(md.getOpen());

			if (day > maxDay)
				continue;

			boolean use = true;
			Integer otherCent = 0;
			if (stock.equals("1A0001") || stock.equals("399001")) {

			} else {
				otherCent = map.get(stock.getCode() + "@" + day);
				int error = 0;
				if (otherCent == null) {
					if (openPriceCent > 1000) {
						error = 1;
					} else {
						error = 2;
					}
					if (day > 20180101) {
						error = 3;
					}
				} else {
					if (Math.abs(otherCent - openPriceCent) > 10) {
						error = 4;
					}
				}
				if (error != 0 && error != 3) {
					use = false;
					System.out.println(
							"Error" + error + " " + stock.getCode() + "@" + day + ", " + otherCent + " != " + openPriceCent + " " + md);
				}
			}

			if (use) {
				dayListsNew.add(aDay);
				mapRev.put(stock.getCode() + "@" + day, otherCent);
			}

		}
		save(stock, dayListsNew, maxDayToSave);
	}

	public void save(Stock stk, List<List<MinDay>> dayLists, int maxDayToSave) throws IOException {
		LineWriter lw = new LineWriter(new File(to, stk.getCode() + ".mn5"));
		try {
			long last = 0;
			for (List<MinDay> lst : dayLists) {
				if (lst.size() != 48) {
					throw new RuntimeException("not 48, " + lst.size() + ", its " + lst);
				}
				for (MinDay mday : lst) {
					if (mday.getDay() > maxDayToSave)
						continue;
					{
						long timeLong = mday.dayTimeEnd();
						// System.out.println(timeLong);
						if (timeLong <= last) {
							throw new RuntimeException("mismatchorder!" + last + ">=" + timeLong);
						}
						last = timeLong;
					}
					lw.writeLine(mday.toLine());
				}

			}
		} finally {
			lw.close();
		}
	}

	public void check(Stock stock) throws IOException {
		int maxDayToSave = -1;
		{
			MinDayLoader loader = new MinDayLoader(stock, new AllAcceptFilter(), secondsPerBar, sourceBarRangeMinutes, from);
			List<MinDay> l = loader.getList();
			if (l.size() == 0)
				maxDayToSave = 0;
			else
				maxDayToSave = l.get(l.size() - 1).getDay();
		}
		MinDayLoader loader = new MinDayLoader(stock, new AllAcceptFilter(), secondsPerBar, sourceBarRangeMinutes, from);
		loader.appendIncData();

		List<MinDay> datas = loader.getList();
		checkMin05List(stock, datas, maxDayToSave);
	}

	public void check() throws IOException {

		File[] froms;
		if (from == null) {
			froms = new File("/ext/min5/").listFiles();
		} else {
			froms = from.listFiles();
		}
		Arrays.sort(froms, (a, b) -> {
			return a.getName().compareTo(b.getName());
		});
		ForEach.asc(froms, (f, fI) -> {

			String name = Misc.substring(f.getName(), null, ".");
			if (name.length() < 6) {
				System.out.println("skip  " + name);
				return;
			}
			Stock stk = AllStock.getStockByCode(name);
			System.out.println("for " + stk);
			check(stk);
		});
	}

	private int priceCent(double open) {
		return (int) Math.floor(open);
	}

	public void checkMissing() {
		List<String> lst = new ArrayList<>(this.map.keySet());
		lst.sort((a, b) -> {
			return a.compareTo(b);
		});
		for (String s : this.map.keySet()) {
			if (!mapRev.containsKey(s)) {
				System.out.println("//mising " + s);
				;
			}
		}
	}

	public static void main(String[] args) throws IOException {
		CheckDaysVsMin05 checker = new CheckDaysVsMin05();

		File dayDir = new File("/tmp/history.out/day");
		checker.loadDays(dayDir);
		System.out.println("load day-code " + checker.map.size());
		System.out.println("checking ");
		checker.check();

		checker.checkMissing();
	}
}
