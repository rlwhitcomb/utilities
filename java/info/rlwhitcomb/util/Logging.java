/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2008-2021 Roger L. Whitcomb.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Logging Utility Module
 *
 * Change History:
 *  31-Jul-2008 (rlwhitcomb)
 *    Initial coding.
 *  06-Jan-2009 (rlwhitcomb)
 *    Add Javadoc comments.
 *  06-Feb-2009 (rlwhitcomb)
 *    Added a different flavor of "Error" that takes a single
 *    ErrorHandler error code and logs the associated error text.
 *  03-Mar-2010 (rlwhitcomb)
 *    Changed to use varargs on the format methods to make string
 *    formatting simpler for our callers.
 *  30-Mar-2010 (rlwhitcomb)
 *    Moved to info.rlwhitcomb.util package; added second flavor of
 *    Except() method to add explanation text; added readConfiguration
 *    method to load settings from an InputStream (formatted as a
 *    .properties file).
 *  26-Jul-2010 (rlwhitcomb)
 *    Cleaned up Javadoc links to packages we don't have in this more
 *    generic version; add concept of an instance Logging object that
 *    is related to a class (or an arbitrary identifying string).
 *  26-Mar-2011 (rlwhitcomb)
 *    Fix "Except" and "except" in case the exception has no message.
 *  12-Apr-2011 (rlwhitcomb)
 *    Add overload to "readConfiguration" to read from a File.
 *  13-Jun-2011 (rlwhitcomb)
 *    Better fix for "except" and "Except" if the exception has no
 *    message (things like NullPointerException and EOFException where
 *    the exception name itself IS the message).
 *  25-Jul-2011 (rlwhitcomb)
 *    Changed Exception to Throwable for new Pivot API change compatibility.
 *  06-Oct-2011 (rlwhitcomb)
 *    Add capability to do environment variable substitutions when reading
 *    the configuration file; changed class naming to use simple name instead
 *    of canonical name; some code rearranging to reduce redundancy; reformat
 *    messages a little bit (add thread id).
 *  30-Nov-2011 (rlwhitcomb)
 *    Add symbol map to the "readConfiguration" calls to allow overrides of
 *    the environment values if desired.
 *  15-Dec-2011 (rlwhitcomb)
 *    Add possibility of a list of alternative symbol names.
 *  30-Jan-2012 (rlwhitcomb)
 *    Add GMT designation to timestamp so anyone looking at the log file will
 *    know what the TZ is.
 *  02-Feb-2012 (rlwhitcomb)
 *    Values from symbol table weren't being found because I used the wrong
 *    string for the key lookup.
 *  11-May-2012 (rlwhitcomb)
 *    Moved the "substituteEnvValue" method into CharUtil so others can use it.
 *  12-Nov-2012 (rlwhitcomb)
 *    Make the imports more explicit.
 *  04-Apr-2013 (rlwhitcomb)
 *    Make available a utility function to format a timestamp.
 *  07-May-2013 (rlwhitcomb)
 *    Make a non-level-based logging function that can be used for start/stop
 *    notifications, which should come out no matter what level of logging is used.
 *  17-May-2013 (rlwhitcomb)
 *    Make some changes to support use with a Commons Logging Adapter (separate
 *    class), basically by recognizing their properties in the configuration file,
 *    by changing the "isXXXEnabled" methods from static to object, and by making
 *    some private variables protected in order to make subclasses of this class.
 *  20-Aug-2013 (rlwhitcomb)
 *    Add "setLoggingLevel(int)" method for convenience; so we can use the DEBUG..OFF
 *    constants.
 *  04-Feb-2014 (rlwhitcomb)
 *    Add convenience method to format a password in a secure way.
 *  14-Mar-2014 (rlwhitcomb)
 *    Don't log the exception stack trace if ERROR level isn't enabled.  Delay creating
 *    log file until actually needed.  Synchronize only once per statement on the static lock.
 *  14-Aug-2014 (rlwhitcomb)
 *    Cleanup "lint" warnings.
 *  26-Mar-2015 (rlwhitcomb)
 *    Make sure that this works to just log to the Java Console (that is "System.out") in an
 *    applet environment (by not setting a LogDirectory or LogFile in the configuration file).
 *  23-Apr-2015 (rlwhitcomb)
 *    Using logs generated by server events from various processes can get
 *    intermingled, making it difficult to figure out which process is doing what.  So
 *    capture the process ID at startup and log that along with the thread ID.
 *  08-Oct-2015 (rlwhitcomb)
 *    Address Javadoc warnings found by Java 8.
 *  18-Feb-2016 (rlwhitcomb)
 *    Add an instance method to log pertinent JVM information (such as version, vendor,
 *    classpath, etc.) at a specified logging level.
 *  19-Feb-2016 (rlwhitcomb)
 *    Add a bunch of configuration stuff to support log file rotation (also see the
 *    "logging.properties" file for the server).  Implement the logic that
 *    also interacts with the existing daily rolling file logic.  Do the rotation in
 *    a separate thread.  Refactor a number of methods into "switch" statements instead
 *    of nested if-else chains (generally more efficient); and switch on Strings also.
 *  01-Mar-2016 (rlwhitcomb)
 *    Fix new Javadoc warnings introduced by the last change.
 *  22-Feb-2017 (rlwhitcomb)
 *    Rename the CharUtil method to "substituteEnvValues".
 *  24-Aug-2017 (rlwhitcomb)
 *    Abstract out a new method to validate a logging level.
 *  29-Jun-2018 (rlwhitcomb)
 *    By default write log files in UTF-8 to support multiple translations, but allow
 *    the charset to be changed in the logging configuration.
 *  15-Mar-2019 (rlwhitcomb)
 *    Don't use FileInputStream/FileOutputStream due to GC problems b/c of the finalize
 *    method in these classes.
 *  10-Mar-2020 (rlwhitcomb)
 *    Prepare for GitHub.
 *    Use lambdas for Runnable instances.
 *  21-Dec-2020 (rlwhitcomb)
 *    Update obsolete Javadoc constructs.
 *  16-Feb-2021 (rlwhitcomb)
 *    Read a default configuration if none has been read previously.
 */
package info.rlwhitcomb.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.*;


/**
 * Logging subsystem to generate operational logs.
 * <p> This logging facility supports logging levels (5 levels from "FATAL" to "DEBUG") that
 * will log various amounts of stuff depending on the level.  Most of the things about logging
 * are set via a configuration file which is read by the {@link #readConfiguration} method.
 * <p> All logging can be done to a single file, or to a daily rolling log file and can optionally
 * be echoed to the console.
 * <p> There is also a {@link LogStream} class that writes to the logging system and can be used
 * when a {@link PrintStream} object is necessary.
 * <p> I made a command decision to always use GMT for the time so (potentially) log files from
 * around the world (if that ever happens) can be compared directly.  This does necessitate a
 * timezone conversion for looking at the logs on a single machine.  There is a tradeoff here.
 * But experience has shown that having client logs directly comparable to server logs is a good
 * thing.
 */
public class Logging
{
    /** Highest logging level that produces the most verbose output.  Useful for debugging purposes. */
    public static final int DEBUG = 5;
    /** Second highest logging level that produces informational output.  Can be used to trace basically
     * what happened during each user session. */
    public static final int INFO = 4;
    /** Medium logging level that logs all errors and warnings that occur.  Much less verbose than either
     * {@link #INFO} or {@link #DEBUG} and would only be useful in a full production system where you
     * only need to know about things like hacking attempts and other warning type of events. */
    public static final int WARN = 3;
    /** Error level logging only.  Warnings are ignored and only actual errors (which would be things like
     * network failures, missing files, and the like) would be recorded. */
    public static final int ERROR = 2;
    /** Fatal error level logging.  The only reason to set this level would be if you don't really care what
     * is happening in the system, and only need to know when it keels over and dies. :'( */
    public static final int FATAL = 1;
    /** Logging is off.  Use this level to disable all logging. */
    public static final int OFF = 0;

    /** Log file rotation value for "none". */
    public static final int NONE = 0;
    /** Log file rotation value for "daily" rotation. */
    public static final int DAILY = 1;
    /** Log file rotation value for "weekly" rotation. */
    public static final int WEEKLY = 2;
    /** Log file rotation value for "monthly" rotation. */
    public static final int MONTHLY = 3;

