package com.bmtech.utils.log;


/**
 * LogLevel. Denotes the level of the loginfo
 * @author beiming
 *
 */

public class LogLevel{
	public static  final int intDebug = 1;
	public static final int intInfo = 2;
	public static final int intWarn = 3;
	public static final int intError = 4;
	public static final int intFatal = 5;
	final int lev;
	private LogLevel(int lev)	{
		this.lev = lev;
	}

	public static final  LogLevel Debug = new LogLevel(intDebug);
	public static final  LogLevel Info = new LogLevel(intInfo);
	public static final  LogLevel Warning = new LogLevel(intWarn);
	public static final  LogLevel Error = new LogLevel(intError);
	public static final  LogLevel Fatal = new LogLevel(intFatal);
}