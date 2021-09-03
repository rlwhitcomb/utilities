/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2018,2020-2021 Roger L. Whitcomb
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
 *	Tests of the Options class.
 *
 *  History:
 *	02-Sep-2021 (rlwhitcomb)
 *	    Moved testing from Options into here.
 */
package info.rlwhitcomb.test;

import java.util.Set;

import info.rlwhitcomb.util.Options;


/**
 * Unit tests of the {@link Options} class.
 */
public class OptionsTest
{
	/**
	 * A main program that does some basic testing of the {@link Options} class.
	 *
	 * @param args	The command line arguments.
	 */
	public static void main(String[] args) {
	    // Run some basic tests of the "getMixedCaseOptions" function, which is by far the most
	    // complicated thing in here.

	    final String[] testInputs = {
		"MixedCase",
		"SuperVerbose",
		"mixedcase",
		"mixed-case",
		"Utf-16"
	    };

	    final String[][] sameCaseResults = {
		{ "mixed_case", "mixed-case", "mixedcase", "Mixed-Case", "MixedCase", "Mixed_Case" },
		{ "super-verbose", "superverbose", "super_verbose", "Super-Verbose", "SuperVerbose", "Super_Verbose" },
		{ "mixedcase" },
		{ "mixed-case" },
		{ "utf-16", "Utf_16", "utf_16", "Utf-16", "Utf16", "utf16" }
	    };

	    final String[][] mixedCaseResults = {
		{ "mixed_case", "mixed-case", "mixedcase" },
		{ "super-verbose", "superverbose", "super_verbose" },
		{ "mixedcase" },
		{ "mixed-case" },
		{ "utf-16", "utf_16", "utf16" }
	    };

	    // TODO: the first "test" will be just to output the results so we can do regression testing
	    // later after we're sure these results are correct
	    int totalNumberOfTests = 0;
	    int numberOfFailures   = 0;

	    for (int i = 0; i < testInputs.length; i++) {
		String test = testInputs[i];
		Set<String> sameCaseOptions = Options.getMixedCaseOptions(test, false);
		Set<String> mixedCaseOptions = Options.getMixedCaseOptions(test, true);

		totalNumberOfTests += 2;

		System.out.println("input '" + test + "' -> " + sameCaseOptions);
		System.out.println("input '" + test + "' -> " + mixedCaseOptions);

		boolean sizeFailure = false;
		if (sameCaseOptions.size() != sameCaseResults[i].length) {
		    System.out.println("different same case results for input '" + test + "'");
		    numberOfFailures++;
		    sizeFailure = true;
		}
		if (mixedCaseOptions.size() != mixedCaseResults[i].length) {
		    System.out.println("different mixed case results for input '" + test + "'");
		    numberOfFailures++;
		    sizeFailure = true;
		}

		// Don't try comparing individual values if the size check fails
		if (sizeFailure)
		    continue;

		boolean valueFailure = false;
		int j = 0;
		for (String opt : sameCaseOptions) {
		    if (!opt.equals(sameCaseResults[i][j])) {
			System.out.println("Error: different result: expected '" + sameCaseResults[i][j] + "', actual '" + opt + "'");
			valueFailure = true;
		    }
		    j++;
		}
		if (valueFailure)
		    numberOfFailures++;

		valueFailure = false;
		j = 0;
		for (String opt : mixedCaseOptions) {
		    if (!opt.equals(mixedCaseResults[i][j])) {
			System.out.println("Error: different result: expected '" + mixedCaseResults[i][j] + "', actual '" + opt + "'");
			valueFailure = true;
		    }
		    j++;
		}
		if (valueFailure)
		    numberOfFailures++;

	    }

	    System.out.println(String.format("Total number of tests = %1$d, number passed = %2$d, failed = %3$d",
		totalNumberOfTests, (totalNumberOfTests - numberOfFailures), numberOfFailures));
	}

}
