package com.bmtech.utils.http;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bmtech.utils.Charsets;
import com.bmtech.utils.Misc;

/**
 * 1. check header Content-Disposition, if there is, is download type 2.
 * fileName is doc/xsl ...,view as download 3. if is text/html, not download
 * type 4. is download type
 * 
 * get suffix_name --> first from Content-Disposition --> if not get, get from
 * fileName, use re_direction_info also, --> if not, bind some as
 * content-type<--->suffix get name --> first from Content-Disposition --> if
 * not get, get from fileName, use re_direction_info also --> anchor
 * 
 * @TODO so many 403 why
 * 
 * 
 * @author mudie
 *
 */
public class DownloadTypeParser {
	static final String[] allowAutoSuffix = new String[] { ".rar", ".zip",
			".doc", ".docx", ".pdf", ".xls", ".xlsx", ".csv", ".7z", ".rtf" };

	public static String getFileNameFromUrl(URL url) {
		if (url == null) {
			return null;
		}
		String fileName = url.getFile();
		int ipos = fileName.indexOf("?");
		if (ipos > -1) {
			fileName = fileName.substring(0, ipos);
		}

		fileName.replace("\\", "/");

		ipos = fileName.lastIndexOf('/');
		if (ipos > -1) {
			fileName = fileName.substring(ipos + 1, fileName.length());
		}
		fileName = Misc.toFileNameFormat(fileName, '^');
		return fileName;
	}

	public static DownloadTypeParser getParser(URL u1, URL u2,
			Map<String, List<String>> headInfo) {
		if (u1.toString().equals(u2.toString())) {
			u2 = null;
		}
		String fileName_1 = getFileNameFromUrl(u1);
		String fileName_2 = getFileNameFromUrl(u2);
		String contentDispos = null;
		List<String> lst = headInfo.get("Content-Disposition");
		if (lst != null) {
			for (String s : lst) {
				if (s == null)
					continue;
				String iname = Misc.getSubString(s, "filename=", null);
				if (iname != null) {
					iname = iname.trim();
					if (iname.length() > 0) {
						contentDispos = iname;
					}
				}
			}
		}
		if (contentDispos != null) {
			if (contentDispos.startsWith("\"") || contentDispos.startsWith("'")) {
				contentDispos = contentDispos.substring(1,
						contentDispos.length());
			}
			if (contentDispos.endsWith("\"") || contentDispos.endsWith("'")) {
				contentDispos = contentDispos.substring(0,
						contentDispos.length() - 1);
			}
			contentDispos = contentDispos.trim();
		}

		String content_type = null;
		lst = headInfo.get("Content-Type");
		if (lst != null) {
			for (String s : lst) {
				if (s != null) {
					s = s.trim();
					if (s.length() > 0) {
						content_type = s;
					}
				}
			}
		}

		return new DownloadTypeParser(contentDispos, fileName_1, fileName_2,
				content_type);
	}

	final String contentDispos;
	final String fileName_1;
	final String fileName_2;
	final String content_type;

	DownloadTypeParser(String contentDispos, String fileName_1,
			String fileName_2, String content_type) {
		this.contentDispos = contentDispos;
		this.fileName_1 = fileName_1;
		this.fileName_2 = fileName_2;
		this.content_type = content_type;
	}

	String tryGetSuffix(String str) {
		if (str == null) {
			return null;
		}
		if (str.length() > 3) {
			int pos = str.lastIndexOf(".");
			if (pos > -1) {
				String ret = str.substring(pos + 1, str.length()).trim();
				ret = ret.trim();
				if (ret.length() > 1) {
					return ret;
				}
			}
		}
		return null;
	}

	String tryGetName(String str) {
		if (str == null) {
			return null;
		}
		str = Misc.formatFileName(str, '~');
		if (str.length() > 3) {
			int pos = str.lastIndexOf(".");
			if (pos > -1) {
				String ret = str.substring(0, pos).trim();
				ret = ret.trim();
				if (ret.length() > 1) {
					return ret;
				}
			}
		}
		return null;
	}

	public boolean isDownloadType() {
		/**
		 * 1. check header Content-Disposition, if there is, is download type 2.
		 * fileName is doc/xsl ...,view as download 3. if is text/html, not
		 * download type 4. is download type
		 **/
		for (String str : allowAutoSuffix) {
			if (contentDispos != null) {
				if (contentDispos.endsWith(str)) {
					return true;
				}
			}
			if (fileName_1 != null) {
				if (fileName_1.endsWith(str)) {
					return true;
				}
			}
			if (fileName_2 != null) {
				if (fileName_2.endsWith(str)) {
					return true;
				}
			}
		}
		return false;
	}

	public String suffix() {
		String ret;
		ret = tryGetSuffix(contentDispos);
		if (ret != null) {
			return ret;
		}

		ret = tryGetSuffix(fileName_2);
		if (ret != null) {
			return ret;
		}

		ret = tryGetSuffix(fileName_1);
		if (ret != null) {
			return ret;
		}

		return null;

	}

	private void addSuggestName(ArrayList<String> ret, final String name) {
		if (name == null)
			return;
		if (!ret.contains(name)) {
			ret.add(name);
		}

		String newName = name;
		if (name.contains("%")) {
			try {
				newName = URLDecoder.decode(name, Charsets.GBK_STR);
				if (!ret.contains(newName)) {
					ret.add(newName);
				}
			} catch (Exception e) {

			}

			try {
				newName = URLDecoder.decode(name, Charsets.UTF8_STR);
				if (!ret.contains(newName)) {
					ret.add(newName);
				}
			} catch (Exception e) {

			}
		}
	}

	public ArrayList<String> sugguestNames() {
		ArrayList<String> ret = new ArrayList<String>();

		String name = tryGetName(contentDispos);
		addSuggestName(ret, name);
		if (name != null) {
			try {
				String n1 = new String(name.getBytes("iso-8859-1"),
						Charsets.GBK_CS);
				String n2 = new String(name.getBytes("iso-8859-1"),
						Charsets.UTF8_CS);
				if (!ret.contains(n1)) {
					ret.add(n1);
				}
				if (!ret.contains(n2)) {
					ret.add(n2);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		}

		name = tryGetName(fileName_1);
		addSuggestName(ret, name);
		name = tryGetName(fileName_2);
		addSuggestName(ret, name);

		return ret;

	}

	public static void main(String[] args) throws Exception {
		System.out
				.println(getFileNameFromUrl(new URL(
						"http://www.wangxiao.cn/files/2011/3/25/1103040854401104309.xls")));
	}
}
