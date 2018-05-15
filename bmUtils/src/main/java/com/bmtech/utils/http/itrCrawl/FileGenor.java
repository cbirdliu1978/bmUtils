package com.bmtech.utils.http.itrCrawl;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.bmtech.utils.io.LineReader;

public abstract class FileGenor implements UrlIterator {

	AtomicInteger itg = new AtomicInteger();
	protected int fileIndex = 0;
	protected LineReader lr;
	final File[] fs;

	public FileGenor(File file) throws IOException {
		this(new File[] { file });
	}

	public FileGenor(File[] files) throws IOException {
		this.fs = files;
		lr = new LineReader(files[fileIndex]);
	}

	protected abstract GenEntry toEntry(String url) throws IOException;

	@Override
	public GenEntry next() throws Exception {
		if (lr == null) {
			return null;
		}
		if (lr.hasNext()) {

			itg.incrementAndGet();
			String line = lr.next();
			line = line.trim();
			if (line.length() == 0) {
				return next();
			}
			return toEntry(line);
		} else {
			lr.close();
			fileIndex++;
			if (fileIndex < fs.length) {
				lr = new LineReader(fs[fileIndex]);
				return this.next();
			} else {
				lr = null;
				return null;
			}

		}

	}

	@Override
	public int readedLine() {
		return itg.get();
	}

	@Override
	public void finalize() {
		this.lr.close();
	}
}
