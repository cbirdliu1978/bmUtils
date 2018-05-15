package com.bmtech.datamine.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bmtech.datamine.AllStock;
import com.bmtech.datamine.Statics;
import com.bmtech.datamine.Stock;
import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.datamine.data.mday.loader.AllAcceptFilter;
import com.bmtech.datamine.data.mday.loader.MinDayLoader;
import com.bmtech.utils.ForEach;
import com.bmtech.utils.log.LogHelper;
import com.bmtech.utils.var.VarBoolean;

public class DataIntergrationVerifier {
	private final Stock stk;
	LogHelper log;

	public DataIntergrationVerifier(Stock stk) {
		this.stk = stk;
		log = new LogHelper(stk.getCode() + "-integeration");
	}

	public static List<MinDay> loadAndCheckOrderAndDataType(Stock stk, DataType dataType) throws Exception {
		MinDayLoader loader = new MinDayLoader(stk, new AllAcceptFilter(), dataType.orgBarRangeSeconds(), dataType.orgBarRangeMinutes());
		return loadAndCheckOrderAndDataType(stk, dataType, loader);
	}

	public static List<MinDay> loadAndCheckOrderAndDataType(Stock stk, DataType dataType, MinDayLoader loader) throws Exception {

		// call list verifier to verify type and sequential

		List<MinDay> dataList = loader.getList();
		if (!isDataTypeMatch(dataList, dataType)) {
			throw new Exception("min05DataType mismatch for stock " + stk);
		}
		if (!isOrderOk(dataList)) {
			throw new Exception("min05 Order not match");
		}
		return dataList;
	}

	public static boolean checkStartAndEndTime(List<MinDay> lst) {
		return checkStartAndEndTime(lst.get(0), lst.get(lst.size() - 1));
	}

	public static boolean checkStartAndEndTime(MinDay first, MinDay last) {
		boolean ret = first.getStartTime() == Statics.m09_30 && last.getEndTime() == Statics.m15_00;

		return ret;
	}

	public static int[] crossCheckIfMissingDay(String code, Set<Integer> set1, Set<Integer> set2, int minDay, int maxDay) {

		Set<Integer> both = new HashSet<>();
		set2.forEach((x) -> {
			if (set1.contains(x)) {
				both.add(x);
			}
		});
		return crossCheckIfMissingDay(code, set1, set2, both, minDay, maxDay);
	}

	private static int[] crossCheckIfMissingDay(String code, Set<Integer> set1, Set<Integer> set2, Set<Integer> both, int minDay,
			int maxDay) {
		set2.forEach((x) -> {
			if (!both.contains(x)) {
				if (x >= minDay && x <= maxDay) {
					// throw new RuntimeException("min05 data missing day " + x);
					System.out.println("min05 data missing day " + x + " for code " + code);
				}
			}
		});

		// log.info("checking if min05 data has more day");
		set1.forEach((x) -> {
			if (!both.contains(x)) {
				if (x >= minDay && x <= maxDay) {
					// throw new RuntimeException("dayData missing day " + x + " for day " + x);
					// System.out.println("dayData missing day " + x + " for day " + code);
				}
			}
		});
		int ret[] = new int[] { minDay, maxDay };
		return ret;
	}

	public static int[] crossCheckIfMissingDay(String code, List<MinDay> min05List, List<MinDay> dayList) {
		// continious check
		Set<Integer> min05Set = new HashSet<>();
		Set<Integer> daySet = new HashSet<>();

		ForEach.asc(min05List, (mday, I) -> {
			min05Set.add(mday.getDay());
		});
		ForEach.asc(dayList, (mday, I) -> {
			daySet.add(mday.getDay());
		});

		Set<Integer> both = new HashSet<>();
		daySet.forEach((x) -> {
			if (min05Set.contains(x)) {
				both.add(x);
			}
		});

		int minDay = Math.max(min05List.get(0).getDay(), dayList.get(0).getDay());
		int maxDay = Math.min(min05List.get(min05List.size() - 1).getDay(), dayList.get(dayList.size() - 1).getDay());
		return crossCheckIfMissingDay(code, min05Set, daySet, minDay, maxDay);
	}

