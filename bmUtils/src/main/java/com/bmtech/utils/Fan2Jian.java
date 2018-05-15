package com.bmtech.utils;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.bmtech.utils.io.LineReader;
import com.bmtech.utils.log.LogHelper;

public class Fan2Jian {
	LogHelper log = new LogHelper("f2j");
	Map<Character, Character> map = new HashMap<Character, Character>();
	public static final Fan2Jian instance = new Fan2Jian();

	private Fan2Jian() {
		try {
			LineReader lr = new LineReader(ConfUtils.getConfFile("f2j.txt"), Charset.forName("utf-8"));
			while (lr.hasNext()) {
				String line = lr.next();
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				if (line.length() != 3) {
					log.warn("error f2j %s", line);
					continue;
				}
				char ft = line.charAt(0);
				char jt = line.charAt(2);
				if (ft == jt) {
					continue;
				}
				map.put(ft, jt);
			}
			lr.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		log.info("f2j total map size %s", map.size());
	}

	public char convert(char c) {
		Character ret = this.map.get(c);
		if (ret == null) {
			return c;
		}
		return ret;
	}

	public boolean needConvert(String str) {
		if (str == null)
			return false;
		char[] ret = str.toCharArray();
		for (char c : ret) {
			Character cvt = this.map.get(c);
			if (cvt != null) {
				return true;
			}
		}
		return false;
	}

	public String convert(String str) {
		if (str == null)
			return null;
		StringBuilder sb = new StringBuilder();
		char[] ret = str.toCharArray();
		for (char c : ret) {
			sb.append(convert(c));
		}

		return sb.toString();
	}

}
