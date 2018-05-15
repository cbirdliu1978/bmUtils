package com.bmtech.utils;

import java.io.IOException;
import java.util.Arrays;

import jline.console.UserInterruptException;

public class Consoler implements ConfigureableReader {
	public static boolean useJLine = true;
	static {
		if (Misc.isWinSys()) {
			useJLine = false;
		}
	}

	/**
	 * read a int from console, if the format is not correctly,continue, if no input or only Enter/whitspace entered,return defaultValue
	 * 
	 * @param input
	 * @param default
	 *            is nothing is inputted, use this as the return
	 * @return
	 */
	public static int readInt(String input, int defaultValue) {
		while (true) {
			hint(input, defaultValue);

			try {
				String str = readLine("");
				if (str.length() == 0)
					return defaultValue;
				return Integer.parseInt(str);
			} catch (Exception e) {
			}
		}
	}

	public static double readDouble(String input, double defaultValue) {
		while (true) {
			hint(input, defaultValue);

			try {
				String str = readLine("");
				if (str.length() == 0)
					return defaultValue;
				return Double.parseDouble(str);
			} catch (Exception e) {
			}
		}
	}

	public static int readInt(String prmpt) {
		return readInt(prmpt, 0);
	}

	public static int readInt(int defaultValue) {
		return readInt("input a int ", defaultValue);
	}

	public static String readLine(String prmpt) {
		return readString(prmpt == null ? "" : prmpt);
	}

	public static String readLine(String prmpt, String dftValue) {
		return readString(prmpt == null ? "" : prmpt, dftValue);
	}

	public static void pause() {
		pause("press Enter key to continue...");
	}

	public static void pause(String prmt) {
		readString(prmt);
	}

	public static String readString(String input) {
		int numUserInterruptException = 0;
		while (true) {
			hint(input, null);
			if (!useJLine) {
				byte[] bs = new byte[1024 * 128];
				try {
					System.in.read(bs);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				return new String(bs).trim();
			} else {
				boolean isUserInterruptException = false;
				try {
					String line = ConsolerLineReader.getInstance().readLine().trim();

					if (line.length() > 0)
						ConsolerLineReader.getInstance().getHistory().add(line);
					return line;
				} catch (UserInterruptException e) {
					// e.printStackTrace();
					isUserInterruptException = true;
				} catch (IOException e) {
					throw new RuntimeException(e);
				} finally {
					if (isUserInterruptException) {
						numUserInterruptException++;
						if (numUserInterruptException > 2) {
							System.err.println("got triple UserInterruptException! System.exit(0) called");
							System.err.flush();
							System.exit(0);
						}
					} else {
						numUserInterruptException = 0;
					}
				}
			}
		}
	}

	public static String readString(String input, String dft) {
		String str = readString(dft == null ? input : input + "(default '" + dft + "') ");
		if (str.length() == 0) {
			return dft;
		}
		return str;
	}

	public static boolean confirm(String input) {
		return confirm(input, null);
	}

	private static void hint(String hint, Object dftValue) {
		if (hint == null)
			hint = "";
		hint = dftValue == null ? hint : hint + "(default '" + dftValue + "') ";
		System.out.print(hint);
		if (hint.length() > 0 && !hint.endsWith("> "))
			System.out.print("> ");
	}

	public static boolean confirm(String input, Boolean dft) {
		while (true) {
			hint(input, dft);
			String str = readLine("").trim();
			if (str.length() == 0 && dft != null) {
				return dft;
			} else {
				Boolean acc = ConfigureableReader.isTrueValue(str);
				if (acc != null)
					return acc;

			}
		}
	}

	public static void println() {
		println("");
	}

	public static void print(Object str) {
		if (!useJLine) {
			System.out.print(str);
		} else {
			try {
				ConsolerLineReader.getInstance().print(str == null ? "null" : str.toString());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void println(String pattern, Object... paras) {
		String xx = null;
		try {
			xx = String.format(pattern, paras);
		} catch (Exception e) {
			e.printStackTrace();
			xx = String.format("%s  %s", pattern, paras == null ? "" : Arrays.asList(paras));
		}
		if (!useJLine) {
			System.out.println(xx);
		} else {
			try {
				ConsolerLineReader.getInstance().println(xx);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getValue(String key) {
		return getValue(key, null);
	}

	@Override
	public String getValue(String key, String defaultValue) {
		return readLine(key, defaultValue);
	}

	@Override
	public boolean getBoolean(String key) {
		return confirm(key);
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue) {
		return confirm(key, defaultValue);
	}

	@Override
	public double getDouble(String key) {
		return getDouble(key, 0);
	}

	@Override
	public double getDouble(String key, double defaultValue) {
		String value = readLine(key, defaultValue + "");
		try {
			return Double.parseDouble(value);
		} catch (Exception e) {
			return getDouble(key, defaultValue);
		}
	}

	@Override
	public float getFloat(String key) {
		return getFloat(key, 0);
	}

	@Override
	public float getFloat(String key, float defaultValue) {
		String value = readLine(key, defaultValue + "");
		try {
			return Float.parseFloat(value);
		} catch (Exception e) {
			return getFloat(key, defaultValue);
		}
	}

	@Override
	public int getInt(String key) {
		return readInt(key, 0);
	}

	@Override
	public int getInt(String key, int defaultValue) {
		return readInt(key, defaultValue);
	}

	public static void block() {
		while (true) {
			boolean quit = Consoler.confirm("is blocked, quit?");
			if (quit) {
				break;
			}
		}

	}

}
