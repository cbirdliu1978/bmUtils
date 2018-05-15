//package com.bmtech.utils.http.itrCrawl;
//
//import java.io.File;
//import java.io.IOException;
//
//import com.bmtech.utils.io.LineReader;
//
//public abstract class LineGenor implements UrlIterator {
//	final File file2Read;
//	final LineReader lr;
//	protected int readedLine = 0;
//
//	public int readedLine() {
//		return readedLine;
//	}
//
//	public LineGenor(File toRead) throws IOException {
//		this.file2Read = toRead;
//		lr = new LineReader(toRead);
//	}
//
//	public synchronized String nextLine() {
//		if (lr.hasNext()) {
//			readedLine++;
//			return lr.next();
//		}
//		return null;
//	}
//
//	public abstract GenEntry next() throws Exception;
//
//	@Override
//	public void finalize() {
//		this.lr.close();
//	}
// }