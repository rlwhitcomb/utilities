/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2018,2020-2022 Roger L. Whitcomb
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
 *	Build process helper to check the copyright declarations
 *	at the top of our source files to make sure they are
 *	present and accurate.
 *
 *  History:
 *	23-Jan-2015 (rlwhitcomb)
 *	    Initial version.
 *	03-Mar-2015 (rlwhitcomb)
 *	    Move CommandProcessor to the new package.
 *	26-May-2016 (rlwhitcomb)
 *	    Fix Javadoc warnings from Java 8.
 *	05-Jul-2016 (rlwhitcomb)
 *	    Simplify the Intl.PackageResourceProvider installation.
 *	21-Mar-2017 (rlwhitcomb)
 *	    Simplify Intl initialization once again.
 *	01-Jun-2017 (rlwhitcomb)
 *	    Add some more ignored extensions and file names.  Add
 *	    the concept of ignored directories also.
 *	16-Jun-2017 (rlwhitcomb)
 *	    Adapt to new return values from LineProcessor methods.
 *	28-Jul-2017 (rlwhitcomb)
 *	    Move the default package resource initialization into Intl itself, so
 *	    all the callers don't have to do it.
 *	07-Feb-2018 (rlwhitcomb)
 *	    If a preprocessor version of the file exists, don't check the processed
 *	    version.
 *	07-Feb-2018 (rlwhitcomb)
 *	    Add ".tab" to ignored extensions.
 *	13-Feb-2020 (rlwhitcomb)
 *	    Add a few more extensions, files, and directories to ignore.
 *	    Add a new "optionalFileNames" set for those files we want to check for proper
 *	    copyrights in, but don't want to error out if there isn't one (like "package-info.java").
 *	18-Feb-2020 (rlwhitcomb)
 *	    Use default methods in LineProcessor interface instead of Adapter class; add nesting level
 *	    to "enterDirectory" method.
 *	29-Mar-2021 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	04-Sep-2021 (rlwhitcomb)
 *	    Use "git" commands to get information. Check license information.
 *	05-Jan-2022 (rlwhitcomb)
 *	    Tweak output; update copyright year.
 *	    #99: Quit early if "git" is not available.
 */
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.*;

import info.rlwhitcomb.util.CommandProcessor;
import info.rlwhitcomb.util.DirectoryProcessor;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.LineProcessor;
import info.rlwhitcomb.util.Options;
import info.rlwhitcomb.util.Which;


/**
 * Processor that recursively descends the entire directory tree and scans
 * all applicable files for proper copyright declaration, license text, and
 * header format.
 */
public class CheckCopyrights
{
	private static final String STARTING_DIRECTORY = "..";
	private static final String UNDERLINE = "======================================";

	private static int totalFiles;
	private static int missingCopyrightErrors;
	private static int wrongCopyrightErrors;
	private static boolean verbose;

	/**
	 * Template for the required license in each file.
	 */
	private static String[] licenseTemplate = {
		"The MIT License (MIT)",
		"",
		"Copyright (c) 2008-2022 Roger L. Whitcomb.",
		"",
		"Permission is hereby granted, free of charge, to any person obtaining a copy",
		"of this software and associated documentation files (the \"Software\"), to deal",
		"in the Software without restriction, including without limitation the rights",
		"to use, copy, modify, merge, publish, distribute, sublicense, and/or sell",
		"copies of the Software, and to permit persons to whom the Software is",
		"furnished to do so, subject to the following conditions:",
		"",
		"The above copyright notice and this permission notice shall be included in all",
		"copies or substantial portions of the Software.",
		"",
		"THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR",
		"IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,",
		"FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE",
		"AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER",
		"LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,",
		"OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE",
		"SOFTWARE."
	};

	/**
	 * A list of file extensions that we will ignore (mostly binary files).
	 */
	private static String[] ignoredExtensions = {
		".class",
		".bin",
		".cmd",
		".gz",
		".zip",
		".gif",
		".bmp",
		".png",
		".ico",
		".ai",
		".dll",
		".dylib",
		".so",
		".jar",
		".pdf",
		".doc",
		".docx",
		".vsd",
		".csv",
		".canon",
		".rss",
		".psd",
		".MF",
		".MFpp",
		".manifest",
		".log",
		".bat",
		".sql",
		".dsv",
		".tab",
		".txt",
		".pom",
		".html",
		".htmlpp",
		".py",
		".jnlp"
	};

