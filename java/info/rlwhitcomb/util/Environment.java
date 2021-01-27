/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2017,2019-2021 Roger L. Whitcomb.
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
 *	Class exposing various attributes of the Environment we're
 *	currently running in.
 *
 *  History:
 *	10-Aug-2011 (rlwhitcomb)
 *	    Created.
 *	15-Aug-2011 (rlwhitcomb)
 *	    Add architecture value.
 *	03-Oct-2011 (rlwhitcomb)
 *	    Allow current directory to be changed as user
 *	    navigates around with a file browser.
 *	07-Oct-2011 (rlwhitcomb)
 *	    Add "isLinux" method; add methods to abstract
 *	    out the high-res timer (workaround for Linux).
 *	06-Nov-2012 (rlwhitcomb)
 *	    Add a method to get the process ID (using
 *	    java management methods).
 *	04-Apr-2013 (rlwhitcomb)
 *	    Expose the host/computer name.
 *	04-Apr-2013 (rlwhitcomb)
 *	    If the host name is not set in the environment,
 *	    call the network layer to get it.
 *	05-Sep-2013 (rlwhitcomb)
 *	    Add method to determine the environment variable
 *	    name used to locate native libraries.
 *	12-Nov-2013 (rlwhitcomb)
 *	    Add accessor for platform, which is os.name.
 *	15-Apr-2014 (rlwhitcomb)
 *	    Add get/set methods for "running as a GUI application"
 *	    so we can make choices for input methods, etc.
 *	16-Sep-2014 (rlwhitcomb)
 *	    New method to check if the user is the same as current
 *	    (desktop) user.  This is for convenience because user
 *	    names under Windows are case-insensitive, but not so on
 *	    Linux or OSX.
 *	06-Nov-2014 (rlwhitcomb)
 *	    Moved error message to resource bundle.
 *	27-Aug-2015 (rlwhitcomb)
 *	    Save the startup properties for access by anyone.
 *	11-Sep-2015 (rlwhitcomb)
 *	    For the web version, return user home dir for current dir
 *	    since the "current" directory will be some weird place in
 *	    the web server area.
 *	07-Jan-2016 (rlwhitcomb)
 *	    Fix Javadoc warnings found by Java 8.
 *	04-May-2016 (rlwhitcomb)
 *	    Add a flag to say this is a Java Web Start application.
 *	10-Feb-2017 (rlwhitcomb)
 *	    Add a setting for the product name (to use with SQL session
 *	    descriptions).
 *	31-Oct-2017 (rlwhitcomb)
 *	    Add new "userDocumentDirectory" and use that for default in
 *	    "currentDirectory()" esp. for OSX where the "current" is in
 *	    a completely weird and unusable location (inside the .app).
 *	20-May-2019 (rlwhitcomb)
 *	    Make this pre-processable and add in the version information.
 *	06-Jan-2020 (rlwhitcomb)
 *	    Add a copyright notice string and accessor method.
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	    Set the default product name from the preprocessor also.
 *	31-Jul-2020 (rlwhitcomb)
 *	    New method to print the product information, which we keep track of.
 *	02-Oct-2020 (rlwhitcomb)
 *	    New method to get the numeric Java major version (6, 7, 8, 9, 10, etc.)
 *	08-Oct-2020 (rlwhitcomb)
 *	    Change parsing of the Java version to compensate for version "11+28".
 *	    Add variants of "printProgramInfo" for the common cases (System.out, etc.)
 *	09-Oct-2020 (rlwhitcomb)
 *	    New "fileSeparator" and "pathSeparator" methods to expose those values.
 *	06-Nov-2020 (rlwhitcomb)
 *	    Reformat the program version info slightly to improve appearance.
 *	09-Nov-2020 (rlwhitcomb)
 *	    Add a new method to scale timer value to (double) seconds.
 *	13-Nov-2020 (rlwhitcomb)
 *	    Display debug build info in "printProgramInfo".
 *	11-Dec-2020 (rlwhitcomb)
 *	    Individual title and version information from build.properties.
 *	21-Dec-2020 (rlwhitcomb)
 *	    Use "version.properties" instead for the title/version info.
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 *	22-Dec-2020 (rlwhitcomb)
 *	    Get all our values from the properties files, instead of needing
 *	    the preprocessor.
 *	24-Dec-2020 (rlwhitcomb)
 *	    Change up the colors in "printProgramInfo" a little bit. Tweak
 *	    the spacing of the display; add another override.
 *	28-Dec-2020 (rlwhitcomb)
 *	    Option to use colors on "printProgramInfo".
 *	05-Jan-2021 (rlwhitcomb)
 *	    Update program info colors (work better on black Windows backgrounds).
 *	05-Jan-2021 (rlwhitcomb)
 *	    "timeThis" methods.
 *	06-Jan-2021 (rlwhitcomb)
 *	    New flavor of "loadProgramInfo".
 *	14-Jan-2021 (rlwhitcomb)
 *	    Add "getAllProgramInfo" method.
 *	25-Jan-2021 (rlwhitcomb)
 *	    Also display Java version with program info.
 *	27-Jan-2021 (rlwhitcomb)
 *	    New method to return temp directory name.
 */
