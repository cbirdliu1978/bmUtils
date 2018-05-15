package com.bmtech.utils.bmfs;

import java.io.IOException;

public abstract class MdirTraverser implements Runnable {
	MDir dir;

	public MdirTraverser(MDir dir) {
		this.dir = dir;
	}

	public abstract boolean forOject(MFile mfile, byte[] bs) throws Exception, IOException;

	public void run() {
		MFileReaderIterator itr = null;
		try {
			try {
				itr = dir.openReader();
				while (itr.hasNext()) {

					try {
						MFileReader reader = itr.next();
						MFile mf = reader.getMfile();

						byte[] bs;
						bs = reader.getBytes();

						forOject(mf, bs);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			} finally {
				if (itr != null) {
					itr.close();
				}
			}
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}

	}
}