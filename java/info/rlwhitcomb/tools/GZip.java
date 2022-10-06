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
 *	09-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 *	05-Oct-2022 (rlwhitcomb)
 *	    #498: Options to delete or not the original file, and change the
 *	    output name.
 */
package info.rlwhitcomb.tools;

import info.rlwhitcomb.util.Exceptions;
import info.rlwhitcomb.util.FileUtilities;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.Options;

import java.io.File;
import java.io.IOException;
import java.util.Optional;


/**
 * Compress a list of input files to the same name with ".gz" extension.
 */
public class GZip
{
	/**
	 * Enum of other command line options to set.
	 */
	private static enum Need
	{
		INPUT_NAME,
		OUTPUT_NAME
	}


	/**
	 * What we need to see next on the command line.
	 */
	private static Need need = Need.INPUT_NAME;

	/**
	 * Possible non-standard output name.
	 */
	private static String outputName = null;

	/**
	 * Whether to delete the input file once it has been compressed.
	 */
	private static boolean delete = true;


	/**
	 * Process one option.
	 *
	 * @param option The option string.
	 */
	private static void processOption(final String option) {
	    switch (option.toLowerCase()) {
		case "delete":
		case "del":
		case "d":
		    delete = true;
		    break;
		case "keep":
		case "k":
		    delete = false;
		    break;
		case "output":
		case "out":
		case "o":
		    need = Need.OUTPUT_NAME;
		    break;
		default:
		    Intl.errFormat("tools#gunzip.badOption", option);
		    System.exit(1);
	    }
	}

	/**
	 * Input is a list of input file names.  The program will produce
	 * the corresponding compressed output files with the ".gz" extension
	 * added.
	 *
	 * @param args The parsed command line argument array.
	 */
	public static void main(final String[] args) {
	    boolean anyFiles = false;

	    for (String arg : args) {
		Optional<String> option = Options.checkOption(arg);
		if (option.isPresent()) {
		    if (need != Need.INPUT_NAME) {
			Intl.errFormat("tools#gunzip.missingOutputName");
		    }
		    else {
			processOption(option.get());
		    }
		}
		else if (need == Need.OUTPUT_NAME) {
		    outputName = arg;
		    need = Need.INPUT_NAME;
		}
		else {
		    try {
			anyFiles = true;

			File f = new File(arg);
			if (FileUtilities.canRead(f)) {
			    FileUtilities.compressFile(f, outputName, delete);
			}
			else {
			    Intl.errFormat("tools#gunzip.cannotFindOrRead", arg);
			}
		    }
		    catch (IOException ioe) {
			Intl.errFormat("tools#gunzip.ioError", arg, Exceptions.toString(ioe));
		    }
		}
	    }

	    if (need != Need.INPUT_NAME) {
		Intl.errFormat("tools#gunzip.missingOutputName");
	    }
	    if (!anyFiles) {
		Intl.outFormat("tools#gunzip.noFiles");
	    }
	}
}

