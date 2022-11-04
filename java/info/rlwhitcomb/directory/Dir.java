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
 *	Based on a DOS program "D.C" written in antiquity
 *	to be a better/simpler version of the "dir" command.
 *
 * History:
 *  20-Nov-20 rlw  ---	More work on new version in Java, loosely translated from C.
 *  15-Aug-21 rlw  ---	More coding using Java paradigms.
 *  12-Apr-22 rlw #269:	New method to load main program info (in Environment).
 *  18-Apr-22 rlw #270:	Make this automatic now.
 *  02-Nov-22 rlw #48:	More coding in the higher level methods.
 *  04-Nov-22		Process indirect files, process wildcards, begin real
 *			display code, add "link" to the attributes, process
 *			more command-line options.
 */
package info.rlwhitcomb.directory;

import info.rlwhitcomb.math.NumericUtil;
import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.Exceptions;
import info.rlwhitcomb.util.FileUtilities;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.Options;
import info.rlwhitcomb.util.WildcardFilter;

import java.awt.Dimension;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.attribute.*;
import java.nio.file.Files;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * Replacement for Windows "dir" command with similar but slightly more
 * powerful options and some subtle, but helpful shortcuts. Also lists
 * file attributes by default, which is a bit of a pain with the standard
 * tools.
 */
public class Dir
{
	/**
	 * Possible exit codes from the process.
	 */
	private static enum ExitCode
	{
		SUCCESS,
		CANNOT_FIND,
		READ_ERROR;

		public int getCode() {
		    return this.ordinal();
		}
	}


	/**
	 * Enumeration of the sorting options.
	 */
	private enum SortBy
	{
	    NAME,
	    FNAM,
	    EXTN,
	    DATE,
	    SIZE,
	    ATTR
	}

	/**
	 * The possible (there are only two) sort directions.
	 */
	private enum SortDirection
	{
	    ASCEND,
	    DESCEND
	}

	/**
	 * The possible filtering options.
	 */
	private enum FilterBy
	{
	    /** Filter by the full path name. */
	    NAME,
	    /** Filter by just the file name (last part of full path). */
	    FNAM,
	    /** Filter by the file extension. */
	    EXTN,
	    /** Filter by file last modified date. */
	    DATE,
	    /** Filter by the file size (length). */
	    SIZE,
	    /** Filter by one of the file attributes. */
	    ATTR,
	    /** Filter in/out the "." and ".." directories. */
	    DOTS,
	    /** Filter by whether the path is a directory or not. */
	    DRCT
	}

	/**
	 * The possible filtering bar (value "above" a given one, or "below" it).
	 */
	private enum FilterDirection
	{
	    /** Indicates the positive direction: size above limit, attribute is set, etc. */
	    ABOVE,
	    /** Indicates the negative direction; size below limit, attribute is not set, etc. */
	    BELOW
	}

	/**
	 * The Windows/DOS file attributes we want to deal with.
	 */
	private enum Attribute
	{
	    READONLY,
	    HIDDEN,
	    SYSTEM,
	    DIRECTORY,
	    ARCHIVED;

	    public static int value(final Set<Attribute> attrs) {
		int ret = 0;
		for (Attribute attr : attrs) {
		    ret |= 1 << attr.ordinal();
		}
		return ret;
	    }

	    public static int value(final Attribute... attrs) {
		int ret = 0;
		for (Attribute attr : attrs) {
		    ret |= 1 << attr.ordinal();
		}
		return ret;
	    }
	}

	/**
	 * One instance of a sort criteria, having the field to sort by and the direction of the sort.
	 * <p> Multiple sort criteria are possible, but some don't make sense, while others only apply
	 * in certain cases.
	 * @see #setSortCriteria for the necessary error checking
	 */
	private class SortCrit
	{
	    /** The field to sort by. */
	    SortBy field;
	    /** The direction of the sort (ascending or descending). */
	    SortDirection direction;
	    /** For name fields, whether to ignore case or not. */
	    boolean ignoreCase;

	    SortCrit(final SortBy fld, final SortDirection direct) {
		this.field     = fld;
		this.direction = direct;

		switch (fld) {
		    case NAME:
		    case FNAM:
		    case EXTN:
			// By default use the platform to determine case sensitivity
			this.ignoreCase = Environment.isWindows();
			break;
		    default:
			// No other fields need the "ignoreCase" setting
			break;
		}
	    }