    /** String value of the {@link #DEBUG} level.
     * This string would be used in the .properties file to set DEBUG level. */
    private static final String LEVEL_DEBUG = "DEBUG";
    /** String value of the {@link #INFO} level.
     * This string would be used in the .properties file to set INFO level. */
    private static final String LEVEL_INFO = " INFO";
    private static final String LEVEL_INFO_INPUT = "INFO";
    /** String value of the {@link #WARN} level.
     * This string would be used in the .properties file to set WARN level. */
    private static final String LEVEL_WARN = " WARN";
    private static final String LEVEL_WARN_INPUT = "WARN";
    /** String value of the {@link #ERROR} level.
     * This string would be used in the .properties file to set ERROR level. */
    private static final String LEVEL_ERROR = "ERROR";
    /** String value of the {@link #FATAL} level.
     * This string would be used in the .properties file to set FATAL level. */
    private static final String LEVEL_FATAL = "FATAL";
    /** String value of the {@link #OFF} level.
     * This string would be used in the .properties file to set OFF level (that is, to turn logging off). */
    private static final String LEVEL_OFF = "  OFF";
    private static final String LEVEL_OFF_INPUT = "OFF";

    /** This string would be used in the .properties files to set {@link #NONE} for the log file rotation. */
    private static final String ROTATE_NONE = "none";
    /** This string would be used in the .properties files to set {@link #DAILY} log file rotation. */
    private static final String ROTATE_DAILY = "daily";
    /** This string would be used in the .properties files to set {@link #WEEKLY} log file rotation. */
    private static final String ROTATE_WEEKLY = "weekly";
    /** This string would be used in the .properties files to set {@link #MONTHLY} log file rotation. */
    private static final String ROTATE_MONTHLY = "monthly";

    /** The current level of logging that is enabled. Set by the {@link #setLoggingLevel setLoggingLevel} method. */
    private static int loggingLevel = FATAL;
    /** Flag to say whether to echo all logging to the console. Set by the {@link #setConsoleLogging setConsoleLogging} method. */
    protected static boolean logToConsole = false;
    /** Shortcut flag to say we are (also) logging to a log file (see {@link #logFileDir} and {@link #logFileTemplate}). */
    protected static boolean logToFile = false;

    /** Presence of this token in the log file name means
     * the file should rollover every day to a new name,
     * depending on the other log file rotation values.
     * <p> This token is replaced in the file name by the
     * actual date in the form of YYYY-MM-DD. */
    protected static final String DATE_TOKEN = "{DATE}";

    /** The "Etc/GMT" timezone value. */
    private static TimeZone gmt = null;
    /** The current calendar used to format a rolling log file name for today. */
    private static Calendar currentCal = null;
    /** The {@link SimpleDateFormat} used for date/time inside the log file. */
    private static DateFormat fmt = null;
    /** The {@link SimpleDateFormat} used to format the daily rolling part of log file names. */
    private static DateFormat dayfmt = null;

    /** Our current process ID.  Captured during static initialization. */
    private static long PID = 0L;

    /** Directory for the log file(s).  Normally set by the {@link #LOG_DIRECTORY} value. */
    protected static String logFileDir = null;
    /** Log file name or name template.  Normally set by the {@link #LOG_FILE} value. */
    protected static String logFileTemplate = null;
    /** Whether the file name template contains the <code>{DATE}</code> token (affects the way
     * log file rotation naming works. */
    protected static boolean fileHasDateTemplate = false;
    /** Whether or not log file rotation is enabled. */
    protected static boolean logFileRotation = false;
    /** What the log file rotation interval should be. */
    protected static int logFileInterval = NONE;
    /** If there is a limit for the log file size before doing a rotation. */
    protected static long logFileMaxSize = 0L;
    /** The number of rotated log files to keep. */
    protected static int logFileKeep = 5;
    /** Whether or not to compress the log files as we rotate them out. */
    protected static boolean logFileCompress = false;
    /** Name of the charset used to encode the log file. */
    protected static String logFileCharsetName = "UTF-8";
    /** The extension we add for compress backup files (compatible with "gzip" and "gunzip"). */
    private static final String GZIP_EXT = FileUtilities.COMPRESS_EXT;

    /** This is the actual {@link File} object that we're logging to currently
     * (mostly saved because we need to check file sizes if we're doing rotation). */
    private static File currentLogFile = null;
    /** This is the actual stream used to do the logging. */
    private static PrintStream logStrm = null;
    /** The lock used to synchronize access to the logging stream. */
    private static ReentrantLock lock = new ReentrantLock();
    /** The background thread in which we do log file rotation. */
    private static ExecutorService logFileRotationService = null;

    /** Our class name for use in identifying appropriate configuration settings. */
    protected static String loggingClassName = Logging.class.getName() + ".";
    /** The Commons Logging package name for identifying their configuration settings. */
    private static String commonsPackageName = "org.apache.commons.logging";
    /** Configuration value to set logging level. */
    protected static final String LOGGING_LEVEL = "LoggingLevel";
    /** Configuration value to set log file directory. */
    protected static final String LOG_DIRECTORY = "LogDirectory";
    /** Configuration value to set log file name or pattern. */
    protected static final String LOG_FILE = "LogFile";
    /** Configuration value to decide whether logging should be echoed to the console. */
    protected static final String LOG_TO_CONSOLE = "LogToConsole";
    /** Configuration value to specify log file rotation. */
    protected static final String LOG_FILE_ROTATE = "LogFileRotate";
    /** Configuration value to specify the log file rotation interval. */
    protected static final String LOG_FILE_INTERVAL = "LogFileInterval";
    /** Configuration value to specify log file maximum size before rotating. */
    protected static final String LOG_FILE_MAX_SIZE = "LogFileMaxSize";
    /** Configuration value to specify the number of log files to keep when rotating. */
    protected static final String LOG_FILE_KEEP = "LogFileKeep";
    /** Configuration value to specify log file maximum size before rotating. */
    protected static final String LOG_FILE_COMPRESS = "LogFileCompress";
    /** Configuration value to specify the log file character set name. */
    protected static final String LOG_FILE_CHARSET_NAME = "LogFileCharsetName";
    /** Maximum file size to allow in the max size value. */
    private static final long MAX_MAX_FILE_SIZE = 4_294_967_296L;
    /** Maximum number of log files to allow. */
    private static final int MAX_MAX_FILES_KEPT = 32_768;
    /** Pattern to match comment lines in a configuration file. */
    private static Pattern COMMENTS = Pattern.compile("^\\s*[#!].*");
    /** Pattern to parse a configuration line into 'name' '=' 'value' parts (or ':' as in {@link Properties} class). */
    private static Pattern PARSER = Pattern.compile("^\\s*(\\S+)\\s*[=:]\\s*(.*)$");

    /** Name of the default logging configuration file, if the application doesn't have one. */
    private static final String DEFAULT_LOGGING_CONFIGURATION = "logging_default.properties";
    /** Flag to say whether {@link #readConfiguration} has ever been called. */
    private static boolean configurationLoaded = false;

    /** The one and only instance variable -- used to identify an instance {@link Logging} object. */
    private String prefix = null;


    static {
	gmt = TimeZone.getTimeZone("Etc/GMT");
	fmt = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss.SSS z");
	dayfmt = new SimpleDateFormat("yyyy-MM-dd");
	currentCal = Calendar.getInstance(gmt);
	fmt.setCalendar(currentCal);
	dayfmt.setCalendar(currentCal);
	PID = Environment.getProcessID();
	Runtime.getRuntime().addShutdownHook(new Thread(() -> stopLogging(true)));
    }


    /**
     * Construct an instance of a Logging object for use by the given class.
     * <p> Will set the {@link #prefix} to the simple class name.
     *
     * @param	clazz	Class object that will be using this object.
     */
    public Logging(Class<?> clazz) {
	this(clazz.getSimpleName());
    }


