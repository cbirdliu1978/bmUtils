package com.bmtech.utils.http;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bmtech.utils.KeyValuePair;
import com.bmtech.utils.io.TchFileTool;

public class ICookie {
	public Map<String, HttpCookie> cookies = Collections.synchronizedMap(new HashMap<String, HttpCookie>());
	public static final String cookieTag = "Set-Cookie";
	public static final String cookieTag2 = "Set-Cookie2";

	public void put(Map<String, List<String>> headInfo) {
		List<String> cookies;

		cookies = headInfo.get(cookieTag);
		if (cookies == null) {
			cookies = headInfo.get(cookieTag2);
		}
		String name;
		if (cookies == null) {
			Iterator<String> itr = headInfo.keySet().iterator();
			while (itr.hasNext()) {
				name = itr.next();
				if (name == null)
					continue;
				if (0 == name.compareToIgnoreCase(cookieTag)) {
					cookies = headInfo.get(name);
					break;
				}
				if (0 == name.compareToIgnoreCase(cookieTag2)) {
					cookies = headInfo.get(name);
					break;
				}
			}
		}
		if (null != cookies) {
			put(cookies);
		}
	}

	private final void put(List<String> cookies) {
		List<HttpCookie> lst = new ArrayList<HttpCookie>();
		for (String s : cookies) {
			lst.addAll(HttpCookie.parse(s));
		}
		for (HttpCookie ck : lst) {
			this.put(ck);
		}
	}

	public void put(ICookie c) {
		for (HttpCookie cc : c.cookies.values()) {
			this.put(cc);
		}
	}

	public void putCookie(String key, String value) {
		HttpCookie ck = new HttpCookie(key, value);
		this.put(ck);
	}

	public void put(HttpCookie ck) {
		String name = ck.getName();
		cookies.put(name, ck);
	}

	public boolean hasCookie() {
		return this.cookies.size() != 0;
	}

	/**
	 * covert to cookieString
	 * 
	 * @return
	 */
	public String cookieString() {
		Collection<HttpCookie> coll = cookies.values();
		StringBuilder sb = new StringBuilder();
		for (HttpCookie cookie : coll) {
			if (sb.length() > 0) {
				sb.append(';');
			}
			sb.append(cookie.getName());
			sb.append('=');
			sb.append(cookie.getValue());
		}
		return sb.toString();
	}

	public String toString() {
		return this.cookieString();
	}

	public static ICookie parseFromTxt(String x) {
		ICookie ret = new ICookie();
		String tokens[] = x.split("\\; ");
		for (String xx : tokens) {
			KeyValuePair<String, String> pair = KeyValuePair.parse(xx);
			HttpCookie cc = new HttpCookie(pair.key, pair.value);
			ret.put(cc);
		}
		return ret;
	}

	public static void main(String[] args) throws IOException {
		String line = TchFileTool.get("config/wx", "sogou.wx.cookie");
		ICookie ic = ICookie.parseFromTxt(line);
		System.out.println(ic);
		HttpHandler hdl = HttpHandler.getCrawlHandler(true);
		hdl.addCookie(ic);
		System.out.println(hdl.useCookie);
		System.out.println(hdl.cookie);
		HttpCrawler cr = HttpCrawler.makeCrawler(
				new URL("http://weixin.sogou.com/weixin?type=2&query=%E8%93%9D%E8%89%B2&ie=utf8&_sug_=n&_sug_type_=&w=01019900&sut=884&sst0=1468032677681&lkt=1%2C1468032677579%2C1468032677579&page=1"),
				hdl);
		String str = cr.getString();
		System.out.println(str);
		;
	}
}
