package com.bmtech.utils;

public class StringTokenItr {
	final String tokens[];
	int cursor = -1;

	public StringTokenItr(String line) {
		this(line, "\t");
	}

	public StringTokenItr(String line, String split) {
		tokens = line.split(split);
	}

	public StringTokenItr(String tokens[]) {
		this.tokens = tokens;
	}

	public boolean hasNext() {
		return cursor + 1 < tokens.length;
	}

	private String nextInner() {
		cursor++;
		return tokens[cursor].trim();
	}

	public int nextInt() {
		return Integer.parseInt(nextInner());
	}

	public double nextDouble() {
		return Double.parseDouble(nextInner());
	}

	public String nextString() {
		return new String(this.nextInner().getBytes());
	}

	public double nextDoublePercent() {
		String next = nextInner();
		if (next.endsWith("%")) {
			next = next.substring(0, next.length() - 1).trim();
		}
		return Misc.round(Double.parseDouble(next) / 100, 10000);
	}

	public int getInt(int index) {
		return Integer.parseInt(getInner(index));
	}

	public double getDouble(int index) {
		return Double.parseDouble(getInner(index));
	}

	private String getInner(int index) {
		return tokens[index].trim();
	}

	public String getString(int index) {
		return new String(this.getInner(index).getBytes());
	}

	public double getDoublePercent(int index) {
		String next = getInner(index);
		if (next.endsWith("%")) {
			next = next.substring(0, next.length() - 1).trim();
		}
		return Misc.round(Double.parseDouble(next) / 100, 10000);
	}

	public int size() {
		return this.tokens.length;
	}

	public long nextLong() {
		return Long.parseLong(nextInner());
	}

}