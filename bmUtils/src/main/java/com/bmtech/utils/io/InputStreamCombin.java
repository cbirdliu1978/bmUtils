package com.bmtech.utils.io;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamCombin extends InputStream {

	InputStream ips[];
	int crtIndex = 0;

	public InputStreamCombin(InputStream... ips) {
		this.ips = ips;
	}

	@Override
	public void close() {
		for (InputStream ip : ips) {
			try {
				ip.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int read() throws IOException {
		int ret = ips[crtIndex].read();
		if (ret == -1) {
			if (crtIndex + 1 < ips.length) {
				crtIndex++;
				return read();
			} else {
				return -1;
			}
		} else {
			return ret;
		}
	}

}
