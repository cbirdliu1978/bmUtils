package com.bmtech.utils.var;

import java.io.IOException;

import com.bmtech.utils.ForEach;

public class AAA {

	public static void main(String[] ars) throws IOException {
		ForEach.forEachLine("/data/sss.csv", (line, lineI) -> {
			// System.out.println(line);
			String tokens[] = line.split("\t");
			StringBuilder sb = new StringBuilder();
			for (int x = 1; x < tokens.length; x++) {
				if (sb.length() > 0)
					sb.append(", ");
				String t = tokens[x];
				if (t.equals("\\N"))
					sb.append("null");
				else {
					sb.append("\"");
					sb.append(t);
					sb.append("\"");
				}

			}
			System.out.println("(" + sb + ")");
		});
	}
}
