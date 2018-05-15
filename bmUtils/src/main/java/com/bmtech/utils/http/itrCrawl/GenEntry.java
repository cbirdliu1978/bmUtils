package com.bmtech.utils.http.itrCrawl;

import java.net.URL;

public class GenEntry {
	public final String word;

	public final URL url;
	public final String storeName;

	public GenEntry(String word, URL url, String storeName) {
		this.word = word;
		this.url = url;
		this.storeName = storeName;
	}

	public GenEntry(URL url, String storeName) {
		this.word = storeName;
		this.url = url;
		this.storeName = storeName;
	}

	@Override
	public String toString() {
		return "GenEntry [storeName=" + storeName + ", url=" + url + ", word="
				+ word + "]";
	}
}