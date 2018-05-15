package com.bmtech.utils.io.diskMerge.gzipbased;

import com.bmtech.utils.io.diskMerge.RecordFactoryImpl;

public class GZIPRecordFactory extends RecordFactoryImpl {

	public GZIPRecordFactory() {
		this.setReaderClass(GZIPRecordReader.class);
		this.setWriterClass(GZIPRecordWriter.class);
	}

}
