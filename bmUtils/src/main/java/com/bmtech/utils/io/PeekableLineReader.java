package com.bmtech.utils.io;

import java.io.Closeable;

public class PeekableLineReader implements Closeable {

	private String crt;
	private LineReader lr;
	private int readed = 0;

	public PeekableLineReader(LineReader lr) {
		this.lr = lr;
		this.crt = getNextRecord();
	}

	public synchronized String peek() {
		return crt;
	}

	public synchronized String take() {
		String ret = crt;
		this.crt = getNextRecord();
		readed++;
		return ret;
	}

	private String getNextRecord() {
		if (lr.hasNext()) {
			String crtLine = lr.next();
			if (crtLine == null) {
				return null;
			}

			crt = crtLine;
		} else {
			crt = null;
		}
		return crt;
	}

	@Override
	public synchronized void close() {
		lr.close();
	}

	@Override
	public synchronized void finalize() {
		close();
	}

	public int getReadedRecordNumber() {
		return readed;
	}
}
