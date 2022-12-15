/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016,2020-2022 Roger L. Whitcomb.
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
 *	Various static methods to find executables in the local computer
 *	environment.  Equivalent of the *nix utility "which".
 *
 * History:
 *	15-Sep-2016 (rlwhitcomb)
 *	    Initial coding.
 *	16-Sep-2016 (rlwhitcomb)
 *	    Fix code for non-Windows platforms.
 *	09-Apr-2020 (rlwhitcomb)
 *	    Prepare for github.
 *	14-Apr-2020 (rlwhitcomb)
 *	    For multiple input targets, print the "target -> result"
 *	12-Nov-2020 (rlwhitcomb)
 *	    On Windows, check "." as a last resort.
 *	13-Nov-2020 (rlwhitcomb)
 *	    Use Environment.pathSeparator; move strings to resources.
 *	11-Dec-2020 (rlwhitcomb)
 *	    New program info mechanism.
 *	21-Jan-2021 (rlwhitcomb)
 *	    Move "canExecute" into FileUtilities, and beef it up for Windows.
 *	30-Aug-2021 (rlwhitcomb)
 *	    Set program exit code to 1 if nothing found. Put the main logic
 *	    into a method "which" can be called from anywhere.
 *	13-Dec-2021 (rlwhitcomb)
 *	    #129: Change "which" method to use varargs for convenience.
 *	    Add "findAll" intermediate method for use by other utilities.
 *	12-Apr-2022 (rlwhitcomb)
 *	    #269: New method to load main program info (in Environment).
 *	14-Apr-2022 (rlwhitcomb)
 *	    #273: Move math-related classes to "math" package.
 *	18-Apr-2022 (rlwhitcomb)
 *	    #270: Make "loadMainProgramInfo" automatic now.
 *	22-May-2022 (rlwhitcomb)
 *	    #340: Add utility "find" method; and "isWindowsBatch" helper.
 *	09-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 *	14-Nov-2022 (rlwhitcomb)
 *	    #556: Print out the canonical path at the end.
 */
package info.rlwhitcomb.util;

import info.rlwhitcomb.math.Num;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A set of static methods used to implement the equivalent of the "which"
 * utility program.  Methods exist for finding the first program available,
 * as well as finding all matching programs.  Alternative paths and extension
 * lists can also be used.
 */
public class Which
{
	/**
	 * An enumeration of the possible outcomes / things to be done from the
	 * main {@link #which} processing.
	 */
	public static enum Result
	{
		/** One or more executables were found, and their information displayed. */
		SUCCESS,
		/** Print the program version information. */
		VERSION,
		/** There was an unknown option given. */
		UNKNOWN,
		/** There was a format error in the options. */
		MALFORMED,
		/** There were no names listed in the arguments. */
		EMPTY,
		/** There were no executables found to match the listed names. */
		NOTFOUND
	}


	/** Default path directories. */
	private static final String DEFAULT_PATH = System.getenv("PATH");
	/** Default list of executable extensions -- Windows only. */
	private static final String DEFAULT_EXTENSIONS = System.getenv("PATHEXT");
	/** The current user directory, which is also searched on Windows. */
	private static final String CURRENT_DIR = System.getProperty("user.dir");
	/** File date formatter. */
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss a (z)");
	/** Split pattern for path string (platform dependent). */
	private static final String SPLIT_PATTERN = "[" + Environment.pathSeparator() + "]";
	/** Are we running in a Windows environment? */
	private static final boolean ON_WINDOWS = Environment.isWindows();


	/**
	 * Attempt to find an executable with the given name in the target directory, given
	 * the list of executable file extensions to try.
	 *
	 * @param name	Name of the executable to find.
	 * @param dir	Directory to search in.
	 * @param exts	List of file name extensions to try.
	 * @return	The file object if found, or <code>null</code> otherwise.
	 * @see FileUtilities#canExecute
	 */
	private static File findInDir(String name, File dir, String[] exts) {
	    if (dir.exists() && dir.isDirectory() && dir.canRead()) {
		File f = new File(dir, name);
		// First try the name as-is
		if (FileUtilities.canExecute(f)) {
		    return f;
		}
		// If not found, loop through the given extensions
		int dotPos = name.lastIndexOf('.');
		String bareName = dotPos < 0 ? name : name.substring(0, dotPos);
		for (String ext : exts) {
		    String fileName = ext.startsWith(".") ? bareName + ext : bareName + "." + ext;
		    f = new File(dir, fileName);
		    if (FileUtilities.canExecute(f)) {
			return f;
		    }
		}
	    }
	    return null;
	}