	/**
	 * A list of specific file names that we know will not (and should not) contain
	 * a copyright, so we can safely ignore them in the reporting.
	 */
	private static String[] ignoredNames = {
		"numeric-conditions.properties",
		"version.properties",
		"logging_default.properties",
		"LICENSE",
		"NOTICE",
		"README.md",
		"build-ant-tasks.xml",
		"build.number",
		"build.properties",
		"build.xml",
		"TerraTheme_old.json",
		".gitignore",
		"setenv",
		"ci-setenv",
		"ci-build.sh"
	};

	/**
	 * Specific file names that <em>could</em> contain a copyright, so we should check for
	 * correct one, but don't have to, so don't complain if we don't find it.
	 */
	private static String[] optionalNames = {
		"package-info.java"
	};


	/**
	 * A list of specific directory names that we know will not (and should not) contain
	 * our code, so we can safely ignore all the files in them.
	 */
	private static String[] ignoredDirectories = {
		".git",
		".github",
		"java/doc",
		"java/external-files",
		"java/name",
		"java/net",
		"java/de",
		"java/scripts",
		"java/test"
	};

	private static final int INITIAL_YEAR = 2020;

	private static Set<String> ignoredFileExtensions = new HashSet<>();
	private static Set<String> ignoredFileNames = new HashSet<>();
	private static Set<String> ignoredDirectoryNames = new HashSet<>();
	private static Set<String> optionalFileNames = new HashSet<>();

	static {
		for (String s : ignoredExtensions) {
		    ignoredFileExtensions.add(s);
		}
		for (String s : ignoredNames) {
		    ignoredFileNames.add(s);
		}
		for (String s : ignoredDirectories) {
		    ignoredDirectoryNames.add(s);
		}
		for (String s : optionalNames) {
		    optionalFileNames.add(s);
		}
	}

	/**
	 * Processor to determine the files under source control in a given directory
	 * (by running "git ls-tree -r master --name-only" there).
	 */
	private static class SourceListProcessor extends CommandProcessor
	{
		public Set<String> files = new HashSet<>();

		public SourceListProcessor() {
		    super("git", "ls-tree", "-r", "master", "--name-only");
		}

		@Override
		public boolean process(String line) {
		    files.add(line);
		    return true;
		}
	}


	private static final Pattern COMMITLOG_PATTERN =
		Pattern.compile("Date:\\s+([A-Z][a-z][a-z])\\s([A-Z][a-z][a-z])\\s(\\d{1,2})\\s(\\d\\d:\\d\\d:\\d\\d)\\s(\\d\\d\\d\\d)\\s\\-?[0-9]{4}");

	/**
	 * Command class to run "git log head file" in a particular directory in order
	 * to get the list of years in which checkins were made.
	 */
	private static class CommitLogProcessor extends CommandProcessor
	{
		private File sourceFile;
		public Set<Integer> years = new TreeSet<>();
		public boolean underSourceControl = true;

		public CommitLogProcessor(String sourceFile) {
		    super("git", "log", "head", sourceFile);
		    this.sourceFile = new File(sourceFile);
		}

		@Override
		public boolean process(String line) {
		    // The only line we're interested in starts with "Date:"
		    if (!line.startsWith("Date: "))
			return true;
		    Matcher m = COMMITLOG_PATTERN.matcher(line);
		    if (m.matches()) {
			String date = m.group(5);
			int year = Integer.parseInt(date);
			years.add(Integer.valueOf(year));
			if (verbose) {
			    System.out.format("Adding year %1$d for file '%2$s'%n", year, sourceFile.getName());
			}
		    }
		    else {
			this.underSourceControl = false;
			throw new IllegalArgumentException("Bad format for 'git log' entry: \"" + line + "\"");
		    }
		    return true;
		}
	}