    /**
     * Construct an instance of a Logging object for use with the given prefix.
     * <p> Will set the {@link #prefix} to the given string.
     *
     * @param	ident	Identifying string used for all logging output from
     *			this {@link Logging} object.
     */
    public Logging(String ident) {
	prefix = ident;
	readDefaultConfigurationIfNeeded();
    }


    /**
     * A logging interface to log something unconditionally in an instance context.
     *
     * @param	fmt	whatever it is you want to log; will have {@link #prefix} prepended
     * @param	args	arguments to substitute for the %nn$f placeholders in the fmt string
     */
    public void log(String fmt, Object ... args) {
	LogStream(System.out, -1, prefix, String.format(fmt, args), logToConsole);
    }

    /**
     * The normal logging interface used for non-error log statements in an instance
     * context.
     * <p> If the given level logging is enabled, call {@link #LogStream LogStream}
     * passing the {@link System#out} value for console echoing.
     *
     * @param	level	normally one of the {@link #DEBUG} to {@link #WARN} values
     * @param	fmt	whatever it is you want to log; will have {@link #prefix} prepended
     * @param	args	arguments to substitute for the %nn$f placeholders in the fmt string
     */
    public void log(int level, String fmt, Object ... args) {
	if (loggingLevel >= level) {
	    LogStream(System.out, level, prefix, String.format(fmt, args), logToConsole);
	}
    }

    /**
     * The logging interface used for error log statements in an instance context.
     * <p> If the given level logging is enabled, call {@link #LogStream LogStream}
     * passing the {@link System#err} value for console echoing.
     * <p> Passes {@code true} for the "logConsole" value so that these statements
     * will always echo to the console regardless of the
     * {@link #logToConsole} setting.
     *
     * @param	level	usually one of {@link #ERROR} to {@link #FATAL} values
     * @param	fmt	the error message you want to log
     * @param	args	arguments to substitute for the %nn$f placeholders in the fmt string
     */
    public void logError(int level, String fmt, Object ... args) {
	if (loggingLevel >= level) {
	    LogStream(System.err, level, prefix, String.format(fmt, args), true);
	}
    }


    /**
     * Log a {@link #DEBUG} level statement in an instance context.
     *
     * @param	fmt	what you want to log if <code>DEBUG</code> logging is enabled
     * @param	args	arguments to substitute for the %nn$f placeholders in the fmt string
     */
    public void debug(String fmt, Object ... args) {
	log(DEBUG, fmt, args);
    }

    /**
     * Log an {@link #INFO} level statement.
     *
     * @param	fmt	what you want to log if <code>INFO</code> logging is enabled
     * @param	args	arguments to substitute for the %nn$f placeholders in the fmt string
     */
    public void info(String fmt, Object ... args) {
	log(INFO, fmt, args);
    }

    /**
     * Log a {@link #WARN} level statement in an instance context.
     *
     * @param	fmt	what you want to log if <code>WARN</code> logging is enabled
     * @param	args	arguments to substitute for the %nn$f placeholders in the fmt string
     */
    public void warn(String fmt, Object ... args) {
	log(WARN, fmt, args);
    }


    /**
     * Log an {@link #ERROR} level statement in an instance context.
     *
     * @param	fmt	what you want to log if <code>ERROR</code> logging is enabled
     * @param	args	arguments to substitute for the %nn$f placeholders in the fmt string
     */
    public void error(String fmt, Object ... args) {
	logError(ERROR, fmt, args);
    }

    /**
     * Log a {@link #FATAL} level statement in an instance context.
     *
     * @param	fmt	what you want to log if <code>FATAL</code> logging is enabled
     * @param	args	arguments to substitute for the %nn$f placeholders in the fmt string
     */
    public void fatal(String fmt, Object ... args) {
	logError(FATAL, fmt, args);
    }

    /**
     * Log an exception in an instance context.
     * <p> This does an {@link #error error} log of the exception string,
     * then does the standard exception stack trace dump to the {@link #logStrm}.
     *
     * @param	e	any {@link Throwable} object
     */
    public void except(Throwable e) {
	if (loggingLevel >= ERROR) {
	    lock.lock();
	    try {
		String exceptClassName = e.getClass().getName();
		String exceptMsg = e.getMessage();
		if (exceptMsg == null)
		    logError(ERROR, exceptClassName);
		else
		    logError(ERROR, "%1$s: %2$s", exceptClassName, exceptMsg);
		if (logStrm != null) {
		    e.printStackTrace(logStrm);
		}
	    }
	    finally {
		lock.unlock();
	    }
	}
    }


    /**
     * Log an exception along with an explanation in an instance context.
     * <p> This does an {@link #error error} log of the explanation plus the
     * exception string, then does the standard exception stack trace dump
     * to the {@link #logStrm}.
     *
     * @param	expl	explanation text (prepended to exception string)
     * @param	e	any {@link Throwable} object
     */
    public void except(String expl, Throwable e) {
	if (loggingLevel >= ERROR) {
	    lock.lock();
	    try {
		String exceptClassName = e.getClass().getName();
		String exceptMsg = e.getMessage();
		logError(ERROR, "%1$s: %2$s", expl, (exceptMsg == null) ? exceptClassName : exceptMsg);
		if (logStrm != null) {
		    e.printStackTrace(logStrm);
		}
	    }
	    finally {
		lock.unlock();
	    }
	}
    }


    /**
     * Validate the given logging level to make sure it is defined.
     *
     * @param	level	An integer in the range of 0-5 (corresponding
     *			to the OFF..DEBUG values).
     * @return		{@code true} if the level is valid (that is,
     *			in the OFF..DEBUG range), or {@code false}
     *			if not.
     */
    public static boolean isLoggingLevelValid(int level) {
	switch (level) {
	    case OFF:
	    case FATAL:
	    case ERROR:
	    case WARN:
	    case INFO:
	    case DEBUG:
		return true;
	    default:
		break;
	}
	return false;
    }

	
    /**
     * Set the current logging level using our defined values.
     *
     * @param	level	An integer in the range of 0-5 (corresponding
     *			to the OFF..DEBUG values).
     * @return		{@code true} if the level is set, {@code false} if the
     *			given level is invalid.
     */
    public static boolean setLoggingLevel(int level) {
	if (isLoggingLevelValid(level)) {
	    loggingLevel = level;
	    return true;
	}
	System.err.println("Invalid value for logging level: " + level);
	return false;
    }


    /**
     * Set the current logging level.
     * <p> This is normally called by the {@link #readConfiguration}
     * method passing the value in the .properties file to set the initial value.
     * <p> A numeric value can also be entered with a value from "0" to "5" which will be interpreted
     * the same as the numeric values of the {@link #OFF} to {@link #DEBUG} constants.
     * <p> It can, however, also be called at any time to change the level, then all subsequent
     * logging statements will respect that new level.
     *
     * @param	level	one of the {@link #LEVEL_DEBUG} through {@link #LEVEL_OFF} strings,
     *			or a number string with a value from "0" to "5"
     * @return		<code>true</code> if set operation was successful
     *			(basically that the level was a correct value), or
     *			<code>false</code> if the level was not a good value.
     */
    public static boolean setLoggingLevel(String level) {
	try {
	    int lev = Integer.parseInt(level);
	    if (isLoggingLevelValid(lev)) {
		loggingLevel = lev;
	    }
	    else {
		System.err.println("Invalid format for logging level value: " + level);
		return false;
	    }
	}
	catch (NumberFormatException nfe) {
	    switch (level.toUpperCase()) {
		case LEVEL_DEBUG:
		    loggingLevel = DEBUG;
		    break;
		case LEVEL_INFO_INPUT:
		    loggingLevel = INFO;
		    break;
		case LEVEL_WARN_INPUT:
		    loggingLevel = WARN;
		    break;
		case LEVEL_ERROR:
		    loggingLevel = ERROR;
		    break;
		case LEVEL_FATAL:
		    loggingLevel = FATAL;
		    break;
		case LEVEL_OFF_INPUT:
		    loggingLevel = OFF;
		    break;
		default:
		    System.err.println("Invalid format for logging level value: " + level);
		    return false;
	    }
	}
	return true;
    }


