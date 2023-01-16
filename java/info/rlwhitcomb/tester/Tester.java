/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2012-2023 Roger L. Whitcomb.
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
 *	Unit test engine.
 *
 *  History:
 *	29-Aug-2012 (rlwhitcomb)
 *	    Created.
 *	30-Aug-2012 (rlwhitcomb)
 *	    Allow comments in the test description file;
 *	    report the number of tests and the composite results.
 *	16-Oct-2012 (rlwhitcomb)
 *	    Add a proper diff output when the results don't compare.
 *	30-Oct-2012 (rlwhitcomb)
 *	    Add initialization of text resources.
 *	01-Nov-2012 (rlwhitcomb)
 *	    And more text resources.
 *	13-Nov-2012 (rlwhitcomb)
 *	    Send test class the private "-echo:true" flag so
 *	    the input is echoed to the output (to make the
 *	    canons more readable).
 *	15-Nov-2012 (rlwhitcomb)
 *	    Allow blank lines in the test description file, and
 *	    C++ style line comments.
 *	15-Nov-2012 (rlwhitcomb)
 *	    Add "-log" option to at least print the test names.
 *	20-Dec-2012 (rlwhitcomb)
 *	    Add "-time" option to print test timing also.
 *	10-Jan-2013 (rlwhitcomb)
 *	    Add options to use different canon lines for
 *	    different platforms and different versions.
 *	22-Feb-2013 (rlwhitcomb)
 *	    New feature: $echo to annotate the test result log.
 *	16-May-2013 (rlwhitcomb)
 *	    Fix the regexp for description line so the canon charset
 *	    works correctly (one extra group to not include the comma).
 *	28-Jun-2013 (rlwhitcomb)
 *	    Use new automatic method to initialize the package resources.
 *	21-Jan-2014 (rlwhitcomb)
 *	    Default to ".canon" extension for the canon files.  Add "$inputDir"
 *	    directive to default the canon file directory.  Move error message
 *	    text to the resource file.
 *	04-Mar-2014 (rlwhitcomb)
 *	    Add option to specify an arbitrary class name instead of the default
 *	    to support other types of tests that basically need a command line,
 *	    output canons, and diffs.
 *	12-Mar-2014 (rlwhitcomb)
 *	    Allow "-" in canon file names.
 *	18-Apr-2014 (rlwhitcomb)
 *	    Expand the version checks to allow a range of version or the major[.minor]+
 *	    syntax.
 *	19-Jun-2014 (rlwhitcomb)
 *	    Allow a "$scriptDir" directive in the test description file and send that
 *	    to test class as a "-d dir" parameter.  Tidy up the final result message.
 *	08-Sep-2014 (rlwhitcomb)
 *	    Set the flag saying this is a desktop application.
 *	13-Oct-2014 (rlwhitcomb)
 *	    As a result of using -D for defining variables on the command line, we have
 *	    to use "-dir" now for specifying the default script directory.
 *	12-Mar-2015 (rlwhitcomb)
 *	    Use new FileUtilities method for generating temp files.
 *	24-Mar-2015 (rlwhitcomb)
 *	    Change the way we add the "-echo:true" parameter to the command line so the new
 *	    "-exec" flag can safely eat the remainder of the command line
 *	    without getting this extra string.
 *	24-Apr-2015 (rlwhitcomb)
 *	    Trap any exceptions loading/unloading the instance to determine the version.
 *	27-Apr-2015 (rlwhitcomb)
 *	    Make that any Throwables (the real problems are class loading issues not derived
 *	    from Exception).
 *	31-Aug-2015 (rlwhitcomb)
 *	    Javadoc cleanup (found by Java 8).
 *	06-Jan-2016 (rlwhitcomb)
 *	    More of the same. Use try-with-resources in some places.
 *	09-Aug-2016 (rlwhitcomb)
 *	    We need to allow an empty command line (that is the tests can be run by
 *	    just invoking the class with no arguments).  Changed for use with TestSqlFormatter.
 *	11-Aug-2016 (rlwhitcomb)
 *	    Print an "underline" at the end of the results to separate tests in the nightly build.
 *	    Display the test name inside a starting banner also.
 *	23-Aug-2016 (rlwhitcomb)
 *	    Add an option to display help.
 *	30-Sep-2016 (rlwhitcomb)
 *	    Need to be able to switch canon lines based on the charset.
 *	11-Oct-2016 (rlwhitcomb)
 *	    Use the Options class for command line argument processing.
 *	11-Oct-2016 (rlwhitcomb)
 *	    Refactor to support "$include" processing.  Make a recursive procedure that can be
 *	    called at any level for one description file.  Add processing for the directive.
 *	03-Nov-2016 (rlwhitcomb)
 *	    Add code to generate canons instead of comparing them.  Use common code in FileUtilities
 *	    to read the file in as a single string.
 *	27-Dec-2016 (rlwhitcomb)
 *	    Allow "!" and "//" also as comment indicators (in addition to "#") in canon files.
 *	07-Feb-2017 (rlwhitcomb)
 *	    For convenience in creating new test canons, allow lines that don't begin with any
 *	    of the regular prefixes to be counted as output lines (the most common case).  This way
 *	    the canon could be created simply by capturing the valid output with no postprocessing
 *	    (most of the time).
 *	28-Feb-2017 (rlwhitcomb)
 *	    Add metadata to a test desription to allow a successful result even if the test itself
 *	    exits unsuccessfully (in other words, "success" means the program exited abnormally).
 *	28-Feb-2017 (rlwhitcomb)
 *	    Update the last change by allowing whitespace around the exit code metadata.
 *	13-Jun-2017 (rlwhitcomb)
 *	    Make explicit checks in "driveOneTest" for file existence, etc. instead of relying
 *	    on an IOException to tell us the file doesn't exist or is not readable, etc.
 *	28-Jul-2017 (rlwhitcomb)
 *	    As part of cleanup for users of the Intl class, move the default
 *	    package resource initialization into Intl itself, so all the callers
 *	    don't have to do it.
 *	31-May-2018 (rlwhitcomb)
 *	    Implement "{^platform}" checks as well.
 *	14-Feb-2019 (rlwhitcomb)
 *	    Incidental to some new tests, fix case where "{}" in test file outputs empty line to canon.
 *	15-Mar-2019 (rlwhitcomb)
 *	    Don't use FileInputStream/FileOutputStream due to GC problems b/c of the finalize
 *	    method in these classes. Had to rename our internal "Files" class to avoid conflict with
 *	    the system class of that name.
 *	06-May-2019 (rlwhitcomb)
 *	    Add "$file", "$endfile" and "$delete" statements to create / delete script files for use.
 *	    Allow (and strip) double quotes around argument(s) of internal commands (to allow leading/
 *	    trailing blanks if desired).
 *	21-May-2019 (rlwhitcomb)
 *	    Implement "-version" command.
 *	10-Jul-2019 (rlwhitcomb)
 *	    Add the Java version / bitness to the "-version" output. Move those strings out to
 *	    the intl resources.
 *	11-Jul-2019 (rlwhitcomb)
 *	    Set the product name, and then display that with "-version".
 *	07-Aug-2019 (rlwhitcomb)
 *	    Support "-locale" option, which doesn't help a lot, but does test the Japanese resource files.
 *	    Fix parsing of "-log" option.
 *	06-Jan-2020 (rlwhitcomb)
 *	    Add copyright notice.
 *	13-Feb-2020 (rlwhitcomb)
 *	    Move locale parsing to CharUtil; move message to util resources.
 *	25-Mar-2020 (rlwhitcomb)
 *	    Add a new command-line option AND a new config file option to fail on the first error.
 *	31-Mar-2020 (rlwhitcomb)
 *	    Tweak the underlines in the output.
 *	20-Nov-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	18-Jan-2021 (rlwhitcomb)
 *	    Add a directive for default options; work nicely with the new Testable interface.
 *	23-Jan-2021 (rlwhitcomb)
 *	    Fix obsolete HTML constructs in Javadoc. Add aliases for a couple of the directives.
 *	    Switch description comments to include "--" and use "!" as an alias for commands.
 *	25-Jan-2021 (rlwhitcomb)
 *	    Add "-inputDir" command line option; use that to find input scripts (defaultScriptDir).
 *	    Switch to Environment.printProgramInfo.
 *	29-Jan-2021 (rlwhitcomb)
 *	    Move standard platform code to Environment; one other code tweak.
 *	11-Feb-2021 (rlwhitcomb)
 *	    Get version from test class so version checks work; refactor MajorMinor to Version.
 *	18-Feb-2021 (rlwhitcomb)
 *	    Allow spaces before colon in test description lines.
 *	24-Feb-2021 (rlwhitcomb)
 *	    Allow directives in test description files to start with ":" in addition to "$" or "!".
 *	24-Feb-2021 (rlwhitcomb)
 *	    Tweak some of the initial error messages. Make some errors in the description file fatal
 *	    to abort the whole process.
 *	02-Mar-2021 (rlwhitcomb)
 *	    Another error for empty "-dir:" (which I seem to do often b/c of the different syntax here).
 *	15-Mar-2021 (rlwhitcomb)
 *	    Actually return a failure exit code if any tests fail.
 *	13-Jul-2021 (rlwhitcomb)
 *	    Add "-testclass" option to the command line.
 *	21-Oct-2021 (rlwhitcomb)
 *	    Use new method in Intl to get/test the locale value.
 *	    #36: Implement TESTER_OPTIONS from the environment. Make all function parameters final.
 *	20-Nov-2021 (rlwhitcomb)
 *	    #97: Implement charset for each description file.
 *	01-Dec-2021 (rlwhitcomb)
 *	    #119: Allow default canon charset in description file.
 *	19-Jan-2022 (rlwhitcomb)
 *	    #208: Fix NPE when testclass is not specified after ":".
 *	21-Jan-2022 (rlwhitcomb)
 *	    #217: Use new Options method to parse environment default options.
 *	01-Feb-2022 (rlwhitcomb)
 *	    #231: Move some constants out to Constants class.
 *	18-Feb-2022 (rlwhitcomb)
 *	    #250: Use NewlineOutputStream on test program output when "$ignorelineendings" is specified.
 *	    Use Exceptions everywhere to display I/O errors.
 *	19-Feb-2022 (rlwhitcomb)
 *	    #241: Directive in canon file to compare different output file. Substitute environment
 *	    values on command lines and the result file line. Another main description file directive
 *	    to NOT do substitutions (where the "%" might cause a problem).
 *	24-Feb-2022 (rlwhitcomb)
 *	    Use CharUtil version of "parseCommandLine" for greater control.
 *	25-Feb-2022 (rlwhitcomb)
 *	    #204: Prepare for parallel operation.
 *	12-Apr-2022 (rlwhitcomb)
 *	    #269: New method to load main program info (in Environment).
 *	13-Apr-2022 (rlwhitcomb)
 *	    #269: Call "loadProgramInfo" to set product version explicitly for every main program we test.
 *	18-Apr-2022 (rlwhitcomb)
 *	    #270: Make "loadMainProgramInfo" automatic now.
 *	08-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 *	12-Sep-2022 (rlwhitcomb)
 *	    #443: Move charset and version checks in front of the stream markers; change the marker characters
 *	    to "[. .]" and "{. .}".
 *	21-Sep-2022 (rlwhitcomb)
 *	    #448: Add support for wildcard specs on test command lines.
 *	22-Sep-2022 (rlwhitcomb)
 *	    #448: Sort the results of the wildcard file filter.
 *	23-Sep-2022 (rlwhitcomb)
 *	    #448: Move the wildcard processing code into a separate class (CommandLine) for reuse.
 *	25-Sep-2022 (rlwhitcomb)
 *	    #488: Options to report memory usage; hopefully to debug Windows unit test failures.
 *	26-Sep-2022 (rlwhitcomb)
 *	    #490: Debug printout of the command line.
 *	    #489: Change exit status codes to the standard Testable ones.
 *	27-Sep-2022 (rlwhitcomb)
 *	    #488: Also report free memory size.
 *	16-Oct-2022 (rlwhitcomb)
 *	    #443: Update Javadoc with the new marker conventions.
 *	17-Oct-2022 (rlwhitcomb)
 *	    #443: Change markers (again) to "{{ }}" for version and "[< >]" for charset.
 *	25-Oct-2022 (rlwhitcomb)
 *	    #536: Set system property to indicate testing in progress; but after we process
 *	    our own options.
 *	26-Oct-2022 (rlwhitcomb)
 *	    #540: Move TestFiles and Version out to separate class file. Significant refactoring here.
 *	    #538: Use new ClassLoader for each test class loaded.
 *	29-Oct-2022 (rlwhitcomb)
 *	    #541: Install SecurityManager to capture System.exit from tested classes.
 *	30-Oct-2022 (rlwhitcomb)
 *	    #538: Implement "$noenv" and "$envopt" directives.
 *	27-Dec-2022 (rlwhitcomb)
 *	    Make both the test class constructor and "main" methods accessible before invoking.
 *	16-Jan-2023 (rlwhitcomb)
 *	    #593: Preprocess our test description files using PreProc to allow macro definitions.
 */
