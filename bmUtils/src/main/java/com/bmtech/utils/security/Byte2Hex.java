package com.bmtech.utils.security;

import java.io.ByteArrayOutputStream;

public class Byte2Hex {

	public static String byte2Hex(byte[] bs) {
		StringBuilder sb = new StringBuilder();
		for (byte c : bs) {
			String sub = Integer.toString(c & 0xff, 16);
			// System.out.println(c + "-->" + sub);
			if (sub.length() < 2) {
				sb.append('0');
			}
			sb.append(sub);
		}
		return sb.toString();
	}

	public static byte[] hex2Byte(String str) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (int x = 0; x < str.length(); x += 2) {
			if (x + 1 >= str.length()) {
				break;
			}
			String sub = str.substring(x, x + 2);
			byte var = (byte) Integer.parseInt(sub, 16);
			// System.out.println(sub + "<--" + var);
			bos.write(var);
		}
		return bos.toByteArray();
	}

	public static void main(String[] args) {
		String content = "技术性问题EDF%&^%#_|~";
		String encKey = "sadfasdfas";
		byte[] bs = BmAes.encrypt(encKey, content.getBytes());
		String byte2Hex = byte2Hex(bs);
		System.out.println(byte2Hex);
		byte[] bytes = hex2Byte(byte2Hex);
		String dec = BmAes.decrypt(encKey, bytes);
		System.out.println(dec);
	}
}