package info.rlwhitcomb.util;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.lang.management.ManagementFactory;

import static info.rlwhitcomb.util.CharUtil.Justification.*;
import static info.rlwhitcomb.util.ConsoleColor.Code.*;


/**
 * Package of essentially static information about the
 * environment we're running in.  This information is
 * largely derived from properties accessible via
 * {@link System#getProperty}, but are implemented here
 * in a (hopefully) more convenient form that hides the
 * actual details (just in case they change).
 */
public final class Environment
{
	private static final String USER_NAME = System.getProperty("user.name");
	private static final String USER_DIR = System.getProperty("user.dir");
	private static final String USER_HOME = System.getProperty("user.home");
	private static final String OS_NAME = System.getProperty("os.name");
	private static final String OS_NAME_LOWER = OS_NAME.toLowerCase(Locale.ENGLISH);
	private static final String OS_VERSION = System.getProperty("os.version");
	private static final String JAVA_RUNTIME_VERSION = System.getProperty("java.runtime.version");
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	private static final String PATH_SEPARATOR = System.getProperty("path.separator");
	private static final int DATA_MODEL = Integer.parseInt(System.getProperty("sun.arch.data.model"));

	public static final int DATA_MODEL_32 = 32;
	public static final int DATA_MODEL_64 = 64;

	private static File currentDirectory = null;

	private static Map<String, String> startupProperties = null;

	private static boolean runningAsDesktop = false;
	private static boolean runningAsWebStart = false;
	private static boolean runningAsGUI = false;

	private static boolean osIsWindows = OS_NAME_LOWER.startsWith("windows");
	private static boolean osIsLinux = OS_NAME_LOWER.startsWith("linux");
	private static boolean osIsOSX = OS_NAME_LOWER.startsWith("mac os x");

	private static int javaMajorVersion;

	private static TimeUnit timeUnit = osIsLinux ? TimeUnit.MILLISECONDS : TimeUnit.NANOSECONDS;

	private static String copyrightNotice = null;

	/**
	 * The set of properties read from various files: "build.properties", "build.number",
	 * and "version.properties", which should include titles and versions for the major
	 * main programs, as well as other build-related information.
	 */
	private static Properties buildProperties = null;

	/**
	 * Default product name -- read from the "build.properties" file,
	 * but can be set by the enclosing application as well.
	 * @see #setProductName
	 */
	private static String productName = null;

	/**
	 * Current version of this application -- read from the "build.properties" file.
	 */
	private static String APP_VERSION = null;
	/**
	 * Current build number -- read from the "build.number" file.
	 */
	private static String APP_BUILD = null;
	/**
	 * Build date.
	 */
	private static String BUILD_DATE = null;
	/**
	 * Build time.
	 */
	private static String BUILD_TIME = null;
	/**
	 * Whether this is a DEBUG build or not.
	 */
	private static boolean IS_DEBUG_BUILD = false;