    /**
     * Set the flag to echo the log to the console (or not).
     * <p> Normally, the code in {@link #readConfiguration}
     * will call this with the value in the .properties file, but
     * it also can be called at any time to enable/disable echo.
     * <p> Sets the private static {@link #logToConsole} flag which will normally
     * be passed to the {@link #LogStream LogStream} method.
     *
     * @param	logConsole	<code>true</code> to echo logging to the console,
     *				<code>false</code> not to
     */
    public static void setConsoleLogging(boolean logConsole) {
	logToConsole = logConsole;
    }


    /**
     * Return the {@link String} representation of the given logging level.
     * <p> An unknown / invalid constant sent in will produce an empty string out.
     *
     * @param	level	One of the {@link #DEBUG} through {@link #OFF} logging levels.
     * @return		Corresponding string value.
     */
    public static String getLoggingLevel(int level) {
	// Ugly, but it works
	switch (level) {
	    case OFF:	return LEVEL_OFF;
	    case FATAL:	return LEVEL_FATAL;
	    case ERROR:	return LEVEL_ERROR;
	    case WARN:	return LEVEL_WARN;
	    case INFO:	return LEVEL_INFO;
	    case DEBUG:	return LEVEL_DEBUG;
	    default:	return "";
	}
    }


    /**
     * Return the {@link String} representation of the current logging level.
     * <p> Calls {@link #getLoggingLevel} with the current logging level.
     *
     * @return		String value of {@link #loggingLevel}
     */
    public static String getLoggingLevel() {
	return getLoggingLevel(loggingLevel);
    }


    /**
     * Determine if a given logging level is enabled.
     * <p> If the given level is less than or equal to
     * the current {@link #loggingLevel} return {@code true}.
     * <p> The purpose of this check is to skip expensive
     * processing used solely for logging purposes if the
     * result won't show up in the logging anyway.
     *
     * @param	level	one of the {@link #DEBUG} to {@link #OFF} values
     * @return		{@code true} if logging output with the given
     *			level will be actually printed or {@code false}
     *			if it will not show up.
     */
    public static boolean isLevelEnabled(int level) {
	switch (level) {
	    case OFF:
	    case FATAL:
	    case ERROR:
	    case WARN:
	    case INFO:
	    case DEBUG:
		return loggingLevel >= level;
	}
	return false;
    }


    /**
     * Check if {@link #WARN} level logging is enabled.
     *
     * @return          value from <code>isLevelEnabled(WARN)</code>
     */
    public boolean isWarnEnabled() {
	return isLevelEnabled(WARN);
    }

    /**
     * Check if {@link #INFO} level logging is enabled.
     *
     * @return          value from <code>isLevelEnabled(INFO)</code>
     */
    public boolean isInfoEnabled() {
	return isLevelEnabled(INFO);
    }

    /**
     * Check if {@link #DEBUG} level logging is enabled.
     *
     * @return          value from <code>isLevelEnabled(DEBUG)</code>
     */
    public boolean isDebugEnabled() {
	return isLevelEnabled(DEBUG);
    }

    /**
     * Check if {@link #ERROR} level logging is enabled.
     *
     * @return          value from <code>isLevelEnabled(ERROR)</code>
     */
    public boolean isErrorEnabled() {
	return isLevelEnabled(ERROR);
    }

    /**
     * Check if {@link #FATAL} level logging is enabled.
     *
     * @return          value from <code>isLevelEnabled(FATAL)</code>
     */
    public boolean isFatalEnabled() {
	return isLevelEnabled(FATAL);
    }

    /**
     * Check if logging is enabled at all.
     * <p> Calls {@link #isLevelEnabled} with the {@link #FATAL}
     * value which will be {@code true} if and only if any
     * logging will occur at all.
     *
     * @return		value from <code>isLevelEnabled(FATAL)</code>
     */
    public static boolean isLoggingEnabled() {
	return isLevelEnabled(FATAL);
    }


    /**
     * Format a timestamp into the given {@link StringBuffer}
     * (for use in other places).
     *
     * @param	buf	Where to append the text
     * @return		The {@link Calendar} object with the current time.
     */
    public static Calendar formatTime(StringBuffer buf) {
	lock.lock();
	try {
	    Calendar now = Calendar.getInstance(gmt);
	    fmt.format(now.getTime(), buf, new FieldPosition(DateFormat.DATE_FIELD));
	    // Strip off the extraneous "+00:00" from the time zone
	    int size = buf.length() - 6;
	    if (buf.charAt(size) == '+') {
		buf.setLength(size);
	    }
	    return now;
	}
	finally {
	    lock.unlock();
	}
    }


    /**
     * Log the given statement to the log stream.
     * <p> This is the guts of the logging process:
     * <ul>
     * <li>Roll over the log file name if the day has changed
     * (by calling {@link #setLogFile setLogFile} after setting the new
     * {@link #currentCal} value)
     * <li>Format the current time
     * <li>Add the current thread name
     * <li>Append the "level" string (if given)
     * <li>Finally add the given logging statement text
     * <li>If "logConsole" is true, stream the text to the given {@link PrintStream}
     * <li>Stream the text to the current {@link #logStrm}
     * </ul>
     *
     * @param	ps	The console <code>PrintStream</code> to use for echoing the logging
     * @param	level	String obtained from {@link #getLoggingLevel getLoggingLevel}
     * @param	prefix	The component or subsytem identifier for this log statement.
     * @param	stmt	Whatever it is you want to output to the log file
     * @param	logConsole	Normally obtained from {@link #logToConsole} value
     */
    public static void LogStream(PrintStream ps, int level, String prefix, String stmt, boolean logConsole) {
	lock.lock();
	try {
	    StringBuffer buf = new StringBuffer();
	    Calendar now = formatTime(buf);

	    // Make sure the log file is open or change name for log file rotation
	    if (logToFile)
		openLogFile(now);

	    // Don't bother formatting if there's no place to put it
	    if (logConsole || logStrm != null) {
		buf.append(" P(").append(PID).append(')');
		Thread curThread = Thread.currentThread();
		buf.append(" T[").append(curThread.getName()).append(']');
		buf.append('(').append(curThread.getId()).append(')');
		if (level >= OFF && level <= DEBUG)
		    buf.append(' ').append(getLoggingLevel(level));
		if (prefix != null && !prefix.isEmpty())
		    buf.append(' ').append(prefix);
		buf.append(": ").append(stmt);
		String output = buf.toString();
		if (logConsole) {
		    // Conceivably some other thread could be writing to stdout
		    // here, but we're not really concerned about the console
		    // output getting scrambled by multiple threads and no one
		    // else will be synchronizing on it anyway, so don't bother
		    ps.println(output);
		}
		if (logStrm != null) {
		    logStrm.println(output);
		}
		output = null;
	    }
	    buf = null;
	    now = null;
	}
	finally {
	    lock.unlock();
	}
    }


    /**
     * A logging interface to log something unconditionally in the global context.
     *
     * @param	fmt	whatever it is you want to log; will have {@link #prefix} prepended
     * @param	args	arguments to substitute for the %nn$f placeholders in the fmt string
     */
    public static void Log(String fmt, Object ... args) {
	LogStream(System.out, -1, null, String.format(fmt, args), logToConsole);
    }

    /**
     * The normal logging interface used for non-error log statements.
     * <p> If the given level logging is enabled, call {@link #LogStream LogStream}
     * passing the {@link System#out} value for console echoing.
     *
     * @param	level	normally one of the {@link #DEBUG} to {@link #WARN} values
     * @param	fmt	whatever it is you want to log
     * @param	args	arguments to substitute for the %nn$f placeholders in the fmt string
     */
    public static void Log(int level, String fmt, Object ... args) {
	if (loggingLevel >= level) {
	    LogStream(System.out, level, null, String.format(fmt, args), logToConsole);
	}
    }

    /**
     * The logging interface used for error log statements.
     * <p> If the given level logging is enabled, call {@link #LogStream LogStream}
     * passing the {@link System#err} value for console echoing.
     * <p> Passes {@code true} for the "logConsole" value so that these statements
     * will always echo to the console regardless of the
     * {@link #logToConsole} setting.
     *
     * @param	level	usually one of {@link #ERROR} to {@link #FATAL} values
     * @param	fmt	the error message you want to log
     * @param	args	arguments to substitute for the %nn$f placeholders in the fmt string
     */
    public static void LogError(int level, String fmt, Object ... args) {
	if (loggingLevel >= level) {
	    LogStream(System.err, level, null, String.format(fmt, args), true);
	}
    }


