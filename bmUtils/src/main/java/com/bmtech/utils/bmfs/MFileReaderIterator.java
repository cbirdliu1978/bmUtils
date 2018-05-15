package com.bmtech.utils.bmfs;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.List;

/**
 * like MFileWriter, * NOT thread-safe *
 * 
 * @author liying
 *
 */
public class MFileReaderIterator implements Iterator<MFileReader>, Closeable {
	final MDir mdir;
	final byte[] margin = new byte[MFile.marginLen];
	List<MFile> mfiles;
	private int index = 0;
	RandomAccessFile raf;
	byte buf[] = new byte[16 * 1024];

	MFileReaderIterator(MDir mdir) throws IOException {
		this.mdir = mdir;
		this.mfiles = mdir.getMFiles();
		this.raf = new RandomAccessFile(mdir.getDataFile(), "r");

	}

	@Override
	public boolean hasNext() {
		return index < mfiles.size();
	}

	@Override
	public void remove() {
		throw new RuntimeException("not support remove()");
	}

	/**
	 * 
	 * @throws IOException
	 */
	public void skip() throws IOException {
		index++;
	}

	@Override
	public MFileReader next() {
		try {
			MFile mf = this.mfiles.get(this.index++);
			MFileReader reader = new MFileReader(mf, raf);
			reader.setBuf(buf);
			return reader;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		try {
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