	/**
	 * Overloaded product name.
	 * @see #productName
	 * @see #getProductName
	 * @see #setProductName
	 */
	private static String overloadedProductName = null;

	/**
	 * Overloaded app version.
	 * @see #APP_VERSION
	 * @see #getAppVersion
	 * @see #setAppVersion
	 */
	private static String overloadedAppVersion = null;


	/**
	 * Class to hold program information.
	 */
	public static class ProgramInfo
	{
		public String className;
		public String title;
		public String version;

		ProgramInfo(final String clsName, final String titleInfo, final String verInfo) {
		    this.className = clsName;
		    this.title     = titleInfo;
		    this.version   = verInfo;
		}
	}


	static {
	    String[] parts = JAVA_RUNTIME_VERSION.split("[\\.+-]");
	    if (parts[0] == "1" && parts.length > 1)
		javaMajorVersion = Integer.parseInt(parts[1]);
	    else
		javaMajorVersion = Integer.parseInt(parts[0]);
	}


	/**
	 * Get the current user we're running as.
	 *
	 * @return	The value of 'user.name' system property.
	 * @see	#USER_NAME
	 */
	public static String currentUser() {
	    return USER_NAME;
	}


	/**
	 * Check if the given user is the same as the current user
	 * <p> This subsumes the platform check and the decision as to
	 * whether the names are case-sensitive or not.
	 *
	 * @param	user	User name to check if it matches the {@link #currentUser}.
	 * @return	{@code true} if the given name matches the current user value
	 *		taking into consideration the case-sensitivity of the platform
	 */
	public static boolean isCurrentUser(String user) {
	    if (osIsWindows)
		return USER_NAME.equalsIgnoreCase(user);
	    return USER_NAME.equals(user);
	}


	/**
	 * Get the static user's current directory.
	 *
	 * @return	The value of the 'user.dir' system property.
	 * @see	#USER_DIR
	 */
	public static File userDirectory() {
	    return new File(USER_DIR);
	}


	/**
	 * Get the current directory.
	 *
	 * @return	The value of the {@link #currentDirectory} value if
	 *		it has been set by user interaction, or the {@link #userDocumentDirectory}
	 *		value by default for the desktop or {@link #userHomeDir}
	 *		for the web version.
	 */
	public static File currentDirectory() {
	    if (currentDirectory != null)
		return currentDirectory;
	    return runningAsDesktop ? userDocumentDirectory() : userHomeDir();
	}


	/**
	 * Set a new value for the current directory used by the application.
	 *
	 * @param	dir	New current directory (probably gotten from a
	 *			file browse dialog).
	 */
	public static void setCurrentDirectory(File dir) {
	    currentDirectory = dir;
	}


	/**
	 * Get the user's home directory.
	 *
	 * @return	The value of the 'user.home' system property as a {@link File}.
	 * @see	#USER_HOME
	 */
	public static File userHomeDir() {
	    return new File(USER_HOME);
	}


	/**
	 * Get the user's "document" directory.
	 *
	 * @return	The value of the 'user.home' system property with the
	 *		"Documents" (or equivalent) subdirectory.
	 * @see #USER_HOME
	 */
	public static File userDocumentDirectory() {
	    File docDir = new File(USER_HOME, "Documents");
	    if (docDir.exists() && docDir.isDirectory())
		return docDir;
	    return userHomeDir();
	}


	/**
	 * @return The platform designation (the "os.name" property).
	 */
	public static String platform() {
	    return OS_NAME;
	}


	/**
	 * @return Are we running on a version of the Windows (tm)
	 * operating system?
	 *
	 * @see	#OS_NAME_LOWER
	 * @see #osIsWindows
	 */
	public static boolean isWindows() {
	    return osIsWindows;
	}


	/**
	 * @return Are we running on a version of the Mac OSX
	 * operating system?
	 *
	 * @see	#OS_NAME_LOWER
	 * @see #osIsOSX
	 */
	public static boolean isOSX() {
	    return osIsOSX;
	}


