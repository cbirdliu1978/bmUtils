package com.bmtech.utils.http;

import java.net.URL;
import java.nio.charset.Charset;

import com.bmtech.utils.KeyValuePair;

public class PostDataBuilder {
	final URLBuilder ub;

	public PostDataBuilder(Charset cs) {
		ub = new URLBuilder((URL) null, cs);
	}

	public void putParam(String key, String value) {
		ub.addPara(key, value);
	}

	public void putParam(KeyValuePair<String, String> pair) {
		ub.addPara(pair);
	}

	public String getPostData() {
		return ub.getParaString();
	}
}