    /**
     * Log a {@link #DEBUG} level statement.
     *
     * @param	fmt	what you want to log if <code>DEBUG</code> logging is enabled
     * @param	args	arguments to substitute for the %nn$f placeholders in the fmt string
     */
    public static void Debug(String fmt, Object ... args) {
	readDefaultConfigurationIfNeeded();
	Log(DEBUG, fmt, args);
    }

    /**
     * Log an {@link #INFO} level statement.
     *
     * @param	fmt	what you want to log if <code>INFO</code> logging is enabled
     * @param	args	arguments to substitute for the %nn$f placeholders in the fmt string
     */
    public static void Info(String fmt, Object ... args) {
	readDefaultConfigurationIfNeeded();
	Log(INFO, fmt, args);
    }

    /**
     * Log a {@link #WARN} level statement.
     *
     * @param	fmt	what you want to log if <code>WARN</code> logging is enabled
     * @param	args	arguments to substitute for the %nn$f placeholders in the fmt string
     */
    public static void Warn(String fmt, Object ... args) {
	readDefaultConfigurationIfNeeded();
	Log(WARN, fmt, args);
    }


    /**
     * Log an {@link #ERROR} level statement.
     *
     * @param	fmt	what you want to log if <code>ERROR</code> logging is enabled
     * @param	args	arguments to substitute for the %nn$f placeholders in the fmt string
     */
    public static void Error(String fmt, Object ... args) {
	readDefaultConfigurationIfNeeded();
	LogError(ERROR, fmt, args);
    }

    /**
     * Log a {@link #FATAL} level statement.
     *
     * @param	fmt	what you want to log if <code>FATAL</code> logging is enabled
     * @param	args	arguments to substitute for the %nn$f placeholders in the fmt string
     */
    public static void Fatal(String fmt, Object ... args) {
	readDefaultConfigurationIfNeeded();
	LogError(FATAL, fmt, args);
    }

    /**
     * Log an exception.
     * <p> This does an {@link #Error Error} log of the exception string,
     * then does the standard exception stack trace dump to the {@link #logStrm}.
     *
     * @param	e	any {@link Throwable} object
     */
    public static void Except(Throwable e) {
	readDefaultConfigurationIfNeeded();
	if (loggingLevel >= ERROR) {
	    String exceptClassName = e.getClass().getName();
	    String exceptMsg = e.getMessage();
	    if (exceptMsg == null)
		LogError(ERROR, exceptClassName);
	    else
		LogError(ERROR, "%1$s: %2$s", exceptClassName, exceptMsg);
	    if (logStrm != null) {
		synchronized(logStrm) {
		    e.printStackTrace(logStrm);
		}
	    }
	}
    }


    /**
     * Log an exception along with an explanation.
     * <p> This does an {@link #Error Error} log of the explanation plus the
     * exception string, then does the standard exception stack trace dump
     * to the {@link #logStrm}.
     *
     * @param	expl	explanation text (prepended to exception string)
     * @param	e	any {@link Exception} object
     */
    public static void Except(String expl, Throwable e) {
	readDefaultConfigurationIfNeeded();
	if (loggingLevel >= ERROR) {
	    String exceptClassName = e.getClass().getName();
	    String exceptMsg = e.getMessage();
	    LogError(ERROR, "%1$s: %2$s", expl, (exceptMsg == null) ? exceptClassName : exceptMsg);
	    if (logStrm != null) {
		synchronized(logStrm) {
		    e.printStackTrace(logStrm);
		}
	    }
	}
    }


    /**
     * Calculate whether it is time to rotate the log files based on time or size criteria.
     *
     * @param	now	Current calendar date/time.
     * @return		{@code true} if the time or size criteria have been met to
     *			necessitate a log file rotation.
     */
    private static boolean isTimeToRotate(Calendar now) {
	boolean itsTime = false;
	switch (logFileInterval) {
	    case NONE:
		break;
	    case DAILY:
		itsTime = now.get(Calendar.DAY_OF_YEAR) != currentCal.get(Calendar.DAY_OF_YEAR);
		break;
	    case WEEKLY:
		itsTime = now.get(Calendar.WEEK_OF_YEAR) != currentCal.get(Calendar.WEEK_OF_YEAR);
		break;
	    case MONTHLY:
		itsTime = now.get(Calendar.MONTH) != currentCal.get(Calendar.MONTH);
		break;
	}

	// Next check is if we're still in the same year, and if not, IT'S TIME TO CHANGE
	// (note: we check this on the OFF chance that it's been a whole year since we last
	//  checked and the checks above MIGHT incorrectly decide false).
	if (!itsTime && logFileInterval != NONE && now.get(Calendar.YEAR) != currentCal.get(Calendar.YEAR)) {
	    itsTime = true;
	}

	// Finally, check if a size-based rotation is necessary
	if (!itsTime && currentLogFile != null && logFileMaxSize > 0L) {
	    itsTime = currentLogFile.length() >= logFileMaxSize;
	}
	return itsTime;
    }


    /**
     * Format the current log file name given the time/date information.
     * <p> Locks the reentrant lock in order to synchronize access to
     * the file name format object.
     *
     * @param	cal	The "current" calendar information.
     * @return		The formatted name of the log file.
     */
    private static String getLogFileName(Calendar cal) {
	lock.lock();
	try {
	    return fileHasDateTemplate
		? logFileTemplate.replace(DATE_TOKEN, dayfmt.format(cal.getTime()))
		: logFileTemplate;
	}
	finally {
	    lock.unlock();
	}
    }


    /**
     * Format a backup log file name taking into account whether they should be
     * compressed or not.  This is used for the date-based case.
     * <p> Locks the reentrant lock in order to synchronize access to
     * the file name format object.
     *
     * @param	cal	The "current" calendar information.
     * @param	n	Which edition of the backup file we want the name for.
     * @param	compress	Whether we want the compressed file name or not.
     * @return		The formatted name of the backup file.
     */
    private static String getBackupFileName(Calendar cal, int n, boolean compress) {
	lock.lock();
	try {
	    return String.format("%1$s.%2$d%3$s",
		(fileHasDateTemplate
			? logFileTemplate.replace(DATE_TOKEN, dayfmt.format(cal.getTime()))
			: logFileTemplate),
		n,
		compress ? GZIP_EXT : "");
	}
	finally {
	    lock.unlock();
	}
    }


    /**
     * Format a backup log file name taking into account whether they should be
     * compressed or not.  This is used for the non-date-based case.
     *
     * @param	n	Which edition of the backup file we want the name for.
     * @param	compress	Whether we want the compressed file name or not.
     * @return		The formatted name of the backup file.
     */
    private static String getBackupFileName(int n, boolean compress) {
	return String.format("%1$s.%2$d%3$s",
		logFileTemplate,
		n,
		compress ? GZIP_EXT : "");
    }


    /**
     * Actually create and open the file we're to log to.
     *
     * @param	now	Current calendar date/time (for use with log file rotation).
     */
    private static void openLogFile(Calendar now) {
	if (logStrm == null || (logFileRotation && isTimeToRotate(now))) {

	    Calendar oldCurrentCal = currentCal;
	    currentCal = now;

	    // Make sure the directory exists (or we create it)
	    File logDir = new File(logFileDir);
	    if (logDir.exists() || logDir.mkdirs()) {

		// Flush and close the current log file if already opened
		stopLogging(false);

		// If the criteria were met for rotating the log files, then do it now
		if (logStrm != null) {
		    startLogFileRotation(oldCurrentCal);
		}

		// Now do the {DATE} token substitution if needed on the file name
		String logFile = getLogFileName(currentCal);

		// Create the actual log file and the print stream for it
		currentLogFile = new File(logDir, logFile);
		try {
		    if (!currentLogFile.exists())
			currentLogFile.createNewFile();

		    logStrm = new PrintStream(
				Files.newOutputStream(currentLogFile.toPath(),
					StandardOpenOption.WRITE, StandardOpenOption.APPEND),
				true, logFileCharsetName);
		} catch (IOException ioe) {
		    System.err.println("IO Exception trying to create log file! " + ioe.toString());
		}
	    }
	}
    }