package info.rlwhitcomb.tester;

import info.rlwhitcomb.Testable;
import info.rlwhitcomb.directory.Match;
import info.rlwhitcomb.math.NumericUtil;
import info.rlwhitcomb.preproc.PreProc;
import info.rlwhitcomb.util.*;
import name.fraser.neil.plaintext.diff_match_patch;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static info.rlwhitcomb.util.Constants.*;


/**
 * Unit test framework.
 * <p> Reads a test description file that consists of multiple
 * lines, each corresponding to one test.  Each line contains
 * the name of a canon file (with optional character set)
 * and a command line for the test class.
 * <p> There is a command line option to specify the test class
 * name, or it can be supplied (before any tests) in the test
 * description file, and different test classes can be specified
 * this way.
 */
public class Tester
	implements Testable
{
	private static final Pattern DESCRIPTION = Pattern.compile("^(\\s*\\{\\s*(\\d+)\\s*\\}\\s*)?([a-zA-Z0-9_/\\-\\\\\\$\\.]+)(,([\\w\\-]+))?\\s*\\:\\s*(.*)$");
	private static final Pattern DIRECTIVE   = Pattern.compile("^([a-zA-Z]+)(\\s+(.*)\\s*)?$");

	private static final String NEWLINE = Environment.lineSeparator();

	private boolean createCanons = false;
	private boolean verbose = false;
	private boolean log = false;
	private boolean timing = false;
	private boolean memory = false;
	private boolean debug = false;
	private boolean abortOnFirstError = false;
	private boolean defaultAbortOnFirstError = false;

	private long totalElapsedTime = 0L;

	private String currentPlatform;
	private Version currentVersion;

	private File defaultCanonDir  = null;
	private File defaultScriptDir = null;

	private String defaultOptions      = "";
	private String defaultCanonExt     = ".canon";
	private String defaultCanonCharset = "";
	private boolean ignoreLineEndings  = false;
	private boolean noSubstitutions    = false;
	private boolean allowEnvOptions    = false;

	private String testClassName = null;

	private Charset currentCharset = null;


	/**
	 * Custom {@link SecurityManager} that selectively enables/disables {@link System#exit}.
	 */
	@SuppressWarnings("removal")
	private static class ExitSecurityManager extends SecurityManager
	{
		private boolean exitAllowed;

		public ExitSecurityManager() {
		    exitAllowed = false;
		}

		public void setAllowed(final boolean allowed) {
		    exitAllowed = allowed;
		}

		@Override
		public void checkPermission(Permission perm) {
		}

		@Override
		public void checkExit(int status) {
		    if (exitAllowed) {
			super.checkExit(status);
		    }
		    else {
			throw new SecurityException(Integer.toString(status));
		    }
		}
	}


	/**
	 * Consists of a file name and character set to use to read it.
	 */
	private static class DescriptionFile
	{
		private DescriptionFile parent;
		private String descriptionFileName;
		private Charset charset;


		public DescriptionFile(final DescriptionFile outer, final String fileName, final Charset cs) {
		    this.parent = outer;
		    this.descriptionFileName = fileName;
		    this.charset = cs;
		}

		public Charset getCharset() {
		    return charset;
		}

		@Override
		public String toString() {
		    return descriptionFileName;
		}
	}

	private ExitSecurityManager exitSecurityManager = new ExitSecurityManager();

	private List<DescriptionFile> testDescriptionFiles = null;

	private FileWriter outputFileWriter = null;

	/** Options both for the command line and in description files to turn on the "abort on first error" option. */
	private static final String[] ABORT_OPTIONS = {
	    "abortonfirsterror", "abortfirsterror", "aborterror", "abortfirst", "firsterror", "abort", "first"
	};
	/** Options both for the command line and in description files to turn off the "abort on first error" option. */
	private static final String[] NO_ABORT_OPTIONS = {
	    "noabortonfirsterror", "noabortfirsterror", "noaborterror", "noabortfirst", "nofirsterror", "noabort", "nofirst"
	};
	/** Options for description files only to restore the "abort on first error" option to the default default, or what was
	 * specified on the command line (if anything).
	 */
	private static final String[] DEFAULT_ABORT_OPTIONS = {
	    "defaultabortonfirsterror", "defaultabortfirsterror", "defaultaborterror", "defaultabortfirst", "defaultfirsterror", "defaultabort", "defaultfirst",
	    "defaborterror", "defabortfirst", "deffirsterror", "defabort", "deffirst"
	};




	/**
	 * Do a file compare between the two files.
	 *
	 * @param testName	The name of this test (parsed from the description file).
	 * @param canonFile	The file containing the expected results.
	 * @param realFile	The file containing the actual results.
	 * @param cs		The charset used to encode these files originally.
	 * @return		{@link #SUCCESS} for success, {@link #MISMATCH} if
	 *			there were differences, {@link #INPUT_IO_ERROR} if
	 *			an I/O error occurred.
	 */
	private int compareFiles(final String testName, final File canonFile, final File realFile, final Charset cs) {
	    if (verbose)
		Intl.outFormat("tester#compareFiles", testName, canonFile.getPath(), realFile.getPath());

	    try {
		String file1 = FileUtilities.readFileAsString(canonFile, cs, 0);
		String file2 = FileUtilities.readFileAsString(realFile, cs, 0);
		diff_match_patch differ = new diff_match_patch();
		List<diff_match_patch.Patch> patches = differ.patch_make(file1, file2);
		if (!patches.isEmpty()) {
		    Intl.errFormat("tester#mismatch", testName, canonFile.getPath(), realFile.getPath());
		    for (diff_match_patch.Patch patch : patches) {
			System.err.println(patch.toString());
		    }
		    return MISMATCH;
		}
	    }
	    catch (IOException ioe) {
		Intl.errFormat("tester#compareError",
			Exceptions.toString(ioe), canonFile.getPath(), realFile.getPath());
		return INPUT_IO_ERROR;
	    }
	    return SUCCESS;
	}


	/**
	 * Compare the real output with the canon output.
	 *
	 * @param testName	The name of this particular test.
	 * @param canonOut	The file of the expected output from the test.
	 * @param realOut	The actual output of the test.
	 * @param canonErr	The file of the expected error ({@link System#err}) output of the test.
	 * @param realErr	The actual error output.
	 * @param cs		The charset used to encode these files.
	 * @return		The return from {@link #compareFiles}.
	 */
	private int compareCanons(
		final String testName,
		final File canonOut,
		final File realOut,
		final File canonErr,
		final File realErr,
		final Charset cs)
	{
	    int ret = compareFiles(testName, canonOut, realOut, cs);
	    if (ret != SUCCESS)
		return ret;

	    return compareFiles(testName, canonErr, realErr, cs);
	}

	private long freeMemory() {
	    return Environment.freeMemorySize();
	}

	private long usedMemory() {
	    return Environment.totalMemorySize() - freeMemory();
	}

	private String memory(final long value) {
	    return NumericUtil.formatToRange(BigInteger.valueOf(value), NumericUtil.RangeMode.MIXED);
	}

	private void logTestName(final PrintStream origOut, final String testName, final String altTestName, final String commandLine) {
	    if (!CharUtil.isNullOrEmpty(altTestName) && !testName.equals(altTestName)) {
		if (log && verbose)
		    origOut.print(Intl.formatString("tester#logVerboseAlt", testName, altTestName, commandLine));
		else if (log)
		    origOut.print(Intl.formatString("tester#logAlt", testName, altTestName));
		else if (verbose)
		    origOut.print(Intl.formatString("tester#verbose", commandLine));
	    }
	    else {
		if (log && verbose)
		    origOut.print(Intl.formatString("tester#logVerbose", testName, commandLine));
		else if (log)
		    origOut.print(Intl.formatString("tester#log", testName));
		else if (verbose)
		    origOut.print(Intl.formatString("tester#verbose", commandLine));
	    }
	}


	/**
	 * Create a new output {@link PrintStream} for use with the class to be tested as "stdout" or "stderr".
	 * <p> If the {@link #ignoreLineEndings} flag is set we will insert a {@link NewlineOutputStream} into
	 * the mix to regularize the line endings from the program, to match what we do when writing the canon
	 * files in {@link TestFiles#writeOutputLine} and {@link TestFiles#writeErrorLine}.
	 *
	 * @param out The output file to write to.
	 * @param cs  Charset to use for the output file.
	 * @return    A {@link PrintStream} to use for writing, possibly filtered.
	 * @throws IOException if anything goes wrong
	 */
	private PrintStream createPrintStream(final File out, final Charset cs)
		throws IOException
	{
	    if (ignoreLineEndings) {
		OutputStream os = Files.newOutputStream(out.toPath());
		NewlineOutputStream nos = new NewlineOutputStream(os);
		return new PrintStream(nos, false, cs.name());
	    }
	    else {
		return new PrintStream(out, cs.name());
	    }
	}

	private void setCurrentVersion(final Version current) {
	    currentVersion = current;
	}

	/**
	 * Execute the given test class with the given command line.
	 *
	 * @param className   Fully-qualified class name to invoke.
	 * @param commandLine Single command line for the test class to execute.
	 * @param testName    Name for this test (reporting purposes).
	 * @param out         Output stream for debug messages.
	 * @param err         Error output stream for error messages.
	 * @return One of the defined exit codes.
	 */
	private int executeClass(
		final String className,
		final String commandLine,
		final String testName,
		final PrintStream out,
		final PrintStream err)
	{
	    ClassLoader cl = null;
	    Class<?> testClass = null;
	    Constructor<?> constructor = null;
	    Object testObject = null;
	    Testable testableObject = null;
	    Method main = null;

	    int exitCode = SUCCESS;

	    try {
		URL[] urls = Environment.getURLClassPath();
		cl = new URLClassLoader(urls);

		testClass = Class.forName(className, true, cl);

		String[] args = CommandLine.parse(commandLine, defaultScriptDir);
		if (debug) {
		    out.println("Command line = " + CharUtil.makeStringList(args));
		}

		// Preload the program's version information because otherwise the "main program"
		// will be us instead of them...
		Environment.loadProgramInfo(testClass);

		constructor = testClass.getDeclaredConstructor();
		constructor.setAccessible(true);

		testObject = constructor.newInstance();

		if (Testable.class.isInstance(testObject)) {
		    testableObject = (Testable) testObject;

		    setCurrentVersion(new Version(testableObject.getVersion()));

		    exitCode = testableObject.setup(args);

		    logTestName(out, testName, testableObject.getTestName(), commandLine);

		    if (exitCode == SUCCESS) {
			exitCode = testableObject.execute();
		    }

		}
		else {
		    main = testClass.getDeclaredMethod("main", String[].class);
		    main.setAccessible(true);

		    setCurrentVersion(new Version(Environment.getAppVersion()));

		    logTestName(out, testName, null, commandLine);

		    main.invoke(null, (Object) args);
		}
	    }
	    catch (NoClassDefFoundError | ClassNotFoundException | ExceptionInInitializerError ex) {
		err.println(Intl.formatString("tester#testClassNotFound", className, Exceptions.toString(ex)));
		exitCode = CLASS_NOT_FOUND;
	    }
	    catch (NoSuchMethodException nsme) {
		err.println(Intl.formatString("tester#noMainMethod", Exceptions.toString(nsme)));
		exitCode = OTHER_ERROR;
	    }
	    catch (IllegalAccessException | IllegalArgumentException | InstantiationException ex) {
		err.println(Intl.formatString("tester#mainInvokeError", Exceptions.toString(ex)));
		exitCode = OTHER_ERROR;
	    }
	    catch (InvocationTargetException ite) {
		Throwable target = ite.getTargetException();
		if (target instanceof SecurityException) {
		    // Thrown by a System.exit(code)
		    String exitString = target.getMessage();
		    exitCode = Integer.parseInt(exitString);
		}
		else {
		    err.println(Intl.formatString("tester#abnormalExitString", Exceptions.toString(ite.getTargetException())));
		    exitCode = OTHER_ERROR;
		}
	    }
	    catch (SecurityException se) {
		// Thrown by a System.exit(code)
		String exitString = se.getMessage();
		exitCode = Integer.parseInt(exitString);
	    }
	    catch (Throwable e) {
		err.println(Intl.formatString("tester#abnormalExitString", Exceptions.toString(e)));
		exitCode = OTHER_ERROR;
	    }
	    finally {
		main = null;
		setCurrentVersion(null);
		constructor = null;
		testableObject = null;
		testObject = null;
		testClass = null;
		cl = null;
	    }

	    return exitCode;
	}


	/**
	 * Run the test and compare results with the canons or just create the canon files
	 * from the current output.
	 *
	 * @param className	Test class name.
	 * @param testName	The name of this particular test.
	 * @param files		Object containing the file-related objects to pass around.
	 * @param cs		The charset to use for input, output, and error files.
	 * @param commandLine	The complete command line for the test.
	 * @param expectedExitCode	Usually zero, but could be an expected "error", so a non-zero value for that.
	 * @return		The result of {@link #compareCanons} or some other precheck errors
	 *			(or zero for {@link #createCanons} mode).
	 */
	private int runTestAndCompareOrCreateCanons(
		final String className,
		final String testName,
		final TestFiles files,
		final Charset cs,
		final String commandLine,
		final int expectedExitCode)
	{
	    if (CharUtil.isNullOrEmpty(className)) {
		Intl.outPrintln("tester#noTestClass");
		return CLASS_NOT_FOUND;
	    }

	    InputStream origIn = System.in;
	    PrintStream origOut = System.out;
	    PrintStream origErr = System.err;

	    File testIn = null;
	    File testOut = null;
	    File testErr = null;
	    InputStream newIn = null;
	    PrintStream newOut = null;
	    PrintStream newErr = null;

	    long startTime = 0L;
	    long elapsedTime;
	    long initialMemoryUsed = usedMemory();

	    int exitCode = SUCCESS;

	    try {
		try {
		    testOut = FileUtilities.createTempFile("testoutput");
		    testErr = FileUtilities.createTempFile("testerror");

		    // Note: significant problems with charsets here on input!!
		    if (createCanons) {
			testIn = FileUtilities.createTempFile("testinput");
			newIn = new EchoInputStream(origIn, Files.newOutputStream(testIn.toPath()));
		    }
		    else {
			newIn = Files.newInputStream(files.inputFile.toPath());
		    }
		    System.setIn(newIn);
		    System.setOut(newOut = createPrintStream(testOut, cs));
		    System.setErr(newErr = createPrintStream(testErr, cs));

		    startTime = Environment.highResTimer();

		    exitCode = executeClass(className, commandLine, testName, origOut, origErr);
		}
		finally {
		    elapsedTime = Environment.highResTimer() - startTime;
		    totalElapsedTime += elapsedTime;

		    System.setIn(origIn);
		    origIn = null;
		    System.setOut(origOut);
		    origOut = null;
		    System.setErr(origErr);
		    origErr = null;

		    newIn.close();
		    newIn = null;
		    newOut.flush();
		    newOut.close();
		    newOut = null;
		    newErr.flush();
		    newErr.close();
		    newErr = null;
		}

		// For "successful failures" compare to the expected exit code
		if (exitCode != expectedExitCode) { 
		    Intl.outFormat("tester#abnormalExit", exitCode);
		    return exitCode;
		}

		int ret = SUCCESS;

		if (createCanons) {
		    // TODO: Read and merge testIn, testOut and testErr into the new canon file
		    
		    if (ret == SUCCESS) {
			testIn.delete();
			testOut.delete();
			testErr.delete();
		    }
		}
		else {
		    // If an alternate output file was named in the canon file, compare that to the canon output instead
		    if (files.resultFile != null)
			ret = compareCanons(testName, files.outputFile, files.resultFile, files.errorFile, testErr, cs);
		    else
			ret = compareCanons(testName, files.outputFile, testOut, files.errorFile, testErr, cs);

		    if (ret == SUCCESS) {
			testOut.delete();
			testErr.delete();
		    }
		}

		double elapsedSecs = (double)elapsedTime / (double)Environment.highResTimerResolution();
		long usedMemory = usedMemory() - initialMemoryUsed;
		long freeMemory = freeMemory();

		if (log && !verbose) {
		    String status = (ret == SUCCESS) ? "tester#statusPassed" : "tester#statusFailed";
		    if (memory && timing)
			Intl.outFormat("tester#statusTimingMemory",
				Intl.getString(status), elapsedSecs, memory(usedMemory), memory(freeMemory));
		    else if (memory)
			Intl.outFormat("tester#statusAndMemory",
				Intl.getString(status), memory(usedMemory), memory(freeMemory));
		    else if (timing)
			Intl.outFormat("tester#statusAndTiming", Intl.getString(status), elapsedSecs);
		    else
			Intl.outPrintln(status);
		}
		else if (memory && timing) {
		    Intl.outFormat("tester#timingMemory", elapsedSecs, memory(usedMemory), memory(freeMemory));
		}
		else if (memory) {
		    Intl.outFormat("tester#memory", memory(usedMemory), memory(freeMemory));
		}
		else if (timing) {
		    Intl.outFormat("tester#timing", elapsedSecs);
		}

		return ret;
	    }
	    catch (IOException ioe) {
		Intl.errFormat("tester#errTempCreate", Exceptions.toString(ioe));
		return OUTPUT_IO_ERROR;
	    }
	    finally {
		try {
		    if (newIn != null)
			newIn.close();
		    if (newOut != null)
			newOut.close();
		    if (newErr != null)
			newErr.close();
		    if (origIn != null)
			System.setIn(origIn);
		    if (origOut != null)
			System.setOut(origOut);
		    if (origErr != null)
			System.setErr(origErr);
		}
		catch (IOException ignore) { }
	    }
	}


	private boolean platformCheck(final String platformCheck) {
	    if (!platformCheck.isEmpty()) {
		if (platformCheck.startsWith("^")) {
		    if (platformCheck.length() > 1) {
			if (platformCheck.substring(1).equalsIgnoreCase(currentPlatform))
			    return false;
		    }
		    else {
			// "Not anything" means nothing matches
			return false;
		    }
		}
		else {
		    if (!platformCheck.equalsIgnoreCase(currentPlatform))
			return false;
		}
	    }
	    return true;
	}


	/**
	 * Check a potential canon line for platform, version and charset values
	 * specified as <code>{{ <i>platform</i> }}</code> or <code>{{ ,<i>major</i>.<i>minor</i> }}</code>
	 * or <code>{{ <i>platform</i>,<i>major</i>.<i>minor</i> }}</code>.
	 * <p>Platform can be <code>"windows"</code>, <code>"linux"</code>, <code>"unix"</code>, or
	 * <code>"osx"</code>. Also, <code>"^<i>platform</i>"</code> will match any platform EXCEPT the given one.
	 * <p>Version can also be <code><i>major</i></code>, <code><i>major</i>.<i>x</i></code> or
	 * <code><i>major</i>.*</code> or any of these followed by <code>+</code> or
	 * <code><i>major</i></code>[<code>.<i>minor</i></code>]<code>-<i>major</i></code>[<code>.<i>minor</i></code>]
	 * with either one omitted.
	 * <p>Charset is specified by <code>[&lt; <i>charset</i> &gt;]</code>, and can be given as
	 * <code>[&lt;*&gt;]</code> to match any character set (same as leaving out the check),
	 * or by <code>[&lt;^<i>name</i>&gt;]</code> which matches any charset BUT the given one.
	 * <p>Either a platform/version or charset check can be given (or both, in either order) and all the
	 * given checks must pass for the canon line to be included in the final canon test file.
	 *
	 * @param input	The input line, with a potential platform/version or charset specification.
	 * @return	{@code null} if the platform/version or charset spec exists and we don't pass the test, or the
	 *		input line (less the version part) if the spec doesn't exist or if it does and we pass the
	 *		tests, and thus the line should be part of the test.
	 */
	private String platformAndVersionCheck(final String input) {
	    if (input.startsWith("{{")) {
		// {{ version }}
		int end = input.indexOf("}}");
		if (end > 0) {
		    String spec = input.substring(2, end).trim();
		    if (spec.isEmpty())
			return input;
		    String canonLine = input.substring(end + 2);
		    String[] parts = spec.split("\\,");
		    if (parts.length == 1) {
			// platform only
			if (!platformCheck(parts[0]))
			    return null;
			return platformAndVersionCheck(canonLine);
		    }
		    else if (parts.length == 2) {
			// platform + version
			// either could be empty, which means all
			if (!platformCheck(parts[0]))
			    return null;
			if (!parts[1].isEmpty()) {
			    String version = parts[1];
			    int ix;
			    if ((ix = version.indexOf("-")) >= 0) {
				// Range of versions: major[.minor]-major[.minor]
				String first = version.substring(0, ix);
				String second = version.substring(ix + 1);
				if (first.isEmpty()) {
				    if (second.isEmpty()) {
					return input;		// will give mismatch error....
				    }
				    // -major[.minor] = anything up to that major[.minor]
				    Version secondVersion = new Version(second);
				    return (secondVersion.compareTo(currentVersion) < 0) ? null : platformAndVersionCheck(canonLine);
				}
				else if (second.isEmpty()) {
				    // major[.minor]- = anything beyond that major[.minor]
				    Version firstVersion = new Version(first);
				    return (firstVersion.compareTo(currentVersion) > 0) ? null : platformAndVersionCheck(canonLine);
				}
				else {
				    // major[.minor]-major[.minor] = anything in between
				    Version firstVersion = new Version(first);
				    Version secondVersion = new Version(second);
				    int firstCompare = firstVersion.compareTo(currentVersion);
				    int secondCompare = secondVersion.compareTo(currentVersion);
				    return (firstCompare > 0 || secondCompare < 0) ? null : platformAndVersionCheck(canonLine);
				}
			    }
			    else {
				boolean andBeyond = false;
				if (version.endsWith("+")) {
				    andBeyond = true;
				    version = version.substring(0, version.length() - 1);
				}
				Version ver = new Version(version);
				int compare = ver.compareTo(currentVersion);
				if (andBeyond) {
				    return (compare > 0) ? null : platformAndVersionCheck(canonLine);
				}
				else {
				    return (compare != 0) ? null : platformAndVersionCheck(canonLine);
				}
			    }
			}
			return platformAndVersionCheck(canonLine);
		    }
		    // else syntax error, just return the whole line which
		    // should generate a diff to highlight the problem
		    // TODO: better solution??
		}
		// Not a valid spec -- should this be an error?
		return input;
	    }
	    else if (input.startsWith("[<")) {
		// [< charset >]
		int end = input.indexOf(">]");
		if (end > 0) {
		    String charsetName = input.substring(2, end).trim();
		    if (charsetName.isEmpty())
			return input;
		    String canonLine = input.substring(end + 2);
		    // "*" means any/all charsets (not necessary, but for annotation if desired)
		    if (charsetName.equals("*")) {
			return platformAndVersionCheck(canonLine);
		    }
		    // If the name is "^name" then this will match anything BUT that name
		    if (charsetName.startsWith("^")) {
			try {
			    Charset charset = Charset.forName(charsetName.substring(1));
			    return charset.equals(DEFAULT_CHARSET) ? null : platformAndVersionCheck(canonLine);
			}
			catch (UnsupportedCharsetException uce) {
			    return input;
			}
		    }
		    else {
			try {
			    Charset charset = Charset.forName(charsetName);
			    return charset.equals(DEFAULT_CHARSET) ? platformAndVersionCheck(canonLine) : null;
			}
			catch (UnsupportedCharsetException uce) {
			    return input;
			}
		    }
		}
		// Not valid syntax
		return input;
	    }
	    // No check (most common case), just return the input unchanged
	    return input;
	}


	/**
	 * Run one test with the given parameters.
	 * <p> In {@link #createCanons} mode, the canon file will be created in the input
	 * directory if specified, or the current directory if not.  Otherwise the canon
	 * file will be parsed and the results compared to it.
	 *
	 * @param desc			The test description file we're currently running in.
	 * @param canonFileName		The canon file name (which contains input, output and error lines).
	 * @param canonCharsetName	Charset name for this file (can be {@code null} to use the platform default).
	 * @param commandLine		The complete command line for the test.
	 * @param expectedExitCode	Usually zero, but some tests can "fail successfully" by annotating the test
	 *				description with the expected exit code.
	 * @return			Test result (0 = success, or an error code).
	 */
	private int runOneTest(
		final DescriptionFile desc,
		final String canonFileName,
		final String canonCharsetName,
		final String commandLine,
		final int expectedExitCode)
	{
	    Charset cs = null;
	    try {
		cs = CharUtil.isNullOrEmpty(canonCharsetName) ?
			DEFAULT_CHARSET : Charset.forName(canonCharsetName);
	    }
	    catch (IllegalArgumentException iae) {
		Intl.errFormat("tester#errBadCanonCharset", canonCharsetName);
		return BAD_ARGUMENT;
	    }

	    BufferedReader canonReader = null;
	    BufferedWriter canonWriter = null;
	    TestFiles files = new TestFiles(ignoreLineEndings);

	    try {
		// Assume the input file is named "xxx.canon" and is in the input directory (if given)
		// For now, just use a File object to parse the name into path + name
		File canonFile  = new File(canonFileName);
		String testName = FileUtilities.nameOnly(canonFile);

		// Get the default file we would like to use: $inputDir + testName + $inputExt
		// (assuming the "canonFileName" does not have a path and/or extension)
		canonFile = FileUtilities.decorate(canonFileName, defaultCanonDir, defaultCanonExt);

		if (createCanons) {
		    boolean created = canonFile.createNewFile();
		    if (!created) {
			Intl.errFormat("tester#warnOverwriteCanon", canonFile.getPath());
			// TODO: should we prompt to overwrite?  Or halt here?  Or what??
		    }
		    // This is the combined input/output/error stream
		    // TODO: what about input to the script???????
		    canonWriter = Files.newBufferedWriter(canonFile.toPath(), cs);

		    files.createStreams(cs);

		    // TODO: pass in canonWriter somehow for this case
		    int ret = runTestAndCompareOrCreateCanons(testClassName, testName, files, cs, commandLine, expectedExitCode);

		    if (ret == 0) {
			files.deleteFiles();
		    }

		    return ret;
		}
		else {
		    // If our preferred file doesn't exist, then try both the bare name in the input dir,
		    // and the bare name itself
		    boolean canRead = FileUtilities.canRead(canonFile);
		    if (!canRead) {
			if (defaultCanonDir != null) {
			    canonFile = FileUtilities.decorate(canonFileName, defaultCanonDir, null);
			    canRead   = FileUtilities.canRead(canonFile);
			    if (!canRead) {
				// One last try with just the name as given
				canonFile = new File(canonFileName);
				canRead   = FileUtilities.canRead(canonFile);
			    }
			}
			else {
			    canonFile = FileUtilities.decorate(canonFileName, null, defaultCanonExt);
			    canRead   = FileUtilities.canRead(canonFile);
			}
		    }
		    if (!canRead) {
			Intl.errFormat("tester#cannotOpenFile", canonFileName);
			return 2;
		    }

		    canonReader = Files.newBufferedReader(canonFile.toPath(), cs);

		    files.createStreams(cs);

		    // Parse the canon file into options, input stream, output stream and error stream
		    // and write the respective stream files
		    String line = null;
		    while ((line = canonReader.readLine()) != null) {
			// Comments are allowed (and ignored)
			if (line.startsWith("#") ||
			    line.startsWith("!") ||
			    line.startsWith("//")) {
			    continue;
			}
			else if (line.startsWith("$")) {
			    String directive = line.substring(1).trim();
			    Matcher m = DIRECTIVE.matcher(directive);
			    if (m.matches()) {
				String command = m.group(1).toLowerCase();
				String argument = m.group(3);
				if (argument != null) {
				    argument = CharUtil.stripDoubleQuotes(argument);
				}

				switch (command) {
				    case "outputfile":
				    case "resultfile":
				    case "outfile":
				    case "output":
				    case "result":
					files.resultFile = new File(CharUtil.substituteEnvValues(argument));
					break;
				    default:
					Intl.errFormat("tester#canonBadSyntax", line);
					return 2;
				}
			    }
			    else {
				Intl.errFormat("tester#canonBadSyntax", line);
				return 2;
			    }
			}
			else if (!line.isEmpty()) {
			    String outLine = platformAndVersionCheck(line);
			    if (outLine != null) {
				if (outLine.startsWith(">>")) {
				    files.writeErrorLine(outLine.substring(2));
				}
				else if (outLine.startsWith(">")) {
				    files.writeOutputLine(outLine.substring(1));
				}
				else if (outLine.startsWith("<")) {
				    files.writeInputLine(outLine.substring(1));
				}
				else {
				    // Default is to write to standard output
				    files.writeOutputLine(outLine);
				}
			    }
			}
			else {
			    files.writeOutputLine("");
			}
		    }
		    canonReader.close();
		    canonReader = null;
		    files.closeStreams();

		    int ret = runTestAndCompareOrCreateCanons(testClassName, testName, files, cs, commandLine, expectedExitCode);

		    if (ret == 0) {
			files.deleteFiles();
		    }

		    return ret;
		}
	    }
	    catch (IOException ioe) {
		Intl.errFormat("tester#errCanonFiles", canonFileName, Exceptions.toString(ioe));
		return 2;
	    }
	    finally {
		try {
		    if (canonReader != null)
			canonReader.close();
		    if (canonWriter != null)
			canonWriter.close();
		    files.abort();
		}
		catch (IOException ignore) { }
	    }
	}


	/**
	 * Process the description file lines that begin with "$", "!", or ":".
	 *
	 * @param desc  Test description file we're processing currently.
	 * @param line	The internal instruction input line (without the leading "$").
	 * @return	{@code false} to abort processing of this file (fatal error).
	 */
	private boolean processInternalCommand(final DescriptionFile desc, final String line) {
	    boolean error = false;
	    Matcher m = DIRECTIVE.matcher(line);
	    if (m.matches()) {
		String command  = m.group(1).toLowerCase();
		String argument = m.group(3);
		argument = argument == null ? null : CharUtil.stripDoubleQuotes(argument);

		switch (command) {
		    case "echo":
			if (!CharUtil.isNullOrEmpty(argument)) {
			    System.out.println(argument);
			}
			else {
			    System.out.println();
			}
			break;

		    case "inputdir":
		    case "canondir":
			if (!CharUtil.isNullOrEmpty(argument)) {
			    File inputDir = new File(argument);
			    if (!inputDir.exists() || !inputDir.isDirectory()) {
				Intl.errFormat("tester#badInputDir", argument);
				return false;
			    }
			    else {
				defaultCanonDir = inputDir;
			    }
			}
			else {
			    Intl.errFormat("tester#emptyInputDir");
			    return false;
			}
			break;

		    case "scriptdir":
		    case "sourcedir":
			if (!CharUtil.isNullOrEmpty(argument)) {
			    File scriptDir = new File(argument);
			    if (!scriptDir.exists() || !scriptDir.isDirectory()) {
				Intl.errFormat("tester#badScriptDir", argument);
				return false;
			    }
			    else {
				defaultScriptDir = scriptDir;
			    }
			}
			else {
			    Intl.errFormat("tester#emptyScriptDir");
			    return false;
			}
			break;

		    case "testclass":
			if (!CharUtil.isNullOrEmpty(argument)) {
			    testClassName = argument;
			}
			else {
			    Intl.errFormat("tester#emptyTestClass");
			    return false;
			}
			break;

		    case "defaultoptions":
			if (!CharUtil.isNullOrEmpty(argument)) {
			    defaultOptions = argument;
			}
			else {
			    error = true;
			}
			break;

		    case "ignorelineendings":
			ignoreLineEndings = true;
			break;

		    case "nosubstitute":
			// This is the behavior from earlier versions, so if there is ever a problem
			// with the description file syntax and the env value substitution conventions
			// using this directive should solve it
			noSubstitutions = true;
			break;

		    case "noenvoptions":
		    case "noenv":
			allowEnvOptions = false;
			break;

		    case "envoptions":
		    case "envopt":
			allowEnvOptions = true;
			break;

		    case "canoncharset":
		    case "canoncs":
			if (!CharUtil.isNullOrEmpty(argument)) {
			    defaultCanonCharset = argument;
			}
			else {
			    error = true;
			}
			break;

		    case "inputext":
		    case "canonext":
			if (!CharUtil.isNullOrEmpty(argument)) {
			    defaultCanonExt = argument;
			}
			else {
			    error = true;
			}
			break;

		    case "include":
			if (!CharUtil.isNullOrEmpty(argument)) {
			    driveOneTest(new DescriptionFile(desc, argument, currentCharset));
			}
			else {
			    Intl.errFormat("tester#emptyInclude");
			}
			break;

		    case "file":
			error = true;
			if (!CharUtil.isNullOrEmpty(argument)) {
			    try {
				outputFileWriter = new FileWriter(argument);
				error = false;
			    }
			    catch (IOException ioe) {
				// TODO: could be a better error message here
			    }
			}
			break;

		    case "delete":
			error = true;
			if (!CharUtil.isNullOrEmpty(argument)) {
			    if (new File(argument).delete())
				error = false;
			}
			break;

		    default:
			for (String option : ABORT_OPTIONS) {
			    if (line.equalsIgnoreCase(option)) {
				abortOnFirstError = true;
				return true;
			    }
			}
			for (String option : NO_ABORT_OPTIONS) {
			    if (line.equalsIgnoreCase(option)) {
				abortOnFirstError = false;
				return true;
			    }
			}
			for (String option : DEFAULT_ABORT_OPTIONS) {
			    if (line.equalsIgnoreCase(option)) {
				abortOnFirstError = defaultAbortOnFirstError;
				return true;
			    }
			}
			error = true;
			break;
		}
	    }

	    if (error)
		Intl.errFormat("tester#badCommand", "$" + line);

	    return true;
	}


	private BufferedReader fileReader(final File file, final Charset cs)
		throws IOException
	{
	    if (cs == null)
		return Files.newBufferedReader(file.toPath());
	    else
		return Files.newBufferedReader(file.toPath(), cs);
	}


	private boolean isDirective(final String line) {
	    if (!line.isEmpty()) {
		char ch = line.charAt(0);
		return (ch == '$' || ch == '!' || ch == ':');
	    }
	    return false;
	}


	private AtomicInteger numberTested = new AtomicInteger();
	private AtomicInteger numberPassed = new AtomicInteger();
	private AtomicInteger numberFailed = new AtomicInteger();


	/**
	 * Read and process one test description file (can be recursive
	 * for included files).
	 *
	 * @param desc	The test description file attributes.
	 */
	private void driveOneTest(final DescriptionFile desc) {
	    String name = desc.toString();
	    File f = new File(name);

	    if (!FileUtilities.canRead(f) && defaultScriptDir != null) {
		f = FileUtilities.decorate(name, defaultScriptDir, null);
		if (!FileUtilities.canRead(f)) {
		    Intl.errFormat("tester#cannotOpenFile", name);
		    return;
		}
	    }

	    // Preprocess the description file into a temp file
	    File descript = null;
	    try {
		descript = FileUtilities.createTempFile("desc");
		PreProc preProc = new PreProc()
			.setAlwaysProcess(true)
			.setIgnoreUnknownDirectives(true)
			.setOutputFileName(descript.getPath())
			.setFile(f.getPath());
		if (Constants.UTF_8_CHARSET.equals(desc.getCharset()))
		    preProc.setFormat("UTF-8");

		preProc.execute();
	    }
	    catch (IllegalArgumentException | IOException ex) {
		Intl.errFormat("tester#preProcError", name, Exceptions.toString(ex));
		return;
	    }

	    try (BufferedReader reader = fileReader(descript, desc.getCharset()))
	    {
		String line = null;
		int lineNo = 0;

		while ((line = reader.readLine()) != null) {
		    lineNo++;
		    String remainingLine = line.isEmpty() ? line : line.substring(1);

		    if (outputFileWriter != null) {
			if (isDirective(line) && remainingLine.equalsIgnoreCase("endfile")) {
			    outputFileWriter.flush();
			    outputFileWriter.close();
			    outputFileWriter = null;
			}
			else {
			    outputFileWriter.write(line);
			    outputFileWriter.write(NEWLINE);
			}
			continue;
		    }

		    line = line.trim();
		    if (line.isEmpty() ||
			line.startsWith("#") ||
			line.startsWith("--") ||
			line.startsWith("//"))
			continue;

		    if (isDirective(line)) {
			if (processInternalCommand(desc, remainingLine))
			    continue;
			else
			    break;
		    }

		    numberTested.incrementAndGet();

		    Matcher m = DESCRIPTION.matcher(line);
		    if (m.matches()) {
			String commandLine;

			if (noSubstitutions)
			    commandLine = m.group(6);
			else
			    commandLine = CharUtil.substituteEnvValues(m.group(6));

			if (!CharUtil.isNullOrEmpty(defaultOptions)) {
			    commandLine = String.format("%1$s %2$s", defaultOptions, commandLine);
			}

			// Some tests "fail successfully" so account for the exit code here
			String exit = m.group(2);
			int expectedExitCode = 0;
			if (!CharUtil.isNullOrEmpty(exit)) {
			    expectedExitCode = Integer.parseInt(exit);
			}

			Environment.setAllowEnvOptions(allowEnvOptions);

			String charset = m.group(5);
			if (charset == null)
			    charset = defaultCanonCharset;

			int ret = runOneTest(desc, m.group(3), charset, commandLine, expectedExitCode);

			Environment.setAllowEnvOptions(true);

			if (ret == 0) {
			    numberPassed.incrementAndGet();
			}
			else {
			    numberFailed.incrementAndGet();

			    if (abortOnFirstError)
				break;
			}
		    }
		    else {
			Intl.errFormat("tester#badSyntax", lineNo, line);
			numberFailed.incrementAndGet();
			break;
		    }
		}
	    }
	    catch (IOException ioe) {
		Intl.errFormat("tester#readError", name, Exceptions.toString(ioe));
	    }
	    finally {
		if (descript != null) {
		    descript.delete();
		}
	    }
	}


	private void header(final String key, final boolean last) {
	    if (memory && timing)
		Intl.outPrintln("tester#" + key + "Both");
	    else if (memory)
		Intl.outPrintln("tester#" + key + "Memory");
	    else if (timing)
		Intl.outPrintln("tester#" + key + "Timing");
	    else if (last)
		Intl.outPrintln("tester#" + key);
	}


	/**
	 * Drive all the tests from the given test description file.
	 *
	 * @param desc	The test description file attributes.
	 */
	private void driveTests(final DescriptionFile desc) {
	    long initialMemory = usedMemory();

	    header("finalUnderline", true);

	    Intl.outFormat("tester#initialFile", desc.toString());

	    header("finalBreak", verbose || log);

	    driveOneTest(desc);

	    String totalTiming = "";
	    if (timing) {
		double elapsedSecs = (double)totalElapsedTime / (double)Environment.highResTimerResolution();
		totalTiming = Intl.formatString("tester#totalTime", elapsedSecs);
	    }
	    String totalMemory = "";
	    if (memory) {
		long finalMemory = usedMemory();
		long freeMemory  = freeMemory();
		totalMemory = Intl.formatString("tester#totalMemory", memory(finalMemory - initialMemory), memory(freeMemory));
	    }

	    header("finalBreak", true);

	    int tested = numberTested.intValue();
	    int passed = numberPassed.intValue();
	    int failed = numberFailed.intValue();

	    Intl.outFormat(tested == 1 ? "tester#finalResultOne" : "tester#finalResults",
		    tested, passed, failed, totalTiming, totalMemory);

	    header("finalUnderline", true);

	    Intl.outPrintln();
	}


	/**
	 * Reset all the options back to default values (used by "-ignoreoptions" command-line argument
	 * to ignore any TESTER_OPTIONS specified in the environment.
	 */
	private void resetToInitialOptions() {
	    createCanons = false;

	    verbose = false;
	    log     = false;
	    timing  = false;
	    memory  = false;
	    debug   = false;

	    abortOnFirstError = defaultAbortOnFirstError = false;

	    defaultScriptDir = null;

	    testClassName = null;

	    currentCharset = null;

	    testDescriptionFiles.clear();
	}


	private void checkNullValue(final String arg1, final String optionValue) {
	    if (CharUtil.isNullOrEmpty(arg1)) {
		Intl.errFormat("tester#emptyArgFor", optionValue);
		System.exit(MISSING_OPTION);
	    }
	}


	/**
	 * Process a single command-line option.
	 *
	 * @param arg	The option to process (without the leading "-", or whatever).
	 */
	private void processOption(final String arg) {
	    if (Options.isOption(arg) == null) {
		testDescriptionFiles.add(new DescriptionFile(null, arg, currentCharset));
		return;
	    }

	    String opt = null, arg1 = null;
	    int splitIndex = arg.indexOf(':');
	    if (splitIndex >= 0) {
		opt = arg.substring(0, splitIndex);
		if (splitIndex < arg.length()) {
		    arg1 = arg.substring(splitIndex + 1);
		}
	    }
	    else {
		opt = arg;
	    }

	    if (Options.matchesOption(opt, "verbose", "v"))
		verbose = true;
	    else if (Options.matchesOption(opt, "log", "l"))
		log = true;
	    else if (Options.matchesOption(opt, true, "timing", "time", "t"))
		timing = log = true;
	    else if (Options.matchesOption(opt, true, "memory", "mem", "m"))
		memory = log = true;
	    else if (Options.matchesOption(opt, true, "createcanons", "create", "c"))
		createCanons = true;
	    else if (Options.matchesOption(opt, true, "debug"))
		debug = true;
	    else if (Options.matchesOption(opt, true, ABORT_OPTIONS))
		abortOnFirstError = defaultAbortOnFirstError = true;
	    else if (Options.matchesOption(opt, true, NO_ABORT_OPTIONS))
		abortOnFirstError = defaultAbortOnFirstError  = false;
	    else if (Options.matchesOption(opt, true, "directory", "inputdirectory", "inputdir", "dir", "d")) {
		checkNullValue(arg1, opt);
		File dir = new File(arg1);
		if (dir.exists() && dir.isDirectory()) {
		    defaultScriptDir = dir;
		}
		else {
		    Intl.errFormat("tester#badInputDirArg", arg1);
		    System.exit(BAD_DIRECTORY);
		}
	    }
	    else if (Options.matchesOption(opt, "locale", "loc")) {
		checkNullValue(arg1, opt);
		String localeName = arg1;
		Locale locale = null;
		try {
		    locale = Intl.getValidLocale(localeName);
		}
		catch (IllegalArgumentException iae) {
		    System.err.println(Exceptions.toString(iae));
		    System.exit(BAD_LOCALE);
		}
		Locale.setDefault(locale);
		Intl.initAllPackageResources(locale);
	    }
	    else if (Options.matchesOption(opt, true, "charset", "char", "cs")) {
		checkNullValue(arg1, opt);
		String charsetName = arg1;
		Charset charset = null;
		try {
		    charset = Charset.forName(charsetName);
		    currentCharset = charset;
		}
		catch (Exception ex) {
		    System.err.println(Exceptions.toString(ex));
		    System.exit(BAD_CHARSET);
		}
	    }
	    else if (Options.matchesOption(opt, true, "testclass", "class", "test")) {
		checkNullValue(arg1, opt);
		testClassName = arg1;
	    }
	    else if (Options.matchesOption(opt, true, "ignoreoptions", "ignoreopt", "ignore", "ign", "i")) {
		resetToInitialOptions();
	    }
	    else if (Options.matchesOption(opt, true, "version", "vers", "ver")) {
		Environment.printProgramInfo();
		System.exit(ACTION_DONE);
	    }
	    else if (Options.matchesOption(opt, true, "help", "h", "?")) {
		Environment.printProgramInfo();
		Intl.printHelp("tester#");
		System.exit(ACTION_DONE);
	    }
	    else {
		Intl.errFormat("tester#badOption", opt);
		System.exit(BAD_ARGUMENT);
	    }
	}


	@Override
	public String getVersion() {
	    return Environment.getAppVersion();
	}


	@Override
	public String getTestName() {
	    return CharUtil.makeFileStringList(testDescriptionFiles);
	}


	private void processArgs(final String[] args) {
	    for (String arg : args) {
		processOption(arg);
	    }
	}


	@Override
	@SuppressWarnings("removal")
	public int setup(final String[] args) {
	    testDescriptionFiles = new ArrayList<>(args.length);

	    // Preprocess the TESTER_OPTIONS environment variable (if present)
	    Options.environmentOptions(Tester.class, (options) -> {
		processArgs(options);
	    });

	    // Now the regular command-line arguments
	    processArgs(args);

	    if (testDescriptionFiles.size() == 0) {
		Intl.errPrintln("tester#missingDescFile");
		Intl.printHelp("tester#");
		return MISSING_OPTION;
	    }

	    // Setup the canonical platform string
	    currentPlatform = Environment.platformIdentifier();

	    System.setSecurityManager(exitSecurityManager);

	    return SUCCESS;
	}


	@Override
	public int execute() {
	    try {
		testDescriptionFiles.forEach(n -> driveTests(n));
	    }
	    catch (Exception err) {
		Intl.errFormat("tester#exception", Exceptions.toString(err));
		exitSecurityManager.setAllowed(true);
		return OTHER_ERROR;
	    }

	    exitSecurityManager.setAllowed(true);

	    int failed = numberFailed.intValue();
	    return failed == 0 ? 0 : NUMBER_OF_ERRORS + failed;
	}


	/**
	 * Standard no-arg constructor.
	 */
	public Tester() {
	}


	/**
	 * The main class method, meant to be invoked from the command line.
	 * The only inputs are the paths of test description files that
	 * provide the input to drive each individual test. Plus some
	 * basic options are accepted.
	 * <p> Each line of one of these files has the name of a "canon"
	 * file used to provide input to the test and which provides the
	 * expected output and error results.  The test description line
	 * also lists the command-line options needed for each test.
	 * <p> The test description files may also contain directives to
	 * guide the test operation (such as the location of files)
	 * and comments to explain things.
	 *
	 * @param args	The command line arguments from the user.
	 */
	public static void main(final String[] args) {
	    Environment.setDesktopApp(true);

	    int result = SUCCESS;

	    Tester instance = new Tester();
	    if ((result = instance.setup(args)) == SUCCESS)
		result = instance.execute();

	    Testable.exit(result);
	}

}