	    SortCrit(final SortBy fld, final SortDirection direct, final boolean ignore) {
		this.field      = fld;
		this.direction  = direct;

		switch (fld) {
		    case NAME:
		    case FNAM:
		    case EXTN:
			this.ignoreCase = ignore;
			break;
		    default:
			// No other fields need the "ignoreCase" setting
			break;
		}
	    }
	}


	/**
	 * One instance of a filter criteria, having the field to filter on, and values to test against.
	 */
	private static class FilterCrit
	{
	    /** The field to filter on. */
	    FilterBy field;
	    /** The direction to filter in. */
	    FilterDirection direction;
	    /** Filter by size, the threshold value. */
	    long size;
	    /** Filter by attribute, which attribute it is. */
	    Attribute attrib;
	    /** Filter by date. */
	    Date date;
	    /** Filter by name (depends on the field which name it should compare to). */
	    String name;
	    /** Whether or not to ignore case when filtering names. */
	    boolean ignoreCase;

	    private FilterCrit(final FilterBy fld, final FilterDirection direct) {
		this.field = fld;
		this.direction = direct;
		// the rest of the fields are filled in by the other static constructors
	    }

	    static FilterCrit newSizeFilter(final long size, final FilterDirection direct) {
		FilterCrit crit = new FilterCrit(FilterBy.SIZE, direct);
		crit.size = size;
		return crit;
	    }

	    static FilterCrit newAttribFilter(final Attribute attr, final FilterDirection direct) {
		FilterCrit crit = new FilterCrit(FilterBy.ATTR, direct);
		crit.attrib = attr;
		return crit;
	    }

	    static FilterCrit newDateFilter(final Date date, final FilterDirection direct) {
		FilterCrit crit = new FilterCrit(FilterBy.DATE, direct);
		crit.date = date;
		return crit;
	    }

	    static FilterCrit newNameFilter(final String name, final FilterDirection direct, final boolean ignoreCase) {
		FilterCrit crit = new FilterCrit(FilterBy.FNAM, direct);
		crit.name       = name;
		crit.ignoreCase = ignoreCase;
		return crit;
	    }

	    static FilterCrit newPathFilter(final String name, final FilterDirection direct, final boolean ignoreCase) {
		FilterCrit crit = new FilterCrit(FilterBy.NAME, direct);
		crit.name       = name;
		crit.ignoreCase = ignoreCase;
		return crit;
	    }

	    static FilterCrit newExtnFilter(final String name, final FilterDirection direct, final boolean ignoreCase) {
		FilterCrit crit = new FilterCrit(FilterBy.EXTN, direct);
		crit.name       = name;
		crit.ignoreCase = ignoreCase;
		return crit;
	    }

	    static FilterCrit newDotFilter(final FilterDirection direct) {
		return new FilterCrit(FilterBy.DOTS, direct);
	    }

	    /**
	     * A filter criteria to include / exclude directories.
	     *
	     * @param direct	{@link FilterDirection#ABOVE} to include directories,
	     *			or {@link FilterDirection#BELOW} to exclude them.
	     * @return		The new object.
	     */
	    static FilterCrit newDirFilter(final FilterDirection direct) {
		return new FilterCrit(FilterBy.DRCT, direct);
	    }
	}


	/**
	 * Keep track of the drives we've visited and their space.
	 */
	private static class Drivespace
	{
	    boolean used = false;
	    long    availClusters = 0L;
	    long    clusterSize = 0L;
	}


	/**
	 * For sorting purposes, we need to save the entries as we read them,
	 * sort the by the criteria, then traverse the sorted list for display.
	 * This is the storage used for that purpose.
	 */
	private static class SaveListEntry
	{
		File dir;
		String name;
		Set<Attribute> attrib;
		long date;
		long size;

		SaveListEntry(final File directory, final String fileName,
			 final Set<Attribute> attrs, final long fileDate, final long fileSize) {
		    this.dir    = directory;
		    this.name   = fileName;
		    this.attrib = attrs;
		    this.date   = fileDate;
		    this.size   = fileSize;
		}

		public File getFile() {
		    return new File(dir, name);
		}

		@Override
		public String toString() {
		    return String.format("%1$s%2$s%3$s", dir, fileSeparator, name);
		}
	}


