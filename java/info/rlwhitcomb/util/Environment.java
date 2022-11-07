/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2017,2019-2022 Roger L. Whitcomb.
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
 *	29-Jan-2021 (rlwhitcomb)
 *	    New method to return a standard platform name. Use new Intl
 *	    exception variants.
 *	09-Feb-2021 (rlwhitcomb)
 *	    Rework "printProgramInfo" using the new coloring method.
 *	23-Feb-2021 (rlwhitcomb)
 *	    Make separate methods for each string printed by "printProgramInfo"
 *	    so GUI programs can format a display with exactly the same info.
 *	25-Feb-2021 (rlwhitcomb)
 *	    Make a "userDownloadsDirectory" method.
 *	25-Feb-2021 (rlwhitcomb)
 *	    Add a main program that prints out selected pieces of information
 *	    to the console. Color the information.
 *	01-Mar-2021 (rlwhitcomb)
 *	    Tweaks to the "timeThis" functions.
 *	07-Jul-2021 (rlwhitcomb)
 *	    Implement "consoleSize" function.
 *	09-Jul-2021 (rlwhitcomb)
 *	    Private constructor since this is a utility class (all static methods).
 *	09-Jul-2021 (rlwhitcomb)
 *	    Switch to using either "release.build" or "debug.build" from the properties.
 *	26-Jul-2021 (rlwhitcomb)
 *	    Add an accessor to get the Implementation-Version (as a SemanticVersion).
 *	03-Aug-2021 (rlwhitcomb)
 *	    Display screen size in the environment list; tweak the colors.
 *	08-Aug-2021 (rlwhitcomb)
 *	    Use box-drawing character to color display of program info.
 *	29-Aug-2021 (rlwhitcomb)
 *	    Trap errors from "stty" if we're running headless (as in CI builds).
 *	25-Oct-2021 (rlwhitcomb)
 *	    New SemanticVersion of program version information.
 *	18-Nov-2021 (rlwhitcomb)
 *	    New method for getting SemanticVersion of an arbitrary class / package.
 *	19-Nov-2021 (rlwhitcomb)
 *	    #98: Add separate methods for screen width and height.
 *	01-Dec-2021 (rlwhitcomb)
 *	    #120: Eliminate other extraneous ".version" strings.
 *	11-Jan-2022 (rlwhitcomb)
 *	    #204: Report the number of available processors, plus max and total memory.
 *	    Allow comma-separated arguments on command line.
 *	12-Jan-2022 (rlwhitcomb)
 *	    #204: Also free memory.
 *	17-Jan-2022 (rlwhitcomb)
 *	    Add variants of "timeThis" with function name / description.
 *	12-Apr-2022 (rlwhitcomb)
 *	    #269: Add "getMainClassName" and "loadMainProgramInfo".
 *	13-Apr-2022 (rlwhitcomb)
 *	    #269: Only add the default main program info if it hasn't already been set
 *	    by a prior call to "loadProgramInfo".
 *	18-Apr-2022 (rlwhitcomb)
 *	    #270: Call "loadMainProgramInfo" inside the lowest-level of "printProgramInfo".
 *	    This will eliminate all but one external call to this method (in Calc, for
 *	    "info.version") and makes it basically automatic now. Cache main class name.
 *	    Print more info in "printProgramInfo".
 *	09-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 *	18-Aug-2022 (rlwhitcomb)
 *	    #445: New flavor of "printProgramInfo" with just colored flag.
 *	27-Sep-2022 (rlwhitcomb)
 *	    #491: Correct the setting of Java major version.
 *	25-Oct-2022 (rlwhitcomb)
 *	    #536: Methods to set and test the system property for testing.
 *	27-Oct-2022 (rlwhitcomb)
 *	    #538: New method to return the current CLASSPATH as an array of URLs,
 *	    suitable for use with a URLClassLoader.
 *	30-Oct-2022 (rlwhitcomb)
 *	    #536: Rename and repurpose the "in testing" property and methods.
 *	06-Nov-2022 (rlwhitcomb)
 *	    Fix wrong end-of-color tag.
 */
package info.rlwhitcomb.util;