    /**
     * Set the logging directory and the log file name (and/or template).
     * <p> Makes sure the directory exists and takes care of implementing
     * the daily rolling log file by parsing the name for the {@link #DATE_TOKEN}
     * value.  If found, it will substitute the current date (formatted using
     * the {@link #dayfmt} formatter from the current day value in
     * {@link #currentCal} value).
     * <p> Once the directory and file name are determined, create that file
     * and the {@link PrintStream} object used to do the actual logging.
     *
     * @param	logDirectory	directory to place the log files (normally relative to application directory)
     * @param	logFile		name of log file (or usually a pattern)
     * @see	#DATE_TOKEN
     * @see	#fileHasDateTemplate
     */
    public static void setLogFile(String logDirectory, String logFile) {
	// Save these original values for daily rolling file
	logFileDir = logDirectory;
	logFileTemplate = logFile;

	if (logFileTemplate != null) {
	    // Check for the date substitution token in the template name.
	    // If it is present, and log file rotation is NOT enabled,
	    // then enable it and set to "daily", otherwise leave these
	    // settings as-is.
	    if (logFileTemplate.indexOf(DATE_TOKEN) >= 0) {
		fileHasDateTemplate = true;
		if (!logFileRotation) {
		    setLogFileRotation(true);
		    logFileInterval = DAILY;
		}
	    }
	    else if (logFileRotation && logFileInterval != NONE) {
		// Here, if we have specified date-based log file rotation, and the template
		// supplied does NOT have the date token in it, then put it at the end,
		// so that we ALWAYS have a date if we're doing date-based rotations
		// (this makes the log file rotation more uniform).
		logFileTemplate = logFile + "." + DATE_TOKEN;
		fileHasDateTemplate = true;
	    }

	    if (logFileDir != null) {
		logToFile = true;
	    }
	}
    }


    /**
     * Enables/disables automatic log file rotation.
     *
     * @param	rotate	Whether or not to rotate log files based on some time/size criteria.
     */
    public static void setLogFileRotation(boolean rotate) {
	logFileRotation = rotate;
	// If we will need a thread to do the rotating, then create the service now
	if (logFileRotation) {
	    logFileRotationService = Executors.newSingleThreadExecutor();
	}
    }


    /**
     * Set the log file rotation interval.
     *
     * @param	value	The desired interval value, one of {@link #ROTATE_DAILY},
     *			{@link #ROTATE_WEEKLY}, {@link #ROTATE_MONTHLY}, or
     *			{@link #ROTATE_NONE}, or the numeric equivalents (0, 1, 2, or 3).
     * @return		{@code true} if the value was acceptable, or {@code false} if not.
     */
    public static boolean setLogFileRotationInterval(String value) {
	try {
	    int interval = Integer.parseInt(value);
	    switch (interval) {
		case NONE:
		case DAILY:
		case WEEKLY:
		case MONTHLY:
		    logFileInterval = interval;
		    break;
		default:
		    System.err.println("Invalid value for log file rotation interval: " + value);
		    return false;
	    }
	}
	catch (NumberFormatException nfe) {
	    switch (value.toLowerCase()) {
		case ROTATE_NONE:
		    logFileInterval = NONE;
		    break;
		case ROTATE_DAILY:
		    logFileInterval = DAILY;
		    break;
		case ROTATE_WEEKLY:
		    logFileInterval = WEEKLY;
		    break;
		case ROTATE_MONTHLY:
		    logFileInterval = MONTHLY;
		    break;
		default:
		    System.err.println("Invalid value for log file rotation interval: " + value);
		    return false;
	    }
	}
	return true;
    }


    /**
     * Set the maximum log file size before it needs rotating.
     *
     * @param	value	A {@code long} value number of bytes for the size limit, where {@code 0L}
     *			means there is no limit.
     * @return		{@code false} if the value is out of a reasonable range (basically 2GB)
     *			(see {@link #MAX_MAX_FILE_SIZE}) or {@code true} if the value is acceptable.
     */
    public static boolean setLogFileMaxSize(long value) {
	if (value < 0L || value > MAX_MAX_FILE_SIZE) {
	    return false;
	}
	logFileMaxSize = value;
	return true;
    }


    /**
     * Sets the number of rotated log files to keep before discarding.
     *
     * @param	value	Zero means keep them all, any other number means that many
     *			files should be kept.
     * @return		{@code true} if the value is acceptable (up to a limit of
     *			about 32,000; see {@link #MAX_MAX_FILES_KEPT}).
     */
    public static boolean setLogFileKeep(int value) {
	if (value < 0 || value > MAX_MAX_FILES_KEPT) {
	    return false;
	}
	logFileKeep = value;
	return true;
    }


    /**
     * Sets the flag for whether to compress the log files as they are rotated.
     *
     * @param	compress	Whether or not to compress the rotated files.
     */
    public static void setLogFileCompress(boolean compress) {
	logFileCompress = compress;
    }


    /**
     * Sets the character set name to use for encoding the log file.  Default
     * is "UTF-8".
     *
     * @param	name	The name of a valid character set.
     * @return		{@code true} if the character set name is in fact valid,
     *			or {@code false} if it is not.
     */
    public static boolean setLogFileCharsetName(String name) {
	try {
	    Charset.forName(name);
	    logFileCharsetName = name;
	    return true;
	}
	catch (IllegalArgumentException ex) {
	    return false;
	}
    }


    /**
     * Load the default logging configuration from a default logging properties file
     * in our package directory.
     * <p> If the {@link #readConfiguration} method has already been called, then do nothing.
     * @see #DEFAULT_LOGGING_CONFIGURATION
     * @see #configurationLoaded
     */
    private static void readDefaultConfigurationIfNeeded() {
	lock.lock();
	try {
	    if (!configurationLoaded) {
		try (InputStream is = Logging.class.getResourceAsStream(DEFAULT_LOGGING_CONFIGURATION)) {
		    readConfiguration(is, null);
		}
		catch (IOException ioe) {
		    System.err.println("Unexpected I/O error reading default logging configuration: " + ExceptionUtil.toString(ioe));
		}
	    }
	}
	finally {
	    lock.unlock();
	}
    }

    /**
     * Sets configuration options from the given {@link InputStream} object
     * which should be a .properties file.
     *
     * @param	is	{@link InputStream} object to read the configuration from
     * @param	symbols	Alternate source of environment values if an override
     *			symbol is not available in the environment; can be {@code null}
     */
    public static void readConfiguration(InputStream is, Map<String,String> symbols) {
	lock.lock();
	try {
	    String logDir = null;
	    String logFile = null;
	    BufferedReader rdr = new BufferedReader(new InputStreamReader(is));
	    String line = null;

	    try {
		while ((line = rdr.readLine()) != null) {
		    Matcher m = COMMENTS.matcher(line);
		    if (m.matches())
			continue;
		    m = PARSER.matcher(line);
		    if (m.matches()) {
			String name = m.group(1);
			String value = CharUtil.substituteEnvValues(m.group(2), symbols);

			if (name.startsWith(loggingClassName)) {
			    name = name.substring(loggingClassName.length());
			    boolean valid = true;
			    try {
				if (name.equalsIgnoreCase(LOGGING_LEVEL)) {
				    valid = setLoggingLevel(value);
				}
				else if (name.equalsIgnoreCase(LOG_DIRECTORY)) {
				    logDir = value;
				}
				else if (name.equalsIgnoreCase(LOG_FILE)) {
				    logFile = value;
				}
				else if (name.equalsIgnoreCase(LOG_TO_CONSOLE)) {
				    setConsoleLogging(CharUtil.getBooleanValue(value));
				}
				else if (name.equalsIgnoreCase(LOG_FILE_ROTATE)) {
				    setLogFileRotation(CharUtil.getBooleanValue(value));
				}
				else if (name.equalsIgnoreCase(LOG_FILE_INTERVAL)) {
				    valid = setLogFileRotationInterval(value);
				}
				else if (name.equalsIgnoreCase(LOG_FILE_MAX_SIZE)) {
				    try {
					valid = setLogFileMaxSize(Long.parseLong(value));
				    }
				    catch (NumberFormatException nfe) {
					valid = setLogFileMaxSize(NumericUtil.convertKMGValue(value));
				    }
				}
				else if (name.equalsIgnoreCase(LOG_FILE_KEEP)) {
				    valid = setLogFileKeep(Integer.parseInt(value));
				}
				else if (name.equalsIgnoreCase(LOG_FILE_COMPRESS)) {
				    setLogFileCompress(CharUtil.getBooleanValue(value));
				}
				else if (name.equalsIgnoreCase(LOG_FILE_CHARSET_NAME)) {
				    valid = setLogFileCharsetName(value);
				}
			    }
			    catch (Throwable ex) {
				valid = false;
			    }
			    if (!valid) {
				System.err.format("Invalid value for %1$s: %2$s%n", name, value);
			    }
			}
			else if (name.startsWith(commonsPackageName)) {
			    System.setProperty(name, value);
			}
		    }
		}
	    } catch (IOException ioe) {
		System.err.format("I/O Exception reading configuration: %1$s!%n", ioe.toString());
	    }

	    if (logDir != null && logFile != null) {
		setLogFile(logDir, logFile);
	    }

	    configurationLoaded = true;
	}
	finally {
	   lock.unlock();
	}
    }


