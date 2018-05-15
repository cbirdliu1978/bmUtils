package com.bmtech.datamine.data.inc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bmtech.datamine.Statics;
import com.bmtech.datamine.Stock;
import com.bmtech.datamine.data.DataIntergrationVerifier;
import com.bmtech.datamine.data.DataType;
import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.TripleValue;

public class IncHolder {

	protected final DataType dataType;
	protected final Stock stock;

	private List<MinDay> list = new ArrayList<>();

	public IncHolder(DataType dataType, Stock stock) {
		this.stock = stock;
		this.dataType = dataType;
	}

	public List<MinDay> getListCopy() {
		List<MinDay> ret = new ArrayList<>(this.list.size());
		ret.addAll(this.list);
		return ret;
	}

	public List<MinDay> getList(MinDay bigThan) {
		if (bigThan == null)
			return getList(0, 0, 0);
		return getList(bigThan.getDay(), bigThan.getCloseHour(), bigThan.getCloseMinutes());
	}

	private List<MinDay> getList(int bigThanDay, int bigThanHour, int bigThanMinute) {
		int endPos = list.size();
		int startPos = -1;
		for (int x = 0; x < endPos; x++) {
			MinDay mday = list.get(x);
			boolean matched = false;

			int dayDiff = mday.getDay() - bigThanDay;
			if (dayDiff > 0) {
				matched = true;
			} else if (dayDiff == 0) {
				int closeHourDiff = mday.getCloseHour() - bigThanHour;
				if (closeHourDiff > 0) {
					matched = true;
				} else if (closeHourDiff == 0) {
					int closeMinuteDiff = mday.getCloseMinutes() - bigThanMinute;
					if (closeMinuteDiff > 0) {
						matched = true;
					}
				}
			}
			if (matched) {
				startPos = x;
				break;
			}
		}
		if (startPos == -1)
			return Collections.emptyList();
		List<MinDay> ret = new ArrayList<>(list.subList(startPos, endPos));
		return ret;
	}

	public static List<MinDay> mergeTwoList(List<MinDay> org, List<MinDay> newest) throws Exception {
		List<MinDay> ret = new ArrayList<MinDay>();
		int orgIdx = 0, newestIdx = 0;
		while (true) {
			TripleValue diff;
			if (orgIdx >= org.size()) {
				if (newestIdx >= newest.size()) {
					break;
				} else {
					diff = TripleValue.pos;// append newewst
				}
			} else {
				if (newestIdx >= newest.size()) {
					diff = TripleValue.neg;// append org
				} else {
					// select
					MinDay iOrg = org.get(orgIdx);
					MinDay iNewest = newest.get(newestIdx);
					diff = iOrg.compareTime(iNewest);
				}
			}

			if (diff == TripleValue.neg) {// use org
				ret.add(org.get(orgIdx));
				orgIdx++;
			} else if (diff == TripleValue.eq) {
				// use newewst and both move forward
				ret.add(newest.get(newestIdx));
				newestIdx++;
				orgIdx++;
			} else {
				ret.add(newest.get(newestIdx));
				newestIdx++;
			}
		}
		if (!DataIntergrationVerifier.isOrderOk(ret)) {
			throw new Exception("order check fail for merged list " + ret + ", from org " + org + ", newest " + newest);
		}
		return ret;
	}

	/**
	 * before call this method, please be-sure the input covers the range of base and this holder!
	 * 
	 * 
	 * @param toAdd
	 * @throws Exception
	 */
	public void merge(List<MinDay> toAdd) throws Exception {
		if (!DataIntergrationVerifier.isCodeMatch(toAdd, this.stock.getCode())) {
			throw new IllegalArgumentException("the list is not match the code " + this.stock);
		}
		if (!DataIntergrationVerifier.isDataTypeMatch(toAdd, this.dataType)) {
			throw new IllegalArgumentException("the list is not match the dataType " + toAdd);
		}
		if (!DataIntergrationVerifier.isOrderOk(toAdd)) {
			throw new IllegalArgumentException("the list is not in the order " + toAdd);
		}
		this.list = mergeTwoList(this.list, toAdd);
	}

	public List<MinDay> getFinishedList() {
		return getFinishedList(this.getList(null));
	}

	public List<MinDay> getFinishedList(MinDay bigThan) {
		return getFinishedList(this.getList(bigThan));
	}

	public void trim2FinishedList() {
		this.list = this.getFinishedList();
	}

	private static List<MinDay> getFinishedList(List<MinDay> lst) {
		List<MinDay> ret = new ArrayList<>();
		int end = -1;
		for (int x = lst.size() - 1; x >= 0; x--) {
			MinDay md = lst.get(x);
			if (md.getEndTime() == Statics.m15_00) {
				end = x + 1;
				break;
			}
		}
		if (end != -1)
			ret.addAll(lst.subList(0, end));
		return ret;
	}

	public Stock getStock() {
		return stock;
	}

	public DataType getDataType() {
		return dataType;
	}

	public int size() {
		return list.size();
	}

	public void saveToInc() throws Exception {

		IncDataIO io = new IncDataIO(this.dataType, this.stock);
		io.setCanWrite();
		io.merge(list);
		io.save(null);
	}
}
