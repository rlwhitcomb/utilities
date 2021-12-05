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
 *	Test program for the DynamicArray class.
 *
 *  Change History:
 *	13-Nov-2020 (rlwhitcomb)
 *	    Initial coding.
 *	13-Nov-2020 (rlwhitcomb)
 *	    Add checks for nulls around the real value.
 *	01-Feb-2021 (rlwhitcomb)
 *	    "size()" changed value in DynamicArray, so report that, but also
 *	    new "capacity()" value.
 *	29-Mar-2021 (rlwhitcomb)
 *	    Move to new package.
 *	03-Dec-2021 (rlwhitcomb)
 *	    #123: Test new constructor.
 */
package info.rlwhitcomb.test;

import info.rlwhitcomb.util.DynamicArray;

/**
 * Some simple tests of the {@link DynamicArray} class.
 */
public class DynArrayTest
{
	private static final String UNDERLINE = "----------------------------------------";
	private static int numberOfTests = 0;
	private static int numberOfFailures = 0;

	private static <T> void reportOne(final DynamicArray<T> array, final int index) {
	    T value = array.get(index);
	    String valueString;
	    String valueFormat = "%2$-8s";

	    if (value == null) {
		valueString = "__null__";
	    }
	    else if (value instanceof String) {
		valueString = String.format("\"%1$s\"", value);
	    }
	    else {
		valueString = value.toString();
		valueFormat = "%2$8s";
	    }
 
	    System.out.println(
		String.format("Value at %1$5d = " + valueFormat + ", size = %3$5d, capacity = %4$5d",
			index, valueString, array.size(), array.capacity()));
	}

	private static <T> void report(final DynamicArray<T> array, final int index) {
	    System.out.println(UNDERLINE);
	    if (index > 0)
		reportOne(array, index - 1);
	    reportOne(array, index);
	    reportOne(array, index + 1);
	}

	private static <T> void checkOne(final DynamicArray<T> array, final int index, final T expectedValue) {
	    numberOfTests++;

	    T getValue = array.get(index);

	    if (getValue != expectedValue) {
		System.out.println("ERROR: value at location " + index + " = " + getValue + " not equal " + expectedValue + "!");
		numberOfFailures++;
	    }
	}

	private static <T> void check(final DynamicArray<T> array, final int index, final T expectedValue) {
	    if (index > 0)
		checkOne(array, index - 1, null);
	    checkOne(array, index, expectedValue);
	    checkOne(array, index + 1, null);
	}

	private static <T> void oneTest(final DynamicArray<T> array, final int index, final T value) {
	    array.put(index, value);
	    report(array, index);
	    check(array, index, value);
	}

	private static void integerTest() {
	    /* Setup a dynamic array of initial size 10 */
	    DynamicArray<Integer> iArray = new DynamicArray<Integer>(Integer.class, 10);

	    oneTest(iArray, 0, 1);
	    oneTest(iArray, 1000, 3);
	    oneTest(iArray, 5000, 6);
	    oneTest(iArray, 10000, 7);
	    oneTest(iArray, 20000, -789);
	}

	private static void stringTest() {
	    /* Setup a dynamic array of default initial size */
	    DynamicArray<String> sArray = new DynamicArray<String>(String.class);

	    oneTest(sArray, 1, "abcd");
	    oneTest(sArray, 900, "efghi");
	    oneTest(sArray, 4500, "jklm");
	    oneTest(sArray, 9000, "nopqr");
	    oneTest(sArray, 11500, "stuv");
	}

	private static void constructorTest() {
	    DynamicArray<String> sArray = new DynamicArray<>(String.class, "This", "is", "the", "day");

	    checkOne(sArray, 0, "This");
	    checkOne(sArray, 1, "is");
	    checkOne(sArray, 2, "the");
	    checkOne(sArray, 3, "day");
	    checkOne(sArray, 4, null);
	}

	private static void runTest(final Runnable test) {
	    test.run();

	    System.out.println(UNDERLINE);
	    System.out.println();
	}

	public static void main(String[] args) {
	    runTest(DynArrayTest::integerTest);
	    runTest(DynArrayTest::stringTest);
	    runTest(DynArrayTest::constructorTest);

	    System.out.println("DynamicArray Tests: " + numberOfTests +
			       ", succeeded: " + (numberOfTests - numberOfFailures) +
			       ", failed: " + numberOfFailures);

	    if (numberOfFailures > 0)
		System.exit(1);
	}
}

