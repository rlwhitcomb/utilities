/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015,2017,2020-2022 Roger L. Whitcomb.
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
 *	An interface for text file processors that examines one line at a time.
 *
 * History:
 *  06-Jan-15 rlw  ---	Created.
 *  27-Jan-15 rlw  ---	Add "enterDirectory" method.
 *  31-Aug-15 rlw  ---	Cleanup Javadoc (found by Java 8).
 *  16-Jun-17 rlw  ---	Code cleanup.  Change return value from "handleError" to indicate
 *			whether to terminate processing of the file/directory.
 *  18-Feb-20 rlw  ---	Eliminate the Adapter class in favor of default methods; use StandardCharsets
 *			for the UTF-8 charset; add "exitDirectory" method (for use with parallel processing).
 *  10-Mar-20 rlw  ---	Prepare for GitHub.
 *  21-Dec-20 rlw  ---	Update obsolete Javadoc constructs.
 *  06-Sep-21 rlw  ---	Final parameters.
 *  18-Feb-22 rlw  ---	Use Exceptions to get better error messages.
 *  19-Sep-22 rlw #448:	Change "exitDirectory" default return value.
 *  07-Nov-22 rlw #48:	Implement FileVisitor interface here.
 */
package info.rlwhitcomb.util;

import info.rlwhitcomb.util.Constants;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;


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
public interface LineProcessor extends FileFilter, FileVisitor<Path>
{
	/**
	 * Specifies the {@link Charset} that the input file is encoded with.
	 * @return	the {@link Charset} to use to decode the file, or {@code null}
	 *		to use the platform default character set.
	 */
	default Charset getCharset() {
	    return Constants.UTF_8_CHARSET;
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
	default boolean handleError(final File inputFile, final Throwable error) {
	    Intl.errFormat("util#line.genericError", inputFile == null ? "<none>" : inputFile.getPath(), Exceptions.toString(error));
	    return true;
	}

	/**
	 * Process one line of input.
	 * @param	line	The current input line.
	 * @return	{@code true} to continue processing, {@code false}
	 *		to abort processing for some reason.
	 */
	default boolean processLine(final String line) {
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
	default boolean enterDirectory(final File directory, final int level) {
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
	default boolean exitDirectory(final File directory, final int level, final boolean error) {
	    return !error;
	}

	/**
	 * Method of {@link FileFilter}, used to filter input files.
	 * @param	pathname	The full path to be tested by this filter.
	 * @return	{@code true} if the file should be processed, {@code false} otherwise.
	 */
	@Override
	default boolean accept(final File pathname) {
	    return true;
	}

	/**
	 * Handle any stuff that needs to happen before starting to read the file.
	 * <p> This method can be used for setting up output files for the processing results.
	 * @param	inputFile	The file we are about to read.
	 * @return	{@code true} to continue processing, {@code false} to abort.
	 */
	default boolean preProcess(final File inputFile) {
	    return true;
	}

	/**
	 * Handle any post-processing tasks.  Called after all lines of the file have
	 * been handled and the file has been closed.  Can be used to clean up or close
	 * any generated output files (for instance).
	 * @param	inputFile	The file we have just finished processing.
	 * @return	{@code true} to continue processing, {@code false} to abort.
	 */
	default boolean postProcess(final File inputFile) {
	    return true;
	}

	@Override
	default FileVisitResult preVisitDirectory(final Path dir, BasicFileAttributes attrs) {
	    return enterDirectory(dir.toFile(), 0) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
	}

	@Override
	default FileVisitResult postVisitDirectory(final Path dir, final IOException exc) {
	    return exitDirectory(dir.toFile(), 0, exc == null ? false : true) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
	}

	@Override
	default FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
	    return FileVisitResult.CONTINUE;
	}

	@Override
	default FileVisitResult visitFileFailed(final Path file, final IOException exc) {
	    return FileVisitResult.CONTINUE;
	}

}

