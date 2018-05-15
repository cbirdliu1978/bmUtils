package com.bmtech.utils.bmfs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class MFileWriter {
	final MFileOutputStream out = new MFileOutputStream();
	MDir dir;

	MFileWriter(MDir dir) {
		this.dir = dir;
	}

	public MFileOutputStream openMFileGzip(MFile mfile) throws IOException {
		return openMFile(mfile, true);
	}

	public MFileOutputStream openMFile(MFile mfile) throws IOException {
		return openMFile(mfile, false);
	}

	public MFileOutputStream openMFile(MFile mfile, boolean gzip) throws IOException {
		out.openMFile(mfile, gzip);
		return out;
	}

	public class MFileOutputStream extends OutputStream {
		MFile mfile;
		ByteArrayOutputStream bosx = new ByteArrayOutputStream();
		OutputStream out;
		byte[] buf = new byte[4096];

		void openMFile(MFile mfile, boolean useGzip) throws IOException {
			this.mfile = mfile;
			if (dir.isMount(mfile)) {
				throw new IOException("can not write to mounted mfile " + mfile);
			}
			bosx.reset();
			if (useGzip) {
				out = new GZIPOutputStream(bosx);
			} else {
				out = bosx;
			}
		}

		@Override
		public void write(byte b[]) throws IOException {
			out.write(b);
		}

		@Override
		public void write(byte b[], int off, int len) throws IOException {
			out.write(b, off, len);
		}

		@Override
		public void write(int b) throws IOException {
			out.write(b);
		}

		public OutputStream write(InputStream ips) throws IOException {
			while (true) {
				int x = ips.read(buf);
				if (x == -1) {
					break;
				}
				this.out.write(buf, 0, x);
			}
			out.flush();
			return out;
		}

		/**
		 * if not close, mfile will not save to mdir
		 */

		public void mountAndClose() throws IOException {
			close();
		}

		@Override
		public void close() throws IOException {
			out.close();
			byte[] bts = bosx.toByteArray();
			dir.mount(mfile, new ByteArrayInputStream(bts));
		}

		public void abort() {
			if (this.mfile != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				dir.abort(mfile);
			}
		}

	}

}