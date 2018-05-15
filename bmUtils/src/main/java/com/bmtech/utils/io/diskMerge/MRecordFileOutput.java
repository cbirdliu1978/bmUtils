package com.bmtech.utils.io.diskMerge;

import java.io.File;
import java.io.IOException;

import com.bmtech.utils.Misc;
import com.bmtech.utils.io.LineWriter;

public class MRecordFileOutput implements MOut {
	private LineWriter lw;

	public MRecordFileOutput(File file) throws IOException {
		// if (file.exists()) {
		// throw new RuntimeException("file already exists " + file);
		// }
		Misc.besureFileExists(file);
		lw = new LineWriter(file, false);
	}

	public MRecordFileOutput(LineWriter lw) throws IOException {
		this.lw = lw;
	}

	@Override
	public synchronized void flush(MRecord mm) throws IOException {
		offer(mm);
		flush();
	}

	@Override
	public synchronized void offer(MRecord mm) throws IOException {
		lw.writeLine(mm.serialize());
	}

	@Override
	public synchronized void flush() throws IOException {
		lw.flush();
	}

	@Override
	public synchronized void close() {
		if (lw != null) {
			lw.close();
			lw = null;
		}
	}

	@Override
	public synchronized void finalize() {
		this.close();
	}
}
