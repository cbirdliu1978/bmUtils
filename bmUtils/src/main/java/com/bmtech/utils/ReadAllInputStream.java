package com.bmtech.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class ReadAllInputStream extends InputStream {

	public abstract byte[] readAll() throws IOException;

	public byte[] readAll(ByteArrayOutputStream bos, byte[] buf)
			throws IOException {
		bos.reset();
		while (true) {
			int readed = this.read(buf);
			if (readed == -1) {
				break;
			}
			bos.write(buf, 0, readed);
		}
		byte[] ret = bos.toByteArray();
		return ret;
	}
}