	/**
	 * @return Are we running on a version of the Linux
	 * operating system?
	 *
	 * @see	#OS_NAME_LOWER
	 * @see #osIsLinux
	 */
	public static boolean isLinux() {
	    return osIsLinux;
	}


	/**
	 * @return The operating system version string.
	 *
	 * @see	#OS_VERSION
	 */
	public static String osVersion() {
	    return OS_VERSION;
	}


	/**
	 * @return Are we running as a desktop application?
	 *
	 * @see	#runningAsDesktop
	 * @see	#setDesktopApp
	 */
	public static boolean isDesktopApp() {
	    return runningAsDesktop;
	}


	/**
	 * Set the flag (from the main program) to say whether we're running
	 * as a desktop application (as opposed to inside a web browser).
	 *
	 * @param	desktop	{@code false} when called from an applet.
	 */
	public static void setDesktopApp(boolean desktop) {
	    runningAsDesktop = desktop;
	}


	/**
	 * @return Are we running as a Java Web Start application?
	 *
	 * @see	#runningAsWebStart
	 * @see	#setWebStartApp
	 */
	public static boolean isWebStartApp() {
	    return runningAsWebStart;
	}


	/**
	 * Set the flag (from the main program) to say whether we're running
	 * as a Java Web Start application (that is, a desktop app, but launched
	 * from a JNLP file, and authenticated with and getting preferences from 
	 * the launching web server).
	 *
	 * @param	webstart	Only set as {@code true} when the {@code DirectorMain.main()}
	 *				is invoked with the build flag saying this is a Java Web Start
	 *				build is set {@code true}, otherwise just leave the default.
	 */
	public static void setWebStartApp(boolean webstart) {
	    runningAsWebStart = webstart;
	}


	/**
	 * @return Are we running as a GUI application
	 * (as opposed to running as a console app)?
	 *
	 * @see #runningAsGUI
	 * @see #setGUIApp
	 */
	public static boolean isGUIApp() {
	    return runningAsGUI;
	}


	/**
	 * Set the flag (from the main GUI program) to say whether we're running
	 * as a GUI application (as opposed to just as a console app).
	 *
	 * @param	gui	{@code true} only from an application that displays a GUI interface.
	 */
	public static void setGUIApp(boolean gui) {
	    runningAsGUI = gui;
	}


	/**
	 * @return The version of Java we're running under.
	 *
	 * @see	#JAVA_RUNTIME_VERSION
	 */
	public static String javaVersion() {
	    return JAVA_RUNTIME_VERSION;
	}


	/**
	 * @return The major version of Java we're running under: 6, 7, 8, 9, 10, 11, etc.
	 */
	public static int javaMajorVersion() {
	    return javaMajorVersion;
	}


	/**
	 * @return The host name (computer name) we're running on.
	 */
	public static String hostName() {
	    String hostName = null;
	    if (osIsWindows)
		hostName = System.getenv("COMPUTERNAME");
	    else
		hostName = System.getenv("HOSTNAME");

	    if (hostName == null || hostName.isEmpty())
		hostName = NetworkUtil.getLocalHostName();

	    return hostName;
	}


	/**
	 * @return The system-dependent line separator string.
	 *
	 * @see	#LINE_SEPARATOR
	 */
	public static String lineSeparator() {
	    return LINE_SEPARATOR;
	}


	/**
	 * @return The system-dependent file separator (that is, separator between parts
	 * of a file name) as a string.
	 *
	 * @see #FILE_SEPARATOR
	 */
	public static String fileSeparator() {
	    return FILE_SEPARATOR;
	}


	/**
	 * @return The system-dependent path separator (that is, separator between entries
	 * in the PATH or CLASSPATH environment variable) as a string.
	 *
	 * @see #PATH_SEPARATOR
	 */
	public static String pathSeparator() {
	    return PATH_SEPARATOR;
	}


