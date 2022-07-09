/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2010-2011,2013,2015-2017,2020,2022 Roger L. Whitcomb.
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
 *  Generic client statistics package
 *
 * History:
 *    13-Jul-2010 (rlwhitcomb)
 *	Generalize from the code in Piccolo Gateway
 *	for use with Discovery / Remote Command.
 *    22-Jul-2011 (rlwhitcomb)
 *	Differentiate read/written bytes.
 *    07-Oct-2011 (rlwhitcomb)
 *	Add accessor method for number of current clients;
 *	convert to using Environment methods for high-res timer
 *	because of endemic problems with System.nanoTime() on Linux.
 *    25-Oct-2011 (rlwhitcomb)
 *	Correct some things found by FindBugs.
 *    19-Mar-2013 (rlwhitcomb)
 *	Report current concurrent users in addition to max.
 *    11-Feb-2015 (rlwhitcomb)
 *	Report current concurrent users also at session end (helps in
 *	tracking down orphaned sessions).  Tweak some of the text to
 *	clarify that all times are in seconds.
 *    07-Jan-2016 (rlwhitcomb)
 *	Fix Javadoc warnings found by Java 8.
 *    27-Mar-2017 (rlwhitcomb)
 *	Add memory used/max to the statistics (same as Director "About" dialog);
 *	refactor to simplify code.
 *    24-Oct-2017 (rlwhitcomb)
 *	Reword the memory statistics messages to be more clear.
 *    16-Mar-2020 (rlwhitcomb)
 *	Don't report negative/positive MAX_LONG for min/max; use N/A instead.
 *	This is for the case that some sessions have started but not ended, so
 *	that the min/max have never been set. Add new flavor of "startMonitorThread"
 *	with time interval given in seconds. Add named constants for some magic
 *	numbers. Add client name to "dumpRunningClients" output. Make a
 *	"stopMonitorThread" method to interrupt the background thread, and then
 *	have that thread do a final dump of the statistics, as a swan song.
 *	Construct the report titles programmatically so they are correctly
 *	centered within the width. Rename "dumpStatistics" to "reportStatistics".
 *    16-Apr-2020 (rlwhitcomb)
 *	Cleanup and prepare for GitHub.
 *    21-Dec-2020 (rlwhitcomb)
 *	Update obsolete Javadoc constructs.
 *    14-Apr-2022 (rlwhitcomb)
 *	#273: Move math-related classes to "math" package.
 *    09-Jul-2022 (rlwhitcomb)
 *	#393: Cleanup imports.
 */
package info.rlwhitcomb.util;

import info.rlwhitcomb.math.Num;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import static info.rlwhitcomb.util.CharUtil.Justification;

/**
 * Collect statistics about the current long-running session and all the clients
 * that have connected.
 * <p> Statistics include:
 * <ul><li>Total number of clients serviced.
 * <li>Clients currently connected.
 * <li>Maximum number of concurrent clients so far.
 * <li>Total number of I/O bytes transferred by all clients.
 * <li>Minimum and maximum number of bytes of any client session.
 * <li>Total clock time used by all sessions.
 * <li>Minimum and maximum times used by any client session.
 * <li>How long since last session finished.
 * </ul>
 * <p> A method is provided to dump these statistics to a given {@link PrintStream}
 * for display and basic information about session start and stop are logged using
 * the standard {@link Logging} class.
 */
public class ClientStatistics implements Runnable
{
	/** Map of currently running clients for informational/debugging purposes. */
	public static final Map<String, ClientStatistics> runningClients = new HashMap<>();

	/** Reference to the background thread that is doing the statistics reporting. */
	private static volatile Thread backgroundThread;

