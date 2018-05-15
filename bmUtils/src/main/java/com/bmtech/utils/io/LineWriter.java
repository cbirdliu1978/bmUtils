package com.bmtech.utils.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;

import com.bmtech.utils.Misc;

public class LineWriter extends BufferedWriter {
	static OutputStream getFileOutput(File f, boolean append) throws IOException {
		if (f.getName().endsWith(".gz")) {
			return new GZIPOutputStream(new FileOutputStream(f, append), 64 * 1024);
		} else {
			return new FileOutputStream(f, append);
		}
	}

	public LineWriter(File file, boolean append, Charset cs) throws IOException {
		super(new OutputStreamWriter(getFileOutput(Misc.besureFileExists(file), append), cs));
	}

	private boolean isWin = false;

	public LineWriter(File file, boolean append) throws IOException {
		this(file, append, Charset.defaultCharset());
	}

	public LineWriter(File file) throws IOException {
		this(file, false);
	}

	public LineWriter(Writer writer) throws IOException {
		super(writer);
		try {
			Properties prop = System.getProperties();

			String os = prop.getProperty("os.name");
			if (os != null) {
				isWin = os.toLowerCase().startsWith("win");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LineWriter(String file, boolean append) throws IOException {
		this(new File(file), append);
	}

	public LineWriter(String file) throws IOException {
		this(new File(file), false);
	}

	public LineWriter(OutputStream ops) throws IOException {
		this(new OutputStreamWriter(ops));
	}

	public LineWriter(String file, Charset cs) throws IOException {
		this(new File(file), false, cs);
	}

	public LineWriter(String file, boolean append, Charset cs) throws IOException {
		this(new File(file), append, cs);
	}

	public LineWriter(String file, Charset cs, boolean append) throws IOException {
		this(new File(file), append, cs);
	}

	// public void write(String str) throws IOException{
	// super.write(str+"\n");
	// }
	// public void writeLine(String str) throws IOException{
	// super.write(str);
	// super.write('\n');
	// if(isWin) {
	// super.write('\r');
	// }
	// }
	public synchronized void writeLine() throws IOException {
		writeLine("");
	}

	public synchronized void writeLine(Object o) throws IOException {
		String toWrite = null;
		if (o != null) {
			toWrite = o.toString();
		} else {
			toWrite = "null";
		}
		super.write(toWrite);

		if (isWin) {
			super.write('\r');
		}
		super.write('\n');
	}

	@Override
	public synchronized void close() {
		try {
			super.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void finalize() {
		this.close();
	}
}