	/**
	 * Return the architecture model value.
	 *
	 * @return	Either {@link #DATA_MODEL_32} or {@link #DATA_MODEL_64}.
	 * @see	#DATA_MODEL
	 * @throws	IllegalStateException if we can't figure out the data model
	 *		(which would be from a non-standard JVM).
	 */
	public static int dataModel() {
	    if (DATA_MODEL == DATA_MODEL_32 ||
	        DATA_MODEL == DATA_MODEL_64)
		return DATA_MODEL;
	    throw new IllegalStateException(Intl.formatString("util#env.unknownDataModel", DATA_MODEL));
	}


	/**
	 * @return The name of the environment variable used to determine where native libraries
	 * are to be found.  Differs per operating system.
	 */
	public static String getNativePathVar() {
	    if (osIsLinux)
		return "LD_LIBRARY_PATH";
	    else if (osIsOSX)
		return "DYLD_LIBRARY_PATH";
	    return "PATH";
	}


	/**
	 * @return The current high-resolution timer value.
	 * <p> Because of problems with {@link System#nanoTime} on Linux
	 * we will use the millisecond timer (which is pretty accurate
	 * anyway on this platform) instead.
	 *
	 * @see	#highResTimerResolution
	 */
	public static long highResTimer() {
	    if (osIsLinux)
		return System.currentTimeMillis();
	    else
		return System.nanoTime();
	}


	/**
	 * @return The resolution (in ticks per second) of the {@link #highResTimer} value.
	 */
	public static long highResTimerResolution() {
	    return timeUnit.convert(1L, TimeUnit.SECONDS);
	}


	/**
	 * @return The system's high-res {@link TimeUnit} value.
	 *
	 * @see	#timeUnit
	 */
	public static TimeUnit highResTimeUnit() {
	    return timeUnit;
	}


	/**
	 * @return The scaling factor needed for the high-res timer value to scale to
	 * a 0.0 to 1.0 range.
	 * <p> For instance, for nanoseconds, the value would be 9.
	 */
	public static int highResTimeScaleFactor() {
	    long value = highResTimerResolution();
	    int scale = 0;
	    while (value > 1L) {
		value /= 10L;
		scale++;
	    }
	    return scale;
	}


	/**
	 * Convert a long number of timer ticks to a double number of seconds
	 * (uses {@link #highResTimeScaleFactor} to do the calculation).
	 *
	 * @param timerValue	The timer value in our timer ticks resolution.
	 * @return		The value converted to fractions of seconds.
	 */
	public static double timerValueToSeconds(final long timerValue) {
	    return (double) timerValue / (double) highResTimerResolution();
	}


	/**
	 * Time the passed in {@link Runnable} (can be a functional interface)
	 * and display the results on {@link System#out}.
	 *
	 * @param func The function to execute and time.
	 */
	public static void timeThis(final Runnable func) {
	    long startTime = highResTimer();

	    try {
		func.run();
	    }
	    finally {
		long endTime   = highResTimer();
		double seconds = timerValueToSeconds(endTime - startTime);

		Intl.outFormat("util#env.timeThis", seconds);
	    }
	}


	/**
	 * Time the passed in {@link Callable} (can be a functional interface)
	 * and display the results on {@link System#out}.
	 *
	 * @param <V>  Type of value returned from the function.
	 * @param func The function to execute and time.
	 * @return     The return value from the callable function.
	 */
	public static <V> V timeThis(final Callable<V> func) {
	    long startTime = highResTimer();
	    V result = null;

	    try {
		result = func.call();
	    }
	    catch (Exception e) {
		; // maybe report this??
	    }
	    finally {
		long endTime   = highResTimer();
		double seconds = timerValueToSeconds(endTime - startTime);

		Intl.outFormat("util#env.timeThis", seconds);
	    }
	    return result;
	}


	/**
	 * Returns the process ID of the current process.
	 * @return	0 if the process ID cannot be determined using the
	 *		management bean (it does work on all the platforms
	 *		I have available currently).
	 * @see <a href="http://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id">http://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id</a>
	 */
	public static long getProcessID() {
	    final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
	    // The format is (always?) pid@host
	    final int index = jvmName.indexOf('@');

	    if (index > 0) {
		try {
		    return Long.parseLong(jvmName.substring(0, index));
		} catch (NumberFormatException e) {
		    // ignore
		}
	    }
	    return 0L;
	}