	// Statistics collected for all clients since we started
	/** Total number of client sessions that have been started so far. */
	public static long numberClients = 0;
	/** The maximum number of client sessions that have been running concurrently. */
	public static int maxConcurrency = 0;
	/** The total number of bytes transferred through any input or output streams
	 * created by all sessions so far (updated by {@link #addToBytes} method. */
	public static long totalNumberBytes = 0;
	/** The total number of bytes read by input streams. The number of bytes written
	 * to output streams can be inferred by {@link #totalNumberBytes} - {@link #totalNumberBytesRead}.
	 */
	public static long totalNumberBytesRead = 0;
	/** The minimum number of bytes transferred by any session so far. */
	public static long minNumberBytes = Long.MAX_VALUE;
	/** The maximum number of bytes transferred by any session so far. */
	public static long maxNumberBytes = Long.MIN_VALUE;
	/** The total amount of time used by all sessions so far. */
	public static long totalTime = 0;
	/** The minimum amount of time used by any session so far. */
	public static long minTime = Long.MAX_VALUE;
	/** The maximum amount of time used by any session so far. */
	public static long maxTime = Long.MIN_VALUE;
	/** The last time a client request ended (used to measure inactivity). */
	public static long lastRequestTime = Environment.highResTimer();
	/** The configurable interval (milliseconds) between {@link #reportStatistics} reports. */
	public static long reportInterval = 0;
	/** {@link LogStream} object used to output the reports. */
	private static LogStream strm = null;

	// Strings for statistics displays
	/** The pad character for the title strings. */
	private static final char PAD = '=';
	/** Title string for the "list of running clients" report. @see #dumpRunningClients dumpRunningClients */
	private static final String RUNNING_CLIENTS_NAME = " Running Clients ";
	/** Width of the "list of running clients" report. */
	private static final int RUNNING_CLIENTS_WIDTH = 46;
	/** Constructed complete title string for the "list of running clients" report. */
	private static final String RUNNING_CLIENTS_TITLE;
	/** Constructed underline for the "list of running clients" report. @see #dumpRunningClients dumpRunningClients */
	private static final String RUNNING_CLIENTS_UNDER;
	/** Title string for the "client statistics" report. @see #reportStatistics reportStatistics */
	private static final String CLIENT_STATISTICS_NAME = " Client Statistics ";
	/** Width of the "client statistics" report. */
	private static final int CLIENT_STATISTICS_WIDTH = 56;
	/** Constructed complete title string for the "client statistics" report. */
	private static final String CLIENT_STATISTICS_TITLE;
	/** Title string for the "final client statistics" report. @see #reportStatistics reportStatistics */
	private static final String FINAL_STATISTICS_NAME =  " Final Client Statistics ";
	/** Constructed complete title string for the "final client statistics" report. */
	private static final String FINAL_STATISTICS_TITLE;
	/** Constructed underline for the client statistics report. @see #reportStatistics reportStatistics */
	private static final String CLIENT_STATISTICS_UNDER;
	/** Title string for the "session statistics" report. */
	private static final String SESSION_STATISTICS_NAME = " Session Statistics ";
	/** Width of the "session statistics" report. */
	private static final int SESSION_STATISTICS_WIDTH = 52;
	/** Constructed complete title for the "session statistics" report. */
	private static final String SESSION_STATISTICS_TITLE;
	/** Constructed underline for the "session statistics" report. */
	private static final String SESSION_STATISTICS_UNDER;

	/** Scale factor for converting high-res time values to whole seconds. */
	private static final int SCALE = Environment.highResTimeScaleFactor();

	/** The default interval to use for reporting (if a different one is not given on startup). */
	private static final int DEFAULT_REPORTING_INTERVAL_MINUTES = 30;
	private static final long SECONDS_PER_MINUTE = 60L;
	private static final long MILLIS_PER_SECOND = 1000L;

