package com.bmtech.utils.io.diskMerge.linebased;

import java.io.File;
import java.io.IOException;

import com.bmtech.utils.io.LineReader;
import com.bmtech.utils.io.diskMerge.MRecord;
import com.bmtech.utils.io.diskMerge.MRecordFileInput;
import com.bmtech.utils.io.diskMerge.PeekableQueue;
import com.bmtech.utils.io.diskMerge.RecordReader;

public class LineRecordReader implements RecordReader {
	protected LineReader lr;
	Class<? extends MRecord> recordClass;

	public void setReaderFile(File file) throws IOException {
		lr = new LineReader(file);

	}

	public void setRecordClass(Class<? extends MRecord> recordClass) {
		this.recordClass = recordClass;
	}

	public PeekableQueue toPeekableQueue() throws Exception {
		MRecordFileInput reader = new MRecordFileInput(lr, recordClass);
		return reader;
	}

}
