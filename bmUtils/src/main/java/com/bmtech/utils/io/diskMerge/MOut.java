package com.bmtech.utils.io.diskMerge;

import java.io.IOException;

public interface MOut {

	public abstract void flush(MRecord mm) throws IOException;

	public abstract void offer(MRecord mm) throws IOException;

	public abstract void flush() throws IOException;

	public abstract void close();

}
