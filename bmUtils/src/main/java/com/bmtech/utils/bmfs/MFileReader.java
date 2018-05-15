package com.bmtech.utils.bmfs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

import com.bmtech.utils.ReadAllInputStream;
import com.bmtech.utils.ZipUnzip;
import com.bmtech.utils.bmfs.util.MFileFormatErrorException;

/**
 * like MFileWriter, * NOT thread-safe *
 * 
 * @author liying
 *
 */
public class MFileReader {
	private ByteArrayOutputStream bos = new ByteArrayOutputStream();
	private byte[] buf;
	final byte[] margin = new byte[MFile.marginLen];
	private final MFile mfile;
	final RandomAccessFile raf;
	boolean openByMe = true;;

	MFileReader(MFile mfile) throws IOException {
		this(mfile, null);
	}

	MFileReader(MFile mfile, RandomAccessFile rafs) throws IOException {
		if (rafs == null) {
			this.raf = new RandomAccessFile(mfile.dir.getDataFile(), "r");
		} else {
			this.raf = rafs;
			openByMe = false;
		}
		this.mfile = mfile;
		raf.seek(mfile.getOffset());
		raf.readFully(margin, 0, margin.length);
		parseMargin();
		int fsId = raf.readInt();
		if (fsId != this.mfile.fsId) {
			throw new IOException(String.format("Strange! fsid not match! expect %s, but get %s for mfile %s",
					getMfile().fsId, fsId, getMfile()));
		}
	}

	@Override
	public void finalize() {
		this.close();
	}

	public void close() {
		if (openByMe) {
			try {
				raf.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void parseMargin() throws IOException {
		int fsId = getMfile().fsId;
		if (margin[14] != (byte) (fsId & 0xff) || margin[15] != (byte) (fsId >> 4)) {

			throw new IOException(String.format(
					"MDir format Error, margin digest mismatch. Becouse fsid not match! expect %s, but get %s for mfile %s",
					getMfile().fsId, fsId, getMfile()));
		}
	}

	public ReadAllInputStream getInputStream() throws IOException {
		return getInputStream(false);
	}

	public ReadAllInputStream getInputStreamUnGZiped() throws IOException {
		return getInputStream(true);
	}

	public String getString(boolean unGzip) throws IOException {
		return getString(unGzip, null);
	}

	public String getString() throws IOException {
		return getString(false, null);
	}

	public String getString(Charset cs) throws IOException {
		return getString(false, cs);
	}

	public String getString(boolean unGzip, Charset cs) throws IOException {
		byte[] bs = this.getBytes(unGzip);
		if (cs == null) {
			cs = Charset.defaultCharset();
		}
		return new String(bs, cs);
	}

	public byte[] getBytes() throws IOException {
		ReadAllInputStream ips = getInputStream();
		return ips.readAll();
	}

	public byte[] getBytesUnGZiped() throws IOException {
		// ReadAllInputStream ips = getInputStreamUnGZiped();
		byte[] ret = this.getBytes();
		return ZipUnzip.unGzip(ret);
	}

	class GZipReadAllInputStream extends ReadAllInputStream {
		final GZIPInputStream ips;

		GZipReadAllInputStream(InputStream ips) throws IOException {
			this.ips = new GZIPInputStream(ips, 4096);
		}

		@Override
		public byte[] readAll() throws IOException {
			if (buf == null) {
				setBuf(new byte[1024 * 8]);
			}
			bos.reset();
			return this.readAll(bos, buf);
		}

		@Override
		public int read() throws IOException {
			return ips.read();
		}

		@Override
		public void close() throws IOException {
			this.ips.close();
		}
	}

	private class MFileInputStream extends ReadAllInputStream {
		int readed = 0;
		final long fileLen;

		MFileInputStream(long fileLen) {
			this.fileLen = fileLen;
		}

		@Override
		public int read() throws IOException {
			if (readed >= fileLen) {
				return -1;
			}
			int ret = raf.read();// .readByte();
			if (ret == -1) {
				throw new MFileFormatErrorException("corrupt mdir " + getMfile());
			}
			readed++;
			return ret;
		}

		@Override
		public int read(byte[] buf) throws IOException {
			return read(buf, 0, buf.length);
		}

		@Override
		public int read(byte[] buf, int offset, int len) throws IOException {
			long left = fileLen - readed;
			if (left < 1) {
				return -1;
			}
			int canRead = buf.length - offset;
			if (canRead > left) {
				len = (int) left;
			}

			int ret = raf.read(buf, offset, len);
			if (ret == -1) {
				throw new MFileFormatErrorException("premature stream! should left " + left + ", require " + len);
			}
			readed += ret;
			return ret;
		}

		@Override
		public byte[] readAll() throws IOException {
			if (buf == null) {
				setBuf(new byte[1024 * 8]);
			}
			return this.readAll(bos, buf);
		}

		@Override
		public void close() {
		}
	}

	private ReadAllInputStream getInputStream(boolean unGzip) throws IOException {

		final long fileLen = getMfile().getLength();
		ReadAllInputStream ips = new MFileInputStream(fileLen);
		if (unGzip) {
			return new GZipReadAllInputStream(ips);
		} else {
			return ips;
		}
	}

	void setBuf(byte[] buf) {
		this.buf = buf;
	}

	public MFile getMfile() {
		return mfile;
	}

	public byte[] getBytes(boolean unGzip) throws IOException {
		byte[] bs;
		if (unGzip) {
			bs = this.getBytesUnGZiped();
		} else {
			bs = this.getBytes();
		}
		return bs;
	}
}
