/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Roger L. Whitcomb.
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
 *      Test program for methods of FileUtilities.
 *
 *  Change History:
 *      26-Jan-2022 (rlwhitcomb)
 *          Initial coding.
 */
package info.rlwhitcomb.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import info.rlwhitcomb.util.FileUtilities;
import info.rlwhitcomb.util.Options;

/**
 * Tests of {@link FileUtilities} methods.
 */
public class FileUtilitiesTest
{
	private static int numberOfTests    = 0;
	private static int numberOfFailures = 0;
	private static int numberOfLines    = 10;

	private static boolean verbose = false;

	private static final String[] TEST_FILE_NAMES = {
	    "Constitution.txt",
	    "Declaration.txt",
	    "Genesis1.txt",
	    "Gettysburg.txt",
	    "MagnaCarta.txt",
	    "Psalm23.txt"
	};

	private static void runTests() {
	    for (String fileName : TEST_FILE_NAMES) {
		numberOfTests++;
		try {
		    File f = new File("test/data", fileName);
		    List<String> lines = FileUtilities.readFileAsLines(f);
		    System.out.println("===== " + f.getName() + "=====");
		    System.out.println("----- " + lines.size() + " lines -----");
		    for (int i = 0; i < Math.min(lines.size(), numberOfLines); i++) {
			System.out.println(lines.get(i));
		    }
		}
		catch (IOException ioe) {
		    numberOfFailures++;
		}
	    }
	}

	public static void main(String[] args) {
	    for (String arg : args) {
		if (Options.matchesOption(arg, true, "verbose", "v")) {
		    verbose = true;
		} else if (Options.matchesOption(arg, true, "quiet", "q")) {
		    verbose = false;
		} else {
		    numberOfLines = Integer.parseInt(arg);
		}
	    }

	    runTests();

	    // TODO: more tests here

	    System.out.println(
		"FileUtilitiesTest: number of tests: " + numberOfTests +
		", number succeeded: " + (numberOfTests - numberOfFailures) +
		", number failed: " + numberOfFailures);

	    if (numberOfFailures > 0)
		System.exit(1);
	}
}