	/**
	 * Initialize the display string programmatically so they look better.
	 */
	static
	{
		RUNNING_CLIENTS_TITLE = CharUtil.padToWidth(RUNNING_CLIENTS_NAME, RUNNING_CLIENTS_WIDTH, PAD, Justification.CENTER);
		RUNNING_CLIENTS_UNDER = CharUtil.makeStringOfChars(PAD, RUNNING_CLIENTS_WIDTH);
		CLIENT_STATISTICS_TITLE = CharUtil.padToWidth(CLIENT_STATISTICS_NAME, CLIENT_STATISTICS_WIDTH, PAD, Justification.CENTER);
		FINAL_STATISTICS_TITLE = CharUtil.padToWidth(FINAL_STATISTICS_NAME, CLIENT_STATISTICS_WIDTH, PAD, Justification.CENTER);
		CLIENT_STATISTICS_UNDER = CharUtil.makeStringOfChars(PAD, CLIENT_STATISTICS_WIDTH);
		SESSION_STATISTICS_TITLE = CharUtil.padToWidth(SESSION_STATISTICS_NAME, SESSION_STATISTICS_WIDTH, PAD, Justification.CENTER);
		SESSION_STATISTICS_UNDER = CharUtil.makeStringOfChars(PAD, SESSION_STATISTICS_WIDTH);
	}

	/********************************************************************************************************/
	/** Per-Object information										*/
	/********************************************************************************************************/
	/** Client name string, used as the key for {@link #runningClients} map. */
	private String clientName = null;
	/** Client object, used as the value for {@link #runningClients} map.
	 * <p> This is the class object that is a {@link Runnable} entity that we are keeping track of.
	 * <p> The {@link Object#toString} method is called to identify the object in the reporting.
	 */
	private Object client = null;
	/** Total number of bytes transferred through this session. */
	private long numBytes = 0;
	/** Total number of bytes read by input streams through this session. */
	private long numBytesRead = 0;
	/** Time when this session started.  Used to calculate the total time for this session once it ends. */
	private long startTime = 0;


	/**
	 * Constructor given a client name and client object.
	 *
	 * @param	name	Name to assign to this client: used as the key
	 *			for the {@link #runningClients} map
	 * @param	client	Client object that we're keeping track of: value
	 *			in the {@link #runningClients} map
	 */
	private ClientStatistics(String name, Object client) {
	    this.clientName = name;
	    this.client = client;
	    this.startTime = Environment.highResTimer();
	}


	private static String format(long value) {
	    return (value == Long.MIN_VALUE || value == Long.MAX_VALUE)
		? CharUtil.padToWidth("N/A", 16, CharUtil.Justification.RIGHT)
		: Num.fmt1(value, 16);
	}

	private static String format(double value) {
	    return (value == (double)Long.MIN_VALUE || value == (double)Long.MAX_VALUE)
		? CharUtil.padToWidth("N/A", 16, CharUtil.Justification.RIGHT)
		: Num.fmt2(value, SCALE, 12);
	}

	/**
	 * Static "factory" method to instantiate an object to keep track of
	 * statistics for the given client.
	 *
	 * @param	name	Name to assign to this client: used as the key
	 *			for the {@link #runningClients} map
	 * @param	client	Client object that we're keeping track of: value
	 *			in the {@link #runningClients} map
	 * @return		A new {@link ClientStatistics} object that will
	 *			be used to update various statistics along the way.
	 */
	public static ClientStatistics registerClient(String name, Object client) {
	    // Create our client object in order to return it
	    ClientStatistics clientObj = new ClientStatistics(name, client);

	    // Register the client with the "currently running" map
	    // and update some statistics right away
	    synchronized(runningClients) {
		runningClients.put(name, clientObj);
		maxConcurrency = Math.max(maxConcurrency, runningClients.size());
		numberClients++;
	    }
	    return clientObj;
	}


