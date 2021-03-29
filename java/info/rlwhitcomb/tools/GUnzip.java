/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017,2020-2021 Roger L. Whitcomb.
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
 *   Test/utility program for the FileUtilities.uncompressFile method.
 *
 *  Change History:
 *	27-Mar-2017 (rlwhitcomb)
 *	    Clone from GZip.java.
 *	13-Nov-2020 (rlwhitcomb)
 *	    Prepare for GitHub; add Javadoc.
 *	29-Mar-2021 (rlwhitcomb)
 *	    Move to new package.
 */
package info.rlwhitcomb.tools;

import java.io.File;
import java.io.IOException;

import info.rlwhitcomb.util.ExceptionUtil;
import info.rlwhitcomb.util.FileUtilities;

/**
 * Unzip a list of files with the ".gz" extension, producing the uncompressed
 * version with the extension removed.
 */
public class GUnzip
{
	/**
	 * Input is a list of input file names.  The program will produce
	 * the corresponding uncompressed output files without ".gz" extension
	 * present.
	 *
	 * @param args The parsed command line argument array.
	 */
	public static void main(String[] args) {
	    for (String arg : args) {
		try {
		    File f = new File(arg);
		    if (f.exists() && f.isFile() && f.canRead()) {
			FileUtilities.uncompressFile(f);
		    }
		    else {
			System.err.format("Cannot find or read the input file: \"%1$s\"!%n", arg);
		    }
		}
		catch (IllegalArgumentException iae) {
		    System.err.format("The input file (%1$s) doesn't have the (required) \".gz\" extension.", arg);
		}
		catch (IOException ioe) {
		    System.err.format("Error reading the input or writing the output file (%1$s): %2$s%n",
			arg, ExceptionUtil.toString(ioe));
		}
	    }
	}
}

