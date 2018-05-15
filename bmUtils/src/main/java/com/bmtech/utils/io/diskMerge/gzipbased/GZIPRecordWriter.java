package com.bmtech.utils.io.diskMerge.gzipbased;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import com.bmtech.utils.Misc;
import com.bmtech.utils.io.LineWriter;
import com.bmtech.utils.io.diskMerge.MOut;
import com.bmtech.utils.io.diskMerge.MRecordFileOutput;
import com.bmtech.utils.io.diskMerge.RecordWriter;

public class GZIPRecordWriter implements RecordWriter {
	File file;

	@Override
	public void setWriterFile(File file) {
		this.file = file;
	}

	public MOut toMOut() throws IOException {
		Misc.besureFileExists(file);
		GZIPOutputStream ops = new GZIPOutputStream(new FileOutputStream(file));
		LineWriter lw = new LineWriter(ops);
		return new MRecordFileOutput(lw);
	}

}
