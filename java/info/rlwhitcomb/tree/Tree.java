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
 */
package info.rlwhitcomb.tree;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import info.rlwhitcomb.util.CharUtil;
import static info.rlwhitcomb.util.CharUtil.Justification;
import info.rlwhitcomb.util.Options;

/**
 * Draw a directory tree.
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

	private static String line(char left, char mid, char right, int width) {
	    StringBuilder sb = new StringBuilder(width);
	    sb.append(left);
	    sb.append(CharUtil.makeStringOfChars(mid, width - 2));
	    sb.append(right);
	    return sb.toString();
	}

	private static String singleBranch(int width) {
	    return line(LLS, HS, SPC, width);
	}

	private static String multiBranch(int width) {
	    return line(LVTS, HS, SPC, width);
	}

	private static enum SortOrder
	{
		ASCENDING,
		DESCENDING
	}

	/**
	 * Comparator for alphabetic sorting by file name.
	 */
	private static class FileNameComparator implements Comparator<File>
	{
		private final boolean asc;

		/**
		 * Construct according to the desired direction of the sort.
		 *
		 * @param order	The sort order to use.
		 */
		public FileNameComparator(SortOrder order) {
		    this.asc = order == SortOrder.ASCENDING;;
		}

		@Override
		public int compare(File f1, File f2) {
		    if (asc)
			return f1.getPath().compareTo(f2.getPath());
		    else
			return -(f1.getPath().compareTo(f2.getPath()));
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
		    this.asc = order == SortOrder.ASCENDING;;
		}

		@Override
		public int compare(File f1, File f2) {
		    boolean dir1 = f1.isDirectory();
		    boolean dir2 = f2.isDirectory();
		    int ret = 0;
		    if (!dir1 && dir2)
			ret = +1;
		    else if (dir1 && !dir2)
			ret = -1;
		    if (asc)
			return ret;
		    else
			return -ret;
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

		public FilterOutFiles(boolean filter) {
		    this.directoriesOnly = filter;
		}

		@Override
		public boolean accept(File path) {
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
	 */
	private static void list(File file, String ancestors, String parent, String branch) {
	    String baseName = file.getName();
	    System.out.format("%s%s%s%n", ancestors, branch, baseName);

	    if (file.isDirectory()) {
		File[] files = file.listFiles(filter);
		if (sorter != null) {
		    Arrays.sort(files, sorter);
		}

		for (int i = 0; i < files.length; i++) {
		    boolean last = i == files.length - 1;
		    String newBranch = last ? singleBranch(INDENT) : multiBranch(INDENT);
		    String newParent = CharUtil.padToWidth((last ? SPC : VS), INDENT, SPC, Justification.LEFT);
		    File f = files[i];
		    list(f, ancestors + parent, newParent, newBranch);
		}
	    }
	}

	public static void main(String[] args) {
	    boolean sortByFileName = false;
	    boolean sortByDirectory = false;
	    boolean omitFiles = true;
	    SortOrder nameOrder = SortOrder.ASCENDING;
	    SortOrder directoryOrder = SortOrder.ASCENDING;
	    List<String> argList = new ArrayList<>(args.length);

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
		else if (Options.matchesOption(arg, false, "files", "file", "all", "f")) {
		    omitFiles = false;
		}
		else if (Options.matchesOption(arg, false, "omit", "o")) {
		    omitFiles = true;
		}
		else if (Options.isOption(arg) != null) {
		    System.err.println("WARNING: Ignoring unknown option: \"" + arg + "\"");
		}
		else {
		    argList.add(arg);
		}
	    }

	    // Construct a comparator based on the desired sorting options
	    if (sortByFileName && sortByDirectory) {
		Comparator<File> c1 = new DirectoryComparator(directoryOrder);
		Comparator<File> c2 = new FileNameComparator(nameOrder);
		sorter = new DualComparator(c1, c2);
	    }
	    else if (sortByFileName) {
		sorter = new FileNameComparator(nameOrder);
	    }
	    else if (sortByDirectory) {
		sorter = new DirectoryComparator(directoryOrder);
	    }

	    // Construct the filter for files + directories, or only directories
	    filter = new FilterOutFiles(omitFiles);

	    for (String arg : argList) {
		list(new File(arg), "", "", "");
	    }
	}

}