	/**
	 * A {@link FileFilter} that applies all the given filter criteria to the given file
	 * to determine if it should be included or not.
	 */
	private static class Filter implements FileFilter
	{
	    /**
	     * @return {@code true} if the file matches the criteria (which means it
	     * should NOT be accepted).
	     * @param file		The file to test.
	     * @param filterCrit	One of the given filtering criteria.
	     */
	    private boolean test(final File file, final FilterCrit filterCrit) {
		// Will be set true for the FilterDirection.ABOVE case, so reverse for BELOW
		boolean result = false;
		String name = null;

		switch (filterCrit.field) {
		    // Filter on the whole path
		    case NAME:
			name = getFullName(file);
			break;
		    // Filter on just the file name itself (exclude extension and parent path)
		    case FNAM:
			name = getFileName(file);
			break;
		    // Filter on just the file extension
		    case EXTN:
			name = getFileExtn(file);
			break;
		    // Filter by file last modified date
		    case DATE:
			result = file.lastModified() >= filterCrit.date.getTime();
			break;
		    // Filter by the file size (length)
		    case SIZE:
			result = file.length() >= filterCrit.size;
			break;
		    // Filter by one of the file attributes
		    case ATTR:
			result = getFileAttrs(file).contains(filterCrit.attrib);
			break;
		    // Filter in/out the "." and ".." directories
		    case DOTS:
			String testName = getFileName(file);
			result = testName.equals("..") || testName.equals(".");
			break;
		    // Filter by whether the path is a directory or not
		    case DRCT:
			result = file.isDirectory();	// TODO: Is this correct for the direction?
			break;
		}

		// Deal with the (more) common case of a name comparison
		if (name != null)
		    result = Match.stringMatch(name, filterCrit.name, !filterCrit.ignoreCase);

		// Deal with the filter direction
		return (filterCrit.direction == FilterDirection.ABOVE) ? result : !result;
	    }

	    @Override
	    public boolean accept(final File file) {
		// Go through all of the given filters, testing them in order of specification
		// and as soon as the file matches one, then do NOT accept it
		for (FilterCrit crit : filterCritList) {
		    if (test(file, crit))
			return false;
		}
		return true;
	    }
	}


	private static final boolean runningOnWindows = Environment.isWindows();
	private static final String fileSeparator     = Environment.fileSeparator();

	private static Set<File> directorySet = new TreeSet<>();
	private static File lastDirectory     = null;

	private static List<SaveListEntry> saveList = new ArrayList<>();

	private static long totalSize        = 0L;
	private static long totalClusterSize = 0L;
	private static long numFiles         = 0L;
	private static long numDirectories   = 0L;
	private static int linePos           = 0;
	private static String lastPath       = "";
	private static Drivespace[] drives   = new Drivespace[26];
	private static int currentDriveIndex = 0;

	static {
	    for (int drive = 0; drive < drives.length; drive++) {
		drives[drive] = new Drivespace();
	    }
	}

	private static boolean brief               = false;
	private static boolean wide                = false;
	private static boolean saveWide            = false;
	private static boolean bareName            = false;
	private static boolean withoutExt          = false;
	private static boolean fullName            = false;
	private static boolean paged               = false;
	private static boolean quoted              = false;
	private static boolean totalsOnly          = false;
	private static boolean unadorned           = false;
	private static boolean exactSize           = false;
	private static int limitRecursion          = 0;
	private static boolean errorLimitRecursion = false;
	private static int widestNameLen           = 0;
	private static boolean useForwardSlashes   = false;

	/** An empty set of file attributes for a "normal" file. */
	private static final Set<Attribute> NORMAL     = EnumSet.noneOf(Attribute.class);
	/** An empty set of filter criteria for NONE filtering. */
	private static final Set<FilterBy> FILTER_NONE = EnumSet.noneOf(FilterBy.class);

	/** The list of sorting criteria specified. */
	private static List<SortCrit> sortCritList     = new ArrayList<>();

	/** The list of filtering criteria specified. */
	private static List<FilterCrit> filterCritList = new ArrayList<>();

	private static int currentRow              = 0;

	private static int screenRows;
	private static int screenCols;

	private static final String hdr1 = "Attr     Size        Date       Time     Name";
	private static final String hdr2 = "---- ----------- ----------- ---------- ------------";
	private static final String ftr1 = "     -----------                        ------------";
	private static final String ftr2 = "  %14s bytes                 %7s file%s.";
	private static final String ftr3 = "  %14s bytes of space used   %7s director%s.";
	private static final String twos = "%s%s";


