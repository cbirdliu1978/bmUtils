package com.bmtech.utils.log;


/**
 * should use open source!!!
 * @author beiming
 *
 */
public class BmtLogger{
	public static BmtLogger instance = new BmtLogger();
	public  static final LogHelper log = new LogHelper("log");
	private BmtLogger() {}

	public static BmtLogger instance() { 
		return instance; 
	}

	/**
	 * add info level logInfo
	 * @param who
	 * @param why
	 */
	public void log(String why, Object...args){
		log.info(why, args);
	}
	public void log(Throwable e, String why, Object...args){
		log.error(e, why, args);
	}
	/**
	 * add info level logInfo using LogLevel
	 * @param why
	 * @param level
	 */


	/**
	 * add a logInfo at level<code>lev</code>
	 * @param who
	 * @param why
	 * @param lev
	 */
	public void log(LogLevel lev, String why, Object...args){
		switch(lev.lev){
		case LogLevel.intDebug:
			log.debug(why, args);
			break;
		case LogLevel.intInfo:
			log.info(why, args);
			break;
		case LogLevel.intWarn:
			log.warn(why, args);
			break;
		case LogLevel.intError:
			log.error(why, args);
			break;
		case LogLevel.intFatal:
			log.fatal(why, args);
			break;
		}
	}


}


