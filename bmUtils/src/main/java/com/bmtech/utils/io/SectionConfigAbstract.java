package com.bmtech.utils.io;

import com.bmtech.utils.ConfigureableReader;

/**
 * TC is a basic tool to read config file the configure file should constructed as #---- notice,lines starting with '#' will be ingnored
 * key=value <b>key-value pair</b>
 * 
 * NOTICE:all of the line will be trimed at first NOTICE:the line contains no '='symbol will be ingored NOTICE:if no appender after '=' the
 * value will be viewed as 0-length String NOTICE:if no prifix before '=', the line will be viewed as illegal and ignored
 * 
 * @author beiming
 *
 */
public abstract class SectionConfigAbstract implements ConfigureableReader {

	protected SectionConfigAbstract() {
	}

	/**
	 * this method get the value by walking throgh the list. when finding one of the matched pair,this method will return, no matter how
	 * many pairs will match becouse efficiency is low,and TC may not give all of the good results
	 * 
	 * @param key
	 *            ,get the specificated key's value pair
	 * @return
	 */
	@Override
	public String getValue(String key) {
		return getValue(key, null);
	}

	@Override
	public abstract String getValue(String key, String defaultValue);

	/**
	 * if null or not good format return 0
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public int getInt(String key) {
		return getInt(key, 0);
	}

	@Override
	public double getDouble(String key) {
		return getDouble(key, 0);
	}

	@Override
	public float getFloat(String key) {
		return getFloat(key, 0);
	}

	@Override
	public float getFloat(String key, float defalutValue) {
		String v = this.getValue(key);
		if (v == null)
			return defalutValue;
		try {
			return Float.parseFloat(v);
		} catch (Exception e) {
			return defalutValue;
		}
	}

	@Override
	public double getDouble(String key, double defalutValue) {
		String v = this.getValue(key);
		if (v == null)
			return defalutValue;
		try {
			return Double.parseDouble(v);
		} catch (Exception e) {
			return defalutValue;
		}
	}

	@Override
	public int getInt(String key, int defalutValue) {
		String v = this.getValue(key);
		if (v == null)
			return defalutValue;
		try {
			return Integer.parseInt(v);
		} catch (Exception e) {
			return defalutValue;
		}
	}

	@Override
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue) {
		String value = this.getValue(key);

		Boolean isTrue = ConfigureableReader.isTrueValue(value);
		if (isTrue == null)
			return defaultValue;
		return isTrue;
	}
}
