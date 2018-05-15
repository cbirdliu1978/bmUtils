/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.bmtech.utils.bmfs.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 */
public class ReadProtocol {

	protected final AssureInputStream trans_;
	final private boolean bigHead;

	public ReadProtocol(InputStream ops) {
		this(ops, true);
	}

	public ReadProtocol(InputStream ops, boolean bigHead) {
		this.bigHead = bigHead;
		if (ops instanceof AssureInputStream) {
			trans_ = (AssureInputStream) ops;
		} else {
			trans_ = new AssureInputStream(ops);
		}
	}

	/**
	 * Reading methods.
	 */

	public byte readByte() throws IOException {
		return (byte) trans_.read();
	}

	private byte[] i16rd = new byte[2];

	public short readI16() throws IOException {
		byte[] buf = i16rd;
		trans_.read(i16rd);
		if (this.bigHead) {
			return (short) (((buf[0] & 0xff) << 8) | ((buf[1] & 0xff)));
		} else {
			return (short) (((buf[1] & 0xff) << 8) | ((buf[0] & 0xff)));
		}
	}

	private byte[] i32rd = new byte[4];

	public int readI32() throws IOException {
		byte[] buf = i32rd;
		trans_.read(buf);
		if (this.bigHead) {
			return ((buf[0] & 0xff) << 24) | ((buf[1] & 0xff) << 16) | ((buf[2] & 0xff) << 8) | ((buf[3] & 0xff));
		} else {
			return ((buf[3] & 0xff) << 24) | ((buf[2] & 0xff) << 16) | ((buf[1] & 0xff) << 8) | ((buf[0] & 0xff));
		}
	}

	private byte[] i64rd = new byte[8];

	public long readI64() throws IOException {
		trans_.read(i64rd, 0, 8);
		if (this.bigHead) {
			return ((long) (i64rd[0] & 0xff) << 56) | ((long) (i64rd[1] & 0xff) << 48)
					| ((long) (i64rd[2] & 0xff) << 40) | ((long) (i64rd[3] & 0xff) << 32)
					| ((long) (i64rd[4] & 0xff) << 24) | ((long) (i64rd[5] & 0xff) << 16)
					| ((long) (i64rd[6] & 0xff) << 8) | (i64rd[7] & 0xff);
		} else {
			return ((long) (i64rd[7] & 0xff) << 56) | ((long) (i64rd[6] & 0xff) << 48)
					| ((long) (i64rd[5] & 0xff) << 40) | ((long) (i64rd[4] & 0xff) << 32)
					| ((long) (i64rd[3] & 0xff) << 24) | ((long) (i64rd[2] & 0xff) << 16)
					| ((long) (i64rd[1] & 0xff) << 8) | (i64rd[0] & 0xff);

		}
	}

	public double readDouble() throws IOException {
		return Double.longBitsToDouble(readI64());
	}

	public String readString() throws IOException {
		int size = readI32();
		try {
			byte[] buf = new byte[size];
			trans_.read(buf);
			return new String(buf, "UTF-8");
		} catch (UnsupportedEncodingException uex) {
			throw new IOException("JVM DOES NOT SUPPORT UTF-8");
		}

	}

	public int read(byte[] buf) throws IOException {
		return trans_.read(buf, 0, buf.length);
	}

	public int read(byte[] buf, int off, int len) throws IOException {
		return trans_.read(buf, off, len);
	}
}
