package com.bmtech.utils.io.diskMerge.gzipbased;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import com.bmtech.utils.io.LineReader;
import com.bmtech.utils.io.diskMerge.MRecord;
import com.bmtech.utils.io.diskMerge.MRecordFileInput;
import com.bmtech.utils.io.diskMerge.PeekableQueue;
import com.bmtech.utils.io.diskMerge.RecordReader;

public class GZIPRecordReader implements RecordReader {
	protected File file;
	Class<? extends MRecord> recordClass;

	public void setReaderFile(File file) throws IOException {
		this.file = file;
	}

	public void setRecordClass(Class<? extends MRecord> recordClass) {
		this.recordClass = recordClass;
	}

	public PeekableQueue toPeekableQueue() throws Exception {
		GZIPInputStream ips = new GZIPInputStream(new FileInputStream(file));
		LineReader lr = new LineReader(ips);
		MRecordFileInput reader = new MRecordFileInput(lr, recordClass);
		return reader;
	}

}
