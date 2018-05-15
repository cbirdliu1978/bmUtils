package com.bmtech.utils.io.diskMerge.gzipbased;

import java.io.File;
import java.util.Comparator;

import com.bmtech.utils.Misc;
import com.bmtech.utils.io.diskMerge.MRTool;
import com.bmtech.utils.io.diskMerge.MRecord;
import com.bmtech.utils.io.diskMerge.RecordFactoryImpl;

public class GZIPRecordFactoryTest extends RecordFactoryImpl {
	public static class EComparator implements Comparator<MRecord> {

		@Override
		public int compare(MRecord o1, MRecord o2) {
			return ((EMRecord) o1).e.compareTo(((EMRecord) o2).e);
		}

	}

	public static class EMRecord extends MRecord {
		E e;

		@Override
		public void init(String paramString) throws Exception {
			e = E.parse(paramString);

		}

		@Override
		public String serialize() {
			return e.toString();
		}

	}

	public static class E implements Comparable<E> {
		int cond;
		String code;
		int day;

		public E(int cond, String code, int day) {
			super();
			this.cond = cond;
			this.code = code;
			this.day = day;
		};

		public String toString() {
			return cond + "\t" + eValue();
		}

		public String eValue() {
			return code + "\t" + day;
		}

		@Override
		public int compareTo(E o1) {
			return cond - (o1.cond);
		}

		public static E parse(String paramString) {
			String tokens[] = paramString.split("\t");
			return new E(Integer.parseInt(tokens[0]), new String(tokens[1].getBytes()), Integer.parseInt(tokens[2]));
		}

	}

	public static void main(String[] args) throws Exception {
		File from = new File("/tmp/gz/2016-11-23_14_13_07.818.gz");
		File to = new File("/tmp/un-gz/2016-11-23_14_13_07.818");
		GZIPRecordFactory fac = new GZIPRecordFactory();
		fac.setComparator(new EComparator());
		fac.setRecordClass(EMRecord.class);

		System.out.println(Misc.timeStr());
		try {
			MRTool.sortFile(to, from, fac, 10 * 1024 * 1024);
		} finally {
			System.out.println(Misc.timeStr());
		}
	}
}
