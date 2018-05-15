package com.bmtech.utils.io.diskMerge;

import java.io.File;
import java.io.IOException;

public interface RecordWriter {
	void setWriterFile(File file);

	MOut toMOut() throws IOException;
}