    /**
     * Sets configuration options from the given object
     * which should be either a {@link File} or a {@link String} pointing
     * to a plain .properties file.
     *
     * @param	obj	Object specifying where to read the configuration from:
     *			either a {@link File}, {@link String}, or other object
     *			whose <code>toString()</code> method will provide a file name
     *			which will be converted to a {@link Path} to be passed to
     *			the {@link Files#newInputStream} method.
     * @param	symbols	Alternate source of environment values if an override
     *			symbol is not available in the environment; can be {@code null}
     */
    public static void readConfiguration(Object obj, Map<String,String> symbols) {
	try {
	    Path path;
	    if (obj instanceof File)
		path = ((File)obj).toPath();
	    else if (obj instanceof String)
		path = Paths.get((String)obj);
	    else
		path = Paths.get(obj.toString());
	    try (InputStream fis = Files.newInputStream(path)) {
		readConfiguration(fis, symbols);
	    }
	    catch (IOException ioe) {
		System.err.format("Unable to read configuration from file %1$s: %2$s!%n", path.toString(), ioe.toString());
	    }
	}
	catch (InvalidPathException ipe) {
	    System.err.format("Cannot convert string to path (%1$s): %2$s!%n", obj.toString(), ipe.toString());
	}
    }


    /**
     * Stops logging.
     * <p> This is called as the last thing that happens during system shutdown, so that
     * all relevant shutdown actions are logged.
     * <p> Flushes and closes the {@link #logStrm} object.
     *
     * @param	shutdown	{@code true} if we shutting the whole application down, or {@code false}
     *				if we're just switching log files.
     */
    public static void stopLogging(boolean shutdown) {
	lock.lock();
	try {
	    if (logStrm != null) {
		logStrm.flush();
		logStrm.close();
	    }
	}
	finally {
	    lock.unlock();
	}
	if (shutdown && logFileRotationService != null) {
	    logFileRotationService.shutdown();
	    try {
		if (!logFileRotationService.awaitTermination(10, TimeUnit.SECONDS)) {
		    logFileRotationService.shutdownNow();
		    if (!logFileRotationService.awaitTermination(5, TimeUnit.SECONDS)) {
			System.err.println("Log file rotation did not terminate!");
		    }
		}
	    }
	    catch (InterruptedException ie) {
		logFileRotationService.shutdownNow();
		Thread.currentThread().interrupt();
	    }
	    finally {
		logFileRotationService = null;
	    }
	}
    }


    /**
     * Create a secure string as a way of logging a password.
     *
     * @param	password	The password we want to log (securely).
     * @return			A string suitable for logging this password value.
     */
    public static String loggablePassword(byte[] password) {
	if (password == null || password.length == 0)
	    return "<empty>";
	return String.format("[%1$d bytes]", password.length);
    }


    /**
     * Log one piece of JVM-related info, routed to the right downstream
     * logging method (depending on the leve).
     *
     * @param	level	The logging level for this information.
     * @param	format	The log message format (with embedded parameters).
     * @param	args	The arguments (if any) for the message template.
     */
    private void logJVMPiece(int level, String format, Object ... args) {
	if (level > ERROR) {
	    log(level, format, args);
	}
	else {
	    logError(level, format, args);
	}
    }


    /**
     * Log a bunch of JVM-related information, which may help debugging
     * configuration issues, classpath conflicts, etc.
     *
     * @param	level	The logging level to use for this information.
     */
    public void logJVMInfo(int level) {
	if (loggingLevel >= level) {
	    // The following mimics what "java -version" will give you
	    logJVMPiece(level, "%n========================%nJava version: \"%1$s\"%n%2$s (build %3$s)%n%4$s (build %5$s, %6$s)%nJava Home: %7$s%nJava Library Path: %8$s%nJava Classpath: %9$s%n========================", 
		System.getProperty("java.version"),
		System.getProperty("java.runtime.name"), System.getProperty("java.runtime.version"),
		System.getProperty("java.vm.name"), System.getProperty("java.vm.version"), System.getProperty("java.vm.info"),
		System.getProperty("java.home"),
		System.getProperty("java.library.path"),
		System.getProperty("java.class.path"));
	}
    }


    /**
     * Create a file name pattern that matches all the files in the current
     * directory and current log file template (with options for needing the
     * backup numbers).
     *
     * @param	needBackups	Whether we need to account for backup files.
     * @return	A pattern for file names (excluding the directory).
     */
    private static Pattern getFilterPattern(boolean needBackups) {
	return Pattern.compile(
		logFileTemplate.replace(DATE_TOKEN, "(\\d{4}-\\d{2}-\\d{2})")
			       .replace(".", "\\.") +
				(needBackups ? "(\\.\\d+)?" : "") +
				(logFileCompress ? "(\\" + GZIP_EXT + ")?": "") );
    }


    /**
     * Filter all log files in the current directory, using the current
     * flags, with an option to consider backup numbers or not.
     */
    private static class LogFileFilter implements FilenameFilter
    {
	private File parentDir;
	private Pattern fileNamePattern;

	public LogFileFilter(boolean needBackups) {
	    parentDir = new File(logFileDir);
	    fileNamePattern = getFilterPattern(needBackups);
	}

	@Override
	public boolean accept(File dir, String name) {
	    if (dir.equals(parentDir)) {
		return fileNamePattern.matcher(name).matches();
	    }
	    return false;
	}
    }


    /**
     * Do a rename on one of our log files.  No error, but return status
     * of whether the rename succeeded.
     *
     * @param	currentName	The current file name in the log directory.
     * @param	newName		The new name for the file.
     * @return	Whether or not the rename succeeded completely (will be
     *		{@code false} if the current file does not exist or the
     *		new file does exist prior to the rename.
     */
    private static boolean renameLogFile(String currentName, String newName) {
	File currentFile = new File(logFileDir, currentName);
	File newFile = new File(logFileDir, newName);

	if (currentFile.exists() && !newFile.exists()) {
	    try {
		FileUtilities.renameFile(currentFile, newName);
		return true;
	    }
	    catch (IOException ioe) {
		System.err.format("Unable to rename log file \"%1$s\" to new name \"%2$s\"%n",
			currentName, newName);
	    }
	}
	return false;
    }


    /**
     * Rename the latest current file and compress it if specified.
     *
     * @param	cal	The date value for the last current time.
     * @param	suffix	Used in the special case of the ".0" file.
     * @return		Whether or not the rename/compress succeeded.
     */
    private static boolean doCurrentFileRotation(Calendar cal, String suffix) {
	String currentName = getLogFileName(cal) + suffix;
	String backupName = getBackupFileName(cal, 1, false);
	String compressedBackupName = logFileCompress ? getBackupFileName(cal, 1, true) : backupName;

	File currentFile = new File(logFileDir, currentName);
	File backupFile = new File(logFileDir, backupName);
	File compressedBackupFile = logFileCompress ? new File(logFileDir, compressedBackupName) : backupFile;

	if (currentFile.exists() && !compressedBackupFile.exists()) {
	    try {
		FileUtilities.renameFile(currentFile, backupName);
		if (logFileCompress) {
		    if (Thread.currentThread().isInterrupted()) {
			return false;
		    }
		    FileUtilities.compressFile(backupFile);
		}
		return true;
	    }
	    catch (IOException ioe) {
		System.err.format("Unable to rename/compress current file \"%1$s\" to backup file \"%2$s\"%n",
			currentName, compressedBackupName);
	    }
	}
	return false;
    }