	/**
	 * Extract just the drive letter for the given file.
	 *
	 * @param f	The file under consideration.
	 * @return	The drive letter (string) if the file has a full path, otherwise "".
	 */
	private static String getDrive(final File f) {
	    String filePath = f.getPath();
	    if (filePath.indexOf(':') == 1) {
		return filePath.substring(0, 1);
	    }
	    return "";
	}

	private static String getFullName(final File f) {
	    try {
		return f.getCanonicalPath();
	    } catch (IOException ioe) {
		return f.getPath();
	    }
	}

	/**
	 * Extract just the file name part (minus the extension) for the given file.
	 *
	 * @param file	The file under consideration.
	 * @return	Just the file name part (no dot).
	 */
	private static String getFileName(final File file) {
	    String name = file.getName();

	    // Special case for "." or ".."
	    if (name == "." || name == "..")
		return name;

	    int pos = name.lastIndexOf('.');
	    return (pos >= 0) ? name.substring(0, pos) : name;
	}

	/**
	 * Extract the file name extension for the given file.
	 *
	 * @param file	The file under consideration.
	 * @return	The last extension (if any) of the file name (including the dot).
	 */
	private static String getFileExtn(final File file) {
	    String name = file.getName();

	    // Special case for "." or ".."
	    if (name == "." || name == "..")
		return "";

	    int pos = name.lastIndexOf('.');
	    return (pos >= 0) ? name.substring(pos) : "";
	}

	/**
	 * Get the set of file attributes from the given file.
	 *
	 * @param file	The file under consideration.
	 * @return	The set of attributes.
	 */
	private static Set<Attribute> getFileAttrs(final File file) {
	    Set<Attribute> attrs = EnumSet.noneOf(Attribute.class);
	    // TODO: finish this up
	    Path path = file.toPath();
	    try {
		BasicFileAttributes basicAttrs = null;
		DosFileAttributes dosAttrs = null;
		if (Files.getFileStore(path).supportsFileAttributeView(DosFileAttributeView.class)) {
		    dosAttrs = Files.readAttributes(path, DosFileAttributes.class);
		    basicAttrs = dosAttrs;
		}
		PosixFileAttributes posixAttrs = null;
		if (Files.getFileStore(path).supportsFileAttributeView(PosixFileAttributeView.class)) {
		    posixAttrs = Files.readAttributes(path, PosixFileAttributes.class);
		    basicAttrs = posixAttrs;
		}
//System.out.println("dosAttrs = " + dosAttrs + ", posixAttrs = " + posixAttrs);
System.out.println("size = " + basicAttrs.size() + ", createTime = " + basicAttrs.creationTime() + ", modifiedTime = " + basicAttrs.lastModifiedTime());
	    } catch (IOException ioe) {
	    }

	    return attrs;
	}


	/**
	 * Quote a file name with embedded spaces or other special characters.
	 *
	 * @param path	The path name string to quote.
	 * @return	Either the given path or the given path quoted, depending...
	 *
	 * @see <a href="http://stackoverflow.com/questions/30620876/how-to-properly-escape-filenames-in-windows-cmd-exe">http://stackoverflow.com/questions/30620876/how-to-properly-escape-filenames-in-windows-cmd-exe</a>
	 */
	private static String quoteName(final String path) {
	    // TODO: use a regex here instead
	    if (path.indexOf(' ') >= 0 ||
		path.indexOf('^') >= 0 ||
		path.indexOf('&') >= 0 ||
		path.indexOf(';') >= 0 ||
		path.indexOf(',') >= 0 ||
		path.indexOf('=') >= 0) {
		return CharUtil.addDoubleQuotes(path);
	    }
	    return path;
	}

	/**
	 * Get the set of DOS attributes for the given file.
	 *
	 * @param file	The file we're working on.
	 * @return	The set of attributes for the file. A normal, writeable file will have
	 *		an empty set (or {@link #NORMAL}.
	 */
	private static Set<Attribute> getAttributes(final File file) {
	    Set<Attribute> result = NORMAL;
	    try {
		DosFileAttributes attrs = Files.readAttributes(file.toPath(), DosFileAttributes.class);

		if (file.isDirectory())
		    result.add(Attribute.DIRECTORY);
		if (attrs.isArchive())
		    result.add(Attribute.ARCHIVED);
		if (attrs.isHidden())
		    result.add(Attribute.HIDDEN);
		if (attrs.isReadOnly())
		    result.add(Attribute.READONLY);
		if (attrs.isSystem())
		    result.add(Attribute.SYSTEM);
	    }
	    catch (IOException ioe) {
		System.err.println("Cannot read DosFileAttributes: " + Exceptions.toString(ioe));
	    }

	    return result;
	}

