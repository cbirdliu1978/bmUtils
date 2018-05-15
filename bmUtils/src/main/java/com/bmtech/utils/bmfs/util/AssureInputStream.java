package com.bmtech.utils.bmfs.util;

import java.io.IOException;
import java.io.InputStream;

public class AssureInputStream extends InputStream{
	public static class AssureFailException extends IOException{
		private static final long serialVersionUID = 1L;

		public AssureFailException(String clause){
			super(clause);
		}
	}
	final InputStream ips;
	public AssureInputStream(InputStream ips){
		this.ips = ips;
	}
	public int read() throws IOException {
		int ret = ips.read();
		if(ret == -1){
			throw new AssureFailException("input stream end reach!");
		}
		return ret;
	}
	public int readByte() throws IOException {
		return ips.read();
	}
	public int readToBuf(byte[] buf) throws IOException {
		return ips.read(buf, 0, buf.length);
	}
	public int readToBuf(byte[] buf, int off, int len) throws IOException {
	     return ips.read(buf, off, len);
	  }
	public int read(byte[] buf) throws IOException {
		return read(buf, 0, buf.length);
	}
	public int read(byte[] buf, int off, int len) throws IOException {
	    int got = 0;
	    int ret = 0;
	    while (got < len) {
	      ret = ips.read(buf, off+got, len-got);
	      if (ret <= 0) {
	        throw new AssureFailException(
	            "end reach! expect "
	                + len
	                + " bytes, but only got "
	                + got
	                + " bytes. )");
	      }
	      got += ret;
	    }
	    return got;
	  }
	
	public void close() throws IOException{
		this.ips.close();
	}
}