    /**
     * Log file name comparator.  Sorts ".2" file before ".1" which is before
     * the straight date file name, like this:<br>
     * <ul>
     * <li>2016-02-22.3.gz
     * <li>2016-02-22.2.gz
     * <li>2016-02-22.1.gz
     * <li>2016-02-22
     * <li>2016-02-23.1.gz
     * <li>2016-02-23
     * <li>2016-02-25.1.gz
     * <li>2016-02-25.0
     * <li>2016-02-25
     * </ul>
     */
    private static class LogComparator implements Comparator<String>
    {
	private Pattern pattern;

	public LogComparator() {
	    pattern = getFilterPattern(true);
	}

	@Override
	public int compare(String s1, String s2) {
	    Matcher m1 = pattern.matcher(s1);
	    Matcher m2 = pattern.matcher(s2);
	    if (m1.matches() && m2.matches()) {
		// group 1 = date, group 2 = backup number (if any)
		String date1 = m1.group(1);
		String date2 = m2.group(1);
		String backup1 = m1.group(2);
		String backup2 = m2.group(2);
		if (date1.equals(date2)) {
		    // Sort the backup values in reverse
		    if (backup2 == null && backup1 == null)
			return 0;
		    else if (backup2 == null && backup1 != null)
			return -1;
		    else if (backup2 != null && backup1 == null)
			return +1;
		    return backup2.compareTo(backup1);
		}
		return date1.compareTo(date2);
	    }
	    // If we can't detect a pattern, then just compare as strings
	    return s1.compareTo(s2);
	}
    }


    /**
     * Loop through the specified number of backup log files and rename them
     * to make room for the newly minted one.
     *
     * @param	cal	The calendar to use if date-based rotation is enabled
     *			(so can be {@code null}).
     * @param	numFiles	The number of files to work through.
     */
    private static void renameBackupFiles(Calendar cal, int numFiles) {
	for (int i = numFiles - 1; i >= 1; i--) {
	    String fileNameN_1 = cal == null ? getBackupFileName(i, logFileCompress)
					     : getBackupFileName(cal, i, logFileCompress);
	    String fileNameN = cal == null ? getBackupFileName(i + 1, logFileCompress)
					   : getBackupFileName(cal, i + 1, logFileCompress);
	    // Ignore failures here because there might not be
	    // that many files in existence yet.
	    // But, note: if only one rename failed for any other reason,
	    // the next one will fail also, so we will cascade down doing
	    // a lot of attempted renames that won't work...
	    renameLogFile(fileNameN_1, fileNameN);
	}
    }


    /**
     * Count the number of backup files in the log directory (according to the
     * current file name template).
     *
     * @param	cal	The backup calendar date (can be {@code null}).
     * @return		The number of backup files found.
     */
    private static int countBackupFiles(Calendar cal) {
	int numFiles = 1;
	for (;;) {
	    String name = cal == null ? getBackupFileName(numFiles, logFileCompress)
				      : getBackupFileName(cal, numFiles, logFileCompress);
	    if (!(new File(logFileDir, name).exists())) {
		break;
	    }
	    numFiles++;
	}
	return numFiles;
    }


    /** Singleton instance of this comparator, which is created if needed. */
    private static LogComparator logComparator = null;


    /**
     * Rotate log files according to the configuration that has been set.
     * <p> <strong>The first presupposition is that the file name template
     * and log file directory have not changed since we first started doing
     * this.  Because if so, then all bets are off as far as being able to
     * identify which are our log files and thus subject to this rotation.</strong>
     *
     * @param	lastCurrentCal	The calendar date/time from "before now"
     *				which was last used to format the file name(s).
     */
    private static void rotateLogFiles(Calendar lastCurrentCal) {
	// Presuppositions:
	// * if DATE_TOKEN is in the file name template
	//   then logFileInterval != NONE and vice-versa
	// * logFileRotation is set
	// * logFileMaxSize could be set or not
	// * this only "works" if the same logFileDir and
	//   logFileTemplate are set now as when we last did this
	// * likewise the other settings haven't changed either
	if (logFileInterval == NONE) {
	    // This rotation is solely because the current file exceeded the size limit,
	    // and so there is no date in the name(s).
	    int numFiles;
	    if (logFileKeep == 0) {
		// Count up to the last file found, then rename going backward.
		numFiles = countBackupFiles(null);
	    }
	    else {
		// Delete the ".n" file (if it exists, which it might not)
		String lastFileName = getBackupFileName(logFileKeep, logFileCompress);
		new File(logFileDir, lastFileName).delete();
		numFiles = logFileKeep;
	    }
	    // Now rename the ".1" to ".n-1" to ".2" to ".n" files
	    // (ignoring files that don't exist)
	    renameBackupFiles(null, numFiles);
	}
	else if (logFileInterval != NONE && logFileMaxSize == 0L) {
	    // This is due strictly to date-based rotation, and so there is a date
	    // in each of the file names.
	    // The only thing we need to do then is prune the files beyond the "keep" limit
	    // and rename/compress the current file.
	    if (logFileKeep != 0) {
		// Get the complete list of files according to the current date pattern
		String[] fileNames = new File(logFileDir).list(new LogFileFilter(false));
		// The file names will only differ (in this case) by the date value, so straight string
		// sorting is quite appropriate.  By ascending date, so the oldest ones are first.
		Arrays.sort(fileNames);
		if (fileNames.length > logFileKeep) {
		    for (int i = 0; i < fileNames.length - logFileKeep; i++) {
			new File(logFileDir, fileNames[i]).delete();
		    }
		}
	    }
	}
	else {
	    // Both date- and size-based rotation is specified, so there is a
	    // date in the names, but there might be multiple files on the same
	    // date (and there could be a ".0" file as well (see code in
	    // "startLogFileRotation")).
	    if (logFileKeep > 0) {
		String[] fileNames = new File(logFileDir).list(new LogFileFilter(true));
		if (logComparator == null) {
		    // Only create it if necessary, so we are sure to get the current template/settings
		    logComparator = new LogComparator();
		}
		Arrays.sort(fileNames, logComparator);
		// Delete the oldest files beyond the "keep" limit
		for (int i = 0; i < fileNames.length; i++) {
		    if (i < fileNames.length - logFileKeep) {
			new File(logFileDir, fileNames[i]).delete();
		    }
		}
	    }
	    // The only renaming to do is the current day's files that might
	    // have a backup number also.
	    // Count up to the last file found, then rename going backward.
	    int numFiles = countBackupFiles(lastCurrentCal);
	    // Now rename the ".1" to".n-1" to ".2" to ".n" files
	    renameBackupFiles(lastCurrentCal, numFiles);
	}
	// Deal with the current file (which should be a ".0")
	doCurrentFileRotation(lastCurrentCal, ".0");
    }


    /**
     * Start the process of rotating log files (start the background thread for this
     * potentially time-consuming operation).
     *
     * @param	lastCurrentCal	The previous log file time (for name formatting).
     */
    private static void startLogFileRotation(final Calendar lastCurrentCal) {
	String lastCurrentName = getLogFileName(lastCurrentCal);
	String newLastCurrentName = lastCurrentName + ".0";
	if (!renameLogFile(lastCurrentName, newLastCurrentName)) {
	    // If we had trouble with this basic rename operation, then skip anything else
	    return;
	}

	// Okay, now we're ready to start the (possibly lengthy) process of
	// compressing the latest file and renaming all prior ones, up to the
	// "keep" number of files -- run it in the background.
	logFileRotationService.submit(() -> rotateLogFiles(lastCurrentCal));
    }


}
