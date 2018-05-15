package com.bmtech.utils.http;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;

public class HttpHandler {

	public static final String POST_METHOD = "POST";
	public static final String GET_METHOD = "GET";
	private boolean useSecure = true;
	public static final String[] agents = {
			// chrome
			"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36",
			"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0",
			// fire fox
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1) Gecko/20090624 Firefox/3.5" };
	// static final String OPRORA_AGENT = agents[0];
	// static final String SE360_AGENT = agents[1];
	// static final String CHROME_AGENT = agents[2];
	// static final String MYIE_AGENT = agents[3];
	// static final String IE8_AGENT = agents[4];
	// static final String FIREP_FOX_AGENT = agents[5];

	public static final String getAgents() {
		return agents[(int) (System.nanoTime() % agents.length)];
	}

	private boolean post = false;

	public void setPost(boolean post) {
		this.post = post;
	}

	public boolean isPost() {
		return post;
	}

	public static class Prop {
		public final String key;
		private String value;

		public Prop(String key, String value) {
			this.key = key;
			this.setValue(value);
		}

		@Override
		public Object clone() {
			return new Prop(key, getValue());
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	protected final Prop acc = new Prop("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	protected final Prop ref = new Prop("Referer", null);
	protected final Prop agent = new Prop("User-Agent", null);
	protected final Prop lan = new Prop("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
	protected final Prop enc = new Prop("Accept-Encoding", "gzip, deflate");
	protected final Prop con = new Prop("Connection", "keep-alive");
	protected final Prop upgradeInsecureRequest = new com.bmtech.utils.http.HttpHandler.Prop(
			"Upgrade-Insecure-Requests", "1");
	// protected final Prop encOnly = new Prop("Accept-Encoding", "gzip,
	// deflate");
	protected Proxy pry;
	public ICookie cookie = new ICookie();
	private int connectTimeout = 3 * 1000;
	private int readTimeout = 30 * 1000;
	public boolean useCookie = true;
	private boolean acceptZipFormat = true;

	protected HttpHandler() {

	}

	public void setAgent(String agent) {
		this.agent.setValue(agent);
	}

	public String getAgent() {
		return this.agent.value;
	}

	public void applyTo(HttpURLConnection conn) {
		conn.addRequestProperty(acc.key, acc.getValue());
		if (ref.getValue() == null) {
			;
		} else {
			conn.addRequestProperty(ref.key, ref.getValue());
		}
		conn.addRequestProperty(lan.key, lan.getValue());
		if (this.acceptZipFormat) {
			conn.addRequestProperty(enc.key, enc.getValue());
		}
		conn.addRequestProperty(agent.key, agent.getValue());
		conn.addRequestProperty(con.key, con.getValue());
		if (this.useCookie) {
			if (this.cookie.hasCookie()) {
				conn.addRequestProperty("Cookie", this.cookie.cookieString());
			}
		}
		if (this.isUseSecure()) {
			conn.addRequestProperty(this.upgradeInsecureRequest.getValue(), upgradeInsecureRequest.getValue());
		}
		conn.setConnectTimeout(this.connectTimeout);
		conn.setReadTimeout(this.readTimeout);

	}

	public void setProxy(Proxy pry) {
		this.pry = pry;
	}

	public void setHttpProxy(String host, int port) throws UnknownHostException {
		this.pry = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(InetAddress.getByName(host), port));
	}

	public Proxy getProxy() {
		return pry;
	}

	public void addCookie(String key, String value) {
		this.cookie.putCookie(key, value);
	}

	public void setCookie(Map<String, List<String>> headInfo) {
		this.cookie.put(headInfo);
	}

	/**
	 * make a BrowserInfo
	 * 
	 * @param ref
	 *            refered url this visitor come from
	 * @param agent
	 *            user-agent
	 * @param pry
	 *            proxy if not null, will use proxy
	 * @param useCookie
	 *            if true, use cookie
	 * @return
	 */
	public static HttpHandler getCrawlHandler(String ref, String agent, Proxy pry, boolean useCookie) {
		HttpHandler info = new HttpHandler();
		info.ref.setValue(ref);
		info.agent.setValue(agent);
		info.pry = pry;
		info.useCookie = useCookie;
		return info;
	}

	public void setCrawled(URL url) {
		if (url != null)
			this.ref.setValue(url.toString());
		else {
			BmtLogger.instance().log(LogLevel.Warning, "setCrawled url failed, null given");
		}
	}

	/**
	 * see {@link #getCrawlHandler(String, String, Proxy, boolean)}, Proxy is
	 * null
	 * 
	 * @param ref
	 * @param agent
	 * @param useCookie
	 * @return
	 */
	public static HttpHandler getCrawlHandler(String ref, String agent, boolean useCookie) {
		return getCrawlHandler(ref, agent, null, useCookie);
	}

	/**
	 * see {@link #getCrawlHandler(String, String, boolean)} using a random
	 * selected user-agent
	 * 
	 * @param ref
	 * @param useCookie
	 * @return
	 */
	public static HttpHandler getCrawlHandler(String ref, boolean useCookie) {
		return getCrawlHandler(ref, getAgents(), useCookie);
	}

	/**
	 * see {@link #getCrawlHandler(String, boolean)} using null ref
	 * 
	 * @param useCookie
	 * @return
	 */
	public static HttpHandler getCrawlHandler(boolean useCookie) {
		return getCrawlHandler(null, useCookie);
	}

	/**
	 * see {@link #getCrawlHandler(boolean)} using no cookie
	 * 
	 * @return
	 */
	public static HttpHandler getCrawlHandler() {
		return getCrawlHandler(true);
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setAcceptZipFormat(boolean acceptZipFormat) {
		this.acceptZipFormat = acceptZipFormat;
	}

	public void addCookie(ICookie ic) {
		this.cookie.put(ic);

	}

	public boolean isUseSecure() {
		return useSecure;
	}

	public void setUseSecure(boolean useSecure) {
		this.useSecure = useSecure;
	}

}