	/**
	 * Get the (system-dependent) name of the current temporary directory
	 * for the current user.
	 *
	 * @return	A temp directory name, usually from TEMP or TMP or other
	 *		known system variables.
	 */
	public static String tempDirName() {
	    String name = System.getenv("TEMP");
	    if (CharUtil.isNullOrEmpty(name)) {
		name = System.getenv("TMP");
		if (CharUtil.isNullOrEmpty(name)) {
		    name = System.getenv("TMPDIR");
		    if (CharUtil.isNullOrEmpty(name)) {
			File f = osIsWindows ? new File("C:\\temp") : new File ("/tmp");
			if (f.exists() && f.isDirectory()) {
			    return f.getPath();
			}
			return userDirectory().getPath();
		    }
		}
	    }
	    return name;
	}


	/**
	 * Save the startup properties for use by anyone.
	 *
	 * @param	properties	The startup properties map for the whole application.
	 */
	public static void setStartupProperties(Map<String, String> properties) {
	    startupProperties = properties;
	}


	/**
	 * @return The saved startup properties.
	 */
	public static Map<String, String> getStartupProperties() {
	    return startupProperties;
	}


	/**
	 * @return The product name string, mostly used for identifying sessions,
	 * so default to a neutral product.
	 */
	public static String getProductName() {
	    if (overloadedProductName != null)
		return overloadedProductName;

	    readBuildProperties();
	    return productName;
	}


	/**
	 * Set the product name field.
	 *
	 * @param	name	The new value for the product name (overrides the "neutral" default).
	 */
	public static void setProductName(String name) {
	    overloadedProductName = name;
	}


	/**
	 * @return The application version (set externally from the "version.properties" file).
	 */
	public static String getAppVersion() {
	    if (overloadedAppVersion != null)
		return overloadedAppVersion;

	    readBuildProperties();
	    return APP_VERSION;
	}


	/**
	 * Set the override app version (from the "version.properties" file).
	 *
	 * @param appVersion The new app version string.
	 */
	public static void setAppVersion(String appVersion) {
	    overloadedAppVersion = appVersion;
	}


	/**
	 * @return The application build number (set externally from the "build.number" file).
	 */
	public static String getAppBuild() {
	    readBuildProperties();
	    return APP_BUILD;
	}


	/**
	 * @return The date the application was built (set externally in the "build.number" file)
	 * in <code>yyyy-MM-dd</code> format.
	 */
	public static String getBuildDate() {
	    readBuildProperties();
	    return BUILD_DATE;
	}


	/**
	 * @return The time the application was built (set externally in the "build.number" file)
	 * in <code>HH:mm:ss.SSS z</code> format.
	 */
	public static String getBuildTime() {
	    readBuildProperties();
	    return BUILD_TIME;
	}


	/**
	 * @return Whether or not this is a DEBUG build (set externally in the "build.properties" file).
	 */
	public static boolean isDebugBuild() {
	    readBuildProperties();
	    return IS_DEBUG_BUILD;
	}


	/**
	 * Read a properties file (ISO-8859-1 charset) from the default classpath and return the
	 * properties map decoded from it.
	 *
	 * @param filePath	The path and name of the file to read.
	 * @return		The decode properties or {@code null} if there was an I/O error
	 *			reading the file.
	 */
	public static Properties readPropertiesFile(String filePath) {
	    return readPropertiesFile(filePath, null);
	}


