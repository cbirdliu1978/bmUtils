package com.bmtech.utils.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import com.bmtech.utils.Charsets;

public class LineReader extends BufferedReader implements Iterator<String> {
	Charset cs = Charset.defaultCharset();
	private int pos = -1;

	String line;
	boolean first = true;

	public LineReader(String file) throws IOException {
		this(new File(file));
	}

	public LineReader(InputStream ips) throws IOException {
		super(new InputStreamReader(ips));
	}

	public LineReader(File file) throws IOException {
		this(file, Charset.defaultCharset());
	}

	public LineReader(String file, String encoder) throws IOException {
		this(new File(file), encoder);
	}

	public LineReader(String file, Charset encoder) throws IOException {
		this(new File(file), encoder);
	}

	static InputStream getFileInputStream(File file) throws IOException, IOException {
		InputStream ips;
		if (file.getName().endsWith(".gz")) {
			ips = new GZIPInputStream(new FileInputStream(file), 1024 * 64);
		} else {
			ips = new FileInputStream(file);
		}
		return ips;
	}

	public LineReader(File file, Charset encoder) throws IOException {
		super(new InputStreamReader(getFileInputStream(file), encoder));
		this.cs = encoder;
	}

	public LineReader(File file, String encoder) throws IOException {
		this(file, Charset.forName(encoder));
	}

	@Override
	public synchronized String readLine() throws IOException {
		String s = super.readLine();
		if (first) {
			if (s != null) {
				if (this.cs.equals(Charsets.UTF8_CS)) {
					if (s.length() > 0) {
						int c = s.charAt(0);
						if (c == 0xfeff) {
							s = s.substring(1);
						}
					}
				}
			}
			first = false;
		}
		return s;
	}

	@Override
	public boolean hasNext() {
		try {
			line = this.readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		pos++;
		if (line == null)
			return false;
		return true;
	}

	public String peer() {
		return line;
	}

	@Override
	public String next() {
		return line;
	}

	@Override
	public void remove() {
		this.line = null;
	}

	/**
	 * test if this line start with a invalidate prefix
	 * 
	 * @param line
	 * @return true if this line is not forbiddened
	 */

	@Override
	public void finalize() {
		this.close();
	}

	@Override
	public void close() {
		try {
			super.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int currentLineNumber() {
		return pos;
	}

	@Override
	public String toString() {
		return "LineReader:" + super.toString();
	}
}
