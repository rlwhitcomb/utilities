/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2022 Roger L. Whitcomb.
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
 *   Simple command-line utility to check if the (one) argument is a directory or not.
 *
 *  Change History:
 *	27-Dec-2021 (rlwhitcomb)
 *	    Coding.
 *	08-Feb-2022 (rlwhitcomb)
 *	    Move text out to resources.
 *	09-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 */
package info.rlwhitcomb.tools;

import info.rlwhitcomb.util.Intl;

import java.io.File;


/**
 * Test if the given (one) argument is a directory or not.
 * <p>Process exit code is:
 * <ul>
 * <li>0 (Success) if the path IS a directory</li>
 * <li>1 (Error) if the path is something else (regular file, pipe, etc.)</li>
 * <li>2 (also Error) if there is no argument given</li>
 * <li>or 3 (Error) if the path is non-existent as it was spelled (typo?)</li>
 * </ul>
 */
public class IsDir
{
	private static final int NOT_DIRECTORY  = 1;
	private static final int ARG_ERROR      = 2;
	private static final int PATH_NOT_EXIST = 3;


	private static final void usage() {
	    Intl.printHelp("tools#isdir");
	}

	/**
	 * Input is a single file path.  The program will set the process exit code (test with {@code ERRORLEVEL}
	 * on Windows or {@code $?} on other O/Ses).
	 *
	 * @param args The parsed command line argument array, which should have one path values.
	 */
	public static void main(String[] args) {
	    if (args.length == 0) {
		Intl.errPrintln("tools#isdir.pathRequired");
		usage();
		System.exit(ARG_ERROR);
	    }
	    else if (args.length > 1) {
		Intl.errPrintln("tools#isdir.tooManyArgs");
		usage();
		System.exit(ARG_ERROR);
	    }

	    String arg = args[0];

	    File f = new File(arg);
	    if (f.exists()) {
		if (f.isDirectory()) {
		    return;
		}
		else {
		    System.exit(NOT_DIRECTORY);
		}
	    }
	    else {
		Intl.errFormat("tools#isdir.cannotFindPath", arg);
		System.exit(PATH_NOT_EXIST);
	    }
	}
}

