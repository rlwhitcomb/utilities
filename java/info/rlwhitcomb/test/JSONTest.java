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
 *      Test program for methods of JSON library.
 *
 *  Change History:
 *      17-Feb-2022 (rlwhitcomb)
 *          #196: Initial coding.
 */
package info.rlwhitcomb.test;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;

import info.rlwhitcomb.json.JSON;
import info.rlwhitcomb.json.JSONException;
import info.rlwhitcomb.util.Options;


/**
 * Tests of {@link JSON} methods.
 */
public class JSONTest
{
	private static int numberOfTests    = 0;
	private static int numberOfFailures = 0;

	private static boolean verbose = false;

	private static final String[] TEST_FILE_NAMES = {
	    "defunkt.json"
	};

	private static final String[] TEST_STRINGS = {
	    "{ a:1, b : 2, c : [ 'abc', 'def' ] }",
	    "true",
	    "[ 2.5, 3.4, 6.2, 1.7 ]",
	    "[ [ 1, 2, 3], [4, 5, 6], [7,8,9]]"
	};

	private static void runTests() {
	    for (String fileName : TEST_FILE_NAMES) {
		numberOfTests++;
		try {
		    File f = new File("test/data", fileName);
		    InputStream is = Files.newInputStream(f.toPath());
		    System.out.println("===== " + f.getName() + " =====");
		    Object obj = JSON.readObject(is);
		    System.out.println(JSON.toStringValue(obj, true));
		    System.out.println("----- " + f.getName() + " -----");
		    System.out.println();
		}
		catch (IOException | JSONException ex) {
		    numberOfFailures++;
		}
	    }

	    int stringTest = 0;
	    for (String test : TEST_STRINGS) {
		numberOfTests++;
		stringTest++;
		try {
		    String testName = String.format("String Test #%1$d", stringTest);
		    System.out.println("===== " + testName + " =====");
		    Object obj = JSON.readString(test);
		    System.out.println(JSON.toStringValue(obj, true));
		    System.out.println("----- " + testName + " -----");
		    System.out.println();
		}
		catch (JSONException ex) {
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
		"JSONTest: number of tests: " + numberOfTests +
		", number succeeded: " + (numberOfTests - numberOfFailures) +
		", number failed: " + numberOfFailures);

	    if (numberOfFailures > 0)
		System.exit(1);
	}
}

