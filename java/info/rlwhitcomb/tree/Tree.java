/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2020 Roger L. Whitcomb.
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
 */
package info.rlwhitcomb.tree;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import info.rlwhitcomb.util.CharUtil;
import static info.rlwhitcomb.util.CharUtil.Justification;
import info.rlwhitcomb.util.ConsoleColor;
import static info.rlwhitcomb.util.ConsoleColor.*;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.ExceptionUtil;
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

	/** List of "executable" file extensions (Windows only). */
	private static String[] executableExtensions;

	private static final String ALL_FILES        = "AllFiles";
	private static final String OMIT_FILES       = "OmitFiles";
	private static final String MIXED_CASE       = "MixedCase";
	private static final String CASE_INSENSITIVE = "CaseInsensitive";
	private static final String SHOW_HIDDEN      = "ShowHidden";
	private static final String NO_COLORS        = "NoColors";
	private static final String NO_COLOR         = "NoColor";
	private static final String NO_COL           = "NoCol";


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
	    return CharUtil.padToWidth((continuation ? VS : SPC), width, SPC, Justification.LEFT);
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
	 * Test if a file is an executable program.
	 * <p> On non-Windows environments we can use {@link File#canExecute}
	 * because there are flags to that effect. On Windows, however,
	 * every file is executable, but we can check the file extension
	 * to see if it is in the PATHEXT list and determine that way.
	 * @param path	The path to the file in question.
	 * @return Whether or not the file is an "executable".
	 */
	private static boolean canExecute(File path) {
	    if (runningOnWindows) {
		String name = path.getName();
		int dotPos = name.lastIndexOf('.');
		if (dotPos >= 0) {
		    String ext = name.substring(dotPos).toUpperCase();
		    return (Arrays.binarySearch(executableExtensions, ext) >= 0);
		}
		return false;
	    } else {
		return path.canExecute();
	    }
	}


	/**
	 * Recursively print one entry and descend into child directories.
	 * @param file		The file/directory to display name (and directory contents).
	 * @param ancestors	The prefix to display according from grandparents up.
	 * @param parent	The new prefix for our immediate parent (applied to children)
	 * @param branch	The branch to display for this entry (according to my parent).
	 * @param fullPath	Whether to display the full path for root files.
	 */
	private static void list(File file, String ancestors, String parent, String branch, boolean fullPath) {
	    String name = fullPath ? file.getPath() : file.getName();
	    boolean isDirectory = file.isDirectory();
	    ConsoleColor nameEmphasis = darkBackgrounds ? WHITE_BOLD : BLACK_BOLD;
	    String type = null, typeDisplay = "";
	    if (!isDirectory) {
		try {
		    type = Files.probeContentType(file.toPath());
		} catch (IOException ioe) {
		    typeDisplay = Intl.getString("tree#unavailable");
		}
		if (file.isHidden()) {
		    nameEmphasis = RED;
		}
		else if (canExecute(file)) {
		    nameEmphasis = GREEN_BOLD_BRIGHT;
		}
		else if (file.canWrite()) {
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
	    if (useColoring) {
		if (typeDisplay.isEmpty()) {
		    System.out.format("%s%s%s%s%s%s%n", BLACK_BRIGHT, ancestors, branch, nameEmphasis, name, RESET);
		} else {
		    System.out.format("%s%s%s%s%s%s (%s)%n", BLACK_BRIGHT, ancestors, branch, nameEmphasis, name, RESET, typeDisplay);
		}
	    } else {
		if (typeDisplay.isEmpty()) {
		    System.out.format("%s%s%s%n", ancestors, branch, name);
		} else {
		    System.out.format("%s%s%s (%s)%n", ancestors, branch, name, typeDisplay);
		}
	    }

	    if (isDirectory) {
		File[] files = file.listFiles(filter);
		if (files != null) {
		    if (sorter != null) {
			Arrays.sort(files, sorter);
		    }

		    for (int i = 0; i < files.length; i++) {
			boolean last = i == files.length - 1;
			File f = files[i];
			list(f, ancestors + parent, parentPrefix(INDENT, !last), branchPrefix(INDENT, !last), false);
		    }
		}
	    }
	}


	private static String helpList(String option) {
	    return Options.getDisplayableOptions(Options.getMixedCaseOptions(option, true));
	}

	private static void usage(String... messages) {
	    for (String message : messages) {
		System.out.println(message);
	    }

	    Map<String, String> symbols = new HashMap<>();
	    symbols.put("MIXED_CASE",       helpList(MIXED_CASE));
	    symbols.put("CASE_INSENSITIVE", helpList(CASE_INSENSITIVE));
	    symbols.put("ALL_FILES",        helpList(ALL_FILES));
	    symbols.put("OMIT_FILES",       helpList(OMIT_FILES));
	    symbols.put("SHOW_HIDDEN",      helpList(SHOW_HIDDEN));
	    symbols.put("NO_COLORS",        helpList(NO_COLORS));
	    symbols.put("NO_COLOR",         helpList(NO_COLOR).replace(", -nocolor", ""));
	    symbols.put("NO_COL",           helpList(NO_COL));

	    Intl.printHelp("tree#usage", symbols);
	}


	/**
	 * Main program.
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
	    boolean showInfoOnly = false;
	    boolean sortByFileName = false;
	    boolean sortByDirectory = false;
	    boolean omitFiles = true;
	    boolean showHidden = false;
	    SortOrder nameOrder = SortOrder.ASCENDING;
	    SortOrder directoryOrder = SortOrder.ASCENDING;
	    CaseSensitivity casing = CaseSensitivity.MIXED_CASE;
	    List<String> argList = new ArrayList<>(args.length);

	    Environment.setProductName(Intl.getString("tree#productName"));

	    // Setup (for Windows) the executable file extension list
	    if (runningOnWindows) {
		String exts = System.getenv("PATHEXT");
		executableExtensions = exts.split(";");
		Arrays.sort(executableExtensions);
	    }

	    // Scan the input arguments for options vs. file/directory specs
	    for (String arg : args) {
		if (Options.matchesOption(arg, false, "alpha", "ascending", "asc", "a")) {
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
		else if (Options.matchesOption(arg, true, MIXED_CASE, "sensitive", "mixed", "mix", "m", "s")) {
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
		else if (Options.matchesOption(arg, true, "colors", "color", "col")) {
		    useColoring = true;
		}
		else if (Options.matchesOption(arg, true, NO_COLORS, NO_COLOR, NO_COL, "no", "n")) {
		    useColoring = false;
		}
		else if (Options.matchesOption(arg, true, "help", "usage", "h", "u", "?")) {
		    usage();
		    showInfoOnly = true;
		}
		else if (Options.matchesOption(arg, true, "version", "vers", "ver", "v")) {
		    Environment.printProgramInfo(System.out, -1);
		    showInfoOnly = true;
		}
		else if (Options.isOption(arg) != null) {
		    Intl.errFormat("tree#warnUnknownOpt", arg);
		}
		else {
		    argList.add(arg);
		}
	    }

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
			list(f.getCanonicalFile(), "", "", "", true);
		    } catch (IOException ioe) {
			Intl.errFormat("tree#errFileName", arg, ExceptionUtil.toString(ioe));
		    }
		}
		else {
		    Intl.errFormat("tree#warnNoFile", f.getPath());
		}
	    }
	}

}

