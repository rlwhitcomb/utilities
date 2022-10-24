/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017,2020-2022 Roger L. Whitcomb.
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
 *	A companion to FileProcessor that will process one directory at a time,
 *	and can recurse down a whole directory tree, with optional wildcard
 *	filtering.
 *
 * History:
 *  06-Jan-15 rlw  ---	Created.
 *  27-Jan-15 rlw  ---	Don't abort the whole directory just because one file
 *			returned "false".  Could be that the processor finished early.
 *			Added processing for the "enterDirectory" method of LineProcessor.
 *  07-Jan-16 rlw  ---	Fix Javadoc warnings found by Java 8.
 *  16-Jun-17 rlw  ---	Code cleanup.  Add an option to quit immediately on errors.
 *			Change return values to indicate whether an error stopped us.
 *  18-Feb-20 rlw  ---	Add nesting level to the LineProcessor.enterDirectory method and
 *			our "processFiles" method; call the new LineProcessor.exitDirectory
 *			method as appropriate too; add another default flavor of "processDirectory".
 *  10-Mar-20 rlw  ---	Prepare for GitHub.
 *  21-Dec-20 rlw  ---	Update obsolete Javadoc constructs.
 *  29-Jan-21 rlw  ---	Use new Intl Exception variants for convenience.
 *  06-Sep-21 rlw  ---	Use FileUtilities.canRead everywhere. Final parameters.
 *  19-Sep-22 rlw #448:	Change default "exitDirectory" return value; add optional
 *			wildcard pattern matcher, and "name only" mode.
 *  21-Oct-22 rlw #473:	New wildcard filter + flags method.
 *  24-Oct-22 rlw #473:	Need to call "preProcess" and "postProcess" on directories in "nameOnly" mode.
 *			Another "setFilter" method with "ignoreCase" parameter.
 */
package info.rlwhitcomb.util;

import java.io.File;


/**
 * Processor for a group of files in one directory and (optionally) its
 * subdirectories.
 * <p> Will call {@link FileProcessor} for each file encountered, which will
 * in turn call the supplied {@link LineProcessor} for each file and line in it.
 * <p> Accepts an optional wildcard pattern for matching files before processing.
 */
public class DirectoryProcessor
{
	private File inputDir;
	private LineProcessor lp;
	private WildcardFilter filter;
	private boolean nameOnly;


	/**
	 * Initialize processing for the given directory name and processor.
	 *
	 * @param dirName	Name of the directory to process.
	 * @param lp		The processor for each line of the files.
	 * @throws IllegalArgumentException if the input name is {@code null} or empty.
	 * @see	#init(File, LineProcessor)
	 */
	public DirectoryProcessor(final String dirName, final LineProcessor lp) {
	    if (CharUtil.isNullOrEmpty(dirName)) {
		throw new Intl.IllegalArgumentException("util#dir.nullEmptyInput");
	    }

	    init(new File(dirName), lp);
	}

	/**
	 * Initialize processing for the given directory and processor.
	 *
	 * @param dir		The directory to process..
	 * @param lp		The processor for each line of the files.
	 * @throws IllegalArgumentException if the input file is {@code null}.
	 * @see	#init(File, LineProcessor)
	 */
	public DirectoryProcessor(final File dir, final LineProcessor lp) {
	    init(dir, lp);
	}

	/**
	 * Initialize processing for the given directory and given {@link LineProcessor}.
	 * <p> Does input validation.
	 *
	 * @param dir	The directory to process.
	 * @param lp	The processor for each line of the files in the directory.
	 * @throws IllegalArgumentException for {@code null} inputs or a non-existent or
	 * non-accessible directory.
	 */
	private void init(final File dir, final LineProcessor lp) {
	    if (dir == null) {
		throw new Intl.IllegalArgumentException("util#dir.nullInputDir");
	    }
	    if (lp == null) {
		throw new Intl.IllegalArgumentException("util#file.nullProcessor");
	    }
	    if (!FileUtilities.canReadDir(dir)) {
		throw new Intl.IllegalArgumentException("util#dir.notExistNotDir", dir.getPath());
	    }

	    this.inputDir = dir;
	    this.lp = lp;
	    this.filter = null;
	    this.nameOnly = false;
	}

	/**
	 * Set "name only" mode, where we don't invoke a {@link FileProcessor} for each
	 * file to be processed, but simply call the {@link LineProcessor#preProcess} and
	 * {@link LineProcessor#postProcess} on the files.
	 *
	 * @param mode	Whether to set "name only" mode or not (default is, of course, {@code false}).
	 * @return	This object (for chained operations).
	 */
	public DirectoryProcessor setNameOnlyMode(final boolean mode) {
	    nameOnly = mode;
	    return this;
	}

	/**
	 * Set the wildcard filter for this processor.
	 *
	 * @param filterString	The wildcard spec used to match the files to be processed.
	 * @return		This object (for chained operations).
	 */
	public DirectoryProcessor setWildcardFilter(final String filterString) {
	    filter = new WildcardFilter(filterString);
	    return this;
	}

	/**
	 * Set the wildcard plus flags filter for this processor.
	 *
	 * @param filterString	The wildcard spec used to match the files to be processed.
	 * @param filterFlags	Flags for matching the file type.
	 * @return		This object (for chained operations).
	 */
	public DirectoryProcessor setWildcardFilter(final String filterString, final String filterFlags) {
	    filter = new WildcardFilter(filterString, filterFlags);
	    return this;
	}

