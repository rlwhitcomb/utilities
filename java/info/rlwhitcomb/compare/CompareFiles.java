/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Roger L. Whitcomb.
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
 *	Compare folders or multiple files.
 *
 *  Change History:
 *	14-Oct-2020 (rlwhitcomb)
 *	    First coding.
 *	14-Oct-2020 (rlwhitcomb)
 *	    Add "-version" command.
 *	15-Oct-2020 (rlwhitcomb)
 *	    Fix the process exit code.
 */
package info.rlwhitcomb.compare;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.ExceptionUtil;
import info.rlwhitcomb.util.FileUtilities;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.Options;


/**
 * Compare sets of files.
 *
 * TODO: set exit code to number of mismatches/missing
 * explicit wildcard support (using filenamefilter)
 */
public class CompareFiles
{
	/**
	 * Enumeration of the various possible output verbosity settings.
	 */
	private enum Level
	{
		/** Silent implies no output at all, only setting the process exit code. */
		SILENT,
		/** Quiet means only the most necessary displays are done (compare failures, I/O errors). */
		QUIET,
		/** Normal means all the not found/failure messages come out. */
		NORMAL,
		/** Verbose puts out messages all along the way of what's happening. */
		VERBOSE,
		/** Super Verbose gets into nit-picky detail of the operation. */
		SUPER_VERBOSE,
		/** Debug mode pretty much documents every line of code as we go. */
		DEBUG;


		/**
		 * Method used to decide whether a given display should be done.
		 * <p> Note: this assumes the values are arranged such that the
		 * ordinals can be used to make this determination.
		 * <p> Usage: {@code if (verbosity.meetsOrExceeds(Level.QUIET)) ...}
		 *
		 * @param level	The "other" level to compare against.
		 * @return Whether or not this setting meets or exceeds the given level.
		 */
		boolean meetsOrExceeds(Level level) {
		    return this.ordinal() >= level.ordinal();
		}
	}


	/** Whether to keep going after the first difference. */
	private static boolean continueAfterError = true;
	/** Whether to descend into subdirectories. */
	private static boolean recursive = false;
	/** Whether to compare directories/files both directions. */
	private static boolean syncMode = true;
	/** What level of output to use. */
	private static Level verbosity = Level.NORMAL;
	/** Count of files compared. */
	private static int numberOfFiles = 0;
	/** Count of mismatched files. */
	private static int numberOfMismatches = 0;

	/** Prefix for Intl strings. */
	private static final String INTL_PREFIX = "compare#compare.";


	/**
	 * Output a message to {@link System#err} depending on the verbosity level.
	 *
	 * @param level		The level of verbosity at which to output this message.
	 * @param formatKey	The Intl key for the format string of the message to output.
	 * @param args		The arguments for that format.
	 */
	private static void err(final Level level, final String formatKey, final Object... args) {
	    if (verbosity.meetsOrExceeds(level)) {
		Intl.errFormat(INTL_PREFIX + formatKey, args);
	    }
	}

	/**
	 * Output a message to {@link System#out} depending on the verbosity level.
	 *
	 * @param level		The level of verbosity at which to output this message.
	 * @param formatKey	The Intl key for the format string of the message to output.
	 * @param args		The arguments for that format.
	 */
	private static void msg(final Level level, final String formatKey, final Object... args) {
	    if (verbosity.meetsOrExceeds(level)) {
		Intl.outFormat(INTL_PREFIX + formatKey, args);
	    }
	}

	/**
	 * Exit with return code of {@code 1} unless {@link #continueAfterError} is set.
	 */
	private static void potentialExit() {
	    if (!continueAfterError) {
		// This should be the number of mismatches so far (namely only one).
		System.exit(1);
	    }
	}

	/**
	 * Compare two files, dealing with informational messages, trapping I/O errors, and dealing
	 * with the {@link #continueAfterError} flag.
	 *
	 * @param file1	The first file to compare.
	 * @param file2	The other file.
	 */
	private static void compareFiles(final File file1, final File file2) {
	    numberOfFiles++;
	    boolean match = false;

	    msg(Level.VERBOSE, "comparingTwo", file1.getPath(), file2.getPath());

	    try {
		match = FileUtilities.compareFiles(file1, file2);
		if (!match) {
		    err(Level.QUIET, "doesNotCompare", file1.getPath(), file2.getPath());
		}
	    }
	    catch (IOException ioe) {
		err(Level.QUIET, "errorOnCompare", ExceptionUtil.toString(ioe));
	    }
	    if (!match) {
		numberOfMismatches++;
		potentialExit();
	    }
	}