	/**
	 * Read a properties file (ISO-8859-1 charset) from the default classpath and return the
	 * properties map decoded from it.
	 *
	 * @param filePath		The path and name of the file to read.
	 * @param baseProperties	The base properties to use as defaults (can be {@code null}).
	 * @return			The decoded properties or {@code baseProperties}
	 *				if there was an I/O error reading the file (which could be
	 *				{@code null} if that was passed in).
	 */
	public static Properties readPropertiesFile(String filePath, Properties baseProperties) {
	    Properties properties = (baseProperties == null)
			? new Properties()
			: new Properties(baseProperties);
	    try (InputStream is = Environment.class.getResourceAsStream(filePath)) {
		properties.load(is);
	    }
	    catch (IOException ioe) {
		// TODO: error somewhere?
		return baseProperties;
	    }
	    return properties;
	}


	/**
	 * Read in all the build properties from the various sources.
	 *
	 * @see #buildProperties
	 * @see #readPropertiesFile(String)
	 * @see #readPropertiesFile(String, Properties)
	 */
	private static void readBuildProperties() {
	    if (buildProperties == null) {
		buildProperties = readPropertiesFile("/build.properties");
		buildProperties = readPropertiesFile("/build.number", buildProperties);
		buildProperties = readPropertiesFile("/version.properties", buildProperties);

		productName    = buildProperties.getProperty("product.name");
		APP_VERSION    = buildProperties.getProperty("build.version");
		APP_BUILD      = buildProperties.getProperty("build.number");
		BUILD_DATE     = buildProperties.getProperty("build.date");
		BUILD_TIME     = buildProperties.getProperty("build.time");
		IS_DEBUG_BUILD = CharUtil.getBooleanValue(buildProperties.getProperty("debug.build"));
	    }
	}


	/**
	 * Get all the program info (read from "version.properties") into a list/map structure
	 * with the following (JSON-format) form:
	 * <code>[ { "Calc": { "title": "Expression Calculator", "version": "1.0.9" } }, { "Cat": { "title"...</code>
	 *
	 * @return The list of objects with all the information.
	 */
	public static List<ProgramInfo> getAllProgramInfo() {
	    readBuildProperties();

	    Set<String> propNames = buildProperties.stringPropertyNames();
	    int size              = propNames.size();
	    String[] names        = new String[size];
	    int num               = 0;

	    for (String name : propNames) {
		if (name.endsWith(".title")) {
		    names[num++] = name;
		}
		else if (name.endsWith(".version")) {
		    if (name.equals("build.version")) {
			continue;
		    }
		    else {
			names[num++] = name;
		    }
		}
	    }

	    Arrays.sort(names, 0, num);

	    List<ProgramInfo> results = new ArrayList<>(num / 2);

	    // At this point "names" will have matching pairs of Class.title and
	    // Class.version in it
	    String title = "";

	    for (int i = 0; i < num; i++) {
		String name  = names[i];
		String value = buildProperties.getProperty(name);
		if (i % 2 == 0) {
		    title = value;
		}
		else {
		    String className = name.substring(0, name.indexOf('.'));
		    ProgramInfo info = new ProgramInfo(className, title, value);
		    results.add(info);
		}
	    }

	    return results;
	}

	/**
	 * @return The current copyright notice string (from the resources).
	 */
	public static String getCopyrightNotice() {
	    if (copyrightNotice == null) {
		copyrightNotice = Intl.getString("util#env.copyright");
	    }
	    return copyrightNotice;
	}


	/**
	 * Load the program information (title/version) from the "version.properties" file
	 * and save for use by {@link #printProgramInfo}.
	 *
	 * @param clazz	The main program class.
	 */
	public static void loadProgramInfo(Class<?> clazz) {
	    loadProgramInfo(clazz.getSimpleName());
	}

	/**
	 * Load the program information (title/version) from the "version.properties" file
	 * and save for use by {@link #printProgramInfo}.
	 *
	 * @param name The class name to use.
	 */
	public static void loadProgramInfo(String name) {
	    readBuildProperties();
	    setProductName(buildProperties.getProperty(name + ".title"));
	    setAppVersion(buildProperties.getProperty(name + ".version"));
	}


	/**
	 * Display a colored program information banner on {@link System#out} with the info
	 * centered on the longest string.
	 */
	public static void printProgramInfo() {
	    printProgramInfo(System.out);
	}