	/**
	 * @return The first executable in the path and path extension list
	 * that matches the given name, or {@code null} if there isn't a match.
	 * @param name	The executable name to search for.
	 */
	public static File findExecutable(String name) {
	    return findExecutable(name, null, null);
	}

	/**
	 * @return The first executable in the given path and path extension list
	 * that matches the given name, or {@code null} if there isn't a match.
	 * @param name	The executable name to search for.
	 * @param path	The path/directory to look in.
	 */
	public static File findExecutable(String name, String path) {
	    return findExecutable(name, path, null);
	}

	/**
	 * @return The first executables in the path and path extension list
	 * that matches the given name, given possible alternate path list and extension
	 * list, or {@code null} if there is no match.
	 * @param name	The executable name to search for.
	 * @param path	An alternate path list (if {@code null}, use the system path).
	 * @param extensions	An alternative list of extensions to try if the name does
	 *			not specify an extension ({@code null} implies the system
	 *			extension list if it exists).
	 */
	public static File findExecutable(String name, String path, String extensions) {
	    String myPath = CharUtil.isNullOrEmpty(path) ? DEFAULT_PATH : path;
	    String myExts = CharUtil.getEmptyForNull(CharUtil.isNullOrEmpty(extensions) ? DEFAULT_EXTENSIONS : extensions);

	    String[] dirs = myPath.split(SPLIT_PATTERN);
	    String[] exts = myExts.split(SPLIT_PATTERN);

	    for (String dirName : dirs) {
		File dir = new File(CharUtil.stripDoubleQuotes(dirName));
		File file = findInDir(name, dir, exts);
		if (file != null) {
		    return file;
		}
	    }

	    return null;
	}

	/**
	 * Attempt to find all the executables that match the given name in the target directory.
	 *
	 * @param name	Name of the executable(s) to find.
	 * @param dir	Target directory to look in.
	 * @param exts	List of file name extensions to try if the name itself isn't found.
	 * @return	The possibly empty list of executables found to match.
	 * @see FileUtilities#canExecute
	 */
	private static List<File> findAllInDir(String name, File dir, String[] exts) {
	    List<File> files = new ArrayList<>();

	    if (dir.exists() && dir.isDirectory() && dir.canRead()) {
		File f = new File(dir, name);
		// First try the name as-is
		if (FileUtilities.canExecute(f)) {
		    files.add(f);
		}
		else {
		    // If not found, loop through the given extensions
		    int dotPos = name.lastIndexOf('.');
		    String bareName = dotPos < 0 ? name : name.substring(0, dotPos);
		    for (String ext : exts) {
			String fileName = ext.startsWith(".") ? bareName + ext : bareName + "." + ext;
			f = new File(dir, fileName);
			if (FileUtilities.canExecute(f)) {
			    files.add(f);
			}
		    }
		}
	    }

	    return files;
	}

	/**
	 * @return A list of all the executables in the path and path extension list
	 * that match the given name.
	 * @param name	The executable name to search for.
	 */
	public static List<File> findAllExecutables(String name) {
	    return findAllExecutables(name, null, null);
	}

	/**
	 * @return A list of all the executables in the path and path extension list
	 * that match the given name, given possible alternate path list and extension
	 * list.
	 * @param name	The executable name to search for.
	 * @param path	An alternate path list (if {@code null}, use the system path).
	 * @param extensions	An alternative list of extensions to try if the name does
	 *			not specify an extension ({@code null} implies the system
	 *			extension list if it exists).
	 */
	public static List<File> findAllExecutables(String name, String path, String extensions) {
	    String myPath = CharUtil.isNullOrEmpty(path) ? DEFAULT_PATH : path;
	    String myExts = CharUtil.getEmptyForNull(CharUtil.isNullOrEmpty(extensions) ? DEFAULT_EXTENSIONS : extensions);

	    String[] dirs = myPath.split(SPLIT_PATTERN);
	    String[] exts = myExts.split(SPLIT_PATTERN);

	    List<File> results = new ArrayList<>();

	    for (String dirName : dirs) {
		File dir = new File(CharUtil.stripDoubleQuotes(dirName));
		List<File> files = findAllInDir(name, dir, exts);
		results.addAll(files);
	    }
	    return results;
	}

	/**
	 * @return A formatted string showing the file's last modified time in a consistent
	 * format.  This format includes the timezone (important for Daylight Savings Time
	 * comparisons).
	 * @param f	The file in question.
	 */
	public static String getFileTimeString(File f) {
	    Date fileTime = new Date(f.lastModified());
	    synchronized(DATE_FORMAT) {
		return DATE_FORMAT.format(fileTime);
	    }
	}