	private static String convertSlashes(final String path) {
	    if (useForwardSlashes) {
		return path.replaceAll("[\\\\]", "/");
	    }
	    return path;
	}

	/**
	 * Compute the filename and extension part of the path (given file attributes).
	 *
	 * @param path	The file or directory under consideration.
	 * @param attrs	The set of attributes associated with this file.
	 * @return	A suitably formatted name.
	 */
	private static String getName(final File path, Set<Attribute> attrs) {
	    String name = path.getName();
	    if (attrs.contains(Attribute.DIRECTORY)) {
		return String.format("[%s]", name);
	    }

	    return quoted ? quoteName(name) : name;
	}

	/**
	 * Compute drive and directory path of path.
	 *
	 * @param path	The file or directory under consideration.
	 * @return	The parent path of the given file.
	 */
	private static String getPath(final File path) {
	    return convertSlashes(path.getParent());
	}

	/**
	 * Given a drive letter, derive an index in the range of 0..n
	 * where drive "A:" is index 0, and so on.
	 *
	 * @param drive	The drive string (should be one letter).
	 * @return	The zero-based drive index, or -1 if the string
	 *		does not indicate a drive.
	 */
	private static int getDriveIndex(final String drive) {
	    if (drive != null && !drive.isEmpty()) {
		int dr = drive.codePointAt(0);

		if (dr >= 'A' && dr <= 'Z')
		    return (dr - 'A');
		else if (dr >= 'a' && dr <= 'a')
		    return (dr - 'a');
	    }
	    return -1;
	}

	/**
	 * Compute drive cluster space used by the file, and log the drive usage for it.
	 *
	 * @param path	The file or directory under consideration.
	 * @param size	The file size to be converted to cluster space.
	 * @return	The size rounded up to the cluster size.
	 */
	private static long logDrive(final File path, final long size) {
	    int driveIndex;
	    String drive = getDrive(path);

	    if ((driveIndex = getDriveIndex(drive)) < 0) {
		driveIndex = currentDriveIndex;
	    }

	    if (!drives[driveIndex].used) {
		drives[driveIndex].used = true;
		drives[driveIndex].availClusters = path.getUsableSpace();
		// TODO: need to scrap the cluster size b/c FileStore has total space and usable space in bytes!
// For Windows, you can run commands (like for console size: https://superuser.com/questions/120809/how-can-i-determine-the-sector-size-on-an-external-hard-drive)
// For Linux: https://askubuntu.com/questions/113227/find-cluster-size
// For OSX: https://apple.stackexchange.com/questions/389346/is-there-a-way-in-macos-to-view-filesystem-details-including-cluster-size-for
	    }
	    // TODO: how to compute cluster size of "size"??
	    return size;
	}

	/**
	 * Find and allocate a new directory list entry for the given path.
	 *
	 * @param path	The file or directory under consideration.
	 * @return	A new or existing directory entry object for this file.
	 */
	private static File dirListFind(final File path) {
	    File directory = path.getParentFile();

	    /* If there is no path part, just return. */
	    if (directory == null)
		return null;

	    /* First check if we match the last directory accessed (mostly true). */
	    if (lastDirectory != null && directory.equals(lastDirectory))
		return lastDirectory;

	    /* Add this directory to the set of already seen directories. */
	    directorySet.add(directory);

	    lastDirectory = directory;

	    return directory;
	}


	private static void setSortCriteria(final SortCrit newCriterion) {
	    // TODO: need to check existing criteria to see if this new one fits
	    // and if so, add to the list; might need to sort the criteria by
	    // most important first, or primary/secondary, something like that
	    // TODO: maybe need a return value to indicate success or an error message/flag
	}

	private static SaveListEntry listAlloc(final File path, Set<Attribute> attrs, long date, long size) {
	    SaveListEntry newList;
	    File directory;
	    String name;

	    directory = dirListFind(path);
	    name = path.getName();
	    newList = new SaveListEntry(directory, name, attrs, date, size);

	    /* Do the "widest name" calculation for "-w" display. */
	    if (saveWide) {
		String displayName = getName(path, attrs);
		widestNameLen = Math.max(widestNameLen, displayName.length());
	    }

	    return newList;
	}