	/**
	 * Trim off extraneous stuff at the beginning of a license text line.
	 * @param	input	The input line from the file.
	 * @return	The line beginning with the first letter.
	 */
	private static String trimBeginning(String input) {
	    for (int i = 0; i < input.length(); i++) {
		char ch = input.charAt(i);
		if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
		    return input.substring(i);
		}
	    }
	    return "";
	}


	/**
	 * Parse a string of copyright years into a set of years.
	 * @param	input	The copyright year string (such as <code>2020-2021</code>).
	 * @return	A set of years parsed from the input.
	 */
	private static Set<Integer> parseCopyrights(String input) {
	    Set<Integer> years = new TreeSet<>();
	    int year = 0;
	    int startYear = 0;
	    for (int i = 0; i < input.length(); i++) {
		char ch = input.charAt(i);
		if (ch >= '0' && ch <= '9') {
		    year = year * 10 + (ch - '0');
		}
		else if (ch == '-') {
		    if (year >= INITIAL_YEAR)
			years.add(year);
		    startYear = year;
		    year = 0;
		}
		else if (ch == ',') {
		    if (year >= INITIAL_YEAR)
			years.add(year);
		    if (startYear != 0) {
			for (int rangeYear = startYear + 1; rangeYear <= year; rangeYear++) {
			    if (rangeYear >= INITIAL_YEAR)
				years.add(rangeYear);
			}
			startYear = 0;
		    }
		    year = 0;
		}
	    }
	    if (year >= INITIAL_YEAR)
		years.add(year);
	    if (startYear != 0) {
		for (int rangeYear = startYear + 1; rangeYear <= year; rangeYear++) {
		    if (rangeYear >= INITIAL_YEAR)
			years.add(rangeYear);
		}
	    }
	    return years;
	}


	/**
	 * Convert a set of years into the proper copyright format.
	 * @param	years	A sorted set of years in which the file
	 *			had actual changes.
	 * @param	addSpaces	Whether to pad between the values.
	 * @return	A properly formatted copyright year string.
	 */
	private static String getYearString(Set<Integer> years, boolean addSpaces) {
	    int lastYearAdded = 0;
	    int maxYearSoFar = 0;
	    StringBuilder buf = new StringBuilder();
	    for (Integer year : years) {
		int y = year.intValue();
		if (lastYearAdded == 0) {
		    lastYearAdded = maxYearSoFar = year;
		    buf.append(year);
		}
		else if (year == maxYearSoFar) {
		    continue;
		}
		else if (year == maxYearSoFar + 1) {
		    lastYearAdded = maxYearSoFar;
		    maxYearSoFar = year;
		}
		else {
		    // Current year is not contiguous, so decide
		    // what needs to be added:
		    // String of contiguous year, add "-max"
		    if (lastYearAdded + 1 == maxYearSoFar) {
			buf.append('-').append(maxYearSoFar);
			lastYearAdded = maxYearSoFar;
		    }
		    buf.append(',');
		    if (addSpaces) {
			buf.append(' ');
		    }
		    buf.append(year);
		    lastYearAdded = maxYearSoFar = year;
		}
	    }
	    if (lastYearAdded + 1 == maxYearSoFar) {
		buf.append('-').append(maxYearSoFar);
		lastYearAdded = maxYearSoFar;
	    }
	    else if (lastYearAdded != maxYearSoFar) {
		buf.append(',');
		if (addSpaces) {
		    buf.append(' ');
		}
		buf.append(maxYearSoFar);
	    }
	    return buf.toString();
	}


	/**
	 * Implementation of the {@link LineProcessor} interface that is used
	 * to do all the processing we require.
	 */
	private static class Processor implements LineProcessor
	{
		private SourceListProcessor source;
		private CommitLogProcessor commitLog;
		private File currentFile;
		private long lineNumber;
		private String copyrightString;
		private static final String COPYRIGHT = "Copyright";
		private int licenseState;

		@Override
		public boolean enterDirectory(File directory, int level) {
		    String dir = directory.getPath();
		    dir = dir.replaceAll("[\\\\/]", "/");
		    if (dir.startsWith("../")) {
			dir = dir.substring(3);
		    }
		    if (ignoredDirectoryNames.contains(dir))
			return false;
		    source = new SourceListProcessor();
		    source.run(directory);
		    return true;
		}

		@Override
		public boolean accept(File pathname) {
		    String name = pathname.getName();
		    if (ignoredFileNames.contains(name))
			return false;

		    // Test for files that have a preprocessor version, ignore the result file if so
		    String ppFileName = pathname.getPath() + "pp";
		    File ppFile = new File(ppFileName);
		    boolean ppFileExists = (ppFile.exists() && ppFile.isFile() && ppFile.canRead());

		    boolean ignoredExtension = false;
		    int extOffset = name.lastIndexOf('.');
		    if (extOffset >= 0) {
			ignoredExtension = ignoredFileExtensions.contains(name.substring(extOffset));
		    }
		    else {
			// Also ignore any files without any extension
			ignoredExtension = true;
		    }

		    return ((pathname.isFile() && !ignoredExtension &&
			source.files.contains(name) && !ppFileExists) ||
			(pathname.isDirectory() && name.indexOf(' ') < 0));
		}

		@Override
		public boolean preProcess(File file) {
		    this.currentFile = file;
		    this.lineNumber = 0L;
		    this.copyrightString = null;
		    this.licenseState = 0;
		    commitLog = new CommitLogProcessor(file.getName());
		    commitLog.run(file.getParentFile());
		    if (!commitLog.underSourceControl) {
			if (verbose) {
			    System.out.format("Skipping file '%1$s' because it is not under source control%n", file.getPath());
			}
		    }
		    else {
			// We really only need to count those files we have in source control in the total count
			totalFiles++;
		    }
		    return commitLog.underSourceControl;
		}

		private static final Pattern COPYRIGHT_PATTERN =
			Pattern.compile(".+\\s*Copyright\\s*(\\(c\\)\\s)?([\\d\\-\\s,]+) Roger L\\. Whitcomb\\.?");

		@Override
		public boolean processLine(String line) {
		    lineNumber++;
		    String trimmedLine = trimBeginning(line);
		    if (licenseState == 0 && trimmedLine.indexOf(licenseTemplate[0]) >= 0) {
			licenseState = 1;
		    }
		    else if (line.indexOf(COPYRIGHT) >= 0 && licenseState == 2) {
			this.copyrightString = line;
			if (verbose) {
			    System.out.format("Found copyright for file '%1$s' on line %2$d%n", currentFile.getName(), lineNumber);
			}
			licenseState++;
			return true;
		    }
		    else if (licenseState > 0) {
			if (licenseState >= licenseTemplate.length)
			    return false;
			if (trimmedLine.equals(licenseTemplate[licenseState])) {
			    licenseState++;
			}
			else {
			    System.out.format("License error in file: %1$s:%n", currentFile.getPath());
			    System.out.format("Contents at line %1$d: \"%2$s\"%n", lineNumber, trimmedLine);
			    System.out.format("   Template version: \"%1$s\"%n", licenseTemplate[licenseState]);
			    return false;
			}
		    }
		    // Return true to keep reading the file until we find
		    // the end of the license text.
		    return true;
		}

		@Override
		public boolean postProcess(File file) {
		    // First check if there was any copyright statement
		    if (copyrightString == null) {
			// An "optional" file means we ignore the error if the copyright isn't found.
			if (!optionalFileNames.contains(file.getName())) {
			    missingCopyrightErrors++;
			    System.out.format("Did not find copyright in file '%1$s'%n", file.getPath());
			}
		    }
		    else {
			Matcher m = COPYRIGHT_PATTERN.matcher(copyrightString);
			if (m.matches()) {
			    // Now check if the years are correct according to the file log
			    String correctYearString = getYearString(commitLog.years, false);
			    Set<Integer> validYears = parseCopyrights(m.group(2));
			    String yearString = getYearString(validYears, false);
			    boolean correct = false;
			    if (yearString.equals(correctYearString)) {
				correct = true;
			    }
			    else {
				// Check if it is just a spacing difference
				String correctYearWithSpaces = getYearString(commitLog.years, true);
				if (yearString.equals(correctYearWithSpaces)) {
				    correctYearString = correctYearWithSpaces;	// adjust the verbose output
				    correct = true;
				}
			    }
			    if (correct) {
				if (verbose) {
				    System.out.format("File '%1$s': correct copyright '%2$s' matches that found in file '%3$s'%n", file.getPath(), correctYearString, yearString);
				}
			    }
			    else {
				wrongCopyrightErrors++;
				System.out.format("Wrong copyright years in file '%1$s':%nValid Years Found: '%2$s'%n        Should be: '%3$s'%n", file.getPath(), yearString, correctYearString);
			    }
			}
			else {
			    wrongCopyrightErrors++;
			    System.out.format("Wrong format for copyright in file '%1$s':%n\tFound: '%2$s'%n", file.getPath(), copyrightString);
			}
		    }
		    // We want to process every file, regardless of "errors" in it
		    return true;
		}

	}

	public static void main(String[] args) {
	    Environment.setDesktopApp(true);

	    if (Which.findExecutable("git") == null) {
		throw new RuntimeException("Unable to locate 'git' utility on the system PATH!");
	    }

	    // Parse options from the command line args
	    for (String arg : args) {
		if (Options.matchesOption(arg, "verbose", "v")) {
		    verbose = true;
		}
		else {
		    System.err.format("Unknown option '%1$s'%n", arg);
		}
	    }

	    new DirectoryProcessor(STARTING_DIRECTORY, new Processor()).processDirectory();

	    System.out.println(UNDERLINE);
	    System.out.format("         Total files checked: %1$d%n", totalFiles);
	    System.out.format("Files with missing copyright: %1$d%n", missingCopyrightErrors);
	    System.out.format(" Files with wrong copyrights: %1$d%n", wrongCopyrightErrors);
	    System.out.println(UNDERLINE);
	}

}

