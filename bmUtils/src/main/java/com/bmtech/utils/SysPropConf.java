package com.bmtech.utils;

import com.bmtech.utils.io.SectionConfigAbstract;

public class SysPropConf extends SectionConfigAbstract {
	public static SysPropConf instance = new SysPropConf();

	@Override
	public String getValue(String key, String defaultValue) {

		if (key == null)
			return defaultValue;
		String value = System.getProperty(key.trim());
		return value == null ? defaultValue : value;

	}

	public static SysPropConf getInstance() {
		return instance;
	}

}
