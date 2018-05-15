package com.bmtech.utils.io.diskMerge.linebased;

import com.bmtech.utils.io.diskMerge.RecordFactoryImpl;
import com.bmtech.utils.io.diskMerge.TabSplitMRecord;

public class LineRecordFactory extends RecordFactoryImpl {

	public LineRecordFactory() {
		this.setReaderClass(LineRecordReader.class);
		this.setWriterClass(LineRecordWriter.class);
		this.setRecordClass(TabSplitMRecord.class);
		this.setComparator(TabSplitMRecord.comparator);
	}
}
