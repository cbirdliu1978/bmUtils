package com.bmtech.utils.http.itrCrawl;

public interface UrlIterator {

	GenEntry next() throws Exception;

	int readedLine();

}