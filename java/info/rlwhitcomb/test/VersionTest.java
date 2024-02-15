/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022,2024 Roger L. Whitcomb.
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
 *      Test program for the SemanticVersion parsing and display.
 *
 * History:
 *  17-Feb-22 rlw ----	Initial coding.
 *  26-Sep-22 rlw #491	New test of "failing" Java 8 version string.
 *  14-Feb-24 rlw #653	Change version format for our version strings.
 */
package info.rlwhitcomb.test;

import java.text.ParseException;

import de.onyxbits.SemanticVersion;

import info.rlwhitcomb.util.Exceptions;
import info.rlwhitcomb.util.Options;


/**
 * Tests of {@link SemanticVersion} methods.
 */
public class VersionTest
{
	private static int numberOfTests    = 0;
	private static int numberOfFailures = 0;

	private static boolean verbose = false;

	/**
	 * Various weird and wonderful version strings we should (must)
	 * be able to parse sanely.
	 */
	private static final String[][] TEST_STRINGS = {
	    { "11.0.14.1+1",          "11.0.14.1+1"         },
	    { "11+28",                "11.0.0+28"           },
	    { "17.0.2+8",             "17.0.2+8"            },
	    { "1.9.0+8b74297.debug",  "1.9.0+8b74297.debug" },
	    { "1.3.1-beta",           "1.3.1-beta"          },
	    { "1.3.1_01",             "1.3.1_01"            },
	    { "1.3.1_05-ea-b01",      "1.3.1_05-ea-b01"     },
	    { "1.8.0_345-b01",        "1.8.0_345-b01"       }
	};

	private static void runTests() {
	    int stringTest = 0;
	    for (String[] tests : TEST_STRINGS) {
		numberOfTests++;
		stringTest++;
		try {
		    String testName = String.format("String Test #%1$d", stringTest);
		    System.out.println("===== " + testName + " =====");
		    String test = tests[0];
		    String output = tests[1];
		    SemanticVersion v = new SemanticVersion(test);
		    System.out.print("input: '" + test + "' -> '" + v.toString() + "'");
		    if (v.toString().equals(output)) {
			System.out.println(" passed");
		    }
		    else {
			System.out.println(" FAILED");
			numberOfFailures++;
		    }
		    System.out.println("----- " + testName + " -----");
		    System.out.println();
		}
		catch (IllegalArgumentException | ParseException ex) {
		    System.out.println("input: '" + tests[0] + "' FAILED: " + Exceptions.toString(ex));
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
		}
	    }

	    runTests();

	    // TODO: more tests here

	    System.out.println(
		"VersionTest: number of tests: " + numberOfTests +
		", number succeeded: " + (numberOfTests - numberOfFailures) +
		", number failed: " + numberOfFailures);

	    if (numberOfFailures > 0)
		System.exit(1);
	}
}

