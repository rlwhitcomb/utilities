/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2021 Roger L. Whitcomb.
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
 *      Test program for the "Matches" program, which has some tricky logic
 *	that needs thorough testing.
 *
 *  Change History:
 *      18-Sep-2020 (rlwhitcomb)
 *          Initial checkin.
 *	13-Nov-2020 (rlwhitcomb)
 *	    Set the process exit code for use with automated testing.
 *	29-Mar-2021 (rlwhitcomb)
 *	    Move to new package.
 *	19-Aug-2021 (rlwhitcomb)
 *	    Add more test cases.
 */
package info.rlwhitcomb.test;

import info.rlwhitcomb.matches.Matches;
import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.Options;

public class MatchesTest
{
	private static int numberOfTests    = 0;
	private static int numberOfFailures = 0;

	private static boolean verbose = false;

	private static final String PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,20}$";
	private static final String HTML_TAG = "<(\\w+).*>(.*?)<\\/\\1>";
	private static final String HTML_TAG_2 = "<(\\w+).*(\\/)?>";

	private static final String[][] TRUE_TEST_CASES = {
	    { "-or", "aaabbb", "a.*b", ".*ab.*" },
	    { "-not", "-or", "build.xml", "apex[/\\\\]web[/\\\\]build", ".*\\.backup" },
	    { "-and", "apex/help/HTML/abc.htm", ".*HTML.*", ".*\\.htm[l]?" },
	    { "IsThisAG@odPassw0rd", PASSWORD },
	    { "<a href='abc.com'>abc.com</a>", HTML_TAG },
	    { "-or", "<br>", HTML_TAG, HTML_TAG_2 }
	};

	private static final String[][] FALSE_TEST_CASES = {
	    { "123455789", PASSWORD },
	    { "Tom", PASSWORD },
	    { "<br>", HTML_TAG },
	    { "<h1>Header</h2>", HTML_TAG }
	};


	private static void runTests(String[][] testCases, boolean expectedResult) {
	    for (int i = 0; i < testCases.length; i++) {
		numberOfTests++;

		String[] testCase = testCases[i];
		boolean match = Matches.match(testCase);
		if (match != expectedResult) {
		    System.err.println("Failure: test case \"" + CharUtil.makeSimpleStringList(testCase) + "\"");
		    numberOfFailures++;
		}
		else if (verbose) {
		    System.out.println("Test case \"" + CharUtil.makeSimpleStringList(testCase) + "\" -> " + match);
		}
	    }
	}

	public static void main(String[] args) {
	    for (String arg : args) {
		if (Options.matchesOption(arg, true, "verbose", "v")) {
		    verbose = true;
		} else if (Options.matchesOption(arg, true, "quiet", "q")) {
		    verbose = false;
		}
	    }

	    runTests(TRUE_TEST_CASES, true);
	    runTests(FALSE_TEST_CASES, false);

	    // TODO: more tests here

	    System.out.println(
		"MatchesTest: number of tests: " + numberOfTests +
		", number succeeded: " + (numberOfTests - numberOfFailures) +
		", number failed: " + numberOfFailures);

	    if (numberOfFailures > 0)
		System.exit(1);
	}
}

