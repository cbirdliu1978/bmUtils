package com.bmtech.utils.io.diskMerge;

import java.io.File;
import java.util.Comparator;

public abstract class RecordFactoryImpl implements RecordFactory {
	private Comparator<MRecord> comparator;
	private Class<? extends MRecord> recordClass;
	private Class<? extends RecordReader> readerClass;
	private Class<? extends RecordWriter> writerClass;

	protected Class<? extends MRecord> getRecordClass() {
		return recordClass;
	}

	@Override
	public PeekableQueue getReader(File file) throws Exception {
		RecordReader reader = getReaderClass().newInstance();
		reader.setReaderFile(file);
		reader.setRecordClass(getRecordClass());
		return reader.toPeekableQueue();
	}

	@Override
	public MOut getWriter(File file) throws Exception {
		RecordWriter writer = getWriterClass().newInstance();
		writer.setWriterFile(file);
		return writer.toMOut();
	}

	public Comparator<MRecord> getComparator() {
		return comparator;
	}

	public void setComparator(Comparator<MRecord> comparator) {
		this.comparator = comparator;
	}

	public void setRecordClass(Class<? extends MRecord> recordClass) {
		this.recordClass = recordClass;
	}

	public Class<? extends RecordReader> getReaderClass() {
		return readerClass;
	}

	public void setReaderClass(Class<? extends RecordReader> readerClass) {
		this.readerClass = readerClass;
	}

	public Class<? extends RecordWriter> getWriterClass() {
		return writerClass;
	}

	public void setWriterClass(Class<? extends RecordWriter> writerClass) {
		this.writerClass = writerClass;
	}

}
