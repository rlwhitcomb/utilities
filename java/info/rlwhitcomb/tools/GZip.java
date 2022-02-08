/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017,2020-2022 Roger L. Whitcomb.
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
 *   Test/utility program for the FileUtilities.compressFile method.
 *
 *  Change History:
 *	20-Feb-2016 (rlwhitcomb)
 *	    Initial implementation.  Not yet suitable for nightly build tests.
 *	30-Jun-2016 (rlwhitcomb)
 *	    Address a Java 8 Javadoc warning.
 *	27-Mar-2017 (rlwhitcomb)
 *	    Fix the (apparently new) incompatibility with the parameters to
 *	    "compressFile()".
 *	13-Nov-2020 (rlwhitcomb)
 *	    Prepare for GitHub; add Javadoc.
 *	29-Mar-2021 (rlwhitcomb)
 *	    Move to new package.
 *	08-Feb-2022 (rlwhitcomb)
 *	    Move text out to resources.
 */
package info.rlwhitcomb.tools;

import java.io.File;
import java.io.IOException;

import info.rlwhitcomb.util.ExceptionUtil;
import info.rlwhitcomb.util.FileUtilities;
import info.rlwhitcomb.util.Intl;


/**
 * Compress a list of input files to the same name with ".gz" extension.
 */
public class GZip
{
	/**
	 * Input is a list of input file names.  The program will produce
	 * the corresponding compressed output files with the ".gz" extension
	 * added.
	 *
	 * @param args The parsed command line argument array.
	 */
	public static void main(String[] args) {
	    for (String arg : args) {
		try {
		    File f = new File(arg);
		    if (f.exists() && f.isFile() && f.canRead()) {
			FileUtilities.compressFile(f);
		    }
		    else {
			Intl.errFormat("tools#gunzip.cannotFindOrRead", arg);
		    }
		}
		catch (IOException ioe) {
		    Intl.errFormat("tools#gunzip.ioError", arg, ExceptionUtil.toString(ioe));
		}
	    }
	}
}

