/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017,2020 Roger L. Whitcomb.
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
 *	A companion to FileProcessor that will process a directory
 *	at a time, and can recurse down a whole directory tree.
 *
 *  History:
 *	06-Jan-2015 (rlwhitcomb)
 *	    Created.
 *	27-Jan-2015 (rlwhitcomb)
 *	    Don't abort the whole directory just because one file
 *	    returned "false".  Could be that the processor finished early.
 *	    Added processing for the "enterDirectory" method of LineProcessor.
 *	07-Jan-2016 (rlwhitcomb)
 *	    Fix Javadoc warnings found by Java 8.
 *	16-Jun-2017 (rlwhitcomb)
 *	    Code cleanup.  Add an option to quit immediately on errors.
 *	    Change return values to indicate whether an error stopped us.
 *	18-Feb-2020 (rlwhitcomb)
 *	    Add nesting level to the LineProcessor.enterDirectory method and
 *	    our "processFiles" method; call the new LineProcessor.exitDirectory
 *	    method as appropriate too; add another default flavor of "processDirectory".
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 */
package info.rlwhitcomb.util;

import java.io.File;


/**
 * Processor for a group of files in one directory and (optionally) its
 * subdirectories.
 * <p> Will call {@link FileProcessor} for each file encountered.
 */
public class DirectoryProcessor
{
	private File inputDir;
	private LineProcessor lp;

	/**
	 * Initialize processing for the given directory name and processor.
	 *
	 * @param dirName	Name of the directory to process.
	 * @param lp		The processor for each line of the files.
	 * @throws IllegalArgumentException if the input name is {@code null} or empty.
	 * @see	#init(File, LineProcessor)
	 */
	public DirectoryProcessor(String dirName, LineProcessor lp) {
	    if (CharUtil.isNullOrEmpty(dirName)) {
		throw new IllegalArgumentException(Intl.getString("util#dir.nullEmptyInput"));
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
	public DirectoryProcessor(File dir, LineProcessor lp) {
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
	private void init(File dir, LineProcessor lp) {
	    if (dir == null) {
		throw new IllegalArgumentException(Intl.getString("util#dir.nullInputDir"));
	    }
	    if (lp == null) {
		throw new IllegalArgumentException(Intl.getString("util#file.nullProcessor"));
	    }
	    if (!dir.exists() || !dir.canRead() || !dir.isDirectory()) {
		throw new IllegalArgumentException(Intl.formatString("util#dir.notExistNotDir", dir.getPath()));
	    }

	    this.inputDir = dir;
	    this.lp = lp;
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
	private boolean processFiles(File dir, boolean recurse, int level, boolean stopOnError) {
	    if (!this.lp.enterDirectory(dir, level)) {
		// Not wanting to process this directory is not an "error" per se.
		return true;
	    }
	    File[] files = dir.listFiles(this.lp);
	    if (files == null) {
		return this.lp.exitDirectory(dir, level, true);
	    }
	    for (File f : files) {
		if (f.canRead() && f.isDirectory()) {
		    if (recurse) {
			if (!processFiles(f, recurse, level + 1, stopOnError) && stopOnError) {
			    this.lp.exitDirectory(dir, level, false);
			    return false;
			}
		    }
		    // Else just continue to the next file in the current directory
		}
		else if (f.canRead() && f.isFile()) {
		    FileProcessor fp = new FileProcessor(f, this.lp);
		    if (!fp.processFile() && stopOnError) {
			this.lp.exitDirectory(dir, level, false);
			return false;
		    }
		    fp = null;
		}
	    }
	    return this.lp.exitDirectory(dir, level, true);
	}

	/**
	 * Process all files in the directory given in the constructor and all subdirectories as well.
	 * Will call the {@link LineProcessor#accept} method to determine which files and/or subdirectories
	 * to process.
	 * <p> Sets the "recurse" flag to true and "level" to 0 (assumes this is only called at the topmost
	 * directory level).
	 */
	public void processDirectory() {
	    processFiles(this.inputDir, true, 0, false);
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
	public void processDirectory(boolean recurse) {
	    processFiles(this.inputDir, recurse, 0, false);
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
	public void processDirectory(boolean recurse, boolean stopOnError) {
	    processFiles(this.inputDir, recurse, 0, stopOnError);
	}

}
