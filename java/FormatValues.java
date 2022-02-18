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
 *      Format values into a Java initializer.
 *
 * History:
 *      05-Jan-2021 (rlwhitcomb)
 *          Initial coding.
 *	29-Mar-2021 (rlwhitcomb)
 *	    Move test code to a new package.
 */
import java.io.*;
import info.rlwhitcomb.util.Exceptions;
import info.rlwhitcomb.util.FileUtilities;


/**
 * Used to take a text file with PI or E digits and turn it into
 * a String initializer, such as used in {@link info.rlwhitcomb.test.TestNumericUtil}.
 */
public class FormatValues
{
	private static final int LINE_WIDTH = 80;

	private static final String VAR_PREFIX = "\t";
	private static final String VAL_PREFIX = "\t\t";
	private static final String QUOTE      = "\"";
	private static final String QUOTE_AND  = "\" +";
	private static final String QUOTE_SEMI = "\";";

	/**
	 * @param args Arguments: file_name, variable_name, max_digits
	 */
	public static void main(String[] args) {
	    if (args.length != 3) {
		System.err.println("Usage: java FormatValues <file_name> <variable_name> <max_digits>");
		System.exit(1);
	    }

	    File f          = new File(args[0]);
	    String var_name = args[1];
	    int max_digits  = Integer.parseInt(args[2]);

	    String value = "";
	    try {
		value = FileUtilities.readFileAsString(f);
		System.err.println(f.getPath() + " -> length " + value.length());
		if (value.length() < max_digits) {
		    System.err.println("Input file length (" + value.length() + ") is not enough for given max_digits (" + max_digits + ").");
		    System.exit(1);
		}
		value = value.substring(0, max_digits);
	    }
	    catch (IOException ioe) {
		System.err.println("I/O Error: " + Exceptions.toString(ioe));
		System.exit(1);
	    }

	    System.out.println(VAR_PREFIX + "private static final String " + var_name + " =");
	    // First line is whole number portion
	    int pos = 0;
	    pos = value.indexOf('.') + 1;
	    System.out.println(VAL_PREFIX + QUOTE + value.substring(0, pos) + QUOTE_AND);
	    int endPos = pos;
	    while (pos < value.length()) {
		endPos = Math.min(value.length(), pos + LINE_WIDTH);
		String line = value.substring(pos, endPos);
		if (endPos == value.length())
		    System.out.println(VAL_PREFIX + QUOTE + line + QUOTE_SEMI);
		else
		    System.out.println(VAL_PREFIX + QUOTE + line + QUOTE_AND);
		pos = endPos;
	    }
	    System.out.println();
	}
}

