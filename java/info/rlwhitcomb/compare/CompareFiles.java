/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2022 Roger L. Whitcomb.
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
 *	06-Nov-2020 (rlwhitcomb)
 *	    Use new Options processing to help.
 *	11-Dec-2020 (rlwhitcomb)
 *	    Use new program info mechanism.
 *	04-Jan-2021 (rlwhitcomb)
 *	    Tweak the final output.
 *	12-Apr-2021 (rlwhitcomb)
 *	    Tweak error checking.
 *	12-Apr-2022 (rlwhitcomb)
 *	    #269: New method to load main program info (in Environment).
 */
package info.rlwhitcomb.compare;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.Exceptions;
import info.rlwhitcomb.util.FileUtilities;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.Options;


/**
 * Compare sets of files.
 *
 * TODO: explicit wildcard support (using filenamefilter)
 */
public class CompareFiles
{
	/**
	 * Enumeration of the various possible output verbosity settings.
	 */
	private enum Level implements Options.ChoiceEnum
	{
		/** Silent implies no output at all, only setting the process exit code. */
		SILENT		("silent", "s"),
		/** Quiet means only the most necessary displays are done (compare failures, I/O errors). */
		QUIET		("quiet", "q"),
		/** Normal means all the not found/failure messages come out. */
		NORMAL		("normal", "norm", "n"),
		/** Verbose puts out messages all along the way of what's happening. */
		VERBOSE		("verbose", "verb", "v"),
		/** Super Verbose gets into nit-picky detail of the operation. */
		SUPER_VERBOSE	("SuperVerbose", "super", "sup"),
		/** Debug mode pretty much documents every line of code as we go. */
		DEBUG		("debug", "deb", "d");

		private String[] choices;

		Level(final String... validChoices) {
		    this.choices = validChoices;
		}

		@Override
		public String[] choices() {
		    return this.choices;
		}

		/**
		 * Match an input parameter against the list of valid choices for
		 * each level and decide if any match.
		 *
		 * @param input The input string to (hopefully) match to one of
		 *              our values.
		 * @return	Either one of our values or {@code None}.
		 */
		public static Optional<Level> match(final String input) {
		    return Options.matchIgnoreCase(values(), input);
		}

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


	/**
	 * The various program options (set by command line options).
	 */
	private static enum Opts implements Options.ToggleEnum
	{
		/** Whether to keep going after the first difference. */
		CONTINUE_AFTER_ERROR	(true,
					 "ContinueAfterError", "continue", "cont", "con"),
		BREAK_ON_ERROR		(CONTINUE_AFTER_ERROR,
					 "BreakOnError", "break", "brk", "br", "b"),
		/** Whether to descend into subdirectories. */
		RECURSIVE		(false,
					 "recursive", "recurse", "rec", "r"),
		/** Whether to compare directories/files both directions. */
		SYNC_MODE		(true,
					 "SyncMode", "sync", "syn"),
		COPY_MODE		(SYNC_MODE,
					 "CopyMode", "copy", "c"),
		/** Option to print the help message. */
		HELP			("help", "h", "?"),
		/** Option to print the version information. */
		VERSION			("version", "vers", "ver");

		private String[] choices;
		private boolean value;
		private Opts obverseOpt;

		Opts(final boolean initialValue, final String... validChoices) {
		    this.choices    = validChoices;
		    this.value      = initialValue;
		    this.obverseOpt = null;
		}

		Opts(final Opts otherOpt, final String... validChoices) {
		    this.choices    = validChoices;
		    this.value      = false;
		    this.obverseOpt = otherOpt;
		}

		Opts(final String... validChoices) {
		    this.choices    = validChoices;
		    this.value      = false;
		    this.obverseOpt = null;
		}

		@Override
		public String[] choices() {
		    return this.choices;
		}

		@Override
		public void set(final boolean newValue) {
		    if (obverseOpt != null)
			obverseOpt.value = !newValue;
		    else
			this.value = newValue;
		}

		@Override
		public boolean isSet() {
		    if (obverseOpt != null)
			return !obverseOpt.value;
		    else
			return this.value;
		}

		public static Optional<Opts> match(final String input) {
		    return Options.matchIgnoreCase(values(), input);
		}
	}


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
	 * Exit with return code of {@code 1} unless {@link Opts#CONTINUE_AFTER_ERROR} is set.
	 */
	private static void potentialExit() {
	    if (!Opts.CONTINUE_AFTER_ERROR.isSet()) {
		// This should be the number of mismatches so far (namely only one).
		System.exit(1);
	    }
	}

	/**
	 * Compare two files, dealing with informational messages, trapping I/O errors, and dealing
	 * with the {@link Opts#CONTINUE_AFTER_ERROR} flag.
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
		err(Level.QUIET, "errorOnCompare", Exceptions.toString(ioe));
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
	 * @param doingReverseSync	Set to true for the second pass in {@link Opts#SYNC_MODE}.
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
			if (Opts.RECURSIVE.isSet()) {
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
		if (!f1.exists()) {
		    err(Level.NORMAL, "sourceNotExist", f1.getPath());
		    potentialExit();
		}
		else if (!f1.isFile()) {
		    err(Level.NORMAL, "sourceNotFile", f1.getPath());
		    potentialExit();
		}
		else if (!f2.exists()) {
		    err(Level.NORMAL, "targetNotExist", f2.getPath());
		    potentialExit();
		}
		else if (!f2.isFile()) {
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

	    List<String> pathArgs = new ArrayList<>();
	    boolean error = false;

	    // Do the command line option processing
	    for (String arg : args) {
		String option = Options.isOption(arg);
		if (option != null) {
		    // Try the verbosity settings first
		    Optional<Level> level = Level.match(option);
		    if (level.isPresent()) {
			verbosity = level.get();
			continue;
		    }
		    // Next, try the regular program options
		    Optional<Opts> opt = Opts.match(option);
		    if (opt.isPresent()) {
			switch (opt.get()) {
			    case HELP:
				usage();
				return;
			    case VERSION:
				Environment.loadMainProgramInfo();
				Environment.printProgramInfo();
				return;
			}
		    }
	    // TODO option process (-caseinsensitive)
		    else {
			err(Level.NORMAL, "unknownOption", arg);
			error = true;
		    }
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

	    if (Opts.SYNC_MODE.isSet()) {
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
			Intl.outFormat("compare#compare.filesOneMismatch", numberOfFiles);
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
