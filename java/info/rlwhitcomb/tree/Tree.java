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
 *	Draw a tree-like representation of the current and lower directories
 *	and optionally the files and attributes.
 *
 * History:
 *	09-Apr-2020 (rlwhitcomb)
 *	    First coding in Java.
 *	14-Apr-2020 (rlwhitcomb)
 *	    Add new filter options; fix compile warning.
 *	14-Apr-2020 (rlwhitcomb)
 *	    New warning message if input file/directory does not exist.
 *	    Tweak some code.  Add "-help" output.  Add some Javadoc.
 *	    Display full path name for top-level files.
 *	14-Apr-2020 (rlwhitcomb)
 *	    Don't print full usage for warnings.
 *	14-Apr-2020 (rlwhitcomb)
 *	    Tweak the help output.
 *	14-Apr-2020 (rlwhitcomb)
 *	    New options for case-sensitive and -insensitive sorting.
 *	26-May-2020 (rlwhitcomb)
 *	    Add coloring to the output.
 *	    Add option to show hidden files and hide by default;
 *	    and tweak coloring for file attributes.
 *	16-Jun-2020 (rlwhitcomb)
 *	    Use Files method to probe the file type and change color
 *	    based on it.
 *	14-Jul-2020 (rlwhitcomb)
 *	    Implement option to not colorize the output if not supported
 *	    by the O/S (Windows, I'm looking at you!).
 *	    Display canonical path of starting directory (like "tree"
 *	    on Windows).
 *	17-Jul-2020 (rlwhitcomb)
 *	    Tweak colors on Windows, fix the executable test for Windows also.
 *	22-Jul-2020 (rlwhitcomb)
 *	    Add some more option choices now that Options can automatically add
 *	    choices like "mixed-case", or "mixed_case" from "MixedCase".
 *	30-Jul-2020 (rlwhitcomb)
 *	    Option to display program info (name, copyright, build info).
 *	11-Sep-2020 (rlwhitcomb)
 *	    Move text to resource file.
 *	08-Oct-2020 (rlwhitcomb)
 *	    Use version of "printProgramInfo" with defaults.
 *	07-Dec-2020 (rlwhitcomb)
 *	    Refactor ConsoleColor;
 *	11-Dec-2020 (rlwhitcomb)
 *	    Use new product info mechanism.
 *	22-Dec-2020 (rlwhitcomb)
 *	    Switch for dark mode (instead of relying on O/S).
 *	    Parse env var TREE_OPTIONS for switches.
 *	04-Jan-2021 (rlwhitcomb)
 *	    Locale option for errors, etc.
 *	21-Jan-2021 (rlwhitcomb)
 *	    Move "canExecute" logic into FileUtilities. Also use
 *	    "FileUtilities.canWrite" because of special considerations
 *	    on *nix systems (with "root" user).
 *	08-Feb-2021 (rlwhitcomb)
 *	    "Don't recurse" option. Tweak hidden directory color. Setup
 *	    to colorize error messages.
 *	    Finish coloring the help message.
 *	02-Mar-2021 (rlwhitcomb)
 *	    Allow comma-separated values for TREE_OPTIONS.
 *	19-May-2021 (rlwhitcomb)
 *	    Fix color usage b/c of new ConsoleColor paradigm.
 *	21-Oct-2021 (rlwhitcomb)
 *	    Use better method to get a valid Locale.
 *	21-Jan-2022 (rlwhitcomb)
 *	    #217: Use new Options method to process environment options.
 *	12-Apr-2022 (rlwhitcomb)
 *	    #269: New method to load main program info (in Environment).
 *	14-Apr-2022 (rlwhitcomb)
 *	    #274: Add "-depth" parameter.
 */
package info.rlwhitcomb.tree;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import info.rlwhitcomb.util.CharUtil;
import static info.rlwhitcomb.util.CharUtil.Justification.*;
import info.rlwhitcomb.util.ConsoleColor;
import static info.rlwhitcomb.util.ConsoleColor.Code.*;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.Exceptions;
import info.rlwhitcomb.util.FileUtilities;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.Options;

/**
 * Draw a directory tree on the console.
 */
public class Tree
{
	/* Line-drawing characters in Unicode. */
	private static final char SPC = ' ';
	private static final char ULS = '┌';
	private static final char URS = '┐';
	private static final char LLS = '└';
	private static final char LRS = '┘';
	private static final char VS = '│';
	private static final char HS = '─';
	private static final char LVTS = '├';
	private static final char RVTS = '┤';

	/** The amount each directory level is indented. */
	private static final int INDENT = 4;

	/** Whether we are running on Windows or not. */
	private static boolean runningOnWindows = Environment.isWindows();

	/** Flag to say we will use color on output. */
	private static boolean useColoring = true;

	/** Flag to say our backgrounds are dark or not. */
	private static boolean darkBackgrounds = runningOnWindows;

	/** Flag to say we are just printing the program info and not
	 * processing any directories.
	 */
	private static boolean showInfoOnly = false;

	/** Sort output by file name? */
	private static boolean sortByFileName = false;

	/** Sort output by directory name? */
 	private static boolean sortByDirectory = false;

	/** Only show directory names, not files? */
	private static boolean omitFiles = true;

	/** Show hidden files as well? */
	private static boolean showHidden = false;

	/** The sort order for file names. */
	private static SortOrder nameOrder = SortOrder.ASCENDING;

	/** The sort order for directory names. */
	private static SortOrder directoryOrder = SortOrder.ASCENDING;

	/** Whether sorting is case-sensitive or not. */
	private static CaseSensitivity casing = CaseSensitivity.MIXED_CASE;

	/** Whether to actually descend into subdirectories. */
	private static boolean recurse = true;

	/** Recursion depth (default "infinite"):
	 * <ul>
	 * <li>negative = {@code "infinite"}</li>
	 * <li>0 = print top-level directory name only ({@code "empty"})</li>
	 * <li>1 = print only files (if {@code -files} is given) at top level ({@code "files"})</li>
	 * <li>2 = only files and first-level directories ({@code "immediates"})</li>
	 * <li>3+ = only to depth n - 1</li>
	 * </ul>
	 */
	private static int maxDepth = -1;

	/** Locale used to format messages, etc. */
	private static Locale locale = null;


	private static final String ALL_FILES        = "AllFiles";
	private static final String OMIT_FILES       = "OmitFiles";
	private static final String MIXED_CASE       = "MixedCase";
	private static final String CASE_INSENSITIVE = "CaseInsensitive";
	private static final String SHOW_HIDDEN      = "ShowHidden";
	private static final String NO_COLORS        = "NoColors";
	private static final String NO_COLOR         = "NoColor";
	private static final String NO_COL           = "NoCol";
	private static final String NO_RECURSE       = "NoRecurse";
	private static final String DARK             = "DarkBackground";
	private static final String LIGHT            = "LightBackground";


	/**
	 * Format an error message from the resource key and arguments, possibly color it,
	 * then output to {@link System#err}.
	 *
	 * @param formatKey The resource key for the message template.
	 * @param args      Any arguments needed to replace in the message.
	 * @see #useColoring
	 */
	private static void error(final String formatKey, final Object... args) {
	    String msg = ConsoleColor.color(Intl.formatString(formatKey, args), useColoring);
	    System.err.println(msg);
	}

	/**
	 * Format a regular message using colors as appropriate.
	 *
	 * @param message The message to output, which may have color tags.
	 */
	private static void output(final String message) {
	    System.out.println(ConsoleColor.color(message, useColoring));
	}


	private static String line(char left, char mid, char right, int width) {
	    StringBuilder sb = new StringBuilder(width);
	    sb.append(left);
	    sb.append(CharUtil.makeStringOfChars(mid, width - 2));
	    sb.append(right);
	    return sb.toString();
	}

	private static String branchPrefix(int width, boolean multi) {
	    return line(multi ? LVTS : LLS, HS, SPC, width);
	}

	private static String parentPrefix(int width, boolean continuation) {
	    return CharUtil.padToWidth((continuation ? VS : SPC), width, SPC, LEFT);
	}


	/**
	 * Enumeration for sort ordering (either by name, or other criteria).
	 */
	private static enum SortOrder
	{
		ASCENDING,
		DESCENDING
	}

	/**
	 * Enumeration for case-sensitivity for alphabetic sorting.
	 */
	private static enum CaseSensitivity
	{
		MIXED_CASE,
		CASE_INSENSITIVE
	}

	/**
	 * Comparator for alphabetic sorting by file name.
	 */
	private static class FileNameComparator implements Comparator<File>
	{
		private final boolean asc;
		private final boolean caseSensitive;

		/**
		 * Construct according to the desired direction of the sort.
		 *
		 * @param order	The sort order to use.
		 * @param casing The mode to use for case differences.
		 */
		public FileNameComparator(SortOrder order, CaseSensitivity casing) {
		    this.asc = order == SortOrder.ASCENDING;
		    this.caseSensitive = casing == CaseSensitivity.MIXED_CASE;
		}

		@Override
		public int compare(File f1, File f2) {
		    String name1 = caseSensitive ? f1.getPath() : f1.getPath().toLowerCase();
		    String name2 = caseSensitive ? f2.getPath() : f2.getPath().toLowerCase();
		    if (asc)
			return name1.compareTo(name2);
		    else
			return name2.compareTo(name1);
		}
	}


	/**
	 * Comparator for sorting directories first or last.
	 * <p> Note: {@link SortOrder#ASCENDING ASCENDING} means directories are listed
	 * BEFORE files, while {@link SortOrder#DESCENDING DESCENDING} means files list
	 * first, followed by directories.
	 */
	private static class DirectoryComparator implements Comparator<File>
	{
		private final boolean asc;

		/**
		 * Construct according to the desired direction of the sort.
		 *
		 * @param order	The sort order to use.
		 */
		public DirectoryComparator(SortOrder order) {
		    this.asc = (order == SortOrder.ASCENDING);
		}

		@Override
		public int compare(File f1, File f2) {
		    int RESULT = asc ? +1 : -1;
		    boolean dir1 = f1.isDirectory();
		    boolean dir2 = f2.isDirectory();
		    int ret = 0;
		    if (!dir1 && dir2)
			ret = RESULT;
		    else if (dir1 && !dir2)
			ret = -RESULT;
		    return ret;
		}
	}


	/**
	 * Comparator that sorts by two other comparators, primary first, then secondary
	 * if the first returns a match.
	 */
	private static class DualComparator implements Comparator<File>
	{
		private final Comparator<File> c1;
		private final Comparator<File> c2;

		/**
		 * Establish the two comparators to use in tandem to sort things.
		 * @param comp1	The primary comparator.
		 * @param comp2	The secondary comparator when the first returns equal.
		 */
		public DualComparator(Comparator<File> comp1, Comparator<File> comp2) {
		    this.c1 = comp1;
		    this.c2 = comp2;
		}

		@Override
		public int compare(File f1, File f2) {
		    int ret = c1.compare(f1, f2);
		    if (ret == 0)
			ret = c2.compare(f1, f2);
		    return ret;
		}
	}

	/** The comparator to use for sorting files within their parent directory. */
	private static Comparator<File> sorter = null;


	/**
	 * File filter that either accepts only directories, or also regular files.
	 */
	private static class FilterOutFiles implements FileFilter
	{
		private final boolean directoriesOnly;
		private final boolean showHidden;

		public FilterOutFiles(boolean filter, boolean hidden) {
		    this.directoriesOnly = filter;
		    this.showHidden = hidden;
		}

		@Override
		public boolean accept(File path) {
		    if (path.isHidden()) {
			if (!showHidden)
			    return false;
		    }
		    if (directoriesOnly)
			return path.isDirectory();
		    return true;
		}
	}

	/** The filter to omit regular files or not. */
	private static FileFilter filter = null;



	/**
	 * Recursively print one entry and descend into child directories.
	 * @param file		The file/directory to display name (and directory contents).
	 * @param ancestors	The prefix to display according from grandparents up.
	 * @param parent	The new prefix for our immediate parent (applied to children)
	 * @param branch	The branch to display for this entry (according to my parent).
	 * @param depth		Current depth of the search.
	 * @param fullPath	Whether to display the full path for root files.
	 */
	private static void list(File file, String ancestors, String parent, String branch, int depth, boolean fullPath) {
	    String name = fullPath ? file.getPath() : file.getName();
	    boolean isDirectory = file.isDirectory();
	    ConsoleColor.Code nameEmphasis = darkBackgrounds ? WHITE_BOLD : BLACK_BOLD;
	    String type = null, typeDisplay = "";

	    if (isDirectory) {
		if (depth == 1 && maxDepth == 1) {
		    return;
		}
		if (file.isHidden()) {
		    nameEmphasis = RED_BOLD;
		}
	    }
	    else {
		try {
		    type = Files.probeContentType(file.toPath());
		} catch (IOException ioe) {
		    typeDisplay = Intl.getString("tree#unavailable");
		}
		if (file.isHidden()) {
		    nameEmphasis = RED_BOLD;
		}
		else if (FileUtilities.canExecute(file)) {
		    nameEmphasis = GREEN_BOLD_BRIGHT;
		}
		else if (FileUtilities.canWrite(file)) {
		    if (type == null) {
			nameEmphasis = darkBackgrounds ? WHITE : BLACK;
		    }
		    else {
			String[] typeParts = type.split("/");
			switch (typeParts[0]) {
			    case "text":
				switch (typeParts[1]) {
				    case "csv":
					nameEmphasis = MAGENTA;
					break;
				    default:
					nameEmphasis = darkBackgrounds ? CYAN_BOLD : BLUE_BOLD;
					break;
				}
				break;
			    case "application":
				switch (typeParts[1]) {
				    case "xml":
				    case "json":
					nameEmphasis = MAGENTA;
					break;
				    case "java-archive":
				    case "zip":
				    case "pdf":
					nameEmphasis = RED_BRIGHT;
					break;
				    case "x-msdownload":
					nameEmphasis = GREEN_BOLD_BRIGHT;
					break;
				    case "msword":
					nameEmphasis = darkBackgrounds ? CYAN_BOLD : BLUE_BOLD;
					break;
				    default:
					nameEmphasis = darkBackgrounds ? YELLOW_BRIGHT : RED_BRIGHT;
					typeDisplay = typeParts[1];
					break;
				}
				break;
			    case "image":
				nameEmphasis = YELLOW_BOLD;
				break;
			    case "audio":
				nameEmphasis = GREEN_UNDERLINED;
				typeDisplay = typeParts[1];
				break;
			    default:
				nameEmphasis = darkBackgrounds ? WHITE : BLACK;
				typeDisplay = type;
				break;
			}
		    }
		}
		else {
		    nameEmphasis = darkBackgrounds ? RED_BOLD_BRIGHT : CYAN_BOLD;
		}
	    }
	    if (typeDisplay.isEmpty()) {
		output(String.format("%s%s%s%s%s%s%s", BLACK_BRIGHT, ancestors, branch, nameEmphasis, name, RESET, RESET));
	    } else {
		output(String.format("%s%s%s%s%s%s%s (%s)", BLACK_BRIGHT, ancestors, branch, nameEmphasis, name, RESET, RESET, typeDisplay));
	    }

	    if (depth == 0 && maxDepth == 0) {
		return;
	    }
	    if (maxDepth >= 2 && depth >= maxDepth - 1) {
		return;
	    }

	    if (isDirectory && (recurse || fullPath)) {
		File[] files = file.listFiles(filter);
		if (files != null) {
		    if (sorter != null) {
			Arrays.sort(files, sorter);
		    }

		    for (int i = 0; i < files.length; i++) {
			boolean last = i == files.length - 1;
			File f = files[i];
			list(f, ancestors + parent, parentPrefix(INDENT, !last), branchPrefix(INDENT, !last), depth + 1, false);
		    }
		}
	    }
	}


	private static String colorOptions(String options) {
	    StringBuilder buf = new StringBuilder(options.length() * 3);
	    boolean colored = false;
	    for (int i = 0; i < options.length(); i++) {
		char ch = options.charAt(i);
		if (ch == '-' && !colored) {
		    buf.append("<Gr>");
		    colored = true;
		}
		else if (ch == ',') {
		    buf.append("<>");
		    colored = false;
		}
		buf.append(ch);
	    }
	    buf.append("<>");
	    return buf.toString();
	}

	private static String helpList(String option) {
	    return colorOptions(Options.getDisplayableOptions(Options.getMixedCaseOptions(option, true)));
	}

	private static void putHelpList(Map<String, String> symbols, String value) {
	    symbols.put(value, helpList(value));
	}

	private static void usage(String... messages) {
	    for (String message : messages) {
		output(message);
	    }

	    Map<String, String> symbols = new HashMap<>();
	    putHelpList(symbols, MIXED_CASE);
	    putHelpList(symbols, CASE_INSENSITIVE);
	    putHelpList(symbols, ALL_FILES);
	    putHelpList(symbols, OMIT_FILES);
	    putHelpList(symbols, SHOW_HIDDEN);
	    putHelpList(symbols, NO_COLORS);
	    symbols.put(NO_COLOR, helpList(NO_COLOR).replace(", <Gr>-nocolor<>", ""));
	    putHelpList(symbols, NO_COL);
	    putHelpList(symbols, NO_RECURSE);
	    putHelpList(symbols, DARK);
	    putHelpList(symbols, LIGHT);

	    Intl.printHelp("tree#usage", symbols);
	}


	/**
	 * Parse a depth parameter: either an integer (signed) or one of the
	 * "files", "empty", "immediates", or "infinity" (same as "svn").
	 * Where:<ul>
	 * <li>empty = list top-level directory name only</li>
	 * <li>files = list only files in top-level directory</li>
	 * <li>immediates = list first-level only files and directories</li>
	 * <li>infinity = list all files and directories to any depth</li>
	 * <li>negative number = same as "infinity"</li>
	 * <li>0 = same as "empty"</li>
	 * <li>1 = same as "files"</li>
	 * <li>2 = same as "immediates"</li>
	 * <li>any other positive integer = same as "immediates" but at depth n - 1</li>
	 * </ul>
	 *
	 * @param arg One of the values above.
	 * @throws IllegalArgumentException if the value is not recognized.
	 * @see #maxDepth
	 */
	private static void parseDepth(final String arg) {
	    String depth = arg;

	    if (depth.equalsIgnoreCase("empty") || depth.equalsIgnoreCase("e"))
		maxDepth = 0;
	    else if (depth.equalsIgnoreCase("files") || depth.equalsIgnoreCase("f"))
		maxDepth = 1;
	    else if (depth.equalsIgnoreCase("immediates") || depth.equalsIgnoreCase("immed") || depth.equalsIgnoreCase("imm"))
		maxDepth = 2;
	    else if (depth.equalsIgnoreCase("infinity") || depth.equalsIgnoreCase("inf"))
		maxDepth = -1;
	    else {
		try {
		    maxDepth = new BigDecimal(arg).intValueExact();
		}
		catch (IllegalArgumentException | ArithmeticException ex) {
		    throw new Intl.IllegalArgumentException("tree#errDepth", arg);
		}
	    }
	}

	private static void parseOptions(final String[] args, final List<String> argList) {
	    boolean expectLocale = false;
	    boolean expectDepth = false;

	    for (String arg : args) {
		if (expectLocale && Options.isOption(arg) != null) {
		    error("tree#errExpectLocale");
		    expectLocale = false;
		}
		else if (expectLocale) {
		    try {
			locale = Intl.getValidLocale(arg);
			if (locale != null && !locale.equals(Locale.getDefault())) {
			    Locale.setDefault(locale);
			    Intl.initAllPackageResources(locale);
			}
		    }
		    catch (IllegalArgumentException iae) {
			error("tree#error", Exceptions.toString(iae));
		    }
		    expectLocale = false;
		}
		else if (expectDepth) {
		    if (CharUtil.isValidNumber(arg) || Options.isOption(arg) == null) {
			try {
			    parseDepth(arg);
			}
			catch (IllegalArgumentException iae) {
			    error("tree#error", Exceptions.toString(iae));
			}
		    }
		    else {
			error("tree#errExpectDepth");
		    }
		    expectDepth = false;
		}
		else if (Options.matchesOption(arg, false, "alpha", "ascending", "asc", "a")) {
		    sortByFileName = true;
		    nameOrder = SortOrder.ASCENDING;
		}
		else if (Options.matchesOption(arg, false, "Alpha", "descending", "desc", "A")) {
		    sortByFileName = true;
		    nameOrder = SortOrder.DESCENDING;
		}
		else if (Options.matchesOption(arg, false, "directory", "dir", "d")) {
		    sortByDirectory = true;
		    directoryOrder = SortOrder.ASCENDING;
		}
		else if (Options.matchesOption(arg, false, "Directory", "Dir", "D")) {
		    sortByDirectory = true;
		    directoryOrder = SortOrder.DESCENDING;
		}
		else if (Options.matchesOption(arg, true, ALL_FILES, "files", "file", "all", "f")) {
		    omitFiles = false;
		}
		else if (Options.matchesOption(arg, true, OMIT_FILES, "omit", "o")) {
		    omitFiles = true;
		}
		else if (Options.matchesOption(arg, true, MIXED_CASE, "sensitive", "mixed", "mix", "m", "sens")) {
		    casing = CaseSensitivity.MIXED_CASE;
		    sortByFileName = true;
		}
		else if (Options.matchesOption(arg, true, CASE_INSENSITIVE, "insensitive", "case", "c", "i")) {
		    casing = CaseSensitivity.CASE_INSENSITIVE;
		    sortByFileName = true;
		}
		else if (Options.matchesOption(arg, true, SHOW_HIDDEN, "hidden", "hid", "show", "s")) {
		    showHidden = true;
		}
		else if (Options.matchesOption(arg, true, NO_RECURSE, "norec", "nor")) {
		    recurse = false;
		}
		else if (Options.matchesOption(arg, true, "colors", "color", "col")) {
		    useColoring = true;
		}
		else if (Options.matchesOption(arg, true, NO_COLORS, NO_COLOR, NO_COL, "no", "n")) {
		    useColoring = false;
		}
		else if (Options.matchesOption(arg, true, DARK, "darkback", "darkbg", "dark")) {
		    darkBackgrounds = true;
		}
		else if (Options.matchesOption(arg, true, LIGHT, "lightback", "lightbg", "light")) {
		    darkBackgrounds = false;
		}
		else if (Options.matchesOption(arg, true, "locale", "loc", "l")) {
		    expectLocale = true;
		}
		else if (Options.matchesOption(arg, true, "depth")) {
		    expectDepth = true;
		}
		else if (Options.matchesOption(arg, true, "help", "usage", "h", "u", "?")) {
		    usage();
		    showInfoOnly = true;
		}
		else if (Options.matchesOption(arg, true, "version", "vers", "ver", "v")) {
		    Environment.loadMainProgramInfo();
		    Environment.printProgramInfo();
		    showInfoOnly = true;
		}
		else if (Options.isOption(arg) != null) {
		    error("tree#warnUnknownOpt", arg);
		}
		else {
		    if (argList != null)
			argList.add(arg);
		}
	    }

	    if (expectLocale) {
		error("tree#errNoLocale");
	    }
	    if (expectDepth) {
		error("tree#errNoDepth");
	    }
	}


	/**
	 * Main program.
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
	    List<String> argList = new ArrayList<>(args.length);

	    // First, parse the TREE_OPTIONS env variable for predefined options
	    // (ignoring any non-options, that is directory names, here)
	    Options.environmentOptions(Tree.class, (options) -> {
		parseOptions(options, null);
	    });

	    // Now, scan the input arguments for options vs. file/directory specs
	    // (so, the command line options override the predefined ones)
	    parseOptions(args, argList);

	    if (showInfoOnly) {
		return;
	    }

	    if (argList.isEmpty()) {
		usage(Intl.getString("tree#errNoFilesGiven"), "");
		return;
	    }

	    // Construct a comparator based on the desired sorting options
	    if (sortByFileName && sortByDirectory) {
		Comparator<File> c1 = new DirectoryComparator(directoryOrder);
		Comparator<File> c2 = new FileNameComparator(nameOrder, casing);
		sorter = new DualComparator(c1, c2);
	    }
	    else if (sortByFileName) {
		sorter = new FileNameComparator(nameOrder, casing);
	    }
	    else if (sortByDirectory) {
		sorter = new DirectoryComparator(directoryOrder);
	    }

	    // Construct the filter for files + directories, or only directories
	    filter = new FilterOutFiles(omitFiles, showHidden);

	    for (String arg : argList) {
		File f = new File(arg);
		if (f.exists()) {
		    try {
			list(f.getCanonicalFile(), "", "", "", 0, true);
		    } catch (IOException ioe) {
			error("tree#errFileName", arg, Exceptions.toString(ioe));
		    }
		}
		else {
		    error("tree#warnNoFile", f.getPath());
		}
	    }
	}

}

