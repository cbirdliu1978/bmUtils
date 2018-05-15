package com.bmtech.utils.io.diskMerge.linebased;

import java.io.File;
import java.io.IOException;

import com.bmtech.utils.Misc;
import com.bmtech.utils.io.diskMerge.MOut;
import com.bmtech.utils.io.diskMerge.MRecordFileOutput;
import com.bmtech.utils.io.diskMerge.RecordWriter;

public class LineRecordWriter implements RecordWriter {
	File file;

	@Override
	public void setWriterFile(File file) {
		this.file = file;
	}

	public MOut toMOut() throws IOException {
		Misc.besureFileExists(file);
		return new MRecordFileOutput(file);
	}

}