	private static class SaveListComparator implements Comparator<SaveListEntry>
	{
		private int test(final SortCrit crit, final File f1, final File f2) {
		    int comp = 0;

		    switch (crit.field) {
			case NAME:
			    comp = getFullName(f1).compareTo(getFullName(f2));
			    break;
			case FNAM:
			    comp = getFileName(f1).compareTo(getFileName(f2));
			    break;
			case EXTN:
			    comp = getFileExtn(f1).compareTo(getFileExtn(f2));
			    break;
// TODO: need to get all the attributes (incl. times) at savelist creation time
// to minimize object allocation and re-allocation (esp. File)
		    }

		    return comp;
		}

		@Override
		public int compare(final SaveListEntry e1, final SaveListEntry e2) {
		    int comp = 0;

		    for (SortCrit crit : sortCritList) {
			comp = test(crit, e1.getFile(), e2.getFile());
			if (comp != 0)
			    break;
		    }

		    return comp;
		}

/*		    switch (sortCrit) {
			case NAME:
			    comp = compareFullNames(e1, e2);
			    break;

			case FNAM:
			case EXTN:
			    comp = compareFileNames(e1.name, e2.name, sortCrit);
			    break;

			case DATE:
			    if (e1.date > e2.date)
				comp = 1;
			    else if (e1.date < e2.date)
				comp = -1;
			    else
				comp = compareFullNames(e1, e2);
			    break;

			case SIZE:
			    if (e1.size > e2.size)
				comp = 1;
			    else if (e1.size < e2.size)
				comp = -1;
			    else
				comp = compareFullNames(e1, e2);
			    break;

			case ATTR:
			    int e1Attr = Attribute.value(e1.attrib);
			    int e2Attr = Attribute.value(e2.attrib);
			    if (e1Attr > e2Attr)
				comp = 1;
			    else if (e1Attr < e2Attr)
				comp = -1;
			    else
				comp = compareFullNames(e1, e2);
			    break;

			default:
			    comp = 0;
			    break;
		    }

//		    if (sortDir == Direction.DESCEND)
//			comp = -comp;

		    return comp;
		} */
	}

	private static final SaveListComparator saveListComparator = new SaveListComparator();


	private static void listInsert(SaveListEntry newListEntry) {
	    int insertIndex = Collections.binarySearch(saveList, newListEntry, saveListComparator);
	    if (insertIndex < 0) {
		// If no "equal" element found, insert a the correct point to maintain sorted order
		insertIndex = -(insertIndex + 1);
	    } else {
		// If an "equal" element was found, insert the new one after it
		insertIndex++;
	    }
	    saveList.add(insertIndex, newListEntry);
	}


	/**
	 * Callback for each file processed -- this does all the work.
	 *
	 * @param path		The file name (could have path or just name).
	 * @param attrs		The set of file attributes.
	 * @param date		File date/time.
	 * @param size		The file size.
	 * @return		The result of processing this file.
	 */
	private static int process(final String path, final Set<Attribute> attrs,
		final long date, final long size) {
	    SaveListEntry entry;

	    /* TODO: use a FileFilter to Check filtering criteria one after the other. */

	    return 0; // ??
	}


	private static void processSaveList() {
	    for (SaveListEntry list : saveList) {
		String name;
		if (list.dir == null)
		    name = list.name;
		else
		    name = String.format("%1$s%2$s", list.dir, list.name);
		process(name, list.attrib, list.date, list.size);
	    }
	}


	private static boolean processOneOption(String option) {
	    // Some options are case-sensitive, others not
	    switch (option) {
		case "full":
		case "F":
		    fullName = true;
		    return true;

		case "X":
		    exactSize = true;
		    return true;
	    }

	    switch (option.toLowerCase()) {
		case "brief":
		case "br":
		case "b":
		    brief = true;
		    break;

		case "exactsize":
		case "exact":
		case "ex":
		    exactSize = true;
		    break;

		case "version":
		case "vers":
		case "ver":
		case "v":
		    Environment.printProgramInfo(50);
		    return false;

		default:
		    Intl.errFormat("directory#unknownOption", option);
		    break;
	    }
	    return true;
	}


