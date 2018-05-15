package com.bmtech.utils.io.diskMerge;

import java.io.File;
import java.util.Comparator;

public interface RecordFactory {

	Comparator<MRecord> getComparator();

	PeekableQueue getReader(File file) throws Exception;

	MOut getWriter(File toFile) throws Exception;

}
