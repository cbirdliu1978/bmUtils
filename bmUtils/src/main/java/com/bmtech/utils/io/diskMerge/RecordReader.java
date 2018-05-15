package com.bmtech.utils.io.diskMerge;

import java.io.File;
import java.io.IOException;

public interface RecordReader {

	public void setReaderFile(File file) throws IOException;

	public void setRecordClass(Class<? extends MRecord> recordClass);

	public PeekableQueue toPeekableQueue() throws Exception;

}