	/**
	 * Unregister ourselves with the {@link #runningClients} map and compute
	 * final statistics about this client session.
	 *
	 */
	public void unregisterClient() {
	    // Unregister ourselves with the "currently running" map
	    synchronized(runningClients) {
		runningClients.remove(clientName);

		// Update byte and time statistics
		long endTime = Environment.highResTimer();
		long sessionTime = endTime - startTime;
		totalTime += sessionTime;
		lastRequestTime = endTime;
		minTime = Math.min(minTime, sessionTime);
		maxTime = Math.max(maxTime, sessionTime);
		totalNumberBytes += numBytes;
		totalNumberBytesRead += numBytesRead;
		minNumberBytes = Math.min(minNumberBytes, numBytes);
		maxNumberBytes = Math.max(maxNumberBytes, numBytes);
		Runtime runtime = Runtime.getRuntime();
		long usedMemory = runtime.totalMemory() - runtime.freeMemory();
		long maxMemory = runtime.maxMemory();

		if (Logging.isLevelEnabled(Logging.INFO)) {
		    Logging.Info(SESSION_STATISTICS_TITLE);
		    Logging.Info("                      Session name: %1$s", clientName);
		    Logging.Info("              Number of bytes read: %1$s", format(numBytesRead));
		    Logging.Info("           Number of bytes written: %1$s", format(numBytes - numBytesRead));
		    Logging.Info("    Total number bytes transferred: %1$s", format(numBytes));
		    Logging.Info(" Total time (seconds) this session: %1$s", format((double)sessionTime));
		    Logging.Info(" Amount of memory currently in use: %1$s", format(usedMemory));
		    Logging.Info("Maximum amount of memory available: %1$s", format(maxMemory));
		    Logging.Info("       Remaining number of clients: %1$s", format(runningClients.size()));
		    Logging.Info(SESSION_STATISTICS_UNDER);
		}
	    }
	    // Remove our object links for GC to proceed happily
	    clientName = null;
	    client = null;
	}


	/**
	 * @return Count of current number of clients (snapshot).
	 */
	public static int currentClientCount() {
	    synchronized(runningClients) {
		return runningClients.size();
	    }
	}


	/**
	 * Update bytes transferred through the client object.
	 *
	 * @param	num	Incremental number of bytes transferred since the last call
	 *			to this method.  Will be added to the {@link #numBytes} value.
	 * @param	read	{@code true} if this is a "read" operation or {@code false}
	 *			for "write".  The {@link #numBytesRead} will be updated if this
	 *			value is {@code true}.
	 */
	public void addToBytes(long num, boolean read) {
	    numBytes += num;
	    if (read)
		numBytesRead += num;
	}


	/**
	 * Produce a report of the currently running clients.
	 * <p> Synchronizes on the {@link #runningClients} map so that the report is always consistent.
	 *
	 * @param	out	where to dump the report
	 * @see		#RUNNING_CLIENTS_TITLE
	 * @see		#RUNNING_CLIENTS_UNDER
	 */
	public static void dumpRunningClients(PrintStream out) {
	    out.println(RUNNING_CLIENTS_TITLE);
	    synchronized(runningClients) {
		for (ClientStatistics client : runningClients.values()) {
		    out.println(String.format("%1$s: %2$s", client.client.toString(), client.clientName));
		}
	    }
	    out.println(RUNNING_CLIENTS_UNDER);
	}


	/**
	 * Report statistics on all client sessions since the service started.
	 * <p> Reports number of sessions, maximum concurrency and total bytes of I/O so far.
	 * Also reports min/max/average of I/O per session and min/max/average time per session.
	 * <p><b>Note: this report uses internal strings, so it cannot be translated easily.</b>
	 *
	 * @param	out	where to dump the statistics report
	 * @param	finalReport	whether this is the final report or not (affects the title)
	 * @see		Num
	 * @see		#CLIENT_STATISTICS_TITLE
	 * @see		#CLIENT_STATISTICS_UNDER
	 */
	public static void reportStatistics(PrintStream out, boolean finalReport) {
	    out.println(finalReport ? FINAL_STATISTICS_TITLE : CLIENT_STATISTICS_TITLE);
	    synchronized(runningClients) {
		out.print("Total number of client sessions so far: "); out.println(format(numberClients));
		out.print("    Number of currently active clients: "); out.println(format(runningClients.size()));
		out.print("  Maximum number of concurrent clients: "); out.println(format(maxConcurrency));
		out.print("                  Number of bytes read: "); out.println(format(totalNumberBytesRead));
		out.print("               Number of bytes written: "); out.println(format(totalNumberBytes - totalNumberBytesRead));
		out.print("     Total number of bytes transferred: "); out.println(format(totalNumberBytes));
		long average;
		if (numberClients != 0) {
		    out.print("  Minimum number of bytes in a session: "); out.println(format(minNumberBytes));
		    out.print("  Maximum number of bytes in a session: "); out.println(format(maxNumberBytes));
		    average = totalNumberBytes / numberClients;
		    out.print("   Average number of bytes per session: "); out.println(format(average));
		}
		out.print(" Total time (seconds) for all sessions: "); out.println(format((double)totalTime));
		long elapsed = Environment.highResTimer() - lastRequestTime;
		if (numberClients != 0) {
		    out.print("             Minimum time in a session: "); out.println(format((double)minTime));
		    out.print("             Maximum time in a session: "); out.println(format((double)maxTime));
		    double avg = (double)totalTime / (double)numberClients;
		    out.print("   Average amount of time in a session: "); out.println(format(avg));
		    out.print(" Time (secs) since last client request: ");
		}
		else {
		    out.print("             Time (secs) since startup: ");
		}
		out.println(format((double)elapsed));

		Runtime runtime = Runtime.getRuntime();
		long usedMemory = runtime.totalMemory() - runtime.freeMemory();
		long maxMemory = runtime.maxMemory();
		out.print("     Amount of memory currently in use: "); out.println(format(usedMemory));
		out.print("    Maximum amount of memory available: "); out.println(format(maxMemory));
	    }
	    out.println(CLIENT_STATISTICS_UNDER);
	}


