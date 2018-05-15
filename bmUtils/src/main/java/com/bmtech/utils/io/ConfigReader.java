package com.bmtech.utils.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bmtech.utils.KeyValuePair;

/**
 * TC is a basic tool to read config file the configure file should constructed
 * as #---- notice,lines starting with '#' will be ingnored key=value
 * <b>key-value pair</b>
 * 
 * NOTICE:all of the line will be trimed at first NOTICE:the line contains no
 * '='symbol will be ingored NOTICE:if no appender after '=' the value will be
 * viewed as 0-length String NOTICE:if no prifix before '=', the line will be
 * viewed as illegal and ignored
 * 
 * @author beiming
 *
 */
public class ConfigReader extends SectionConfig {
	public final String encode;
	public final File configFile;
	public final String sectionName;
	static final String encoder = "utf8";

	// public ConfigReader(String path,String section){
	// this(new File(path), section, encoder);
	// }
	public ConfigReader(File path, String section) {
		this(path, section, encoder);
	}

	public ConfigReader(String path, String section, String encode)
			throws IOException {
		this(new File(path), section, encode);
	}

	public ConfigReader(File path, String section, String encode) {
		super(section, readAll(path, section, encode));
		this.configFile = path;
		this.encode = encode;
		this.sectionName = section;

	}

	public ConfigReader(String string, String section) {
		this(new File(string), section);
	}

	/**
	 * get all <key = value> pair from a special section. This method is a step
	 * method and need not to close the reader becouse in this method we has
	 * already close the reader
	 * 
	 * @return never null
	 */
	public static Map<String, String> readAll(File configFile, String section,
			String encode) {
		boolean hasConfigSection = false;
		try {

			LineReader reader = new LineReader(configFile, encode);
			Map<String, String> ret = new HashMap<String, String>();
			boolean enter = false;
			section = "[" + section + "]";
			while (true) {
				String str = reader.readLine();

				if (str == null)
					break;
				str = str.trim();
				if (enter) {
					if (str.startsWith("[")) {// in enter,but new section
												// started,
						// so we break;
						break;
					}
				} else {
					if (str.compareToIgnoreCase(section) == 0) {
						enter = true;
						hasConfigSection = true;
						continue;
					} else {
						continue;// short cut if not enter the section
					}
				}

				KeyValuePair<String, String> kv = KeyValuePair.parseLine(str);
				if (kv == null)// parse failure,note line or illegal line
					continue;// ignore the line
				ret.put(kv.key, kv.value);

			}
			reader.close();
			if (!hasConfigSection) {
				throw new Exception("section '" + section
						+ "' not found in file " + configFile);
			}
			return ret;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public List<String> listSectionsInConfigFile() throws IOException {
		return listSectionsInConfigFile(this.configFile);
	}

	public static List<String> listSectionsInConfigFile(File f)
			throws IOException {
		if (!f.exists()) {
			throw new FileNotFoundException(f.getAbsolutePath());
		}
		LineReader lr = new LineReader(f);
		List<String> set = new ArrayList<String>();
		while (lr.hasNext()) {
			String line = lr.next().trim();
			if (line.startsWith("[") && line.endsWith("]")) {
				line = line.substring(1, line.length() - 1);
				set.add(line);
			}
		}
		lr.close();
		return set;
	}
}