	public void verify(List<MinDay> min05List, List<MinDay> dayList) throws Exception {

		isCodeMatch(min05List, this.stk.getCode());
		isCodeMatch(dayList, this.stk.getCode());
		if (dayList.size() == 0 && min05List.size() == 0) {
			throw new Exception(" bad code " + stk + ", no datas");
		}

		if (min05List.size() == 0) {
			throw new Exception("missing min05 List for stock " + stk);
		}
		if (dayList.size() == 0) {
			throw new Exception("missing day List for stock " + stk);
		}

		if (!checkStartAndEndTime(min05List)) {
			log.error("error start and end time for min05 %s => %s", min05List.get(0), min05List.get(min05List.size() - 1));
			throw new RuntimeException("min05 list start and end time error! " + stk);
		}

		if (!checkStartAndEndTime(dayList)) {
			log.error("error start and end time for day %s => %s", dayList.get(0), dayList.get(dayList.size() - 1));
			throw new RuntimeException("day list start and end time error!" + stk);
		}

		int crossDay[] = crossCheckIfMissingDay(stk.getCode(), min05List, dayList);
		log.debug("cross day [%s, %s]", crossDay[0], crossDay[1]);
		// data integeration match, sum up min05 to Data
		Map<Integer, MinDay> dayMinMap = new HashMap<Integer, MinDay>();
		dayList.forEach((x) -> {
			dayMinMap.put(x.getDay(), x);
		});

		List<MinDay> newDayList = MinDayLoader.getDayList(min05List, 12 * 4);
		newDayList.forEach((n) -> {
			MinDay o = dayMinMap.get(n.getDay());
			if (o == null) {
				// log.error("missing day %s", n.getDay());

			} else {
				if (!dataValueRufMatch(n, o)) {
					boolean throwExc = true;
					if (n.getDay() == 20161107) {
						if (n.getCode().equals("603011"))
							throwExc = false;
						if (n.getCode().equals("603601"))
							throwExc = false;
						if (n.getCode().equals("603611"))
							throwExc = false;
						if (n.getCode().equals("603901"))
							throwExc = false;
					}
					if (throwExc && o.getDay() != 20161107) {
						// throw new RuntimeException("minday values misMatch for " + stk + "\n\tday " + o + "\n\tmin " + n);
						System.out.println("minday values misMatch for " + stk + "\n\tday " + o + "\n\tmin " + n);
					}

				}
			}
		});

	}

	public static boolean dataValueRufMatch(MinDay n, MinDay o) {
		return StockDiffer.differ.diff(o, n);
	}

	public Stock getStock() {
		return stk;
	}

	public static boolean isDataTypeMatch(List<MinDay> list, DataType dataType) {
		VarBoolean accType = new VarBoolean(true);
		ForEach.asc(list, (i, iI) -> {
			if (!isDataTypeMatch(i, dataType)) {
				accType.value = false;// reject
				return true;
			}
			return false;
		});
		return accType.value;
	}

	public static boolean isDataTypeMatch(MinDay toAdd, DataType dataType) {
		int startSec = toAdd.getStartTime() / 1000;
		int endSec = toAdd.getEndTime() / 1000;
		boolean isMatch = false;
		int usedSec = endSec - startSec;
		if (usedSec == dataType.orgBarRangeSeconds()) {
			isMatch = true;
		} else {

			return false;
		}
		return isMatch;
	}

	public static boolean isOrderOk(List<MinDay> lst) throws Exception {
		for (int x = 1; x < lst.size(); x++) {
			MinDay now = lst.get(x);
			MinDay last = lst.get(x - 1);
			boolean orderOk = isOrderOk(now, last);
			if (!orderOk) {
				throw new Exception(last.getCode() + " order error at index " + x + ", old is " + last + " vs newer " + now);
			}
		}
		return true;
	}

	public static boolean isOrderOk(MinDay toAdd, MinDay last) {
		int dayDiff = toAdd.getDay() - last.getDay();
		if (dayDiff > 0) {
			if (toAdd.getStartTime() == Statics.m09_30 && last.getEndTime() == Statics.endMarketTime)
				return true;
		} else if (dayDiff == 0) {
			if (toAdd.getStartTime() == last.getEndTime())
				return true;
			else {
				if (toAdd.getStartTime() == Statics.m13_00 && last.getEndTime() == Statics.m11_30)
					return true;
			}
		}
		// System.out.println("not match! ");
		return false;
	}

	public static boolean isCodeMatch(List<MinDay> toAdd, String code) {
		VarBoolean isMatch = new VarBoolean(true);
		ForEach.asc(toAdd, (i, iI) -> {
			if (!AllStock.getStockByCode(i.getCode()).equals(code)) {
				isMatch.value = false;
				return true;
			}
			return false;
		});
		return isMatch.value;
	}

	private static double maxChangePercent = 0.21;

	private static double diff(double from, double to) {
		return Math.abs(Statics.ratio(from, to));
	}

	public static boolean isChangeInRange(MinDay from, MinDay to) {
		return diff(from.getClose(), to.getOpen()) < maxChangePercent || isChangeInRange(from) || isChangeInRange(to);
	}

	public static boolean isChangeInRange(MinDay var) {
		return diff(var.getOpen(), var.getClose()) <= maxChangePercent && (diff(var.getLow(), var.getHigh()) <= maxChangePercent);
	}

	public static boolean isChangeInRange(List<MinDay> list) {
		VarBoolean ret = new VarBoolean(true);
		ForEach.asc(list, (x, xI) -> {
			if (xI > 0) {
				MinDay last = list.get(xI - 1);
				boolean inRange = isChangeInRange(last, x);
				if (!inRange) {
					ret.value = false;
				}
			}
			if (ret.value) {
				return false;// continue compare
			} else {
				return true;// stop compare
			}
		});
		return ret.value;
	}

}
