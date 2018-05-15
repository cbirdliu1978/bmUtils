package com.bmtech.utils;

import java.io.File;

public class ConfUtils {
	/**
	 * 
	 * @param path the relative path to config, for example: videoLet.conf
	 * @return
	 */
	public static File getConfFile(String path){
		String str = System.getProperty("$fs_isn_config_path");
		if(str == null){
			str = "./config";
		}
		File dir = new File(str);
		return new File(dir, path);
	}
}