import de.onyxbits.SemanticVersion;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
	private static final SemanticVersion IMPLEMENTATION_VERSION;

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
	 * The system property, mostly used during testing, to allow or not the processing
	 * of options from an environment variable at startup.
	 */
	private static final String ALLOW_ENV_OPTIONS_PROPERTY = "info.rlwhitcomb.EnvOptions";

	/**
	 * The set of properties read from various files: "build.properties", "build.number",
	 * and "version.properties", which should include titles and versions for the major
	 * main programs, as well as other build-related information.
	 */
	private static Properties buildProperties = null;

	/**
	 * Number format for grouping thousands (for memory size, etc.)
	 */
	private static NumberFormat longFormat = null;

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
	 * The parsed structure from {@link #APP_VERSION}.
	 */
	private static SemanticVersion BUILD_VERSION = null;
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
	 * Cached main class name.
	 * @see #getMainClassName
	 */
	private static String cachedMainClassName = null;

	/**
	 * Cached version of the current CLASSPATH, as an array of URLs.
	 */
	private static URL[] urlClassPath = null;


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


	/**
	 * An enumeration of available pieces of information to display to the console
	 * in the {@link #main} method.
	 * <p> Each enum value defines aliases that can be entered by the user to specify
	 * this piece of information, as well as the function used to provide the value.
	 */
	public static enum Env
	{
		ALL		(null, "All", "all", "a"),
		CURRENT_DIR	(Environment::currentDirectoryString, "Current Directory", "currentDir", "cd"),
		CURRENT_USER	(Environment::currentUser, "Current User", "currentUser", "user", "u"),
		BUILD		(Environment::getAppBuild, "Build", "build", "bld", "b"),
		VERSION		(Environment::getAppVersion, "Version", "version", "vers", "v"),
		BUILD_DATE	(Environment::getBuildDate, "Build Date", "buildDate", "date", "d"),
		BUILD_TIME	(Environment::getBuildTime, "Build Time", "buildTime", "time", "t"),
		BUILD_DATE_TIME	(Environment::getProductBuildDateTime, "Build Date/Time", "buildDateTime", "datetime", "dt"),
		COPYRIGHT	(Environment::getCopyrightNotice, "Copyright", "copyright", "copy", "c"),
		JAVA_VERSION	(Environment::javaVersionModel, "Java Version", "javaVersion", "javaVer", "java", "jv"),
		MAIN_CLASS	(Environment::getMainClassName, "Main Class", "mainClass", "mainCls", "main", "mc"),
		PROCESS_ID	(Environment::getProcessID, "Process ID", "processID", "process", "pid"),
		NATIVE_PATH_VAR	(Environment::getNativePathVar, "Native Code Path Variable", "nativePathVar", "npv"),
		PRODUCT_NAME	(Environment::getProductName, "Product Name", "productName", "prodName", "name"),
		PRODUCT_VERSION	(Environment::productVersion, "Product Version", "productVersion", "prodVersion", "prodVer", "pv"),
		IMPL_VERSION	(Environment::implementationVersionString, "Implementation Version", "implementationVersion", "implVersion", "iv"),
		HOST_NAME	(Environment::hostName, "Host Name", "hostName", "host", "h"),
		OS_VERSION	(Environment::osVersion, "O/S Version", "osVersion", "osVer", "ov"),
		PLATFORM	(Environment::platform, "Platform", "platform", "plat", "p"),
		PLATFORM_ID	(Environment::platformIdentifier, "Platform ID", "platformID", "platID", "pid"),
		PROCESSORS	(Environment::processors, "Processors", "process", "proc"),
		MAX_MEMORY	(Environment::maxMemory, "Maximum Memory", "maxMemory", "maxm"),
		FREE_MEMORY	(Environment::freeMemory, "Free Memory", "freeMemory", "freem"),
		TOTAL_MEMORY	(Environment::totalMemory, "Total Memory", "totalMemory", "totm"),
		TEMP_DIR	(Environment::tempDirName, "Temp Directory", "tempDir", "tempd", "temp", "td"),
		USER_HOME_DIR	(Environment::userHomeDirString, "Home Directory", "homeDir", "homed", "home", "h"),
		SCREEN_SIZE	(Environment::getScreenSize, "Screen Size", "screenSize", "screen", "size", "ss");

		private Supplier<String> supplier;
		private String valueTitle;
		private String[] options;

		private Env(final Supplier<String> func, final String title, final String... opts) {
		    supplier = func;
		    valueTitle = title;
		    options = opts;
		}

		public Supplier<String> getSupplier() {
		    return supplier;
		}

		public String getTitle() {
		    return valueTitle;
		}

		public static Optional<Env> match(final String input) {
		    return Arrays.stream(values())
			  .filter(e -> Arrays.stream(e.options)
					     .anyMatch(s -> s.equalsIgnoreCase(input)))
			  .findFirst();
		}

		/**
		 * @return The set of all the available options (except {@link #ALL}
		 * because that's silly.
		 */
		public static Set<Env> all() {
		    Set<Env> set = EnumSet.allOf(Env.class);
		    set.remove(ALL);
		    return set;
		}
	}


	static {
	    String[] parts = JAVA_RUNTIME_VERSION.split("[\\.+\\-_]");
	    if (parts[0].equals("1") && parts.length > 1)
		javaMajorVersion = Integer.parseInt(parts[1]);
	    else
		javaMajorVersion = Integer.parseInt(parts[0]);

	    IMPLEMENTATION_VERSION = getVersion(Environment.class);

	    longFormat = NumberFormat.getInstance();
	    longFormat.setGroupingUsed(true);
	}


	/**
	 * Private constructor so no one can instantiate another.
	 */
	private Environment() {
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
	 * @return A string representation of the current directory.
	 * @see #currentDirectory
	 */
	public static String currentDirectoryString() {
	    return currentDirectory().getPath();
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
	 * @return The user's home directory as a string.
	 * @see #userHomeDir
	 */
	public static String userHomeDirString() {
	    return userHomeDir().getPath();
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
	 * Get the user's "downloads" directory.
	 *
	 * @return	The value of the 'user.home' system property with the
	 *		"Downloads" (or equivalent) subdirectory.
	 * @see #USER_HOME
	 */
	public static File userDownloadsDirectory() {
	    File downDir = new File(USER_HOME, "Downloads");
	    if (downDir.exists() && downDir.isDirectory())
		return downDir;
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
	 * Returns a standard platform identifier.
	 *
	 * @return One of: <code>"windows"</code>, <code>"linux"</code>,
	 * <code>"osx"</code>, or <code>"unix"</code> signifying which
	 * kind of platform we are running on.
	 */
	public static String platformIdentifier() {
	    String platform = "unix";

	    if (osIsWindows)
		platform = "windows";
	    else if (osIsLinux)
		platform = "linux";
	    else if (osIsOSX)
		platform = "osx";

	    return platform;
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
	 * Read the <code>"Implementation-Version"</code> attribute of the .jar file
	 * and parse into a semantic version.
	 *
	 * @return the implementation version.
	 */
	public static SemanticVersion implementationVersion() {
	    return IMPLEMENTATION_VERSION;
	}


	/**
	 * @return Formatted string of the implementation version.
	 *
	 * @see #implementationVersion
	 */
	public static String implementationVersionString() {
	    return IMPLEMENTATION_VERSION.toString();
	}


	/**
	 * Get a formatted string of the implementation version.
	 *
	 * @return The formatted implementation version.
	 */
	public static String getImplementationVersion() {
	    return Intl.formatString("util#env.implVersion", IMPLEMENTATION_VERSION);
	}


	/**
	 * Get the current program version information.
	 *
	 * @return The program version information, determined the same way
	 *	   as {@link #printProgramInfo}.
	 */
	public static SemanticVersion programVersion() {
	    // First build a version string compatible with the semantic versioning spec
	    String versionSpec = String.format("%1$s%2$s+%3$s",
		getAppVersion(),
		isDebugBuild() ? "-debug" : "",
		getAppBuild());
	    try {
		return new SemanticVersion(versionSpec);
	    }
	    catch (ParseException pe) {
		// This is guaranteed to have a value, but this *should* never happen
		return IMPLEMENTATION_VERSION;
	    }
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
	 * @return The version and data model of Java we're running under.
	 *
	 * @see #javaVersion
	 * @see #dataModel
	 */
	public static String javaVersionModel() {
	    return Intl.formatString("util#env.javaVersionModel", javaVersion(), dataModel());
	}


	/**
	 * @return The major version of Java we're running under: 6, 7, 8, 9, 10, 11, etc.
	 */
	public static int javaMajorVersion() {
	    return javaMajorVersion;
	}


	/**
	 * @return The complete Java runtime version string.
	 *
	 * @see #javaVersion
	 * @see #dataModel
	 */
	public static String getJavaVersion() {
	    return Intl.formatString("util#env.javaVersion", javaVersion(), dataModel());
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
	    throw new Intl.IllegalStateException("util#env.unknownDataModel", DATA_MODEL);
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
	    timeThis(null, func, Optional.empty());
	}


	/**
	 * Time the named {@link Runnable} (can be a functional interface)
	 * and display the results on {@link System#out}.
	 *
	 * @param name Name or description of the function we're going to execute.
	 * @param func The function to execute and time.
	 */
	public static void timeThis(final String name, final Runnable func) {
	    timeThis(name, func, Optional.empty());
	}


	/**
	 * Time the passed in {@link Runnable} (can be a functional interface)
	 * and display the results on {@link System#out}.
	 *
	 * @param name Name or description of the function we're going to execute.
	 * @param func The function to execute and time.
	 * @param errorReporter Optional method to report exceptions thrown during timing.
	 * If not specified, defaults to printing an error message to {@link System#err}.
	 */
	public static void timeThis(final String name, final Runnable func, final Optional<Consumer<Throwable> > errorReporter) {
	    long startTime = highResTimer();

	    try {
		func.run();
	    }
	    catch (Throwable e) {
		if (errorReporter.isPresent()) {
		    errorReporter.get().accept(e);
		}
		else {
		    if (CharUtil.isNullOrEmpty(name))
			Intl.errFormat("util#env.timeThisError", Exceptions.toString(e));
		    else
			Intl.errFormat("util#env.timeThisErrorNamed", name, Exceptions.toString(e));
		}
	    }
	    finally {
		long endTime   = highResTimer();
		double seconds = timerValueToSeconds(endTime - startTime);

		if (CharUtil.isNullOrEmpty(name))
		    Intl.outFormat("util#env.timeThis", seconds);
		else
		    Intl.outFormat("util#env.timeThisNamed", name, seconds);
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
	    return timeThis(null, func, Optional.empty());
	}


	/**
	 * Time the named {@link Callable} (can be a functional interface)
	 * and display the results on {@link System#out}.
	 *
	 * @param <V>  Type of value returned from the function.
	 * @param name Name or description of the function we're going to execute.
	 * @param func The function to execute and time.
	 * @return     The return value from the callable function.
	 */
	public static <V> V timeThis(final String name, final Callable<V> func) {
	    return timeThis(name, func, Optional.empty());
	}


	/**
	 * Time the passed in {@link Callable} (can be a functional interface)
	 * and display the results on {@link System#out}.
	 *
	 * @param <V>  Type of value returned from the function.
	 * @param name Name or description of the function we're going to execute.
	 * @param func The function to execute and time.
	 * @param errorReporter Optional method to report exceptions thrown during timing.
	 * If not specified, defaults to printing an error message to {@link System#err}.
	 * @return     The return value from the callable function.
	 */
	public static <V> V timeThis(final String name, final Callable<V> func, final Optional<Consumer<Throwable> > errorReporter) {
	    long startTime = highResTimer();
	    V result = null;

	    try {
		result = func.call();
	    }
	    catch (Throwable e) {
		if (errorReporter.isPresent()) {
		    errorReporter.get().accept(e);
		}
		else {
		    if (CharUtil.isNullOrEmpty(name))
			Intl.errFormat("util#env.timeThisError", Exceptions.toString(e));
		    else
			Intl.errFormat("util#env.timeThisErrorNamed", name, Exceptions.toString(e));
		}
	    }
	    finally {
		long endTime   = highResTimer();
		double seconds = timerValueToSeconds(endTime - startTime);

		if (CharUtil.isNullOrEmpty(name))
		    Intl.outFormat("util#env.timeThis", seconds);
		else
		    Intl.outFormat("util#env.timeThisNamed", name, seconds);
	    }
	    return result;
	}


	/**
	 * Find and parse the semantic version from an arbitrary class (from its "Implementation-Version"
	 * attribute in the corresponding .jar file).
	 *
	 * @param cls	The class to interrogate.
	 * @return	The implementation version of the class (except in the case of error, in which case
	 *		the version will be retrieved from {@link #getBuildVersion}.
	 */
	public static SemanticVersion getVersion(final Class<?> cls) {
	    SemanticVersion semVer;
	    try {
		semVer = new SemanticVersion(cls);
	    }
	    catch (ParseException ex) {
		semVer = getBuildVersion();
	    }
	    return semVer;
	}


	/**
	 * Returns the process ID of the current process.
	 * @return	"0" if the process ID cannot be determined using the
	 *		management bean (it does work on all the platforms
	 *		I have available currently).
	 * @see <a href="http://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id">http://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id</a>
	 */
	public static String getProcessID() {
	    final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
	    // The format is (always?) pid@host
	    final int index = jvmName.indexOf('@');

	    if (index > 0) {
		try {
		    return jvmName.substring(0, index);
		} catch (NumberFormatException e) {
		    // ignore
		}
	    }
	    return "0";
	}


	/**
	 * Returns a nicely-formatted version of the process ID value, suitable for presentation.
	 *
	 * @return The process ID information.
	 * @see #getProcessID
	 */
	public static String getProcess() {
	    return Intl.formatString("util#env.processID", getProcessID());
	}


	/**
	 * Returns the main class name (from the JVM command line) used to invoke the application.
	 * <p> We will run the "jps -l" command and parse the output. The output will look like:
	 * <pre>65841 info.rlwhitcomb.util.Lists
	 * 65845 jdk.jcmd/sun.tools.jps.Jps</pre>
	 * which are parsed into two parts: the process ID and the main class name being run by that
	 * process. So we will match the process ID against our own value of it (from {@link #getProcessID})
	 * and return the main class that corresponds to us.
	 *
	 * @return	Fully-qualified class name that is the main program of this application.
	 */
	public static String getMainClassName() {
	    if (cachedMainClassName != null)
		return cachedMainClassName;

	    try {
		File f = FileUtilities.createTempFile("class");

		ProcessBuilder pb = new ProcessBuilder("jps", "-l")
			.inheritIO().redirectOutput(f).redirectErrorStream(true);

		pb.start().waitFor();

		List<String> lines = FileUtilities.readFileAsLines(f);

		if (!f.delete())
		    f.deleteOnExit();

		String pid = getProcessID();

		for (String line : lines) {
		    String parts[] = line.split("\\s+");
		    if (parts.length == 2) {
			if (pid.equals(parts[0])) {
			    cachedMainClassName = parts[1];
			    return cachedMainClassName;
			}
		    }
		}
	    }
	    catch (Exception e) {
	    }

	    return "";
	}


	/**
	 * Returns a user-readable version of the main class name (with header info, basically).
	 *
	 * @return Main class name formatted for presentation.
	 * @see #getMainClassName
	 */
	public static String getMainClass() {
	    return Intl.formatString("util#env.mainClass", getMainClassName());
	}


	/**
	 * Compute the (character) size of the terminal, using native system commands.
	 *
	 * @return A {@link Dimension} object containing the current character width
	 * and height of the console.
	 */
	public static Dimension consoleSize() {
	    Dimension defaultSize = new Dimension(80, 25);

	    try {
		File f = FileUtilities.createTempFile("size");
		ProcessBuilder pb;

		if (osIsWindows) {
		    pb = new ProcessBuilder(
			"powershell",
			"-Command",
			"Out-File",
			"-InputObject",
			"$host.UI.RawUI.WindowSize.Height",
			"-Encoding",
			"ASCII",
			"-FilePath",
			f.getPath(),
			";",
			"Out-File",
			"-InputObject",
			"$host.UI.RawUI.WindowSize.Width",
			"-Encoding",
			"ASCII",
			"-FilePath",
			f.getPath(),
			"-Append").inheritIO();
		}
		else {
		    pb = new ProcessBuilder(
			"stty",
			"size").inheritIO().redirectOutput(f).redirectErrorStream(true);
		}
		pb.start().waitFor();

		String outputStream = FileUtilities.readFileAsString(f);

		if (!f.delete())
		    f.deleteOnExit();

		if (!osIsWindows) {
		    if (outputStream.indexOf("Inappropriate ioctl for device") >= 0) {
			return defaultSize;
		    }
		}

		String[] sizes = outputStream.split("\\s+");
		return new Dimension(Integer.valueOf(sizes[1]), Integer.valueOf(sizes[0]));
	    }
	    catch (Exception ex) {
		// If we can't determine the dimensions, return a default size
		return defaultSize;
	    }
	}


	/**
	 * Get just the screen width, using the {@link #consoleSize} method.
	 * <p> It is more efficient, if both width and height are needed, to
	 * call {@link #consoleSize} instead of this method.
	 *
	 * @return The current width of the screen (in characters).
	 */
	public static int consoleWidth() {
	    Dimension size = consoleSize();
	    return size.width;
	}


	/**
	 * Get just the screen height, using the {@link #consoleSize} method.
	 * <p> It is more efficient, if both width and height are needed, to
	 * call {@link #consoleSize} instead of this method.
	 *
	 * @return The current height of the screen (in characters).
	 */
	public static int consoleHeight() {
	    Dimension size = consoleSize();
	    return size.height;
	}


	/**
	 * Return a human-readable string of the screen size, found from {@link #consoleSize}.
	 *
	 * @return The printable screen size.
	 */
	public static String getScreenSize() {
	    Dimension size = consoleSize();
	    return Intl.formatString("util#env.screenSize", size.width, size.height);
	}


	/**
	 * Get the current {@var CLASSPATH} as an array of URLs, suitable for use with
	 * {@link java.net.URLClassLoader}.
	 *
	 * @return The CLASSPATH in URL form.
	 */
	public static URL[] getURLClassPath() {
	    if (urlClassPath == null) {
		String classPath = System.getenv("CLASSPATH");
		String[] paths = classPath.split(PATH_SEPARATOR);
		urlClassPath = new URL[paths.length];
		int i = 0;
		for (String path : paths) {
		    File f = new File(path);
		    try {
			urlClassPath[i++] = f.toURI().toURL();
		    }
		    catch (MalformedURLException mue) {
			; // Oops!
		    }
		}
	    }
	    return urlClassPath;
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
	 * @return Whether or not this is a DEBUG build (set externally in the "build.properties" file).
	 */
	public static boolean isDebugBuild() {
	    readBuildProperties();
	    return IS_DEBUG_BUILD;
	}


	/**
	 * @return The product version string.
	 *
	 * @see #getAppVersion
	 * @see #getAppBuild
	 * @see #isDebugBuild
	 */
	public static String productVersion() {
	    String debugInfo = isDebugBuild() ? Intl.getString("util#env.debug") : "";
	    return Intl.formatString("util#env.versionBuild", getAppVersion(), getAppBuild(), debugInfo);
	}


	/**
	 * @return The complete product version string, formatted for display.
	 *
	 * @see #productVersion
	 */
	public static String getProductVersion() {
	    return Intl.formatString("util#env.version", productVersion());
	}


	/**
	 * @return The unadulterated base application version.
	 */
	public static SemanticVersion getBuildVersion() {
	    readBuildProperties();
	    return BUILD_VERSION;
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
	 * @return The complete product build date/time string.
	 *
	 * @see #getBuildDate
	 * @see #getBuildTime
	 */
	public static String getProductBuildDateTime() {
	    return Intl.formatString("util#env.build", getBuildDate(), getBuildTime());
	}


	/**
	 * Report the number of available processors.
	 *
	 * @return The number of processors available via the {@link Runtime} class.
	 */
	public static int numberOfProcessors() {
	    return Runtime.getRuntime().availableProcessors();
	}


	/**
	 * String version of {@link #numberOfProcessors}.
	 *
	 * @return The number of available processors.
	 */
	public static String processors() {
	    return String.valueOf(numberOfProcessors());
	}


	/**
	 * Report the maximum memory available.
	 *
	 * @return The maximum memory available via the {@link Runtime} class (in bytes).
	 */
	public static long maximumMemorySize() {
	    return Runtime.getRuntime().maxMemory();
	}


	/**
	 * String version of the {@link #maximumMemorySize}.
	 *
	 * @return The maximum memory size.
	 */
	public static String maxMemory() {
	    return longFormat.format(maximumMemorySize());
	}


	/**
	 * Report the free memory available.
	 *
	 * @return The free memory available from the {@link Runtime} class (in bytes).
	 */
	public static long freeMemorySize() {
	    return Runtime.getRuntime().freeMemory();
	}


	/**
	 * String version of the {@link #freeMemorySize}.
	 *
	 * @return The free memory size.
	 */
	public static String freeMemory() {
	    return longFormat.format(freeMemorySize());
	}


	/**
	 * Report the total memory available.
	 *
	 * @return The total memory available via the {@link Runtime} class (in bytes).
	 */
	public static long totalMemorySize() {
	    return Runtime.getRuntime().totalMemory();
	}


	/**
	 * String version of the {@link #totalMemorySize}.
	 *
	 * @return The total memory size.
	 */
	public static String totalMemory() {
	    return longFormat.format(totalMemorySize());
	}


	/**
	 * Sets the system property to allow getting options from the environment (not allowed
	 * usually during testing to ensure a consistent environment).
	 *
	 * @param allow Whether to allow {@code xxx_OPTIONS} environment variable to set options.
	 * @see #allowEnvOptions
	 */
	public static void setAllowEnvOptions(boolean allow) {
	    // Default if the property is not set will be true
	    if (allow)
		System.clearProperty(ALLOW_ENV_OPTIONS_PROPERTY);
	    else
		System.setProperty(ALLOW_ENV_OPTIONS_PROPERTY, Boolean.FALSE.toString());
	}


	/**
	 * Are we allowing environment variables to preset program options?
	 *
	 * @return The state of the system property set during testing.
	 */
	public static boolean allowEnvOptions() {
	    String value = System.getProperty(ALLOW_ENV_OPTIONS_PROPERTY, Boolean.TRUE.toString());
	    return Boolean.valueOf(value);
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

		try {
		    BUILD_VERSION = new SemanticVersion(APP_VERSION);
		}
		catch (ParseException pe) {
		    BUILD_VERSION = new SemanticVersion();
		}

		String debugBuild   = buildProperties.getProperty("debug.build");
		String releaseBuild = buildProperties.getProperty("release.build");
		if (debugBuild != null)
		    IS_DEBUG_BUILD = CharUtil.getBooleanValue(debugBuild);
		else if (releaseBuild != null)
		    IS_DEBUG_BUILD = !CharUtil.getBooleanValue(releaseBuild);
		else
		    IS_DEBUG_BUILD = false;
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
		    // The real program versions all start with Upper Case letters
		    if (Character.isLowerCase(name.charAt(0))) {
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
	 * specified by the main program currently being run (as determined by
	 * {@link #getMainClassName}), but only if this info hasn't already been set by
	 * {@link #setProductName} or {@link #setAppVersion}.
	 */
	public static void loadMainProgramInfo() {
	    if (overloadedProductName == null && overloadedAppVersion == null) {
		loadProgramInfo(ClassUtil.parseModuleClassName(getMainClassName()).getSimpleClassName());
	    }
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
	 * Display a possibly colored program information banner using things we know about here
	 * to {@link System#out}, using the longest string to center on.
	 *
	 * @param colors	Whether to use colors.
	 */
	public static void printProgramInfo(boolean colors) {
	    printProgramInfo(System.out, -1, colors);
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


	private static void println(PrintStream ps, boolean colors, String formatKey, Object... args) {
	    ps.println(ConsoleColor.color(Intl.formatString(formatKey, args), colors));
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
	    // If no one has overridden the program info previously, now that we absolutely need it,
	    // load the default (if possible) before proceeding.
	    loadMainProgramInfo();

	    String productName = getProductName();
	    String versionInfo = getProductVersion();
	    String buildInfo   = getProductBuildDateTime();
	    String implVersion = getImplementationVersion();
	    String copyright   = getCopyrightNotice();
	    String javaVersion = getJavaVersion();
	    String mainClass   = getMainClass();
	    String processID   = getProcess();

	    int lineWidth = centerWidth;

	    // Even if a width is given, the strings might be longer still
	    lineWidth = Math.max(lineWidth, productName.length());
	    lineWidth = Math.max(lineWidth, versionInfo.length());
	    lineWidth = Math.max(lineWidth, buildInfo.length());
	    lineWidth = Math.max(lineWidth, implVersion.length());
	    lineWidth = Math.max(lineWidth, copyright.length());
	    lineWidth = Math.max(lineWidth, javaVersion.length());
	    lineWidth = Math.max(lineWidth, mainClass.length());
	    lineWidth = Math.max(lineWidth, processID.length());

	    // If the given width wasn't sufficient for all the text
	    // then give a little extra to make it look better
	    if (lineWidth > centerWidth)
		lineWidth += 2;

	    String underline = CharUtil.makeStringOfChars(colors ? '\u2550' : '=', lineWidth);

	    ps.println();
	    println(ps, colors, "util#env.otherInfo", "", underline);

	    if (centerWidth == 0) {
		println(ps, colors, "util#env.productInfo", " ", productName);
		println(ps, colors, "util#env.versionInfo", " ", versionInfo);
		ps.println();
		println(ps, colors, "util#env.otherInfo", " ", implVersion);
		println(ps, colors, "util#env.otherInfo", " ", buildInfo);
		println(ps, colors, "util#env.otherInfo", " ", copyright);
		ps.println();
		println(ps, colors, "util#env.otherInfo", " ", javaVersion);
		println(ps, colors, "util#env.otherInfo", " ", mainClass);
		println(ps, colors, "util#env.otherInfo", " ", processID);
	    } 
	    else {
		int width = (lineWidth + 1) / 2 * 2;

		// Negative width puts the odd space on the right
		String product = CharUtil.padToWidth(productName, -width, CENTER);
		String version = CharUtil.padToWidth(versionInfo, -width, CENTER);
		String implVer = CharUtil.padToWidth(implVersion, -width, CENTER);
		String build   = CharUtil.padToWidth(buildInfo,   -width, CENTER);
		String copy    = CharUtil.padToWidth(copyright,   -width, CENTER);
		String java    = CharUtil.padToWidth(javaVersion, -width, CENTER);
		String main    = CharUtil.padToWidth(mainClass,   -width, CENTER);
		String pid     = CharUtil.padToWidth(processID,   -width, CENTER);

		println(ps, colors, "util#env.productInfo", "", product);
		println(ps, colors, "util#env.versionInfo", "", version);
		ps.println();
		println(ps, colors, "util#env.otherInfo", "", implVer);
		println(ps, colors, "util#env.otherInfo", "", build);
		println(ps, colors, "util#env.otherInfo", "", copy);
		ps.println();
		println(ps, colors, "util#env.otherInfo", "", java);
		println(ps, colors, "util#env.otherInfo", "", main);
		println(ps, colors, "util#env.otherInfo", "", pid);
	    }
	    println(ps, colors, "util#env.otherInfo", "", underline);
	    ps.println();
	}


	/**
	 * A main program that prints out selected pieces of our precious information
	 * to {@link System#out}.
	 *
	 * @param args	The parsed command line arguments, which will be a list of standard
	 *		identifiers from the {@link Env} list to print out.
	 */
	public static void main(final String[] args) {
	    Set<Env> valuesToDisplaySet = EnumSet.noneOf(Env.class);
	    for (String arg : args) {
		String parts[] = arg.split("[,;]");
		for (String part : parts) {
		    Optional<Env> opt = Env.match(part);
		    opt.ifPresent(e -> {
			if (e.equals(Env.ALL))
			    valuesToDisplaySet.addAll(Env.all());
			else
			    valuesToDisplaySet.add(e);
		    });
		}
	    }

	    if (valuesToDisplaySet.isEmpty())
		valuesToDisplaySet.addAll(Env.all());

	    valuesToDisplaySet.forEach(e ->
		System.out.println(ConsoleColor.color(String.format("<Bk!>%1$s:<.> <Gr>%2$s<.>", e.getTitle(), e.getSupplier().get()))));
	}


}

