package com.bmtech.datamine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stock {
	public static String formatStockCode(String next) {
		String ret = next.trim().toUpperCase();
		if (ret.length() < 6) {
			throw new RuntimeException("error stock code " + next);
		} else if (ret.length() > 6) {
			ret = ret.substring(0, 6);
		}
		return ret;
	}

	private String code;
	private String name;

	public Stock() {

	}

	public Stock(String code, String name) {
		this.setCode(code);
		this.name = name;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = formatStockCode(code);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return code.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Stock) {
			Stock s = (Stock) obj;
			boolean ret = this.code.equalsIgnoreCase(s.code);
			if (!ret) {
				if (this == AllStock.sh) {
					return AllStock.sh_alia.getCode().equalsIgnoreCase(s.getCode());
				} else if (s == AllStock.sh) {
					return AllStock.sh_alia.getCode().equalsIgnoreCase(this.getCode());
				}
			}
			return ret;
		} else if (obj instanceof String) {
			return equals(AllStock.getStockByCode((String) obj));
		}
		return false;
	}

	@Override
	public String toString() {
		return this.name + " [" + code + "]";
	}

	public boolean isMyCode(String code) {
		return this.getCode().equals(code);
	}

	public boolean is60x() {
		return this.code.startsWith("60");
	}

	public boolean is30x() {
		return this.code.startsWith("30");
	}

	public boolean is00x() {
		return this.code.startsWith("00");
	}

	public static Stock toStockInstance(String code) {
		return AllStock.getStockByCode(code);
	}
}