	/**
	 * The {@link Runnable} interface.  This is the method that gets
	 * called when the background thread's {@link Thread#start} method
	 * is called.  So, this method is the guts of what happens in the
	 * background.
	 * <p>Run the thread until completion. However, since this is a
	 * background thread which won't end until the main process ends,
	 * the code looks like an infinite loop.  Marking the thread as
	 * a "daemon" allows the VM to terminate this thread when the
	 * main thread finishes.
	 * <p>Fall asleep for the configured amount of time, wake up
	 * and call the {@link #reportStatistics} method
	 * to print the client statistics to the log file (via the
	 * {@link LogStream} object), then go back to sleep again.
	 */
	public void run() {
	    try {
		do {
		    Thread.sleep(reportInterval);
		    reportStatistics(strm, false);
		} while (true);
	    }
	    catch (InterruptedException ie) {
		Logging.Info("%1$s thread interrupted, now exiting.", Thread.currentThread().getName());
	    }
	    // Dump the final statistics once we're done.
	    reportStatistics(strm, true);
	}


	/**
	 * The static method to start a background thread to monitor our
	 * statistics.
	 *
	 * @param	minutes	The reporting interval in minutes (&lt;=0 is default = 30 minutes).
	 * @see		#DEFAULT_REPORTING_INTERVAL_MINUTES
	 */
	public static void startMonitorThread(int minutes) {
	    startMonitorThread((long)minutes * SECONDS_PER_MINUTE);
	}


	/**
	 * The static method to start a background thread to monitor our
	 * statistics.
	 *
	 * @param	seconds	The reporting interval in seconds (&lt;=0 is default = 30 minutes).
	 * @see		#DEFAULT_REPORTING_INTERVAL_MINUTES
	 */
	public static void startMonitorThread(long seconds) {
	    Logging.Debug("ClientStatistics startMonitorThread");
	    ClientStatistics cs = new ClientStatistics("Monitor Thread", null);
	    backgroundThread = new Thread(cs, ClientStatistics.class.getSimpleName());
	    backgroundThread.setDaemon(true);
	    if (seconds <= 0L)
		seconds = (long)DEFAULT_REPORTING_INTERVAL_MINUTES * SECONDS_PER_MINUTE;
	    reportInterval = seconds * MILLIS_PER_SECOND;
	    strm = new LogStream();
	    backgroundThread.start();
	}


	/**
	 * Stop the running monitor thread (not necessary since it will be a daemon thread,
	 * but handy if you always want final statistics).
	 */
	public static void stopMonitorThread() {
	    if (backgroundThread != null) {
		synchronized (backgroundThread) {
		    if (backgroundThread.isAlive()) {
			backgroundThread.interrupt();
		    }
		}
		backgroundThread = null;
	    }
	}


}
