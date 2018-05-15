package com.bmtech.utils.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import com.bmtech.utils.log.LogHelper;

public class CrawlContext implements Runnable {
	public static class CrawlOut {
		private final boolean useFile;
		private final File fileToSave;
		protected final OutputStream out;
		private String suffix = ".html";

		public CrawlOut(OutputStream output) {
			this.useFile = false;
			this.fileToSave = null;
			this.out = output;
		}

		public OutputStream getOutputStream() throws IOException {
			if (useFile) {
				return new FileOutputStream(fileToSave);
			} else {
				return out;
			}
		}

		public String getSuffix() {
			return suffix;
		}

		private void setSuffix(String suffix) {
			this.suffix = suffix;
		}
	}

	private final URL url;
	private CrawlOut output;
	final HttpHandler hdl;
	private long totRead = -1;
	private int httpCode = -1;
	private boolean okRun = false;
	private boolean isDownload = false;
	private LogHelper log;
	private boolean allowDownload = false;

	public CrawlContext(URL url, CrawlOut out, HttpHandler hdl, LogHelper log) {
		this(url, out, hdl, log, false);
	}

	public CrawlContext(URL url, CrawlOut out, HttpHandler hdl, LogHelper log,
			boolean allowDownload) {
		this.url = url;
		this.output = out;
		if (this.output.useFile && output.fileToSave.exists()) {
			output.fileToSave.delete();
		}
		this.hdl = hdl;
		this.log = log;
		this.allowDownload = allowDownload;
	}

	@Override
	public void run() {
		HttpCrawler crl = null;
		try {
			crl = HttpCrawler.makeCrawler(getUrl(), hdl);
			if (200 == crl.getHttpCode()) {
				DownloadTypeParser dpp = DownloadTypeParser.getParser(
						crl.getURL(), getUrl(), crl.getHeadInfo());

				boolean isDownload = dpp.isDownloadType();
				String suffix = null;
				if (isDownload) {
					suffix = dpp.suffix();
					if (suffix != null) {
						this.setDownload(true);
					}
				}
				if (this.isDownload()) {
					if (!allowDownload) {
						crl.close();
						log.error(
								"reject read url %s beacouse not allow download",
								getUrl());
						return;
					}
				}
				OutputStream out = output.getOutputStream();
				try {
					crl.dumpTo(out);
					crl.close();
				} finally {
					out.close();
				}
				totRead = crl.getTotalReaded();
				this.setHttpCode(crl.getHttpCode());

				if (this.isDownload()) {
					output.setSuffix(suffix);
				}
				setOkRun(true);
			} else {
				log.error("error httpCode %s for url %s", crl.getHttpCode(),
						getUrl());
				crl.close();
			}
		} catch (Exception e) {
			log.error(e, "when crawl %s", getUrl());
		} finally {
			if (crl != null) {
				crl.close();
			}
		}
	}

	public long getTotRead() {
		return totRead;
	}

	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}

	public int getHttpCode() {
		return httpCode;
	}

	public void setOkRun(boolean okRun) {
		this.okRun = okRun;
	}

	public boolean isOkRun() {
		return okRun;
	}

	public CrawlOut getCrawlOut() {
		return this.output;
	}

	public URL getUrl() {
		return url;
	}

	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}

	public boolean isDownload() {
		return isDownload;
	}
}