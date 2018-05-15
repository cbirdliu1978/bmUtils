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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Binary protocol implementation for thrift.
 *
 */
public class WriteProtocol {


	final OutputStream trans_ ;
	/**
	 * Constructor
	 */
	public WriteProtocol(OutputStream ops) {
		trans_ = ops;
	}


	public void writeBool(boolean b) throws IOException {
		writeByte(b ? (byte)1 : (byte)0);
	}

	public void writeByte(byte b) throws IOException {
		trans_.write(b);
	}

	private byte[] i16out = new byte[2];
	public void writeI16(short i16) throws IOException {
		i16out[0] = (byte)(0xff & (i16 >> 8));
		i16out[1] = (byte)(0xff & (i16));
		trans_.write(i16out, 0, 2);
	}

	private byte[] i32out = new byte[4];
	public void writeI32(int i32) throws IOException {
		i32out[0] = (byte)(0xff & (i32 >> 24));
		i32out[1] = (byte)(0xff & (i32 >> 16));
		i32out[2] = (byte)(0xff & (i32 >> 8));
		i32out[3] = (byte)(0xff & (i32));
		trans_.write(i32out, 0, 4);
	}

	private byte[] i64out = new byte[8];
	public void writeI64(long i64) throws IOException {
		i64out[0] = (byte)(0xff & (i64 >> 56));
		i64out[1] = (byte)(0xff & (i64 >> 48));
		i64out[2] = (byte)(0xff & (i64 >> 40));
		i64out[3] = (byte)(0xff & (i64 >> 32));
		i64out[4] = (byte)(0xff & (i64 >> 24));
		i64out[5] = (byte)(0xff & (i64 >> 16));
		i64out[6] = (byte)(0xff & (i64 >> 8));
		i64out[7] = (byte)(0xff & (i64));
		trans_.write(i64out, 0, 8);
	}

	public void writeDouble(double dub) throws IOException {
		writeI64(Double.doubleToLongBits(dub));
	}

	public void writeString(String str) throws IOException {
		try {
			byte[] dat = str.getBytes("UTF-8");
			writeI32(dat.length);
			trans_.write(dat, 0, dat.length);
		} catch (UnsupportedEncodingException uex) {
			throw new IOException("JVM DOES NOT SUPPORT UTF-8");
		}
	}

	public void writeBinary(ByteBuffer bin) throws IOException {
		int length = bin.limit() - bin.position();
		writeI32(length);
		trans_.write(bin.array(), bin.position() + bin.arrayOffset(), length);
	}

	public void write(byte[]buf, int offset, int len) throws IOException{
		trans_.write(buf, offset, len);
	}


	public void flush() throws IOException {
		this.trans_.flush();
	}


	public void close() throws IOException {
		trans_.close();
	}

	public void write(byte[] b) throws IOException {
		this.write(b, 0, b.length);
	}
	
}
