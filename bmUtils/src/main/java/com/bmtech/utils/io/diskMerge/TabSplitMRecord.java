package com.bmtech.utils.io.diskMerge;

import java.util.Comparator;

import com.bmtech.utils.StringTokenItr;

public class TabSplitMRecord extends MRecord {
	public static Comparator<MRecord> comparator = new Comparator<MRecord>() {
		@Override
		public int compare(MRecord o1, MRecord o2) {
			return ((TabSplitMRecord) o1).getKey().compareTo(((TabSplitMRecord) o2).getKey());
		}

	};
	private String org;
	private StringTokenItr itr;

	@Override
	public void init(String str) throws Exception {
		this.org = str;
		this.itr = new StringTokenItr(str);

	}

	@Override
	public String serialize() {
		return org;
	}

	/**
	 * index 0 is key, others is value
	 * 
	 * @return
	 */
	public StringTokenItr getValues() {
		return itr;
	}

	public String getKey() {
		return itr.getString(0);
	}

	public int getInt(int index) {
		return itr.getInt(index);
	}

	public double getDouble(int index) {
		return itr.getDouble(index);
	}

	public String gettString(int index) {
		return itr.getString(index);
	}

	public double getDoublePercent(int index) {
		return itr.getDoublePercent(index);
	}
}
