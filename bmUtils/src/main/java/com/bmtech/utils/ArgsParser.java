package com.bmtech.utils;

import java.util.Arrays;

public class ArgsParser {
	private final String[] args;

	public ArgsParser(String cmds) {
		this(cmds.replaceAll("\\s+", " ").split(" "));
	}

	public ArgsParser(String[] args) {
		this.args = args;
	}

	public String getArg(int index) {
		if (index >= args.length)
			return null;
		return args[index];
	}

	protected Integer parseInt(String str) {
		if (str != null) {
			try {
				return Integer.parseInt(str);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public Integer getIntArg(int index) {
		String ret = this.getArg(index);
		return parseInt(ret);
	}

	public int argNumber() {
		return this.args.length;
	}

	public String getArgWithPrefix(String prefix) {
		for (String x : this.args) {
			if (x.startsWith(prefix.trim())) {
				return x.substring(prefix.length());
			}
		}
		return null;
	}

	public Integer getIntArgWithPrefix(String prefix) {
		return this.parseInt(this.getArgWithPrefix(prefix));
	}

	public boolean isArgSet(String str) {
		for (String x : args) {
			if (x.equals(str)) {
				return true;
			}
		}
		return false;
	}

	public boolean isArgSetWithPrefix(String prefix) {
		return this.getArgWithPrefix(prefix) != null;
	}

	@Override
	public String toString() {
		return "ArgsParser [args=" + Arrays.toString(args) + "]";
	}
}
