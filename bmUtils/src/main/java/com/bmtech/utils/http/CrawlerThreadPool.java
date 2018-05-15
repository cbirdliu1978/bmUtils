package com.bmtech.utils.http;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.bmtech.utils.log.LogHelper;

public abstract class CrawlerThreadPool {

	final BlockingQueue<Runnable> crawlQueue = new LinkedBlockingQueue<Runnable>();
	final ThreadPoolExecutor crawlerPool ;
	AtomicInteger hasOkRun = new AtomicInteger(0);
	AtomicLong readedBytes = new AtomicLong(0);
	private AtomicInteger allRun = new AtomicInteger(0);
	AtomicInteger hasOkUrl = new AtomicInteger(0);
	LogHelper log = new LogHelper("crlPool");
	protected abstract int getPoolSize();
	public CrawlerThreadPool(){

		crawlerPool = new ThreadPoolExecutor(
				getPoolSize(),
				getPoolSize(), 
				100, TimeUnit.SECONDS, crawlQueue){
			protected void afterExecute(Runnable r, Throwable t) {
				synchronized(r){
					r.notifyAll();
				}
				CrawlContext rr = (CrawlContext) r;

				int allRuned = allRun.addAndGet(1);
				if(rr.isOkRun()){
					hasOkRun.addAndGet(1);
				}

				int hasOk = hasOkRun.get();
				if(rr.getTotRead() > 0){
					readedBytes.addAndGet(rr.getTotRead());
				}

				if(allRuned % 10 == 0 ) {
					log.fatal("has okRunned urls %s(%sMB), has allRuned:%s, getTaskCount:%s, now running Threads:%s, waiting threads: %s",
							hasOk,
							readedBytes.longValue() / 1024 /10/100.0,
							allRuned,
							crawlerPool.getTaskCount(),
							crawlerPool.getActiveCount(),
							crawlQueue.size()
					);
				}
			}
		};
	}

	public void blockExe(CrawlContext ce){
		synchronized(ce){
			crawlerPool.execute(ce);
			try {
				ce.wait(2 * 60 * 60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void submit(CrawlContext crawlContext) {
		crawlerPool.execute(crawlContext);		
	}
}
