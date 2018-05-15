package com.bmtech.datamine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bmtech.datamine.data.stockcodefilters.All_Filter;
import com.bmtech.datamine.data.stockcodefilters.StockCodeFilter;
import com.bmtech.datamine.data.stockcodefilters._60_00_Filter;
import com.bmtech.utils.Misc;
import com.bmtech.utils.io.FileGet;
import com.bmtech.utils.log.L;

public class AllStock {

	private static Set<String> badCode = new HashSet<>();
	static {
		badCode.add("000024");
		badCode.add("000594");
		badCode.add("000024");
	}
	public static final Stock sh = new Stock("1A0001", "上证");
	public static final Stock sh_alia = new Stock("000001", "上证");
	public static final Stock sz = new Stock("399001", "深证");
	// public static final Stock cy = new Stock("3990062", "创业");

	public static final AllStock instance = new AllStock();

	// public static double spanWeight(String code, double pricePerShare) {
	// double lt = instance.ltAg(code);
	// return lt * pricePerShare;
	// }

	public static Stock getStockByCode(String code) {
		Stock stock = instance.getByCode(code);
		if (stock == null) {
			L.d("not usedStock %s", code);
			stock = new Stock(code, code);
		}
		return stock;
	}

	// private Map<String, Map<String, String>> basicInfo = null;
	// private File basicJsonFile = new File("./config/stockBasic.json");
	private List<Stock> data = new ArrayList<>();
	private Map<String, Stock> codeMap = new HashMap<>();
	private Map<String, Stock> nameMap = new HashMap<>();

	public boolean inCodeMap(String code) {
		return codeMap.containsKey(code) && !badCode.contains(code);
	}

	private AllStock() {
		String txt;
		try {

			{
				Stock[] datas = new Stock[] { sh, sz };
				for (Stock stock : datas) {
					data.add(stock);
					codeMap.put(stock.getCode(), stock);
					nameMap.put(stock.getName(), stock);
				}

			}

			txt = FileGet.getStr(new File("config/allStock.json"));
			Stock[] datas = Misc.parseJson(txt, Stock[].class);
			Arrays.sort(datas, (s1, s2) -> {
				return Integer.parseInt(s1.getCode()) - Integer.parseInt(s2.getCode());
			});
			for (Stock stock : datas) {
				if (badCode.contains(stock.getCode()))
					continue;
				data.add(stock);
				codeMap.put(stock.getCode(), stock);
				nameMap.put(stock.getName(), stock);

			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public Stock getByName(String name) {
		return this.nameMap.get(name);
	}

	public Stock getByCode(String code) {
		if (code.length() == 6) {
			return this.codeMap.get(code);
		} else {
			throw new RuntimeException("unknown stock code " + code);
		}

	}

	public Iterator<Stock> getStockItr(StockCodeFilter flt) {
		return getStocks(flt);
	}

	public Iterator<Stock> getStocks(StockCodeFilter flt) {
		List<Stock> lst = new ArrayList<Stock>();
		for (Stock s : data) {
			if (flt.accept(s)) {
				lst.add(s);
			}
		}
		return lst.iterator();
	}

	public Iterator<Stock> getStocks() {
		return this.getStocks(new _60_00_Filter());
	}

	public Iterator<Stock> getStockItr() {
		return this.getStocks(new _60_00_Filter());
	}

	public Iterator<Stock> getRealStockIterator(String... codes) {
		return new Iterator<Stock>() {
			int idx = -1;

			@Override
			public boolean hasNext() {
				idx++;
				if (idx >= data.size())
					return false;
				Stock s = data.get(idx);
				if (s.equals(sz) || s.equals(sh))
					return hasNext();
				if (codes.length > 0) {
					for (String code : codes) {
						if (s.isMyCode(code)) {
							return true;
						}
					}
					return hasNext();
				} else {
					return true;
				}
			}

			@Override
			public Stock next() {

				return data.get(idx);
			}

		};
	}

	public Iterator<Stock> randRealStockIterator(int num) {
		List<Stock> lst = new ArrayList<Stock>();
		Set<Integer> selected = new HashSet<Integer>();
		while (num > 0) {
			int idx = Misc.randInt(0, this.data.size());
			if (selected.contains(idx))
				continue;
			Stock s = this.data.get(idx);
			if (s.equals(sz) || s.equals(sh))
				continue;
			lst.add(s);
			num--;
		}
		return lst.iterator();
	}

	public boolean isSh(String code) {
		if (code.startsWith("60") || code.equalsIgnoreCase(sh.getCode()))
			return true;
		return false;
	}

	public static void main(String[] args) {
		Iterator<Stock> itr = instance.getStockItr(new All_Filter());
		while (itr.hasNext()) {
			System.out.println(itr.next());
		}
	}

}
