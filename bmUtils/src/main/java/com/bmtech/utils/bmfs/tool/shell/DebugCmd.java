package com.bmtech.utils.bmfs.tool.shell;

import java.util.Arrays;

public abstract class DebugCmd {
	final private String help;
	MDirShell env;

	protected DebugCmd(String help, MDirShell env) {
		this.help = help;
		this.env = env;
	}

	protected DebugCmd(String[] helps, MDirShell debugger) {
		StringBuilder sb = new StringBuilder();
		for (String str : helps) {
			sb.append(str);
			sb.append("\n");
		}
		this.help = sb.toString();
	}

	public String help() {
		return help;
	}

	public abstract Object run(String paras[]) throws Exception;

	public void expectParasNumber(String[] paras, int num) {
		if (num != paras.length) {
			throw new RuntimeException("expect " + num + " paras, but get " + Arrays.asList(paras));
		}
	}

	protected static String asHelpInfo(String[] helpLines) {
		StringBuilder sb = new StringBuilder();
		for (String x : helpLines) {
			if (sb.length() > 0) {
				sb.append("\n\t\t");
			}
			sb.append(x);
		}
		return sb.toString();
	}

	public static Integer intValue(String str) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return null;
		}
	}

}