	/**
	 * Set the wildcard plus flags and ignoreCase filter for this processor.
	 *
	 * @param filterString	The wildcard spec used to match the files to be processed.
	 * @param filterFlags	Flags for matching the file type.
	 * @param ignoreCase	Whether to also ignore case of file names when matching.
	 * @return		This object (for chained operations).
	 */
	public DirectoryProcessor setWildcardFilter(final String filterString, final String filterFlags, final boolean ignoreCase) {
	    filter = new WildcardFilter(filterString, filterFlags, ignoreCase);
	    return this;
	}

	/**
	 * If a filter is present, check it to see if the given file should be processed.
	 *
	 * @param f	The file to check against the filter (if present).
	 * @return	{@code true} if the filter is absent, or if present, this file passes
	 *		the filter and should be processed.
	 */
	private boolean checkFilter(final File f) {
	    if (filter == null) {
		return true;
	    }
	    return filter.accept(f);
	}

	/**
	 * Recursive method to process all files in the given directory, with the option
	 * to descend into child directories as well.
	 * <p> Note: The "stopOnError" flag affects the return value from this method, but
	 * only in certain cases.  {@link LineProcessor#enterDirectory} is benign in this
	 * regard, meaning if that method returns {@code false} this method will still return
	 * {@code true}.  Likewise for the {@link LineProcessor#accept} method.
	 *
	 * @param dir		The directory to traverse.
	 * @param recurse	Whether or not to recurse into subdirectories.
	 * @param level		The nesting level of this directory from the root.
	 * @param stopOnError	Whether or not to stop on any errors.
	 * @return	{@code true} if either no errors were encountered (as reported by
	 *		the {@link LineProcessor}), or <var>stopOnError</var> is {@code false};
	 *		{@code false} if any errors were reported and <var>stopOnError</var> is
	 *		{@code true}.
	 */
	private boolean processFiles(final File dir, final boolean recurse, final int level, final boolean stopOnError) {
	    if (!lp.enterDirectory(dir, level)) {
		// Not wanting to process this directory is not an "error" per se.
		return true;
	    }

	    File[] files = dir.listFiles(lp);
	    if (files == null) {
		return lp.exitDirectory(dir, level, true);
	    }

	    for (File f : files) {
		if (checkFilter(f)) {
		    if (FileUtilities.canReadDir(f)) {
			if (nameOnly) {
			    if (!lp.preProcess(f) && stopOnError) {
				return lp.exitDirectory(dir, level, false);
			    }
			}
			if (recurse) {
			    if (!processFiles(f, recurse, level + 1, stopOnError) && stopOnError) {
				return lp.exitDirectory(dir, level, false);
			    }
			}
			if (nameOnly) {
			    if (!lp.postProcess(f) && stopOnError) {
				return lp.exitDirectory(dir, level, false);
			    }
			}
		    }
		    else if (FileUtilities.canRead(f)) {
			if (nameOnly) {
			    if (!lp.preProcess(f) && stopOnError) {
				return lp.exitDirectory(dir, level, false);
			    }
			    if (!lp.postProcess(f) && stopOnError) {
				return lp.exitDirectory(dir, level, false);
			    }
			}
			else {
			    FileProcessor fp = new FileProcessor(f, lp);
			    if (!fp.processFile() && stopOnError) {
				return lp.exitDirectory(dir, level, false);
			    }
			    fp = null;
			}
		    }
		}
	    }

	    return lp.exitDirectory(dir, level, true);
	}

	/**
	 * Process all files in the directory given in the constructor and all subdirectories as well.
	 * Will call the {@link LineProcessor#accept} method to determine which files and/or subdirectories
	 * to process.
	 * <p> Sets the "recurse" flag to true and "level" to 0 (assumes this is only called at the topmost
	 * directory level).
	 *
	 * @return This object for chaining.
	 */
	public DirectoryProcessor processDirectory() {
	    processFiles(inputDir, true, 0, false);
	    return this;
	}

	/**
	 * Process all files in the directory given in the constructor and (optionally)
	 * all subdirectories as well.  Will call the {@link LineProcessor#accept} method
	 * to determine which files and/or subdirectories to process.
	 * <p> Sets the "level" to 0 (assumes this is only called at the topmost directory level).
	 *
	 * @param	recurse	Whether or not to descend into subdirectories or limit
	 *		processing to the specified directory only.
	 */
	public DirectoryProcessor processDirectory(final boolean recurse) {
	    processFiles(inputDir, recurse, 0, false);
	    return this;
	}

	/**
	 * Process all files in the directory given in the constructor and (optionally)
	 * all subdirectories as well.  Will call the {@link LineProcessor#accept} method
	 * to determine which files and/or subdirectories to process.
	 * <p> Option is available to stop processing as soon as any file/line is in error.
	 * The default is to process all files regardless.
	 * <p> Sets the "level" to 0 (assumes this is only called at the topmost directory level).
	 *
	 * @param	recurse	Whether or not to descend into subdirectories or limit
	 *		processing to the specified directory only.
	 * @param	stopOnError	Whether or not to stop on any errors.
	 */
	public DirectoryProcessor processDirectory(final boolean recurse, final boolean stopOnError) {
	    processFiles(inputDir, recurse, 0, stopOnError);
	    return this;
	}

}
