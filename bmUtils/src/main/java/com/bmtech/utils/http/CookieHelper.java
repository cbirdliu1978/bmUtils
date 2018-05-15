package com.bmtech.utils.http;

import java.io.IOException;
import java.util.ArrayList;

import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;

public abstract class CookieHelper {
	public class CookieInfos {
		public final long cookieSetTime;
		public final String userAgent;
		public final String cookie1, cookie2;
		public final HttpHandler handler;

		CookieInfos(long cookieSetTime, String userAgent, String cookie1, String cookie2) throws Exception {
			this.cookieSetTime = cookieSetTime;
			this.userAgent = userAgent;
			this.cookie1 = cookie1;
			this.cookie2 = cookie2;
			this.handler = getHandler();
		}

		private HttpHandler getHandler() throws Exception {
			String[] strs = new String[] { cookie1, cookie2 };
			ArrayList<String[]> lst = new ArrayList<String[]>();
			for (String x : strs) {
				ArrayList<String[]> ilst = parseCookieStr(x);
				lst.addAll(ilst);
			}

			HttpHandler handler = HttpHandler.getCrawlHandler(null, userAgent, null, true);
			for (String[] x : lst) {
				handler.addCookie(x[0], x[1]);
			}
			return handler;

		}
	}

	public abstract CookieInfos loadCookieInfo() throws Exception;

	protected final int cookieExpireMinutes;
	protected final String cookieId;

	public CookieHelper(String cookieId) throws IOException {
		this.cookieId = cookieId;
		cookieExpireMinutes = 28;
	}

	public static ArrayList<String[]> parseCookieStr(String cookieStr) {
		String prf = "Cookie:";
		cookieStr = cookieStr.trim();

		if (cookieStr.startsWith(prf)) {
			cookieStr = cookieStr.substring(prf.length(), cookieStr.length());
		}
		ArrayList<String[]> ret = new ArrayList<String[]>();
		if (cookieStr != null) {
			String[] strs = cookieStr.split("\n");
			for (String x : strs) {
				x = x.trim();
				if (x.length() == 0) {
					continue;
				}
				String[] token = x.split("	");
				if (token.length < 2) {
					BmtLogger.instance().log(LogLevel.Error, "skip cookieStr, can not parse! '%s'", x);
					continue;
				}
				if (token.length < 5) {
					BmtLogger.instance().log(LogLevel.Warning, "mini cookieStr! '%s'", x);
				}
				ret.add(new String[] { token[0], token[1] });
			}
		}
		return ret;
	}

}
