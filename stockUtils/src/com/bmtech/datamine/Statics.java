package com.bmtech.datamine;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.Misc;
import com.bmtech.utils.log.LogHelper;

public class Statics {
	public static int getTime(int hour, int minute) {
		return ((hour - 8) * 60 + minute) * 60 * 1000;
	}

	public static final int m09_30 = getTime(9, 30);
	public static final int m09_35 = getTime(9, 35);
	public static final int m11_30 = getTime(11, 30);
	public static final int m13_00 = getTime(13, 00);
	public static final int m14_30 = getTime(14, 30);
	public static final int m14_55 = getTime(14, 55);
	public static final int m15_00 = getTime(15, 00);

	public static final int startMarketTime = m09_30;
	public static final int endMarketTime = m15_00;

	public static File baseDir = new File("/data/datamine").getAbsoluteFile();
	public static final int tradeDaySecondsIncludeNoon = (int) ((15 - 9.5) * 60 * 60);
	public static final int tradeDayMsIncludeNoon = tradeDaySecondsIncludeNoon * 1000;

	static LogHelper log = LogHelper.log;

	public static int min05Minutes = 5;
	public static int target30MinMinutes = 30;
	public static int dayBarMinutes = 4 * 60;

	public static int min05BarSeconds = min05Minutes * 60;
	public static int min05BarMs = min05BarSeconds * 1000;
	public static int target05MinSeconds = min05Minutes * 60;
	public static int target30MinSeconds = target30MinMinutes * 60;
	public static int dayBarSeconds = dayBarMinutes * 60;


	public static int day(long time) {
		String str = new SimpleDateFormat("yyyyMMdd").format(time);
		return Integer.parseInt(str);
	}

	public static int toCent(double dValue) {
		return (int) Math.round(dValue * 100);
	}

	public static double ratio(double from, double to) {
		return (to - from) / from;
	}

	public static int scale1000(double from, double to) {
		double ratio = ratio(from, to);
		return scale1000(ratio);
	}

	public static int scale1000(double ratio) {
		return (int) Math.round(1000 * ratio);
	}

	public static int scale10(double ratio) {
		return (int) Math.round(10 * ratio);
	}

	public static int scale100(double ratio) {
		return (int) Math.round(100 * ratio);
	}

	public static int divScale100(double fenzi, double fenmu) {
		return scale100(Misc.div(fenzi, fenmu));
	}

	public static List<List<MinDay>> toDayList(List<MinDay> min05List) {
		int nowDay = -1;
		List<MinDay> day = null;
		ArrayList<List<MinDay>> ret = new ArrayList<List<MinDay>>();
		for (MinDay x : min05List) {
			if (x.getDay() != nowDay) {
				day = new ArrayList<>();
				ret.add(day);
				nowDay = x.getDay();
			}
			day.add(x);
		}
		return ret;
	}

	public static void checkMin05Day(List<MinDay> day) throws Exception {
		if (day.size() != 48) {
			Misc.throwNewRuntimeException("expect size 48, but is %s, its %s", day.size(), day);
		}

		MinDay first = day.get(0);
		MinDay last = day.get(day.size() - 1);
		if (first.getDay() != last.getDay()) {
			Misc.throwNewRuntimeException("mismatch day! first = %s, last  = %s", first, last);
		}
		if (first.getStartTime() != Statics.startMarketTime) {
			Misc.throwNewRuntimeException("not start time %s", first);
		}

		if (last.getEndTime() != Statics.endMarketTime) {
			Misc.throwNewRuntimeException("not end time %s", last);
		}
	}

	public static int findDayIndex(int day, List<MinDay> days) {
		MinDay md = new MinDay();
		md.setDay(day);
		int index = Collections.binarySearch(days, md, new Comparator<MinDay>() {

			@Override
			public int compare(MinDay o1, MinDay o2) {
				return o1.getDay() - o2.getDay();
			}

		});
		if (index < 0)
			return -1;
		return index;
	}

}
