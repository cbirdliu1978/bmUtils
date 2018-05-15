package com.bmtech.utils;

public interface ConfigureableReader {

	public String getValue(String key);

	public String getValue(String key, String defaultValue);

	public boolean getBoolean(String key);

	public boolean getBoolean(String key, boolean defaultValue);

	public double getDouble(String key);

	public double getDouble(String key, double defaultValue);

	public float getFloat(String key);

	public float getFloat(String key, float defaultValue);

	public int getInt(String key);

	public int getInt(String key, int defaultValue);

	public static Boolean isTrueValue(String str) {
		if (null == str)
			return null;
		if (str.length() == 1) {
			if (str.equalsIgnoreCase("y") || str.equalsIgnoreCase("t") || str.equalsIgnoreCase("1"))
				return true;
			else if (str.equalsIgnoreCase("n") || str.equalsIgnoreCase("f") || str.equalsIgnoreCase("0")) {
				return false;
			}
		} else {
			if (str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("true"))
				return true;
			else if (str.equalsIgnoreCase("no") || str.equalsIgnoreCase("false")) {
				return false;
			}
		}
		return null;
	}
}