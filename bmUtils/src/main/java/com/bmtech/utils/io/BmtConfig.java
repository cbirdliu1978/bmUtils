package com.bmtech.utils.io;

import java.io.IOException;

import com.bmtech.utils.ConfigureableReader;
import com.bmtech.utils.Consoler;
import com.bmtech.utils.SysPropConf;

public class BmtConfig {
	public static ConfigureableReader getConfig() throws IOException {
		String section = System.getProperty("conf.from");
		if (section == null) {
			return new Consoler();
		} else if (section.equalsIgnoreCase("sys.prop")) {
			return new SysPropConf();
		} else if (section.equalsIgnoreCase("file")) {
			return new ConfigReader("conf/bmtConfig.conf", "main");
		} else {
			throw new IOException("not defined config" + section);
		}
	}

}
