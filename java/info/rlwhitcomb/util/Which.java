/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016,2020 Roger L. Whitcomb.
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
 */
package info.rlwhitcomb.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.File;


/**
 * A set of static methods used to implement the equivalent of the "which"
 * utility program.  Methods exist for finding the first program available,
 * as well as finding all matching programs.  Alternative paths and extension
 * lists can also be used.
 */
public class Which
{
	/** Default path directories. */
	private static final String DEFAULT_PATH = System.getenv("PATH");
	/** Default list of executable extensions -- Windows only. */
	private static final String DEFAULT_EXTENSIONS = System.getenv("PATHEXT");
	/** The current user directory, which is also searched on Windows. */
	private static final String CURRENT_DIR = System.getProperty("user.dir");
	/** File date formatter. */
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss a (z)");
	/** Split pattern for path string (platform dependent). */
	private static final String SPLIT_PATTERN = Environment.isWindows() ? "[,;]" : "\\:";

	/**
	 * @return Whether or not the given file exists and can be executed
	 * (according to the O/S and any Security Manager installed).
	 * @param f	The file (complete path) to test.
	 */
	public static boolean canExecute(File f) {
	    try {
		if (f.exists() && f.canExecute()) {
		    return true;
		}
	    } catch (SecurityException se) {
		// According to the Javadoc for "canExecute" this means
		// execute access is denied, so "NO".
	    }
	    return false;
	}

	private static File findInDir(String name, File dir, String[] exts) {
	    if (dir.exists() && dir.isDirectory() && dir.canRead()) {
		File f = new File(dir, name);
		// First try the name as-is
		if (canExecute(f)) {
		    return f;
		}
		// If not found, loop through the given extensions
		int dotPos = name.lastIndexOf('.');
		String bareName = dotPos < 0 ? name : name.substring(0, dotPos);
		for (String ext : exts) {
		    String fileName = ext.startsWith(".") ? bareName + ext : bareName + "." + ext;
		    f = new File(dir, fileName);
		    if (canExecute(f)) {
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

	private static List<File> findAllInDir(String name, File dir, String[] exts) {
	    List<File> files = new ArrayList<>();
	    if (dir.exists() && dir.isDirectory() && dir.canRead()) {
		File f = new File(dir, name);
		// First try the name as-is
		if (canExecute(f)) {
		    files.add(f);
		}
		else {
		    // If not found, loop through the given extensions
		    int dotPos = name.lastIndexOf('.');
		    String bareName = dotPos < 0 ? name : name.substring(0, dotPos);
		    for (String ext : exts) {
			String fileName = ext.startsWith(".") ? bareName + ext : bareName + "." + ext;
			f = new File(dir, fileName);
			if (canExecute(f)) {
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
	    return DATE_FORMAT.format(fileTime);
	}

	private static void showFileInfo(String name, File f, boolean showTarget, boolean showMore) {
	    if (showTarget)
		System.out.print(name + " -> ");
	    System.out.println(f.getPath());
	    if (showMore) {
		System.out.format("\t%1$s  %2$s%n", Num.fmt1(f.length(), 10), getFileTimeString(f));
	    }
	}

	/**
	 * Provide a main program in here so we can test from the command line.
	 * @param args	The parsed command line arguments.
	 */
	public static void main(String[] args) {
	    boolean findAll = false;
	    boolean showInfo = false;
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
		    else {
			System.err.format("Unknown option \"%1$s\"%n", arg);
			System.exit(1);
		    }
		}
		else {
		    names.add(arg);
		}
	    }

	    boolean showTargetNameFirst = names.size() > 1;

	    // Then evaluate the names one at a time
	    for (String name : names) {
		// On Windows the first place to look is the current directory
		if (Environment.isWindows()) {
		    File f = findExecutable(name, CURRENT_DIR);
		    if (f != null) {
			showFileInfo(name, f, showTargetNameFirst, showInfo);
			if (!findAll)
			    return;
		    }
		}

		if (findAll) {
		    for (File f : findAllExecutables(name)) {
			showFileInfo(name, f, showTargetNameFirst, showInfo);
		    }
		}
		else {
		    File f = findExecutable(name);
		    if (f != null) {
			showFileInfo(name, f, showTargetNameFirst, showInfo);
		    }
		}
	    }
	}

}

