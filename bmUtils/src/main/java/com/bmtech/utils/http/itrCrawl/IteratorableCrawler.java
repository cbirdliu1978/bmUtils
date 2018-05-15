package com.bmtech.utils.http.itrCrawl;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import com.bmtech.utils.ZipUnzip;
import com.bmtech.utils.bmfs.MDir;
import com.bmtech.utils.bmfs.MFile;
import com.bmtech.utils.bmfs.MFileWriter;
import com.bmtech.utils.bmfs.MFileWriter.MFileOutputStream;
import com.bmtech.utils.http.HttpCrawler;
import com.bmtech.utils.http.HttpHandler;
import com.bmtech.utils.log.LogHelper;

public class IteratorableCrawler {

	private int interval = 3000;
	protected final MDir mdir;
	public final HttpHandler handler = HttpHandler.getCrawlHandler();
	protected final UrlIterator genor;
	private final int connectTimeoutMs, readTimeoutMs;
	AtomicInteger runningThread = new AtomicInteger(0);
	protected AtomicInteger errorNum = new AtomicInteger(0), crawledNum = new AtomicInteger(0),
			skipNum = new AtomicInteger(0);
	private int minSize = 0;

	public void waitTerminal() {
		while (true) {
			if (this.runningThread.get() <= 0) {
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		MDir.closeMDir(mdir);
	}

	public IteratorableCrawler(MDir mdirx, UrlIterator gen) throws IOException {
		this(mdirx, gen, 5000, 30000);
	}

	LogHelper reporter;

	public IteratorableCrawler(MDir mdirx, UrlIterator gen, int connectTimeoutMs, int readTimeoutMs)
			throws IOException {
		this.connectTimeoutMs = connectTimeoutMs;
		this.readTimeoutMs = readTimeoutMs;
		this.mdir = mdirx;
		this.genor = gen;
		reporter = new LogHelper("reporter");
		Thread report = new Thread() {
			@Override
			public void run() {

				while (true) {
					try {
						Thread.sleep(1000);
						reporter.fatal(
								"IteratorCrawler's report: lines = %s, real crawl %s, skip %s, error %s, mdir size %s",
								genor.readedLine(), crawledNum, skipNum, errorNum, mdir.size());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		report.setDaemon(true);
		report.start();
	}

	public void spawn(int threadsNumber) {
		for (int x = 0; x < threadsNumber; x++) {
			CrawlThread ct = new CrawlThread(x);
			ct.start();
		}
	}

	public class CrawlThread extends Thread {
		LogHelper log;
		final int id;
		protected final MFileWriter writer;
		private String lastUrl = null;
		HttpHandler hdl = HttpHandler.getCrawlHandler();

		CrawlThread(int id) {
			this.id = id;
			writer = mdir.openWriter();
			log = new LogHelper("" + this.getId());
			hdl = HttpHandler.getCrawlHandler(lastUrl, true);
			hdl.setAgent("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0");
			hdl.setConnectTimeout(connectTimeoutMs);
			hdl.setReadTimeout(readTimeoutMs);
		}

		@Override
		public long getId() {
			return this.id;
		}

		public void crawl(URL url, MFile mfile, MFileWriter writer) throws IOException {
			lastUrl = url.toString();
			HttpCrawler crl = new HttpCrawler(url, hdl);
			try {
				crl.connect();
				byte[] bs = crl.getBytes();
				int orgLen = bs.length;
				bs = ZipUnzip.gzip(bs);
				int gzLen = bs.length;
				log.debug("gzip rates %.2f for mfile %s", gzLen / (float) orgLen, mfile);
				MFileOutputStream ops = writer.openMFile(mfile);
				try {
					ops.write(bs);
				} finally {
					ops.close();
				}
			} finally {
				crl.close();
			}
			log.warn("crawl %s, size %sKB after gzip, from url %s", mfile, mfile.getLength() / 100 / 10.0, url);
		}

		@Override
		public void run() {

			runningThread.incrementAndGet();
			while (true) {
				GenEntry genEntry;
				try {
					synchronized (genor) {
						genEntry = genor.next();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					continue;
				}
				if (genEntry == null) {
					log.info("thread exist");
					break;
				}
				boolean realCrawl = false;
				try {
					realCrawl = this.crawlKeyword(genEntry);

				} catch (Exception e) {
					errorNum.incrementAndGet();
					realCrawl = true;
					log.error(e, "when crawl %s", genEntry);
					e.printStackTrace();
				}
				if (realCrawl && getInterval() > 0) {
					crawledNum.incrementAndGet();
					try {
						sleep(getInterval());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					skipNum.incrementAndGet();
				}
			}
			runningThread.decrementAndGet();
		}

		public boolean crawlKeyword(GenEntry entry) throws Exception {
			MFile mfile = mdir.createMFileIfPossible(entry.storeName);
			if (mfile == null) {
				if (minSize > 0) {
					MFile mf = mdir.getMFileByName(entry.storeName);
					if (mf.getLength() < minSize) {
						log.warn("delete %s because too small %sB, by %s", entry.storeName, mf.getLength(), entry.word);
						mdir.delete(mf);
						mfile = mdir.createMFileIfPossible(entry.storeName);
						log.info("crawling %s, by %s", entry.storeName, entry.word);
						crawl(entry.url, mfile, writer);
						return true;
					}
				}

				log.info("SKIP %s, by %s", entry.storeName, entry.word);
				return false;
			}
			log.info("crawling %s, by %s", entry.storeName, entry.word);
			crawl(entry.url, mfile, writer);
			return true;
		}
	}

	// public static void crawl(MDir mdirx, UrlIterator itr, int crawlInterval)
	// throws IOException {
	// int threadsNumber = Consoler.readInt("threadsNumber(default 1):", 1);
	// IteratorableCrawler c = new IteratorableCrawler(mdirx, itr);
	// c.setInterval(crawlInterval);
	// c.spawn(threadsNumber);
	// }

	public static IteratorableCrawler crawl(MDir mdirx, UrlIterator itr, int threadsNumber, int crawlInterval,
			int connectTimeoutMs, int readtimeoutMs) throws IOException {
		IteratorableCrawler c = new IteratorableCrawler(mdirx, itr, connectTimeoutMs, readtimeoutMs);
		c.setInterval(crawlInterval);
		c.spawn(threadsNumber);
		return c;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getMinSize() {
		return minSize;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	public void setLogName(String name) {
		reporter = new LogHelper(name);
	}
}