	private static boolean processOption(String option, List<String> specs) {
	    if (option.startsWith("--")) {
		if (!processOneOption(option.substring(2)))
		    return false;
	    }
	    else if (option.startsWith("-")) {
		if (!processOneOption(option.substring(1)))
		    return false;
	    }
	    else if (runningOnWindows && option.startsWith("/")) {
		if (!processOneOption(option.substring(1)))
		    return false;
	    }
	    else {
		specs.add(option);
	    }
	
	    return true;	// continue processing
	}


	private static void exit(ExitCode code) {
	    System.exit(code.getCode());
	}

	private static void processInputFile(final String inputFileName) {
	    File inputFile = new File(inputFileName);
	    if (FileUtilities.canRead(inputFile)) {
		try {
		    List<String> lines = FileUtilities.readFileAsLines(inputFile);
		    for (String line : lines) {
			String[] args = CharUtil.parseCommandLine(line);
			List<String> specs = new ArrayList<>();
			for (String arg : args) {
			    if (!processOption(arg, specs))
				return;
			}
			for (String spec : specs) {
			    processSpec(spec);
			}
		    }
		}
		catch (IOException ioe) {
		    Intl.errFormat("directory#readError", inputFileName, Exceptions.toString(ioe));
		    exit(ExitCode.READ_ERROR);
		}
	    }
	    else {
		Intl.errFormat("directory#cannotFindOrRead", inputFileName);
		exit(ExitCode.CANNOT_FIND);
	    }
	}

	private static void display(File file) {
	    StringBuilder buf = new StringBuilder();
	    FileInfo info = new FileInfo(file);

	    if (!brief) {
		buf.append(info.getAttributes()).append(' ');
		if (exactSize)
		    buf.append(String.format("%1$,10d ", info.getLength()));
		else
		    buf.append(String.format("%1$8s ", NumericUtil.formatToRangeTiny(info.getLength())));
	    }
	    if (fullName) {
		buf.append(info.getFullPath());
	    }
	    else {
		buf.append(info.getName());
	    }

	    System.out.println(buf.toString());
	}

	private static void processDirectory(File dir, FileFilter filter) {
	    if (FileUtilities.canReadDir(dir)) {
		File[] files = filter == null ? dir.listFiles() : dir.listFiles(filter);
		for (File file : files) {
		    display(file);
		}
	    }
	}

	private static void processSpec(String spec) {
	    // This could be a directory name, a wildcard file spec, or just a file name
	    // Note: Java actually processes some wildcard things already, but may not,
	    // depending on how we arrange the options: such as "-dir basedir spec1, spec2 ..."

	    File f = new File(spec);
	    if (Match.hasWildCards(spec)) {
		File parentDir = f.getParentFile();
		if (parentDir == null)
		    parentDir = Environment.userDirectory();

		WildcardFilter filter = new WildcardFilter(f.getName());	// TODO: case-sensitive flag based on O/S
		processDirectory(parentDir, filter);
	    }
	    else {
		if (FileUtilities.canReadDir(f)) {
		    processDirectory(f, null);
		}
		else if (FileUtilities.canRead(f)) {
		    display(f);
		}
		else {
		    Intl.outFormat("directory#cannotFindOrRead", spec);
		    exit(ExitCode.CANNOT_FIND);
		}
	    }
	}


	public static void main(String[] args) {
	    // TODO: a bunch of Environment and Intl stuff
	    Dimension d = Environment.consoleSize();
	    screenRows = d.height;
	    screenCols = d.width;

	    currentDriveIndex = getDriveIndex(getDrive(Environment.currentDirectory()));

	    final List<String> specs = new ArrayList<>();

	    Options.environmentOptions(Dir.class, a -> {
		for (String arg : a) {
		    if (!processOption(arg, specs))
			return;
		}
	    });

	    // Process command line options
	    for (String arg : args) {
		if (!processOption(arg, specs))
		    return;
	    }

	    // If there are no directory/file specs the just list the current directory
	    if (specs.isEmpty())
		specs.add(".");

	    for (String spec : specs) {
		if (spec.startsWith("@")) {
		    if (spec.length() > 1) {
			processInputFile(spec.substring(1));
		    }
		}
		else {
		    processSpec(spec);
		}
	    }

	    // TODO: for sorted outputs, need to display the saved/sorted values
	}

}

