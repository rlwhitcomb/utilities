/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2023 Roger L. Whitcomb.
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
 *      An interface to assist with unit testing.
 *
 * History:
 *  22-Jan-21 rlw ----	Initial coding.
 *  15-Mar-21 rlw ----	Add another return code.
 *  26-Sep-22 rlw #489	More return codes.
 *  08-Oct-22 rlw #481	Add NO_INPUTS return code.
 *  28-Nov-23 rlw #627	New static methods for the "in testing" flag.
 */
package info.rlwhitcomb;

import static info.rlwhitcomb.util.Constants.*;


/**
 * An interface to facilitate unit testing. Includes a small set of defined
 * exit codes for common success and failure cases.
 */
public interface Testable
{
	/** Success value. */
	public static final int SUCCESS         = 0;

	/** A bad argument value. */
	public static final int BAD_ARGUMENT    = 1;

	/** A missing option. */
	public static final int MISSING_OPTION  = 2;

	/** File not found. */
	public static final int FILE_NOT_FOUND  = 3;

	/** I/O error on input file. */
	public static final int INPUT_IO_ERROR  = 4;

	/** I/O error on output file. */
	public static final int OUTPUT_IO_ERROR = 5;

	/** File compare error. */
	public static final int MISMATCH        = 6;

	/** Test class not found. */
	public static final int CLASS_NOT_FOUND = 7;

	/** Directory argument is invalid. */
	public static final int BAD_DIRECTORY   = 8;

	/** Locale argument is invalid. */
	public static final int BAD_LOCALE      = 9;

	/** Charset argument is invalid. */
	public static final int BAD_CHARSET     = 10;

	/** Nothing to do, no inputs. */
	public static final int NO_INPUTS	= 11;


	/** Action completed -- return from setup for "help" (for instance). */
	public static final int ACTION_DONE     = 98;

	/** Other unspecified error. */
	public static final int OTHER_ERROR     = 99;

	/** Base for non-zero status codes that need a "number of failures" code. */
	public static final int NUMBER_OF_ERRORS = 100;


	/** Name of system property set by {@code Tester} while testing is in progress. */
	public static final String TESTING_IN_PROGRESS_PROPERTY = Testable.class.getPackageName() + ".testing_in_progress";


	/**
	 * Setup one test with the given arguments.
	 *
	 * <p> Note: since multiple tests can be run from one class, any
	 * static initialization (or static variable initialization) should
	 * be idempotent over multiple tests, or redone by this method.
	 *
	 * @param args	Command line arguments, just as if the program
	 *		were invoked from the command line via {@code main(String[])}.
	 * @return	If setup was successful and the test can be run, return {@code 0},
	 *		or non-zero if either the setup was unsuccessful, or
	 *		the test should otherwise not be run (because the arguments
	 *		specified an action that has already been accomplished, such
	 *		as "-help"). This value can be used as the process exit code.
	 */
	int setup(String[] args);

	/**
	 * Execute the test just initialized by the {@link #setup} method.
	 *
	 * @return	An integer "exit code" which can be used as the
	 *		success (=0) or failure (!0) for the test(s).
	 */
	int execute();

	/**
	 * Once the {@link #setup} method has been invoked, determine a suitable
	 * name for the test that can be reported to the user.
	 *
	 * @return	A suitable name for the current test.
	 */
	String getTestName();

	/**
	 * Get the current version (major.minor[.revision][_build} format) of this testable class.
	 *
	 * @return	The current version for the test.
	 */
	String getVersion();

	/**
	 * Process an exit code returned by either the {@link #setup} or {@link #execute} methods.
	 *
	 * @param code	The integer return code value (0 = success, non-zero = failure)
	 */
	public static void exit(int code) {
	    if (code == SUCCESS)
		return;

	    if (code < 0)
		code = -code;

	    if (code > 255)
		code = 255;

	    System.exit(code);
	}

	/**
	 * Set the system property to signify that we are in the process of testing,
	 * just in case the class being tested has to do something special in that case.
	 */
	public static void setInTesting() {
	    System.setProperty(TESTING_IN_PROGRESS_PROPERTY, B_TRUE);
	}

	/**
	 * Clear the system property to signify that testing of a class has finished.
	 */
	public static void clearInTesting() {
	    System.clearProperty(TESTING_IN_PROGRESS_PROPERTY);
	}

	/**
	 * Check to see if the special {@link #TESTING_IN_PROGRESS_PROPERTY} system property has
	 * been set, meaning that we are in the process of testing.
	 *
	 * @return {@code true} if we are in the middle of testing, {@code false} if not.
	 */
	public static boolean inTesting() {
	    return Boolean.valueOf(System.getProperty(TESTING_IN_PROGRESS_PROPERTY, B_FALSE));
	}

}

