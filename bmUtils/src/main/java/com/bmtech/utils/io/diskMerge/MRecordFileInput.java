package com.bmtech.utils.io.diskMerge;

import java.io.File;
import java.nio.charset.Charset;

import com.bmtech.utils.io.LineReader;
import com.bmtech.utils.log.LogHelper;

public class MRecordFileInput implements PeekableQueue {

	private MRecord crt;
	private LineReader lr;
	private int readed = 0;
	Class<? extends MRecord> mOutClass;
	LogHelper log = new LogHelper("recordReader");

	public MRecordFileInput(LineReader lr, Class<? extends MRecord> mOutClass) throws Exception {
		this.lr = lr;
		this.mOutClass = mOutClass;
		this.crt = getNextRecord();
	}

	public MRecordFileInput(File file, Class<? extends MRecord> moutClass) throws Exception {
		this(new LineReader(file), moutClass);
	}

	public MRecordFileInput(File file, Charset cs, Class<? extends MRecord> moutClass) throws Exception {
		this(new LineReader(file, cs), moutClass);
	}

	@Override
	public synchronized MRecord peek() {
		return crt;
	}

	@Override
	public synchronized MRecord take() throws Exception {
		MRecord ret = crt;
		this.crt = getNextRecord();
		readed++;
		return ret;
	}

	private MRecord getNextRecord() {
		MRecord crt = null;
		while (lr.hasNext()) {
			String crtLine = lr.next();
			if (crtLine == null) {
				return null;
			}

			try {
				crt = mOutClass.newInstance();
				crt.init(crtLine);
				break;

			} catch (Exception e) {
				log.error(e, "when init %s from str '%s'", mOutClass, crtLine);
			}
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

	@Override
	public int getReadedRecordNumber() {
		return readed;
	}
}
