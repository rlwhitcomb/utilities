/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015,2017,2019-2020 Roger L. Whitcomb.
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
 *	A generic text file processor that can adapt to many different
 *	processing tasks that can deal with one line at a time.
 *
 *  History:
 *	06-Jan-2015 (rlwhitcomb)
 *	    Created.
 *	16-Jun-2017 (rlwhitcomb)
 *	    Code cleanup.
 *	15-Mar-2019 (rlwhitcomb)
 *	    Don't use FileInputStream/FileOutputStream due to GC problems b/c of the finalize
 *	    method in these classes.
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 */
package info.rlwhitcomb.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * A generic text file processor that can adapt to many different processing
 * tasks that deal with files one line at a time.
 */
public class FileProcessor
{
	private File inputFile;
	private LineProcessor lp;

	/**
	 * Construct a new processor for the given file name with the given
	 * processor for each line of the file.
	 *
	 * @param fileName	Name of the file to process.
	 * @param lp		The processor for each line of the file.
	 * @throws IllegalArgumentException if the name is {@code null} or empty or
	 * the file cannot be accessed.
	 * @see #init
	 */
	public FileProcessor(String fileName, LineProcessor lp) {
	    if (CharUtil.isNullOrEmpty(fileName)) {
		throw new IllegalArgumentException(Intl.getString("util#file.nullEmptyInput"));
	    }

	    init(new File(fileName), lp);
	}

	/**
	 * Construct a new processor for the given file with the given
	 * processor for each line of the file.
	 *
	 * @param file		The file to process.
	 * @param lp		The processor for each line of the file.
	 * @throws IllegalArgumentException if the file is {@code null} or
	 * cannot be accessed.
	 * @see #init
	 */
	public FileProcessor(File file, LineProcessor lp) {
	    init(file, lp);
	}

	/**
	 * Initalize processing for the given file and line processor.
	 * <p> Does null input and file access checks.
	 *
	 * @param file		The file to begin processing.
	 * @param lp		The processor for each line of the file.
	 * @throws IllegalArgumentException if inputs are {@code null} or the file
	 * cannot be accessed.
	 */
	private void init(File file, LineProcessor lp) {
	    if (file == null) {
		throw new IllegalArgumentException(Intl.getString("util#file.nullInputFile"));
	    }
	    if (lp == null) {
		throw new IllegalArgumentException(Intl.getString("util#file.nullProcessor"));
	    }
	    if (!file.exists() || !file.canRead() || !file.isFile()) {
		throw new IllegalArgumentException(Intl.formatString("util#file.notExistNotFile", file.getPath()));
	    }

	    this.inputFile = file;
	    this.lp = lp;
	}

	/**
	 * Process the file using the {@link LineProcessor} given in the constructor.
	 *
	 * @return	{@code true} if processing should continue (that is, no errors
	 *		considered fatal were encountered), or {@code false} to abort
	 *		processing.  This would be the return value from any of the
	 *		<tt>LineProcessor</tt> methods.
	 */
	public boolean processFile() {
	    BufferedReader reader = null;
	    try {
		if (!lp.preProcess(inputFile)) {
		    return false;
		}

		Charset charset = lp.getCharset();
		if (charset == null) {
		    // Don't treat this as an error, just use the default
		    charset = Charset.defaultCharset();
		}

		reader = Files.newBufferedReader(inputFile.toPath(), charset);

		String inputLine;
		while ((inputLine = reader.readLine()) != null) {
		    if (!lp.processLine(inputLine)) {
			return false;
		    }
		}
	    }
	    catch (Throwable ex) {
		if (!lp.handleError(inputFile, ex)) {
		    return false;
		}
	    }
	    finally {
		if (reader != null) {
		    try {
			reader.close();
		    }
		    catch (IOException ioe) {
		    }
		}
		reader = null;

		try {
		    if (!lp.postProcess(inputFile)) {
			return false;
		    }
		}
		catch (Throwable ex2) {
		    if (!lp.handleError(inputFile, ex2)) {
			return false;
		    }
		}
	    }
	    return true;
	}

}
