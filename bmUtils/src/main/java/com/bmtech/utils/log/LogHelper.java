package com.bmtech.utils.log;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.bmtech.utils.Misc;
import com.bmtech.utils.SysPropConfig;

public class LogHelper {
	static {
		String pth = System.getProperty("log4jConfig");
		File cfgFile = null;
		if (pth != null) {
			cfgFile = new File(pth);
		}
		if (cfgFile == null || !cfgFile.exists()) {
			cfgFile = new File("config/log4j.properties");
			if (!cfgFile.exists()) {
				cfgFile = new File("log4j.properties");
			}
		}
		PropertyConfigurator.configureAndWatch(cfgFile.getAbsolutePath(), 10000);
	}
	public static final LogHelper log = new LogHelper("log");
	public final Logger logger;
	public final String logName;
	private static boolean printException = SysPropConfig.instance.getBoolean("printException", true);

	public LogHelper(String logName) {
		this.logName = logName;
		logger = Logger.getLogger(logName);
	}

	public LogHelper(Object forObj) {
		this(forObj == null ? "null" : forObj.getClass().getSimpleName());
	}

	public static void iLog(String why, Object... args) {
		log.log(why, args);
	}

	/**
	 * add a logInfo at level<code>lev</code>
	 * 
	 * @param who
	 * @param why
	 * @param lev
	 */
	public static void iInfo(String why, Object... args) {
		log.info(why, args);
	}

	public static void iDebug(String why, Object... args) {
		log.debug(why, args);
	}

	public static void iWarn(String why, Object... args) {
		log.warn(why, args);
	}

	public static void iError(String why, Object... args) {
		log.error(why, args);
	}

	public static void iWarn(Throwable e, String why, Object... args) {
		log.warn(e, why, args);
	}

	public static void iError(Throwable e, String why, Object... args) {
		log.error(e, why, args);
	}

	public static void iFatal(Throwable e, String why, Object... args) {
		log.fatal(e, why, args);
	}

	public static void iFatal(String why, Object... args) {
		log.fatal(why, args);
	}

	public void log(String why, Object... args) {
		info(why, args);
	}

	/**
	 * add a logInfo at level<code>lev</code>
	 * 
	 * @param who
	 * @param why
	 * @param lev
	 */
	public void info(String why, Object... args) {
		if (logger.isInfoEnabled()) {
			logger.info(Misc.format(why, args));
		}
	}

	public void debug(String why, Object... args) {
		if (logger.isDebugEnabled()) {
			logger.debug(Misc.format(why, args));
		}
	}

	public void warn(String why, Object... args) {
		logger.warn(Misc.format(why, args));
	}

	public void error(String why, Object... args) {
		logger.error(Misc.format(why, args));
	}

	public void warn(Throwable e, String why, Object... args) {
		logger.warn(Misc.format(why, args) + ", Exception = " + e);
		checkAndPrintException(e);
	}

	public void error(Throwable e, String why, Object... args) {
		logger.error(Misc.format(why, args) + ", Exception = " + e);
		checkAndPrintException(e);
	}

	public void fatal(Throwable e, String why, Object... args) {
		logger.fatal(Misc.format(why, args) + ", Exception = " + e);
		checkAndPrintException(e);
	}

	public void fatal(String why, Object... args) {
		logger.fatal(Misc.format(why, args));
	}

	public void checkAndPrintException(Throwable t) {
		if (logger.isDebugEnabled() || isPrintException()) {
			System.out.println(Misc.printErrorAsString(t));
		}
	}

	public static boolean isPrintException() {
		return printException;
	}

	public static void setPrintException(boolean printException) {
		LogHelper.printException = printException;
	}

	public static void i(String why, Object... args) {
		iInfo(why, args);
	}

	public static void d(String why, Object... args) {
		iDebug(why, args);
	}

	public static void w(String why, Object... args) {
		iWarn(why, args);
	}

	public static void e(String why, Object... args) {
		iError(why, args);
	}

	public static void w(Throwable e, String why, Object... args) {
		iWarn(e, why, args);
	}

	public static void e(Throwable e, String why, Object... args) {
		iError(e, why, args);
	}

	public static void f(Throwable e, String why, Object... args) {
		iFatal(e, why, args);
	}

	public static void f(String why, Object... args) {
		iFatal(why, args);
	}

	public static void main(String[] args) {
		L.f(new RuntimeException(), "afa");
	}
}
