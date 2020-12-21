/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015,2017,2020 Roger L. Whitcomb.
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
 *	An interface for text file processors that examines one
 *	line at a time.
 *
 *  History:
 *	06-Jan-2015 (rlwhitcomb)
 *	    Created.
 *	27-Jan-2015 (rlwhitcomb)
 *	    Add "enterDirectory" method.
 *	31-Aug-2015 (rlwhitcomb)
 *	    Cleanup Javadoc (found by Java 8).
 *	16-Jun-2017 (rlwhitcomb)
 *	    Code cleanup.  Change return value from "handleError" to indicate
 *	    whether to terminate processing of the file/directory.
 *	18-Feb-2020 (rlwhitcomb)
 *	    Eliminate the Adapter class in favor of default methods; use StandardCharsets
 *	    for the UTF-8 charset; add "exitDirectory" method (for use with parallel processing).
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 */
package info.rlwhitcomb.util;

import java.io.File;
import java.io.FileFilter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 * Interface to any text file processor that can deal with one
 * line at a time.
 * <p> Processes that implement this interface can be called by
 * the {@link FileProcessor} or {@link DirectoryProcessor} classes
 * and get all the generic parts of text file processing done
 * automatically.
 * <p> The {@link LineProcessor} methods all have default implementations, suitable
 * for subclassing if not all methods of the interface are needed.
 * <p> Specifies the <code>UTF-8</code> character set for the input file.
 */
public interface LineProcessor extends FileFilter
{
	/**
	 * Specifies the {@link Charset} that the input file is encoded with.
	 * @return	the {@link Charset} to use to decode the file, or {@code null}
	 *		to use the platform default character set.
	 */
	default Charset getCharset() {
	    return StandardCharsets.UTF_8;
	}

	/**
	 * Handle any errors/exceptions encountered during reading.
	 * <p> Note: this method will not be called from within a <code>try</code>
	 * block, so any exceptions in here will likely terminate the application.
	 * @param	inputFile	The file we have been processing (for annotation purposes).
	 * @param	error		The exception that was caught.
	 * @return	{@code true} to continue processing, {@code false}
	 *		to abort processing for some reason.
	 */
	default boolean handleError(File inputFile, Throwable error) {
	    Intl.errFormat("util#line.genericError", inputFile == null ? "<none>" : inputFile.getPath(), error.getMessage());
	    return true;
	}

	/**
	 * Process one line of input.
	 * @param	line	The current input line.
	 * @return	{@code true} to continue processing, {@code false}
	 *		to abort processing for some reason.
	 */
	default boolean processLine(String line) {
	    return true;
	}

	/**
	 * Handle acceptance or pre-processing for a directory.  Only called from
	 * {@link DirectoryProcessor#processFiles}.
	 *
	 * @param	directory	The directory we're about to process.
	 * @param	level		The nesting level of this directory (0 at the root).
	 * @return	{@code true} to go ahead and process, {@code false} to skip.
	 */
	default boolean enterDirectory(File directory, int level) {
	    return true;
	}

	/**
	 * Handle post-processing for a directory.  Only called from
	 * {@link DirectoryProcessor#processFiles}.
	 * <p> Note: this will always be called if {@link #enterDirectory} returns {@code true}.
	 *
	 * @param	directory	The directory we're done processing.
	 * @param	level		The nesting level of this directory (0 at the root).
	 * @param	error		Whether processing was successful within the directory.
	 * @return	{@code true} to continue processing, {@code false} to stop.
	 */
	default boolean exitDirectory(File directory, int level, boolean error) {
	    return true;
	}

	/**
	 * Method of {@link FileFilter}, used to filter input files.
	 * @param	pathname	The full path to be tested by this filter.
	 * @return	{@code true} if the file should be processed, {@code false} otherwise.
	 */
	@Override
	default boolean accept(File pathname) {
	    return true;
	}

	/**
	 * Handle any stuff that needs to happen before starting to read the file.
	 * <p> This method can be used for setting up output files for the processing results.
	 * @param	inputFile	The file we are about to read.
	 * @return	{@code true} to continue processing, {@code false} to abort.
	 */
	default boolean preProcess(File inputFile) {
	    return true;
	}

	/**
	 * Handle any post-processing tasks.  Called after all lines of the file have
	 * been handled and the file has been closed.  Can be used to clean up or close
	 * any generated output files (for instance).
	 * @param	inputFile	The file we have just finished processing.
	 * @return	{@code true} to continue processing, {@code false} to abort.
	 */
	default boolean postProcess(File inputFile) {
	    return true;
	}

}