	private static void showFileInfo(String name, File f, boolean showTarget, boolean showMore) {
	    if (showTarget)
		System.out.print(name + " -> ");
	    try {
		System.out.println(f.getCanonicalPath());
	    }
	    catch (IOException ioe) {
		System.out.println(f.getPath());
	    }
	    if (showMore) {
		System.out.format("\t%1$s  %2$s%n", Num.fmt1(f.length(), 10), getFileTimeString(f));
	    }
	}

	/**
	 * Find first or all of the named executables and return the file list of those found.
	 *
	 * @param names	The list of executable names to search for.
	 * @param all	{@code true} to find all the occurrences of each, or just the first.
	 * @return	The list of files found, which could be empty.
	 */
	public static List<File> findAll(final List<String> names, final boolean all) {
	    List<File> files = new ArrayList<>();

	    for (String name : names) {
		// On Windows the first place to look is the current directory
		if (ON_WINDOWS) {
		    File f = findExecutable(name, CURRENT_DIR);
		    if (f != null) {
			files.add(f);
			if (!all)
			    break;
		    }
		}

		if (all) {
		    files.addAll(findAllExecutables(name));
		}
		else {
		    File f = findExecutable(name);
		    if (f != null) {
			files.add(f);
		    }
		}
	    }

	    return files;
	}

	/**
	 * Find the location of the executable given.
	 *
	 * @param name The executable name to search for.
	 * @return     File location found, if it is found, or {@code null} if not.
	 * @see #findAll
	 */
	public static File find(final String name) {
	    List<String> names = new ArrayList<>(1);
	    names.add(name);

	    List<File> files = findAll(names, false);
	    return files.isEmpty() ? null : files.get(0);
	}

	/**
	 * Helper method to determine if the given file (found by {@link #find} or
	 * {@link #findAll}) is a Windows batch file.
	 *
	 * @param exeFile The executable file to examine.
	 * @return        Whether or not the executable is a ".bat" or ".cmd" file
	 *                and we're running on Windows.
	 */
	public static boolean isWindowsBatch(final File exeFile) {
	    if (ON_WINDOWS) {
		String lowerName = exeFile.getName().toLowerCase();
		return (lowerName.endsWith(".cmd") || lowerName.endsWith(".bat"));
	    }

	    return false;
	}


	/**
	 * The main body of the "which" algorithm: process the command line arguments
	 * (names and options) and produce the report.
	 *
	 * @param args	The parsed command line arguments (names and options).
	 * @return	One of the status enum values indicating the result and/or
	 *		action to be taken by the caller.
	 */
	public static Result which(String... args) {
	    boolean findAll = false;
	    boolean showInfo = false;
	    boolean quiet = false;

	    List<String> names = new ArrayList<>(args.length);

	    // Evaluate all the options first
	    for (String arg : args) {
		if (Options.isOption(arg) != null) {
		    if (Options.matchesOption(arg, "all", "a")) {
			findAll = true;
		    }
		    else if (Options.matchesOption(arg, "show", "s")) {
			showInfo = true;
		    }
		    else if (Options.matchesOption(arg, "quiet", "q")) {
			quiet = true;
		    }
		    else if (Options.matchesOption(arg, true, "version", "vers", "ver", "v")) {
			return Result.VERSION;
		    }
		    else {
			if (!quiet)
			    Intl.errFormat("util#which.unknownOption", arg);
			return Result.UNKNOWN;
		    }
		}
		else {
		    names.add(arg);
		}
	    }

	    if (names.isEmpty())
		return Result.EMPTY;

	    List<File> files = findAll(names, findAll);

	    if (!quiet) {
		boolean showTargetNameFirst = names.size() > 1;

		for (File f : files) {
		    // Note: the "nameOnly" call is only an approximation of the input name, but because
		    // we haven't saved the correspondence between "name" and "files" we can only guess now
		    showFileInfo(FileUtilities.nameOnly(f), f, showTargetNameFirst, showInfo);
		}
	    }

	    return files.isEmpty() ? Result.NOTFOUND : Result.SUCCESS;
	}

	/**
	 * Provide a main program in here so we can test from the command line.
	 * @param args	The parsed command line arguments.
	 */
	public static void main(String[] args) {
	    Result result = which(args);
	    switch (result) {
		case SUCCESS:
		case EMPTY:
		    return;
		case VERSION:
		    Environment.printProgramInfo();
		    break;
		case UNKNOWN:
		case MALFORMED:
		case NOTFOUND:
		    break;
	    }

	    // Anything but a SUCCESS or EMPTY result will set the program exit code
	    System.exit(1);
	}

}