	/**
	 * The recursive procedure to compare two directories or two files.
	 *
	 * @param f1	The first file/directory to compare.
	 * @param f2	The second file/directory to compare it to.
	 * @param doingReverseSync	Set to true for the second pass in {@link #syncMode}.
	 */
	private static void recursiveCompare(final File f1, final File f2, final boolean doingReverseSync) {
	    if (f1.isDirectory()) {
		if (!f2.isDirectory()) {
		    err(Level.NORMAL, "notDirectory", f2.getPath());
		    potentialExit();
		}

		msg(Level.VERBOSE, "comparingFiles", f1.getPath(), f2.getPath());

		// TODO: use a filter for wildcard support
		String[] files = f1.list();
		for (String file : files) {
		    File file1 = new File(f1, file);
		    File file2 = new File(f2, file);
		    if (file1.isFile() && file2.exists() && file2.isFile()) {
			if (doingReverseSync) {
			    // This means we already did the compare going the other direction
			    // so just ignore it this time
			    continue;
			}
			compareFiles(file1, file2);
		    }
		    else if (file1.isDirectory() && file2.isDirectory()) {
			if (recursive) {
			    recursiveCompare(file1, file2, doingReverseSync);
			}
		    }
		    else {
			if (file2.exists())
			    err(Level.NORMAL, "targetNotFile", file2.getPath());
			else
			    err(Level.NORMAL, "targetNotExist", file2.getPath());
			potentialExit();
		    }
		}
	    }
	    else {
		if (!f2.isFile()) {
		    err(Level.NORMAL, "targetNotFile", f2.getPath());
		    potentialExit();
		}
		else if (!doingReverseSync) {
		    compareFiles(f1, f2);
		}
	    }
	}

	/**
	 * Print a Usage/Help message in the case of errors with the command line parameters.
	 */
	private static void usage() {
	    System.out.println();
	    Intl.printHelp("compare#compare");
	}

	/**
	 * Main program -- process the command line arguments.
	 *
	 * @param args	The parsed command line arguments.
	 */
	public static void main(String[] args) {
	    long memoryUseBefore = Runtime.getRuntime().freeMemory();

	    Environment.setProductName(Intl.getString("compare#compare.productName"));

	    List<String> pathArgs = new ArrayList<>();
	    boolean error = false;

	    for (String arg : args) {
		if (Options.matchesOption(arg, true, "verbose", "verb", "v"))
		    verbosity = Level.VERBOSE;
		else if (Options.matchesOption(arg, true, "SuperVerbose", "super", "sup"))
		    verbosity = Level.SUPER_VERBOSE;
		else if (Options.matchesOption(arg, true, "normal", "norm", "n"))
		    verbosity = Level.NORMAL;
		else if (Options.matchesOption(arg, true, "quiet", "q"))
		    verbosity = Level.QUIET;
		else if (Options.matchesOption(arg, true, "silent", "s"))
		    verbosity = Level.SILENT;
		else if (Options.matchesOption(arg, true, "debug", "deb", "d"))
		    verbosity = Level.DEBUG;
		else if (Options.matchesOption(arg, true, "recursive", "recurse", "rec", "r"))
		    recursive = true;
		else if (Options.matchesOption(arg, true, "SyncMode", "sync", "syn"))
		    syncMode = true;
		else if (Options.matchesOption(arg, true, "CopyMode", "copy", "c"))
		    syncMode = false;
		else if (Options.matchesOption(arg, true, "ContinueAfterError", "continue", "cont", "con"))
		    continueAfterError = true;
		else if (Options.matchesOption(arg, true, "BreakOnError", "break", "brk", "br", "b"))
		    continueAfterError = false;
		else if (Options.matchesOption(arg, true, "help", "h", "?")) {
		    usage();
		    return;
		}
		else if (Options.matchesOption(arg, true, "version", "vers", "ver", "v")) {
		    Environment.printProgramInfo();
		    return;
                }
	    // TODO option process (-caseinsensitive)
		else if (Options.isOption(arg) != null) {
		    err(Level.NORMAL, "unknownOption", arg);
		    error = true;
		}
		else {
		    msg(Level.SUPER_VERBOSE, "addingPath", arg);
		    pathArgs.add(arg);
		}
	    }

	    if (pathArgs.size() != 2) {
		error = true;
	    }

	    if (error) {
		if (verbosity.meetsOrExceeds(Level.NORMAL)) {
		    usage();
		}
		System.exit(1);
	    }

	    File f1 = new File(pathArgs.get(0));
	    File f2 = new File(pathArgs.get(1));

	    recursiveCompare(f1, f2, false);

	    if (syncMode) {
		recursiveCompare(f2, f1, true);
	    }

	    // Display the final statistics
	    if (verbosity.meetsOrExceeds(Level.NORMAL)) {
		if (numberOfMismatches == 0) {
		    if (numberOfFiles == 0)
			Intl.outPrintln("compare#compare.noFilesCompared");
		    else if (numberOfFiles == 1)
			Intl.outPrintln("compare#compare.oneFileCompared");
		    else
			Intl.outFormat("compare#compare.filesCompared", numberOfFiles);
		}
		else if (numberOfMismatches == 1) {
		    if (numberOfFiles == 1)
			Intl.outPrintln("compare#compare.oneMismatch");
		    else
			Intl.outFormat("compare#compare.mismatches", numberOfFiles, numberOfMismatches);
		}
		else {
		    Intl.outFormat("compare#compare.mismatches", numberOfFiles, numberOfMismatches);
		}

		if (verbosity.meetsOrExceeds(Level.VERBOSE)) {
		    long memoryUseAfter = Runtime.getRuntime().freeMemory();
		    Intl.outFormat("compare#compare.memoryUse", memoryUseBefore - memoryUseAfter);
		}
	    }

	    // The process exit code needs to reflect whether there were any mismatches or not.
	    if (numberOfMismatches > 0) {
		System.exit(numberOfMismatches > 255 ? 255 : numberOfMismatches);
	    }
	}
}