	/**
	 * Display a colored program information banner using information we know about here
	 * to the given {@link PrintStream}, using the longest string to center on.
	 *
	 * @param ps            The print stream to display the info to.
	*/
	public static void printProgramInfo(PrintStream ps) {
	    printProgramInfo(ps, -1, true);
	}


	/**
	 * Display a colored program information banner using things we know about here
	 * to {@link System#out}, using the given width to display.
	 *
	 * @param centerWidth	The width to use for centering (0 = don't center,
	 *			negative = center over longest string).
	 */
	public static void printProgramInfo(int centerWidth) {
	    printProgramInfo(System.out, centerWidth, true);
	}


	/**
	 * Display a program information banner using things we know about here
	 * to {@link System#out}, using the given width to display, optionally colored.
	 *
	 * @param centerWidth	The width to use for centering (0 = don't center,
	 *			negative = center over longest string).
	 * @param colors	Whether to use colors.
	 */
	public static void printProgramInfo(int centerWidth, boolean colors) {
	    printProgramInfo(System.out, centerWidth, colors);
	}


	/**
	 * Display a program information banner using things we know about here
	 * to the given {@link PrintStream}.
	 *
	 * @param ps		The print stream to display the info to.
	 * @param centerWidth	The width to use for centering (0 = don't center,
	 *			negative = center over longest string).
	 * @param colors	Whether or not to use colors.
	 */
	public static void printProgramInfo(PrintStream ps, int centerWidth, boolean colors) {
	    String debugInfo   = isDebugBuild() ? Intl.getString("util#env.debug") : "";
	    String versionInfo = Intl.formatString(
		"util#env.version",
		getAppVersion(), getAppBuild(), debugInfo);
	    String buildInfo = Intl.formatString(
		"util#env.build",
		getBuildDate(), getBuildTime());
	    String productName = getProductName();
	    String copyright   = getCopyrightNotice();
	    String javaVersion = Intl.formatString(
		"util#env.javaVersion",
		javaVersion(),
		dataModel());

	    int lineWidth = centerWidth;

	    // Even if a width is given, the strings might be longer still
	    lineWidth = Math.max(lineWidth, productName.length());
	    lineWidth = Math.max(lineWidth, versionInfo.length());
	    lineWidth = Math.max(lineWidth, buildInfo.length());
	    lineWidth = Math.max(lineWidth, copyright.length());
	    lineWidth = Math.max(lineWidth, javaVersion.length());
	    // If the given width wasn't sufficient for all the text
	    // then give a little extra to make it look better
	    if (lineWidth > centerWidth)
		lineWidth += 2;

	    String underline = CharUtil.makeStringOfChars('=', lineWidth);

	    ps.println();
	    if (colors)
		ps.println(BLACK_BRIGHT + underline + BLUE_BOLD_BRIGHT);
	    else
		ps.println(underline);

	    if (centerWidth == 0) {
		ps.println(" " + productName);
		if (colors) {
		    ps.println(GREEN + " " + versionInfo);
		    ps.println(BLACK_BRIGHT);
		}
		else {
		    ps.println(" " + versionInfo);
		    ps.println();
		}
		ps.println(" " + buildInfo);
		ps.println(" " + copyright);
		ps.println(" " + javaVersion);
	    } 
	    else {
		int width = (lineWidth + 1) / 2 * 2;
		String version = CharUtil.padToWidth(versionInfo, -width, CENTER);

		// Negative width puts the odd space on the right
		ps.println(CharUtil.padToWidth(productName, -width, CENTER));
		if (colors) {
		    ps.println(GREEN + version);
		    ps.println(BLACK_BRIGHT);
		}
		else {
		    ps.println(version);
		    ps.println();
		}
		ps.println(CharUtil.padToWidth(buildInfo, -width, CENTER));
		ps.println(CharUtil.padToWidth(copyright, -width, CENTER));
		ps.println(CharUtil.padToWidth(javaVersion, -width, CENTER));
	    }
	    if (colors)
		ps.println(underline + RESET);
	    else
		ps.println(underline);
	    ps.println();
	}

}

